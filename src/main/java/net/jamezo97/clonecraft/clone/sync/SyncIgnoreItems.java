package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public class SyncIgnoreItems extends Sync
{
	
	

	public SyncIgnoreItems(int id)
	{
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{return false;}

	@Override
	public void updateValues(EntityClone clone)
	{}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException
	{
//		System.out.println("WRITE");
		out.writeBoolean(clone.getBuildAI().shouldIgnoreItems());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException{}

	@Override
	public void read(DataInputStream in, EntityClone clone, EntityPlayer player) throws IOException
	{
		boolean set = in.readBoolean();

//		System.out.println("READ: " + set);
		if(!clone.worldObj.isRemote && player != null && player.capabilities.isCreativeMode)
		{
			clone.getBuildAI().ignoreItems(set);
		}
	}

	@Override
	public boolean canBeEditedByClient()
	{
		return true;
	}
	
	
	
	
	

}
