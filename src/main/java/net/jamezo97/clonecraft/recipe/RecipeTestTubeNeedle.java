package net.jamezo97.clonecraft.recipe;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeTestTubeNeedle implements IRecipe, IOnCrafted{
	
	public RecipeTestTubeNeedle(){
		CloneCraftCraftingHandler.add(this);
	}

	@Override
	public void onCrafted(EntityPlayer player, ItemStack result, IInventory ic) {
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stackOld = ic.getStackInSlot(a);
			if(stackOld != null && stackOld.getItem() == CloneCraft.INSTANCE.itemTestTube){
				ItemStack stack = stackOld.copy();
				stack.stackSize = 1;
				stack.setItemDamage(0);
				ItemData data = new ItemData(stack);
				data.empty();
				data.save();
				if(!player.inventory.addItemStackToInventory(stack)){
					player.dropPlayerItemWithRandomChoice(stack, false);
				}
				return;
			}
		}
	}



	@Override
	public boolean isValidItem(ItemStack stack, IInventory craftMatrix) {
		return stack.getItem() == CloneCraft.INSTANCE.itemNeedle && stack.getItemDamage() == 2;
	}

	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		ItemStack needle = null, tube = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack s = ic.getStackInSlot(a);
			if(s != null){
				if(s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() == 0){
					if(needle != null){
						return false;
					}
					needle = s;
				}else if(s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() == 2){
					if(tube != null){
						return false;
					}
					tube = s;
				}
			}
		}
		return needle != null && tube != null && !(new ItemData(needle).isDirty());
	}
	
	

	@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ItemStack needle = null, tube = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack s = ic.getStackInSlot(a);
			if(s != null){
				if(s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() == 0){
					if(needle != null){
						return null;
					}
					needle = s;
				}else if(s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() == 2){
					if(tube != null){
						return null;
					}
					tube = s;
				}
			}
		}
		if(needle != null && tube != null){
			//Create a new needle, fill the needle data with the tube data, save the needle data to the new output, return the new output.
			ItemStack result = new ItemStack(CloneCraft.INSTANCE.itemNeedle, 1, 2);
			ItemData nData = new ItemData(needle);
			ItemData tData = new ItemData(tube);
			nData.fillWith(tData);
			result.setTagCompound(nData.save());
			return result;
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
