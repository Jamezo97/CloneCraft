package net.jamezo97.clonecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.gui.container.ContainerCentrifuge;
import net.jamezo97.clonecraft.network.Handler1CentrifugeItemStacks;
import net.jamezo97.clonecraft.network.HandlerPacket;
import net.jamezo97.clonecraft.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;


public class TileEntityCentrifuge extends TileEntityFueled implements ISidedInventory{
	
	public boolean canStart = false;
	
	public float speed;
	public float maxSpeed = 4F;
	public float spin = 0F, lastSpin = 0F;
	
	public float lidAngle;
	
    public float prevLidAngle;

	public TileEntityCentrifuge() {
		//8 + fuel
		super(9);
		this.notifiers = NOTIFY_COOKDONE;
	}

	@Override
	public String getTileEntityName() {
		return "Centrifuge";
	}

	@Override
	public int getFuelSlot() {
		return 0;
	}

	@Override
	public int getTimeToComplete() {
		return CloneCraft.INSTANCE.config.DEBUG_ENABLED?200:1600;
	}
	
	@Override
	public boolean canStart() 
	{
		if(canStart)
		{
			canStart = false;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean canPerformAction() 
	{
		if(stop)
		{
			stop = false;
			return false;
		}
		
		int count = 0;
		
		for(int a = 1; a < items.length; a++)
		{
			if(items[a] != null && items[a].stackSize > 0)
			{
				if(items[a].getItem() == CloneCraft.INSTANCE.itemTestTube && items[a].getItemDamage() == 1 && items[a].stackSize == 1)
				{
					count++;	
				}
				else if(items[a].getItemDamage() != 2)
				{
					return false;
				}
			}
		}
		return count > 0;
	}

	@Override
	public boolean shouldRefresh(Block oldBlock, Block newBlock, int oldMeta, int newMeta, World world, int x, int y, int z)
    {
        return oldBlock != newBlock;
    }
	
	AxisAlignedBB playerSearch;

	@Override
	public void validate()
	{
		super.validate();
		updateSearch();
	}
	 
	public void updateSearch(){
		playerSearch = AxisAlignedBB.getBoundingBox(xCoord+0.5 - 32, yCoord+0.5 - 32, zCoord+0.5 - 32, xCoord+0.5 + 32, yCoord+0.5 + 32, zCoord+0.5 + 32);
	}
	
	 
	List lastPlayerList = null;
	
	public void checkSendItems()
	{
		if(playerSearch != null)
		{
			List l = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, playerSearch);
			
			if(lastPlayerList == null || lastPlayerList.size() != l.size())
			{
				Handler1CentrifugeItemStacks handler = new Handler1CentrifugeItemStacks(xCoord, yCoord, zCoord, this.items);
				HandlerPacket p = handler.getPacket();
				
				for(int a = 0; a < l.size(); a++)
				{
					if(lastPlayerList == null || !lastPlayerList.contains(l.get(a)))
					{
						PacketHandler.net.sendTo(p, ((EntityPlayerMP)l.get(a)));
					}
				}
				lastPlayerList = l;
			}
		}
	}
	
	public void sendItemsToAround()
	{
		Handler1CentrifugeItemStacks handler = new Handler1CentrifugeItemStacks(xCoord, yCoord, zCoord, this.items);
		handler.sendToAllNear(xCoord+0.5, yCoord+0.5, zCoord+0.5, 32, worldObj.provider.dimensionId);
	}
	
//	EntityPlayer exclude = null;
	
	public void sendItemsToAroundExcluding(EntityPlayer player)
	{
		Handler1CentrifugeItemStacks handler = new Handler1CentrifugeItemStacks(xCoord, yCoord, zCoord, this.items);
		List l = worldObj.getEntitiesWithinAABB(EntityPlayerMP.class, playerSearch);
		
		if(l.size() > 0)
		{
			HandlerPacket p = handler.getPacket();
			
			for(int a = 0; a < l.size(); a++)
			{
				if(player != l.get(a)){
					PacketHandler.net.sendTo(p, ((EntityPlayerMP)l.get(a)));
				}
			}
		}
	}
	

	@Override
	public void performAction() 
	{
		for(int a = 1; a < items.length; a++)
		{
			if(items[a] != null && items[a].stackSize > 0)
			{
				items[a].setItemDamage(2);
			}
		}
	}

	boolean stop = false;
	
	public void setCanStart() 
	{
		if(cookTime == 0)
		{
			if(canPerformAction() && (this.canBurnNewItem() || this.isBurning()))
			{
				canStart = true;
			}
		}
		else
		{
			stop = true;
		}
	}
	
	boolean wasSpinning = false;
	float decFactor = 0;
	
	int numPlayersUsing = 0;
	
	int ticksSinceSync = 0;
	
	int tick = 0;
	
	boolean forceUpdateToSurrounding = false;
	
	@Override
	public void updateEntity() 
	{
		tick++;
		super.updateEntity();
		
		if (this.cookTime > 0) 
		{
			if(speed == 0)
			{
				maxSpeed = 3-this.worldObj.rand.nextFloat()/2;
			}
			
			wasSpinning = true;
			
			if(speed <= 0)
			{
				speed = .2f;
			}
			
			if(speed < maxSpeed)
			{
				speed *= 1.1f;
			}
			
			if(speed > maxSpeed)
			{
				speed = maxSpeed;
			}
		} 
		else 
		{
			if(wasSpinning)
			{
				float nextStop = (float)Math.round(spin / 3.0F) * 3 + (3 * Math.round(speed*speed*8));
				float displacement = nextStop - spin;
				float velocity = speed;
				decFactor = getDecreasingSpeed(velocity, displacement);
				wasSpinning = false;
			}
			
			if(speed > 0)
			{
				speed -= decFactor;
				
				if(speed < 0)
				{
					speed = 0;
					float nextStop = (float)Math.round(spin / 3.0F) * 3;
					
					spin = nextStop;
					
				}
			}
		}
		lastSpin = spin;
		
		if(speed > 0)
		{
			spin += speed;
			
			if(this.cookTime == 0)
			{
					//Because the velocity goes down in jumps, it doesn't follow the line of regression perfectly
					//And thus we need to remove the top triangles from the decrements
/*
        __   
       | /|		We must remove the top triangles to make a straight line of regression.
      _|/ |		Each drop is = 'decFactor', and each bar accross is just one unit of time (1 tick)
     | /  |		Thus remove half of (decFactor * 1), which is just half of decfactor
    _|/   |		
   | /    |	Vel	, Area under = V*t, v = d/t, d/t * t = d, Area under = d, displacements
  _|/     |		
 | /      |		However I just realized I drew the line on the wrong side, it should follow the outter points
_|/       |		Thus why in the code below, we add on decFactor (I got that right with trial and error haha
 /        |		
/_________|		And the code to calculate dec factor came from v^2=u^2 + 2ax
	Time		Initial velocity = 0, rearrage to get a, which is v^2 / 2x. Constant acceleration formula Coooooool stuff
*/
					
				spin += (decFactor/2);
			}
			
			if(spin > 24 && lastSpin > 24)
			{
				float remove = (float)(Math.floor(lastSpin/24)*24);
				spin -= remove;
				lastSpin -= remove;
			}
		}
		++this.ticksSinceSync;
        float f;

        if (!this.worldObj.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + this.xCoord + this.yCoord + this.zCoord) % 200 == 0)
        {
            this.numPlayersUsing = 0;
            f = 5.0F;
            List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox((double)((float)this.xCoord - f), (double)((float)this.yCoord - f), (double)((float)this.zCoord - f), (double)((float)(this.xCoord + 1) + f), (double)((float)(this.yCoord + 1) + f), (double)((float)(this.zCoord + 1) + f)));
            Iterator iterator = list.iterator();

            while (iterator.hasNext())
            {
                EntityPlayer entityplayer = (EntityPlayer)iterator.next();
                if (entityplayer.openContainer instanceof ContainerCentrifuge){
                    IInventory iinventory = ((ContainerCentrifuge)entityplayer.openContainer).tE;

                    if (iinventory == this)
                    {
                        ++this.numPlayersUsing;
                    }
                }
            }
            sendClientInfo(20, this.numPlayersUsing);
        }
		
