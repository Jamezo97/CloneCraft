package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.task.CommandTaskOnce;
import net.jamezo97.clonecraft.command.word.WordSet;

public class CommandCome extends Command{
	
	

	public CommandCome()
	{
		super(null, null, new Parameter[]{Parameters.p_stop});
	}

	@Override
	public CommandTask getCommandExecutionDelegate()
	{
		return new CommandTaskOnce(){

			@Override
			public void execute()
			{
				double distance = clone.getDistanceSqToEntity(this.commander);
				
				if(distance > 400)
				{
					clone.setPosition(this.commander.posX, this.commander.posY, this.commander.posZ);
				}
				else
				{
					clone.setPath(clone.getNavigator().getPathToEntityLiving(this.commander));
				}
			}
			
		};
	}

	@Override
	public WordSet getRequiredVerbs()
	{
		return WordSet.come;
	}
	
	

}
