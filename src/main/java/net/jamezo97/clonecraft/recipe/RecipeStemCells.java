package net.jamezo97.clonecraft.recipe;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeStemCells  implements IRecipe, IOnCrafted{
	
	public RecipeStemCells()
	{
		CloneCraftCraftingHandler.add(this);
	}

	@Override
	public void onCrafted(EntityPlayer player, ItemStack result, IInventory ic)
	{
		for(int a = 0; a < ic.getSizeInventory(); a++)
		{
			ItemStack stackOld = ic.getStackInSlot(a);
			if(stackOld != null && stackOld.getItem() == CloneCraft.INSTANCE.itemTestTube)
			{
				ItemStack stack = stackOld.copy();
				stack.stackSize = 1;
				stack.setItemDamage(0);
				ItemData data = new ItemData(stack);
				data.empty();
				data.save();
				
				if(!player.inventory.addItemStackToInventory(stack))
				{
					player.dropPlayerItemWithRandomChoice(stack, false);
				}
				return;
			}
		}
	}



	@Override
	public boolean isValidItem(ItemStack stack, IInventory craftMatrix)
	{
		return stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 3;
	}

	@Override
	public boolean matches(InventoryCrafting ic, World world)
	{
		ItemStack needle = null, tube = null, bonemeal = null;
		
		for(int a = 0; a < ic.getSizeInventory(); a++)
		{
			ItemStack s = ic.getStackInSlot(a);
			if(s != null)
			{
				if(s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() == 0)
				{
					if(needle != null)
					{
						return false;
					}
					needle = s;
				}
				else if(s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() == 2)
				{
					if(tube != null || new ItemData(s).getId() != CCEntityList.classToId.get(EntityClone.class))
					{
						return false;
					}
					tube = s;
				}
				else if(s.getItem() == Items.dye && s.getItemDamage() == 15)
				{
					if(bonemeal != null)
					{
						return false;
					}
					bonemeal = s;
				}
				else
				{
					return false;
				}
			}
		}
		return needle != null && bonemeal != null && tube != null && !(new ItemData(needle).isDirty());
	}
	
	

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic)
	{
		ItemStack needle = null, tube = null, bonemeal = null;
		
		for(int a = 0; a < ic.getSizeInventory(); a++)
		{
			ItemStack s = ic.getStackInSlot(a);
			if(s != null)
			{
				if(s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() == 0)
				{
					if(needle != null)
					{
						return null;
					}
					needle = s;
				}
				else if(s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() == 2)
				{
					if(tube != null || new ItemData(s).getId() != CCEntityList.classToId.get(EntityClone.class))
					{
						return null;
					}
					tube = s;
				}
				else if(s.getItem() == Items.dye && s.getItemDamage() == 15)
				{
					if(bonemeal != null)
					{
						return null;
					}
					bonemeal = s;
				}
				else
				{
					return null;
				}
			}
		}
		if(needle != null && tube != null && bonemeal != null){
			//Create a new needle, fill the needle data with the tube data, save the needle data to the new output, return the new output.
/*			ItemStack result = new ItemStack(CloneCraft.INSTANCE.itemNeedle, 1, 2);
			ItemData nData = new ItemData(needle);
			ItemData tData = new ItemData(tube);
			nData.fillWith(tData);
			result.setTagCompound(nData.save());*/
			return new ItemData().setDirty().save(new ItemStack(CloneCraft.INSTANCE.itemNeedle, 1, 3));
		}
		return null;
	}

	@Override
	public int getRecipeSize()
	{
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput()
	{
		return null;
	}

}
