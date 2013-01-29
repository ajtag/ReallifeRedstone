/*
 	Copyright 2013 Angus Taggart
	
	This file is part of mcmqtt.

    mcmqtt is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    mcmqtt is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser Public License for more details.

    You should have received a copy of the GNU Lesser Public License
    along with mcmqtt.  If not, see <http://www.gnu.org/licenses/>.
*/
package uk.co.electrictea.mc_mqtt;

import java.util.logging.Logger;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.MqttTopic;

import uk.co.electrictea.mc_mqtt.McIO;

public class Courier extends MqttClient implements MqttCallback{
	private String id;
	private String topic;
	private McIO mcIO; 
	
	public Courier(String URI, String id, String topic, McIO mcIO) throws MqttException{
		super("tcp://"+ URI + ":1883", id);
		Logger.getLogger("info").info("Using MQTT server: '" + "tcp://"+ URI + ":1883" + "' with ClientID '" + this.id + "'.");
		
		this.mcIO  = mcIO;
		this.topic = topic;
		this.id = id;
	}
	
	@Override
	public void connect() throws MqttSecurityException, MqttException{
		super.connect();
		//subscribe to all input messages related to our config
		setCallback(this);
		subm(topic+"/input/#"); 
	}

	 public void connectionLost(java.lang.Throwable arg0){
		  Logger.getLogger("INFO").info("Connection to MQTT Lost");
		  try {
			connect();
		  } catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		  } catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	 
	  

	  public void messageArrived(MqttTopic arg0, MqttMessage arg1) throws java.lang.Exception {
		  if (arg0.toString().startsWith(topic+ "/input/")){
			  //Logger.getLogger("INFO").info(topic +" - " + new String(arg1.getPayload()));
			  mcIO.updateLevers(arg0.getName().replace(topic+ "/input/", ""), new String(arg1.getPayload()));
		  }
		  
	  }
	  
	  public void deliveryComplete(MqttDeliveryToken arg0){
		  
	  }
	
	
	
	public void pubm(byte[] payload, String topicName, int qos ){
		MqttTopic topic = null;
		try{
			topic = this.getTopic(this.topic+ "/" + topicName);
		}catch(Exception  e){
			e.printStackTrace();
		}
		MqttMessage message = new MqttMessage(payload);
		message.setQos(qos);
			
		try {
			Logger.getLogger("INFO").info(new String(payload));
			//MqttDeliveryToken token = 
			topic.publish(message);
		} catch (MqttPersistenceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	public void subm(String topicname){
    	try {
			this.subscribe(this.topic+ "/input/"+topicname);
		} catch (MqttSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
