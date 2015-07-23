package net.jamezo97.clonecraft.recipe;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class RecipeNeedleTestTubeRecipe implements IRecipe, IOnCrafted{

	public RecipeNeedleTestTubeRecipe(){
		CloneCraftCraftingHandler.add(this);
	}
	
	
	
	/*@Override
	public void onCrafted(EntityPlayer player, ItemStack result, IInventory ic) {
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack stackOld = ic.getStackInSlot(a);
			if(stackOld != null && stackOld.getItem() == CloneCraft.instance.needle){
				ItemStack stack = stackOld.copy();
				stack.stackSize = 1;
				stack.setItemDamage(0);
				DNA data = new DNA(stack);
				data.drain();
				data.save(stack);
				if(!player.inventory.addItemStackToInventory(stack)){
					player.dropPlayerItem(stack);
				}
			}
		}
	}


	@Override
	public boolean isValidItem(ItemStack stack) {
		return stack.itemID == CloneCraft.testTube.itemID && stack.getItemDamage() == 1;
	}


	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		ItemStack needle = null, tube = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack s = ic.getStackInSlot(a);
			if(s != null){
				if(s.getItem().itemID == CloneCraft.instance.needle.itemID && s.getItemDamage() == 1){
					if(needle != null){
						return false;
					}
					needle = s;
				}else if(s.getItem().itemID == CloneCraft.instance.testTube.itemID && s.getItemDamage() == 0){
					if(tube != null){
						return false;
					}
					tube = s;
				}
			}
		}
		return needle != null && tube != null;
	}*/
	
	

	@Override
	public void onCrafted(EntityPlayer player, ItemStack result, IInventory ic) {
		for(int a = 0; a < ic.getSizeInventory(); a++)
		{
			ItemStack stackOld = ic.getStackInSlot(a);
			
			if(stackOld != null && stackOld.getItem() == CloneCraft.INSTANCE.itemNeedle)
			{
				ItemStack stack = stackOld.copy();
				stack.stackSize = 1;
				stack.setItemDamage(0);
				ItemData data = new ItemData(stack);
				data.empty();
				data.save(stack);
				
				if(!player.inventory.addItemStackToInventory(stack))
				{
					player.dropPlayerItemWithRandomChoice(stack, false);
				}
				return;
			}
		}
	}



	@Override
	public boolean isValidItem(ItemStack stack, IInventory craftMatrix) {
		return stack.getItem() == CloneCraft.INSTANCE.itemTestTube && stack.getItemDamage() == 1;
	}



	@Override
	public boolean matches(InventoryCrafting ic, World world) {
		ItemStack needle = null, tube = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack s = ic.getStackInSlot(a);
			if(s != null){
				if(s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() == 1){
					if(needle != null){
						return false;
					}
					needle = s;
				}else if(s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() == 0){
					if(tube != null){
						return false;
					}
					tube = s;
				}
			}
		}
		return needle != null && tube != null && !(new ItemData(tube).isDirty());
	}

	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ItemStack needle = null, tube = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack s = ic.getStackInSlot(a);
			if(s != null){
				if(s.getItem() == CloneCraft.INSTANCE.itemNeedle && s.getItemDamage() == 1){
					if(needle != null){
						return null;
					}
					needle = s;
				}else if(s.getItem() == CloneCraft.INSTANCE.itemTestTube && s.getItemDamage() == 0){
					if(tube != null){
						return null;
					}
					tube = s;
				}
			}
		}
		if(needle != null && tube != null){
			ItemStack result = new ItemStack(tube.getItem(), 1, 1);
			ItemData nData = new ItemData(needle);
			ItemData tData = new ItemData(tube);
			tData.fillWith(nData);
			result.setTagCompound(tData.save());
			return result;
		}
		return null;
	}

	/*@Override
	public ItemStack getCraftingResult(InventoryCrafting ic) {
		ItemStack needle = null, tube = null;
		for(int a = 0; a < ic.getSizeInventory(); a++){
			ItemStack s = ic.getStackInSlot(a);
			if(s != null){
				if(s.getItem().itemID == CloneCraft.instance.needle.itemID && s.getItemDamage() == 1){
					if(needle != null){
						return null;
					}
					needle = s;
				}else if(s.getItem().itemID == CloneCraft.instance.testTube.itemID && s.getItemDamage() == 0){
					if(tube != null){
						return null;
					}
					tube = s;
				}
			}
		}
		if(needle != null && tube != null){
			ItemStack result = new ItemStack(tube.itemID, 1, 1);
			DNA nData = new DNA(needle);
			DNA tData = new DNA(tube);
			tData.fillWith(nData);
			tData.save(result);
			return result;
		}
		return null;
	}*/

	@Override
	public int getRecipeSize() {
		return 0;
	}

	@Override
	public ItemStack getRecipeOutput() {
		return null;
	}

}
