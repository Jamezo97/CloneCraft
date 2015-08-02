package net.jamezo97.clonecraft.clone.ai;

import java.util.Random;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class EntityAICloneWander extends EntityAIBase
{
    private EntityClone entity;
    private double xPosition;
    private double yPosition;
    private double zPosition;
    private float speed;

    public EntityAICloneWander(EntityClone entityClone, float speed)
    {
        this.entity = entityClone;
        this.speed = speed;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
	@Override
    public boolean shouldExecute()
    {
        if (this.entity.getAge() >= 100)
        {
            return false;
        }
        else if (this.entity.getRNG().nextInt(120) != 0)
        {
            return false;
        }
        else if (!this.entity.getOptions().wander.get())
        {
            return false;
        }
        else if(entity.getOptions().guard.get())
        {
        	return false;
        }
        else
        {
            Vec3 var1 = findRandomTargetBlock(this.entity, 10, 7, (Vec3)null);
            if (var1 == null)
            {
                return false;
            }
            else
            {
                this.xPosition = var1.xCoord;
                this.yPosition = var1.yCoord;
                this.zPosition = var1.zCoord;
                return true;
            }
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
	@Override
    public boolean continueExecuting()
    {
        return !this.entity.getNavigator().noPath() && entity.getOptions().wander.get() && !entity.getOptions().guard.get();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
	@Override
    public void startExecuting()
    {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
    
    private static Vec3 findRandomTargetBlock(EntityClone par0EntityCreature, int par1, int par2, Vec3 par3Vec3)
    {
        Random var4 = par0EntityCreature.getRNG();
        boolean var5 = false;
        int var6 = 0;
        int var7 = 0;
        int var8 = 0;
        float var9 = -99999.0F;
        boolean var10 = false;


        for (int var16 = 0; var16 < 10; ++var16)
        {
            int var12 = var4.nextInt(2 * par1) - par1;
            int var17 = var4.nextInt(2 * par2) - par2;
            int var14 = var4.nextInt(2 * par1) - par1;

            if (par3Vec3 == null || (double)var12 * par3Vec3.xCoord + (double)var14 * par3Vec3.zCoord >= 0.0D)
            {
                var12 += MathHelper.floor_double(par0EntityCreature.posX);
                var17 += MathHelper.floor_double(par0EntityCreature.posY);
                var14 += MathHelper.floor_double(par0EntityCreature.posZ);


                float var15 = par0EntityCreature.getRNG().nextFloat()*100;//par0EntityCreature.getBlockPathWeight(var12, var17, var14);

                if (var15 > var9)
                {
                    var9 = var15;
                    var6 = var12;
                    var7 = var17;
                    var8 = var14;
                    var5 = true;
                }
            }
        }

        if (var5)
        {
        	return Vec3.createVectorHelper((double)var6, (double)var7, (double)var8);
        }
        else
        {
            return null;
        }
    }
    
    
}
