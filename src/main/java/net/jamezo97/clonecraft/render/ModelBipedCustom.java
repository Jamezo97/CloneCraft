package net.jamezo97.clonecraft.render;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

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
        	
            if(p_78088_1_ instanceof EntityClone){
            	EntityClone clone = (EntityClone)p_78088_1_;
            	
//            	System.out.println(p_78088_2_);
            	
            	float scale = clone.getScale();
            	float max = clone.getMaxScale();
            	
            	scaleHead = 0.5f + 1/scale * (0.5f);
            	
            }
        	if(scaleHead > 1.0f){
        		
        		
        		GL11.glScalef(scaleHead,  scaleHead, scaleHead);
        		
                this.bipedHead.render(p_78088_7_);
                this.bipedHeadwear.render(p_78088_7_);
                

        		GL11.glScalef(1/scaleHead,  1/scaleHead, 1/scaleHead);
                
                
        	}else{
                this.bipedHead.render(p_78088_7_);
                this.bipedHeadwear.render(p_78088_7_);
        	}
        	
            this.bipedBody.render(p_78088_7_);
            this.bipedRightArm.render(p_78088_7_);
            this.bipedLeftArm.render(p_78088_7_);
            this.bipedRightLeg.render(p_78088_7_);
            this.bipedLeftLeg.render(p_78088_7_);
        }
    }

}
