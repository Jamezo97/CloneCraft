package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.entity.player.EntityPlayer;

/**
 * i.e. Some, all, a couple, 15, 23 (but not decimal numbers)
 * @author James
 *
 */
public class ParamQuantity extends Parameter{

	WordSet ws_some = new WordSet("some", "few");
	
	WordSet ws_couple = new WordSet("couple", "two");
	
	@Override
	public PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		PGuess guesses = new PGuess(this);
		
		for(int a = 0; a < words.length; a++)
		{
			try
			{
				
				int value = Integer.parseInt(words[a]);
				double dValue = Double.parseDouble(words[a]);
				if(value > 0 && value == dValue)
				{
					guesses.add(new ParamGuess(value, 0.5f));
					continue;
				}
			}
			catch (Exception e)
			{
			}
		}
		
		if(ws_some.containsWord(words) != -1)
		{
			guesses.add(new ParamGuess(clone.getRNG().nextInt(4)+2, 0.4f));
		}
		if(ws_couple.containsWord(words) != -1)
		{
			guesses.add(new ParamGuess(2, 0.4f));
		}
		
		return guesses;
	}

	@Override
	public String getDefaultAskString() {
		return "How many?";
	}
	
	

}
