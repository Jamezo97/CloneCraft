package net.jamezo97.clonecraft.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelCentrifuge extends ModelBase{
	
	float rotateCentrifuge = 0;
	

	int texSize = 128;
	
	/** The chest lid in the chest's model. */
    public ModelRenderer lid = (new ModelRenderer(this, 0, 0)).setTextureSize(texSize, texSize);
    /** The model of the bottom of the chest. */
    public ModelRenderer box = (new ModelRenderer(this, 0, 11)).setTextureSize(texSize, texSize);
    /** The chest's knob in the chest model. */
    public ModelRenderer sideBox = (new ModelRenderer(this, 0, 29)).setTextureSize(texSize, texSize);
    
    public ModelRenderer panel = (new ModelRenderer(this, 0, 48)).setTextureSize(texSize, texSize);

    public ModelRenderer inner = (new ModelRenderer(this, 0, 52)).setTextureSize(texSize, texSize);

    public ModelRenderer innerStand = (new ModelRenderer(this, 40, 0)).setTextureSize(texSize, texSize);

    public ModelCentrifuge()
    {
    	//this.chestLid.addBox(1, 9, 3, 10, 3, 10, 0.0F);
    	this.lid.addBox(1, -1, -10, 10, 1, 10, 0.0F);
    	this.lid.rotationPointX = 0F;
    	this.lid.rotationPointY = 8F;
    	this.lid.rotationPointZ = 13F;
    	
    	this.box.addBox(1.0F, 8.0F, 3.0F,  10, 8, 10, 0.0F);

    	this.sideBox.addBox(11, 7, 3, 4, 9, 10, 0.0F);
    	
    	
    	
    	this.panel.addBox(2, 9-12, 2-3, 8, 3, 1, 0.0F);
    	
    	this.panel.rotationPointX = 0F;
    	this.panel.rotationPointY = 12;
    	this.panel.rotationPointZ = 3F;

    	this.inner.addBox(2, 8, 4, 8, 7, 8, 0.0F);
    	

    	this.innerStand.addBox(4.5f-6, 9-9, 6.5f-8, 3, 6, 3, 0.0F);
    	this.innerStand.rotationPointX = 6F;
    	this.innerStand.rotationPointY = 9F;
    	this.innerStand.rotationPointZ = 8F;
    	
    }

    /**
     * This method renders out all parts of the chest model.
     */
    public void renderAll()
    {
//        this.chestKnob.rotateAngleX = this.chestLid.rotateAngleX;
    	this.panel.rotateAngleX = -0.32f;
        this.lid.render(0.0625F);
        this.sideBox.render(0.0625F);
        this.box.render(0.0625F);
        this.panel.render(0.0625F);
//        
        GL11.glDisable(GL11.GL_CULL_FACE);
        this.inner.render(0.0625F);
        GL11.glEnable(GL11.GL_CULL_FACE);
//
        this.innerStand.rotateAngleY = 0+rotateCentrifuge;
        this.innerStand.render(0.0625F);
        //45 deg = (Math.PI/4.0F) radians, = 0.7854
        this.innerStand.rotateAngleY = 0.7854F+rotateCentrifuge;
        this.innerStand.render(0.0625F);
    }
	
}
