package net.jamezo97.clonecraft.town;

import net.minecraft.nbt.NBTTagCompound;

public class BlockCoord
{
	
	public int x, y, z;
	
	public BlockCoord(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public BlockCoord(NBTTagCompound nbt)
	{
		this.x = nbt.getInteger("x");
		this.y = nbt.getInteger("y");
		this.z = nbt.getInteger("z");
	}
	
	public void setPos(int x, int y, int z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void save(NBTTagCompound nbt)
	{
		nbt.setInteger("x", x);
		nbt.setInteger("y", y);
		nbt.setInteger("z", z);
	}

}
