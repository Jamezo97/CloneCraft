package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BedCustomBuilder implements CustomBuilder{

	ItemStack required = new ItemStack(Items.bed);

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using) 
	{
		int otherMeta = meta;
		
		if(meta >= 8)
		{
			otherMeta -= 8;
		}
		else
		{
			otherMeta += 8;
		}
		
		int newX = x;
		int newZ = z;
		//Depending on which part of the bed im trying to build, search for a new block in the correct direction.
		//i.e., If i'm looking at the head of a bed, then look for the feet below the head.
		
		if(meta == 3 || meta == 9)
		{
			newX++;
		}
		else if(meta == 1 || meta == 11)
		{
			newX--;
		}
		else if(meta == 0 || meta == 10)
		{
			newZ++;
		}
		else if(meta == 2 || meta == 8)
		{
			newZ--;
		}
		else
		{
			System.out.println("NO TRANSLATE");
		}
		
		int index = ai.getSchematicIndexFromWorld(newX, y, newZ);
		
		if(index > -1)
		{
			if(ai.getSchematic().blockAtSafe(index) == block)
			{
				if(RotationMapping.translate(block, ai.getSchematic().blockMetaAtSafe(index), ai.getRotate()) == otherMeta)
				{
					world.setBlock(x, y, z, block, meta, 2);
					world.setBlock(newX, y, newZ, block, otherMeta, 3);
					
					if(using != null)
					{
						using.stackSize--;
					}
					
					return true;
				}
				else
				{
					System.out.println("Non similar block meta: " + ai.getSchematic().blockMetaAtSafe(index) );
				}
			}
			else
			{
				System.out.println("Non similar block");
			}
		}
		else
		{
			System.out.println("Out of bounds");
		}
		//3, 9   X+
		//11,1   X-
		//0,10   Z+
		//9, 2   Z-
		
		return false;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z) 
	{
		return required;
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
		return false;
	}

}
