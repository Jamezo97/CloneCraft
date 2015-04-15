package net.jamezo97.clonecraft.recipe;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeEmptyEggToSpawnEgg implements IRecipe, IOnCrafted{
	
	public RecipeEmptyEggToSpawnEgg(){
//		CloneCraftCraftingHandler.add(this);
	}

	@Override
	public void onCrafted(EntityPlayer player, ItemStack result, IInventory ic) {
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stack = ic.getStackInSlot(a);
			if(stack != null && stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 2){
				ItemStack returnStack = stack.copy();
				returnStack.stackSize = 1;
				ItemData dna = new ItemData(returnStack);
				dna.empty();
				dna.save();
				returnStack.setItemDamage(0);
				if(!player.inventory.addItemStackToInventory(returnStack)){
					player.dropPlayerItemWithRandomChoice(returnStack, false);
				}
				return;
			}
		}
	}

	@Override
	public boolean isValidItem(ItemStack stack, IInventory craftMatrix) {
		return stack.getItem() == CloneCraft.INSTANCE.itemSpawnEgg;	
	}

	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		ItemStack needle = null;
		ItemStack egg = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stack = ic.getStackInSlot(a);
			if(stack != null){
				if(stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 2){
					if(needle != null) return false;
					needle = stack;
				}else if(stack.getItem() == CloneCraft.INSTANCE.itemEmptyEgg){
					if(egg != null) return false;
					egg = stack;
				}else{
					return false;
				}
			}
		}
		return needle != null && egg != null;
	}

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ItemStack needle = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stack = ic.getStackInSlot(a);
			if(stack != null){
				if(stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 2){
					needle = stack;
				}
			}
		}
		if(needle != null){
			ItemStack itemReturn = new ItemStack(CloneCraft.INSTANCE.itemSpawnEgg);
			ItemData dna = new ItemData(needle);
			itemReturn.setTagCompound(dna.save());
			return itemReturn;
		}
		
		return new ItemStack(CloneCraft.INSTANCE.itemSpawnEgg);
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
