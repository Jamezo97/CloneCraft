package net.jamezo97.clonecraft.render;

import java.util.Random;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;

import org.lwjgl.opengl.GL11;

public class ModelBipedCustom extends ModelBiped
{

	public boolean isEating = false;

	public ModelRenderer modelBreastLeft;
	public ModelRenderer modelBreastRight;

	public boolean isChestPlate;

	public ModelBipedCustom()
	{
		super();
	}

	public ModelBipedCustom(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_)
	{
		super(p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
		genBreasts(p_i1149_1_);
	}

	public ModelBipedCustom(float p_i1148_1_)
	{
		super(p_i1148_1_);
		genBreasts(p_i1148_1_);
	}

	public void genBreasts(float scale)
	{
		scale = scale / 10f;

		modelBreastLeft = new ModelRenderer(this, 18, scale == 0 ? 19 : 20);
		modelBreastRight = new ModelRenderer(this, 21, scale == 0 ? 19 : 20);

		this.modelBreastLeft.rotationPointX = 1.5f;
		this.modelBreastLeft.rotationPointY = 2f;
		this.modelBreastLeft.rotationPointZ = 0.0f;

		this.modelBreastLeft.addBox(-3.2f - this.modelBreastLeft.rotationPointX, 2 - this.modelBreastLeft.rotationPointY, -3f
				- this.modelBreastLeft.rotationPointZ, 3, 3, 3, scale);

		this.modelBreastRight.rotationPointX = 1.5f;
		this.modelBreastRight.rotationPointY = 2f;
		this.modelBreastRight.rotationPointZ = 0.0f;
		this.modelBreastRight.addBox(0.2f - this.modelBreastRight.rotationPointX, 2 - this.modelBreastRight.rotationPointY, -3f
				- this.modelBreastRight.rotationPointZ, 3, 3, 3, scale);

	}

	@Override
	public void setLivingAnimations(EntityLivingBase p_78086_1_, float p_78086_2_, float p_78086_3_, float partial)
	{
		super.setLivingAnimations(p_78086_1_, p_78086_2_, p_78086_3_, partial);

		if (p_78086_1_ instanceof EntityClone)
		{
			EntityClone clone = (EntityClone) p_78086_1_;

			if (clone.leftBParticle != null && clone.rightBParticle != null)
			{
				boolean wearingChestPlate = clone.inventory.armorItemInSlot(2) != null && clone.inventory.armorItemInSlot(2).getItem() instanceof ItemArmor;

				if (!wearingChestPlate)
				{

					double posX = interpolate(clone.prevPosX, clone.posX, partial);
					double posY = interpolate(clone.prevPosY, clone.posY, partial);

					double LX = interpolate(clone.lastBLX, clone.leftBParticle.posX, partial);
					double LY = interpolate(clone.lastBLY, clone.leftBParticle.posY, partial);

					double RX = interpolate(clone.lastBRX, clone.rightBParticle.posX, partial);
					double RY = interpolate(clone.lastBRY, clone.rightBParticle.posY, partial);

					this.modelBreastRight.rotateAngleX = (float) ((RY - posY) / clone.maxBDisp) * 0.2f;

					this.modelBreastLeft.rotateAngleX = (float) ((LY - posY) / clone.maxBDisp) * 0.2f;

					this.modelBreastRight.rotateAngleY = (float) ((RX - posX) / clone.maxBDisp) * 0.3f - 0.06f;
					this.modelBreastLeft.rotateAngleY = (float) ((LX - posX) / clone.maxBDisp) * 0.3f + 0.06f;

					this.modelBreastRight.offsetY = this.modelBreastRight.rotateAngleX / 4f;
					this.modelBreastLeft.offsetY = this.modelBreastLeft.rotateAngleX / 4f;

				}
				else
				{
					this.modelBreastRight.rotateAngleX = 0f;

					this.modelBreastLeft.rotateAngleX = 0f;

					this.modelBreastRight.rotateAngleY = 0f;
					this.modelBreastLeft.rotateAngleY = 0f;

					this.modelBreastRight.offsetY = 0f;
					this.modelBreastLeft.offsetY = 0f;
				}

				{
					float scale = (float) Math.max(-0.25f, 1 - clone.getScale());

					if (scale < 0)
					{
						scale *= 2.0;
					}

					this.modelBreastLeft.offsetZ = scale * (0.09f);
					this.modelBreastRight.offsetZ = scale * (0.09f);
				}

				if (wearingChestPlate)
				{
					this.modelBreastLeft.offsetZ -= 0.05f;
					this.modelBreastRight.offsetZ -= 0.05f;
				}

			}
		}
	}

	@Override
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float scale, Entity p_78087_7_)
	{
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, scale, p_78087_7_);
		
