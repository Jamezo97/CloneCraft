package net.jamezo97.clonecraft.tileentity;

import java.util.Random;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;

public abstract class CCTileEntityBase extends TileEntity implements IInventory
{

	ItemStack[] items;

	public CCTileEntityBase(int size)
	{
		items = new ItemStack[size];

	}

	public abstract String getTileEntityName();

	/**
	 * Returns the number of slots in the inventory.
	 */
	@Override
	public int getSizeInventory()
	{
		return this.items.length;
	}

	/**
	 * Returns the stack in slot i
	 */
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return this.items[slot];
	}

	public void dropAllItems()
	{
		Random rand = new Random();
		for (int i1 = 0; i1 < getSizeInventory(); ++i1)
		{
			ItemStack itemstack = getStackInSlot(i1);
			if (itemstack != null)
			{
				float f = rand.nextFloat() * 0.8F + 0.1F;
				float f1 = rand.nextFloat() * 0.8F + 0.1F;
				float f2 = rand.nextFloat() * 0.8F + 0.1F;

				while (itemstack.stackSize > 0)
				{
					int j1 = rand.nextInt(21) + 10;

					if (j1 > itemstack.stackSize)
					{
						j1 = itemstack.stackSize;
					}

					itemstack.stackSize -= j1;
					EntityItem entityitem = new EntityItem(this.getWorldObj(), (double) ((float) this.xCoord + f), (double) ((float) this.yCoord + f1),
							(double) ((float) this.zCoord + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

					if (itemstack.hasTagCompound())
					{
						entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
					}

					float f3 = 0.05F;
					entityitem.motionX = (double) ((float) rand.nextGaussian() * f3);
					entityitem.motionY = (double) ((float) rand.nextGaussian() * f3 + 0.2F);
					entityitem.motionZ = (double) ((float) rand.nextGaussian() * f3);
					this.getWorldObj().spawnEntityInWorld(entityitem);
				}
			}
		}
	}

	/**
	 * Removes from an inventory slot (first arg) up to a specified number
	 * (second arg) of items and returns them in a new stack.
	 */
	@Override
	public ItemStack decrStackSize(int slot, int count)
	{
		if (this.items[slot] != null)
		{
			ItemStack itemstack;

			if (this.items[slot].stackSize <= count)
			{
				itemstack = this.items[slot];
				this.items[slot] = null;
				return itemstack;
			}
			else
			{
				itemstack = this.items[slot].splitStack(count);

				if (this.items[slot].stackSize == 0)
				{
					this.items[slot] = null;
				}

				return itemstack;
			}
		}
		else
		{
			return null;
		}
	}

	/**
	 * When some containers are closed they call this on each slot, then drop
	 * whatever it returns as an EntityItem - like when you close a workbench
	 * GUI.
	 */
	@Override
	public ItemStack getStackInSlotOnClosing(int slot)
	{
		if (this.items[slot] != null)
		{
			ItemStack itemstack = this.items[slot];
			this.items[slot] = null;
			return itemstack;
		}
		else
		{
			return null;
		}
	}

	/**
	 * Sets the given item stack to the specified slot in the inventory (can be
	 * crafting or armor sections).
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		this.items[slot] = stack;

		if (stack != null && stack.stackSize > this.getInventoryStackLimit())
		{
			stack.stackSize = this.getInventoryStackLimit();
		}
	}

	/**
	 * Returns the name of the inventory
	 */
	@Override
	public String getInventoryName()
	{
		return "container." + getTileEntityName();
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		this.items = new ItemStack[this.getSizeInventory()];

		for (int i = 0; i < nbttaglist.tagCount(); ++i)
		{
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte b0 = nbttagcompound1.getByte("Slot");

			if (b0 >= 0 && b0 < this.items.length)
			{
				this.items[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.items.length; ++i)
		{
			if (this.items[i] != null)
			{
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte) i);
				this.items[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbt.setTag("Items", nbttaglist);
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
	public boolean receiveClientEvent(int key, int value)
	{
		if (key == 1)
		{

		}
		else if (key == 2)
		{

		}
		else
		{
			return super.receiveClientEvent(key, value);
		}
		return true;
	}

	public void sendClientInfo(int key, int value)
	{
		this.worldObj.addBlockEvent(this.xCoord, this.yCoord, this.zCoord, this.getBlockType(), key, value);
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord, this.zCoord, this.getBlockType());
		this.worldObj.notifyBlocksOfNeighborChange(this.xCoord, this.yCoord - 1, this.zCoord, this.getBlockType());
	}

	@Override
	public boolean hasCustomInventoryName()
	{
		return false;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player)
	{
		return true;
	}

	@Override
	public void openInventory()
	{
	}

	@Override
	public void closeInventory()
	{
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		return true;
	}

}
