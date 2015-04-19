package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Set;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import cpw.mods.fml.relauncher.Side;

public abstract class Handler{
	
	
	
	public abstract void handle(Side side, EntityPlayer player);

	public abstract void read(ByteBuf buf);

	public abstract void write(ByteBuf buf);
	
	public EntityClone getClone(EntityPlayer player, int entityId){
		Entity e = player.worldObj.getEntityByID(entityId);
		if(e != null && e instanceof EntityClone){
			return ((EntityClone)e);
		}
		return null;
	}
	
	public EntityClone getUsableClone(EntityPlayer player, int entityId){
		Entity e = player.worldObj.getEntityByID(entityId);
		if(e != null && e instanceof EntityClone && ((EntityClone)e).canUseThisEntity(player)){
			return ((EntityClone)e);
		}
		return null;
	}
	
	public int getId(){
		return Handler.getHandlerId(this);
	}
	
	public void sendToServer(){
		PacketHandler.net.sendToServer(new HandlerPacket(this));
	}
	
	public void sendToPlayers(Object... players){
		if(players == null)return;
		HandlerPacket packet = new HandlerPacket(this);
		for(int a = 0; a < players.length; a++){
			if(players[a] != null && players[a] instanceof EntityPlayerMP){
				PacketHandler.net.sendTo(packet, (EntityPlayerMP)players[a]);
			}
		}
	}
	
	public void sendToAllWatching(EntityClone clone) {
		Set<EntityPlayer> watching = clone.getWatchingEntities();
		if(watching != null){
			HandlerPacket packet = new HandlerPacket(this);
			for(EntityPlayer p : watching){
				if(p instanceof EntityPlayerMP){
					PacketHandler.net.sendTo(packet, (EntityPlayerMP)p);
				}
			}
		}
	}
	
	public void sendToOwnersWatching(EntityClone clone) {
		Set<EntityPlayer> watching = clone.getWatchingEntities();
		if(watching != null){
			HandlerPacket packet = new HandlerPacket(this);
			for(EntityPlayer p : watching){
				if(p instanceof EntityPlayerMP && clone.canUseThisEntity(p)){
					PacketHandler.net.sendTo(packet, (EntityPlayerMP)p);
				}
			}
		}
	}
	
	public void sendToOwnersWatchingExcluding(EntityClone clone, EntityPlayer exclude) {
		Set<EntityPlayer> watching = clone.getWatchingEntities();
		if(watching != null){
			HandlerPacket packet = new HandlerPacket(this);
			for(EntityPlayer p : watching){
				if(p != exclude && p instanceof EntityPlayerMP && clone.canUseThisEntity(p)){
					PacketHandler.net.sendTo(packet, (EntityPlayerMP)p);
				}
			}
		}
	}

	
	public void sendToPlayer(Object player) {
		if(player != null && player instanceof EntityPlayerMP){
			PacketHandler.net.sendTo(new HandlerPacket(this), (EntityPlayerMP)player);
		}
	}
	
	public void sendToPlayers(EntityPlayerMP... players){
		HandlerPacket packet = new HandlerPacket(this);
		for(int a = 0; a < players.length; a++){
			PacketHandler.net.sendTo(packet, players[a]);
		}
	}
	
	public HandlerPacket getPacket(){
		return new HandlerPacket(this);
	}
	
	public void sendToAllNear(double xCoord, double yCoord, double zCoord, double range, int dim) {
		PacketHandler.net.sendToAllAround(new HandlerPacket(this), new TargetPoint(dim, xCoord, yCoord, zCoord, range));
	}
	
	static HashMap<Integer, Class> idToClass = new HashMap<Integer, Class>();
	static HashMap<Class, Integer> classToId = new HashMap<Class, Integer>();
	static{
		registerHandler(0, Handler0SpinCentrifuge.class);
		registerHandler(1, Handler1CentrifugeItemStacks.class);
		registerHandler(2, Handler2UpdateCloneData.class);
		registerHandler(3, Handler3LifeInducerUpdates.class);
		registerHandler(4, Handler4UpdateOptions.class);
		registerHandler(5, Handler5TransferXP.class);
		registerHandler(6, Handler6KillClone.class);
		registerHandler(7, Handler7CloneClones.class);
		registerHandler(8, Handler8UpdateAttackEntities.class);
		registerHandler(9, Handler9UpdateBreakBlocks.class);
		registerHandler(10, Handler10ChangeOwner.class);
		
		
	}
	
	public static Handler getNewHandlerFromId(int id){
		Class c = idToClass.get(id);
		if(c != null){
			try {
				return (Handler) c.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static int getHandlerId(Handler handler){
		if(handler != null){
			return getHandlerId(handler.getClass());
		}
		return -1;
	}
	
	public static int getHandlerId(Class<? extends Handler> c){
		return classToId.get(c);
	}
	

	
	public static void registerHandler(int id, Class<? extends Handler> theClass){
		idToClass.put(id, theClass);
		classToId.put(theClass, id);
	}

}
