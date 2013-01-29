ReallifeRedstone
0.0.1
======================

Bukkit Plugin to breakout redstone signals to a mqtt server.
this code is still very much in alpha state. 

Mosquitto( http://mosquitto.org/ ) is a nice example of an mqtt server that's multiplatform
if you are on a pi,"sudo apt-get install mosquitto" will install a server, but be aware, you 
are now running a MQTT server, and you might not want this open to all.
  
The code for this plugin is available at: https://github.com/ajtag/ReallifeRedstone 
For more information, try: http://electrictea.co.uk/pimc

Paho is a MQTT library from the Eclipse foundation, distributed under the EPL licence,
more information is available at their website: http://www.eclipse.org/paho/



INSTALLING
==========
unzip folder, and copy contents of MCMQTT folder into craftbukkit plugins folder.
run craftbukkit server


USAGE
==========
mqttconnect [server]
	To connect to mqttserver and start sending updates 

mqttconnect 
	to save you entering the details for your server on every connection, 
	edit McMqtt/config.yml file to have your mqtt server's address there
	currently defaulting to loacalhost.
 
mqttdisconnect  
	disconnect from server


to create an IO point, place a sign, the first line defines the direction of data.
The second line is the signid (you are best using alpha or numbers only for the moment). 

	"[input]" creates a input point (don't type in the " marks)
	on placing this an input sign it turns into a lever, that players cannot modify, only destroy.
	the lever can be modified by sending a message to the topic that the sign listens on.
	
	to update it's state, send the string "1" or "0" to  minecraft/input/*signid* 
	Edit *signid* as appropriate.
	
	
	"[output]" creates a sign that sends a message whenever there is a change of redstone signal on the sign.
	it will send "0" for no signal or "15" if there is a signal.
	
	the signal will be sent to minecraft/output/*signid*
	  

Pi_scripts
==========
	Contains code designed to run on a raspberry Pi to push mqtt messages out
	onto the PiFace board.
	this code assumees you have the python-mosquitto and piface(pfio) librarys available.
	copy this onto your Pi and run with your python interpreter.

Docs
==========
	Contains EPL.txt, the licence that covers the Paho library.
	and LGPL.txt, the licence text to cover this code.
	
Paho is a MQTT library from the eclipse foundation, distributed under the EPL licence,
more information is available at their website: http://www.eclipse.org/paho/


Angus Taggart, 2013
	