		if (isEating)
		{
			float diff = (this.bipedHead.rotateAngleY - this.bipedBody.rotateAngleY) / 2;
			this.bipedRightArm.rotateAngleX += (float) (50 * Math.PI / -180.0F);
			this.bipedRightArm.rotateAngleY += (float) (40 * Math.PI / -180.0F) + diff;
			float add = (float) Math.sin(p_78087_3_ * 1.8) / 3 + 0.2f;
			this.bipedHead.rotateAngleX += add;
			this.bipedHeadwear.rotateAngleX += add;
		}

		this.modelBreastLeft.rotateAngleY += this.bipedBody.rotateAngleY;
		this.modelBreastRight.rotateAngleY += this.bipedBody.rotateAngleY;

		this.modelBreastLeft.rotateAngleX += this.bipedBody.rotateAngleX;
		this.modelBreastRight.rotateAngleX += this.bipedBody.rotateAngleX;

		if (this.isSneak)
		{
			this.modelBreastRight.offsetZ = 0.06f;
			this.modelBreastLeft.offsetZ = 0.06f;
		}

	}

	public double interpolate(double prev, double now, float partial)
	{
		return prev + (now - prev) * partial;
	}

	/**
	 * Sets the models various rotation angles then renders the model.
	 */
	public void render(Entity p_78088_1_, float p_78088_2_, float p_78088_3_, float p_78088_4_, float p_78088_5_, float p_78088_6_, float p_78088_7_)
	{
		this.setRotationAngles(p_78088_2_, p_78088_3_, p_78088_4_, p_78088_5_, p_78088_6_, p_78088_7_, p_78088_1_);
		
		if (this.isChild)
		{
			float f6 = 2.0F;
			GL11.glPushMatrix();

			GL11.glScalef(1.5F / f6, 1.5F / f6, 1.5F / f6);
			GL11.glTranslatef(0.0F, 16.0F * p_78088_7_, 0.0F);
			this.bipedHead.render(p_78088_7_);
			GL11.glPopMatrix();
			GL11.glPushMatrix();
			GL11.glScalef(1.0F / f6, 1.0F / f6, 1.0F / f6);
			GL11.glTranslatef(0.0F, 24.0F * p_78088_7_, 0.0F);
			this.bipedBody.render(p_78088_7_);
			this.bipedRightArm.render(p_78088_7_);
			this.bipedLeftArm.render(p_78088_7_);
			this.bipedRightLeg.render(p_78088_7_);
			this.bipedLeftLeg.render(p_78088_7_);
			this.bipedHeadwear.render(p_78088_7_);
			GL11.glPopMatrix();
		}
		else
		{
			float scaleHead = 1.0f;

			boolean renderB = false;

			if (p_78088_1_ instanceof EntityClone)
			{
				EntityClone clone = (EntityClone) p_78088_1_;

				float scale = clone.getScale();
				float max = clone.getMaxScale();

				scaleHead = 0.5f + 1 / scale * (0.5f);

				renderB = clone.getOptions().female.get();

			}

			scaleHead = 1.0f;

			if (scaleHead > 1.0f)
			{

				GL11.glScalef(scaleHead, scaleHead, scaleHead);

				this.bipedHead.render(p_78088_7_);
				this.bipedHeadwear.render(p_78088_7_);

				GL11.glScalef(1 / scaleHead, 1 / scaleHead, 1 / scaleHead);

			}
			else
			{
				this.bipedHead.render(p_78088_7_);
				this.bipedHeadwear.render(p_78088_7_);
			}

			this.bipedBody.render(p_78088_7_);
			this.bipedRightArm.render(p_78088_7_);
			this.bipedLeftArm.render(p_78088_7_);
			this.bipedRightLeg.render(p_78088_7_);
			this.bipedLeftLeg.render(p_78088_7_);

			if (renderB)
			{
				this.modelBreastLeft.render(p_78088_7_);
				this.modelBreastRight.render(p_78088_7_);
			}
			

		}
	}

	@Override
	public ModelRenderer getRandomModelBox(Random p_85181_1_)
	{
		return super.getRandomModelBox(p_85181_1_);
	}

}
