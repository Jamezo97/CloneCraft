package net.jamezo97.clonecraft;

import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

public class RenderBlockOverlay extends RenderOverlay
{

	// int red, green, blue, alpha;

	public RenderBlockOverlay(int x, int y, int z, int colour)
	{
		super(new Colour(colour));
		setBlockBounds(x, y, z);
	}

	public void setBlockBounds(int x, int y, int z)
	{
		setBounds(x - 0.01, y - 0.01, z - 0.01, x + 1.01, y + 1.01, z + 1.01);
	}

}
