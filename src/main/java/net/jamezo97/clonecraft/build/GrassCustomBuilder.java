package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GrassCustomBuilder implements CustomBuilder
{

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using)
	{
		if(ai.shouldIgnoreItems())
		{
			world.setBlock(x, y, z, block, 0, 3);
			ai.playBlockSound(block, x, y, z);
		}
		else
		{
			world.setBlock(x, y, z, Blocks.dirt, 0, 3);
			ai.playBlockSound(Blocks.dirt, x, y, z);
		}
		return true;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		if(ai.shouldIgnoreItems())
		{
			return new ItemStack(block);
		}
		return new ItemStack(Blocks.dirt);
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
