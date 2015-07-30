package net.jamezo97.clonecraft.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiProgressBar extends GuiButton
{
	
	public boolean autoGenText = true;
	
	float displayValue = 0;

	public GuiProgressBar(int index, int posX, int posY, int width, int height, String text)
	{
		super(index, posX, posY, width, height, text);
		setText(text);
		setValue(0);
	}
	
	public void setText(String text)
	{
		if(text == null)
		{
			this.displayString = "";
		}
		else
		{
			this.displayString = text;
		}
	}
	
	/**
	 * Sets the progress value, between 0.0 and 1.0
	 * @param percent The percent complete, between 0.0 and 1.0 (this is checked anyway)
	 */
	public void setValue(float percent)
	{
		if(percent < 0)
		{
			percent = 0;
		}
		else if(percent > 1)
		{
			percent = 1;
		}
		
		this.displayValue = percent;
		
		if(this.autoGenText)
		{
			this.setText(Math.round(this.displayValue * 1000)/10.0 + "%");
		}
	}

	@Override
	public void drawButton(Minecraft mc, int mX, int mY)
	{
		int border = 2;
		
		this.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, 0xFF333333);
		
		int w = (int)Math.round((float)(width-border*2) * this.displayValue);
		
		this.drawRect(xPosition + border, yPosition + border, xPosition + w + border, yPosition + height-border, 0xff33cc22);
		
		this.drawCenteredString(mc.fontRenderer, this.displayString, xPosition+width/2, yPosition + height/2 - 4, 0xffffffff);
	}
	
	


	
	
}
