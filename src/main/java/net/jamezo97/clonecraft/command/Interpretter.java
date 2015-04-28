package net.jamezo97.clonecraft.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.parameter.PGuess;
import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.VerbSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

public class Interpretter {
	
	EntityClone clone;
	
	Command currentCommand = null;
	
	CurrentParams params = null;
	
	public Interpretter(EntityClone clone){
		this.clone = clone;
	}
	
	public void parseInput(String input, EntityPlayer sender)
	{
		if(currentCommand == null){
			
			String[] words = input.split(" ");
			
			/*ArrayList<VerbSet> keyWords = new ArrayList<VerbSet>();
			for(int a = 0; a < VerbSet.size(); a++)
			{
				if(VerbSet.get(a).containsKeyWord(words))
				{
					keyWords.add(VerbSet.get(a));
				}
			}*/
			
			ArrayList<PossibleCommand> possibleCommands = new ArrayList<PossibleCommand>();
			
//			ArrayList<PossibleCommand> possibleCommands = new ArrayList<PossibleCommand>();
			
			for(int a = 0; a < Commands.size(); a++)
			{
				Command command = Commands.get(a);
				VerbSet requiredVerb = command.getRequiredVerb();
				if(requiredVerb == null/* || verbs.length == 0*/)
				{
					//If there are no keywords, there is nothing to distinguish
					//this command. So disregard it, and give a warning
					System.err.println("Command must have a keyword, otherwise it's not distinguishable");
					continue;
				}
				/*int found = 0;
				
				for(int b = 0; b < verbs.length; b++)
				{
					//If the keyword exists in the command string
					if(verbs[b].containsVerb(words))
					{
						found++;
					}
				}*/
				
				int wordIndex;
				
				if((wordIndex = requiredVerb.containsVerb(words)) != -1/*found == verbs.length*/)
				{
					//This is a possible command.
					//Let's find out how likely this is the command the player wants.
					//The better the sum of the Parameter confidence levels, the better the chance
//					possibleCommandsBase.add(command);
					
					Parameter[] reqParams = command.getRequiredParameters();
					
					float confidence = 0f;
					
					if(reqParams != null)
					{
						
						for(int b = 0; b < reqParams.length; b++)
						{
							//This isn't a good way of determining parameter confidence.
							//i.e. One really good parameter and 1 really bad one, will bring
							//down the overall rating. Perhaps just choose the highest confidence level?
							//Wait no, that's good. If it's only 50% sure, then it should only be 50% sure
							//Not 100% sure, even though it completely doubts the second param...
							PGuess guesses = reqParams[b].findParameters(clone, sender, words);
							if(guesses != null)
							{
								float subConfidence = 0;
								
								for(int c = 0; c < guesses.size(); c++)
								{
									subConfidence += guesses.get(c).confidence;
								}
								
								subConfidence /= guesses.size();
								confidence += subConfidence;
							}
						}
					}
					possibleCommands.add(new PossibleCommand(command, confidence));
				}
			}
			
			if(possibleCommands.size() == 0)
			{
				System.out.println("No Commands Found");
				sender.addChatMessage(new ChatComponentText(clone.getCommandSenderName() + ": Sorry, I have no idea what you want me to do."));
				return;
			}
			
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
			
			Command theBestCommand = ((PossibleCommand)possibleCommands.get(0)).command;
			
			CommandTask ct = theBestCommand.getCommandExecutionDelegate();
			
			clone.getLookHelper().setLookPositionWithEntity(sender, 10, clone.getVerticalFaceSpeed());
			
			ct.setClone(clone);
			ct.setSender(sender);
			
			ct.startExecuting();
			
			
//			for(int a = 0; a < possibleCommands.size(); a++)
//			{
//				
//			}
			
			
//			ArrayList<PossibleCommand> possibleCommands = new ArrayList<PossibleCommand>();
//			for(int a = 0; a < possibleCommandsBase.size(); a++)
//			{
//				Command compossibleCommandsBase.get(a);
//			}
			
		}
	}
	
	public static class PossibleCommand{
		
		Command command;
		float chance;
		
		public PossibleCommand(Command command, float chance){
			this.command = command;
			this.chance = chance;
		}
		
	}
	
	public Command interpretCommand(String command){
		
		
		
		
		
		return null;
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
