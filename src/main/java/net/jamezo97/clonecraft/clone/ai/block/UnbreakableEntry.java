package net.jamezo97.clonecraft.clone.ai.block;

import net.minecraft.block.Block;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class UnbreakableEntry {

	ChunkCoordinates coordinates;
	
	Block block;
	
	int meta;
	
	public UnbreakableEntry(ChunkCoordinates coordinates, Block block, int meta)
	{
		this.coordinates = coordinates;
		this.block = block;
		this.meta = meta;
	}
	
	public boolean isSameBlock(World world)
	{
		return world.getBlock(coordinates.posX, coordinates.posY, coordinates.posZ) == block
				&& world.getBlockMetadata(coordinates.posX, coordinates.posY, coordinates.posZ) == meta;
	}
	
}
