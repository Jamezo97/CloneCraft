package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class LiquidCustomBuilder implements CustomBuilder
{
	
	ItemStack toUse = null;
	
	Block toPlace;
	
	public LiquidCustomBuilder(ItemStack toUse, Block toPlace)
	{
		this.toUse = toUse;
		this.toPlace = toPlace;
	}
	
	public LiquidCustomBuilder(Item toUse, Block toPlace)
	{
		this.toUse = new ItemStack(toUse);
		this.toPlace = toPlace;
	}

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using)
	{
		if(meta == 0)
		{
			world.setBlock(x, y, z, toPlace, 0, 3);
			
			if(using != null)
			{
				using.stackSize--;
				
				if(using.stackSize <= 0)
				{
					ai.clone.setCurrentItemOrArmor(0, null);
//					ai.clone.inventory.removeItemStackFromInventory(using);
					//Set it back to 1 to stop the EntityAIBuild from removing the itemstack in the current slot
					using.stackSize = 1;
				}
				
				ai.clone.inventory.addItemStackToInventory(new ItemStack(Items.bucket));
			}
		}
		
		
		return true;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		return toUse;
	}

	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 2;
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
