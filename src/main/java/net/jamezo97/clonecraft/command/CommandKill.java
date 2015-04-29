package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.task.CommandTaskOnce;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.jamezo97.clonecraft.network.Handler6KillClone;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentText;

public class CommandKill extends Command{
	
	

	public CommandKill() {
		super(null, new Parameter[]{Parameters.p_entity/*, Parameters.p_courtesy*/}, null);
	}

	@Override
	public CommandTask getCommandExecutionDelegate() {
		
		return new CommandTaskOnce(){

			@Override
			public void execute() {
				
				
				Object toKill = this.paramSet.getParamValue(Parameters.p_entity).getBestGuess().value;
				
				if(toKill == this.clone)
				{
					this.commander.addChatComponentMessage(new ChatComponentText("Okay then!"));
					
					this.clone.commitSuicide();
					
					new Handler6KillClone(this.clone).sendToAllWatching(this.clone);
				}
				else
				{
					if(toKill instanceof EntityLivingBase)
					{
						if(this.clone.canAttackEntity((EntityLivingBase)toKill)){
							this.commander.addChatComponentMessage(new ChatComponentText("Righteo!"));
							this.clone.setAttackTarget((EntityLivingBase)toKill);
							this.clone.setPath(this.clone.getNavigator().getPathToEntityLiving((EntityLivingBase)toKill));
						}
					}
					
				}
			}
		};
		
	}
	
	

	@Override
	public String[] getBonusWords() {
		return new String[]{"commit", "go"};
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.attack;
	}
	
	

}
