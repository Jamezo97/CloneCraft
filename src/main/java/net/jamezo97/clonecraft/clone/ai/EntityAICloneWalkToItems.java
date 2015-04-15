package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;
import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;

public class EntityAICloneWalkToItems extends EntityAIBase {

	EntityClone clone;
	
	public EntityAICloneWalkToItems(EntityClone clone){
		this.clone = clone;
	}
	
	@Override
	public boolean shouldExecute() {
		return clone.getOptions().pickUp.get() && clone.getOptions().walkToItems.get()/* && clone.notMining()*/ && clone.getAttackTarget() == null && clone.getNavigator().noPath();
	}

	@Override
	public boolean continueExecuting() {
		List items = clone.worldObj.getEntitiesWithinAABB(EntityItem.class, clone.boundingBox.expand(16, 8, 16));
		ArrayList<EntityItem> closest = new ArrayList<EntityItem>();
		while(!items.isEmpty()){
			double distance = -1;
			int index = -1;
			for(int a = 0; a < items.size(); a++){
				if(index < 0 || (clone.getDistanceSqToEntity((EntityItem)items.get(a)) < distance)){
					distance = clone.getDistanceSqToEntity((EntityItem)items.get(a));
					index = a;
				}
			}
			closest.add((EntityItem)items.remove(index));
		}
		for(int a = 0; a < closest.size(); a++){
			EntityItem item = closest.get(a);
			if(item.isCollided){
				double distance = clone.getDistanceSqToEntity(item);
				if(distance < 1){
					break;
				}
				clone.moveTo(item.posX, item.posY, item.posZ);
				if(!clone.getNavigator().noPath()){
					break;
				}
			}
		}
		return super.continueExecuting();
	}
	
	

}
