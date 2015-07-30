package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.build.EntityAIBuild;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public class SyncBuildData extends Sync
{

	public SyncBuildData(int id)
	{
		super(id);
	}
	
	boolean lastBuild = false;
	
	int lastBuildState, lastFullSize;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		EntityAIBuild ai = clone.getBuildAI();

		return isDirty || ai.isRunning() != lastBuild || ai.getBuildState() != lastBuildState || ai.getSchemFullSize() != lastFullSize;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		lastBuild = clone.getBuildAI().isRunning();
		lastBuildState = clone.getBuildAI().getBuildState();
		lastFullSize = clone.getBuildAI().getSchemFullSize();
		isDirty = false;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeBoolean(clone.getBuildAI().isRunning());
		out.writeInt(clone.getBuildAI().getSchemFullSize());
		out.write(clone.getBuildAI().getBuildState());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		if(clone.worldObj.isRemote)
		{
			clone.getBuildAI().setBuilding(in.readBoolean());
			clone.getBuildAI().setSchemFullSize(in.readInt());
			clone.getBuildAI().setBuildState(in.read());
		}
		else
		{
			clone.getBuildAI().setBuilding(in.readBoolean());
			in.readInt();
			in.read();
		}
	}
	

	@Override
	public boolean canBeEditedByClient()
	{
		return true;
	}

	
	
	
	
	

}
