package net.jamezo97.clonecraft.clone;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.world.World;

public class ClonePathNavigate extends PathNavigate{

	EntityClone clone;
	
	public ClonePathNavigate(EntityClone clone, World world) {
		super(clone, world);
		this.clone = clone;
	}

	@Override
	public PathEntity getPathToXYZ(double p_75488_1_, double p_75488_3_, double p_75488_5_)
	{
		float height = clone.height;
		clone.height = 1.8f;
		clone.boundingBox.maxY = clone.boundingBox.minY + clone.height;
		
		PathEntity temp = super.getPathToXYZ(p_75488_1_, p_75488_3_, p_75488_5_);

		clone.height = height;
		clone.boundingBox.maxY = clone.boundingBox.minY + clone.height;

		return temp;
	}

	@Override
	public PathEntity getPathToEntityLiving(Entity p_75494_1_) {
		
		float height = clone.height;
		clone.height = 1.8f;
		clone.boundingBox.maxY = clone.boundingBox.minY + clone.height;

		PathEntity temp = super.getPathToEntityLiving(p_75494_1_);

		clone.height = height;
		clone.boundingBox.maxY = clone.boundingBox.minY + clone.height;
	
		return temp;
	}
	
	

	
}
