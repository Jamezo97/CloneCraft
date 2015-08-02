package net.jamezo97.clonecraft.clone;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.StatCollector;

public class CloneOption
{

	public final int id;

	final String unlocName;

	final String unlocDesc;

	boolean value = false;

	CloneOptions options;

	public CloneOption(int id, String name, boolean defaultValue, CloneOptions options)
	{
		this.id = id;
		this.unlocName = "clonecraft.coption." + name;
		this.unlocDesc = "clonecraft.coption.desc." + name;
		this.value = defaultValue;
		this.options = options;
	}

	public String getName()
	{
		return StatCollector.translateToLocal(unlocName);
	}

	public boolean get()
	{
		return value;
	}

	public void set(boolean value)
	{
		if (value != this.value)
		{
			options.setDirty();
		}
		this.value = value;
	}

	public int write()
	{
		return id | ((value ? 1 : 0) << 30);
	}

	public void read(int data)
	{
		this.value = ((data >> 30) & 1) == 1;
	}

	public String getInfo()
	{
		return StatCollector.translateToLocal(unlocDesc);
	}

}
