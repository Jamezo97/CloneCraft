package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityAIReturnGuard extends EntityAIBase{

	EntityClone clone;
	
	public EntityAIReturnGuard(EntityClone clone) {
		this.clone = clone;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute()
	{
		if(clone.getOptions().guard.get())
		{
			ChunkCoordinates cc = clone.getGuardPosition();
			if(!clone.isGuardPositionSet())
			{
				System.out.println("Set");
				cc.posX = (int)Math.floor(clone.posX);
				cc.posY = (int)Math.floor(clone.posY)-1;
				cc.posZ = (int)Math.floor(clone.posZ);
				clone.setBlockHighlight(cc.posX, cc.posY, cc.posZ);
			}
			else
			{
				clone.setBlockHighlightIfEmpty(cc.posX, cc.posY, cc.posZ);
			}


			checkGuardPos();
			
			
			return clone.isGuardPositionSet() && !isNearGuardPosition(0.5);
		}
		else if(clone.isGuardPositionSet())
		{
			ChunkCoordinates cc = clone.getGuardPosition();
			
			if(clone.blockHighlight.posX == cc.posX && clone.blockHighlight.posY == cc.posY && clone.blockHighlight.posZ == cc.posZ)
			{
				clone.resetBlockHighlight();
			}
			
			clone.resetGuardPosition();
		}
		return false;
	}
	
	public boolean isNearGuardPosition(double distance)
	{
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
	
	public void checkGuardPos()
	{
		ChunkCoordinates cc = clone.getGuardPosition();
		
		Block stand = clone.worldObj.getBlock(cc.posX, cc.posY, cc.posZ);
		Block feet = clone.worldObj.getBlock(cc.posX, cc.posY+1, cc.posZ);
		Block head = clone.worldObj.getBlock(cc.posX, cc.posY+1, cc.posZ);
		
		if(!stand.isSideSolid(clone.worldObj, cc.posX, cc.posY, cc.posZ, ForgeDirection.UP))
		{
			if(clone.blockHighlight.posX == cc.posX && clone.blockHighlight.posY == cc.posY && clone.blockHighlight.posZ == cc.posZ)
			{
				clone.resetBlockHighlight();
			}
			clone.resetGuardPosition();
			clone.say("My guard position has been compromised. The floor is gone!", clone.getOwner());
			
		}
		else if(!(feet == Blocks.air || feet.getCollisionBoundingBoxFromPool(clone.worldObj, cc.posX, cc.posY+1, cc.posZ) == null)
				|| !(head == Blocks.air || head.getCollisionBoundingBoxFromPool(clone.worldObj, cc.posX, cc.posY+2, cc.posZ) == null))
		{
			if(clone.blockHighlight.posX == cc.posX && clone.blockHighlight.posY == cc.posY && clone.blockHighlight.posZ == cc.posZ)
			{
				clone.resetBlockHighlight();
			}
			clone.resetGuardPosition();
			clone.say("My guard position has been compromised. There are blocks in the way!", clone.getOwner());
		}
		
	}

	@Override
	public boolean continueExecuting()
	{
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
			checkGuardPos();
			
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
