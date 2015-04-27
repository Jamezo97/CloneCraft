package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public abstract class Parameter {

	public abstract ParamGuess[] findParameters(EntityClone clone, EntityPlayer sender, String[] words);
	
}
