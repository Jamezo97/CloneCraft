package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.jamezo97.clonecraft.CloneCraftHelper;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.util.RelativeCoord;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

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
	
	
	ChunkCoordinates craftingLocation = null;
	
	ItemStack[] craftStacks = null;
	
	ItemStack craftOutput = null;
	
	int craftCount = 0;
	
	private int amountWeWant;
	
	private int previousQuantity;
	
	boolean fetchedSomething = false;
	
	boolean forceNewCheck = false;
	
	
	@Override
	public boolean shouldExecute()
	{
		if(goFetch != null)
		{
			int currentQuantity = clone.inventory.containsCount(goFetch);
			
			int quantityDiff = currentQuantity - previousQuantity;
			
			if(quantityDiff > 0)
			{
				this.fetchedSomething = true;
				forceNewCheck = true;
			}
			
			this.goFetch.stackSize -= quantityDiff;
			
			this.previousQuantity = currentQuantity;
			
			if(goFetch.stackSize <= 0)
			{
				goFetch = null;
				this.resetCrafting();
				this.closeChest();
				fetchedSomething = false;
				return false;
			}
			
			if(forceNewCheck || clone.ticksExisted % 10 == 0)
			{
				forceNewCheck = false;
				
				
				List eItems = this.clone.worldObj.getEntitiesWithinAABB(EntityItem.class, this.clone.boundingBox.expand(16, 16, 16));
				
				for(int a = 0; a < eItems.size(); a++)
				{
					EntityItem entityItem = (EntityItem)eItems.get(a);
					
					ItemStack stack = entityItem.getEntityItem();
					
					if(this.areStacksSame(stack, goFetch))
					{
						action = 1;
						toPickup = entityItem;
						this.clone.moveToEntity(toPickup);
						
						
						
						if(!this.clone.getNavigator().noPath())
						{
							if(!this.clone.inventory.canFullyFit(stack))
							{
								int toDrop = clone.getRNG().nextInt(clone.inventory.mainInventory.length-9);
								CloneCraftHelper.dropAtEntity(clone, this.clone.inventory.mainInventory[toDrop]);
								this.clone.inventory.mainInventory[toDrop] = null;
							}
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
				
				List recipes = CraftingManager.getInstance().getRecipeList();
				
				for(int a = 0; a < recipes.size(); a++)
				{
					if(recipes.get(a) instanceof IRecipe)
					{
						IRecipe recipe = (IRecipe)recipes.get(a);
						
						ItemStack output;
						
						
						
						if(areStacksSame(output = recipe.getRecipeOutput(), goFetch))
						{
							
							List items = null;
							
							int craftingSize = recipe.getRecipeSize();
							
							boolean needsTable = craftingSize > 2;
							
							if(recipes.get(a) instanceof ShapelessOreRecipe)
							{
								items = ((ShapelessOreRecipe)recipes.get(a)).getInput();
								needsTable = craftingSize > 4;
							}
							else if(recipes.get(a) instanceof ShapelessRecipes)
							{
								items = ((ShapelessRecipes)recipes.get(a)).recipeItems;
								needsTable = craftingSize > 4;
							}
							else if(recipes.get(a) instanceof ShapedRecipes)
							{
								ShapedRecipes shaped = (ShapedRecipes)recipes.get(a);
								
								ItemStack[] itemStacks = shaped.recipeItems;
								
								needsTable = (shaped.recipeHeight * shaped.recipeWidth) > 4 || shaped.recipeHeight > 2 || shaped.recipeWidth > 3;
								
								if(itemStacks == null){continue;}
								
								items = new ArrayList();
								
								for(int b = 0; b < itemStacks.length; b++)
								{
									items.add(itemStacks[b]);
								}
							}
							else if(recipes.get(a) instanceof ShapedOreRecipe)
							{
								Object[] itemStacks = ((ShapedOreRecipe)recipes.get(a)).getInput();
								
								if(itemStacks == null){continue;}
								
								items = new ArrayList();
								
								for(int b = 0; b < itemStacks.length; b++)
								{
									items.add(itemStacks[b]);
								}
							}
							else
							{
								continue;
							}
							
							if(items != null && !items.isEmpty())
							{
								ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
								
								int canMake = Integer.MAX_VALUE;
								
								for(int b = 0; b < items.size(); b++)
								{
									Object obj = items.get(b);
									
									if(obj == null){continue;}
									
									ItemStack stack;
									if(obj instanceof ItemStack)
									{
										stack = (ItemStack)obj;
									}
									else if(obj instanceof Item)
									{
										stack = new ItemStack((Item)obj);
									}
									else if(obj instanceof Block)
									{
										stack = new ItemStack((Block)obj);
									}
									else if(obj instanceof ArrayList)
									{
										List list = (List)obj;
										if(list.size() == 1 && list.get(0) instanceof ItemStack)
										{
											stack = (ItemStack)list.get(0);
										}
										else
										{
											System.out.println("Array contains more than one item. Why?:");
											for(int c = 0; c < list.size(); c++)
											{
												System.out.println(list.get(c));
											}
											continue;
										}
									}
									else
									{
										System.err.println("I don't know what to do with item type: " + obj.getClass());
										continue;
									}
									
									stack = stack.copy();
									
									if(stack.getItemDamage() == 32767)
									{
										stack.setItemDamage(0);
									}
									
									boolean added = false;
									
									for(int c = 0; c < stacks.size(); c++)
									{
										if(this.areStacksSame(stacks.get(c), stack))
										{
											stacks.get(c).stackSize += stack.stackSize;
											added = true;
											break;
										}
									}
									
									if(!added)
									{
										stacks.add(stack);
									}
								}
								
//								System.out.println("Found recipe with " + stacks.size() + " required items");
								
								
								boolean failed = true;
								
								for(int b = 0; b < stacks.size(); b++)
								{
									ItemStack stack = stacks.get(b);
									
//									System.out.println(stack);
									
									int count = clone.inventory.containsCount(stack);
									
									
									
									if(count < stack.stackSize)
									{
										failed = true;
//										System.out.println("Failed: " + stack + ", " + stack.stackSize);
										break;
									}
									else
									{
										failed = false;
										
										canMake = Math.min(canMake, count / stack.stackSize);
									}
								}
								
//								System.out.println(failed);
								
								if(!failed)
								{
									if(canMake > 0)
									{
//										System.out.println("Can make");
										
										if(!needsTable)
										{
											//We can do this without a crafting table
//											System.out.println("No bench");
											action = 3;
											this.craftStacks = stacks.toArray(new ItemStack[stacks.size()]);
											this.craftCount = Math.min(canMake, (int)Math.ceil(goFetch.stackSize / (float)output.stackSize));
											this.craftOutput = output;
											return true;
										}
										else
										{
											ArrayList<RelativeCoord> crafting = clone.getIBlockReg().getNearbyBlocks(Blocks.crafting_table, 64);
//											System.out.println("Bench needed");
											if(crafting.size() > 0)
											{
												
//												System.out.println("Found bench!");
												Collections.sort(crafting);
												
												RelativeCoord coord = crafting.get(0);
												
												this.craftingLocation = new ChunkCoordinates(coord.xPos, coord.yPos, coord.zPos);
												
												PathEntity path = clone.moveTo(coord.xPos, coord.yPos, coord.zPos);
												
												if(path != null || clone.getDistanceSq(coord.xPos, coord.yPos, coord.zPos) < 10)
												{
//													System.out.println("Reset");
													action = 3;
													this.craftStacks = stacks.toArray(new ItemStack[stacks.size()]);
													this.craftCount = Math.min(canMake, (int)Math.ceil(goFetch.stackSize / (float)output.stackSize));
													this.craftOutput = output;	
													return true;
												}
												this.craftingLocation = null;
												
												continue;
											}
											else
											{
												continue;
											}
										}
									}
									else
									{
										System.out.println("Has the items, just not enough to craft.");
										continue;
									}
								}
								else
								{
									continue;
								}
							}
						}
						else
						{
							continue;
						}
					}
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
					clone.setDisplayMessageColour(0xffee2233);
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
	public void resetTask()
	{
		closeChest();
		if(goFetch == null)
		{
			this.chestLocation = null;
			this.craftCount = 0;
			this.craftingLocation = null;
			this.craftStacks = null;
			this.toPickup = null;
			this.action = -1;
		}
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
						else if(this.clone.getNavigator().noPath() && this.toPickup.ticksExisted > 20 && (this.toPickup.motionY*this.toPickup.motionY) < 0.01)
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
											forceNewCheck = true;
										}
										
										stack.stackSize -= removed;
										this.goFetch.stackSize -= removed;
										this.previousQuantity += removed;
										
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
								
								this.closeChest();
								
								this.action = -1;
								this.chestLocation = null;
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
								this.clone.openCloseChest(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ, true);
								
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
		else if(action == 3)
		{
			if(this.craftCount > 0 && this.craftOutput != null && this.craftStacks != null)
			{
				if(openedChest != 0)
				{
					openedChest--;
					
					if(openedChest <= 0)
					{
						openedChest--;
						
						if(this.craftCount > 0)
						{
							boolean hasAllStacks = true;
							
							//If we have enough items
							for(int a = 0; a < this.craftStacks.length; a++)
							{
								ItemStack toConsume = this.craftStacks[a];
								if(toConsume == null){continue;}
								
								if(clone.inventory.containsCount(toConsume) < toConsume.stackSize)
								{
									hasAllStacks = false;
									break;
								}
							}
							
							if(hasAllStacks)
							{
								//Consume all the items
								for(int a = 0; a < this.craftStacks.length; a++)
								{
									ItemStack toConsume = this.craftStacks[a];
									if(toConsume == null){continue;}
									
									clone.inventory.consume(toConsume);
								}
								
								ItemStack fit = craftOutput.copy();
								
								this.goFetch.stackSize -= fit.stackSize;
								this.previousQuantity += fit.stackSize;
								fetchedSomething = true;
								forceNewCheck = true;
								
								this.clone.inventory.tryFitInInventory(fit);
								
								if(fit.stackSize > 0)
								{
									CloneCraftHelper.dropAtEntity(clone, fit);
								}
								
								this.craftCount--;
								
								
								if(this.craftCount <= 0)
								{
									resetCrafting();
									action = -1;
								}
							}
							else
							{
								//We no longer have all the items we need. Abort.
								resetCrafting();
								action = -1;
							}
						}
					}
					else
					{
						if(this.craftingLocation != null)
						{
							clone.getLookHelper().setLookPosition(this.craftingLocation.posX+0.5, this.craftingLocation.posY+0.8, this.craftingLocation.posZ+0.5, 10.0F, clone.getVerticalFaceSpeed());
						}
					}
				}
				else
				{
					boolean craft = true;
					
					if(this.craftingLocation != null)
					{
						craft = false;
						double distance = clone.getDistanceSq(this.craftingLocation.posX, this.craftingLocation.posY, this.craftingLocation.posZ);
						
						if(distance < 10 && (distance < 1 || clone.canSeeBlock(this.craftingLocation, Blocks.crafting_table)) )
						{
							craft = true;
						}
						else if(clone.getNavigator().noPath())
						{
							clone.moveTo(this.craftingLocation.posX, this.craftingLocation.posY, this.craftingLocation.posZ);
						}
					}
					
					if(craft)
					{
						openedChest = 20;
					}
				}
			}
			else
			{
				resetCrafting();
				action = -1;
			}
		}
		
		
	}
	
	private void resetCrafting()
	{
		this.craftCount = 0;
		this.openedChest = 0;
		this.craftingLocation = null;
		this.craftStacks = null;
		this.craftOutput = null;
	}
	
	public void closeChest()
	{
		if(this.chestLocation != null)
		{
//			System.out.println("CLOSE");
			openedChest = 0;
			this.clone.openCloseChest(this.chestLocation.posX, this.chestLocation.posY, this.chestLocation.posZ, false);
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
		this.craftingLocation = null;
		this.action = -1;
		this.resetCrafting();
		
		this.goFetch = fetch;
		
		if(this.goFetch != null)
		{
			this.previousQuantity = this.clone.inventory.containsCount(this.goFetch);
			this.amountWeWant = this.previousQuantity + this.goFetch.stackSize;
		}
		
	}

	public void pickedUpItem(ItemStack stack)
	{
		if(this.goFetch != null && stack != null)
		{
			if(areStacksSame(stack, this.goFetch))
			{
				this.goFetch.stackSize -= stack.stackSize;
				this.previousQuantity += stack.stackSize;
				fetchedSomething = true;
				forceNewCheck = true;
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
