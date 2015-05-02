package net.jamezo97.clonecraft.command;

import net.jamezo97.clonecraft.command.parameter.PGuess;
import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.task.CommandTaskOnce;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class CommandFollow extends Command{
	
	WordSet confirmation = new WordSet("Okay!", "Okay @PLAYER!", "Okey Dokey!", "Sure can do", "I'll follow you anywhere.",
			"Fiine. If I have to", "Okay then. Let's go!", "Sure thing.", "Let's go @PLAYER");
	

	public CommandFollow() {
		super(new Parameter[]{}, new Parameter[]{Parameters.p_player}, new Parameter[]{});
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.follow;
	}

	@Override
	public CommandTask getCommandExecutionDelegate() {
		return new CommandTaskOnce(){

			@Override
			public void execute() {
				PGuess playerParam = this.paramSet.getParamValue(Parameters.p_player);
				Object player = playerParam.getBestGuess().value;

				
				
				if(player instanceof EntityPlayer){
					this.clone.setOwner(((EntityPlayer)player).getCommandSenderName());
					this.clone.getOptions().follow.set(true);
					this.clone.getOptions().guard.set(false);
					this.clone.getOptions().setDirty();
					
					this.clone.say(confirmation.getRandom().replace("@PLAYER", this.commanderName), this.commander);
			
				}
			}
			
		};
	}
	
	

	
	
}
