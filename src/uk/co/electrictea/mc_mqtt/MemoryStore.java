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


import org.bukkit.Material;
import org.bukkit.block.Block;

public class MemoryStore implements BlockStore{
	//this store is temporary, so Input levers will only last as long as the server
	ArrayList<InputLever> inputBlocks;
	
	public MemoryStore () {
		inputBlocks = new ArrayList<InputLever>(10);
	}
	
	public int length(){
		return inputBlocks.size();
	}
	
	public void addBlock(Block ioBlock, String[] signText) {
		this.inputBlocks.add(
				new InputLever(
						signText, 
						ioBlock.getLocation(), 
						ioBlock.getWorld()
				)
			);
	}
	
	public boolean isIoBlock(Block ioBlock){
		if(ioBlock.getType() == Material.LEVER){
			for(int i=0; i < this.inputBlocks.size(); i++){
				if (this.inputBlocks.get(i).getLocation().hashCode() == ioBlock.getLocation().hashCode()){
					return true;
				}
			}
		}
		return false;
	}
	
	public ArrayList<InputLever> getInputBlockLocations(String topic){
		ArrayList<InputLever> bl = new ArrayList<InputLever>();
		for(int i=0; i < this.inputBlocks.size(); i++){
			if (inputBlocks.get(i).getSignName().equalsIgnoreCase( topic ) ){
				bl.add(inputBlocks.get(i));
			}	
		}
		return bl;
	}
	
	public void removeBlock(Block ioBlock){
		if(ioBlock.getType() == Material.LEVER){
			for(int i=0; i < this.inputBlocks.size(); i++){
				if (this.inputBlocks.get(i).getLocation().hashCode() == ioBlock.getLocation().hashCode()){
					this.inputBlocks.remove(i);
				}
			}
		}
	}
}