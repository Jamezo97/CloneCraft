package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;

public abstract class Command {
	
	public KeyWord[] getRequiredKeyWords(){
		return new KeyWord[]{getRequiredKeyWord()};
	}
	
	public abstract CommandTask getCommandExecutionDelegate();
	
	public abstract KeyWord getRequiredKeyWord();
	
	public abstract Parameter[] getRequiredParameters();

}
