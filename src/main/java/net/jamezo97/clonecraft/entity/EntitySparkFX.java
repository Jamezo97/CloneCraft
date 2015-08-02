package net.jamezo97.clonecraft.entity;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.world.World;

public class EntitySparkFX extends EntityMyFX
{

	public EntitySparkFX(World par1World, double par2, double par4, double par6, float throwMultiplier)
	{
		super(par1World, par2, par4, par6);

		this.particleRed = 1.0F;
		this.particleGreen = 1.0F;
		this.particleBlue = 0f;

		this.motionX = (rand.nextFloat() / 5 - .1) * throwMultiplier;
		this.motionY = .1 * throwMultiplier;
		this.motionZ = (rand.nextFloat() / 5 - .1) * throwMultiplier;

		setParticleTextureIndex(rand.nextInt(3));
		setSize(.2f, .2f);
		particleScale = rand.nextFloat() + 2f;
		particleGravity = .75f;
		particleMaxAge = 10 + rand.nextInt(20);
	}

	@Override
	public void onUpdate()
	{
		super.onUpdate();
		particleGreen -= .02f;
	}

}
