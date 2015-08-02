package net.jamezo97.clonecraft.clone.ai;

import net.minecraft.util.MovingObjectPosition;

public class CanSeeEntry
{

	public boolean canSeeAtAll = false;

	public MovingObjectPosition mop = null;

	public void setSee(boolean b)
	{
		this.canSeeAtAll = b;
	}

	public void setMop(MovingObjectPosition mop)
	{
		this.mop = mop;
		if (mop == null)
		{
			this.canSeeAtAll = true;
		}
	}

	public boolean getSee()
	{
		return canSeeAtAll;
	}

	public MovingObjectPosition getMop()
	{
		return this.mop;
	}

}
