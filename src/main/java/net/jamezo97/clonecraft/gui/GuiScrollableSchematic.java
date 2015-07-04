package net.jamezo97.clonecraft.gui;

import java.util.ArrayList;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.jamezo97.clonecraft.schematic.SchematicEntry;
import net.minecraft.client.Minecraft;

public class GuiScrollableSchematic extends GuiScrollable{
	
	ArrayList<SchematicEntry> visibleList = new ArrayList<SchematicEntry>();

	GuiChooseSchematic gui;
	
	int entryHeight = 60;
	
	public GuiScrollableSchematic(GuiChooseSchematic gui, int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		this.gui = gui;
		setSearchString(null);
	}
	
	public void setSearchString(String string)
	{
		visibleList.clear();
		
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
	}
	
	@Override
	public void entryClicked(int entryIndex)
	{
		if(isEntrySelected(entryIndex))
		{
			gui.clone.getBuildAI().setSchematic(null);
		}
		else
		{
			if(gui.clone.getBuildAI().getSchematic() == null)
			{
				gui.clone.getBuildAI().posX = (int)Math.floor(this.gui.clone.posX);
				gui.clone.getBuildAI().posY = (int)Math.floor(this.gui.clone.posY);
				gui.clone.getBuildAI().posZ = (int)Math.floor(this.gui.clone.posZ);
			}
			setSelected(entryIndex);
		}
	}
	
	
	public Schematic getSelectedSchematic()
	{
		return gui.clone.getBuildAI().getSchematic();
	}

	@Override
	public void renderEntry(int entryIndex, int width, int height)
	{
		SchematicEntry schem = visibleList.get(entryIndex);
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
		}
		
	}
	
	


	
	

}
