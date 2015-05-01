package net.jamezo97.clonecraft.command.task;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.CurrentParams;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public abstract class CommandTaskOnce extends CommandTask{
	
	
	/**
	 * Doesn't need to remember the command that sent it, because it's executed immediately, and only once.
	 */
	public CommandTaskOnce() {
		super(null);
	}

	@Override
	public boolean shouldExecute() {
		return true;
	}

	@Override
	public boolean continueExecuting() {
		return false;
	}

	@Override
	public boolean isInterruptible() {
		return false;
	}

	@Override
	public void startExecuting() {
		execute();
	}
	
	public abstract void execute();

	@Override
	public void resetTask() {}

	@Override
	public void updateTask() {}

	@Override
	public void saveTask(NBTTagCompound nbt) { }

	@Override
	public void loadTask(NBTTagCompound nbt) { }

	
	/**
	 * The poor bugger who gets to be on the front end of my AI system
	 */
	protected EntityClone clone = null;
	/**
	 * The player who executed the command.
	 */
	protected EntityPlayer commander = null;
	/**
	 * The parameters for the command to use.
	 */
	protected CurrentParams paramSet = null;
	
	@Override
	public void taskInit(EntityClone clone, EntityPlayer commander, CurrentParams paramset) {
		this.clone = clone;
		this.commander = commander;
		this.paramSet = paramset;
		
	}
	
	

}
