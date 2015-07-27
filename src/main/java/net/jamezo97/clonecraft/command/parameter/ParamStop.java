package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.entity.player.EntityPlayer;

public class ParamStop extends Parameter
{
	
	@Override
	public PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] words)
	{
		PGuess guesses = new PGuess(this);
		if(WordSet.stop.containsWord(words) != -1)
		{
			guesses.add(new ParamGuess(true, 0.5f));
		}
		return guesses;
	}

	@Override
	public String getDefaultAskString()
	{
		return "Do you want me to stop?";
	}
	

}
