package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.WordSet;

/**
 * All Commands must be defined by a verb(/action). i.e. mine, dig, build, jump, kill, come
 * @author James
 *
 */
public abstract class Command {
	
	Parameter[] objectParams = null;
	Parameter[] subjectParams = null;
	Parameter[] optionalParams = null;
	
	public Command(Parameter[] objectParams, Parameter[] subjectParams, Parameter[] optionalParams) {
		this.objectParams = objectParams;
		this.subjectParams = subjectParams;
		this.optionalParams = optionalParams;
	}

	public abstract CommandTask getCommandExecutionDelegate();
	
	public abstract WordSet getRequiredVerbs();
	
	private int id = 0;
	
	public Command setId(int id){
		this.id = id;
		return this;
	}
	
	public int getId(){
		return this.id;
	}
	
	
	
	public Parameter hasRequiredParams(CurrentParams params){
		Parameter objParam = params.getMissingParam(objectParams);
		if(objParam != null)
		{
			return objParam;
		}
		Parameter subParam = params.getMissingParam(subjectParams);
		return subParam;
	}
	
	
	
	/**
	 * If these words are found in the command string, then it is more likely that this command is to be chosen
	 * to be executed.
	 * @return And array of Strings (words)
	 */
	public String[] getBonusWords(){
		return null;
	}
	
	/**
	 * Gets the Parameters that define the subject of the command. i.e. Go kill some creepers 
	 * @return
	 */
	public Parameter[] getSubjectParameters(){
		return subjectParams;
	}
	
	public Parameter[] getOptionalParameters(){
		return optionalParams;
	}
	
	public Parameter[] getObjectParameters(){
		return objectParams;
	}

	public String getAskStringFor(Parameter missingParam) {
		return missingParam.getDefaultAskString();
	}

	public float getConfidenceMultiplier(){
		return 1.0f;
	}
	
}
