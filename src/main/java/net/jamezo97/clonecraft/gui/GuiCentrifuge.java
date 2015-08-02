package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.gui.container.ContainerCentrifuge;
import net.jamezo97.clonecraft.network.Handler0SpinCentrifuge;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiCentrifuge extends GuiContainer
{

	TileEntityCentrifuge te = null;

	ResourceLocation centrifuge = new ResourceLocation("clonecraft:textures/gui/centrifuge_empty.png");

	public GuiCentrifuge(EntityPlayer player, TileEntityCentrifuge teCentrifuge)
	{
		super(new ContainerCentrifuge(player, teCentrifuge));
		this.te = teCentrifuge;
		this.xSize = 176;
		this.ySize = 196;
	}

	GuiButton startStop;

	@Override
	public void initGui()
	{
		super.initGui();

		this.buttonList.add(startStop = new GuiButton(0, guiLeft + xSize - 44, guiTop + 6, 40, 20, "Spin"));
	}

	@Override
	protected void actionPerformed(GuiButton b)
	{
		if (b.id == 0)
		{
			new Handler0SpinCentrifuge().sendToServer();
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2)
	{
		fontRendererObj.drawString("Centrifuge", 8, 6, 0x404040);
		fontRendererObj.drawString("Inventory", 8, (ySize - 106) + 2, 0x404040);
		if (te.isBurning())
		{
			GL11.glPushMatrix();
			mc.getTextureManager().bindTexture(centrifuge);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);

			int left = te.getburnTimeLeftRemainingScaled(14);
			// x y, u v, w h
			drawTexturedModalRect(81, 59 - left, 177, 15 - left, 14, left);

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void updateScreen()
	{
		super.updateScreen();
		if (te.cookTime > 0)
		{
			startStop.displayString = "Stop";
		}
		else
		{
			startStop.displayString = "Spin";
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mX, int mY)
	{
		GL11.glPushMatrix();
		mc.getTextureManager().bindTexture(centrifuge);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

		int l = (width - xSize) / 2;
		int i1 = (height - ySize) / 2;
		drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
		drawTexturedModalRect(8 + l, 102 + i1, 0, 196, (int) Math.round(te.getProgressScaled(160)), 6);
		drawTexturedModalRect(8 + l, 102 + i1, 0, 196, (int) Math.round(te.getProgressScaled(160)), 6);

		for (int a = 0; a < 8; a++)
		{
			int x = 79;
			int y = 43;
			double rotate = (te.interpolate(partialTicks, te.lastSpin, te.spin) / 6F) * Math.PI;
			rotate += 0.7854 * a;
			x += Math.round(Math.cos(rotate) * 36.0);
			y += Math.round(Math.sin(rotate) * 36.0);
			// *36
			Slot slot = (Slot) inventorySlots.inventorySlots.get(a);
			slot.xDisplayPosition = x + 1;
			slot.yDisplayPosition = y + 1;
			drawTexturedModalRect(l + x, i1 + y, 7, 113, 18, 18);
		}

		GL11.glPopMatrix();
	}

}
