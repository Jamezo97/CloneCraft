package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class NoNotifyCustomBuilder implements CustomBuilder{

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using) 
	{
		world.setBlock(x, y, z, block, meta, 2);
		if(using != null)
		{
			using.stackSize--;
		}
		return y >= 0 && y < 256;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		return null;
	}

	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 2;
	}

	@Override
	public boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		//Good for redstone.
		return true;
	}
	
	@Override
	public boolean isBlockNormalizable()
	{
		return false;
	}

}
