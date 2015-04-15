package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.PlayerTeam;
import net.minecraft.world.WorldServer;

public class SyncTeam extends Sync{
	
	
	
	public SyncTeam(int id) {
		super(id);
	}

	PlayerTeam last = null;
	
	public boolean checkNeedsUpdating(EntityClone clone){
		return (last != clone.team) || isDirty;
	}
	
	public void updateValues(EntityClone clone){
		last = clone.team;
	}
	
	
	public void write(DataOutputStream out, EntityClone clone)throws IOException{
		out.writeInt(clone.team.teamID);
	}

	public void read(DataInputStream in, EntityClone clone)throws IOException{
		last = clone.team = PlayerTeam.getByID(in.readInt());
	}

	@Override
	public boolean canBeEditedByClient() {
		return true;
	}
	
	
	
}
