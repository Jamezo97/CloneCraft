package net.jamezo97.clonecraft.clone.sync;

import java.util.ArrayList;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.network.Handler;
import net.jamezo97.clonecraft.network.Handler2UpdateCloneData;
import net.jamezo97.clonecraft.network.Handler4UpdateOptions;
import net.jamezo97.clonecraft.network.HandlerPacket;
import net.jamezo97.clonecraft.network.PacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;

public class Syncer {

	

	private final EntityClone clone;
	
	private final int frequency;
	
	Sync[] syncs;
	
	ArrayList<Sync> updated = new ArrayList<Sync>();
	
	
	public Syncer(EntityClone clone, int frequency){
		this.clone = clone;
		this.frequency = frequency;
		ArrayList<Sync> syncs = new ArrayList<Sync>();
		init(syncs);
		this.syncs = syncs.toArray(new Sync[syncs.size()]);
		//Reuse Sync ArrayList
		updated = syncs;
		syncs.clear();
	}
	
	
	
	public void tick(){
		if(!(clone.worldObj instanceof WorldServer)){return;}
		if(!clone.isEntityAlive()) return;
		if(clone.ticksExisted % frequency != 0) return;
		
		//UPDATE CHANGED VALUES TO WATCHING ENTITIES
		for(int a = 0; a < syncs.length; a++){
			if(syncs[a].checkNeedsUpdating(clone)){
				updated.add(syncs[a]);
			}
		}
//		boolean flag = clone.hasOwner();
		if(updated.size() > 0){
			Object[] players = ((WorldServer)clone.worldObj).getEntityTracker().getTrackingPlayers(clone).toArray();
			if(players != null && players.length > 0){
				Handler2UpdateCloneData handler_0 = new Handler2UpdateCloneData(clone);
				Handler2UpdateCloneData handler_1 = new Handler2UpdateCloneData(clone);
				for(int a = 0; a < updated.size(); a++)
				{
					if(updated.get(a).getChannel() == 0/* || (!flag && updated.get(a).getChannel() == 1)*/)
					{
						handler_0.add(clone, updated.get(a));
					}
					else if(updated.get(a).getChannel() == 1)
					{
						handler_1.add(clone, updated.get(a));
					}
					else if(updated.get(a).getChannel() == -1)
					{
						Object[] sendTo = updated.get(a).getPlayersToSendTo(clone);
						if(sendTo != null && sendTo.length > 0)
						{
							Handler h = new Handler2UpdateCloneData(updated.get(a), clone);
							for(int b = 0; b < sendTo.length; b++)
							{
								h.sendToPlayers(sendTo[b]);
							}
						}
					}
				}
				handler_0.close();
				handler_1.close();
				if(!handler_0.isEmpty())
				{
					for(int a = 0; a < players.length; a++)
					{
						handler_0.sendToPlayer(players[a]);
					}
				}
				if(!handler_1.isEmpty())
				{
					HandlerPacket packet = handler_1.getPacket();
					for(int a = 0; a < players.length; a++)
					{
						
						if(players[a] instanceof EntityPlayerMP && clone.canUseThisEntity((EntityPlayer)players[a])){
							
							PacketHandler.net.sendTo(packet, (EntityPlayerMP)players[a]);
						}
					}
				}
			}
			for(int a = 0; a < updated.size(); a++)
			{
				updated.get(a).updateValues(clone);
			}
			
			updated.clear();
		}
		
		//UPDATE NEW WATCHING ENTITIES
		
		current.addAll(((WorldServer)clone.worldObj).getEntityTracker().getTrackingPlayers(clone));
		
		newPlayers.clear();
		for(int a = 0; a < current.size(); a++)
		{
			if(prev == null || !prev.contains(current.get(a)) || (CloneCraft.INSTANCE.config.SYNC_FORCE != 0 && clone.ticksExisted % CloneCraft.INSTANCE.config.SYNC_FORCE == 0))
			{
				newPlayers.add(current.get(a));
			}
		}
		if(newPlayers.size() > 0)
		{
			Handler2UpdateCloneData handler_0 = new Handler2UpdateCloneData(clone);
			Handler2UpdateCloneData handler_1 = new Handler2UpdateCloneData(clone);
			for(int a = 0; a < syncs.length; a++)
			{
				if(syncs[a].getChannel() == 0/* || (!flag && syncs[a].getChannel() == 1)*/)
				{
					handler_0.add(clone, syncs[a]);
				}
				else if(/*flag && */syncs[a].getChannel() == 1)
				{
					handler_1.add(clone, syncs[a]);
				}
				else if(syncs[a].getChannel() == -1)
				{
					Handler h = new Handler2UpdateCloneData(syncs[a], clone);
					for(int b = 0; b < newPlayers.size(); b++)
					{
						h.sendToPlayers(newPlayers.get(b));
					}
				}
			}
			handler_0.close();
			handler_1.close();
			if(!handler_0.isEmpty())
			{
				for(int a = 0; a < newPlayers.size(); a++)
				{
					handler_0.sendToPlayer(newPlayers.get(a));
				}
			}
			if(!handler_1.isEmpty())
			{
				for(int a = 0; a < newPlayers.size(); a++)
				{
					if(clone.canUseThisEntity(newPlayers.get(a))){
						handler_1.sendToPlayer(newPlayers.get(a));
					}
					
				}
			}
			

			
		}
		//Swap arrays
		temp = prev;
		prev = current;
		current = temp;
		current.clear();
		
		
		
		
	}
	
	ArrayList<EntityPlayer> newPlayers = new ArrayList<EntityPlayer>();
	ArrayList<EntityPlayer> temp;
	ArrayList<EntityPlayer> current = new ArrayList();
	ArrayList<EntityPlayer> prev = new ArrayList();
	
	public int getSize(){
		return syncs.length;
	}
	
	public Sync getSync(int id){
		for(int a = 0; a < syncs.length; a++){
			if(syncs[a].id == id){
				return syncs[a];
			}
		}
		return null;
	}
	
	public static void init(ArrayList<Sync> syncs){
		syncs.add(new SyncTeam(ID_TEAM));
		syncs.add(new SyncExperience(ID_EXPE));
		syncs.add(new SyncName(ID_NAME));
		syncs.add(new SyncOwner(ID_OWNR));
		syncs.add(new SyncFoodLevel(ID_FOOD));
		syncs.add(new SyncSelectedItem(ID_ITEM));
		syncs.add(new SyncEntities(ID_ATCK));
		syncs.add(new SyncBlocks(ID_BLCK));
		syncs.add(new SyncEffects(ID_POTS));
		syncs.add(new SyncScale(ID_SCAL));
		syncs.add(new SyncItemOffer(ID_OFFR));
		syncs.add(new SyncShrinkCooldown(ID_SHRINKCOOL));
	}
	public static final int ID_TEAM = 	0;
	public static final int ID_EXPE = 	1;
	public static final int ID_NAME = 	2;
	public static final int ID_OWNR = 	3;
	public static final int ID_FOOD = 	4;
	public static final int ID_ITEM = 	5;
	public static final int ID_ATCK = 	6;
	public static final int ID_BLCK = 	7;
	public static final int ID_POTS = 	8;
	public static final int ID_SCAL = 	9;
	public static final int ID_OFFR = 	10;
	public static final int ID_SHRINKCOOL =	11;


	public void sendValueToServer(int idName) {
		Sync sync = this.getSync(idName);
		if(sync != null){
			Handler2UpdateCloneData handler = new Handler2UpdateCloneData(clone);
			handler.add(this.clone, sync);
			handler.close();
			handler.sendToServer();
		}
		
	}
	
}
