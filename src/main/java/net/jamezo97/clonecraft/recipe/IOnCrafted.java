package net.jamezo97.clonecraft.recipe;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public interface IOnCrafted {

	/**
	 * When the valid stack is crafted.
	 * @param player
	 * @param result
	 * @param ic
	 */
	public void onCrafted(EntityPlayer player, ItemStack result, IInventory ic);
	
	/**
	 * Is the stack a valid output of this recipe.
	 * @param stack
	 * @param craftMatrix 
	 * @return
	 */
	public boolean isValidItem(ItemStack stack, IInventory craftMatrix);
	
}
