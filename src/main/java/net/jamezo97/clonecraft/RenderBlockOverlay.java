package net.jamezo97.clonecraft;

import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

public class RenderBlockOverlay extends RenderOverlay{

	int red, green, blue, alpha;
	
	public RenderBlockOverlay(int x, int y, int z, int colour) {
		super(new Colour(colour));
		setBounds(x, y, z, x+1, y+1, z+1);
	}
	
}
