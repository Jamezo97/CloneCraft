package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DoorCustomBuilder implements CustomBuilder{

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z) 
	{
		/*if(block == Blocks.wooden_door)
		{
			return new ItemStack(Items.wooden_door);
		}
		else if(block == Blocks.iron_door)
		{
			return new ItemStack(Items.iron_door);
		}*/
		return null;
	}

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using) 
	{
		if(y+1 > 255)
		{
			return false;
		}
		
		Block blockOnTop = ai.schem.blockAtSafe(ai.getIndex() + ai.schem.getLayerSize());
		
		if(blockOnTop == block)
		{
			int metaOnTop = RotationMapping.translate(blockOnTop, ai.schem.blockMetaAtSafe(ai.getIndex() + ai.schem.getLayerSize()), ai.getRotate());
			
			world.setBlock(x, y, z, block, meta, 2);
			world.setBlock(x, y+1, z, blockOnTop, metaOnTop, 3);
			
			
			
			if(using != null)
			{
				using.stackSize--;
			}
			return true;
		}
		return false;
	}


	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 1;
	}

	@Override
	public boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		//If a door changes its metadata value, it's probably the player using the door. So don't change it back :P
		return true;
	}

	
	
	@Override
	public boolean isBlockNormalizable()
	{
		return false;
	}
	



}
