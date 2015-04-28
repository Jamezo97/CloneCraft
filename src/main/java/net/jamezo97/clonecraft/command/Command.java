package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.VerbSet;

/**
 * All Commands must be defined by a verb(/action). i.e. mine, dig, build, jump, kill, come
 * @author James
 *
 */
public abstract class Command {
	
//	public VerbSet[] getVerbs(){
//		return new VerbSet[]{getRequiredVerb()};
//	}
	
	public abstract CommandTask getCommandExecutionDelegate();
	
	public abstract VerbSet getRequiredVerb();
	
	/**
	 * If these words are found in the command string, then it is more likely that this command is to be chosen
	 * to be executed.
	 * @return And array of Strings (words)
	 */
	public String[] getBonusWords(){
		return null;
	}
	
	public abstract Parameter[] getRequiredParameters();

}
