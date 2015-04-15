package net.jamezo97.clonecraft.watcher;

import io.netty.buffer.ByteBuf;

import java.util.List;
import java.util.Set;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

public abstract class Watchable {

	final int id;
	
	public Watchable(int id){
		this.id = id;
	}
	
	public int getId(){
		return id;
	}
	
	public Object[] toSendUpdateTo(EntityClone clone){
		if(clone.worldObj instanceof WorldServer){
			Set<EntityPlayer> trackingPlayers = ((WorldServer)clone.worldObj).getEntityTracker().getTrackingPlayers(clone);
			if(trackingPlayers != null){
				return trackingPlayers.toArray();
			}
		}
		return null;
	}
	
	/**
	 * 
	 * @return The amount of variables this watcher watches
	 */
	public abstract int getSize();
	
	public abstract Object get(EntityClone clone, int index);
	
	public abstract void write(ByteBuf buf, EntityClone clone);
	
	public abstract Object[] read(ByteBuf buf);
	
	public abstract void handle(Object[] data, EntityClone clone);
	
}
