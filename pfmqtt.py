#! /usr/bin/python
'''
apt-get install python-mosquitto
this assumes you have your piface installed and the python library installed too
'''

import mosquitto
import piface.pfio as pfio
import re


class pfmqtt:
    def __init__(self, ident, topicheader, uri = 'localhost', port = 1883):
        self.uri = uri
        self.port = port
        self.ident = ident
        
        self.topicheader = topicheader+ '/' + self.ident
        self.outputRegex = re.compile(self.topicheader + "/output/pin/(\d*)", re.IGNORECASE)
        
        
        self.mos = mosquitto.Mosquitto(self.ident)
        self.mos.on_message = self.on_message # register for callback
        self.connect()
        
        
        pfio.init()
        self.inputpins = pfio.read_input()
        
    def connect(self):
        conn = self.mos.connect(self.uri, port=self.port)
        if conn != 0:
            print 'Connection Failed with error: ' + str(conn)
            raise "CONNECTION ERROR"
        
        self.mos.subscribe(self.topicheader + "/output/pin/+", 0) # get all messages for me

    def run(self):
        while self.mos.loop(0) == 0:
            inputpins = pfio.read_input() # get input pin state
            if self.inputpins != inputpins: # check to see if they are different
                change = self.inputpins ^ inputpins #get the differences
                
                for i in range(8): 
                    pinid = 1<<i
                    if change & pinid == pinid:
                            self.mos.publish(self.topicheader + '/input/pin' + str(i), str((inputpins&pinid)>>i", 0 )
                self.inputpins = inputpins

    def on_message(self,  obj, msg):
        
        rm = self.outputRegex.match( msg.topic )
        
        if rm != None:
            pin = int(rm.group(1))
            #print msg.topic + " - " + msg.payload
            
            if msg.payload == '0':
                pfio.digital_write(pin, 0)
                #print "setting pin %d to %d"%(int(rm.group(1)), 1)
            else:
                pfio.digital_write(pin, 1)
                #print "setting pin %d to %d"%(int(rm.group(1)), 0)
                
    
    def __del__(self):
        self.mos.disconnect()
        pfio.deinit()
if __name__ == '__main__':
    conn = pfmqtt('pi', 'minecraft', 'localhost', 1883)
    conn.run()


    
