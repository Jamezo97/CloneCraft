package net.jamezo97.clonecraft.command.task;

public abstract class CommandTaskOnce extends CommandTask{

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
	
	

}
