package net.jamezo97.clonecraft.clone.ai.block;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class EntityAIMine extends EntityAIBase{

	
	/** Once all blocks have been minded from this finder, set this to null, and call
	 *  the on finished method in the interface
	 */
	BlockFinder currentFinder = null;
	
	EntityClone clone = null;
	
	public EntityAIMine(EntityClone clone) {
		this.clone = clone;
		//00000101
		this.setMutexBits(5);
	}
	
	public EntityClone getClone(){
		return this.clone;
	}

	@Override
	public boolean shouldExecute() {
		return currentFinder != null && clone.getOptions().breakBlocks.get();
	}

	@Override
	public boolean continueExecuting() {
		return shouldExecute();
	}

	@Override
	public void startExecuting() {
	}

	@Override
	public void resetTask() {
		
	}

	@Override
	public void updateTask() {
		  
	}

}
