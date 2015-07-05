package net.jamezo97.clonecraft.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiRenderSchematic extends GuiButton{

	public Schematic toRender = null;
	
	public boolean doRender = true;
	
	boolean isHovering = false;
	
	public int posX, posY, width, height;
	
	float scale = 1.0f;
	
	float rotateX = 0.0f;
	
	float rotateY = 0.0f;
	
	float rotateZ = 0.0f;
	
	float transX = 0.0f;
	
	float transY = 0.0f;
	
	public GuiRenderSchematic(int posX, int posY, int width, int height)
	{
		super(Integer.MAX_VALUE, posX, posY, width, height, "");
		this.posX = posX;
		this.posY = posY;
		this.width = width;
		this.height = height;
		
		this.rotateY = 30;
		this.rotateX = 15;
	}

	@Override
	public void drawButton(Minecraft mc, int mX, int mY) 
	{
		this.isHovering = mX >= this.xPosition && mY >= this.yPosition && mX < this.xPosition + this.width && mY < this.yPosition + this.height;
		
		if(this.isHovering)
		{
			
			float dWheel = (Mouse.getDWheel())/1000.0f;
			
			if(dWheel > 0)
			{
				scale *= (dWheel + 1);
			}
			else if(dWheel < 0)
			{
				scale /= (-dWheel + 1);
			}
			
			if(scale < 0)
			{
				scale = 0;
			}
			if(scale > 5)
			{
				scale = 5;
			}
		}
		
		if(doRender)
		{
			GL11.glTranslatef(0, 0, 100);
			
			
			GuiRenderSchematic.renderCenteredSchematicAt(toRender, this.posX+transX, this.posY+transY, 
					width, height, rotateX, rotateY, 0, scale);
			
			
			GL11.glTranslatef(0, 0, -100);
		}
	}
	
	protected void keyTyped(char charC, int p_73869_2_)
	{
		if(this.isHovering && (charC == 'r' || charC == 'R'))
		{
			this.rotateX = 0;
			this.rotateY = 0;
			this.rotateZ = 0;
			this.scale = 1.0f;
			this.transX = 0;
			this.transY = 0;
		}
	}
	
	int lastMX = 0, lastMY = 0;
	
	protected void mouseClicked(int mX, int mY, int btn)
	{
		this.lastMX = mX;
		this.lastMY = mY;
	}
	
	@Override
	public boolean mousePressed(Minecraft p_146116_1_, int mX, int mY)
	{
		return false;
	}

	public void mouseClickMove(int mX, int mY, int lastButton, long downTime)
	{
		int dMX = lastMX - mX;
		int dMY = lastMY - mY;
		
		this.lastMX = mX;
		this.lastMY = mY;
		
		if(isHovering && lastButton == 1)
		{
			float rX = dMX / 1.0f;
			float rY = dMY / 1.0f;
			
			this.rotateY -= rX;
			
			this.rotateX -= rY;// * Math.cos(this.rotateY / 180.0 * Math.PI);
			
//			this.rotateZ -= rY * Math.sin(this.rotateY / 180.0 * Math.PI);
			
			
			if(this.rotateX > 90)
			{
				this.rotateX = 90;
			}
			if(this.rotateX < -90)
			{
				this.rotateX = -90;
			}
			
//			double angle = Math.asin(rotateX / 90f) / Math.PI * 180.0f;
			
//			angle -= rY;
			
//			this.rotateX = (float)Math.sin(angle / 180 * Math.PI) * 90f /** (float)Math.sin(this.rotateY)*/;
		}
		else if(isHovering && lastButton == 0)
		{
			this.transX -= dMX;
			this.transY -= dMY;
		}
		
	}
	
	
	public static void renderCenteredSchematicAt(Schematic schem, float x, float y, float maxWidth, float maxHeight, float rotateX, float rotateY, float rotateZ, float scaleFactor)
	{
		if(schem == null || schem.blockIds.length == 0)
		{
			return;
		}
		
		//Calculate scaling
		float scale = 1.0f;
		
		float dx = schem.xSize/2.0f;
		float dy = schem.ySize/2.0f;
		float dz = schem.zSize/2.0f;
		
		float maxRadi = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		
		float minDimension = Math.min(maxWidth, maxHeight);
		
		scale = minDimension / (maxRadi*2);
		
		
		scale *= scaleFactor;
		
		GL11.glPushMatrix();
		
		GL11.glTranslatef(x + maxWidth/2.0f, y + maxHeight/2.0f, maxRadi*scale);
		
		GL11.glScalef(scale, scale, scale);
		
		GL11.glScalef(1, -1, 1);
		
		GL11.glRotatef(rotateZ, 0.0f, 0.0f, 1.0f);
		GL11.glRotatef(rotateX, 1.0f, 0.0f, 0.0f);
		GL11.glRotatef(rotateY, 0.0f, 1.0f, 0.0f);
		
		GL11.glTranslatef(-schem.xSize/2.0f, -schem.ySize/2.0f, -schem.zSize/2.0f);
		
		schem.render();
		
		GL11.glPopMatrix();
	}
}
