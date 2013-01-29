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

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class InputLever {
	private String[] signText;
	private Location signLocation;
	private World world;
	
	public World getWorld(){
		return world;
	}
	
	public InputLever(String[] signText, Location signLocation, World signWorld){
		this.signText = signText;
		this.signLocation = signLocation;
		this.world = signWorld;
	}
	
	public Location getLocation(){
		return this.signLocation;
	}
	
	public String getSignType(){
		return this.signText[0].replace("[", "").replace("]", "");
	}
	public String[] getSignText(){
		return this.signText;
	}
	public String getSignName(){
		return this.signText[1];
	}
	public Block getBlock(){
		return world.getBlockAt(signLocation);
	}
}