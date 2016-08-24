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
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiSlider;

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
		
		
		this.schematicList.draw(mX, mY);
		
//		System.out.println(this.schematicList.useFrameBuffer);
		
		
		if(!this.schematicList.useFrameBuffer)
	    {
	        GL11.glDisable(GL11.GL_LIGHTING);
	        GL11.glDisable(GL11.GL_FOG);
	        Tessellator tessellator = Tessellator.instance;
	        this.mc.getTextureManager().bindTexture(optionsBackground);
	        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	        tessellator.startDrawingQuads();
	        tessellator.setColorOpaque_I(4210752);
	        
	        this.draw(tessellator, 0, 0, schematicList.xPosition, this.height);
			this.draw(tessellator, 0, 0, this.width, schematicList.yPosition);
			this.draw(tessellator, 0, schematicList.yPosition+schematicList.height, this.width, this.height);
			this.draw(tessellator, schematicList.xPosition+schematicList.width, 0, this.width, this.height);
	        
	        tessellator.draw();
	    }
		
		
		

		int maxY = height-(itemsRequired==null?0:20) - 5;
		
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
			
			
			String stageName = "Unknown";
			switch(buildAi.getBuildState())
			{
			case 0: stageName = "Solid Blocks"; break;
			case 1: stageName = "Non-solid Blocks"; break;
			case 2: stageName = "Fire & Redstone"; break;
			case 3: stageName = "Finalizing"; break;
			}

			this.drawString(Minecraft.getMinecraft().fontRenderer, "Stage " + (buildAi.getBuildState()+1) + " of 4: " + stageName , minX+15, minY+60, 0xffffffff);
			

			this.drawString(Minecraft.getMinecraft().fontRenderer, "Complete:", minX+15, minY+90, 0xffffffff);
			
			
			progressBar.xPosition = minX+10;
			progressBar.yPosition = minY+100;
			progressBar.width = width-20;
			
			progressBar.drawButton(Minecraft.getMinecraft(), mX, mY);
			
			
		}
		
		
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, "Show:", 5, 11, 0xffffffff);
		
		
		
		
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, "Search:", 5, 37, 0xffffffff);
		
		searchField.drawTextBox();
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
				clone.getSyncer().sendValueToServer(Syncer.ID_BUILD);
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
				clone.getSyncer().sendValueToServer(Syncer.ID_IGNOREITEMS);
			}
		}
		else if(btn.id == 11)
		{
			System.out.println("Move");
		}
	}
	
	GuiButton btnNone, btnSelected, btnAll;
	
	GuiButton btnBuildIt;
	
	GuiTextField searchField;

	GuiCheckBox itemsRequired;
	
	GuiProgressBar progressBar;
	
	GuiSlider buildSpeed;
	

	@Override
	public void initGui()
	{
		this.schematicList = new GuiScrollableSchematic(this, 5, 55, 200, height-60);
		
		this.buttonList.add(btnNone = 		new GuiButton(0, 35, 5, 40, 20, "None"));
		this.buttonList.add(btnSelected = 	new GuiButton(1, 75, 5, 60, 20, "Selected"));
		this.buttonList.add(btnAll = 		new GuiButton(2, 135, 5, 40, 20, "All"));
		
		if(this.mc.thePlayer != null && this.mc.thePlayer.capabilities.isCreativeMode)
		{
			this.buttonList.add(itemsRequired = 	new GuiCheckBox(10, 210, height-18, "Creative Mode"));
			itemsRequired.setState(clone.getBuildAI().shouldIgnoreItems());
			

			this.buttonList.add(buildSpeed = new GuiSlider(11, width - 110, height-22, 100, 20, "Build Delay: ", "", 0.0, 19.0, this.clone.getBuildAI().getBuildSpeed(), false, true));
			
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
		
		progressBar = new GuiProgressBar(20, 200, 50, 200, 20, "Progress");
		
		progressBar.setValue(0);
		
	}
	
	

	float xRotate = 0.0f;
	float yRotate = 0.0f;
	
	int lastBuildSpeed = 0;
	
	@Override
	public void updateScreen() 
	{
		yRotate += 0.5f;
		yRotate = yRotate % 360f;
		xRotate = (float)Math.sin(yRotate / 45 * Math.PI) * 20f;
		
		this.guiRender.toRender = this.schematicList.getSelectedSchematic();
		this.guiRender.doRender = this.displayMode != 0;
		
		if(clone.getBuildAI().getSchemFullSize() != 0)
		{
			progressBar.setValue((float)clone.getBuildAI().getIndex() / (float)clone.getBuildAI().getSchemFullSize());
		}
		else
		{
			progressBar.setValue(0.0f);
		}
		
		if(buildSpeed != null)
		{
			if(this.buildSpeed.getValueInt() != lastBuildSpeed)
			{
				this.clone.getBuildAI().setBuildSpeed(lastBuildSpeed = (this.buildSpeed.getValueInt()));
				this.clone.getSyncer().sendValueToServer(Syncer.ID_BUILDSPEED);
			}
			else if(this.buildSpeed.getValueInt() != this.clone.getBuildAI().getBuildSpeed())
			{
				this.buildSpeed.setValue(lastBuildSpeed = this.clone.getBuildAI().getBuildSpeed());
			}
		}
		
		
		
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
