package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.jamezo97.clonecraft.schematic.SchematicEntry;
import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

public class GuiScrollableSchematic extends GuiScrollable{

	GuiChooseSchematic gui;
	
	int entryHeight = 60;
	
	public GuiScrollableSchematic(GuiChooseSchematic gui, int xPosition, int yPosition, int width, int height) {
		super(xPosition, yPosition, width, height);
		this.gui = gui;
	}

//	int selected = -1;
	
	@Override
	public boolean isEntrySelected(int entryIndex) {
		SchematicEntry schem = CloneCraft.INSTANCE.schematicList.getSchematics().get(entryIndex);
		return gui.clone.getBuildAI().getSchematic() == schem.schem;
//		return selected == entryIndex;
	}

	@Override
	public int getEntryCount() {
		return CloneCraft.INSTANCE.schematicList.getSchematics().size();
	}

	@Override
	public int getEntryHeight() {
		return entryHeight;
	}

	public void setSelected(int i) {
		SchematicEntry schem = CloneCraft.INSTANCE.schematicList.getSchematics().get(i);
		gui.clone.getBuildAI().setSchematic(schem.schem);
//		System.out.println(gui.clone.getBuildAI().getSchematic());
//		this.selected = i;
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
	
	/*public int getSelectedIndex()
	{
		return selected;
	}*/
	
	public Schematic getSelectedSchematic()
	{
		return gui.clone.getBuildAI().getSchematic();
		/*if(selected != -1)
		{
			return CloneCraft.INSTANCE.schematicList.getSchematics().get(selected).schem;
		}
		return null;*/
	}

	@Override
	public void renderEntry(int entryIndex, int width, int height)
	{
		SchematicEntry schem = CloneCraft.INSTANCE.schematicList.getSchematics().get(entryIndex);
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
			renderCenteredSchematicAt(schem.schem, width-75, 5, 80-10, entryHeight-10, gui.xRotate, gui.yRotate, 180, 1);
		}
		
	}
	
	public static void renderCenteredSchematicAt(Schematic schem, float x, float y, float maxWidth, float maxHeight, float rotateX, float rotateY, float rotateZ, float scaleFactor)
	{
//		schem.storeOnGPU(false);
		
		//Calculate scaling
		float scale = 1.0f;
		
		float dx = schem.xSize/2.0f;
		float dy = schem.ySize/2.0f;
		float dz = schem.zSize/2.0f;
		
		float maxRadi = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		
		float minDimension = Math.min(maxWidth, maxHeight);
		
		scale = minDimension / (maxRadi*2);
		
		
		/*
		float schemWidth = Math.max(schem.xSize, schem.zSize);
		float schemHeight = schem.ySize;
		
//		float maxRadius = Math.sqrt()
		
		scale = Math.min(maxWidth/schemWidth, maxHeight/schemHeight);*/
		
		scale *= scaleFactor;
		
		GL11.glPushMatrix();
		
		/*GL11.glTranslatef(x, y, 0);
		GL11.glTranslatef(maxWidth/2.0f, maxHeight/2.0f, 0);
		GL11.glTranslatef(0, 0, 500);*/
		
		GL11.glTranslatef(x + maxWidth/2.0f, y + maxHeight/2.0f, maxRadi*scale);
		
		GL11.glScalef(scale, scale, scale);
		
		GL11.glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
		GL11.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
		
		GL11.glTranslatef(-schem.xSize/2.0f, -schem.ySize/2.0f, -schem.zSize/2.0f);
		
		schem.render();
		
		GL11.glPopMatrix();
	}


	
	

}
