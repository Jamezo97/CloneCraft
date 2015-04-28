package net.jamezo97.clonecraft.command.task;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public abstract class CommandTask {
	
	/**
	 * The poor bugger who gets to be on the front end of my AI system
	 */
	protected EntityClone clone = null;
	
	/**
	 * The player who executed the command.
	 */
	protected EntityPlayer commander = null;
	
	public void setClone(EntityClone clone){
		this.clone = clone;
	}
	
	public void setSender(EntityPlayer commander){
		this.commander = commander;
	}

	/**
     * Returns whether the EntityAIBase should begin execution.
     */
    public abstract boolean shouldExecute();

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting()
    {
        return this.shouldExecute();
    }

    /**
     * Determine if this AI Task is interruptible by a higher (= lower value) priority task.
     */
    public boolean isInterruptible()
    {
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {}

    /**
     * Resets the task
     */
    public void resetTask() {}

    /**
     * Updates the task
     */
    public void updateTask() {}
	
}
