package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncBuildIndex extends Sync
{

	public SyncBuildIndex(int id)
	{
		super(id);
	}
	
	int lastIndex = 0;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return isDirty || clone.getBuildAI().getIndex() != lastIndex;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		lastIndex = clone.getBuildAI().getIndex();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeInt(clone.getBuildAI().getIndex());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		clone.getBuildAI().setIndex(in.readInt());
	}

	
	
}
