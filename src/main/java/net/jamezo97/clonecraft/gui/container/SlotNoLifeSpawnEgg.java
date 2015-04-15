package net.jamezo97.clonecraft.gui.container;

import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotNoLifeSpawnEgg extends Slot{

	TileEntityLifeInducer te;
	
	public SlotNoLifeSpawnEgg(TileEntityLifeInducer par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		this.te = par1iInventory;
	}

	@Override
	public boolean isItemValid(ItemStack par1ItemStack) {
		return te.isValidItem(par1ItemStack);
	}
	
	

}
