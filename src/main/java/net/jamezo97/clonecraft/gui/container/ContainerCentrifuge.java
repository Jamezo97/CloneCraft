package net.jamezo97.clonecraft.gui.container;

import java.util.Iterator;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerCentrifuge extends Container{
	
	public TileEntityCentrifuge tE;
	
	public ContainerCentrifuge(EntityPlayer player, TileEntityCentrifuge te) {
		this.tE = te;
		IInventory playerInven = player.inventory;
		tE.openInventory();
		addSlotToContainer(new SlotCentrifuge(tE, 1, 116, 44, player));
		addSlotToContainer(new SlotCentrifuge(tE, 2, 105, 69, player));
		addSlotToContainer(new SlotCentrifuge(tE, 3, 80, 80, player));
		addSlotToContainer(new SlotCentrifuge(tE, 4, 55, 69, player));
		addSlotToContainer(new SlotCentrifuge(tE, 5, 44, 44, player));
		addSlotToContainer(new SlotCentrifuge(tE, 6, 55, 19, player));
		addSlotToContainer(new SlotCentrifuge(tE, 7, 80, 8, player));
		addSlotToContainer(new SlotCentrifuge(tE, 8, 105, 19, player));

		addSlotToContainer(new Slot(tE, 0, 80, 44));
        for (int j = 0; j < 9; j++)
        {
        	addSlotToContainer(new Slot(playerInven, j, 8 + j * 18, 172));
        }
        for (int i = 0; i < 3; i++)
        {
            for (int k = 0; k < 9; k++)
            {
            	addSlotToContainer(new Slot(playerInven, k + i * 9 + 9, 8 + k * 18, 114 + i * 18));
            }
        }
	}


	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	private int lastCurrentItemBurnTime;
	private int lastFurnaceBurnTime;
	private int lastTimeSpun;
	
	@Override
	public void addCraftingToCrafters(ICrafting par1iCrafting) {
		super.addCraftingToCrafters(par1iCrafting);
		par1iCrafting.sendProgressBarUpdate(this, 0, tE.currentItemBurnTime);
		par1iCrafting.sendProgressBarUpdate(this, 1, tE.burnTimeLeft);
		par1iCrafting.sendProgressBarUpdate(this, 2, tE.cookTime);
	}

	
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		Iterator var1 = crafters.iterator();
		while(var1.hasNext()){
			ICrafting crafting = (ICrafting)var1.next();
			if(tE.currentItemBurnTime != lastCurrentItemBurnTime){
				crafting.sendProgressBarUpdate(this, 0, tE.currentItemBurnTime);
			}
			if(tE.burnTimeLeft != lastFurnaceBurnTime){
				crafting.sendProgressBarUpdate(this, 1, tE.burnTimeLeft);
			}
			if(tE.cookTime != lastTimeSpun){
				crafting.sendProgressBarUpdate(this, 2, tE.cookTime);
			}
		}
		
		lastCurrentItemBurnTime = tE.currentItemBurnTime;
		lastFurnaceBurnTime = tE.burnTimeLeft;
		lastTimeSpun = tE.cookTime;
	}

	@Override
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0){
			tE.currentItemBurnTime = par2;
		}
		if(par1 == 1){
			tE.burnTimeLeft = par2;
		}
		if(par1 == 2){
			tE.cookTime = par2;
		}
	}
	
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if(i >= 0 && i < 9){
            	if (!mergeItemStack(itemstack1, 9, 45, true))
                {
                    return null;
                }
            }/*else if(i == 4){
            	if (!mergeItemStack(itemstack1, 5, 45, true))
                {
                    return null;
                }
            }*/else if(i >= 9 && i < 46){
            	if(itemstack1.getItem() == CloneCraft.INSTANCE.itemTestTube && itemstack1.getItemDamage() == 1){
//            		if (tE.cookTime == 0){
            			for(int a = 0; a < 8; a++){
            				if(itemstack1.stackSize > 0 && getSlot(a).getStack() == null){
            					ItemStack stack = itemstack1.copy();
            					stack.stackSize = 1;
            					itemstack1.stackSize--;
            					getSlot(a).putStack(stack);
            				}else if(itemstack1.stackSize == 0){
            					break;
            				}
            			} 
//                    }
            	}else if(TileEntityFurnace.getItemBurnTime(itemstack1)>0){
            		if (!mergeItemStack(itemstack1, 8, 9, true))
                    {
                        return null;
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
                slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
            }
            else
            {
                return null;
            }
        }
        return itemstack;
    }
	
	public void onContainerClosed(EntityPlayer p_75134_1_)
    {
        super.onContainerClosed(p_75134_1_);
        this.tE.closeInventory();
    }
	
}
