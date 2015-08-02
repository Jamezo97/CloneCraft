package net.jamezo97.clonecraft.recipe;

import java.util.ArrayList;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CloneCraftCraftingHandler
{

	public static void add(IOnCrafted i)
	{
		crafters.add(i);
	}

	private static ArrayList<IOnCrafted> crafters = new ArrayList<IOnCrafted>();

	public static void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix)
	{
		for (int a = 0; a < crafters.size(); a++)
		{
			if (crafters.get(a).isValidItem(item, craftMatrix))
			{
				crafters.get(a).onCrafted(player, item, craftMatrix);
			}
		}

	}

}
