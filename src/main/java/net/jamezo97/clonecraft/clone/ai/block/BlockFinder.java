package net.jamezo97.clonecraft.clone.ai.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

public interface BlockFinder{
	
	public boolean hasNextBlock();
	
	public ChunkCoordinates getNextBlock();
	
	public void onFinished(EntityAIMine entityAI);
	
	public void saveState(NBTTagCompound nbt);
	
	public void loadState(NBTTagCompound nbt);
	
}
