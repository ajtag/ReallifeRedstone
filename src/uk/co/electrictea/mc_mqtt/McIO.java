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

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class McIO implements Listener {
	private McMqtt mcmqtt;
	private BlockStore ioBlockStore;
	
	public McIO(McMqtt mcmqtt, BlockStore ioBlockStore){
		this.mcmqtt = mcmqtt;
		this.ioBlockStore = ioBlockStore;
	}
	
	
	public void updateLevers(String topic, String message){
	  //TODO: rewrite this to return a list of matching blocks
	  //int blockid = this.getInputBlockID( topic );
	  //Logger.getLogger("INFO").info("BID: " +blockid);
		Block ib;
		ArrayList<InputLever> bl  = ioBlockStore.getInputBlockLocations(topic);
		int i = bl.size();
		byte lever;
		
		while (i > 0){
			ib = bl.get(i).getBlock();		
			//bl.get(i).getWorld().getBlockAt(bl.get(i).getLocation());
			lever = ib.getData();
			if(ib != null){
				  if( message.equalsIgnoreCase("1")){
					// Logger.getLogger("INFO").info("Set HIGH");
					 lever = (byte) (lever | 0x8);
				  }else{
					// Logger.getLogger("INFO").info("Set LOW");
					 lever = (byte) (lever & ~ 0x8);
				  }
				  ib.setData(lever);
				  updateLeverBlock(ib);		
			  }
			i--;
		}
	}
	
	
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onInteract(PlayerInteractEvent event){
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)
		{
			if( ioBlockStore.isIoBlock( event.getClickedBlock() ) ){
				event.setCancelled(true);
				event.getPlayer().chat("you can't do that Hal"); 
			}
		}
    }

	@EventHandler //Priority
	 public void BlockRedstoneEvent(BlockRedstoneEvent bre){
		/*
		 * code for 2 way levers
		 */
		/*
		if (bre.getBlock().getType() == Material.LEVER){
			String lstat;
			if ((bre.getBlock().getData() & 0x8) != 0x8){ lstat = "on"; }else{ lstat = "off"; }
			if(this.isInputBlock(bre.getBlock())){
					Logger.getLogger("INFO").info("Got Input Block");
			}
			//this.pubm( ("lever:"+lstat).getBytes() , "Lever/"+bre.getBlock().getLocation().toString(), 0);
		}
		*/
		
		
		if ( ( (bre.getBlock().getType() == Material.SIGN) || bre.getBlock().getType() == Material.SIGN_POST) ){
			String[] signText = ((Sign) bre.getBlock().getState()).getLines();
			if (signText[0].equalsIgnoreCase("[output]")){
				String signid = signText[1];
				byte[] message = {(byte)bre.getNewCurrent()};//new byte[1];
				
				
				this.mcmqtt.publish((String.valueOf((int)message[0])).getBytes() , "output/"+ signid);		
			}
		}
		
	 }
	
	 
	 @EventHandler
	 public void BreakBlock(BlockBreakEvent bbe){
		 ioBlockStore.removeBlock(bbe.getBlock());
	 }
	 
	 @EventHandler
	 public void SignChange(SignChangeEvent bce){
		 if (((bce.getBlock().getTypeId() == 63) || bce.getBlock().getTypeId() == 68)){
			 if ( bce.getLine(0).equalsIgnoreCase("[input]")){
				 //Logger.getLogger("INFO").info(bce.getLines()[0]);
				 //Logger.getLogger("INFO").info(bce.getBlock);
				 ioBlockStore.addBlock(bce.getBlock(), bce.getLines());
				 byte lever = 0x0;
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
				}
			 }
		 }	
	
		
		/*private Block getInputBlock(int inputID){
			return this.world.getBlockAt(this.inputBlocks.get(inputID).getLocation());
		}
		*/

	/*
		private int getInputBlockID(String inputID){
			Logger.getLogger("INFO").info("Searching for :" + inputID);
			for(int i=0; i < this.inputBlocks.size(); i++){
				if (this.inputBlocks.get(i).getSignName().equalsIgnoreCase( inputID ) ){
					return i;
				}else
				 Logger.getLogger("INFO").info(this.inputBlocks.get(i).getSignName());
				
			}
			return -1;
		}
*/
		private void updateLeverBlock(Block lb){
			
			 if (lb.getType() != Material.LEVER){
				 return;
			 }
				 
				 byte ld = lb.getData();
				 Location lloc = lb.getLocation();
				 Block b = lb;
				 
				 if ((ld & 0x7) == 0x7 || (ld & 0x7) == 0x0) //ceiling
					 b = lloc.add(0, 1, 0).getBlock();
				 else if ((ld & 0x7) == 0x5 || (ld & 0x7) == 0x6) //floor
					 b = lloc.add(0, -1, 0).getBlock();
				 else if ((ld & 0x7) == 0x4) //north
					 b = lloc.add(0, 0, -1).getBlock();
				 else if ((ld & 0x7) == 0x2) //west
					 b = lloc.add(-1, 0, 0).getBlock();
				 else if ((ld & 0x7) == 0x3) //east
					 b = lloc.add(1, 0, 0).getBlock();
				 else if ((ld & 0x7) == 0x1) //south
					 b = lloc.add(0, 0, 1).getBlock();
				 
				 byte oldData = b.getData();
				 byte notData;
				 
				 if (oldData>1) notData = (byte)(oldData-1);
				 else if (oldData<15) notData = (byte)(oldData+1);
				 else notData = 0;
				 
				 b.setData(notData, true);
				 b.setData(oldData, true);

		}
		
}
