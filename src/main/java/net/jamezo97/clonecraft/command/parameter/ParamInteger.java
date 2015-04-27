package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.util.SimpleList;
import net.minecraft.entity.player.EntityPlayer;

public class ParamInteger extends Parameter{

	@Override
	public ParamGuess[] findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		SimpleList<ParamGuess> guesses = new SimpleList<ParamGuess>();
		for(int a = 0; a < words.length; a++)
		{
			try{
				Integer i = Integer.parseInt(words[a]);
				
				if(words[a].contains("."))
				{
					guesses.add(new ParamGuess(i, 0.4f));
				}
				else
				{
					guesses.add(new ParamGuess(i, 0.6f));
				}
			}catch(Exception e){}
		}
		
		return guesses.size() == 0? null : guesses.toArray(new ParamGuess[guesses.size()]);
	}
	
	

}
