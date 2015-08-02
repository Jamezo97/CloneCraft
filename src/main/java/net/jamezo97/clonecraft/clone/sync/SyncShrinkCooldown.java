package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncShrinkCooldown extends Sync
{

	public SyncShrinkCooldown(int id)
	{
		super(id);
	}

	int lastCooldown = 0;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return isDirty || Math.abs(clone.getShrinkCooldown() - lastCooldown) > 200;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		this.lastCooldown = clone.getShrinkCooldown();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeInt(clone.getShrinkCooldown());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		clone.setShrinkCooldown(in.readInt());
	}

}
