package net.jamezo97.clonecraft.raytrace;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface BlockChecker{
	
	public boolean isBlockSeeThru(Block block, int x, int y, int z, World world);
	
}