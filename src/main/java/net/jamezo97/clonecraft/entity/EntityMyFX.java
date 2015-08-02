package net.jamezo97.clonecraft.entity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public abstract class EntityMyFX extends EntityFX
{

	public EntityMyFX(World par1World, double par2, double par4, double par6, double par8, double par10, double par12)
	{
		super(par1World, par2, par4, par6, par8, par10, par12);
	}

	public EntityMyFX(World par1World, double par2, double par4, double par6)
	{
		super(par1World, par2, par4, par6);
	}

	@Override
	public void renderParticle(Tessellator par1Tessellator, float par2, float par3, float par4, float par5, float par6, float par7)
	{

		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		super.renderParticle(par1Tessellator, par2, par3, par4, par5, par6, par7);
	}

	private final ResourceLocation texture = new ResourceLocation("CloneCraft:textures/fx.png");
}
