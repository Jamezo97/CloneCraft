package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;

public abstract class Parameter <E> {
	
	/**
	 * Should return ordered by word index. Thus guess can be made. i.e. Action before Object.. Some times
	 * i.e. Hey Jamezo97, go kill some creepers. Kill before creepers.
	 * @param clone
	 * @param sender
	 * @param words
	 * @return
	 */
	public abstract PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] preWords);

	public abstract String getDefaultAskString();
	
}
