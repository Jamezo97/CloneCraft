package net.jamezo97.clonecraft.gui;

import java.util.ArrayList;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.build.EntityAIBuild;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.jamezo97.clonecraft.schematic.SchematicEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiScrollableSchematic extends GuiScrollable{
	
	ArrayList<SchematicEntry> visibleList = new ArrayList<SchematicEntry>();

	GuiChooseSchematic gui;
	
	int entryHeight = 60;
	
	String searchStringCurrent = null;
	
	public GuiScrollableSchematic(GuiChooseSchematic gui, int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		this.gui = gui;
		setSearchString(null);
	}
	
	public void setSearchString(String string)
	{
		visibleList.clear();
		deleteSchem = null;
		
		searchStringCurrent = string;
		
		if(string == null || string.isEmpty())
		{
			this.visibleList = (ArrayList<SchematicEntry>)CloneCraft.INSTANCE.schematicList.getSchematics().clone();
			return;
		}
		
		string = string.toLowerCase();
		
		ArrayList<SchematicEntry> schems = CloneCraft.INSTANCE.schematicList.getSchematics();
		
		for(int a = 0; a < schems.size(); a++)
		{
			if(schems.get(a).schem.name.toLowerCase().contains(string))
			{
				visibleList.add(schems.get(a));
			}
		}
		
	}
	
	@Override
	public boolean isEntrySelected(int entryIndex)
	{
		SchematicEntry schem = visibleList.get(entryIndex);
		return gui.clone.getBuildAI().getSchematic() == schem.schem;
	}

	@Override
	public int getEntryCount()
	{
		return visibleList.size();
	}

	@Override
	public int getEntryHeight() {
		return entryHeight;
	}

	public void setSelected(int i)
	{
		SchematicEntry schem = visibleList.get(i);
		gui.clone.getBuildAI().setSchematic(schem.schem);
		deleteSchem = null;
		
		if(this.gui.clone.worldObj.isRemote)
		{
			EntityClone.renderFocusedClone = this.gui.clone;
		}
	}
	
	Schematic deleteSchem = null;
	
	@Override
	public void entryClicked(int entryIndex, int mX, int mY)
	{
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		int width = this.width - this.scrollBarWidth;
		
		if(mX >= width-13 && mX < width-2 && mY >= 2 && mY < 13)
		{
			SchematicEntry schemEntry = this.visibleList.get(entryIndex);
			
			if(deleteSchem == schemEntry.schem)
			{
				deleteSchem.delete();
				
				this.visibleList.get(entryIndex).fileLoc.delete();
				
				CloneCraft.INSTANCE.schematicList.reloadSchematics();
				
				this.setSearchString(searchStringCurrent);
				
				
				gui.clone.getBuildAI().setSchematic(null);
				
				deleteSchem = null;
			}
			else
			{
				deleteSchem = schemEntry.schem;
				
//				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			}
		}
		else if(isEntrySelected(entryIndex))
		{
			if(deleteSchem == null)
			{
				gui.clone.getBuildAI().setSchematic(null);
			}
			else
			{
				deleteSchem = null;
			}
		}
		else
		{
			if(deleteSchem == null)
			{
				if(gui.clone.getBuildAI().getSchematic() == null)
				{
					gui.clone.getBuildAI().posX = (int)Math.floor(this.gui.clone.posX) + EntityAIBuild.xzMultipliers[gui.clone.getBuildAI().getRotate()][0];
					gui.clone.getBuildAI().posY = (int)Math.floor(this.gui.clone.posY);
					gui.clone.getBuildAI().posZ = (int)Math.floor(this.gui.clone.posZ) + EntityAIBuild.xzMultipliers[gui.clone.getBuildAI().getRotate()][1];;
				}
				setSelected(entryIndex);
			}
			else
			{
				deleteSchem = null;
			}
			
		}
	}
	
	
	public Schematic getSelectedSchematic()
	{
		return gui.clone.getBuildAI().getSchematic();
	}

	@Override
	public void renderEntry(int entryIndex, int width, int height, int mX, int mY)
	{
		SchematicEntry schem = visibleList.get(entryIndex);
		if(schem.schem == deleteSchem)
		{
			this.drawGradientRect(0, 0, width, height, 0xff982200, 0xff902000);
			
			this.drawString(Minecraft.getMinecraft().fontRenderer, "Are you sure you wish to delete", 3, 5, 0xffffff);

			this.drawString(Minecraft.getMinecraft().fontRenderer, schem.schem.name, 3, 15, 0xffffff);
			this.drawString(Minecraft.getMinecraft().fontRenderer, "from your computer?", 3, 25, 0xffffff);
			this.drawString(Minecraft.getMinecraft().fontRenderer, "Press the X button again to delete.", 3, 40, 0xffffff);
		}
		else
		{
			
			if(isEntrySelected(entryIndex))
			{
				this.drawRect(0, 0, width, height, 0xffffffff);
				this.drawRect(2, 2, width-2, height-2, 0xff1177aa);
			}
			else
			{
				this.drawGradientRect(0, 0, width, height, 0xff005588, 0xff005080);
			}

			this.drawString(Minecraft.getMinecraft().fontRenderer, schem.schem.name, 3, 3, 0xffffffff);
			this.drawString(Minecraft.getMinecraft().fontRenderer, "Width (X): " + schem.schem.xSize, 3, 20, 0xffffffff);
			this.drawString(Minecraft.getMinecraft().fontRenderer, "Height(Y): " + schem.schem.ySize, 3, 30, 0xffffffff);
			this.drawString(Minecraft.getMinecraft().fontRenderer, "Length(Z): " + schem.schem.zSize, 3, 40, 0xffffffff);
			if(gui.displayMode == 2/* || (gui.displayMode == 1 && entryIndex == selected)*/)
			{
				GuiRenderSchematic.renderCenteredSchematicAt(schem.schem, width-75, 5, 80-10, entryHeight-10, gui.xRotate, gui.yRotate, 0, 1);
				GL11.glDisable(GL11.GL_CULL_FACE);
			}
		}
		
		
		Minecraft.getMinecraft().renderEngine.bindTexture(iconsResource);
		
		if(mX >= width-13 && mX < width-2 && mY >= 2 && mY < 13)
		{
			this.drawTexturedModalRect(width-13, 2, 11, 0, 11, 11);
		}
		else
		{
			this.drawTexturedModalRect(width-13, 2, 0, 0, 11, 11);
		}
	}
	
	static ResourceLocation iconsResource = new ResourceLocation("clonecraft:textures/gui/icons.png");

	
	

}
