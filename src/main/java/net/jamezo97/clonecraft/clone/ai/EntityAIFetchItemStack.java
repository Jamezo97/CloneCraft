package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;
import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.util.RelativeCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;

/**
 * Attempts to go
 * @author James
 *
 */
public class EntityAIFetchItemStack extends EntityAIBase{

	ItemStack goFetch = null;
	
	Notifier notifier = null;
	
	EntityClone clone;
	
	public EntityAIFetchItemStack(EntityClone clone)
	{
		//8 - - 1
		this.setMutexBits(9);
		this.clone = clone;
	}
	
	/**
	 * The action we are trying to perform.
	 * 0 = Wait for a human to give us the item
	 * 1 = Go walk to an item and pick it up.
	 * 2 = Go walk to a chest and grab a stack from it.
	 */
	int action = 0;

	EntityItem toPickup = null;
	
	ChunkCoordinates chestLocation = null;
	
	boolean fetchedSomething = false;
	
	@Override
	public boolean shouldExecute()
	{
		if(goFetch != null)
		{
			if(goFetch.stackSize <= 0)
			{
				goFetch = null;
				fetchedSomething = false;
				return false;
			}
			
			if(clone.ticksExisted % 10 == 0)
			{
				List items = this.clone.worldObj.getEntitiesWithinAABB(EntityItem.class, this.clone.boundingBox.expand(16, 16, 16));
				
				for(int a = 0; a < items.size(); a++)
				{
					EntityItem entityItem = (EntityItem)items.get(a);
					
					ItemStack stack = entityItem.getEntityItem();
					
					if(this.areStacksSame(stack, goFetch))
					{
						action = 1;
						toPickup = entityItem;
						this.clone.moveToEntity(toPickup);
						if(!this.clone.getNavigator().noPath())
						{
							return true;
						}
					}
				}
			
				ImportantBlockRegistry ibr = clone.getIBlockReg();
				ArrayList<RelativeCoord> coords = ibr.getNearbyBlocks(Blocks.chest, 64);
				
				ArrayList<RelativeCoord> foundCoords = new ArrayList<RelativeCoord>();
				
				for(int a = 0; a < coords.size(); a++)
				{
					RelativeCoord coord = coords.get(a);
					TileEntity te = clone.worldObj.getTileEntity(coord.xPos, coord.yPos, coord.zPos);
					
					if(te instanceof TileEntityChest)
					{
						TileEntityChest chest = (TileEntityChest)te;
						
						for(int i = 0; i < chest.getSizeInventory(); i++)
						{
							ItemStack stack = chest.getStackInSlot(i);
							
							if(stack != null && this.areStacksSame(stack, this.goFetch))
							{
								foundCoords.add(coord);
								break;
							}
						}
					}
				}
				
				if(foundCoords.size() > 0)
				{
					int closestIndex = -1;
					double distance = 0;
					
					for(int a = 0; a < foundCoords.size(); a++)
					{
						if(closestIndex == -1 || foundCoords.get(a).distanceSq < distance)
						{
							closestIndex = a;
							distance = foundCoords.get(a).distanceSq;
						}
					}
					
					RelativeCoord coord = foundCoords.get(closestIndex);
					
				
					this.chestLocation = new ChunkCoordinates(coord.xPos, coord.yPos, coord.zPos);
					
					this.action = 2;
					
					return true;
				}
				
				if(fetchedSomething && notifier != null && !notifier.mustFetchAll())
				{
					notifier.onFailure(goFetch);
					goFetch = null;
					action = -1;
					fetchedSomething = false;
					return false;
				}
				
				if(goFetch.getItem() != null)
				{
					String string = "I need " + goFetch.stackSize + " " + /*(notifier!=null && !notifier.mustFetchAll()?"some ":goFetch.stackSize + " ") + */goFetch.getDisplayName() + (goFetch.getItem() instanceof ItemBlock?" Blocks":"");
					
					clone.setDisplayMessage(string);
					clone.setDisplayMessageColour(0xffdd2233);
					clone.setDisplayMessageCooldown(80);
					
					action = 0;
					return false;
				}
			}
		}
		action = -1;
		return false;
	}
	
	public void setNotifier(Notifier notifier)
	{
		this.notifier = notifier;
	}
	
	@Override
	public boolean continueExecuting()
	{
		if(goFetch != null)
		{
			this.checkFetchComplete();
		}
		
		return goFetch != null && action > 0;
	}

	@Override
	public void startExecuting()
	{
		super.startExecuting();
		
	}
	
	int nextSetPath = 0;

