package net.jamezo97.clonecraft.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelLifeInducer extends ModelBase{
	
	ModelRenderer base = new ModelRenderer(this, 0, 0).setTextureSize(128, 128);
	ModelRenderer collector = new ModelRenderer(this, 0, 0).setTextureSize(128, 128);
	ModelRenderer armOut = new ModelRenderer(this, 0, 24).setTextureSize(128, 128);
	ModelRenderer armDown = new ModelRenderer(this, 0, 26 ).setTextureSize(128, 128);
	
	public ModelLifeInducer(){
		base.addBox(0, 8, 0, 16, 8, 16, 0.0F);
		
		collector.addBox(6.5f-8.0f, 0.0f, 6.5f-8.0f, 3, 4, 3, 0.0F);
		
		collector.rotationPointX = 8.0f;
		collector.rotationPointY = 0.0f;
		collector.rotationPointZ = 8.0f;
		
		armOut.addBox(7.5f-8.0f, 1.0f, 7.5f-8.0f, 7, 1, 1, 0.0F);
		armOut.rotationPointX = 8.0f;
		armOut.rotationPointY = 0.0f;
		armOut.rotationPointZ = 8.0f;
		
		armDown.addBox(13.5f-8.0f, 2.0f, 7.5f-8.0f, 1, 6, 1, 0.0F);
		armDown.rotationPointX = 8.0f;
		armDown.rotationPointY = 0.0f;
		armDown.rotationPointZ = 8.0f;
	}

	public void renderAll(){
		float f = 0.0625F;
		
		
		base.render(0.0625F);
		collector.render(0.0625F);
		
		GL11.glPushMatrix();
		
		int q = 8;
		float rote = 360.0f  / ((float)q);
		
		for(int a = 0; a < q; a++){
			GL11.glTranslatef(armOut.rotationPointX*f, armOut.rotationPointY*f, armOut.rotationPointZ*f);
			GL11.glRotatef(rote, 0, 1, 0);
			GL11.glTranslatef(-armOut.rotationPointX*f, -armOut.rotationPointY*f, -armOut.rotationPointZ*f);
			armOut.render(0.0625F);
			armDown.render(0.0625F);
		}
		
		
		
		GL11.glPopMatrix();
		
		
		
    }
	
	

}
