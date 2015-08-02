package net.jamezo97.clonecraft.entity;

import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.world.World;

public class EntityExplodeCollapseFX extends EntityExplodeFX
{

	double dirX, dirY, dirZ;

	double startX, startY, startZ;

	int currentTick;

	boolean particleType;

	public EntityExplodeCollapseFX(World par1World, double posX, double posY, double posZ, double motionX, double motionY, double motionZ)
	{
		super(par1World, posX, posY, posZ, motionX, motionY, motionZ);
		double rand1 = (1 + (par1World.rand.nextFloat() / 10.0 - .05));

		dirX = motionX * (1 + (par1World.rand.nextFloat() / 10.0 - .05));
		dirY = motionY * (1 + (par1World.rand.nextFloat() / 10.0 - .05));
		dirZ = motionZ * (1 + (par1World.rand.nextFloat() / 10.0 - .05));
		this.motionX = 0;
		this.motionY = 0;
		this.motionZ = 0;
		startX = posX;
		startY = posY;
		startZ = posZ;
		particleMaxAge = 95 + par1World.rand.nextInt(6);
		particleType = par1World.rand.nextInt(6) == 0;
		if (particleType)
		{
			this.setParticleTextureIndex(48);
		}
	}

	@Override
	public void onUpdate()
	{
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge)
		{
			this.setDead();
		}
		if (!particleType)
		{
			this.setParticleTextureIndex(7 - this.particleAge * 6 / this.particleMaxAge);
		}

		double complete = ((double) particleAge) / ((double) particleMaxAge);

		double rad = complete * Math.PI;

		double multiplier = Math.sin(rad);

		this.setPosition(startX + (multiplier * dirX), startY + (multiplier * dirY), startZ + (multiplier * dirZ));

	}

}
