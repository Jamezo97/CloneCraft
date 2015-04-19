package net.jamezo97.clonecraft.clone.ai;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

public class EntityAIShare extends EntityAIBase{
	
	EntityClone clone;

	public EntityAIShare(EntityClone clone) {
		this.clone = clone;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}

	@Override
	public boolean continueExecuting() {
		return true;
	}

	@Override
	public void startExecuting() {
		
	}
	
	ItemStack onOffer = null;

	@Override
	public void updateTask() {
		
		List list = clone.worldObj.getEntitiesWithinAABB(EntityClone.class, clone.boundingBox.expand(2, 1, 2));
		for(int a = 0; a < list.size(); a++){
			EntityClone other = (EntityClone)list.get(a);
			if(other.getTeam() == clone.getTeam()){
				onOffer = other.getCurrentEquippedItem();
			}
		}
		
	}

	public ItemStack getCurrentItem() {
		return onOffer;
	}

}
