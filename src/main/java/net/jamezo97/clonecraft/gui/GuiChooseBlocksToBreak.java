package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiChooseBlocksToBreak extends GuiScreen {

	GuiScrollableBlocks scrollable;
	
	EntityClone clone;
	
	GuiScreen parent;
	
	ResourceLocation backgroundImageResource = new ResourceLocation("clonecraft:textures/gui/chooseEntities.png");
	
	int xSize, ySize;
	
	public GuiChooseBlocksToBreak(EntityClone clone, GuiScreen parent) {
		this.clone = clone;
		this.parent = parent;
		xSize = 176;
		ySize = 212;
	}



	@Override
	public void drawScreen(int mX, int mY, float partial) {
		drawDefaultBackground();
		
		int xPosition = (width-xSize)/2;
		int yPosition = (height-ySize)/2;
		
		mc.renderEngine.bindTexture(backgroundImageResource);
		
		this.drawTexturedModalRect(xPosition, yPosition, 0, 0, 256, ySize);
		
		super.drawScreen(mX, mY, partial);
		
		scrollable.draw(mX, mY);
		search.drawTextBox();
		
		this.drawString(mc.fontRenderer, search_label, xPosition+4, yPosition+8, 0xffffffff);
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
		
		search_label = StatCollector.translateToLocal("cc.search");
		int labelWidth = mc.fontRenderer.getStringWidth(search_label);
		
		scrollable = (GuiScrollableBlocks)new GuiScrollableBlocks(xPosition+4, yPosition+26, xSize-8, ySize-30, clone).setColourPallete(0x00000000, 0xffbbbbbb);
		search = new GuiTextField(mc.fontRenderer, xPosition+labelWidth+8, yPosition+4, xSize-12-labelWidth, 20);


		buttonList.add(new GuiButton(0, xPosition+174, yPosition+23+2, 78, 20, "Clear All"));
		buttonList.add(new GuiButton(1, xPosition+174, yPosition+23+2+28, 78, 20, "Select All"));
		buttonList.add(new GuiButton(2, xPosition+174, yPosition+23+2+28+28, 78, 20, "Select Ores"));
		buttonList.add(new GuiButton(3, xPosition+174, yPosition+23+2+28+28+28, 78, 20, "Select Trees"));
		buttonList.add(new GuiButton(4, xPosition+174, yPosition+23+2+28+28+28+36, 78, 20, "Back"));
	}

	
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}



	@Override
	protected void actionPerformed(GuiButton b) {
		if(b.id == 0){
			clone.getOptions().breakables.clear();
		}else if(b.id == 1){
			clone.getOptions().breakables.selectAll();
		}else if(b.id == 2){
			clone.getOptions().breakables.selectOres();
		}else if(b.id == 3){
			clone.getOptions().breakables.selectTrees();
		}else if(b.id == 4){
			mc.displayGuiScreen(parent);
			if(parent == null){
				mc.setIngameFocus();
			}
		}
	}
	
	

}
