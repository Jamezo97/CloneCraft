package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncFoodLevel extends Sync
{

	int lastFoodLevel = 0;

	public SyncFoodLevel(int id)
	{
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return clone.foodStats.getFoodLevel() != lastFoodLevel;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		lastFoodLevel = clone.foodStats.getFoodLevel();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeInt(clone.foodStats.getFoodLevel());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		clone.foodStats.setFoodLevel(lastFoodLevel = in.readInt());
	}

	public int getChannel()
	{
		return 0;
	}

}
