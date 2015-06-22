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
		this.drawDefaultBackground();
		this.drawDefaultBackground();
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
	
	public int displayMode = 0;

	@Override
	protected void actionPerformed(GuiButton btn)
	{
		if(btn.id == 0)
		{
			displayMode = 0;
			btnNone.enabled = false;
			btnSelected.enabled = true;
			btnAll.enabled = true;
		}
		else if(btn.id == 1)
		{
			displayMode = 1;
			btnNone.enabled = true;
			btnSelected.enabled = false;
			btnAll.enabled = true;
		}
		else if(btn.id == 2)
		{
			displayMode = 2;
			btnNone.enabled = true;
			btnSelected.enabled = true;
			btnAll.enabled = false;
		}
	}
	
	GuiButton btnNone, btnSelected, btnAll;

	@Override
	public void initGui() {
		this.schematicList = new GuiScrollableSchematic(this, 5, 5, 200, height-10);
		this.buttonList.add(btnNone = 		new GuiButton(0, width-145, 5, 40, 20, "None"));
		btnNone.enabled = false;
		this.buttonList.add(btnSelected = 	new GuiButton(1, width-105, 5, 60, 20, "Selected"));
		this.buttonList.add(btnAll = 		new GuiButton(2, width-45, 5, 40, 20, "All"));
	}

	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	@Override
	public void updateScreen() {
		yRotate += 0.5f;
		yRotate = yRotate % 360f;
		xRotate = (float)Math.sin(yRotate / 45 * Math.PI) * 20f;
	}

	@Override
	public void onGuiClosed() {
		// TODO Auto-generated method stub
		super.onGuiClosed();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	

	
	
}
