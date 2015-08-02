package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FarmlandCustomBuilder implements CustomBuilder
{
	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using)
	{
		world.setBlock(x, y, z, block);
		return true;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z) 
	{
		return null;
	}

	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 0;
	}

	@Override
	public boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		return false;
	}
	
	@Override
	public boolean isBlockNormalizable()
	{
		return false;
	}
	
}
