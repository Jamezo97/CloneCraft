package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncDisplayMessage extends Sync{

	public SyncDisplayMessage(int id) 
	{
		super(id);
	}

	String lastMessage = "";
	
	int lastColour = 0;
	
	@Override
	public boolean checkNeedsUpdating(EntityClone clone) 
	{
		return isDirty || clone.getDisplayMessageColour() != lastColour || !clone.getDisplayMessage().equals(lastMessage);
	}

	@Override
	public void updateValues(EntityClone clone) 
	{
		this.lastColour = clone.getDisplayMessageColour();
		this.lastMessage = clone.getDisplayMessage();
		isDirty = false;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException {
		out.writeInt(clone.getDisplayMessageColour());
		out.writeInt(clone.getDisplayMessageCooldown());
		out.writeUTF(clone.getDisplayMessage());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		clone.setDisplayMessageColour(in.readInt());
		clone.setDisplayMessageCooldown(in.readInt());
		clone.setDisplayMessage(in.readUTF());
	}
	
	

}
