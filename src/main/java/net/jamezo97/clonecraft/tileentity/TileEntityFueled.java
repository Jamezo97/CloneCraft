package net.jamezo97.clonecraft.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityFurnace;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class TileEntityFueled extends TileEntityBase{


	public int burnTimeLeft;
    public int currentItemBurnTime;
    public int cookTime;
    
    private int lastBurnTimeLeft;
    private int lastCurrentItemBurnTime;
    private int lastCookTime;
    
    public int notifiers = 0;
    
    static int NOTIFY_FUELGONE = 1;
    static int NOTIFY_FUELSTART = 2;
    static int NOTIFY_COOKDONE = 4;
    static int NOTIFY_COOKSTART = 8;
    
	
	public TileEntityFueled(int size){
		super(size);
	}
	
	private boolean notify(int type){
		return (notifiers & type) != 0;
	}

	/**
	 * Name of the TileEntity, i.e. Furnace, Chest, Centrifuge etc
	 */
	public abstract String getTileEntityName();
	
	/**
	 * Slot index of the fuel
	 * @return
	 */
	public abstract int getFuelSlot();

	/**
	 * Time it takes to complete the action, in ticks.
	 * @return
	 */
	public abstract int getTimeToComplete();
	
	/**
	 * Automatically start burning fuel and performing action when all requirements are met
	 * @return Whether the TE can start burning fuel
	 */
	public boolean canStart(){
		return true;
	}

	
    public void readFromNBT(NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        this.burnTimeLeft = nbt.getShort("burnTimeLeft");
        this.cookTime = nbt.getShort("CookTime");
        this.currentItemBurnTime = nbt.getShort("CurrentItemBurn");//TileEntityFurnace.getItemburnTimeLeft(this.items[1]);
    }

    public void writeToNBT(NBTTagCompound nbt)
    {
        super.writeToNBT(nbt);
        nbt.setShort("burnTimeLeft", (short)this.burnTimeLeft);
        nbt.setShort("CookTime", (short)this.cookTime);
        nbt.setShort("CurrentItemBurn", (short)this.currentItemBurnTime);
    }

	
    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    @SideOnly(Side.CLIENT)
    public int getProgressScaled(int scale)
    {
        return this.cookTime * scale / getTimeToComplete();
    }

    /**
     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     */
    @SideOnly(Side.CLIENT)
    public int getburnTimeLeftRemainingScaled(int scale)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = getTimeToComplete();
        }

        return this.burnTimeLeft * scale / this.currentItemBurnTime;
    }

    /**
     * Furnace isBurning
     */
    public boolean isBurning()
    {
        return this.burnTimeLeft > 0;
    }

    public void updateEntity()
    {
    	this.lastBurnTimeLeft = burnTimeLeft;
    	this.lastCurrentItemBurnTime = currentItemBurnTime;
    	this.lastCookTime = cookTime;
    	
    	if (this.burnTimeLeft > 0){
    		this.burnTimeLeft--;
    	}
    	if (!this.worldObj.isRemote)
        {
    		//If no fuel is burning, and there is something new to burn, and there is an action that can be performed, and it's allowed to start cooking(or it already is)
    		boolean flag = this.canPerformAction();
    		boolean flag1 = this.canStart();
    		if(!this.isBurning() && this.canBurnNewItem() && flag && (this.cookTime > 0 || flag1))
    		{
    			int fuelSlot = getFuelSlot();
    			burnTimeLeft = TileEntityFurnace.getItemBurnTime(this.items[fuelSlot]);
    			currentItemBurnTime = burnTimeLeft;
    			this.items[fuelSlot].stackSize--;
    			if (this.items[fuelSlot].stackSize == 0)
                {
                    this.items[fuelSlot] = items[fuelSlot].getItem().getContainerItem(items[fuelSlot]);
                }
    		}else if(!this.isBurning() && currentItemBurnTime != 0){
    			currentItemBurnTime = 0;
    		}
    		if(this.isBurning() && flag && (this.cookTime > 0 || flag1)){
    			this.cookTime++;
        		if(this.cookTime >= this.getTimeToComplete()){
        			this.cookTime -= this.getTimeToComplete();
        			this.performAction();
        		}
    		}else{
    			this.cookTime = 0;
    		}
    		flag = this.canPerformAction();
    		flag1 = this.canStart();
    		if(this.notify(NOTIFY_FUELGONE)){
        		if(burnTimeLeft == 0){
        			this.sendClientInfo(2, 0);
        		}
        	}
    		if(this.notify(NOTIFY_FUELSTART)){
        		if(burnTimeLeft > 0 && lastBurnTimeLeft == 0){
        			this.sendClientInfo(2, burnTimeLeft);
        		}
        	}
    		if(this.notify(NOTIFY_COOKDONE)){
        		if(lastCookTime > 0 && (cookTime == 0 && (!this.isBurning() || !flag || !(this.cookTime > 0 || flag1)))){
        			this.sendClientInfo(1, 0);
        		}
        	}
    		if(this.notify(NOTIFY_COOKSTART)){
        		if(lastCookTime == 0 && cookTime > 0){
        			this.sendClientInfo(1, cookTime);
        		}
        	}
        }
    	
    	
    	
    	
    }
    
    public boolean receiveClientEvent(int key, int value){
    	if(key == 1){
    		this.cookTime = value;
//    		System.out.println("Set cookTime: " + value);
    	}else if(key == 2){
    		this.burnTimeLeft = value;
//    		System.out.println("Set burnTimeLeft: " + value);
    	}else{
    		return super.receiveClientEvent(key, value);
    	}
		return true;
    }
    
    public boolean canBurnNewItem(){
    	return items[getFuelSlot()] != null && TileEntityFurnace.isItemFuel(items[getFuelSlot()]);
    }
    
    /**
     * Checks if the action can be performed (not including whether fuel is available to burn
     * @return
     */
    public abstract boolean canPerformAction();
    
    /**
     * Perform the action!
     */
    public abstract void performAction();
	
}
