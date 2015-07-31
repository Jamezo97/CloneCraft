package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class PlantCustomBuilder implements CustomBuilder{
	
	ItemStack required;
	
	Item seeds;
	
	IPlantable plantable = null;
	
	public PlantCustomBuilder(Item seeds)
	{
		this.seeds = seeds;
		
		if(seeds instanceof IPlantable)
		{
			plantable = (IPlantable)seeds;
		}
		
		this.required = new ItemStack(seeds);
	}
	
	public PlantCustomBuilder(ItemStack seeds)
	{
		this.seeds = seeds.getItem();
		
		if(seeds.getItem() instanceof IPlantable)
		{
			plantable = (IPlantable)seeds.getItem();
		}
		
		this.required = seeds;
	}

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using) 
	{
		if(y > 1)
		{
			Block below = world.getBlock(x, y-1, z);
			
			if(!below.isAir(world, x, y, z))
			{
				if(plantable != null && !below.canSustainPlant(world, x, y-1, z, ForgeDirection.UP, plantable))
				{
					int belowIndex = ai.getSchematicIndexFromWorld(x, y-1, z);
					
					if(belowIndex > -1)
					{
						if(ai.getSchematic().blockAtSafe(belowIndex) == Blocks.farmland && (below == Blocks.dirt || below == Blocks.grass))
						{
							world.playSoundEffect((double)(x + 0.5F), (double)(y + 0.5F), (double)(z + 0.5F), below.stepSound.getStepResourcePath(), (below.stepSound.getVolume() + 1.0F) / 2.0F, below.stepSound.getPitch() * 0.8F);

							world.setBlock(x, y-1, z, Blocks.farmland);
							below = Blocks.farmland;
						}
					}
				}
				
				
				if(plantable == null || below.canSustainPlant(world, x, y-1, z, ForgeDirection.UP, plantable))
				{
					if(block instanceof BlockCocoa)
					{
						meta = meta & 3;
					}
					else
					{
						meta = 0;
					}
					world.setBlock(x, y, z, block, meta, 3);
					
					
					if(using != null)
					{
						using.stackSize--;
					}
					return true;
				}
			}
		}
		
		return false;
	}
	
	public ItemStack getRequiredItem()
	{
		return required;
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
