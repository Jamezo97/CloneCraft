package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;

public class EntityAIBuild extends EntityAIBase{
	
	EntityClone clone;
	
	Schematic schem;
	
	/**
	 * From above, in OpenGL a + y rotation rotates anti clockwise.<br>
	 * When rotate =<ul>
	 * <li><b>0:</b> X+ Z+</li>
	 * <li><b>1:</b> X+ Z-</li>
	 * <li><b>2:</b> X- Z-</li>
	 * <li><b>3:</b> X- Z+</li>
	 * </ul>
	 */
	int rotate = 0;
	
	//The corner from which the current building is being created from.
	public int posX;
	public int posY;
	public int posZ;
	
	public void setSchematic(Schematic schem)
	{
		this.schem = schem;
	}
	
	public EntityAIBuild(EntityClone clone)
	{
		this.clone = clone;
	}

	@Override
	public boolean shouldExecute()
	{
		
		return false;
	}

	
	public NBTTagCompound saveState(NBTTagCompound nbt)
	{
		this.rotate = 0;
		return nbt;
	}
	
	public void loadState(NBTTagCompound nbt)
	{
		
	}

	public boolean renderOverlay() {
		return schem != null;
	}

	public Schematic getSchematic() {
		return schem;
	}
	
	
	
	
}
