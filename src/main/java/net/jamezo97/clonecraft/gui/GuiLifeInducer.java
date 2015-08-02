package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.gui.container.ContainerLifeInducer;
import net.jamezo97.clonecraft.network.Handler3LifeInducerUpdates;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiLifeInducer extends GuiContainer
{

	TileEntityLifeInducer tileEntity;

	public GuiLifeInducer(InventoryPlayer playerInven, TileEntityLifeInducer tileEntity)
	{
		super(new ContainerLifeInducer(playerInven, tileEntity));
		this.tileEntity = tileEntity;
		xSize = 176;
		ySize = 190;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{

	}

	float alpha = 1.0f;

	double rad = 0;

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		rad += ((double) tileEntity.power) / 1600.0d * Math.PI * 2.0d;
		if (rad > Math.PI * 2)
		{
			rad = rad % (Math.PI * 2.0d);
		}
		alpha = (float) ((Math.cos(rad) + 1.0) / 2.0);
	}

	boolean check = false;

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		GL11.glPushMatrix();
		mc.getTextureManager().bindTexture(guiImage);
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		int minX = 8 + xStart;
		int minY = 7 + yStart;
		int maxX = 168 + xStart;
		int maxY = 23 + yStart;
		drawTexturedModalRect(xStart, yStart, 0, 0, xSize, ySize);
		drawTexturedModalRect(xStart + 8, yStart + 7, 0, 200, tileEntity.power, 16);
		if (tileEntity.power > 0)
		{
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0f, 1.0f, 1.0f, alpha);
			drawTexturedModalRect(xStart + 15, yStart + 24, 0, 190, 146, 10);

			GL11.glDisable(GL11.GL_BLEND);
		}

		// 88 15
		if (i >= minX && i <= maxX && j >= minY && j <= maxY)
		{
			if (check)
			{
				this.drawCenteredString(mc.fontRenderer, "Are you sure??", xStart + 88, yStart + 12, 0xffff2222);
			}
			else
			{
				this.drawCenteredString(mc.fontRenderer, "Discharge Safely?", xStart + 88, yStart + 12, 0xffffffff);
			}
		}

		GL11.glPopMatrix();
	}

	@Override
	protected void mouseClicked(int x, int y, int button)
	{
		super.mouseClicked(x, y, button);
		int xStart = (width - xSize) / 2;
		int yStart = (height - ySize) / 2;
		int minX = 8 + xStart;
		int minY = 7 + yStart;
		int maxX = 168 + xStart;
		int maxY = 23 + yStart;
		if (x >= minX && x <= maxX && y >= minY && y <= maxY)
		{
			if (!check)
			{
				check = true;
			}
			else
			{
				new Handler3LifeInducerUpdates(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 3, 0).sendToServer();
				check = false;
				// Discharge
			}
			mc.getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		}
	}

	public static final ResourceLocation guiImage = new ResourceLocation("CloneCraft:textures/gui/transmogrifier.png");

}
