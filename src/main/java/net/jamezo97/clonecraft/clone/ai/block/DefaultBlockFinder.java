package net.jamezo97.clonecraft.clone.ai.block;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;

public class DefaultBlockFinder implements BlockFinder{

	ArrayList<UnbreakableEntry> entries = new ArrayList<UnbreakableEntry>();
	
	@Override
	public boolean hasNextBlock() {
		
		return false;
	}

	@Override
	public ChunkCoordinates getNextBlock(EntityAIMine ai) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onFinished(EntityAIMine entityAI) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mustBeCloseToBreak() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void saveState(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadState(NBTTagCompound nbt) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cantBreakBlock(ChunkCoordinates cc, Block break_block) {
		// TODO Auto-generated method stub
		
	}

	public void cloneStateChanged(){
		
	}
	
	
}
