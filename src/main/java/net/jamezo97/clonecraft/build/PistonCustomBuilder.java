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
			/*int x2 = x;
			int y2 = y;
			int z2 = z;
			
			switch(meta & 7)
			{
			case 0: y2--; break;
			case 1: y2++; break;
			case 2: z2--; break;
			case 3: z2++; break;
			case 4: x2--; break;
			case 5: x2++; break;
			}
			
			System.out.println("GOOOOOOOOO: " + x2 + ", " + y2 + ", " + z2);
			
			world.setBlock(x2, y2, z2, Blocks.diamond_block);
			
			if(world.getBlock(x2, y2, z2) == Blocks.air)
			{
				meta = meta & 7;
				
				world.setBlock(x, y, z, block, meta, 3);
			}
			else
			{
				int index = ai.getSchematicIndexFromWorld(x2, y2, z2);
				
				Block adj = ai.schem.blockAtSafe(index);
				
				
				if(adj instanceof BlockPistonExtension)
				{
					int metaAdj = RotationMapping.translate(adj, ai.schem.blockMetaAtSafe(index), ai.getRotate());
					
					world.setBlock(x2, y2, z2, adj, metaAdj, 2);
					world.setBlock(x, y, z, block, meta, 2);
				}
				else
				{
					meta = meta & 7;
					
					world.setBlock(x, y, z, block, meta, 3);
				}
			}*/
			
			
			
			
			
			
			
			if(using != null)
			{
				using.stackSize--;
			}
			
			return true;
		}
		
		/*if((meta & 7) == meta)
		{
			
			
			
			int x2 = x;
			int y2 = y;
			int z2 = z;
			
			switch(meta)
			{
			case 0: y2--; break;
			case 1: y2++; break;
			case 2: z2--; break;
			case 3: z2++; break;
			case 4: x2--; break;
			case 5: x2++; break;
			}
			
			int index = ai.getSchematicIndexFromWorld(x2, y2, z2);
			
			Block adj = ai.schem.blockAtSafe(index);
			
			
			
		}*/
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
