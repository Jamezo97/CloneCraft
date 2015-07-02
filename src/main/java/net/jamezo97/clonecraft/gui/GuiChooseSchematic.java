package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.ai.EntityAIBuild;
import net.jamezo97.clonecraft.network.Handler12BuildSchematic;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiChooseSchematic extends GuiScreen{

	EntityClone clone;
	
	GuiScreen parentScreen;
	
	public GuiChooseSchematic(EntityClone clone, GuiScreen parentScreen) {
		this.clone = clone;
		this.parentScreen = parentScreen;
	}
	
	GuiScrollableSchematic schematicList;
	
	
	float scale = 1.0f;
	
	float rotateX = 0.0f;
	
	float rotateY = 0.0f;
	
	float transX = 0.0f;
	
	float transY = 0.0f;
	
	float previewXPos = 214;
	
	float previewYPos = 34;
	
	float previewXPos2 = width-9;
	
	float previewYPos2 = height-9;

	@Override
	public void drawScreen(int mX, int mY, float partial) {
		this.drawDefaultBackground();
		this.drawDefaultBackground();
		super.drawScreen(mX, mY, partial);
		this.schematicList.draw(mX, mY);
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, "Show:", 5, 11, 0xffffffff);
		
		{
			this.drawRect(200+10, 30, width-5, height-5, 0x66ffffff);
			this.drawRect(200+12, 32, width-7, height-7, 0x66ffffff);
			this.drawRect(200+14, 34, width-9, height-9, 0x66ffffff);
		}
		
		
		if(this.schematicList.getSelectedSchematic() != null)
		{
			btnBuildIt.enabled = true;
			
			if(this.displayMode != 0)
			{
				Schematic schem = this.schematicList.getSelectedSchematic();
				
				
				
				GL11.glTranslatef(0, 0, 100);
				
//				GL11.glDisable(GL11.GL_CULL_FACE);

				
				this.schematicList.renderCenteredSchematicAt(schem, previewXPos+transX, previewYPos+transY, 
						previewXPos2 - previewXPos, previewYPos2 - previewYPos, rotateX, rotateY, 0, scale);
				
				
//				GL11.glEnable(GL11.GL_CULL_FACE);
				
				GL11.glTranslatef(0, 0, -100);
			}
		}
		else
		{
			btnBuildIt.enabled = false;
		}
	}
	
	

	@Override
	protected void keyTyped(char charC, int p_73869_2_) {
		if (p_73869_2_ == 1)
        {
            this.mc.displayGuiScreen(parentScreen);
            
            if(parentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
		
		if(charC == 'r' || charC == 'R')
		{
			this.rotateX = 0;
			this.rotateY = 0;
			this.scale = 1.0f;
			this.transX = 0;
			this.transY = 0;
		}
	}
	
	int lastMX = 0;
	int lastMY = 0;
	

	@Override
	protected void mouseClickMove(int mX, int mY, int lastButton, long downTime)
	{
		super.mouseClickMove(mX, mY, lastButton, downTime);
		
		
		
		int dMX = lastMX - mX;
		int dMY = lastMY - mY;
		
		this.lastMX = mX;
		this.lastMY = mY;
		
		if(lastButton == 1)
		{
			float rX = dMX / 1.0f;
			float rY = dMY / 1.0f;
			
			this.rotateY -= rX;
			this.rotateX = (float)Math.sin(rotateY / 148 * Math.PI) * 12f;
		}
		else if(lastButton == 0)
		{
			this.transX -= dMX;
			this.transY -= dMY;
		}
		
	}



	@Override
	protected void mouseClicked(int mX, int mY, int button)
	{
		super.mouseClicked(mX, mY, button);
		this.schematicList.mousePress(mX, mY, button);
		
		
		
		this.lastMX = mX;
		this.lastMY = mY;
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
		else if(btn.id == 3)
		{
			
		}
		else if(btn.id == 4 && this.schematicList.getSelectedSchematic() != null)
		{
			Schematic schem = this.schematicList.getSelectedSchematic();
			
			Handler12BuildSchematic handler = new Handler12BuildSchematic(schem, clone);
			
			handler.sendToServer();
			
			clone.getBuildAI().setSchematic(null);
			
			this.mc.displayGuiScreen(null);
			
			this.mc.setIngameFocus();
			
		}
	}
	
	GuiButton btnNone, btnSelected, btnAll;
	
	GuiButton btnBuildIt;

	@Override
	public void initGui()
	{
		this.schematicList = new GuiScrollableSchematic(this, 5, 30, 200, height-35);
		
		this.buttonList.add(btnNone = 		new GuiButton(0, 35, 5, 40, 20, "None"));
		this.buttonList.add(btnSelected = 	new GuiButton(1, 75, 5, 60, 20, "Selected"));
		this.buttonList.add(btnAll = 		new GuiButton(2, 135, 5, 40, 20, "All"));

		switch(displayMode)
		{
			case 0: btnNone.enabled = false; break;
			case 1: btnSelected.enabled = false; break;
			case 2: btnAll.enabled = false; break;
		}
		
		
		this.buttonList.add(				new GuiButton(3, width-145, 5, 140, 20, "Server Schematics"));
		this.buttonList.add(btnBuildIt =	new GuiButton(4, width-145-80, 5, 70, 20, "Build It!"));
		
		/*if(this.schematicList.getSelectedSchematic() == -1)
		{
			EntityAIBuild buildAI = this.clone.getBuildAI();
			for(int a = 0; a < CloneCraft.INSTANCE.schematicList.getSchematics().size(); a++)
			{
				if(buildAI.getSchematic() == CloneCraft.INSTANCE.schematicList.getSchematics().get(a).schem)
				{
					this.schematicList.setSelected(a);
					continue;
				}
			}
			if(this.schematicList.getSelectedIndex() == -1)
			{
				buildAI.setSchematic(null);
			}
		}*/
		
		previewXPos = 214;
		previewYPos = 34;
		previewXPos2 = width-9;
		previewYPos2 = height-9;
	}
	
	

	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	@Override
	public void updateScreen() {
		yRotate += 0.5f;
		yRotate = yRotate % 360f;
		xRotate = (float)Math.sin(yRotate / 45 * Math.PI) * 20f;
		
		
		//For when the screen is resized. Buttons and their states are reset.

		if(!this.schematicList.isMouseOver(this.schematicList.lmx, this.schematicList.lmy))
		{
			scale += (Mouse.getDWheel())/5000.0f;
			if(scale < 0)
			{
				scale = 0;
			}
			if(scale > 5)
			{
				scale = 5;
			}
		}
		
	}

	@Override
	public void onGuiClosed() {
		CloneCraft.INSTANCE.schematicList.cleanSchematics();
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	

	
	
}