	@Override
	public void updateTask()
	{
		if(action == 1)
		{
			if(this.toPickup != null)
			{
				if(this.toPickup.isEntityAlive())
				{
					if(this.toPickup.getEntityItem().stackSize <= 0)
					{
						this.toPickup = null;
						this.action = -1;
					}
					else
					{
						double dist = this.clone.getDistanceSqToEntity(toPickup);
						
						if(dist > 100)
						{
							this.toPickup = null;
							this.action = -1;
						}
						else if(dist < 2)
						{
							ItemStack picked = this.clone.pickupItem(this.toPickup.getEntityItem());
							
							if(this.toPickup.getEntityItem().stackSize == 0)
							{
								clone.worldObj.removeEntity(this.toPickup);
							}
							
							this.pickedUpItem(picked);
						}
						else if(this.clone.getNavigator().noPath() && Math.abs(this.toPickup.motionY) < 0.01)
						{
							clone.moveTo(this.toPickup.posX, this.toPickup.posY, this.toPickup.posZ);
						}
					}
				}
				else
				{
					this.toPickup = null;
					this.action = -1;
				}
			}
		}
		else if(action == 2)
		{
			if(this.chestLocation != null && this.goFetch != null)
			{
				Block atPos = clone.worldObj.getBlock(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ);
				
				if(atPos != Blocks.chest)
				{
					openedChest = 0;
					action = -1;
					this.chestLocation = null;
				}
				else
				{
					if(openedChest > 0)
					{
						openedChest--;
						
						clone.getLookHelper().setLookPosition(this.chestLocation.posX+0.5, this.chestLocation.posY+0.5, this.chestLocation.posZ+0.5, 16.0f, clone.getVerticalFaceSpeed());
						
						if(openedChest == 0)
						{
							TileEntity te = clone.worldObj.getTileEntity(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ);
							
							if(te instanceof IInventory)
							{
								IInventory inven = (IInventory)te;
								
								
								
								for(int a = 0; a < inven.getSizeInventory(); a++)
								{
									ItemStack stack = inven.getStackInSlot(a);
									
									
									if(stack != null && this.areStacksSame(goFetch, stack))
									{
										ItemStack addToInven = stack.copy();
										addToInven.stackSize = Math.min(stack.stackSize, this.goFetch.stackSize);

										int removed = clone.inventory.tryFitInInventory(addToInven);

										if(removed > 0)
										{
											fetchedSomething = true;
										}
										
										stack.stackSize -= removed;
										this.goFetch.stackSize -= removed;
										
										if(stack.stackSize == 0)
										{
											inven.setInventorySlotContents(a, null);
										}
										
										if(this.goFetch.stackSize == 0)
										{
											this.goFetch = null;
											break;
										}
									}
								}
								
								this.action = -1;
								this.chestLocation = null;
								
								inven.closeInventory();
							}
						}

						
					}
					else
					{
						double distance = clone.getDistanceSq(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ);
						
						
						if(distance < 10 && (distance < 1 || clone.canSeeBlock(this.chestLocation, Blocks.chest)) )
						{
							TileEntity te = clone.worldObj.getTileEntity(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ);
							
							if(te instanceof IInventory)
							{
								((IInventory) te).openInventory();
								openedChest = (int)Math.round(20 + (this.goFetch.stackSize * 0.5));
							}
							else
							{
								closeChest();
								openedChest = 0;
								action = -1;
								this.chestLocation = null;
							}
						}
						else if(clone.getNavigator().noPath())
						{
							clone.moveTo(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ);
						}
					}
				}
			}
			else
			{
				closeChest();
				this.chestLocation = null;
				this.action = -1;
			}
		}
	}
	
	public void closeChest()
	{
		if(openedChest > 0 && this.chestLocation != null)
		{
			TileEntity te = clone.worldObj.getTileEntity(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ);
		
			if(te instanceof IInventory)
			{
				((IInventory) te).closeInventory();
				openedChest = 0;
			}
		}
	}
	
	int openedChest = 0;

	public NBTTagCompound writeToNBT(NBTTagCompound nbt)
	{
		if(goFetch != null)
		{
			goFetch.writeToNBT(nbt);
			nbt.setBoolean("wroteitem", true);
		}
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		if(nbt.getBoolean("wroteitem"))
		{
			goFetch = ItemStack.loadItemStackFromNBT(nbt);
		}
	}
	
	

	public ItemStack getFetchingItem()
	{
		return goFetch;
	}

	public void fetchItem(ItemStack fetch)
	{
		this.goFetch = fetch;
	}

	public void pickedUpItem(ItemStack stack)
	{
		if(this.goFetch != null && stack != null)
		{
			if(areStacksSame(stack, this.goFetch))
			{
				this.goFetch.stackSize -= stack.stackSize;
				fetchedSomething = true;
				checkFetchComplete();
			}
		}
	}
	
	public void checkFetchComplete()
	{
		if(this.goFetch != null && this.goFetch.stackSize <= 0)
		{
			this.goFetch.stackSize = 0;
			
			if(this.notifier != null)
			{
				this.notifier.onSuccess(goFetch);
			}
			
			this.goFetch = null;
		}
	}
	
	/**
	 * Checks if two stacks are the same, not including their stack size
	 * @param stack1 The first stack to compare
	 * @param stack2 The second stack to compare
	 * @return True if the two stacks are the same type, regardless of their stack size.
	 */
	public static boolean areStacksSame(ItemStack stack1, ItemStack stack2)
	{
		return (stack1 == null && stack2 == null)?true:((stack1 != null && stack2 != null)?(   stack1.getItem() == stack2.getItem() && (stack1.getHasSubtypes()?stack1.getItemDamage() == stack2.getItemDamage():true) && ItemStack.areItemStackTagsEqual(stack1, stack2)   ):false);
	}

}
