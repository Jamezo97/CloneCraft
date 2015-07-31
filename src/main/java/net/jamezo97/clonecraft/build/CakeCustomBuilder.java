package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CakeCustomBuilder implements CustomBuilder
{

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using)
	{
		world.setBlock(x, y, z, block, 0, 3);
		if(using != null)
		{
			using.stackSize--;
		}
		return true;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		return new ItemStack(Items.cake);
	}

	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 1;
	}

	@Override
	public boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		return true;
	}

	@Override
	public boolean isBlockNormalizable()
	{
		return true;
	}
	
	

}
