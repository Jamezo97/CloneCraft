package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.clone.CloneOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

import org.lwjgl.opengl.GL11;

public class GuiColourButton extends GuiButton
{

	int colourFrom = 0xff888888;
	int colourTo = 0xff999999;
	int colourOutline = 0xffffffff;

	float scaleText = 1.0f;

	public GuiColourButton(int par1, int par2, int par3, int par4, int par5, String s)
	{
		super(par1, par2, par3, par4, par5, s);
	}

	public void prepColours()
	{

	}

	public GuiColourButton setColours(int colourFrom, int colourTo, int colourOutline)
	{
		this.colourFrom = colourFrom;
		this.colourTo = colourTo;
		this.colourOutline = colourOutline;
		return this;
	}

	public boolean isHovering(int mouseX, int mouseY)
	{
		return mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
	}

	@Override
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_)
	{
		if (this.visible)
		{
			FontRenderer fontrenderer = p_146112_1_.fontRenderer;
			this.field_146123_n = p_146112_2_ >= this.xPosition && p_146112_3_ >= this.yPosition && p_146112_2_ < this.xPosition + this.width
					&& p_146112_3_ < this.yPosition + this.height;
			int k = this.getHoverState(this.field_146123_n);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			prepColours();
			if (k == 0)
			{
				this.drawRect(this.xPosition, this.yPosition, this.xPosition + width, this.yPosition + height, colourFrom);
			}
			else if (k == 1)
			{
				this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + width, this.yPosition + height, colourFrom, colourTo);
			}
			else if (k == 2)
			{
				this.drawGradientRect(this.xPosition, this.yPosition, this.xPosition + width, this.yPosition + height, colourTo, colourFrom);
			}

			this.drawRect(this.xPosition, this.yPosition, this.xPosition + width, this.yPosition + 1, colourOutline);
			this.drawRect(this.xPosition, this.yPosition, this.xPosition + 1, this.yPosition + height, colourOutline);
			this.drawRect(this.xPosition + width - 1, this.yPosition, this.xPosition + width, this.yPosition + height, colourOutline);
			this.drawRect(this.xPosition, this.yPosition + height - 1, this.xPosition + width, this.yPosition + height, colourOutline);

			this.mouseDragged(p_146112_1_, p_146112_2_, p_146112_3_);
			int l = 14737632;

			if (packedFGColour != 0)
			{
				l = packedFGColour;
			}
			else if (!this.enabled)
			{
				l = 10526880;
			}
			else if (this.field_146123_n)
			{
				l = 16777120;
			}

			GL11.glScalef(scaleText, scaleText, scaleText);
			this.drawCenteredString(fontrenderer, this.displayString, (int) ((this.xPosition + this.width / 2) / scaleText),
					(int) ((this.yPosition + (this.height - (7 * scaleText)) / 2) / scaleText), l);
			GL11.glScalef(1 / scaleText, 1 / scaleText, 1 / scaleText);
		}
	}

	public GuiColourButton setScale(float scale)
	{
		this.scaleText = scale;
		return this;
	}

}
