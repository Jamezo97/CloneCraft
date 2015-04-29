package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.task.CommandTaskOnce;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.util.ChatComponentText;

public class CommandHello extends Command{
	
	public CommandHello(){
		super(null, null, null);
	}
	
	String[] returnMessages = new String[]{
			"Top of the morning to ye @PLAYER",
			"G'Day @PLAYER",
			"Yeha?",
			"Whaddya want @PLAYER?",
			"Leave me alone",
			"What can I do yer for?",
			"Hello there @PLAYER. Lovely weather today isn't it?",
			"Howsit going @PLAYER?",
			"Rack off mate. I'm doing just fine here",
			"I'm bored @PLAYER",
			"Do I look stupid to you? Don't answer that!",
			"How much wood could a wook chuck chop if a wood chuck could chop wood?",
			"Howdy",
			"You smell lovely today. Not that.. I was trying to.. smell you..",
			"Bless you too kind person"
			
	};
	

	@Override
	public CommandTask getCommandExecutionDelegate() {
		return new CommandTaskOnce(){

			@Override
			public void execute() {
				this.commander.addChatMessage(new ChatComponentText("<" + this.clone.getCommandSenderName() + "> " + returnMessages[this.clone.getRNG().nextInt(returnMessages.length)].replace("@PLAYER", this.commander.getCommandSenderName())));
				
				if(this.clone.isCollidedVertically){
					this.clone.motionY += 0.5;
					this.clone.isAirBorne = true;
				}
			
			}
			
		};
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.hello;
	}
	
	public float getConfidenceMultiplier(){
		return 0.2f;
	}

}
