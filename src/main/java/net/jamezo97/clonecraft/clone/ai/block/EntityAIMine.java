package net.jamezo97.clonecraft.clone.ai.block;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EntityAIMine extends EntityAIBase{

	
	/** Once all blocks have been minded from this finder, set this to null, and call
	 *  the on finished method in the interface
	 */
	BlockFinder currentFinder = null;
	
	EntityClone clone = null;
	
	public EntityAIMine(EntityClone clone)
	{
		this.clone = clone;
		// 00000101
		this.setMutexBits(5);
		
		silkTouchStick = new ItemStack(Items.stick, 1, 0);
		silkTouchStick.addEnchantment(Enchantment.silkTouch, 1);
	}
	
	public EntityClone getClone()
	{
		return this.clone;
	}
	
	public void setBlockFinder(BlockFinder finder)
	{
		this.currentFinder = finder;
	}

	@Override
	public boolean shouldExecute()
	{
		//Make sure they're not currently using an item, otherwise when we call 'selectNextBlock' we will disrupt the current item in use
		//causing the clone to switch between eating and not eating, and just.. yeah. buggerry stuff
		return !clone.isUsingItem() && currentFinder != null && (clone.getOptions().breakBlocks.get() || currentFinder == clone.getBuildAI()) && selectNextBlock(5);
	}
	
	public boolean selectNextBlock(int iterations)
	{
		for(int a = 0; a < iterations; a++)
		{
			ChunkCoordinates theCoords = currentFinder.getNextBlock(this);
			if(theCoords == null)
			{
				return false;
			}
			
			Block theBlock = this.clone.worldObj.getBlock(theCoords.posX, theCoords.posY, theCoords.posZ);
			
			if(theBlock == Blocks.air)
			{
				continue;
			}
			
			int theMeta = this.clone.worldObj.getBlockMetadata(theCoords.posX, theCoords.posY, theCoords.posZ);
			
			if(currentFinder.isCreativeMode())
			{
				this.breakCoord = theCoords;
				this.breakItem = this.clone.getCurrentEquippedItem();
				this.breakBlock = theBlock;
				this.breakMeta = theMeta;
				
				return true;
			}
			
			boolean canBreak = this.clone.selectBestItemForBlock(theCoords, theBlock, theMeta, true);
			
			if(canBreak)
			{
				this.breakCoord = theCoords;
				this.breakItem = this.clone.getCurrentEquippedItem();
				this.breakBlock = theBlock;
				this.breakMeta = theMeta;
				
				return true;
			}
			else
			{
				this.currentFinder.cantBreakBlock(theCoords, theBlock, theMeta);
			}
		}
		return false;
	}

	@Override
	public boolean continueExecuting()
	{
		return breakCoord != null && breakBlock != null && breakMeta != -1;
	}

	@Override
	public void startExecuting()
	{
		if(this.currentFinder.mustBeCloseToBreak())
		{
			clone.setPath(clone.getNavigator().getPathToXYZ(breakCoord.posX, breakCoord.posY, breakCoord.posZ));
		}
		else
		{
			startBreakingBlock();
		}
		
		lookAtBlock();
		
		lastTickTime = -1;
	}
	
	public void lookAtBlock()
	{
		clone.getLookHelper().setLookPosition(breakCoord.posX+0.5, breakCoord.posY+0.5, breakCoord.posZ+0.5, 10, clone.getVerticalFaceSpeed());
	}

	@Override
	public void resetTask() {
		stopBreakingBlock();
		lastTickTime = -1;
	}

	/**
	 * The coordinate of the block being currently broken
	 */
	ChunkCoordinates breakCoord = null;
	
	/**
	 * The block object which is currently being broken
	 */
	Block breakBlock = null;
	
	/**
	 * The metadata of the block being broken
	 */
	int breakMeta = -1;
	
	/**
	 * The Item currently being used to destroy a block
	 */
    private ItemStack breakItem;
    
    public boolean sameItemAndBlock()
    {
    	Block currentBlock = clone.worldObj.getBlock(this.breakCoord.posX,  this.breakCoord.posY, this.breakCoord.posZ);
    	
    	int currentMeta = clone.worldObj.getBlockMetadata(this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ);
    	
    	if(currentBlock != breakBlock || currentMeta != breakMeta)
    	{
    		return false;
    	}
    	
    	ItemStack currentItem = clone.inventory.getCurrentItem();
    	
    	
    	if(currentItem == null && breakItem == null)
    	{
    		return true;
    	}
    	else if(currentItem != null && breakItem != null)
    	{
    		return currentItem.isItemEqual(breakItem);
    	}
    	
    	return false;
    }
	
	@Override
	public void updateTask()
	{
		if(breakCoord != null && breakBlock != null && breakMeta != -1)
		{
			this.continueBreakingBlock();
		}
	}
	
	/**
	 * The time in milliseconds since the last time a the method 'continueBreakingBlock' was called.
	 * Used to calibrate the amplifier, as it's called every 3 or so ticks.
	 */
	long lastTickTime = -1;
	
	double lastX = 0;
	double lastY = 0;
	double lastZ = 0;
	
	int noImprovement = 0;
	
	public void continueBreakingBlock()
	{
		
		if(!sameItemAndBlock())
		{
			if(this.isHittingBlock)
			{
				this.stopBreakingBlock();
			}
			breakCoord = null;
			return;
		}
		
		//Flag is true if we're close enough to break the block
		boolean closeFlag = !currentFinder.mustBeCloseToBreak() || 
				clone.getDistanceSq(breakCoord.posX+0.5, breakCoord.posY+0.5-clone.getEyeHeight(), breakCoord.posZ+0.5) <= 25;
		
		if(!closeFlag && clone.ticksExisted % 5 == 0)
		{
			clone.setPath(clone.getNavigator().getPathToXYZ(breakCoord.posX, breakCoord.posY, breakCoord.posZ));
			
			if(Math.abs(clone.posX - lastX) < 5 && Math.abs(clone.posZ - lastZ) < 5 && Math.abs(clone.posZ - lastZ) < 5)
			{
				noImprovement++;
			}
			else
			{
				noImprovement = 0;
				
				lastX = clone.posX;
				lastY = clone.posY;
				lastZ = clone.posZ;
			}
			
			
		
		
			if(noImprovement > 20)
			{
				noImprovement = 0;
				this.currentFinder.cantBreakBlock(breakCoord, breakBlock, breakMeta);
				this.stopBreakingBlock();
				return;
			}
		}
		
		if(closeFlag && !this.isHittingBlock)
		{
			this.startBreakingBlock();
		}
		else if(!closeFlag && this.isHittingBlock)
		{
			this.stopBreakingBlock();
		}
		
		//If we're currently breaking a block.
		//And we're using the same item we had at the beginning, and the block hasn't changed
		if(this.isHittingBlock)
		{
			
			if(blockHitDelay > 0)
			{
				--blockHitDelay;
			}
			else
			{
				try
				{
					EntityPlayer thePlayer = clone.getPlayerInterface();
					
					Block block = clone.worldObj.getBlock(breakCoord.posX, breakCoord.posY, breakCoord.posZ);
					
					//Must be modified to take into account the tick rate of the ai class
					
					double amplifier = 1.0;
					
					if(this.lastTickTime != -1)
					{
						long tickTime = System.currentTimeMillis();
						double tickDiff = tickTime - this.lastTickTime;
						
						lastTickTime = tickTime;
						
						amplifier = Math.round(tickDiff / 50.0d);
						//Each time, it should be called 50 milliseconds after the last
						//If it's called, for example, 100 milliseconds later, then the block should
						//be twice as much broken. Thus 100/50 = 2 = the amplification factor.
						//This should roughly equal the 'EntityAITasks.tickRate' value
						if(amplifier > 5)
						{
							amplifier = 5;
						}
						else if(amplifier < 1)
						{
							amplifier = 1;
						}
					}
					else
					{
						lastTickTime = System.currentTimeMillis();
					}
					
					this.curBlockDamageMP += amplifier * (block.getPlayerRelativeBlockHardness(thePlayer, thePlayer.worldObj, breakCoord.posX, breakCoord.posY, breakCoord.posZ));
					
					this.clone.swingItem();
					
	                if (this.stepSoundTickCounter % 4.0F == 0.0F)
	                {
	                	this.clone.worldObj.playSoundEffect(breakCoord.posX, breakCoord.posY, breakCoord.posZ, block.stepSound.getStepResourcePath(), block.stepSound.getPitch(), 0.3f);
	                }

	                this.lookAtBlock();
	                
	                ++this.stepSoundTickCounter;

	                if (this.curBlockDamageMP >= 1.0F)
	                {
	                    
	                    this.harvestBlock(breakCoord.posX, breakCoord.posY, breakCoord.posZ);
	                    
	                    this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getEntityId(), breakCoord.posX, breakCoord.posY, breakCoord.posZ, (int)(this.curBlockDamageMP * 10.0F) - 1);

	                    this.currentFinder.onFinished(this, breakCoord, breakItem, breakBlock, breakMeta);
	                    
	                    this.stopBreakingBlock();
	                    
	                    this.stepSoundTickCounter = 0.0F;
	                    this.blockHitDelay = 5;
	                    
	                    clone.inventory.currentItem = 0;
	                    lastTickTime = -1;
	                }
	                else
	                {
	                	this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getEntityId(), breakCoord.posX, breakCoord.posY, breakCoord.posZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
	                }
				}
				catch(Throwable t)
				{
					this.breakFailedError(t);
				}
			}
		}
		else
		{
			if(this.curBlockDamageMP > 0)
			{
				if (breakCoord != null) {
					this.clone.worldObj.destroyBlockInWorldPartially(
							this.clone.getEntityId(), this.breakCoord.posX,
							this.breakCoord.posY, this.breakCoord.posZ, -1);
				}
				this.curBlockDamageMP = 0.0F;
			}
	        
//			stopBreakingBlock();
		}
	}
	
	public void stopBreakingBlock()
    {
        this.isHittingBlock = false;
        this.curBlockDamageMP = 0.0F;
        
        if(breakCoord != null)
        {
        	this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getEntityId(), this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ, -1);
            this.breakCoord = null;
        }
        
        this.breakCoord = null;
        this.breakItem = null;
        this.breakBlock = null;
        this.breakMeta = -1;
        this.blockHitDelay = 0;
    }
	
	ItemStack silkTouchStick = null;
	
	//From ItemInWorldManager
	public boolean harvestBlock(int blockX, int blockY, int blockZ)
	{
		try
		{
			EntityPlayer thisPlayerMP = this.clone.getPlayerInterface();
			World theWorld = this.clone.worldObj;
			
			ItemStack itemstack = thisPlayerMP.getCurrentEquippedItem();
	        
			//Prevents harvesting for items like Sword in creative mode I assume. Don't need this, as I always want to harvest the block
			/*if (stack != null && stack.getItem().onBlockStartBreak(stack, blockX, blockY, blockZ, thisPlayerMP))
	        {
	            return false;
	        }*/
	        
	        Block block = theWorld.getBlock(blockX, blockY, blockZ);
	        int l = theWorld.getBlockMetadata(blockX, blockY, blockZ);
	        theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, blockX, blockY, blockZ, Block.getIdFromBlock(block) + (theWorld.getBlockMetadata(blockX, blockY, blockZ) << 12));
	        boolean flag = false;

//	        ItemStack itemstack = thisPlayerMP.getCurrentEquippedItem();
	        boolean flag1 = block.canHarvestBlock(thisPlayerMP, l);

	        if (itemstack != null)
	        {
	            itemstack.func_150999_a(theWorld, block, blockX, blockY, blockZ, thisPlayerMP);

	            if (itemstack.stackSize == 0)
	            {
	                thisPlayerMP.destroyCurrentEquippedItem();
	            }
	        }

	        flag = removeBlock(blockX, blockY, blockZ, flag1);
	        
	        ItemStack replaced = null;
	        
	        if(this.currentFinder.isCreativeMode())
	        {
	        	replaced = thisPlayerMP.getCurrentEquippedItem();
	        	thisPlayerMP.setCurrentItemOrArmor(0, silkTouchStick);
	        }
	        
	        if (flag && flag1)
	        {
	            block.harvestBlock(theWorld, thisPlayerMP, blockX, blockY, blockZ, l);
	        }

	        // Drop experience
	        if (flag)
	        {
	            block.dropXpOnBlockBreak(theWorld, blockX, blockY, blockZ, getEXPDrop());
	        }
	        
	        if(this.currentFinder.isCreativeMode())
	        {
	        	thisPlayerMP.setCurrentItemOrArmor(0, replaced);
	        }
	        
	        return flag;
		}
		catch(Throwable t)
		{
			this.breakFailedError(t);
		}
		return false;
	}
	
	private int getEXPDrop()
	{
		try
		{
			if (	breakBlock == null || 
					!ForgeHooks.canHarvestBlock(breakBlock, clone.getPlayerInterface(), breakMeta) || 
					breakBlock.canSilkHarvest(clone.worldObj, clone.getPlayerInterface(), this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ, breakMeta) && 
					EnchantmentHelper.getSilkTouchModifier(clone)
				)
			{
				return 0;
			}
			else
	        {
				int meta = breakBlock.getDamageValue(clone.worldObj, this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ);
	            int bonusLevel = EnchantmentHelper.getFortuneModifier(clone);
	            return breakBlock.getExpDrop(clone.worldObj, meta, bonusLevel);
	        }
		}
		catch(Throwable t)
		{
			this.breakFailedError(t);
		}
		return 0;
	}
	
	
	public boolean removeBlock(int blockX, int blockY, int blockZ, boolean canHarvest)
    {
		try
		{
			EntityPlayer thisPlayerMP = this.clone.getPlayerInterface();
			World theWorld = this.clone.worldObj;
			
	        Block block = theWorld.getBlock(blockX, blockY, blockZ);
	        int l = theWorld.getBlockMetadata(blockX, blockY, blockZ);
	        block.onBlockHarvested(theWorld, blockX, blockY, blockZ, l, thisPlayerMP);
	        boolean flag = block.removedByPlayer(theWorld, thisPlayerMP, blockX, blockY, blockZ, canHarvest);

	        if (flag)
	        {
	            block.onBlockDestroyedByPlayer(theWorld, blockX, blockY, blockZ, l);
	        }

	        return flag;
		}
		catch(Throwable t)
		{
			breakFailedError(t);
		}
		return false;
    }
	
	public void breakFailedError(Throwable t)
	{
		if(currentFinder != null)
		{
			this.currentFinder.onFinished(this, breakCoord, breakItem, breakBlock, breakMeta);
		}
		this.stopBreakingBlock();
	}
	
	public void startBreakingBlock()
	{
		try
		{
			int blockX = this.breakCoord.posX;
			int blockY = this.breakCoord.posY;
			int blockZ = this.breakCoord.posZ;
			

			Block block = this.clone.worldObj.getBlock(blockX, blockY, blockZ);
	        boolean flag = block.getMaterial() != Material.air;

	        if (flag && this.curBlockDamageMP == 0.0F)
	        {
	            block.onBlockClicked(this.clone.worldObj, blockX, blockY, blockZ, this.clone.getPlayerInterface());
	        }

	        if (flag && (block.getPlayerRelativeBlockHardness(this.clone.getPlayerInterface(), this.clone.worldObj, blockX, blockY, blockZ) >= 1.0F) || currentFinder.isCreativeMode())
	        {
	            this.harvestBlock(blockX, blockY, blockZ);
	            this.currentFinder.onFinished(this, breakCoord, breakItem, breakBlock, breakMeta);
	        }
	        else
	        {
	            this.isHittingBlock = true;
	            
	            this.breakItem = this.clone.getHeldItem();
	            this.curBlockDamageMP = 0.0F;
	            this.stepSoundTickCounter = 0.0F;
	            this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getPlayerInterface().getEntityId(), this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
	        }
		}
		catch(Throwable t)
		{
			this.breakFailedError(t);
		}
		
	}
	
	

	/** Current block damage (MP) */
    private float curBlockDamageMP;
    /** Tick counter, when it hits 4 it resets back to 0 and plays the step sound */
    private float stepSoundTickCounter;
    /** Delays the first damage on the block after the first click on the block */
    private int blockHitDelay;
    /** Tells if the player is hitting a block */
    private boolean isHittingBlock;

}
