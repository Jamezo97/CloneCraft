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

	int selected = -1;
	
	@Override
	public boolean isEntrySelected(int entryIndex) {
		return selected == entryIndex;
	}

	@Override
	public int getEntryCount() {
		return CloneCraft.INSTANCE.schematicList.getSchematics().size();
	}

	@Override
	public int getEntryHeight() {
		return entryHeight;
	}

	@Override
	public void entryClicked(int entryIndex)
	{
		if(selected == entryIndex)
		{
			selected = -1;
		}
		else
		{
			selected = entryIndex;
		}
	}
	
	public int getSelectedIndex()
	{
		return selected;
	}
	
	public Schematic getSelectedSchematic()
	{
		if(selected != -1)
		{
			return CloneCraft.INSTANCE.schematicList.getSchematics().get(selected).schem;
		}
		return null;
	}

	@Override
	public void renderEntry(int entryIndex, int width, int height)
	{
		SchematicEntry schem = CloneCraft.INSTANCE.schematicList.getSchematics().get(entryIndex);
		if(selected == entryIndex)
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
		if(gui.displayMode == 2 || (gui.displayMode == 1 && entryIndex == selected))
		{
			renderCenteredSchematicAt(schem.schem, width-75, 5, 80-10, entryHeight-10, gui.xRotate, gui.yRotate, 180);
		}
		
	}
	
	public static void renderCenteredSchematicAt(Schematic schem, float x, float y, float maxWidth, float maxHeight, float rotateX, float rotateY, float rotateZ)
	{
		schem.storeOnGPU = false;
		
		//Calculate scaling
		float scale = 1.0f;
		
		float schemWidth = Math.max(schem.xSize, schem.zSize);
		float schemHeight = schem.ySize;
		
		scale = Math.min(maxWidth/schemWidth, maxHeight/schemHeight);
		
		int theWidth = (int)(scale * schemWidth);
		int theHeight = (int)(scale * schemHeight);
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x + (maxWidth-theWidth)/2, y + (maxHeight-theHeight)/2, 0);

		GL11.glScalef(scale, scale, scale);
		
		GL11.glTranslatef(schem.xSize/2, schem.ySize/2, schem.zSize/2);
		
		GL11.glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
		GL11.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
		
		GL11.glTranslatef(-schem.xSize/2, -schem.ySize/2, -schem.zSize/2);
		
		schem.render();
		
		GL11.glPopMatrix();
	}
	
	

}
