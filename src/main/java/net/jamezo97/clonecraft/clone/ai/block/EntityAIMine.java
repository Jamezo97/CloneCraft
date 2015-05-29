package net.jamezo97.clonecraft.clone.ai.block;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

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
		return currentFinder != null && clone.getOptions().breakBlocks.get() && currentFinder.hasNextBlock();
	}
	
	public boolean selectNextBlock(int iterations)
	{
		
		return false;
	}

	@Override
	public boolean continueExecuting() {
		return breakCoord == null && shouldExecute();
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
	}

	/**
	 * The coordinate of the block being currently broken
	 */
	ChunkCoordinates breakCoord = null;
	
	Block breakBlock = null;
	
	/**
	 * The Item currently being used to destroy a block
	 */
    private ItemStack currentItemHittingBlock;
    
    public boolean sameItemAndBlock()
    {
    	Block currentBlock = clone.worldObj.getBlock(this.breakCoord.posX,  this.breakCoord.posY, this.breakCoord.posZ);
    	if(currentBlock != breakBlock)
    	{
    		return false;
    	}
    	ItemStack currentItem = clone.inventory.getCurrentItem();
    	if(currentItem == null && currentItemHittingBlock == null)
    	{
    		return true;
    	}
    	else if(currentItem != null && currentItemHittingBlock != null)
    	{
    		return currentItem.isItemEqual(currentItemHittingBlock);
    	}
    	return false;
    }
	
	@Override
	public void updateTask() {
		if(breakCoord != null)
		{
			this.continueBreakingBlock();
		}
		
		if(breakCoord == null && this.currentFinder.hasNextBlock())
		{
			//Try to find a block 3 times.
			for(int a = 0; a < 3; a++)
			{
				ChunkCoordinates cc = this.currentFinder.getNextBlock(this);
				Block break_block = this.clone.worldObj.getBlock(cc.posX, cc.posY, cc.posZ);
				boolean canBreak = this.clone.selectBestItemForBlock(break_block);
				
				if(canBreak)
				{
					this.breakCoord = cc;
					
					if(this.currentFinder.mustBeCloseToBreak())
					{
						clone.setPath(clone.getNavigator().getPathToXYZ(cc.posX, cc.posY, cc.posZ));
					}
					else
					{
						startBreakingBlock();
					}
					this.breakBlock = break_block;
					break;
				}
				else
				{
					this.currentFinder.cantBreakBlock(cc, break_block);
				}
			}
		}
	}
	
	public void continueBreakingBlock()
	{
		
		//Flag is true if we're close enough to break the block
		boolean closeFlag = !currentFinder.mustBeCloseToBreak() || 
				clone.getDistanceSq(breakCoord.posX+0.5, breakCoord.posY+0.5, breakCoord.posZ+0.5) <= 10;
		
//		System.out.println(closeFlag);
		
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
		if(sameItemAndBlock() && this.isHittingBlock)
		{
			if(blockHitDelay > 0)
			{
				--blockHitDelay;
			}
			else
			{
				EntityPlayer thePlayer = clone.getPlayerInterface();
				
				Block block = clone.worldObj.getBlock(breakCoord.posX, breakCoord.posY, breakCoord.posZ);
				
				this.curBlockDamageMP += block.getPlayerRelativeBlockHardness(thePlayer, thePlayer.worldObj, breakCoord.posX, breakCoord.posY, breakCoord.posZ);
				
				this.clone.swingItem();
				
                if (this.stepSoundTickCounter % 4.0F == 0.0F)
                {
                	
                    //this.mc.getSoundHandler().playSound(new PositionedSoundRecord(new ResourceLocation(block.stepSound.getStepResourcePath()), (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getPitch() * 0.5F, (float)blockToBreak.posX + 0.5F, (float)blockToBreak.posY + 0.5F, (float)blockToBreak.posZ + 0.5F));
                }

                ++this.stepSoundTickCounter;

                if (this.curBlockDamageMP >= 1.0F)
                {
                    this.isHittingBlock = false;
                    //clone.sendToAllWatching(new C07PacketPlayerDigging(2, blockToBreak.posX, blockToBreak.posY, blockToBreak.posZ, /*side*/2));
                    //this.destroyBlock(blockToBreak.posX, blockToBreak.posY, blockToBreak.posZ);
                    
                    this.harvestBlock(breakCoord.posX, breakCoord.posY, breakCoord.posZ);
                    
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.blockHitDelay = 5;
                    
                    this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getEntityId(), breakCoord.posX, breakCoord.posY, breakCoord.posZ, (int)(this.curBlockDamageMP * 10.0F) - 1);

                    
                    
                    this.breakCoord = null;
                    this.currentItemHittingBlock = null;
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
        if (this.isHittingBlock)
        {
//            this.clone.sendToAllWatching(new C07PacketPlayerDigging(1, this.blockToBreak.posX, this.blockToBreak.posY, this.blockToBreak.posZ, -1));
        }

        this.isHittingBlock = false;
        this.curBlockDamageMP = 0.0F;
        
        if(breakCoord != null)
        {
        	this.clone.worldObj.destroyBlockInWorldPartially(this.clone.getEntityId(), this.breakCoord.posX, this.breakCoord.posY, this.breakCoord.posZ, -1);
            this.breakCoord = null;
        }
        
        this.breakBlock = null;
        
        this.currentItemHittingBlock = null;
    }
	//From ItemInWorldManager
	public boolean harvestBlock(int p_73084_1_, int p_73084_2_, int p_73084_3_)
	{
		EntityPlayer thisPlayerMP = this.clone.getPlayerInterface();
		World theWorld = this.clone.worldObj;
		
		ItemStack stack = thisPlayerMP.getCurrentEquippedItem();
        if (stack != null && stack.getItem().onBlockStartBreak(stack, p_73084_1_, p_73084_2_, p_73084_3_, thisPlayerMP))
        {
            return false;
        }
        Block block = theWorld.getBlock(p_73084_1_, p_73084_2_, p_73084_3_);
        int l = theWorld.getBlockMetadata(p_73084_1_, p_73084_2_, p_73084_3_);
        theWorld.playAuxSFXAtEntity(thisPlayerMP, 2001, p_73084_1_, p_73084_2_, p_73084_3_, Block.getIdFromBlock(block) + (theWorld.getBlockMetadata(p_73084_1_, p_73084_2_, p_73084_3_) << 12));
        boolean flag = false;

        {
            ItemStack itemstack = thisPlayerMP.getCurrentEquippedItem();
            boolean flag1 = block.canHarvestBlock(thisPlayerMP, l);

            if (itemstack != null)
            {
                itemstack.func_150999_a(theWorld, block, p_73084_1_, p_73084_2_, p_73084_3_, thisPlayerMP);

                if (itemstack.stackSize == 0)
                {
                    thisPlayerMP.destroyCurrentEquippedItem();
                }
            }

            flag = removeBlock(p_73084_1_, p_73084_2_, p_73084_3_, flag1);
            if (flag && flag1)
            {
                block.harvestBlock(theWorld, thisPlayerMP, p_73084_1_, p_73084_2_, p_73084_3_, l);
            }
        }

        // Drop experience
        if (flag)
        {
            block.dropXpOnBlockBreak(theWorld, p_73084_1_, p_73084_2_, p_73084_3_, /*event.getExpToDrop()*/10);
        }
        return flag;
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
            this.currentItemHittingBlock = this.clone.getHeldItem();
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
