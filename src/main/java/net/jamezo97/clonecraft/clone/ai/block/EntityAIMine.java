package net.jamezo97.clonecraft.clone.ai.block;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;

public class EntityAIMine extends EntityAIBase{

	
	/** Once all blocks have been minded from this finder, set this to null, and call
	 *  the on finished method in the interface
	 */
	BlockFinder currentFinder = null;
	
	EntityClone clone = null;
	
	Minecraft mc = Minecraft.getMinecraft();
	
	public EntityAIMine(EntityClone clone) {
		this.clone = clone;
		//00000101
		this.setMutexBits(5);
	}
	
	public EntityClone getClone(){
		return this.clone;
	}
	
	public void setBlockFinder(BlockFinder finder)
	{
		this.currentFinder = finder;
	}

	@Override
	public boolean shouldExecute() {
		return currentFinder != null && clone.getOptions().breakBlocks.get() && selectNextBlock(5);
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
			
			boolean canBreak = this.clone.selectBestItemForBlock(theCoords, theBlock, theMeta);
			
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
				this.currentFinder.cantBreakBlock(theCoords, theBlock);
			}
		}
		return false;
	}
	/**/

	@Override
	public boolean continueExecuting() {
		return breakCoord != null && breakBlock != null && breakMeta != -1;
	}

	@Override
	public void startExecuting() {
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
		breakCoord = null;
		breakBlock = null;
		breakItem = null;
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
				clone.getDistanceSq(breakCoord.posX+0.5, breakCoord.posY+0.5, breakCoord.posZ+0.5) <= 10;
		
		if(!closeFlag && clone.ticksExisted % 5 == 0)
		{
			clone.setPath(clone.getNavigator().getPathToXYZ(breakCoord.posX, breakCoord.posY, breakCoord.posZ));
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

                    this.stopBreakingBlock();
                    
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                    
                    clone.inventory.currentItem = 0;
                    lastTickTime = -1;
                    this.currentFinder.onFinished(this, breakCoord, breakItem, breakBlock, breakMeta);
                }
                else
                {
                	this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getEntityId(), breakCoord.posX, breakCoord.posY, breakCoord.posZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
                }
			}
		}
		else
		{
			stopBreakingBlock();
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
    }
	
	//From ItemInWorldManager
	public boolean harvestBlock(int blockX, int blockY, int blockZ)
	{
		EntityPlayer thisPlayerMP = this.clone.getPlayerInterface();
		World theWorld = this.clone.worldObj;
		
		ItemStack stack = thisPlayerMP.getCurrentEquippedItem();
        
		if (stack != null && stack.getItem().onBlockStartBreak(stack, blockX, blockY, blockZ, thisPlayerMP))
        {
            return false;
        }
        
        Block block = theWorld.getBlock(blockX, blockY, blockZ);
        int l = theWorld.getBlockMetadata(blockX, blockY, blockZ);
        theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, blockX, blockY, blockZ, Block.getIdFromBlock(block) + (theWorld.getBlockMetadata(blockX, blockY, blockZ) << 12));
        boolean flag = false;

        ItemStack itemstack = thisPlayerMP.getCurrentEquippedItem();
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
        
        if (flag && flag1)
        {
            block.harvestBlock(theWorld, thisPlayerMP, blockX, blockY, blockZ, l);
        }

        // Drop experience
        if (flag)
        {
            block.dropXpOnBlockBreak(theWorld, blockX, blockY, blockZ, getEXPDrop());
        }
        return flag;
	}
	
	private int getEXPDrop()
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
	
	
	private boolean removeBlock(int blockX, int blockY, int blockZ, boolean canHarvest)
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
	
	
	public void startBreakingBlock(){
		
		int blockX = this.breakCoord.posX;
		int blockY = this.breakCoord.posY;
		int blockZ = this.breakCoord.posZ;
		
		
		if (this.isHittingBlock)
        {
//            this.clone.sendToAllWatching(new C07PacketPlayerDigging(1, this.blockToBreak.posX, this.blockToBreak.posY, this.blockToBreak.posZ, side));
        }

//        this.clone.sendToAllWatching(new C07PacketPlayerDigging(0, blockX, blockY, blockZ, side));
        Block block = this.clone.worldObj.getBlock(blockX, blockY, blockZ);
        boolean flag = block.getMaterial() != Material.air;

        if (flag && this.curBlockDamageMP == 0.0F)
        {
            block.onBlockClicked(this.clone.worldObj, blockX, blockY, blockZ, this.clone.getPlayerInterface());
        }

        if (flag && block.getPlayerRelativeBlockHardness(this.clone.getPlayerInterface(), this.clone.worldObj, blockX, blockY, blockZ) >= 1.0F)
        {
            this.destroyBlock(blockX, blockY, blockZ);
        }
        else
        {
            this.isHittingBlock = true;
            /*this.blockToBreak.posX = blockX;
            this.blockToBreak.posY = blockY;
            this.blockToBreak.posZ = blockZ;*/
            this.breakItem = this.clone.getHeldItem();
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getPlayerInterface().getEntityId(), this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ, (int)(this.curBlockDamageMP * 10.0F) - 1);
        }
	}
	
	
	/** PosX of the current block being destroyed *//*
    private int currentBlockX = -1;
    *//** PosY of the current block being destroyed *//*
    private int currentBlockY = -1;
    *//** PosZ of the current block being destroyed *//*
    private int currentblockZ = -1;*/
    
	public boolean destroyBlock(int posX, int posY, int posZ)
    {
        ItemStack stack = this.clone.getCurrentEquippedItem();
        if (stack != null && stack.getItem() != null && stack.getItem().onBlockStartBreak(stack, posX, posY, posZ, this.clone.getPlayerInterface()))
        {
            return false;
        }
        
        World world = this.clone.worldObj;
        Block block = world.getBlock(posX, posY, posZ);

        if (block.getMaterial() == Material.air)
        {
            return false;
        }
        else
        {
            world.playAuxSFX(2001, posX, posY, posZ, Block.getIdFromBlock(block) + (world.getBlockMetadata(posX, posY, posZ) << 12));
            int i1 = world.getBlockMetadata(posX, posY, posZ);
            boolean flag = block.removedByPlayer(world, this.clone.getPlayerInterface(), posX, posY, posZ);

            if (flag)
            {
                block.onBlockDestroyedByPlayer(world, posX, posY, posZ, i1);
            }

//            this.blockToBreak.posY = -1;

            ItemStack itemstack = this.clone.getCurrentEquippedItem();

            if (itemstack != null)
            {
                itemstack.func_150999_a(world, block, posX, posY, posZ, this.clone.getPlayerInterface());

                if (itemstack.stackSize == 0)
                {
                    this.clone.destroyCurrentEquippedItem();
               }
            }

            return flag;
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
