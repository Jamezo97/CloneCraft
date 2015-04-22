package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;

public class EntityAICloneWalkToItems extends EntityAIBase {

	EntityClone clone;
	
	public EntityAICloneWalkToItems(EntityClone clone){
		this.clone = clone;
		this.setMutexBits(1);
	}
	
	EntityItem itemToGet = null;
	
	@Override
	public boolean shouldExecute() {
		boolean states = clone.getOptions().pickUp.get() && clone.getOptions().walkToItems.get() && clone.getAttackTarget() == null && clone.getNavigator().noPath();
		if(states){
			
			List<EntityItem> items = clone.worldObj.getEntitiesWithinAABB(EntityItem.class, clone.boundingBox.expand(16, 8, 16));
			if(items.size() == 0){
				return false;
			}
			Collections.sort(items, new Comparator<EntityItem>(){

				@Override
				public int compare(EntityItem item1, EntityItem item2) {
					double distance1 = item1.getDistanceSqToEntity(clone);
					double distance2 = item2.getDistanceSqToEntity(clone);
					if(distance1 == distance2){
						return 0;
					}else if(distance1 > distance2){
						return 1;
					}
					return -1;
				}
			});
			
			for(int a = 0; a < items.size(); a++)
			{
				EntityItem item = items.get(a);
				
				if(clone.canEntityBeSeen(item))
				{
					itemToGet = item;
					return true;
				}
			}
		}
		return false;
	}

	int counter = 0;

	@Override
	public void updateTask() {
	
		
		if(counter % 5 == 0)
		{
			double distance = clone.getDistanceSqToEntity(itemToGet);
			if(distance >= 1){
				clone.moveTo(itemToGet.posX, itemToGet.posY, itemToGet.posZ);
			}
		}
		//Took too long to get this item.
		if(counter >= 1000){
			itemToGet = null;
			return;
		}
		
		
//		List<EntityItem> items = clone.worldObj.getEntitiesWithinAABB(EntityItem.class, clone.boundingBox.expand(16, 8, 16));
//		
//		if(items.size() == 0){
//			foundNoItems = true;
//			return;
//		}else{
//			foundNoItems = false;
//		}
		
		
//		ArrayList<EntityItem> closest = new ArrayList<EntityItem>();
//		while(!items.isEmpty()){
//			double distance = -1;
//			int index = -1;
//			for(int a = 0; a < items.size(); a++){
//				if(index < 0 || (clone.getDistanceSqToEntity((EntityItem)items.get(a)) < distance)){
//					distance = clone.getDistanceSqToEntity((EntityItem)items.get(a));
//					index = a;
//				}
//			}
//			closest.add((EntityItem)items.remove(index));
//		}
//		for(int a = 0; a < items.size(); a++){
//			EntityItem item = items.get(a);
//			if(item.isCollided && clone.canEntityBeSeen(item)){
//				double distance = clone.getDistanceSqToEntity(item);
//				if(distance < 1){
//					break;
//				}
//				clone.moveTo(item.posX, item.posY, item.posZ);
//				if(!clone.getNavigator().noPath()){
//					break;
//				}
//			}
//		}
		counter++;
	}

	@Override
	public boolean continueExecuting() {
		return clone.getOptions().pickUp.get() && clone.getOptions().walkToItems.get() && itemToGet != null && itemToGet.isEntityAlive();
	}

	@Override
	public void startExecuting() {
		counter = 0;
	}

	
	
	
	

}
