package net.jamezo97.clonecraft.gui;

import org.lwjgl.opengl.GL11;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiChooseAttackEntities extends GuiScreen{

	GuiScrollableEntities scrollable;
	
	EntityClone clone;
	
	GuiScreen parent;

	ResourceLocation backgroundImageResource = new ResourceLocation("clonecraft:textures/gui/chooseEntities.png");
	ResourceLocation backgroundImageResource_hollow = new ResourceLocation("clonecraft:textures/gui/chooseEntities_hollow.png");
	
	int xSize, ySize;
	
	public GuiChooseAttackEntities(EntityClone clone, GuiScreen parent) {
		this.clone = clone;
		this.parent = parent;
		xSize = 176;
		ySize = 212;
	}



	@Override
	public void drawScreen(int mX, int mY, float partial)
	{
		drawDefaultBackground();
		
		int xPosition = (width-xSize)/2;
		int yPosition = (height-ySize)/2;
		

		
		if(scrollable.useFrameBuffer)
		{
			mc.renderEngine.bindTexture(backgroundImageResource);
			
			this.drawTexturedModalRect(xPosition, yPosition, 0, 0, 256, ySize);
			
			super.drawScreen(mX, mY, partial);
			
			scrollable.draw(mX, mY);
			
			search.drawTextBox();
			
			this.drawString(mc.fontRenderer, search_label, xPosition+4, yPosition+8, 0xffffffff);
		}
		else
		{
			
			int BG = 0xFFFFffff;

			GL11.glDisable(GL11.GL_DEPTH_TEST);
			drawBackground(1);
			GL11.glEnable(GL11.GL_DEPTH_TEST);

			scrollable.draw(mX, mY);
			

			GL11.glDisable(GL11.GL_DEPTH_TEST);
			

//			public void drawBackground(int p_146278_1_)
		    {
		        GL11.glDisable(GL11.GL_LIGHTING);
		        GL11.glDisable(GL11.GL_FOG);
		        Tessellator tessellator = Tessellator.instance;
		        this.mc.getTextureManager().bindTexture(optionsBackground);
		        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		        tessellator.startDrawingQuads();
		        tessellator.setColorOpaque_I(4210752);
		        
		        this.draw(tessellator, 0, 0, scrollable.xPosition, height);
				this.draw(tessellator, 0, 0, width, scrollable.yPosition);
				this.draw(tessellator, 0, scrollable.yPosition+scrollable.height, width, height);
				this.draw(tessellator, scrollable.xPosition+scrollable.width, 0, width, height);
		        
		        tessellator.draw();
		    }
			
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			
			
			mc.renderEngine.bindTexture(backgroundImageResource_hollow);

			GL11.glEnable(GL11.GL_BLEND);
	        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			
			this.drawTexturedModalRect(xPosition, yPosition, 0, 0, 256, ySize);
			
			super.drawScreen(mX, mY, partial);
			
			search.drawTextBox();
			

			GL11.glDisable(GL11.GL_BLEND);
			
			this.drawString(mc.fontRenderer, search_label, xPosition+4, yPosition+8, 0xffffffff);
			

			GL11.glEnable(GL11.GL_DEPTH_TEST);
			
		}
		
		
	}
	
	private void draw(Tessellator tessellator, int minX, int minY, int maxX, int maxY)
	{
        float f = 32.0F;
        
		tessellator.addVertexWithUV(minX, maxY, 0.0D, minX / f, minY / f + 1);
		tessellator.addVertexWithUV(maxX, maxY, 0.0D, maxX / f, minY / f + 1);
        tessellator.addVertexWithUV(maxX, minY, 0.0D, maxX / f, maxY / f + 1);
        tessellator.addVertexWithUV(minX, minY, 0.0D, minX / f, maxY / f + 1);
	}
	
	@Override
	protected void keyTyped(char c, int par2) {
		if(par2 == 1){
			mc.displayGuiScreen(parent);
			if(parent == null){
				mc.setIngameFocus();
			}
		}else{
			if(!search.isFocused()){
				search.setFocused(true);
			}
			String before = search.getText();
			search.textboxKeyTyped(c, par2);
			if(!search.getText().equals(before)){
				scrollable.updateViewable(search.getText());
			}
		}
	}

	@Override
	protected void mouseClicked(int x, int y, int button) {
		super.mouseClicked(x, y, button);
		scrollable.mousePress(x, y, button);
		search.mouseClicked(x, y, button);
	}
	
	String search_label;
	
	GuiTextField search;
	
	

	@Override
	public void initGui() {
		int xPosition = (width-xSize)/2;
		int yPosition = (height-ySize)/2;
		
//		int scrollableWidth = 176;
		
		search_label = StatCollector.translateToLocal("cc.search");
		int labelWidth = mc.fontRenderer.getStringWidth(search_label);
		
		scrollable = (GuiScrollableEntities)new GuiScrollableEntities(xPosition+4, yPosition+26, xSize-8, ySize-30, clone).setColourPallete(0x00000000, 0xffbbbbbb);
		search = new GuiTextField(mc.fontRenderer, xPosition+labelWidth+8, yPosition+4, xSize-12-labelWidth, 20);


		buttonList.add(new GuiButton(0, xPosition+174, yPosition+23+2, 78, 20, "Clear All"));
		buttonList.add(new GuiButton(1, xPosition+174, yPosition+23+2+28, 78, 20, "Select All"));
		buttonList.add(new GuiButton(2, xPosition+174, yPosition+23+2+28+28, 78, 20, "Select Mobs"));
		buttonList.add(new GuiButton(3, xPosition+174, yPosition+23+2+28+28+28, 78, 20, "Select Animals"));
		buttonList.add(new GuiButton(4, xPosition+174, yPosition+23+2+28+28+28+36, 78, 20, "Back"));
		
		//Clear All
		//Select All
		//Select All Mobs
		//Select All Animals
	}

	
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}



	@Override
	protected void actionPerformed(GuiButton b) {
		if(b.id == 0){
			clone.getOptions().attackables.clear();
		}else if(b.id == 1){
			clone.getOptions().attackables.selectAll();
		}else if(b.id == 2){
			clone.getOptions().attackables.selectMobs();
		}else if(b.id == 3){
			clone.getOptions().attackables.selectAnimals();
		}else if(b.id == 4){
			mc.displayGuiScreen(parent);
			if(parent == null){
				mc.setIngameFocus();
			}
		}
	}



	
	

	
	
}
