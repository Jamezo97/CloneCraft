package net.jamezo97.clonecraft.render;

import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class LifeInducerRenderHandler extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler {
	
//	int t1 = 0;
//	int t2 = 0;
//	int t3 = 0;

    private static final ResourceLocation tex = new ResourceLocation("clonecraft:textures/entity/lifeInducer/lifeInducer128.png");
	ModelLifeInducer model = new ModelLifeInducer();
	
	boolean was = false;
	
	
	public void renderLifeInducer(TileEntityLifeInducer te, double x, double y, double z, float partial){
		
		if(Minecraft.getMinecraft().isGamePaused()){
			if(was == false){
				model = new ModelLifeInducer();
			}
			was = true;
		}else{
			was = false;
		}
		
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(tex);
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
        GL11.glScalef(1.0F, -1.0F, -1.0F);
//        GL11.glTranslatef(0.5f, 0.5f, 0.5f);
////        GL11.glRotatef(t1, 0, 1, 0);
//        GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
        
//        if(was){
//        	t1++;
//        	if(t1 > 600){
//        		t1 = 0;
////        		was = false;
//        	}
//        }else{
//        	t1--;
//        	if(t1 <= 0){
//        		was = true;
//        	}
//        }
        
//        float rad = (float)(t1 / 600f * 20f);
//        
//        float scale = (float)(Math.sin(rad*1.8223)*(rad/12)+(rad/10))+1;
//        
//        GL11.glTranslatef(0.5f, 1, 0.5f);
//        GL11.glScalef(scale, scale, scale);
//        GL11.glTranslatef(-0.5f, -1, -0.5f);
        if(te != null){
        	model.collector.rotateAngleY=model.armOut.rotateAngleY=model.armDown.rotateAngleY = (float) (((te.lastSpin + (te.spin-te.lastSpin)*partial) / 20.0f)*2*Math.PI);
        }else{
        	model.collector.rotateAngleY=model.armOut.rotateAngleY=model.armDown.rotateAngleY = 0;
        }
        model.collector.rotateAngleY = 0;
		model.renderAll();
		GL11.glPopMatrix();
		if(te != null){
			GL11.glPushMatrix();
	        GL11.glTranslatef((float)x, (float)y, (float)z);
			for(int a = 0; a < te.elec.size(); a++){
				te.elec.get(a).render(partial);
			}
			GL11.glPopMatrix();
		}
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
	
	@Override
	public void renderTileEntityAt(TileEntity teGeneric, double x, double y, double z, float partial) {
		TileEntityLifeInducer te = (TileEntityLifeInducer)teGeneric;
		renderLifeInducer(te, x, y, z, partial);
	}

	int renderID;
	
	public LifeInducerRenderHandler(int renderID) {
		this.renderID = renderID;
	}

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
        renderLifeInducer(null, 0, 0, 0, 0);
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
