package net.jamezo97.clonecraft.clone.ai;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

public class EntityAIShare extends EntityAIBase{
	
	EntityClone clone;

	public EntityAIShare(EntityClone clone) {
		this.clone = clone;
		this.setMutexBits(1);
	}
	
	ItemStack itemOnOffer;
	
	EntityClone toShareWith;

	@Override
	public boolean shouldExecute() {
		if(clone.getRNG().nextFloat() > 0.05){
			return false;
		}
		List list = clone.worldObj.getEntitiesWithinAABB(EntityClone.class, clone.boundingBox.expand(2, 1, 2));
		for(int a = 0; a < list.size(); a++){
			EntityClone other = (EntityClone)list.get(a);
			if(other.getTeam() == clone.getTeam()){
				itemOnOffer = other.getCurrentEquippedItem();
				toShareWith = other;
				return true;
			}
		}
		return false;
	}
	
	int countDown = 30;

	@Override
	public boolean continueExecuting() {
		return countDown > 0 && isStackAvailable(itemOnOffer) && toShareWith.isEntityAlive();
	}
	
	public boolean isStackAvailable(ItemStack stack){
		if(stack == null)
		{
			return false;
		}
		ItemStack copy = stack.copy();
		
		ItemStack[] stacks = clone.inventory.mainInventory;
		
		for(int a = 0; a < stacks.length; a++)
		{
			if(stacks[a] != null)
			{
				if(stacks[a].getItem() == copy.getItem() && ItemStack.areItemStackTagsEqual(copy,  stacks[a]))
				{
					copy.stackSize -= stacks[a].stackSize;
					if(copy.stackSize <= 0)
					{
						return true;
					}
				}
			}
		}
		
		return copy.stackSize <= 0;
	}

	@Override
	public void startExecuting() {
		countDown = 40;
		System.out.println("Set look at eachother");

		makeLookAt(clone, toShareWith);
		makeLookAt(toShareWith, clone);
		
//		clone.getLookHelper().setLookPositionWithEntity(toShareWith, 10, clone.getVerticalFaceSpeed());
//		toShareWith.getLookHelper().setLookPositionWithEntity(clone, 10, toShareWith.getVerticalFaceSpeed());
	}
	
	public void makeLookAt(EntityLiving me, EntityLiving at){
		me.getLookHelper().setLookPosition(at.posX, at.posY + at.getEyeHeight()+20, at.posZ, 10, me.getVerticalFaceSpeed());
//        this.idleEntity.getLookHelper().setLookPosition(this..posX + this.lookX, this.idleEntity.posY + (double)this.idleEntity.getEyeHeight(), this.idleEntity.posZ + this.lookZ, 10.0F, (float)me.getVerticalFaceSpeed());
	}

	@Override
	public void updateTask() {
		countDown--;
	}
	
	
	

	@Override
	public void resetTask() {
		System.out.println("SHARE");
		if(isStackAvailable(itemOnOffer) && toShareWith.isEntityAlive()){
			toShareWith.inventory.addItemStackToInventory(itemOnOffer);
			clone.inventory.removeItemStackFromInventory(itemOnOffer);
			System.out.println("Share did");
		}
		this.toShareWith = null;
		this.itemOnOffer = null;
	}

	public ItemStack getOfferedItem() {
		return itemOnOffer;
	}
	
	public void setOfferedItem(ItemStack stack){
		this.itemOnOffer = stack;
	}

}
