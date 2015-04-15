package net.jamezo97.clonecraft.gui.container;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.InventoryClone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;

public class ContainerTransferPlayerItems extends Container
{
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 2, 2);
    public IInventory craftResult = new InventoryCraftResult();
    
    EntityPlayer thePlayer;
    
    public ContainerTransferPlayerItems(EntityPlayer mainPlayer, EntityClone other)
    {
    	thePlayer = mainPlayer;
    	InventoryPlayer inventoryplayer = mainPlayer.inventory;
    	InventoryClone inventoryplayer1 = other.inventory;
    	this.addSlotToContainer(new SlotCrafting(mainPlayer, this.craftMatrix, this.craftResult, 0, 108, 99));
        for (int i = 0; i < 2; ++i)
        {
            for (int j = 0; j < 2; ++j)
            {
                this.addSlotToContainer(new Slot(this.craftMatrix, j + i * 2, 52 + j * 18, 89 + i * 18));
            }
        }
        
        
        for (int j = 0; j < 2; j++)
        {
        	for(int k = 0; k < 2; k++){
                addSlotToContainer(new SlotArmorOtherPlayer(this, inventoryplayer, inventoryplayer.getSizeInventory() - 1 - k-(j*2), 8+(18*j), 89 + k * 18, (j*2)+k));	
        	}
        }
        for (int k = 0; k < 3; k++)
        {
            for (int k1 = 0; k1 < 9; k1++)
            {
                addSlotToContainer(new Slot(inventoryplayer, k1 + (k + 1) * 9, 8 + k1 * 18, 9 + k * 18));
            }
        }
        for (int l = 0; l < 9; l++)
        {
            addSlotToContainer(new Slot(inventoryplayer, l, 8 + l * 18, 67));
        }
        //------------------
        for (int j = 0; j < 2; j++)
        {
        	for(int k = 0; k < 2; k++){
                addSlotToContainer(new SlotArmorOtherPlayer(this, inventoryplayer1, inventoryplayer1.getSizeInventory() - 1 - k-(j*2), 134+(18*j), 89 + k * 18, (j*2)+k));	
        	}
        }
        for (int k = 0; k < 3; k++)
        {
            for (int k1 = 0; k1 < 9; k1++)
            {
                addSlotToContainer(new Slot(inventoryplayer1, k1 + (k + 1) * 9, 8 + k1 * 18, 129 + k * 18));
            }
        }
        for (int l = 0; l < 9; l++)
        {
            addSlotToContainer(new Slot(inventoryplayer1, l, 8 + l * 18, 187));
        }
        
        

        onCraftMatrixChanged(craftMatrix);
    }
    
    public void onCraftMatrixChanged(IInventory par1IInventory)
    {
        this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix, this.thePlayer.worldObj));
    }

    /**
     * Called when the container is closed.
     */
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);

        for (int i = 0; i < 4; ++i)
        {
            ItemStack itemstack = this.craftMatrix.getStackInSlotOnClosing(i);

            if (itemstack != null)
            {
                par1EntityPlayer.dropPlayerItemWithRandomChoice(itemstack, false);
            }
        }

        this.craftResult.setInventorySlotContents(0, (ItemStack)null);
    }
    
	protected void retrySlotClick(int par1, int par2, boolean par3, EntityPlayer par4EntityPlayer) {}

    public boolean canInteractWith(EntityPlayer entityplayer)
    {
        return true;
    }

	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)inventorySlots.get(i);
        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();
            if (i == 0)
            {
                if (!mergeItemStack(itemstack1, 9, 45, true))
                {
                    return null;
                }
            }else if(i >= 9 && i <= 44){
            	if (!mergeItemStack(itemstack1, 49, 85, false))
                {
                    return null;
                }
            }else if(i >= 49 && i <= 84){
            	if (!mergeItemStack(itemstack1, 9, 45, false))
                {
                    return null;
                }
            }else if(i >= 5 && i <= 8){
            	if (!mergeItemStack(itemstack1, 9, 45, false))
                {
                    return null;
                }
            }else if(i >= 45 && i <= 48){
            	if (!mergeItemStack(itemstack1, 49, 85, false))
                {
                    return null;
                }
            }else if (!mergeItemStack(itemstack1, 9, 45, false))
            {
                return null;
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
	
	public boolean func_94530_a(ItemStack par1ItemStack, Slot par2Slot)
    {
        return par2Slot.inventory != this.craftResult && super.func_94530_a(par1ItemStack, par2Slot);
    }
}
