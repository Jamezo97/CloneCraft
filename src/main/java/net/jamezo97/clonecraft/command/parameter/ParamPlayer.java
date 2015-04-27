package net.jamezo97.clonecraft.command.parameter;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public class ParamPlayer extends Parameter{

	@Override
	public ParamGuess[] findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		PGuess pg = new PGuess();
		
		List players = clone.worldObj.getEntitiesWithinAABB(EntityPlayer.class, clone.boundingBox.expand(32, 16, 32));
		
		for(int a = 0; a < players.size(); a++)
		{
			EntityPlayer ep = (EntityPlayer)players.get(a);
			String playerName = ep.getCommandSenderName().toLowerCase();
			
			
			for(int b = 0; b < words.length; b++)
			{
				if(words[b].toLowerCase().equals(playerName))
				{
					boolean isSameName = clone.getCommandSenderName().toLowerCase().equals(playerName);
					
					pg.add(new ParamGuess(ep, isSameName?0.4f:0.6f));
				}
			}
		}
		if(pg.size() == 0)
		{
			pg.add(new ParamGuess(sender, 0f));
		}
		return pg.build();
	}

	
	
}
