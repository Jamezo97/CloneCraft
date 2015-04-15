package net.jamezo97.clonecraft.clone.ai_old;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class AILookIdle extends AITask{

	EntityLiving idleEntity;
	
	/** X offset to look at */
    private double lookX;

    /** Z offset to look at */
    private double lookZ;
	
	int idleTime = 0;
	
	public AILookIdle(EntityLiving idleEntity){
		this.idleEntity = idleEntity;
	}
	
	@Override
	public boolean canStartTask() {
		return this.idleEntity.getRNG().nextFloat() < 0.02F;
	}

	@Override
	public boolean canEndTask() {
		return this.idleTime == 0;
	}

	@Override
	public void onTaskTick() {
//		System.out.println("Start");
		--this.idleTime;
        this.idleEntity.getLookHelper().setLookPosition(this.idleEntity.posX + this.lookX, this.idleEntity.posY + (double)this.idleEntity.getEyeHeight(), this.idleEntity.posZ + this.lookZ, 10.0F, (float)this.idleEntity.getVerticalFaceSpeed());
	}

	@Override
	public void beginTask() {
		if(this.idleEntity.getRNG().nextBoolean()){
			List l = this.idleEntity.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.idleEntity.getBoundingBox().expand(5, 4, 5));
			if(l != null && l.size() > 0){
				EntityPlayer lookAt = (EntityPlayer)l.get(this.idleEntity.getRNG().nextInt(l.size()));
				return;
			}
		}
		double var1 = (Math.PI * 2D) * this.idleEntity.getRNG().nextDouble();
        this.lookX = Math.cos(var1);
        this.lookZ = Math.sin(var1);
        this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
	}

	
	
}
