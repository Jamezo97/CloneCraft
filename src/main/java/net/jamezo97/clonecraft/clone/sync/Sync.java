package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.PlayerTeam;
import net.minecraft.world.WorldServer;

public abstract class Sync {
	
	public final int id;
	
	public Sync(int id){
		this.id = id;
	}

	boolean isDirty = false;
	
	public abstract boolean checkNeedsUpdating(EntityClone clone);
	
	public abstract void updateValues(EntityClone clone);
	
	public Object[] getPlayersToSendTo(EntityClone clone){
		if(clone.worldObj instanceof WorldServer){
			return ((WorldServer)clone.worldObj).getEntityTracker().getTrackingPlayers(clone).toArray();
		}
		return null;
	}
	
	
	//0 is all players
	//1 is just the owner(s)
	
	//-1 Is custom. Gets players from method 'getPlayersToSendTo'
	public int getChannel(){
		return 0;
	}
	
	public boolean canBeEditedByClient(){
		return false;
	}
	
	public abstract void write(DataOutputStream out, EntityClone clone)throws IOException;

	public abstract void read(DataInputStream in, EntityClone clone)throws IOException;
	
	public void setDirty()
	{
		isDirty = true;
	}
	
}
