package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public class SyncBuildSpeed extends Sync
{
	
	

	public SyncBuildSpeed(int id)
	{
		super(id);
	}

	int lastBuildSpeed = 0;
	
	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		return this.isDirty || this.lastBuildSpeed != clone.getBuildAI().getBuildSpeed();
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		this.isDirty = false;
		this.lastBuildSpeed = clone.getBuildAI().getBuildSpeed();
	}

	@Override
	public boolean canBeEditedByClient()
	{
		return true;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.write(clone.getBuildAI().getBuildSpeed());
	}

	
	
	@Override
	public void read(DataInputStream in, EntityClone clone, EntityPlayer player) throws IOException
	{
		if(!clone.worldObj.isRemote && player.capabilities.isCreativeMode)
		{
			clone.getBuildAI().setBuildSpeed(in.read());
		}
		else
		{
			in.read();
		}
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{}

}
