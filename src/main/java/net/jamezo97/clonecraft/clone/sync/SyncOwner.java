package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncOwner extends Sync
{

	String oldOwner = "";

	public SyncOwner(int id)
	{
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return isDirty || oldOwner == null || !oldOwner.equals(clone.getOwnerName());
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		oldOwner = clone.getOwnerName();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeUTF(clone.getOwnerName());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		clone.setOwner(oldOwner = in.readUTF());
	}

	@Override
	public int getChannel()
	{
		return 0;
	}

}
