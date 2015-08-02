package net.jamezo97.clonecraft.clone;

import java.util.Random;
import java.util.concurrent.Callable;

import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ReportedException;

public class InventoryClone extends InventoryPlayer
{

	EntityClone clone;
	
	public InventoryClone(EntityClone clone){
		super(null);
		this.clone = clone;
	}
	
	/**
	 * Checks if the ItemStack can fit completely in the inventory
	 * @param stack ItemStack to try and fit
	 * @return True if the stack fits, false otherwise
	 */
	public boolean canFullyFit(ItemStack stack)
	{
		if(stack == null){return false;}
		
		int removed = 0;
		ItemStack toFit = stack.copy();
		ItemStack slot;
		int emptySlotIndex = -1;
		
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(toFit.stackSize == 0){
				break;
			}
			slot = mainInventory[a];
			if(slot != null)
			{
				if(areStacksSame(toFit, slot))
				{
					int remove = slot.getMaxStackSize() - slot.stackSize;
					if(remove > toFit.stackSize){
						remove = toFit.stackSize;
					}
					toFit.stackSize -= remove;
					removed += remove;
				}
			}
			else if(emptySlotIndex == -1)
			{
				emptySlotIndex = a;
			}
		}
		if(toFit.stackSize > 0 && emptySlotIndex != -1)
		{
			removed += toFit.stackSize;
		}
		return removed == stack.stackSize;
	}
	
	/**
	 * Tries to fit as much of the stack as possible into the inventory
	 * @param stack The ItemStack to try and fit
	 * @return The amount removed from the input stack
	 */
	public int tryFitInInventory(ItemStack toFit)
	{
		if(toFit == null){return 0;}
		
		int removed = 0;
		
		ItemStack slot;
		int emptySlotIndex = -1;
		
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(toFit.stackSize == 0)
			{
				break;
			}
			
			slot = mainInventory[a];
			
			if(slot != null)
			{
				if(areStacksSame(toFit, slot))
				{
					int remove = slot.getMaxStackSize() - slot.stackSize;
					
					slot.animationsToGo = 5;
					
					if(remove > toFit.stackSize)
					{
						remove = toFit.stackSize;
					}
					
					slot.stackSize += remove;
					toFit.stackSize -= remove;
					removed += remove;
				}
			}
			else if(emptySlotIndex == -1)
			{
				emptySlotIndex = a;
			}
		}
		
		if(toFit.stackSize > 0 && emptySlotIndex != -1)
		{
			mainInventory[emptySlotIndex] = toFit.copy();
			removed += toFit.stackSize;
			toFit.stackSize = 0;
		}
		return removed;
	}
	
	
	
	/**
	 * 
	 * @param s1 ItemStack 1
	 * @param s2 ItemStack 2
	 * @return Whether the two item stacks are the same in everything except quantity
	 */
	public boolean areStacksSame(ItemStack s1, ItemStack s2){
		return s1 == null && s2 == null ? true:s1!=null && s2!=null?(s1.getItem() == s2.getItem() && s1.getItemDamage() == s2.getItemDamage() && ItemStack.areItemStackTagsEqual(s1, s2)):false;
	}
	
	
	
	
	
	public void damageArmour(float dam)
    {
		dam /= 4.0F;

        if (dam < 1.0F)
        {
        	dam = 1.0F;
        }

        for (int i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor)
            {
                this.armorInventory[i].damageItem((int)dam, this.clone);

                if (this.armorInventory[i].stackSize == 0)
                {
                    this.armorInventory[i] = null;
                }
            }
        }
    }
	
	
	
	
	
	
	
	
	
	
	//Basic Inventory Stuff
	
	/**
     * Returns the number of slots in the inventory.
     */
	@Override
    public int getSizeInventory()
    {
        return 40;
    }
    
    /** FROM ITEMARMOR:  Stores the armor type: 0 is helmet, 1 is plate, 2 is legs and 3 is boots */
    /**
     * 
     * @param slot 3=Head, 2=Chest, 1=Legs, 0=Feet
     * @return The ItemStack in the armour slot
     */
	@Override
    public ItemStack armorItemInSlot(int slot){
    	return armorInventory[slot];
    }

    /**
     * Returns the stack in slot i
     */
	@Override
    public ItemStack getStackInSlot(int slot)
    {
    	if(slot >= 36){
    		return this.armorInventory[slot-36];
    	}
        return this.mainInventory[slot];
    }
    
	public void dropAllItemsMe() {
		Random rand = new Random();
		for (int i1 = 0; i1 < getSizeInventory(); ++i1) {
			ItemStack mainInventorytack = getStackInSlot(i1);
			if (mainInventorytack != null) {
				float f = rand.nextFloat() * 0.8F + 0.1F;
				float f1 = rand.nextFloat() * 0.8F + 0.1F;
				float f2 = rand.nextFloat() * 0.8F + 0.1F;

				while (mainInventorytack.stackSize > 0) {
					int j1 = rand.nextInt(21) + 10;

					if (j1 > mainInventorytack.stackSize) {
						j1 = mainInventorytack.stackSize;
					}

					mainInventorytack.stackSize -= j1;
					EntityItem entityitem = new EntityItem(this.clone.worldObj,
							(double) ((float) this.clone.posX + f),
							(double) ((float) this.clone.posY + f1),
							(double) ((float) this.clone.posZ + f2), new ItemStack(
									mainInventorytack.getItem(), j1,
									mainInventorytack.getItemDamage()));

					if (mainInventorytack.hasTagCompound()) {
						entityitem.getEntityItem().setTagCompound(
								(NBTTagCompound) mainInventorytack.getTagCompound()
										.copy());
					}

					float f3 = 0.05F;
					entityitem.delayBeforeCanPickup = 20;
					entityitem.motionX = (double) ((float) rand.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) rand.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) rand.nextGaussian() * f3);
					this.setInventorySlotContents(i1, null);
					this.clone.worldObj.spawnEntityInWorld(entityitem);
				}
			}
		}
	}

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of mainInventory and returns them in a
     * new stack.
     */
	@Override
    public ItemStack decrStackSize(int slot, int count)
    {
    	ItemStack[] stacks = mainInventory;
    	if(slot >= 36){
    		slot -= 36;
    		stacks = armorInventory;
    	}
    	
        if (stacks[slot] != null)
        {
            ItemStack mainInventorytack;

            if (stacks[slot].stackSize <= count)
            {
                mainInventorytack = stacks[slot];
                stacks[slot] = null;
                return mainInventorytack;
            }
            else
            {
                mainInventorytack = stacks[slot].splitStack(count);

                if (stacks[slot].stackSize == 0)
                {
                    stacks[slot] = null;
                }

                return mainInventorytack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
	@Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
    	ItemStack[] stacks = mainInventory;
    	if(slot >= 36){
    		slot -= 36;
    		stacks = armorInventory;
    	}
        if (stacks[slot] != null)
        {
            ItemStack mainInventorytack = stacks[slot];
            stacks[slot] = null;
            return mainInventorytack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
	@Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
    	ItemStack[] stacks = mainInventory;
    	if(slot >= 36){
    		slot -= 36;
    		stacks = armorInventory;
    	}
        stacks[slot] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
        	stack.stackSize = this.getInventoryStackLimit();
        }
    }

	public void setArmour(int i, ItemStack stack) {
		armorInventory[i] = stack;
	}
    
    /**
     * Returns the name of the inventory
     */
	@Override
    public String getInventoryName()
    {
        return "inventory.clone";
    }


    /**
     * Returns the maximum stack size for a inventory slot.
     */
	@Override
    public int getInventoryStackLimit()
    {
        return 64;
    }
    
    @Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}

	@Override
	public void markDirty() {}

	
	
	
	
	
	
	
	
	
	
	
	
	@Override
    public void decrementAnimations()
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null)
            {
                this.mainInventory[i].updateAnimation(this.player.worldObj, this.player, i, currentItem == i);
            }
        }

        for (int i = 0; i < armorInventory.length; i++)
        {
            if (armorInventory[i] != null)
            {
                armorInventory[i].getItem().onArmorTick(player.worldObj, player, armorInventory[i]);
            }
        }
    }
    
	@Override
    public boolean addItemStackToInventory(final ItemStack p_70441_1_)
    {
        if (p_70441_1_ != null && p_70441_1_.stackSize != 0 && p_70441_1_.getItem() != null)
        {
            try
            {
                int i;

                if (p_70441_1_.isItemDamaged())
                {
                    i = this.getFirstEmptyStack();

                    if (i >= 0)
                    {
                        this.mainInventory[i] = ItemStack.copyItemStack(p_70441_1_);
                        this.mainInventory[i].animationsToGo = 5;
                        p_70441_1_.stackSize = 0;
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    do
                    {
                        i = p_70441_1_.stackSize;
                        p_70441_1_.stackSize = this.storePartialItemStack(p_70441_1_);
                    }
                    while (p_70441_1_.stackSize > 0 && p_70441_1_.stackSize < i);

                    return p_70441_1_.stackSize < i;
                }
            }
            catch (Throwable throwable)
            {
                CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Adding item to inventory");
                CrashReportCategory crashreportcategory = crashreport.makeCategory("Item being added");
                crashreportcategory.addCrashSection("Item ID", Integer.valueOf(Item.getIdFromItem(p_70441_1_.getItem())));
                crashreportcategory.addCrashSection("Item data", Integer.valueOf(p_70441_1_.getItemDamage()));
                crashreportcategory.addCrashSectionCallable("Item name", new Callable()
                {
                    private static final String __OBFID = "CL_00001710";
                    public String call()
                    {
                        return p_70441_1_.getDisplayName();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
        else
        {
            return false;
        }
    }
	
    private int storeItemStack(ItemStack p_70432_1_)
    {
        for (int i = 0; i < this.mainInventory.length; ++i)
        {
            if (this.mainInventory[i] != null && this.mainInventory[i].getItem() == p_70432_1_.getItem() && this.mainInventory[i].isStackable() && this.mainInventory[i].stackSize < this.mainInventory[i].getMaxStackSize() && this.mainInventory[i].stackSize < this.getInventoryStackLimit() && (!this.mainInventory[i].getHasSubtypes() || this.mainInventory[i].getItemDamage() == p_70432_1_.getItemDamage()) && ItemStack.areItemStackTagsEqual(this.mainInventory[i], p_70432_1_))
            {
                return i;
            }
        }

        return -1;
    }
    
    private int storePartialItemStack(ItemStack p_70452_1_)
    {
        Item item = p_70452_1_.getItem();
        int i = p_70452_1_.stackSize;
        int j;

        if (p_70452_1_.getMaxStackSize() == 1)
        {
            j = this.getFirstEmptyStack();

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.mainInventory[j] == null)
                {
                    this.mainInventory[j] = ItemStack.copyItemStack(p_70452_1_);
                }

                return 0;
            }
        }
        else
        {
            j = this.storeItemStack(p_70452_1_);

            if (j < 0)
            {
                j = this.getFirstEmptyStack();
            }

            if (j < 0)
            {
                return i;
            }
            else
            {
                if (this.mainInventory[j] == null)
                {
                    this.mainInventory[j] = new ItemStack(item, 0, p_70452_1_.getItemDamage());

                    if (p_70452_1_.hasTagCompound())
                    {
                        this.mainInventory[j].setTagCompound((NBTTagCompound)p_70452_1_.getTagCompound().copy());
                    }
                }

                int k = i;

                if (i > this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize)
                {
                    k = this.mainInventory[j].getMaxStackSize() - this.mainInventory[j].stackSize;
                }

                if (k > this.getInventoryStackLimit() - this.mainInventory[j].stackSize)
                {
                    k = this.getInventoryStackLimit() - this.mainInventory[j].stackSize;
                }

                if (k == 0)
                {
                    return i;
                }
                else
                {
                    i -= k;
                    this.mainInventory[j].stackSize += k;
                    this.mainInventory[j].animationsToGo = 5;
                    return i;
                }
            }
        }
    }


    @Override
    public void damageArmor(float p_70449_1_)
    {
        p_70449_1_ /= 4.0F;

        if (p_70449_1_ < 1.0F)
        {
            p_70449_1_ = 1.0F;
        }

        for (int i = 0; i < this.armorInventory.length; ++i)
        {
            if (this.armorInventory[i] != null && this.armorInventory[i].getItem() instanceof ItemArmor)
            {
                this.armorInventory[i].damageItem((int)p_70449_1_, this.player);

                if (this.armorInventory[i].stackSize == 0)
                {
                    this.armorInventory[i] = null;
                }
            }
        }
    }

    @Override
    public void dropAllItems()
    {
    	this.dropAllItemsMe();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return this.clone.isDead ? false : p_70300_1_.getDistanceSqToEntity(this.clone) <= 64.0D;
    }

	public int putStackOnHotbar(int slotFrom)
	{
		if(slotFrom < 9)
		{
			return slotFrom;
		}
		
		for(int a = 8; a >= 0; a--)
		{
			if(this.mainInventory[a] == null)
			{
				this.mainInventory[a] = this.mainInventory[slotFrom];
				this.mainInventory[slotFrom] = null;
				return a;
			}
		}
		
		ItemStack temp;
		temp = mainInventory[8];
		mainInventory[8] = mainInventory[slotFrom];
		mainInventory[slotFrom] = temp;
		return 8;
	}

	public boolean removeItemStackFromInventory(ItemStack stack) {
		if(stack == null)
		{
			return false;
		}
		ItemStack copy = stack.copy();
		
	
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(mainInventory[a] != null)
			{
				if(mainInventory[a].getItem() == copy.getItem() && ItemStack.areItemStackTagsEqual(copy, mainInventory[a]))
				{
					
					int remove = mainInventory[a].stackSize;
					
					if(remove > copy.stackSize){
						remove = copy.stackSize;
					}

					copy.stackSize -= remove;
					mainInventory[a].stackSize -= remove;
					
					if(mainInventory[a].stackSize == 0){
						mainInventory[a] = null;
					}
					
					if(copy.stackSize <= 0)
					{
						return true;
					}
				}
			}
		}
		
		return copy.stackSize <= 0;
	}
	
	public boolean trySelectEmptySlot() {
		//Try select an already empty slot.
		for(int a = 0; a < 9; a++)
		{
			if(mainInventory[a] == null)
			{
				this.currentItem = a;
				return true;
			}
		}
		
		//No empty slots found. Let's try swapping something out.
		this.currentItem = 0;
		
		for(int a = 9; a < 36; a++)
		{
			if(mainInventory[a] == null)
			{
				mainInventory[a] = mainInventory[0];
				mainInventory[0] = null;
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Gets the slot index for the specified item
	 * @param milkBucket
	 * @return
	 */
	public int getSlotForItem(Item item)
	{
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(mainInventory[a] != null && mainInventory[a].getItem() == item)
			{
				return a;
			}
		}
		return -1;
	}
	
	/*public int getSlotForItemStack(ItemStack stack)
	{
		
	}*/
	
	
	/**
	 * Checks if the given ItemStack can be found, or made from the current inventory contents (and made as in, stack similar items together)
	 * @param stack The stack to check if available.
	 * @return true if the ItemStack could be made from the current inventory (i.e. 6 Bread. 3 in slot 6, 1 in slot 2, and 2 in slot 19. 6 Bread is available.
	 */
	public boolean isStackAvailable(ItemStack stack)
	{
		if(stack == null)
		{
			return false;
		}
		ItemStack copy = stack.copy();
		
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(mainInventory[a] != null)
			{
				if(mainInventory[a].getItem() == copy.getItem()
						&& mainInventory[a].getItemDamage() == copy.getItemDamage()
						&& ItemStack.areItemStackTagsEqual(copy,  mainInventory[a]))
				{
					copy.stackSize -= mainInventory[a].stackSize;
					
					if(copy.stackSize <= 0)
					{
						return true;
					}
				}
			}
		}
		
		return copy.stackSize <= 0;
	}
	
	public int getFitCount(ItemStack stack)
	{
		if(stack == null)
		{
			return -1;
		}
		ItemStack copy = stack.copy();
		
		int fitCount = 0;
		
		int emptyCount = 0;
		
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(mainInventory[a] != null)
			{
				if(mainInventory[a].getItem() == copy.getItem() && mainInventory[a].getItemDamage() == copy.getItemDamage() && ItemStack.areItemStackTagsEqual(copy,  mainInventory[a]))
				{
					int maxAdd = (mainInventory[a].getMaxStackSize()-mainInventory[a].stackSize);
					
					if(maxAdd > copy.stackSize)
					{
						maxAdd = copy.stackSize;
					}
					
					copy.stackSize -= maxAdd;
					fitCount += maxAdd;
				}
			}
			else
			{
				emptyCount+=this.getInventoryStackLimit();
			}
		}
		
		return fitCount+emptyCount;
	}
	
	public boolean hasEmptySlot()
	{
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(mainInventory[a] == null)
			{
				return true;
			}
		}
		return false;
	}
	
	public int getTypeCount(TypeCheck type)
	{
		int count = 0;
		for(int a = 0; a < mainInventory.length; a++)
		{
			if(mainInventory[a] != null)
			{
				if(type.isType(mainInventory[a]))
				{
					count += mainInventory[a].stackSize;
				}
			}
		}
		return count;
	}
	
	public int containsCount(ItemStack find)
	{
		if(find == null){return 0;}
		
		int found = 0;
		
		for(int a = 0; a < mainInventory.length; a++)
		{
			ItemStack stack = mainInventory[a];
			
			if(stack != null)
			{
//				System.out.println("Check " + find + ", " + stack);
				
			}
			
			if(this.areStacksSame(find, stack))
			{
				found += stack.stackSize;
			}
		}
		return found;
	}
	
	public boolean consume(ItemStack toConsume)
	{
		if(toConsume == null){return false;}
		
		int count = toConsume.stackSize;
		
		for(int a = 0; a < mainInventory.length; a++)
		{
			ItemStack stack = mainInventory[a];
			
			if(this.areStacksSame(toConsume, stack))
			{
				int remove = Math.min(count, stack.stackSize);
				
				stack.stackSize -= remove;
				
				if(stack.stackSize == 0)
				{
					mainInventory[a] = null;
				}
				
				count -= remove;
			}
		}
		return count == 0;
	}
	
	
	
	public static interface TypeCheck{
		/**
		 * 
		 * @param stack A Non-Null ItemStack
		 * @return Whether the stack is the specified type
		 */
		public boolean isType(ItemStack stack);
		
	}
	
	public static class TypeCheckItem implements TypeCheck{
		
		public final Item theItem;
		
		public TypeCheckItem(Item item){
			this.theItem = item;
		}

		@Override
		public boolean isType(ItemStack stack) {
			return stack.getItem() == theItem;
		}
		
	}
	
	public static final TypeCheck CHECK_FOOD = new TypeCheck(){
		@Override
		public boolean isType(ItemStack stack) {
			return stack.getItem().getItemUseAction(stack) == EnumAction.eat || stack.getItem() instanceof ItemFood;
		}
	};
	
	public static final TypeCheck CHECK_ARROW = new TypeCheck(){
		@Override
		public boolean isType(ItemStack stack) {
			return stack.getItem() == Items.arrow;
		}
	};
	
	public static final TypeCheck[] CHECK_WOOD = new TypeCheck[]{
		new TypeCheckItem(Items.wooden_axe),
		new TypeCheckItem(Items.wooden_hoe),
		new TypeCheckItem(Items.wooden_pickaxe),
		new TypeCheckItem(Items.wooden_shovel),
		new TypeCheckItem(Items.wooden_sword)
	};
	public static final TypeCheck[] CHECK_STONE = new TypeCheck[]{
		new TypeCheckItem(Items.stone_axe),
		new TypeCheckItem(Items.stone_hoe),
		new TypeCheckItem(Items.stone_pickaxe),
		new TypeCheckItem(Items.stone_shovel),
		new TypeCheckItem(Items.stone_sword)
	};
	public static final TypeCheck[] CHECK_IRON = new TypeCheck[]{
		new TypeCheckItem(Items.iron_axe),
		new TypeCheckItem(Items.iron_hoe),
		new TypeCheckItem(Items.iron_pickaxe),
		new TypeCheckItem(Items.iron_shovel),
		new TypeCheckItem(Items.iron_sword)
	};
	public static final TypeCheck[] CHECK_GOLD = new TypeCheck[]{
		new TypeCheckItem(Items.golden_axe),
		new TypeCheckItem(Items.golden_hoe),
		new TypeCheckItem(Items.golden_pickaxe),
		new TypeCheckItem(Items.golden_shovel),
		new TypeCheckItem(Items.golden_sword)
	};
	public static final TypeCheck[] CHECK_DIAMOND = new TypeCheck[]{
		new TypeCheckItem(Items.diamond_axe),
		new TypeCheckItem(Items.diamond_hoe),
		new TypeCheckItem(Items.diamond_pickaxe),
		new TypeCheckItem(Items.diamond_shovel),
		new TypeCheckItem(Items.diamond_sword)
	};

	



	
}
