package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public class ParameterEntity extends Parameter{

	@Override
	public PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDefaultAskString() {
		return "What kind of Entity? (me, you, Jamezo97, skeleton etc)";
	}

	
	
}
