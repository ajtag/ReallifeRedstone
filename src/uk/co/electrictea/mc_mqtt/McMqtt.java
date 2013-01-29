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



import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
//import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;


interface BlockStore{
	void addBlock(Block ioBlock, String[] signText);
	void removeBlock(Block ioBlock);
	boolean isIoBlock(Block ioBlock);
	ArrayList<InputLever> getInputBlockLocations(String topic);
	int length()
	;
}

public final class McMqtt extends JavaPlugin {
	enum BlockStorageTypes{Memory};
	BlockStore ioBlockStore;
	
	private Courier cl = null;
	private McIO mc = null;
	
		
    @Override
    public void onEnable(){
		saveDefaultConfig();
		switch(BlockStorageTypes.valueOf(getConfig().getString("BlockStore.Type"))){
    	default:
    		getLogger().info("StoreType not recognised, using: Memory");
    	case Memory:
    		ioBlockStore = new MemoryStore();
    	}		
    	mc = new McIO(this, ioBlockStore);
    	
    	getServer().getPluginManager().registerEvents(this.mc, this);
    	getLogger().info("mcmqtt Enabled!");
    }
    
    @Override
    public void onDisable() {
    	getLogger().info("mcmqtt Disabled!");
    	try {
			cl.disconnect();
		} catch (MqttException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    @Override    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    	if (cmd.getName().equalsIgnoreCase("mqttconnect")){ 
    		
    		
    		try {
	    		if (args.length == 0){
	    			cl = new Courier(getConfig().getString("Mqtt.Server"), getConfig().getString("Mqtt.ClientID"), getConfig().getString("Mqtt.Topic"), mc);	
	    		}else if (args.length == 1){
	    			cl = new Courier(args[0].toString()					 , getConfig().getString("Mqtt.ClientID"), getConfig().getString("Mqtt.Topic"), mc);	
	    		}
	    		cl.connect();
	  
    		} catch (MqttSecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return true;
    	} if (cmd.getName().equalsIgnoreCase("mqttdisconnect")){
    		try {
				this.cl.disconnect();
			} catch (MqttException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		return true;
    	}
    		
    	return false;
    }
    
    public void publish(byte[] payload, String topicName){
    	if (cl != null){
    		if (cl.isConnected()){
    			cl.pubm(payload, topicName, 0);
    		}
    	}
    }
}
