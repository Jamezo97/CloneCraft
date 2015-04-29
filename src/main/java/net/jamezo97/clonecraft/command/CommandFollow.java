package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.VerbSet;

public class CommandFollow extends Command{
	
	

	public CommandFollow() {
		super(new Parameter[]{}, new Parameter[]{Parameters.p_player}, new Parameter[]{});
	}

	@Override
	public VerbSet getRequiredVerb() {
		return VerbSet.follow;
	}

	@Override
	public CommandTask getCommandExecutionDelegate() {
		return null;
	}
	
	

	
	
}
