package net.jamezo97.clonecraft.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

public class GuiCheckBox extends GuiButton
{
	
	boolean state = false;

	static ResourceLocation iconsResource = new ResourceLocation("clonecraft:textures/gui/icons.png");
	

	public GuiCheckBox(int id, int posX, int posY, String text)
	{
		super(id, posX, posY, 10, 10, text);
	}

	@Override
	public void drawButton(Minecraft mc, int mX, int mY)
	{
		mc.renderEngine.bindTexture(iconsResource);
		
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		int index = 0;
		if(state)
		{
			index = 2;
		}
		if(isHovering(mX, mY))
		{
			index++;
		}
		
		
		this.drawTexturedModalRect(xPosition, yPosition, index*width, 11, width, height);
		
		this.drawString(mc.fontRenderer, displayString, xPosition+width+1, yPosition+1, 0xffffffff);
	}
	
	public boolean isHovering(int mX, int mY)
	{
		return this.enabled && this.visible && mX >= this.xPosition && mY >= this.yPosition && mX < this.xPosition + this.width && mY < this.yPosition + this.height;
	}
	
	public boolean getState()
	{
		return state;
	}
	
	public void setState(boolean state)
	{
		this.state = state;
	}
	
	
	public void toggle()
	{
		this.state = !state;
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mX, int mY)
	{
		boolean b = super.mousePressed(mc, mX, mY);
		
		if(b)
		{
			this.toggle();
		}
		
		return b;
	}
	

}
