package net.jamezo97.clonecraft.command.task;

public abstract class CommandTask {

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
