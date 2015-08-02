package net.jamezo97.clonecraft.recipe;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeClearDNAItem implements IRecipe
{

	@Override
	public boolean matches(InventoryCrafting ic, World world)
	{
		boolean foundOne = false;
		for (int a = 0; a < ic.getSizeInventory(); a++)
		{
			ItemStack s = ic.getStackInSlot(a);
			if (s != null)
			{
				if ((s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() != 0)
						|| (s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() != 0))
				{
					if (foundOne)
					{
						return false;
					}
					foundOne = true;
				}
				else
				{
					return false;
				}
			}
		}
		return foundOne;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic)
	{
		ItemStack item = null;
		for (int a = 0; a < ic.getSizeInventory(); a++)
		{
			ItemStack s = ic.getStackInSlot(a);
			if (s != null)
			{
				if ((s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() != 0)
						|| (s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() != 0))
				{
					if (item != null)
					{
						return null;
					}
					item = s;
				}
			}
		}
		if (item != null)
		{
			ItemStack returnItem = item.copy();
			returnItem.stackSize = 1;
			returnItem.setItemDamage(0);
			ItemData itemDNA = new ItemData(returnItem);
			itemDNA.empty();
			itemDNA.save(returnItem);
			return returnItem;
		}
		return item;
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
