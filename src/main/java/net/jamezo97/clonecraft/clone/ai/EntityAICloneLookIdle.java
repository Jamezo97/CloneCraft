package net.jamezo97.clonecraft.clone.ai;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAICloneLookIdle extends EntityAIBase
{
    /** The entity that is looking idle. */
    private EntityClone idleEntity;
    /** X offset to look at */
    private double lookX;
    /** Z offset to look at */
    private double lookY;
    /** Z offset to look at */
    private double lookZ;
    
    private EntityLivingBase watchEntity;
    
    /** A decrementing tick that stops the entity from being idle once it reaches 0. */
    private int idleTime;
    private static final String __OBFID = "CL_00001607";

    public EntityAICloneLookIdle(EntityClone p_i1647_1_)
    {
        this.idleEntity = p_i1647_1_;
        this.setMutexBits(1);
    }

    /**
     * Returns whether the EntityAIBase should begin execution.
     */
    public boolean shouldExecute()
    {
        return this.idleEntity.getOptions().curious.get() && this.idleEntity.getRNG().nextFloat() < 0.1F;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.idleTime >= 0;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting()
    {
    	if(this.idleEntity.getRNG().nextFloat() > 0.2f)
    	{
    		List list = this.idleEntity.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.idleEntity.boundingBox.expand(8, 3, 8));
    		
    		EntityLivingBase closest = null;
    		
    		boolean playerSet = false;
    		
    		double distSq = Double.MAX_VALUE;
    		
    		for(int a = 0; a < list.size(); a++)
    		{
    			EntityLivingBase entity = (EntityLivingBase)list.get(a);
    			if(list.get(a) != idleEntity)
    			{
    				boolean isPlayer = EntityPlayer.class.isAssignableFrom(entity.getClass());
    				
    				if(isPlayer)
    				{
    					isPlayer = this.idleEntity.getRNG().nextFloat() > 0.7f;
    				}
    				
    				if(closest == null || (isPlayer && !playerSet ) )
    				{
    					if(this.idleEntity.canEntityBeSeen(entity))
    					{
    						closest = entity;
        					distSq = closest.getDistanceSqToEntity(idleEntity);
        					playerSet = isPlayer;
    					}
    					
    				}
    				else
    				{
    					double dist = entity.getDistanceSqToEntity(idleEntity);
    					
    					if(dist < distSq)
    					{
    						if(!playerSet || isPlayer)
    						{
    							if(this.idleEntity.canEntityBeSeen(entity))
    							{
    								closest = entity;
            						distSq = dist;
    							}
    						}
    					}
    				}
    			}
    		}
    		
    		if(closest != null)
    		{
    			this.watchEntity = closest;
    			this.idleTime = 100 + this.idleEntity.getRNG().nextInt(60);
    			return;
    		}

   	     	
    	}
    	if(this.idleEntity.getRNG().nextFloat() > 0.6f)
    	{
    		 double d0 = (Math.PI * 2D) * this.idleEntity.getRNG().nextDouble();
    		 this.lookX = Math.cos(d0);
    	     this.lookZ = Math.sin(d0);
    	     this.lookY = Math.max(0, this.idleEntity.getRNG().nextFloat()-0.5f);
    	     this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
    	     watchEntity = null;
    	}
       
    }
    
    

    @Override
	public void resetTask() {
    	watchEntity = null;
	}

	/**
     * Updates the task
     */
    public void updateTask()
    {
        --this.idleTime;
        if(watchEntity != null)
        {
        	this.idleEntity.getLookHelper().setLookPositionWithEntity(watchEntity, 10.0F, (float)this.idleEntity.getVerticalFaceSpeed());
        }
        else
        {
        	this.idleEntity.getLookHelper().setLookPosition(this.idleEntity.posX + this.lookX, this.idleEntity.posY + (double)this.idleEntity.getEyeHeight() + this.lookY, this.idleEntity.posZ + this.lookZ, 10.0F, (float)this.idleEntity.getVerticalFaceSpeed());
        }
        
    }
}