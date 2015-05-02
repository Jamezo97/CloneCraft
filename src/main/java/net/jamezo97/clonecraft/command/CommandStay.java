package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.PGuess;
import net.jamezo97.clonecraft.command.parameter.ParamGuess;
import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.task.CommandTaskOnce;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.util.ChunkCoordinates;

/**
 * Akin to Guard. Stay, Guard, etc, all the same.
 * @author James
 *
 */
public class CommandStay extends Command{

	public CommandStay() {
		super(null, null, new Parameter[]{Parameters.p_position});
	}
	
	@Override
	public CommandTask getCommandExecutionDelegate() {
		return new CommandTaskOnce(){

			@Override
			public void execute() {
				
				System.out.println("Guard");
				
				clone.getOptions().guard.set(true);
				clone.getOptions().follow.set(false);
				clone.getOptions().wander.set(false);
				
				PGuess params = this.paramSet.getParamValue(Parameters.p_position);
				if(params != null && params.size() > 0){
					ParamGuess guess = params.getBestGuess();
					if(guess != null && guess.value instanceof ChunkCoordinates)
					{
						ChunkCoordinates cc = (ChunkCoordinates)guess.value;
						
						clone.say("Okay, I'll stay at the position (" + cc.posX + ", " + cc.posY + ", " + cc.posZ + ")", commander);
						
						clone.setGuardPosition((ChunkCoordinates)guess.value);
						return;
					}else{
						clone.say("I can't stay there! That's impossible!", commander);
						return;
					}
				}
				clone.say("Okay, I'll guard my position here.", commander);
			}
		};
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.stay;
	}

	

	
	
}
