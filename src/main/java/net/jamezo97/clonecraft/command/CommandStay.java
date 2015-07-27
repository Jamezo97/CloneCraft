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
		super(null, null, new Parameter[]{Parameters.p_position, Parameters.p_stop});
	}
	
	@Override
	public CommandTask getCommandExecutionDelegate() {
		return new CommandTaskOnce(){

			@Override
			public void execute()
			{
				PGuess stopGuess = this.paramSet.getParamValue(Parameters.p_stop);
				if(stopGuess != null && stopGuess.size() > 0)
				{
					clone.getOptions().guard.set(false);
					
					/*ChunkCoordinates cc2 = clone.getGuardPosition();
					
					if(clone.blockHighlight.posX == cc2.posX && clone.blockHighlight.posY == cc2.posY && clone.blockHighlight.posZ == cc2.posZ)
					{
						clone.resetBlockHighlight();
					}*/
					
					clone.say("Okay");
				}
				else
				{
					clone.getOptions().guard.set(true);
					clone.getOptions().follow.set(false);
					clone.getOptions().wander.set(false);
					
					PGuess params = this.paramSet.getParamValue(Parameters.p_position);
					
					if(params != null && params.size() > 0)
					{
						ParamGuess guess = params.getBestGuess();
						if(guess != null && guess.value instanceof ChunkCoordinates)
						{
							ChunkCoordinates cc = (ChunkCoordinates)guess.value;
							
							clone.say("Okay, I'll stay at the position (" + cc.posX + ", " + cc.posY + ", " + cc.posZ + ")", commander);
							
							ChunkCoordinates cc2 = clone.getGuardPosition();
							
							if(clone.blockHighlight.posX == cc2.posX && clone.blockHighlight.posY == cc2.posY && clone.blockHighlight.posZ == cc2.posZ)
							{
								clone.resetBlockHighlight();
							}
							
							clone.setGuardPosition((ChunkCoordinates)guess.value);
							return;
						}else{
							clone.say("I can't stay there! That's impossible!", commander);
							return;
						}
					}
					clone.say("Okay, I'll guard my position here.", commander);
				}
				
				
			}
		};
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.stay;
	}

	

	
	
}
