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
		return isDirty || lastX != clone.blockHighlight.posX || 
				lastY != clone.blockHighlight.posY || lastZ != clone.blockHighlight.posZ;
	}

	@Override
	public void updateValues(EntityClone clone) 
	{
		isDirty = false;
		lastX = clone.blockHighlight.posX;
		lastY = clone.blockHighlight.posY;
		lastZ = clone.blockHighlight.posZ;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException 
	{
		out.writeInt(clone.blockHighlight.posX);
		out.writeInt(clone.blockHighlight.posY);
		out.writeInt(clone.blockHighlight.posZ);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException 
	{
		clone.blockHighlight.posX = in.readInt();
		clone.blockHighlight.posY = in.readInt();
		clone.blockHighlight.posZ = in.readInt();
	}
	
	

}
