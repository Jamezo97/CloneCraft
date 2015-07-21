package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncBlockHighlight extends Sync{

	public SyncBlockHighlight(int id)
	{
		super(id);
	}
	
	int lastX = 0, lastY = -1, lastZ = 0;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone) 
	{
		return isDirty || lastX != clone.getBuildAI().blockHighlight.posX || 
				lastY != clone.getBuildAI().blockHighlight.posY || lastZ != clone.getBuildAI().blockHighlight.posZ;
	}

	@Override
	public void updateValues(EntityClone clone) 
	{
		isDirty = false;
		lastX = clone.getBuildAI().blockHighlight.posX;
		lastY = clone.getBuildAI().blockHighlight.posY;
		lastZ = clone.getBuildAI().blockHighlight.posZ;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException 
	{
		out.writeInt(clone.getBuildAI().blockHighlight.posX);
		out.writeInt(clone.getBuildAI().blockHighlight.posY);
		out.writeInt(clone.getBuildAI().blockHighlight.posZ);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException 
	{
		clone.getBuildAI().blockHighlight.posX = in.readInt();
		clone.getBuildAI().blockHighlight.posY = in.readInt();
		clone.getBuildAI().blockHighlight.posZ = in.readInt();
	}
	
	

}
