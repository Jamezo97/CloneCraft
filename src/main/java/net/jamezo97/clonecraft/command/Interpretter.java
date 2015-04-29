package net.jamezo97.clonecraft.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.parameter.PGuess;
import net.jamezo97.clonecraft.command.parameter.ParamGuess;
import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.VerbSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class Interpretter {
	
	EntityClone clone;
	
	Command currentCommand = null;
	
	CurrentParams currentParams = null;
	
	Parameter missingParameter = null;
	
	
	public Interpretter(EntityClone clone){
		this.clone = clone;
	}
	
	public void parseInput(String input, EntityPlayer sender)
	{
		if(currentCommand == null){
			
			String[] words = input.split(" ");
			
			ArrayList<PossibleCommand> possibleCommands = new ArrayList<PossibleCommand>();
			
			for(int a = 0; a < Commands.size(); a++)
			{
				Command command = Commands.get(a);
				CurrentParams paramSet = new CurrentParams();
				
				VerbSet requiredVerb = command.getRequiredVerb();
				
				if(requiredVerb == null/* || verbs.length == 0*/)
				{
					//If there are no keywords, there is nothing to distinguish
					//this command. So disregard it, and give a warning
					System.err.println("Command must have a keyword, otherwise it's not distinguishable");
					continue;
				}
				
				int wordIndex;
				
				if((wordIndex = requiredVerb.containsVerb(words)) != -1/*found == verbs.length*/)
				{
					
					String[] objectParams = new String[wordIndex];
					String[] subjectParams = new String[words.length-(wordIndex+1)];
					
					System.arraycopy(words, 0, objectParams, 0, objectParams.length);
					System.arraycopy(words, wordIndex+1, subjectParams, 0, subjectParams.length);
					
					float confidence = 0f;
					
					int foundParams = 0;
					
					float[] subjectMultipliers = new float[]{0.8f, 1.0f, 1.0f};
					float[] objectMultipliers = new float[]{1.0f, 0.8f, 1.0f};
					
					
					
					for(int b = 0; b < 3; b++)
					{
						Parameter[] theParams = null;
						
						float objectMultiplier = objectMultipliers[b];
						float subjectMultiplier = subjectMultipliers[b];
						
						switch(b){
							case 0: theParams = command.getObjectParameters(); break;
							case 1: theParams = command.getSubjectParameters(); break;
							case 2: theParams = command.getOptionalParameters(); break;
						}
						
						int requiredParams = 0;
						
						if(theParams != null && theParams.length > 0)
						{
							if(b == 0 || b == 1)
							{
								requiredParams = theParams.length;
							}
							for(int c = 0; c < theParams.length; c++)
							{
								Parameter theParam = theParams[c];
								
								PGuess objects = theParam.findParameters(clone, sender, objectParams);
								PGuess subjects = theParam.findParameters(clone, sender, subjectParams);
								
								PGuess params = new PGuess(theParam);
								
								objects.amplify(objectMultiplier);
								subjects.amplify(subjectMultiplier);
								
								objects.addTo(params);
								subjects.addTo(params);
								
								float subConfidence = 0;
								
								for(int d = 0; d < params.size(); d++)
								{
									ParamGuess guess = params.get(d);
									subConfidence += guess.confidence * 1/(d+1);
								}
								
								confidence += subConfidence;
								
								if(params.size() > 0)
								{
									requiredParams--;
									paramSet.setParameter(theParam, params);
								}
							}
						}
						else
						{
							continue;
						}
					}
					//Valid command, parameters have been found.
					possibleCommands.add(new PossibleCommand(command, paramSet, confidence));
				}
			}
			
			if(possibleCommands.size() == 0)
			{
				if(this.currentCommand != null)
				{
					if(this.missingParameter != null)
					{
						PGuess params = this.missingParameter.findParameters(clone, sender, words);
						
					}
				}
				else
				{
					System.out.println("No Commands Found");
					sender.addChatMessage(new ChatComponentText(clone.getCommandSenderName() + ": Sorry, I have no idea what you want me to do."));
					this.currentCommand = null;
					this.currentParams = null;
					this.missingParameter = null;
				}
				
				return;
			}
			else
			{
				Collections.sort(possibleCommands, new Comparator<PossibleCommand>(){

					@Override
					public int compare(PossibleCommand pc1, PossibleCommand pc2) {
						if(pc1.chance > pc2.chance){
							return 1;
						}else if(pc2.chance > pc1.chance){
							return -1;
						}
						return 0;
					}
					
				});
				
				PossibleCommand bestCommand = possibleCommands.get(0);
				
				Command theBestCommand = bestCommand.command;
				
				CurrentParams paramSet = bestCommand.paramSet;
				
				if(this.currentCommand != null){
					if(this.currentCommand == theBestCommand)
					{
						this.currentParams.merge(paramSet);
					}
					else
					{
						this.currentCommand = theBestCommand;
						this.currentParams = paramSet;
					}
				}
				else
				{
					this.currentCommand = theBestCommand;
					this.currentParams = paramSet;
				}
				
				
				this.missingParameter = theBestCommand.hasRequiredParams(paramSet);
			}
			
			if(this.missingParameter != null)
			{
				String ask = this.currentCommand.getAskStringFor(this.missingParameter);
				sender.addChatComponentMessage(new ChatComponentText(ask));
			}
			else if(this.currentCommand != null && this.currentParams != null)
			{
				this.currentCommand = null;
				this.currentParams = null;
				this.missingParameter = null;
				
				CommandTask ct = this.currentCommand.getCommandExecutionDelegate();
				
				clone.getLookHelper().setLookPositionWithEntity(sender, 10, clone.getVerticalFaceSpeed());
				
				ct.setClone(clone);
				ct.setSender(sender);
				
				ct.startExecuting();
			}
		}
	}
	
	
	public static class PossibleCommand{
		
		Command command;
		CurrentParams paramSet;
		float chance;
		
		public PossibleCommand(Command command, CurrentParams paramSet, float chance){
			this.command = command;
			this.chance = chance;
		}
		
	}
	
	
	
	//A command, is defined by a unique subset of key words.
	//Key words are, for example, go, dig, build, kill, attack, jump, follow, stop, stay, guard, farm, come, give
	//With those key words defined and a command found, parameters are then needed. This is the hardest part.
	//Parameters are like, numbers, names, positions (there, here, next to me)
	//Once a command has the correctly specified parameter set, it may then begin executing
	//If it can't find all the info, it will ask directed questions, dependent on the parameter type.
	//i.e. How many blocks would you like? Where would you like me to go? (not "How many?" or "Where?", as this
	//provides no indication that the currently constructed command is what the user believes it to be)

}
