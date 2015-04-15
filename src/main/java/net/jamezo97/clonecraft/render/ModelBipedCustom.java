package net.jamezo97.clonecraft.render;

import net.jamezo97.clonecraft.CloneCraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class ModelBipedCustom extends ModelBiped{

	public boolean isEating = false;
	
	public ModelBipedCustom() {
		super();
	}

	public ModelBipedCustom(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_,
			int p_i1149_4_) {
		super(p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
	}

	public ModelBipedCustom(float p_i1148_1_) {
		super(p_i1148_1_);
	}

	@Override
	public void setRotationAngles(float p_78087_1_, float p_78087_2_, float p_78087_3_, float p_78087_4_, float p_78087_5_, float p_78087_6_, Entity p_78087_7_) {
		super.setRotationAngles(p_78087_1_, p_78087_2_, p_78087_3_, p_78087_4_, p_78087_5_, p_78087_6_, p_78087_7_);
		if(isEating){
			float diff = (this.bipedHead.rotateAngleY - this.bipedBody.rotateAngleY)/2;
			this.bipedRightArm.rotateAngleX += (float) (50 * Math.PI / -180.0F);
			this.bipedRightArm.rotateAngleY += (float) (40 * Math.PI / -180.0F) + diff;
			float add = (float)Math.sin(p_78087_3_ * 1.8)/3 + 0.2f;
			this.bipedHead.rotateAngleX += add;
			this.bipedHeadwear.rotateAngleX += add;
		}
		
		
	}

}
