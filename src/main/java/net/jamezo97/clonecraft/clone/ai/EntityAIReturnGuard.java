package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.ChunkCoordinates;

public class EntityAIReturnGuard extends EntityAIBase{

	EntityClone clone;
	
	public EntityAIReturnGuard(EntityClone clone) {
		this.clone = clone;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if(clone.getOptions().guard.get()){
			if(!clone.isGuardPositionSet()){
				ChunkCoordinates cc = clone.getGuardPosition();
				cc.posX = (int)Math.floor(clone.posX);
				cc.posY = (int)Math.floor(clone.posY)-1;
				cc.posZ = (int)Math.floor(clone.posZ);
			}
			return !isNearGuardPosition(0.5);
		}
		return false;
	}
	
	public boolean isNearGuardPosition(double distance){
		if(getDistanceSquaredToGuardPosition() > distance)
		{
			return false;
		}
		return true;
	}
	
	public double getDistanceSquaredToGuardPosition(){
		double x1 = clone.getGuardPosition().posX+0.5;
		double y1 = clone.getGuardPosition().posY+1;
		double z1 = clone.getGuardPosition().posZ+0.5;
		
		double x2 = clone.posX;
		double y2 = clone.posY;
		double z2 = clone.posZ;

		double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		
		
		double distanceSquared = dx*dx+dy*dy+dz*dz;
		
		return distanceSquared;
	}

	@Override
	public boolean continueExecuting() {
		if(isNearGuardPosition(0.5))
		{
			return false;
		}
		return true && clone.getOptions().guard.get();
	}
	
	int ticksExecuting = 0;

	@Override
	public void updateTask() {
		ticksExecuting++;
		
		if(ticksExecuting % 10 == 0)
		{
			double distance = getDistanceSquaredToGuardPosition();
			if(distance > 1600 || ticksExecuting > 400)
			{
				clone.teleportToGuardPosition();
			}
			else
			{
				clone.moveTo(clone.getGuardPosition().posX+0.5, clone.getGuardPosition().posY+1, clone.getGuardPosition().posZ+0.5);
			}
			return;
		}
	}
	
	
	
	@Override
	public void resetTask() {
		ticksExecuting = 0;
	}


	

	
	
}
