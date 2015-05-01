package net.jamezo97.clonecraft.command.task;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.Command;
import net.jamezo97.clonecraft.command.CurrentParams;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class CommandTask {

	protected EntityClone clone;
	
	protected String commanderName;
	
	public final Command commandBase;
	
	public CommandTask(Command commandBase) {
		this.commandBase = commandBase;
	}
	
	public void setClone(EntityClone clone){
		this.clone = clone;
	}
	
	public void setPlayerName(String name){
		this.commanderName = name;
	}
	
	public String getPlayerName(){
		return this.commanderName;
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
    
	
	public abstract void taskInit(EntityClone clone, EntityPlayer sender, CurrentParams params);
	
	public abstract void saveTask(NBTTagCompound nbt);
	
	public abstract void loadTask(NBTTagCompound nbt);
	
	
}
