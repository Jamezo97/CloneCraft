package net.jamezo97.clonecraft.render;

import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class CentrifugeRenderHandler extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {
	
//	int t1 = 0;
//	int t2 = 0;
//	int t3 = 0;

    private static final ResourceLocation tex = new ResourceLocation("clonecraft:textures/entity/centrifuge/centrifugeTex128.png");
	ModelCentrifuge model = new ModelCentrifuge();
	
//	boolean was = false;
	
//	Lightning light = new Lightning(436, 6, -60, 437, 12, -60, 12, false, true);
	
	public void renderCentrifuge(TileEntityCentrifuge te, int i, double x, double y, double z, float partial){
//		t1++;
//		if(Minecraft.getMinecraft().isGamePaused()){
//			if(was == false){
//				model = new ModelCentrifuge();
//			}
//			was = true;
//		}else{
//			was = false;
//		}
		Minecraft mc = Minecraft.getMinecraft();
		mc.renderEngine.bindTexture(tex);
        GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
        GL11.glTranslatef(0.5F, 0.5F, 0.5F);
        short short1 = 0;

        if (i == 0)
        {
            short1 = 180;
        }

        if (i == 2)
        {
            short1 = 0;
        }

        if (i == 3)
        {
            short1 = 90;
        }

        if (i == 1)
        {
            short1 = -90;
        }

        GL11.glRotatef((float)short1, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        
        
        
        this.model.renderAll();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        
        
       
	    if(te != null){
	    	
	    	
	    	
	    	float scale = 0.35f;
	    	float iScale = 1/scale;
	    	float p = 0.0625F;
	    	float pIScale = 0.0625F * iScale;
	    	
	    	
	        GL11.glPushMatrix();
	        GL11.glTranslatef((float)x, (float)y, (float)z);
	        GL11.glTranslated(-te.xCoord, -te.yCoord, -te.zCoord);
//	        light.render(partial);
	        GL11.glTranslated(te.xCoord, te.yCoord, te.zCoord);
	        
	        GL11.glTranslatef(-0.5F, -(1-scale), 0.03125F);
	        GL11.glTranslatef(0.5F, 1.1F, -0.03125F);
	        if(i == 0){
	        	GL11.glTranslatef(10*p, 0.0f, 8*p);
	        	GL11.glRotatef(180, 0.0F, 1.0F, 0.0F);
	        }else if(i == 1){
	        	GL11.glTranslatef(8*p, 0.0f, 10*p);
	        	GL11.glRotatef(90, 0.0F, 1.0F, 0.0F);
	        }else if(i == 2){
	        	GL11.glTranslatef(6*p, 0.0f, 8*p);
	        }else if(i == 3){
	        	GL11.glTranslatef(8*p, 0.0f, 6*p);
	        	GL11.glRotatef(-90, 0.0F, 1.0F, 0.0F);
	        }
	        
//	        t1++;

	        float spinUp = te.speed /te.maxSpeed*20.0f;
//	        spinUp = (float)Math.sqrt(spinUp) * 4.690416F;
	        float rotateBase = (float)((te.interpolate(partial, te.lastSpin, te.spin) / 12F) * 360.0F)-90;
        	for(int a = 1; a < te.getSizeInventory(); a++){
        		if(te.getStackInSlot(a) != null){
        			GL11.glPushMatrix();
        			GL11.glRotatef((float)-rotateBase + (te.getSizeInventory()-a)*45.0f , 0.0F, 1.0F, 0.0F);
        	        GL11.glRotatef((float)-spinUp, 1.0F, 0.0F, 0.0F);
        	        GL11.glScalef(scale, scale, scale);
        	        GL11.glTranslatef(-0.5F, -1.0F, 0.40F);
        			this.renderItemstack(te.getStackInSlot(a));
        			GL11.glPopMatrix();
        		}
        	}
        	GL11.glPopMatrix();
        	
        }
        
        
	}
	
	static RenderItem ri = new RenderItem();
	public static void renderItemstack(ItemStack stack){
		RenderManager renderManager = RenderManager.instance;
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		for(int a = 0; a < stack.getItem().getRenderPasses(stack.getItemDamage()); a++){
			IIcon par2Icon = stack.getItem().getIcon(stack, a);
	        float f4 = par2Icon.getMinU();
	        float f5 = par2Icon.getMaxU();
	        float f6 = par2Icon.getMinV();
	        float f7 = par2Icon.getMaxV();

	        GL11.glPushMatrix();
	        float f12 = 0.0625F;
            texturemanager.bindTexture(texturemanager.getResourceLocation(stack.getItemSpriteNumber()));
	        int colour = stack.getItem().getColorFromItemStack(stack, a);
	        int red = (colour >> 16) & 0xff;
	        int green = (colour >> 8) & 0xff;
	        int blue = colour & 0xff;
	        float redF = ((float)red)/255.0f;
	        float greenF = ((float)green)/255.0f;
	        float blueF = ((float)blue)/255.0f;
	        GL11.glColor4f(redF, greenF, blueF, 1.0f);
	        ItemRenderer.renderItemIn2D(Tessellator.instance, f5, f6, f4, f7, par2Icon.getIconWidth(), par2Icon.getIconHeight(), f12);
	        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	        GL11.glPopMatrix();
		}
	}
	
	
	@Override
	public void renderTileEntityAt(TileEntity teGeneric, double x, double y, double z, float partial) {
		TileEntityCentrifuge te = (TileEntityCentrifuge)teGeneric;
		float f1 = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partial;
        f1 = 1.0F - f1;
        f1 = 1.0F - f1 * f1 * f1;
        model.lid.rotateAngleX = (-(f1 * (float)Math.PI / 2.0F))*0.8f;
		model.rotateCentrifuge = (float) ((te.interpolate(partial, te.lastSpin, te.spin) / 6F) * Math.PI);
		
		renderCentrifuge(te, te.getBlockMetadata(), x, y, z, partial);
	}

	int renderID;
	
	public CentrifugeRenderHandler(int renderID) {
		this.renderID = renderID;
		
		
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        model.lid.rotateAngleX = -1f;
        renderCentrifuge(null, 2, 0, 0, 0, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return true;
	}

	@Override
	public int getRenderId() {
		return renderID;
	}

}
