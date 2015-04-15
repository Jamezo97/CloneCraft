package net.jamezo97.clonecraft.gui.container;

import java.util.Iterator;

import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerLifeInducer extends Container{

	public TileEntityLifeInducer inducer;
	
	public ContainerLifeInducer(InventoryPlayer playerInven, TileEntityLifeInducer tileEntity) {
		inducer = tileEntity;
		
		for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
            	addSlotToContainer(new SlotNoLifeSpawnEgg(tileEntity, k + i * 9, 8 + k * 18, 35 + i * 18));
            }
        }
		

        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
            	addSlotToContainer(new Slot(playerInven, k + i * 9 + 9, 8 + k * 18, 108 + i * 18));
            }
        }
		for (int j = 0; j < 9; j++)
        {
        	addSlotToContainer(new Slot(playerInven, j, 8 + j * 18, 166));
        }
        
	}
	
	
	int lastPower = -1;
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		Iterator var1 = crafters.iterator();
		while(var1.hasNext()){
			ICrafting crafting = (ICrafting)var1.next();
			if(lastPower != inducer.power){
				crafting.sendProgressBarUpdate(this, 0, inducer.power);
			}
		}
		lastPower = inducer.power;
	}

	


	@Override
	public ItemStack transferStackInSlot(EntityPlayer p, int i) {
		ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack()){
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(i >= 0 && i < 27){
            	if (!mergeItemStack(itemstack1, 27, 63, true))
                {
                    return null;
                }
            }else if(i >= 27 && i < 63){
            	if(inducer.isValidItem(itemstack1)){
            		for(int a = 0; a < 27; a++){
                		Slot slot2 = (Slot)inventorySlots.get(a);
                		if(!slot2.getHasStack()){
                			ItemStack set = itemstack1.copy();
                			set.stackSize = 1;
                			slot2.putStack(set);
                			slot2.onSlotChanged();
                			itemstack1.stackSize--;
                			if(itemstack1.stackSize < 1){
                				slot.putStack(null);
                				return null;
                			}
                		}
                	}
            	}
            	
            }
            
            
            if (itemstack1.stackSize == 0)
            {
                slot.putStack(null);
            }
            else
            {
                slot.onSlotChanged();
            }
            if (itemstack1.stackSize != itemstack.stackSize)
            {
                slot.onPickupFromSlot(p, itemstack1);
            }
            else
            {
                return null;
            }
        }
        return itemstack;
	}




	@Override
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0){
			inducer.power = par2;
		}
		
	}



	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return true;
	}

}
