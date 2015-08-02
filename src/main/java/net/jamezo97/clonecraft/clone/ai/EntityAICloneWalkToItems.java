package net.jamezo97.clonecraft.clone.ai;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;

public class EntityAICloneWalkToItems extends EntityAIBase {

	EntityClone clone;
	
	public EntityAICloneWalkToItems(EntityClone clone){
		this.clone = clone;
		this.setMutexBits(1);
	}
	
	Entity itemToGet = null;
	
	int maxDist = 1;
	
	@Override
	public boolean shouldExecute()
	{
		boolean states = clone.getOptions().pickUp.get() && clone.getOptions().walkToItems.get() && clone.getAttackTarget() == null && clone.getNavigator().noPath();
		
		if(states)
		{
			List entities = clone.worldObj.getEntitiesWithinAABBExcludingEntity(clone, clone.boundingBox.expand(16, 8, 16));
			if(entities.size() == 0)
			{
				return false;
			}
			
			double d = Double.MAX_VALUE;
			
			double temp;
			
			itemToGet = null;
			
			for(int a = 0; a < entities.size(); a++)
			{
				Entity item = (Entity)entities.get(a);
				
				if(item instanceof EntityItem || item instanceof EntityXPOrb)
				{
					if(clone.canEntityBeSeen(item))
					{
						temp=clone.getDistanceSqToEntity(item);
						if(itemToGet == null || (temp) < d)
						{
							d = temp;
							itemToGet = item;
							if(item instanceof EntityXPOrb)
							{
								//Which is actually 3
								maxDist = 9;
							}
							else
							{
								maxDist = 1;
							}
						}
						
						
					}
				}
			}
			return itemToGet != null;
		}
		return false;
	}
	
	

	@Override
	public void resetTask() {
		itemToGet = null;
	}

	int counter = 0;

	@Override
	public void updateTask() {
	
		
		if(counter % 10 == 0)
		{
			double distance = clone.getDistanceSqToEntity(itemToGet);
			if(distance >= maxDist)
			{
				clone.moveTo(itemToGet.posX, itemToGet.posY, itemToGet.posZ);
			}
		}
		
		//Took too long to get this item.
		if(counter >= 1000){
			itemToGet = null;
			return;
		}
		
		counter++;
	}

	@Override
	public boolean continueExecuting()
	{
		return clone.getOptions().pickUp.get() && clone.getOptions().walkToItems.get() && itemToGet != null && itemToGet.isEntityAlive();
	}

	@Override
	public void startExecuting() {
		counter = 0;
	}

	
	
	
	

}
