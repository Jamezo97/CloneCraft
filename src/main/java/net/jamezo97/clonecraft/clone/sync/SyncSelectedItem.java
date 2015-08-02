package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncSelectedItem extends Sync
{

	int lastSelected = 0;

	public SyncSelectedItem(int id)
	{
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return clone.inventory.currentItem != lastSelected;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		lastSelected = clone.inventory.currentItem;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeInt(clone.inventory.currentItem);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		lastSelected = clone.inventory.currentItem = in.readInt();
	}

}
