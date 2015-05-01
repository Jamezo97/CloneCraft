package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.task.CommandTaskOnce;
import net.jamezo97.clonecraft.command.word.WordSet;

public class CommandStop extends Command{

	
	
	public CommandStop() {
		super(null, null, null);
	}

	@Override
	public CommandTask getCommandExecutionDelegate() {
		return new CommandTaskOnce(){

			@Override
			public void execute() {
				clone.getCommandAI().setTask(null);
			}
			
		};
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.stop;
	}

	
	
}
