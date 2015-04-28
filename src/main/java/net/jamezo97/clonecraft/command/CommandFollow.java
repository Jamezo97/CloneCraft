package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.VerbSet;

public class CommandFollow extends Command{

	@Override
	public VerbSet getRequiredVerb() {
		return VerbSet.follow;
	}

	@Override
	public Parameter[] getRequiredParameters() {
		return new Parameter[]{Parameters.p_player};
	}

	@Override
	public CommandTask getCommandExecutionDelegate() {
		return null;
	}
	
	

	
	
}
