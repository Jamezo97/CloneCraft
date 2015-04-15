package net.jamezo97.clonecraft.gui.container;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotCentrifuge extends Slot{

	TileEntityCentrifuge te;
	
	EntityPlayer using = null;
	
	public SlotCentrifuge(TileEntityCentrifuge iinventory, int i, int j, int k, EntityPlayer player) {
		super(iinventory, i, j, k);
		this.te = iinventory;
		using = player;
	}
	
	public boolean isItemValid(ItemStack itemstack) {
		if(itemstack.getItem() == CloneCraft.INSTANCE.itemTestTube && itemstack.getItemDamage() == 1){
			return true;
		}
		return false;
	}

	@Override
	public int getSlotStackLimit() {
		return 1;
	}

	@Override
	public void putStack(ItemStack p_75215_1_) {
		super.putStack(p_75215_1_);
		if(!this.te.getWorldObj().isRemote){
			System.out.println("PutStack");
			te.sendItemsToAroundExcluding(using);
		}
	}

	@Override
	public void onPickupFromSlot(EntityPlayer p_82870_1_, ItemStack p_82870_2_) {
		super.onPickupFromSlot(p_82870_1_, p_82870_2_);
		if(!this.te.getWorldObj().isRemote){
			System.out.println("TookStack");
			te.sendItemsToAroundExcluding(using);
		}
	}
	
	
	
	

}
