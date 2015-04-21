package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.InventoryClone;
import net.jamezo97.clonecraft.clone.InventoryClone.TypeCheck;
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
		if(!clone.getOptions().share.get() || clone.ticksExisted % 10 != 0 || clone.getRNG().nextFloat() > 0.9003){
			return false;
		}
		List<EntityClone> list = clone.worldObj.getEntitiesWithinAABB(EntityClone.class, clone.boundingBox.expand(2, 1, 2));
		
		Collections.sort(list, new Comparator<EntityClone>(){
			@Override
			public int compare(EntityClone o1, EntityClone o2) {
				double d1 = o1.getDistanceSqToEntity(clone);
				double d2 = o2.getDistanceSqToEntity(clone);
				if(d1 > d2){
					return 1;
				}else if(d1 < d2){
					return -1;
				}
				return 0;
			}
		});
		
		ArrayList<TypeCheck> typeChecks = new ArrayList<TypeCheck>();
		
		//If this clone has diffSize more than the other clone, share.
		int diffSize = 2;

		typeChecks.add(InventoryClone.CHECK_ARROW);
		typeChecks.add(InventoryClone.CHECK_FOOD);
		
		TypeCheck[][] checks = new TypeCheck[][]{
				InventoryClone.CHECK_WOOD,
				InventoryClone.CHECK_STONE,
				InventoryClone.CHECK_IRON,
				InventoryClone.CHECK_GOLD,
				InventoryClone.CHECK_DIAMOND};
		for(int a = 0; a < checks.length; a++){
			for(int b = 0; b < checks[a].length; b++){
				typeChecks.add(checks[a][b]);
			}
		}
		
		for(int a = 0; a < list.size(); a++)
		{
			EntityClone other = (EntityClone)list.get(a);
			if(other != clone && other.getTeam() == clone.getTeam() && other.getOptions().share.get())
			{
				
				int[] typeCountsOther = new int[typeChecks.size()];
				
				for(int b = 0; b < typeChecks.size(); b++)
				{
					typeCountsOther[b] = other.inventory.getTypeCount(typeChecks.get(b));
				}
				
				for(int b = 0; b < clone.inventory.mainInventory.length; b++)
				{
					ItemStack stack = clone.inventory.mainInventory[b];
					if(stack != null)
					{
						for(int c = 0; c < typeChecks.size(); c++)
						{
							if(typeChecks.get(c).isType(stack))
							{
								
								int countMe = clone.inventory.getTypeCount(typeChecks.get(c));
								int countOther = typeCountsOther[c];
								
								if(countMe-countOther >= diffSize)
								{
									int amountShouldGive = (countMe-countOther)/2;
									
									if(amountShouldGive > 64)
									{
										amountShouldGive = 64;
									}
									else if(amountShouldGive > stack.stackSize)
									{
										amountShouldGive = stack.stackSize;
									}
									
									int amountCanFit = other.inventory.getFitCount(stack);
									
									if(amountCanFit > 0)
									{
										itemOnOffer = stack.copy();
										itemOnOffer.stackSize = amountCanFit<amountShouldGive?amountCanFit:amountShouldGive;
										toShareWith = other;
										return true;
									}
								}
							}
						}
					}	
				}
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
		return clone.inventory.isStackAvailable(stack);
	}

	@Override
	public void startExecuting() {
		countDown = 30;
	}
	

	@Override
	public void updateTask() {
		countDown--;
		makeLookAt(clone, toShareWith);
		makeLookAt(toShareWith, clone);
	}
	
	private void makeLookAt(EntityLiving me, EntityLiving at){
		me.getLookHelper().setLookPosition(at.posX, (at.posY + at.getEyeHeight()), at.posZ, 10, me.getVerticalFaceSpeed());
	}
	

	@Override
	public void resetTask() {
		if(this.isStackAvailable(itemOnOffer) && toShareWith.isEntityAlive() && toShareWith.getDistanceSqToEntity(clone) < 16){
			
			clone.inventory.removeItemStackFromInventory(itemOnOffer);
			//Important to copy it here, otherwise the passed stack is modified.
			toShareWith.inventory.addItemStackToInventory(itemOnOffer.copy());
			clone.worldObj.playSoundAtEntity(clone, "random.pop", 1.0f, 1.0f + (clone.getRNG().nextFloat()-0.5f)/10.0f);
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
