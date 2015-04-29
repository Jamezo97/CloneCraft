package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public class ParameterCourtesy extends Parameter{

	@Override
	public PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		PGuess pguess = new PGuess(this);
		for(int a = 0; a < words.length; a++){
			if(words[a].toLowerCase().equals("please")){
				pguess.add(new ParamGuess(words[a], 0.5f));
			}
		}
		return pguess;
	}

	@Override
	public String getDefaultAskString() {
		return "And where are your manners?";
	}

	
	
}
