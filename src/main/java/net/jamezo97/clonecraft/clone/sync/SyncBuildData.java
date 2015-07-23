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
	
	int lastIndex, lastBuildState;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone)
	{
		EntityAIBuild ai = clone.getBuildAI();

		return isDirty || ai.isRunning() != lastBuild || ai.getIndex() != lastIndex || ai.getBuildState() != lastBuildState;
	}

	@Override
	public void updateValues(EntityClone clone)
	{
		lastBuild = clone.getBuildAI().isRunning();
		lastIndex = clone.getBuildAI().getIndex();
		lastBuildState = clone.getBuildAI().getBuildState();
		isDirty = false;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException
	{
		out.writeBoolean(clone.getBuildAI().isRunning());
		out.writeInt(clone.getBuildAI().getIndex());
		out.writeInt(clone.getBuildAI().getBuildState());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException
	{
		if(clone.worldObj.isRemote)
		{
			clone.getBuildAI().setBuilding(in.readBoolean());
			clone.getBuildAI().setIndex(in.readInt());
			clone.getBuildAI().setBuildState(in.readInt());
		}
		else
		{
			clone.getBuildAI().setBuilding(in.readBoolean());//in.readBoolean());
			in.readInt();
			in.readInt();
		}
	}
	

	@Override
	public boolean canBeEditedByClient()
	{
		return true;
	}

	
	
	
	
	

}
