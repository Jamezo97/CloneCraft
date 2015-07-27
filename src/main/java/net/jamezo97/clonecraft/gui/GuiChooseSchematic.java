package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.build.EntityAIBuild;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.sync.Syncer;
import net.jamezo97.clonecraft.network.Handler12BuildSchematic;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class GuiChooseSchematic extends GuiScreen{

	EntityClone clone;
	
	GuiScreen parentScreen;
	
	public GuiChooseSchematic(EntityClone clone, GuiScreen parentScreen) {
		this.clone = clone;
		this.parentScreen = parentScreen;
	}
	
	GuiScrollableSchematic schematicList;
	
	GuiRenderSchematic guiRender = null;

	@Override
	public void drawScreen(int mX, int mY, float partial)
	{
		this.drawDefaultBackground();
		this.drawDefaultBackground();
		

		int maxY = height-(itemsRequired==null?0:12) - 5;
		
		int minX = 210;
		int minY = 30;
		int width = (this.width-5) - minX;
		int height = (maxY) - minY;
		
		this.drawRect(minX    , minY     , minX + width    , maxY  , 0x66ffffff);
		this.drawRect(minX + 2, minY + 2, minX + width - 2, maxY-2, 0x66ffffff);
		this.drawRect(minX + 4, minY + 4 , minX + width - 4, maxY-4, 0x66ffffff);
		
		super.drawScreen(mX, mY, partial);
		
		EntityAIBuild buildAi = clone.getBuildAI();
		
		if(buildAi.isRunning() && buildAi.getSchematic() == null)
		{

			this.drawRect(minX + 4, minY + 4 , minX + width - 4, maxY-4, 0x66000000);
			
			this.drawCenteredString(mc.fontRenderer, "Currently Building", minX + width/2, minY + 10, 0xff77ee77);
			
			this.drawString(Minecraft.getMinecraft().fontRenderer, "Index: " + buildAi.getIndex(), minX+15, minY+40, 0xffffffff);
			
			
			
			
			
		}
		
		this.schematicList.draw(mX, mY);
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, "Show:", 5, 11, 0xffffffff);
		
		
		
		
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, "Search:", 5, 37, 0xffffffff);
		
		searchField.drawTextBox();
	}
	
	

	@Override
	protected void keyTyped(char charC, int p_73869_2_) 
	{
		if (p_73869_2_ == 1)
        {
            this.mc.displayGuiScreen(parentScreen);
            
            if(parentScreen == null)
            {
                this.mc.setIngameFocus();
            }
        }
		guiRender.keyTyped(charC, p_73869_2_);
		
		String old = searchField.getText();
		searchField.textboxKeyTyped(charC, p_73869_2_);
		String newStr = searchField.getText();
		
		if(old != null && newStr != null && old.length() != newStr.length())
		{
			this.schematicList.setSearchString(newStr);
		}
	}


	@Override
	protected void mouseClickMove(int mX, int mY, int lastButton, long downTime)
	{
		super.mouseClickMove(mX, mY, lastButton, downTime);
		
		this.guiRender.mouseClickMove(mX, mY, lastButton, downTime);
	}
	
	

	
	@Override
	protected void mouseClicked(int mX, int mY, int btn)
	{
		super.mouseClicked(mX, mY, btn);
		this.schematicList.mousePress(mX, mY, btn);
		this.guiRender.mouseClicked(mX, mY, btn);
		this.searchField.mouseClicked(mX, mY, btn);
	}

	public static int displayMode = 1;

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
		else if(btn.id == 4)
		{
			if(clone.getBuildAI().isRunning())
			{
				clone.getBuildAI().setBuilding(false);
				clone.getWatcher().sendValueToServer(Syncer.ID_BILD);
			}
			else if(this.schematicList.getSelectedSchematic() != null)
			{
				Schematic schem = this.schematicList.getSelectedSchematic();
				
				Handler12BuildSchematic handler = new Handler12BuildSchematic(schem, clone, clone.getBuildAI().shouldIgnoreItems());
				
				handler.sendToServer();
				
				clone.getBuildAI().setSchematic(null);
				
				this.mc.displayGuiScreen(null);
				
				this.mc.setIngameFocus();
			}
			
			
		}
		else if(btn.id == 10)
		{
//			System.out.println("Set to: " + itemsRequired.getState());
			
			clone.getBuildAI().ignoreItems(itemsRequired.getState());
			
			if(clone.getBuildAI().isRunning())
			{
				clone.getWatcher().sendValueToServer(Syncer.ID_IGNORE);
			}
		}
	}
	
	GuiButton btnNone, btnSelected, btnAll;
	
	GuiButton btnBuildIt;
	
	GuiTextField searchField;
	
	GuiCheckBox itemsRequired;
	

	@Override
	public void initGui()
	{
		this.schematicList = new GuiScrollableSchematic(this, 5, 55, 200, height-60);
		
		this.buttonList.add(btnNone = 		new GuiButton(0, 35, 5, 40, 20, "None"));
		this.buttonList.add(btnSelected = 	new GuiButton(1, 75, 5, 60, 20, "Selected"));
		this.buttonList.add(btnAll = 		new GuiButton(2, 135, 5, 40, 20, "All"));
		
		if(this.mc.thePlayer != null && this.mc.thePlayer.capabilities.isCreativeMode)
		{
			this.buttonList.add(itemsRequired = 	new GuiCheckBox(10, 210, height-15, "Unlimited Items"));
			itemsRequired.setState(clone.getBuildAI().shouldIgnoreItems());
		}

		this.searchField = new GuiTextField(mc.fontRenderer, 50, 30, 150, 20);
		
		displayMode = 1;
		
		switch(displayMode)
		{
			case 0: btnNone.enabled = false; break;
			case 1: btnSelected.enabled = false; break;
			case 2: btnAll.enabled = false; break;
		}
		
		int buildWidth = Math.min(200, width-220);
		
		this.buttonList.add(btnBuildIt =	new GuiButton(4, 220 + (width - 220 - 15 - buildWidth)/2, 5, buildWidth, 20, "Build It!"));
		
		this.buttonList.add(guiRender = new GuiRenderSchematic(214, 34, ((width-9) - 214), ((height-9)-34-(itemsRequired==null?0:12)) ) );
		
	}
	
	

	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	@Override
	public void updateScreen() 
	{
		yRotate += 0.5f;
		yRotate = yRotate % 360f;
		xRotate = (float)Math.sin(yRotate / 45 * Math.PI) * 20f;
		
		this.guiRender.toRender = this.schematicList.getSelectedSchematic();
		this.guiRender.doRender = this.displayMode != 0;
		
		
		
		searchField.updateCursorCounter();
		
		if(clone.getBuildAI().isRunning())
		{
			
			btnBuildIt.displayString = "Stop Building";
			btnBuildIt.enabled = true;
		}
		else
		{
			btnBuildIt.enabled = this.guiRender.toRender != null;
			btnBuildIt.displayString = "Build It!";
			
		}
		
	}

	@Override
	public void onGuiClosed()
	{
		CloneCraft.INSTANCE.schematicList.cleanSchematics();

	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	

	
	
}
