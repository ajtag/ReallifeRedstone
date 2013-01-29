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

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;

import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Sign;

import java.util.logging.Logger;


public class switchListener implements Listener {
	 @EventHandler
	 public void BlockRedstoneEvent(BlockRedstoneEvent bre){
		//Location breloc;
		//breloc = bre.getBlock().getLocation();
		Logger.getLogger("REDSTONE").info( "" + bre.getBlock().getTypeId() );
		
		if (  /*(bre.getBlock().isBlockPowered() == true) &&*/ ((bre.getBlock().getTypeId() == 63) || bre.getBlock().getTypeId() == 68) ){
			Logger.getLogger("REDSTONE").info( ((Sign) bre.getBlock().getState()).getLine(0));
		}
	 }


	 
	 @EventHandler
	 public void SignChange(SignChangeEvent bce){
		 if (((bce.getBlock().getTypeId() == 63) || bce.getBlock().getTypeId() == 68)){
			 if ( bce.getLine(0).equalsIgnoreCase("[output]")){
				 Logger.getLogger("INFO").info("Output Sign");
				 byte lever = 0x0;
				 
				 if (bce.getLine(1).equalsIgnoreCase("1")){
					 Logger.getLogger("INFO").info("Sign HIGH");
					 lever = (byte) (lever | 0x8);
				 }else{
					 Logger.getLogger("INFO").info("Sign LOW");
					 lever = (byte) (lever & ~ 0x8);
				 }
					 
					 if (bce.getBlock().getTypeId() == 63){
						 lever = (byte) (lever | 0x5 );
					 }else{
						 byte wallsign = bce.getBlock().getData();
						 if ((wallsign & 0x7) == 0x2) //north
							 lever = (byte) (lever | 0x4);
						 if ((wallsign & 0x7) == 0x4) //west
							 lever = (byte) (lever | 0x2);
						 if ((wallsign & 0x7) == 0x3) //east
							 lever = (byte) (lever | 0x3);
						 if ((wallsign & 0x7) == 0x5) //south
							 lever = (byte) (lever | 0x1);
					 }	 
					 bce.getBlock().setType(Material.LEVER);
					 bce.getBlock().setData(lever);	 
					 if ((lever & 0x8) == 0x8){
						 ;
					 }
				}
			 }
		 }	 
	 
	 
	 @EventHandler
	 public void blockDestroy(BlockBreakEvent bbe){
		 if (bbe.getBlock().getType().getId() == 69){
			 Logger.getLogger("INFO").info("Lever Destroyed!");
		 }
	 }
}

