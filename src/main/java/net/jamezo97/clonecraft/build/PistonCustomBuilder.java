package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class PistonCustomBuilder implements CustomBuilder
{

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using)
	{
		//If it's the base of the piston
		
		if(block instanceof BlockPistonBase)
		{
			meta = meta & 7;
			
			world.setBlock(x, y, z, block, meta, 3);
			
			if(using != null)
			{
				using.stackSize--;
			}
			
			return true;
		}
		
		return false;
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
		return true;
	}

	@Override
	public boolean isBlockNormalizable()
	{
		return false;
	}

	
	
}
