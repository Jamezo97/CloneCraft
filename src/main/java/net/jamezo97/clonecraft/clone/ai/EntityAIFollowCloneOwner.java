package net.jamezo97.clonecraft.clone.ai;

import java.util.Random;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;

public class EntityAIFollowCloneOwner extends EntityAIBase
{
	EntityClone clone;
	
	PathEntity lastSetPath = null;
	
	boolean followingPlayer = false;
	
//	boolean setoffsetPath = false;
	
    public EntityAIFollowCloneOwner(EntityClone entityMyPerson) {
		clone = entityMyPerson;
	}

	@Override
	public boolean shouldExecute() {
		if(clone != null && clone.getOptions() != null && clone.getOptions().follow != null && clone.getOptions().follow.get() && clone.getAttackTarget() == null && clone.getOwner() != null && !clone.getOptions().guard.get()){
			return true;
		}
		return false;
	}
	
	Random rand = new Random();

	@Override
	public boolean continueExecuting() {
		EntityPlayer owner = clone.getOwner();
		if(owner != null){
			double distance = clone.getDistanceSqToEntity(owner);
			if(distance > 400){
                int var1 = MathHelper.floor_double(owner.posX) - 2;
                int var2 = MathHelper.floor_double(owner.posZ) - 2;
                int var3 = MathHelper.floor_double(owner.boundingBox.minY);

                for (int var4 = 0; var4 <= 4; ++var4)
                {
                    for (int var5 = 0; var5 <= 4; ++var5)
                    {
                        /*if ((var4 < 1 || var5 < 1 || var4 > 3 || var5 > 3) && clone.worldObj.doesBlockHaveSolidTopSurface(clone.worldObj, var1 + var4, var3 - 1, var2 + var5) && !clone.worldObj.isBlockNormalCube(var1 + var4, var3, var2 + var5) && !clone.worldObj.isBlockNormalCube(var1 + var4, var3 + 1, var2 + var5))
                        {
                            clone.setLocationAndAngles((double)((float)(var1 + var4) + 0.5F), (double)var3, (double)((float)(var2 + var5) + 0.5F), clone.rotationYaw, clone.rotationPitch);
                            clone.getNavigator().clearPathEntity();
                            return true;
                        }*/
                    }
                }
			}else if(followingPlayer && distance < 4){
				double x = owner.posX + rand.nextInt(4)-2;
				double z = owner.posZ + rand.nextInt(4)-2;
				PathEntity path = clone.moveTo(x, owner.posY, z);
				followingPlayer = false;
			}else if(distance > 36 || (followingPlayer && lastSetPath == clone.getNavigator().getPath())){
				lastSetPath = clone.moveToEntity(owner);
				followingPlayer = true;
			}
		}
		return super.continueExecuting();
	}
	
	

    
    
}
