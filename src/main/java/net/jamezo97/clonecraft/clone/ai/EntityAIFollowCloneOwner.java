package net.jamezo97.clonecraft.clone.ai;

import java.util.Random;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowCloneOwner extends EntityAIBase
{
	EntityClone clone;
	
	PathEntity lastSetPath = null;
	
	boolean followingPlayer = false;
	
    public EntityAIFollowCloneOwner(EntityClone entityMyPerson)
    {
		clone = entityMyPerson;
		this.setMutexBits(1);
	}
    

	@Override
	public boolean shouldExecute()
	{
		if(clone.getOptions().follow.get() && clone.getOwner() != null && !clone.getOptions().guard.get() && !clone.getBuildAI().isRunning())
		{
			EntityPlayer owner = clone.getOwner();
			
			double distance = clone.getDistanceSqToEntity(owner);
			
			return distance > 50;
		}
		return false;
	}
	
	Random rand = new Random();
	
	//A timer which increments whilst the player is being followed. Reset when not.
	int followingCount = 0;
	
//	int runningTick = 0;
	
	

//	@Override
//	public void startExecuting() {
//		runningTick = 0;
//	}
//
//
//	@Override
//	public void resetTask() {
//		runningTick = 0;
//	}

	//long timer = 0;
	
	@Override
	public boolean continueExecuting() {
		EntityPlayer owner = clone.getOwner();
		if(owner != null)
		{
			double distance = clone.getDistanceSqToEntity(owner);

//			System.out.println("Look");
			
			this.clone.getLookHelper().setLookPositionWithEntity(owner, 16.0f, clone.getVerticalFaceSpeed());
			
			//30 blocks (30*30 = 900)
			if(distance > 900)
			{
				//If Clone is really far away, teleport.
                int var1 = MathHelper.floor_double(owner.posX) - 2;
                int var2 = MathHelper.floor_double(owner.posZ) - 2;
                int var3 = MathHelper.floor_double(owner.boundingBox.minY);

                for (int var4 = 0; var4 <= 4; ++var4)
                {
                    for (int var5 = 0; var5 <= 4; ++var5)
                    {
                        if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && clone.worldObj.doesBlockHaveSolidTopSurface(clone.worldObj, var1 + var4, var3 - 1, var2 + var5) && !clone.worldObj.isBlockNormalCubeDefault(var1 + var4, var3, var2 + var5, false) && !clone.worldObj.isBlockNormalCubeDefault(var1 + var4, var3 + 1, var2 + var5, false))
                        {
                            clone.setLocationAndAngles((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), clone.rotationYaw, clone.rotationPitch);
                            clone.getNavigator().clearPathEntity();
                            followingCount = 0;
                            //Stop executing, the clone must be close enough now.
                            return false;
                        }
                    }
                }
			}
			else if(distance > 49 && followingCount++ >= 10/* || (followingPlayer && (lastSetPath != clone.getNavigator().getPath() || followingCount % 10 == 0))*/)
			{		
				long l1 = System.currentTimeMillis();
				double x = owner.posX + rand.nextInt(4)-2;
				double y = owner.posY;
				double z = owner.posZ + rand.nextInt(4)-2;
				
				int blockX = (int)Math.floor(x);
				int blockY = (int)Math.floor(y);
				y = -1;
				int blockZ = (int)Math.floor(z);
				
				for(int a = 0; a < 5; a++)
				{
					if(World.doesBlockHaveSolidTopSurface(clone.worldObj, blockX, blockY+a, blockZ))
					{
						y = blockY + a + 1;
						break;
					}
					if(World.doesBlockHaveSolidTopSurface(clone.worldObj, blockX, blockY-a, blockZ))
					{
						y = blockY - a + 1;
						break;
					}
				}
				
				
				
				if(y >= 1)
				{
					lastSetPath = clone.moveTo(x, y, z);
					followingCount = 0;
					followingPlayer = true;
				}
				else
				{
					followingCount = 5;
					followingPlayer = false;
				}
				long l2 = System.currentTimeMillis();
				
				l2 -= l1;
				//timer += l2;
			}
			return clone.getOptions().follow.get() && clone.getOwner() != null && !clone.getOptions().guard.get() && distance > 16;
		}
		else
		{
			return false;
		}
	}


	
	
	
	

    
    
}