		this.prevLidAngle = this.lidAngle;
		f = 0.1F;
        double d2;
        boolean flag = isPlayerInFront();
        
        boolean shouldCloseLid = ((this.numPlayersUsing == 0 && cookTime>0) || !flag);
        boolean shouldOpenLid = this.numPlayersUsing > 0 || cookTime == 0 || flag;
        
        if ((shouldOpenLid) && this.lidAngle == 0.0F)
        {
            double d1 = (double)this.xCoord + 0.5D;
            d2 = (double)this.zCoord + 0.5D;

            this.worldObj.playSoundEffect(d1, (double)this.yCoord + 0.5D, d2, "clonecraft:block.open", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if ((shouldCloseLid && this.lidAngle > 0.0F ) || (shouldOpenLid && this.lidAngle < 1.0F))
        {
            float f1 = this.lidAngle;

            if (shouldOpenLid)
            {
                this.lidAngle += f;
            }
            else if(shouldCloseLid)
            {
                this.lidAngle -= f;
            }

            if (this.lidAngle > 1.0F)
            {
                this.lidAngle = 1.0F;
            }

            float f2 = 0.5F;

            if (this.lidAngle < f2 && f1 >= f2)
            {
                d2 = (double)this.xCoord + 0.5D;
                double d0 = (double)this.zCoord + 0.5D;

                this.worldObj.playSoundEffect(d2, (double)this.yCoord + 0.5D, d0, "clonecraft:block.close", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (this.lidAngle < 0.0F)
            {
                this.lidAngle = 0.0F;
            }
        }
        if(worldObj.isRemote)
        {
        	if((cookTime>0 || speed > 0.1))
        	{
        		worldObj.playSound(xCoord+0.5, yCoord+0.5, zCoord+0.5, "clonecraft:block.spin", ((float)speed / (float)maxSpeed)*0.6f, 0.9f + ((float)speed / (float)maxSpeed)*1.1F, false);
        		
        		int blockMetadataAND = this.blockMetadata & 3;
        		
        		if(tick % 1 == 0)
        		{
        			worldObj.spawnParticle("smoke", xCoord + ventPositionsX[blockMetadataAND], yCoord+0.625, zCoord + ventPositionsZ[blockMetadataAND], 0.0D, 0.2D, 0.0D);
        		}
        		if(tick % 2 == 0){
        			worldObj.spawnParticle("flame", xCoord + ventPositionsX[blockMetadataAND], yCoord+0.625, zCoord + ventPositionsZ[blockMetadataAND], 0.0D, 0.09D, 0.0D);
        		}
        		
        	}
        	else if(isBurning())
        	{
        		int blockMetadataAND = this.blockMetadata & 3;
        		
        		if(tick % 4 == 0)
        		{
        			worldObj.spawnParticle("smoke", xCoord + ventPositionsX[blockMetadataAND], yCoord+0.625, zCoord + ventPositionsZ[blockMetadataAND], 0.0D, 0.05D, 0.0D);
        		}
        		if(tick % 5 == 0)
        		{
        			worldObj.spawnParticle("flame", xCoord + ventPositionsX[blockMetadataAND], yCoord+0.625, zCoord + ventPositionsZ[blockMetadataAND], 0.0D, 0.025D, 0.0D);
        		}
			}
		}
        else
		{
			checkSendItems();
			
			if(this.cookTime == 1)
			{
				this.sendClientInfo(21, 1);
			}
		}
		
		if(forceUpdateToSurrounding)
		{
			forceUpdateToSurrounding = false;
			this.sendItemsToAround();
		}
			
		
	}
	
	double ventPositionsX[] = {0.1875, 0.3125, 0.8125, 0.6875};
	double ventPositionsZ[] = {0.6875, 0.1875, 0.3125, 0.8125};
	
	int[] blockXOffsets = {0, -1, 0, +1};
	
	int[] blockZOffsets = {+1, 0, -1, 0};
	
	private boolean isPlayerInFront() 
	{
		double radius = 1.2;
		List list = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getBoundingBox(this.xCoord+0.5-radius, this.yCoord+0.5-radius, this.zCoord+0.5-radius, this.xCoord+0.5+radius, this.yCoord+0.5+radius, this.zCoord+0.5+radius));
		int i = this.blockMetadata & 3;
		
		if(i >= 0 && i < 4)
		{
			int x = xCoord + blockXOffsets[i];
			int z = zCoord + blockZOffsets[i];
			EntityPlayer e;
			
			for(int a = 0; a < list.size(); a++)
			{
				e = (EntityPlayer)list.get(a);
				
				if(!((Math.floor(e.posX) - x) == 0 && (Math.floor(e.posZ)-z) == 0))
				{
					return true;
				}
			}
		}
		
		return false;
	}

	//Thankyou constant acceleration formulas!
	public float getDecreasingSpeed(float velocity, float displacement)
	{
		return (velocity*velocity) / (2 * displacement);
	}
	
	public float interpolate(float partial, float val1, float val2)
	{
		return val1 + ((val2-val1)*partial);
	}

	public boolean receiveClientEvent(int key, int val)
    {
		if (key == 20)
		{
            this.numPlayersUsing = val;
            return true;
        }
		else if (key == 21)
        {
            this.cookTime = val;
            return true;
        }
		else
        {
            return super.receiveClientEvent(key, val);
        }
    }
	
	public void openInventory()
    {
        if (this.numPlayersUsing < 0)
        {
            this.numPlayersUsing = 0;
        }
        
        ++this.numPlayersUsing;
        sendClientInfo(20, this.numPlayersUsing);
    }

    public void closeInventory()
    {
    	--this.numPlayersUsing;
        sendClientInfo(20, this.numPlayersUsing);
    }
    
	public void toggleOnOffFromPower()
	{
		setCanStart();
	}

	@Override
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
					
					EntityItem entityitem = new EntityItem(this.getWorldObj(),
							(double) ((float) this.xCoord + f),
							(double) ((float) this.yCoord + f1),
							(double) ((float) this.zCoord + f2), new ItemStack(
									itemstack.getItem(), j1,
									itemstack.getItemDamage()));

					if (itemstack.hasTagCompound()) 
					{
						entityitem.getEntityItem().setTagCompound(
								(NBTTagCompound) itemstack.getTagCompound()
										.copy());
					}
					
					if(i1 == 0 || this.speed < 0.05f)
					{
						float f3 = 0.05F;
						entityitem.motionX = (double) ((float) rand.nextGaussian() * f3);
						entityitem.motionY = (double) ((float) rand.nextGaussian() * f3 + 0.2F);
						entityitem.motionZ = (double) ((float) rand.nextGaussian() * f3);
					}
					else
					{
						 float f3 = this.speed/4.0f;
						 double x = Math.cos((i1-1)/4.0d * Math.PI)*f3*(rand.nextGaussian()/20+0.95);
						 double z = Math.sin((i1-1)/4.0d * Math.PI)*f3*(rand.nextGaussian()/20+0.95);
						 entityitem.motionX = (double) ((float) x);
						 entityitem.motionY = (double) ((float) rand.nextGaussian()/10+0.9);
						 entityitem.motionZ = (double) ((float) z);
					}
					
					
					this.getWorldObj().spawnEntityInWorld(entityitem);
				}
			}
		}
	}
	//All the slots
	private static int[] allSlots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
	//Test Tube slots
	private static int[] tubeSlots = new int[]{1, 2, 3, 4, 5, 6, 7, 8};

	@Override
	public int[] getAccessibleSlotsFromSide(int side)
	{
		return side == 0 ? tubeSlots : allSlots;
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side)
	{
		if((slot == 0 || (cookTime == 0 && speed == 0)) && this.isItemValidForSlot(slot, stack) && (slot == 0 || this.items[slot] == null))
		{
			forceUpdateToSurrounding = true;
			return true;
		}
		
		return false;
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side)
	{
		return cookTime == 0 && speed == 0 && slot != 0 && stack.getItemDamage() == 2;
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack)
	{
		if(slot == 0)
		{
			return TileEntityFurnace.isItemFuel(stack);
		}
		else
		{
			return stack.getItem() == CloneCraft.INSTANCE.itemTestTube && stack.getItemDamage() == 1;
		}
	}

	/**
	 * A value between 0 and 15
	 * @param side 
	 * @param tileEntity
	 * @return
	 */
	public int calcRedstoneFromInventory(int side)
	{
		//TestTube Side. (LHS)
		int rotatedSide = ((this.blockMetadata & 3) + 3) % 4;
		
		if(side == rotatedSide)
		{
			int total = 0;
			
			for(int a = 1; a < 9; a++)
			{
				if(items[a] != null)
				{
					if(items[a].getItem() == CloneCraft.INSTANCE.itemTestTube && items[a].getItemDamage() == 1)
					{
						total++;
					}
					
				}
			}
			
			return total;
		}
		
		//Fuel Side (RHS)
		rotatedSide = ((this.blockMetadata & 3) + 1) % 4;
		
		if(side == rotatedSide)
		{
			if(this.items[0] != null)
			{
				return (int)Math.min(Math.ceil(this.items[0].stackSize / ( (float) this.items[0].getMaxStackSize()) * 15), 15);
			}
			return 0;
		}
		
		int total = 0;
		
		//15-8 = 7.
		
		if(this.items[0] != null)
		{
			total += Math.round(this.items[0].stackSize / ( (float) this.items[0].getMaxStackSize()) * 7);
		}
		
		for(int a = 1; a < 9; a++)
		{
			if(items[a] != null)
			{
				total++;
			}
		}
		
		//Just in case my math is wrong.
		return Math.min(total, 15);
	}


	
	
	
	

	

}
