package net.jamezo97.clonecraft.build;

import java.util.ArrayList;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.ai.EntityAIFetchItemStack;
import net.jamezo97.clonecraft.clone.ai.Notifier;
import net.jamezo97.clonecraft.clone.ai.block.BlockFinder;
import net.jamezo97.clonecraft.clone.ai.block.EntityAIMine;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.jamezo97.clonecraft.schematic.SchematicEntry;
import net.jamezo97.clonecraft.tileentity.CCTileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockButton;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLever;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class EntityAIBuild extends EntityAIBase implements BlockFinder, Notifier{
	
	EntityClone clone;
	
	Schematic schem;
	//Tests
	boolean isBuilding = false;
	
	/**
	 * From above, in OpenGL a '+ y' rotation rotates anti clockwise. Let's invert it because it works nicer with Minecraft. Thus<br>
	 * When rotate =<ul>
	 * <li><b>0:</b> X+ Z+</li>
	 * <li><b>1:</b> X- Z+</li>
	 * <li><b>2:</b> X- Z-</li>
	 * <li><b>3:</b> X+ Z-</li>
	 * </ul>
	 * 0 +X -> +X	+Z -> +Z
	 * 1 +X -> +Z	+Z -> -X
	 * 2 +X -> -X	+Z -> -Z
	 * 3 +X -> -Z	+Z -> +X
	 */
	private int rotate = 0;
	
	public static final int[][] xzMultipliers = new int[][]{
		{1, 1},
		{-1, 1},
		{-1, -1},
		{1, -1}
	};
	
	public static final int[][] offsetIndexes = new int[][]{
		{0, 2},
		{2, 0},
		{0, 2},
		{2, 0}
	};
	
	//The corner from which the current building is being created from.
	public int posX;
	public int posY;
	public int posZ;
	
	int index = 0;
	
	/**
	 * The current state of the build:<br>
	 * <ul>
	 * <li>0: Just placing solid full blocks. Dirt, stone, Glass, Sponge, Logs, Leaves, Crafting Bench, Furnace etc</li>
	 * <li>1: Placing non solid blocks, excluding doors and redstone.</li>
	 * <li>2: Placing doors and redstone, without block updates. Door blocks only consume door item if there is no door beneath the block being placed.</li>
	 * <li>3: Plant Crops, Place Fire, Light Portals, Place water and lava, actions which require an item to perform it.</li>
	 * </ul>
	 */
	int buildState = 0;
	
	ArrayList<Integer> skipBlocks = new ArrayList<Integer>();
	
//	ArrayList<Block> specialConsiderations = new ArrayList<Block>();
	
//	ArrayList<ChunkCoordinates> availableChests = new ArrayList<ChunkCoordinates>();
	
	public EntityAIBuild(EntityClone clone)
	{
		this.clone = clone;
		//8 4 - 1
		//1101
		this.setMutexBits(8);
	}
	
	public void setSchematic(Schematic schem)
	{
		this.schem = schem;
		if(!this.clone.worldObj.isRemote)
		{
			this.index = 0;
			buildState = 0;
		}
	}
	
	public float getAngularRotation()
	{
		return (this.rotate % 4) * 90;
	}
	
	public int getRotate()
	{
		return rotate;
	}
	
	public void setRotate(int rotate)
	{
		if(!this.isBuilding)
		{
			this.rotate = rotate % 4;
		}
	}
	
	public void incrementRotate()
	{
		setRotate(rotate+1);
	}

	public void setBuilding(boolean b)
	{
		this.isBuilding = b;
		if(!clone.worldObj.isRemote)
		{
			buildState = 0;
			
		}
	}
	
	/**
	 * The ItemStack currently trying to be fetched.
	 */
	ItemStack fetchingItem = null;
	
	int nextForceTicks = 0;
	
	@Override
	public boolean shouldExecute()
	{
		if(!isBuilding)
		{
			if(this.fetchingItem != null)
			{
				if(EntityAIFetchItemStack.areStacksSame(clone.getFetchAI().getFetchingItem(), this.fetchingItem))
				{
					clone.getFetchAI().fetchItem(null);
				}
				this.fetchingItem = null;
			}
			
			return false;
		}

		return schem != null;
	
	}
	
	@Override
	public boolean continueExecuting()
	{
		return shouldExecute();
	}
	
	public int getIndex()
	{
		return index;
	}
	

	public int getBuildState()
	{
		return buildState;
	}
	
	public boolean isRunning() 
	{
		return this.isBuilding;
	}
	
	
	@Override
	public void resetTask() 
	{
		//If we've been told to not build it, then reset the build states. Otherwise, just leave them, as we'll probably 
		if(!isBuilding)
		{
			this.schem = null;
			this.buildState = 0;
			this.skipBlocks.clear();
			this.ignoreItems = false;
			this.fetchingItem = null;
		}
		lookY = -1;
	}

	int placementCoolDown = 0;

	int maxBlockPlaceTries = 5;
	

	public int getSchematicIndexFromWorld(int x, int y, int z)
	{
		int[] offsets = new int[3];

		offsets[offsetIndexes[rotate][0]] = (x - posX) /  xzMultipliers[rotate][0];
		offsets[1] = y - posY;
		offsets[offsetIndexes[rotate][1]] = (z - posZ) /  xzMultipliers[rotate][1];
		
		if(schem.coordExists(offsets[0], offsets[1], offsets[2]))
		{
			return schem.posToIndex(offsets[0], offsets[1], offsets[2]);
		}
		return -1;
	}
	
	public int[] getWorldFromSchematicIndex(int index)
	{
		int[] offsets = schem.indexToPos(index);
		int[] xyz = new int[3];
		
		xyz[0] = posX + offsets[offsetIndexes[rotate][0]] * xzMultipliers[rotate][0];
		xyz[1] = posY + offsets[1];
		xyz[2] = posZ + offsets[offsetIndexes[rotate][1]] * xzMultipliers[rotate][1];
		
		return xyz;
	}
	
	boolean ignoreItems = false;
	

	public void ignoreItems(boolean b)
	{
		this.ignoreItems = b;
	}
	
	public boolean shouldIgnoreItems()
	{
		return this.ignoreItems;
	}
	
	double lookX, lookY, lookZ;

	
	/**
	 * @see PlayerControllerMP#onPlayerRightClick(net.minecraft.entity.player.EntityPlayer, net.minecraft.world.World, ItemStack, int, int, int, int, net.minecraft.util.Vec3)
	 */
	@Override
	public void updateTask()
	{
		int amountToPlace = 1;
		//Default of 4 blocks per second, 20/(4+1) = 4. Waits 4 ticks between places, just every 5 ticks, 20/5 = 4
		int blockDelay = 4;

		blockDelay = this.buildSpeed;
		

		
		if(isBuilding && lookY > 0)
		{
			clone.getLookHelper().setLookPosition(lookX, lookY, lookZ, 10, clone.getVerticalFaceSpeed());
		}
		
		if(placementCoolDown > 0)
		{
			placementCoolDown--;
			return;
		}
		
		maxBlockPlaceTries = 1000;
		
		int breakBlockLimit = 20;
		
		int x, y, z, metaAtPos, metaToPlace;
		int[] offsets;
		
		Block atPos;
		
		Block toPlace;

		if(EntityAIFetchItemStack.areStacksSame(clone.getFetchAI().getFetchingItem(), this.fetchingItem))
		{
			if(ignoreItems)
			{
				clone.getFetchAI().fetchItem(null);
				this.fetchingItem = null;
			}
		}
		
		
		for(int tryNo = 0; tryNo < maxBlockPlaceTries; tryNo++)
		{
			if(buildState >= 4)
			{
				break;
			}
			
			if(skipBlocks.contains(index))
			{
				index++;
				continue;
			}
			
			if(index >= schem.blockIds.length)
			{
				index = 0;
				buildState++;
				log("Next build state " + buildState);
				continue;
			}
			
			offsets = schem.indexToPos(index);
			
			x = posX + offsets[offsetIndexes[rotate][0]] * xzMultipliers[rotate][0];
			y = posY + offsets[1];
			z = posZ + offsets[offsetIndexes[rotate][1]] * xzMultipliers[rotate][1];
			
			if(y < 0 || y > 255)
			{
				index++;
				continue;
			}
			
			toPlace = schem.blockAt(index);
			metaToPlace = RotationMapping.translate(toPlace, schem.blockMetaAt(index), rotate);
			
			if(blockToBreak != null)
			{
				Block b;
				if(blockTryingToBreak == (b=clone.worldObj.getBlock(x, y, z)))
				{
					if(b == toPlace && clone.worldObj.getBlockMetadata(x, y, z) == metaToPlace)
					{
						this.blockToBreak = null;
						this.blockTryingToBreak = null;
						this.clone.getBlockAI().stopBreakingBlock();
						index++;
						continue;
					}
					break;
				}
				else
				{
					this.blockToBreak = null;
					this.blockTryingToBreak = null;
					this.clone.getBlockAI().stopBreakingBlock();
				}
			}
			else
			{
				clone.resetBlockHighlight();
			}
			
			if(this.fetchingItem != null && clone.getFetchAI().getFetchingItem() == this.fetchingItem && (clone.ticksExisted < nextForceTicks))
			{
				break;
			}
			else
			{
				nextForceTicks = clone.ticksExisted + 10;
			}
			
			log("At " + x + ", " + y + ", " + z);
			

			lookX = x+0.5; lookY = y + 0.5; lookZ = z + 0.5;
			
			
			toPlace = schem.blockAt(index);
			
			//If it's a skippable air block, or flowing liquid (not a source block)
			if((toPlace == Blocks.air && clone.getOptions().skipAirBlocks.get()))
			{
				index++;
				continue;
			}
			
			metaToPlace = RotationMapping.translate(toPlace, schem.blockMetaAt(index), rotate);
			
			
			atPos = clone.worldObj.getBlock(x, y, z);
			metaAtPos = clone.worldObj.getBlockMetadata(x, y, z);
			
			CustomBuilder builder = CustomBuilders.customBuilderMap.get(toPlace);
			
			
			
			if((builder instanceof GrassCustomBuilder || toPlace == Blocks.dirt) && (atPos == Blocks.dirt || atPos == Blocks.grass || atPos == Blocks.farmland))
			{
				index++;
				continue;
			}
			else
			{
				if(toPlace == atPos && metaAtPos == metaToPlace)
				{
					index++;
					log("Skipping: Exists");
					continue;
				}
				else if(toPlace == atPos)
				{
					if(((toPlace instanceof BlockDoor) ||
							(toPlace instanceof BlockTrapDoor) ||
							(toPlace instanceof BlockButton)  ||
							(toPlace instanceof BlockLever)) || 
							CustomBuilders.shouldIgnoreChangedMeta(this, x, y, z, clone.worldObj, toPlace, metaAtPos, metaToPlace))
					{
						index++;
						log("Skipping: Changed metadata");
						continue;
					}
					else if(RotationMapping.mappings.containsKey(toPlace))
					{
						//If there is a furnace or whatever already there, but in a different rotation, then rotate it!
						clone.worldObj.setBlockMetadataWithNotify(x, y, z, metaToPlace, 2);
						index++;
						log("Skipping: Adjusted");
						continue;
					}
				}
			}
			
			
			//If a block exists there when it shouldn't
			if(atPos != Blocks.air && !atPos.getMaterial().isLiquid())
			{
				
				
				if(buildState == 0)
				{
					if(!this.clone.getOptions().breakBlocks.get() && this.ignoreItems)
					{
						clone.getBlockAI().removeBlock(x, y, z, true);
						try
						{
					        clone.worldObj.playAuxSFXAtEntity(clone.getPlayerInterface(), 2001, x, y, z, Block.getIdFromBlock(atPos) + (metaAtPos << 12));
						}
						catch(Throwable t)
						{
							//Bah! Stupid minecraft mods assuming my player interface is the multiplayer version!
						}
						clone.worldObj.setBlock(x, y, z, Blocks.air);
						breakBlockLimit--;
						if(breakBlockLimit < 1)
						{
							break;
						}
					}
					else
					{
						log("Break it");
						clone.getBlockAI().setBlockFinder(this);
						this.blockToBreak = new ChunkCoordinates(x, y, z);
						this.blockTryingToBreak = atPos;
						continue;
					}
					
					
					
				}
				else
				{
					log("Skip it");
					index++;
					continue;
				}
			}
			else if(atPos.getMaterial().isLiquid() && ignoreItems)
			{
				clone.worldObj.setBlock(x, y, z, Blocks.air, 0, 3);
			}
			
			
			
			
			if(!isCorrectBuildstate(toPlace, metaToPlace, builder, x, y, z))
			{
				index++;
				log("Skipping: Nothing to build");
				continue;
			}
			
			
			
			if(atPos == Blocks.bedrock)
			{
				//We can't do anything with bedrock. Just skip over it.
				index++;
				log("Skipping bedrock");
				continue;
			}
			
			log("AtPos: " + atPos);
			
			if(toPlace instanceof BlockDynamicLiquid || (toPlace instanceof BlockLiquid && metaToPlace != 0))
			{
				index++;
				continue;
			}
			
			
			
			ItemStack toBuildWith = getRequiredItemToBuild(toPlace, metaToPlace, clone.worldObj, x, y, z);
			
			if(!ignoreItems && toBuildWith == null)
			{
				index++;
				log("Skipping null itemstack");
				continue;
			}
			
			boolean hasItemToBuild = ignoreItems || ((toBuildWith = this.fetchAndHoldItem(toBuildWith)) != null);
			
			if(!hasItemToBuild)
			{
				break;
			}
			else if(clone.getFetchAI().getFetchingItem() == this.fetchingItem)
			{
				log("Clear item");
				clone.getFetchAI().fetchItem(null);
			}
			
			if(builder != null)
			{
				if(builder.doCustomBuild(this, toPlace, metaToPlace, clone.worldObj, x, y, z, toBuildWith))
				{
					
					if(!ignoreItems) { checkStack(toBuildWith); }
					clone.swingItem();
					index++;
					
					placementCoolDown = ignoreItems?blockDelay:4;
					
					if(--amountToPlace < 1){break;}
				}
				else
				{
					log("Skipping: Custom Build Failed (" + builder.getClass() + ")");
					index++;
					continue;
				}
			}
			else if(buildState == 0)
			{
				
				clone.worldObj.setBlock(x, y, z, toPlace, metaToPlace, 3);
				clone.worldObj.setBlockMetadataWithNotify(x, y, z, metaToPlace, 2);
				playBlockSound(toPlace, x, y, z);
				clone.swingItem();
				
				if(!ignoreItems) {toBuildWith.stackSize--; checkStack(toBuildWith);}
				
				getTileEntity(index, x, y, z, true);
				
				index++;
				
				placementCoolDown = ignoreItems?blockDelay:4;
				
				if(--amountToPlace < 1){break;}
			}
			else if(buildState == 1)
			{
				
				clone.worldObj.setBlock(x, y, z, toPlace, metaToPlace, 3);
				clone.worldObj.setBlockMetadataWithNotify(x, y, z, metaToPlace, 2);
				playBlockSound(toPlace, x, y, z);
				clone.swingItem();
				getTileEntity(index, x, y, z, true);
				index++;
				
				if(!ignoreItems) {toBuildWith.stackSize--; checkStack(toBuildWith);}
				
				placementCoolDown = ignoreItems?blockDelay:4;
				
				if(--amountToPlace < 1){break;}
			}
			else
			{
				index++;
			}
		}
			
		
		if(buildState >= 4)
		{
			this.schem = null;

			this.isBuilding = false;
			this.index = 0;
			this.blockToBreak = null;
			clone.say("I'm done!", 64);
			this.skipBlocks.clear();
		}
	}
	
	public boolean isCorrectBuildstate(Block block, int meta, CustomBuilder cb, int x, int y, int z)
	{
		if(cb != null)
		{
			return cb.isCorrectBuildstate(buildState);
		}
		
		boolean isSolid = (block.getMaterial().isSolid()) && !(block instanceof BlockSign) && !(block instanceof BlockTrapDoor);
		
		return (isSolid && buildState == 0) || (!isSolid && buildState == 1) ;
	}
	
	public ItemStack getRequiredItemToBuild(Block block, int meta, World world, int x, int y, int z)
	{
		ItemStack use = CustomBuilders.getRequiredItemstack(this, block, meta, world, x, y, z);
		
		if(use == null)
		{
			use = BlockItemRegistry.getItemstack(block, meta);
		}
		
		if(use == null)
		{
			return null;
		}

		return use.copy();
	}
	
	public ItemStack fetchAndHoldItem(ItemStack fetch)
	{
		if(fetch == null)
		{
			return null;
		}
		
		for(int a = 0; a < clone.inventory.mainInventory.length; a++)
		{
			ItemStack stack = clone.inventory.mainInventory[a];
			
			if(stack != null)
			{
				if (fetch.getItem() == stack.getItem() && (!fetch.getHasSubtypes() || fetch.getItemDamage() == stack.getItemDamage())
						&& ItemStack.areItemStackTagsEqual(fetch, stack))
				{
					int index = clone.inventory.putStackOnHotbar(a);
					clone.inventory.currentItem = index;
					return stack;
				}
			}
		}
		
		fetchingItem = fetch;
		
		{
			Block toPlace = schem.blockAt(index);
			
			int toPlaceId = schem.blockIdAt(index);
			int metaToPlace = RotationMapping.translate(toPlace, schem.blockMetaAt(index), rotate);
			
			int itemMeta = BlockItemRegistry.normalizeMeta(toPlace, metaToPlace);
			
			
			float stackSize = 1;
			
			if(toPlace instanceof BlockBed || toPlace instanceof BlockDoor)
			{
				stackSize = 0.5f;
			}

			CustomBuilder b = CustomBuilders.customBuilderMap.get(toPlace);
			
			for(int a = index+1; a < schem.blockIds.length && stackSize < 64; a++)
			{
				//if it's the same block, and potentially the same meta data
				if(schem.blockIds[a] == toPlaceId)
				{
					int[] offsets = schem.indexToPos(a);
					
					int x = posX + offsets[offsetIndexes[rotate][0]] * xzMultipliers[rotate][0];
					int y = posY + offsets[1];
					int z = posZ + offsets[offsetIndexes[rotate][1]] * xzMultipliers[rotate][1];
					
					ItemStack toBuild = this.getRequiredItemToBuild(toPlace, schem.blockMetas[a], clone.worldObj, x, y, z);
					
					if(toBuild != null && toBuild.getItem() == fetch.getItem() && toBuild.getItemDamage() == fetch.getItemDamage() && ItemStack.areItemStackTagsEqual(toBuild, fetch))
					{
						Block atPos = clone.worldObj.getBlock(x, y, z);
						
						int metaAtPos = BlockItemRegistry.normalizeMeta(atPos, clone.worldObj.getBlockMetadata(x, y, z));
						
						//If this block is already there, then just skip it.
						{
							if(!(toPlace == Blocks.grass && atPos == Blocks.dirt) && !(toPlace == Blocks.dirt && atPos == Blocks.grass))
							{
								if(toPlace == atPos && metaAtPos == metaToPlace)
								{
									continue;
								}
								else if(toPlace == atPos)
								{
									if(((toPlace instanceof BlockDoor) ||
											(toPlace instanceof BlockTrapDoor) ||
											(toPlace instanceof BlockButton)  ||
											(toPlace instanceof BlockLever)))
									{
										continue;
									}
									else if(RotationMapping.mappings.containsKey(toPlace))
									{
										continue;
									}
									else if(CustomBuilders.shouldIgnoreChangedMeta(this, x, y, z, clone.worldObj, toPlace, metaAtPos, metaToPlace))
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
						
						if(toPlace instanceof BlockDynamicLiquid || (toPlace instanceof BlockLiquid && schem.blockMetas[a] != 0))
						{
							continue;
						}
						System.out.println(schem.blockMetas[a]);
						
						if(toPlace instanceof BlockDoor || toPlace instanceof BlockBed)
						{
							stackSize += toBuild.stackSize * 0.5f;
						}
						else
						{
							stackSize += toBuild.stackSize;
						}
					}
					
					
				}
			}
			
			fetch.stackSize = Math.min(64, Math.round(stackSize));
			
		}
		
		
		clone.goFetchItem(fetch, this);

		return null;
	}
	
	public void playBlockSound(Block block, double x, double y, double z)
	{
		 clone.worldObj.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, 
				 block.stepSound.func_150496_b(), (block.stepSound.getVolume() + 1.0F) / 2.0F, block.stepSound.getPitch() * 0.8F);
	}
	
	public void checkStack(ItemStack stack)
	{
		if(stack != null && stack.stackSize == 0)
		{
			clone.setCurrentItemOrArmor(0, null);
		}
	}
	
	public TileEntity getTileEntity(int index, int x, int y, int z, boolean place)
	{
		if(schem != null && schem.hasTileEntityAt(index))
		{
			TileEntity te = schem.getTileEntity(index, clone.worldObj);
			if(te != null)
			{
				te.xCoord = x;
				te.yCoord = y;
				te.zCoord = z;
				
				//Makes sure we're not cloning ItemStacks
				CCTileEntityRegistry.clearItemstacks(te);
				
				if(y >= 0 && y < clone.worldObj.getHeight())
				{
					if(place)
					{
						clone.worldObj.setTileEntity(x, y, z, te);
					}
					return te;
				}
			}
			
		}
		
		return null;
	}
	
	public void log(String s)
	{
//		System.out.println(s);
	}

	public NBTTagCompound saveBuildState(NBTTagCompound nbt)
	{
		nbt.setInteger("posX", posX);
		nbt.setInteger("posY", posY);
		nbt.setInteger("posZ", posZ);
		
		nbt.setInteger("index", index);

		nbt.setInteger("rotate", rotate);
		
		nbt.setInteger("buildState", buildState);
		
		nbt.setInteger("buildSpeed", buildSpeed);

		nbt.setBoolean("isBuilding", isBuilding);
		nbt.setBoolean("ignoreItems", ignoreItems);
		
		
		
		if(schem != null)
		{
			nbt.setBoolean("savedSchem", true);
			
			nbt.setLong("schemHash", schem.myHashCode());
			nbt.setInteger("xSize", schem.xSize);
			nbt.setInteger("ySize", schem.ySize);
			nbt.setInteger("zSize", schem.zSize);
		}
		else
		{
			nbt.setBoolean("savedSchem", false);
		}
		
		if(fetchingItem != null)
		{
			NBTTagCompound itemNBT = new NBTTagCompound();
			fetchingItem.writeToNBT(itemNBT);
			nbt.setTag("Fetching", itemNBT);
		}
		
		return nbt;
	}
	
	public void loadBuildState(NBTTagCompound nbt)
	{
		this.posX = nbt.getInteger("posX");
		this.posY = nbt.getInteger("posY");
		this.posZ = nbt.getInteger("posZ");
		
		this.index = nbt.getInteger("index");
		
		this.rotate = nbt.getInteger("rotate");

		this.buildState  = nbt.getInteger("buildState");

		this.buildSpeed  = nbt.getInteger("buildSpeed");

		this.isBuilding = nbt.getBoolean("isBuilding");

		this.ignoreItems = nbt.getBoolean("ignoreItems");
		
		boolean savedSchematic = nbt.getBoolean("savedSchem");
		
		if(savedSchematic)
		{
			long hash = nbt.getLong("schemHash");
			int xSize = nbt.getInteger("xSize");
			int ySize = nbt.getInteger("ySize");
			int zSize = nbt.getInteger("zSize");
			
			SchematicEntry entry = CloneCraft.INSTANCE.schematicList.getSchematic(hash, xSize, ySize, zSize);
			
			if(entry != null)
			{
				this.schem = entry.schem;
			}
			else
			{
				System.err.println("Couldn't load schematic for clone build AI. Hash: " + Long.toHexString(hash) + 
						"  xSize: " + xSize + "  ySize: " + ySize + "  zSize: " + zSize);
			}
		}
		if(nbt.hasKey("Fetching"))
		{
			NBTTagCompound itemNBT = nbt.getCompoundTag("Fetching");
			this.fetchingItem = ItemStack.loadItemStackFromNBT(itemNBT);
		}
	}
	
	ChunkCoordinates blockToBreak = null;
	
	Block blockTryingToBreak = null;

	

	public boolean renderOverlay() 
	{
		return schem != null;
	}

	public Schematic getSchematic() 
	{
		return schem;
	}

	@Override
	public ChunkCoordinates getNextBlock(EntityAIMine ai) 
	{
		return blockToBreak;
	}

	@Override
	public void onFinished(EntityAIMine entityAI, ChunkCoordinates coordinates, ItemStack stack, Block block, int meta) 
	{
		blockToBreak = null;
		blockTryingToBreak = null;

		
		clone.resetBlockHighlight();
		entityAI.setBlockFinder(clone.getDefaultBlockFinder());
	}

	@Override
	public boolean mustBeCloseToBreak() {return false;}

	@Override
	public void cantBreakBlock(ChunkCoordinates cc, Block theBlock, int theMeta) 
	{
		if(theBlock == Blocks.bedrock)
		{
			skipBlocks.add(index);
			
			clone.setDisplayMessage("I can't break bedrock. I'll skip that block.");
			clone.setDisplayMessageColour(0xffffffff);
			clone.setDisplayMessageCooldown(40 + "I can't break bedrock. I'll skip that block.".length() * 2);
			
			
			
			this.blockToBreak = null;
			this.blockTryingToBreak = null;
		}
		else
		{
			ItemStack stack = new ItemStack(theBlock, 1, theMeta);
			
			if(stack.getItem() != null)
			{
				String s = "I can't break " + stack.getDisplayName();

				clone.setDisplayMessage(s);
				clone.setDisplayMessageColour(0xffdd2222);
				clone.setDisplayMessageCooldown(40 + s.length() * 2);
				
				
				clone.setBlockHighlight(cc.posX, cc.posY, cc.posZ);
			}
		}
	}

	@Override
	public void clonePickedUp(EntityClone clone, ItemStack stack) {}

	@Override
	public void saveState(NBTTagCompound nbt) {}

	@Override
	public void loadState(NBTTagCompound nbt) {}

	@Override
	public void onSuccess(ItemStack fetched)
	{
		
	}
	
	@Override
	public boolean isCreativeMode()
	{
		return this.shouldIgnoreItems();
	}

	public boolean mustFetchAll()
	{
		return false;
	}

	@Override
	public void onFailure(ItemStack failedToFetch)
	{
		
	}

	@Override
	public void onFetchedSome(ItemStack fetched)
	{
		
	}

	public void setIndex(int index)
	{
		if(clone.worldObj.isRemote)
		{
			this.index = index;
		}
	}

	public void setBuildState(int state)
	{
		if(clone.worldObj.isRemote)
		{
			this.buildState = state;
		}
	}

	int fullSize;
	
	public int getSchemFullSize()
	{
		if(schem != null)
		{
			return schem.getLayerSize() * schem.ySize;
		}
		return fullSize;
	}

	public void setSchemFullSize(int fullSize)
	{
		this.fullSize = fullSize;
	}

	int buildSpeed = 5;
	
	public void setBuildSpeed(int i)
	{
		if(i > 19)
		{
			i = 19;
		}
		else if(i < 0)
		{
			i = 0;
		}
		this.buildSpeed = i;
	}
	
	public int getBuildSpeed()
	{
		return this.buildSpeed;
	}






	
	
	
	
}
