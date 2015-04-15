package net.jamezo97.clonecraft.recipe;

import net.jamezo97.clonecraft.CloneCraft;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

//TODO Delete this class if never used.
public class RecipeEggToEmpty implements IRecipe{

	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		ItemStack eggs = null;
		ItemStack needle = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stack = ic.getStackInSlot(a);
			if(stack != null){
				if(stack.getItem() == Items.egg){
					if(eggs != null){return false;}
					eggs = stack;
				}else if(stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 0){
					if(needle != null){return false;}
					needle = stack;
				}
			}
		}
		return eggs != null && needle != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ItemStack needle = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stack = ic.getStackInSlot(a);
			if(stack != null){
				if(stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 0){
					needle = stack;
				}
			}
		}
		if(needle != null){
			
		}
		
		return null;
	}

	@Override
	public int getRecipeSize() {
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
