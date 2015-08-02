package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncBlocks extends Sync
{

	public SyncBlocks(int id)
	{
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return false;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		ArrayList<Long> list = clone.getOptions().breakables.getArray();
		out.writeInt(list.size());
		for (int a = 0; a < list.size(); a++)
		{
			out.writeLong(list.get(a));
		}
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		int size = in.readInt();
		if (size > -1)
		{
			clone.getOptions().breakables.clear();
			long[] data = new long[size];
			for (int a = 0; a < size; a++)
			{
				data[a] = in.readLong();
			}
			clone.getOptions().breakables.importLong(data);
		}
	}

	@Override
	public int getChannel()
	{
		return 1;
	}

}
