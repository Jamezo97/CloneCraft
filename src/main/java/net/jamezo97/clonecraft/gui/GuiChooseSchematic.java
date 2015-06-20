package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiChooseSchematic extends GuiScreen{

	EntityClone clone;
	
	GuiScreen parentScreen;
	
	public GuiChooseSchematic(EntityClone clone, GuiScreen parentScreen) {
		this.clone = clone;
		this.parentScreen = parentScreen;
	}
	
	GuiScrollableSchematic schematicList;

	@Override
	public void drawScreen(int mX, int mY, float partial) {
		super.drawScreen(mX, mY, partial);
		this.schematicList.draw(mX, mY);
	}

	@Override
	protected void keyTyped(char p_73869_1_, int p_73869_2_) {
		if (p_73869_2_ == 1)
        {
            this.mc.displayGuiScreen(parentScreen);
            
            if(parentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
	}
	
	

	@Override
	protected void mouseClicked(int mX, int mY, int button) {
		super.mouseClicked(mX, mY, button);
		this.schematicList.mousePress(mX, mY, button);
	}

	@Override
	protected void actionPerformed(GuiButton buttonId) {
		
	}

	@Override
	public void initGui() {
		this.schematicList = new GuiScrollableSchematic(this, (width-200)/2, (height-200)/2, 200, 200);
	}

	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	@Override
	public void updateScreen() {
		yRotate += 0.5f;
		yRotate = yRotate % 360f;
		xRotate = (float)Math.sin(yRotate / 180 * Math.PI) * 20f;
	}

	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		super.onGuiClosed();
	}

	@Override
	public void drawBackground(int p_146278_1_) {
		// TODO Auto-generated method stub
		super.drawBackground(p_146278_1_);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	

	
	
}
