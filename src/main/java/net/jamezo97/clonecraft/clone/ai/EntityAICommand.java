package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAICommand extends EntityAIBase{
	
	CommandTask theTask = null;
	
	EntityClone theClone;
	
	public EntityAICommand(EntityClone clone){
		this.theClone = clone;
		this.setMutexBits(2);
	}
	
	public void setTask(CommandTask theTask){
		this.theTask = theTask;
	}

	@Override
	public boolean shouldExecute() {
		return theTask != null && theTask.shouldExecute();
	}

	@Override
	public boolean continueExecuting() {
		return theTask != null && theTask.continueExecuting();
	}

	@Override
	public void startExecuting() {
		if(theTask != null)
		{
			theTask.startExecuting();
		}
	}

	@Override
	public void resetTask() {
		if(theTask != null)
		{
			theTask.resetTask();
		}
	}

	@Override
	public void updateTask() {
		if(theTask != null)
		{
			theTask.updateTask();
		}
	}

	public CommandTask getRunningTask() {
		return theTask;
	}

	public void clear() {
		this.theTask = null;
	}

	
	
}
