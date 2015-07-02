package net.jamezo97.clonecraft.clone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.jamezo97.clonecraft.network.Handler4UpdateOptions;
import net.minecraft.nbt.NBTTagCompound;

public class CloneOptions {

	public CloneOption fight;
	public CloneOption sprint;
	public CloneOption follow;
	public CloneOption pickUp;
	public CloneOption walkToItems;
	public CloneOption retaliate;
	public CloneOption guard;
	public CloneOption wander;
	public CloneOption jump;
	public CloneOption curious;
	public CloneOption breakExtraBlocks;
	public CloneOption stats;
	public CloneOption female;
	public CloneOption breakBlocks;
	public CloneOption farming;
	public CloneOption share;
	public CloneOption command;

	public final EntityClone clone;

	public final AttackableEntities attackables;

	public final BreakableBlocks breakables;

	public CloneOptions(EntityClone clone)
	{
		this.clone = clone;
		this.attackables = new AttackableEntities(this);
		this.breakables = new BreakableBlocks(this);
		initOptions();
	}

	ArrayList<CloneOption> allOptions = new ArrayList<CloneOption>();

	HashMap<Integer, CloneOption> idToOption = new HashMap<Integer, CloneOption>();

	public void initOptions()
	{
		addOption(fight = new CloneOption(0, "fight", true, this));
		addOption(sprint = new CloneOption(1, "sprint", false, this));
		addOption(follow = new CloneOption(2, "follow", false, this));
		addOption(pickUp = new CloneOption(3, "pickup", true, this));
		addOption(walkToItems = new CloneOption(4, "walkToItems", false, this));
		addOption(retaliate = new CloneOption(5, "retaliate", true, this));
		addOption(guard = new CloneOption(6, "guard", false, this));
		addOption(wander = new CloneOption(7, "wander", false, this));
		addOption(jump = new CloneOption(8, "jump", false, this));
		addOption(curious = new CloneOption(9, "curious", true, this));
		addOption(stats = new CloneOption(10, "stats", true, this));
		addOption(female = new CloneOption(11, "female", clone.getRNG().nextBoolean(), this));
		addOption(breakBlocks = new CloneOption(12, "breakBlocks", false, this));
		addOption(breakExtraBlocks = new CloneOption(13, "breakExtraBlocks", false, this));
		addOption(farming = new CloneOption(14, "farm", false, this));
		addOption(share = new CloneOption(15, "share", false, this));
		addOption(command = new CloneOption(16, "command", true, this));
		lastOptionData = this.toInteger();

	}

	private void addOption(CloneOption option)
	{
		allOptions.add(option);
		idToOption.put(option.id, option);
	}

	public int size()
	{
		return allOptions.size();
	}

	private boolean isDirty = false;

	public void setDirty()
	{
		this.isDirty = true;
	}

	int lastOptionData = 0;

	public void onTick()
	{
		attackables.tick(clone);
		breakables.tick(clone);
		
		if (!clone.worldObj.isRemote)
		{
			if (isDirty)
			{
				isDirty = false;
				clone.getDataWatcher().updateObject(EntityClone.ID_OPTIONS, this.toInteger());
			}
		}
		else if (clone.getCloneOptions() != lastOptionData)
		{
			lastOptionData = clone.getCloneOptions();
			this.fromInteger(lastOptionData);
		}
		else if(this.isDirty)
		{
			this.isDirty = false;
			new Handler4UpdateOptions(this.clone, this.toInteger()).sendToServer();
		}
	}

	public void loadFromInt(int[] array)
	{
		for (int a = 0; a < array.length; a++)
		{
			int id = this.getIdFromVal(array[a]);
			
			if (idToOption.containsKey(id))
			{
				idToOption.get(id).read(array[a]);
			}
		}
	}

	public void readFromNBT(NBTTagCompound nbt)
	{
		this.fromInteger(nbt.getInteger("CloneOptions"));
		this.attackables.load(nbt);
		this.breakables.load(nbt);
	}

	public void writeNBT(NBTTagCompound nbt)
	{
		nbt.setInteger("CloneOptions", this.toInteger());
		this.attackables.save(nbt);
		this.breakables.save(nbt);
	}

	public static int getIdFromVal(int val)
	{
		return val & idMask;
	}

	public static final int idMask = ~(1 << 30);

	public int[] toIntArray()
	{
		int[] data = new int[allOptions.size()];
		
		for (int a = 0; a < allOptions.size(); a++) {
			data[a] = allOptions.get(a).write();
		}
		return data;
	}

	public int toInteger() {
		int val = 0;
		
		for (int a = 0; a < allOptions.size(); a++) {
			val |= ((allOptions.get(a).get() ? 1 : 0) << a);
		}
		
		return val;
	}

	public void fromInteger(int value)
	{
		for (int a = 0; a < allOptions.size(); a++)
		{
			allOptions.get(a).set(((value >> a) & 1) == 1);
		}
	}

	public CloneOption getOptionByIndex(int a)
	{
		return allOptions.get(a);
	}

}
