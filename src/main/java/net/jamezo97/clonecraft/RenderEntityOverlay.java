package net.jamezo97.clonecraft;

import net.minecraft.entity.Entity;

public class RenderEntityOverlay extends RenderOverlay
{

	Entity e;

	public RenderEntityOverlay(Entity e, int colour)
	{
		super(new Colour(colour));
		this.e = e;

		this.setBounds(e.boundingBox.minX - e.posX, e.boundingBox.minY - e.posY, e.boundingBox.minZ - e.posZ, e.boundingBox.maxX - e.posX, e.boundingBox.maxY
				- e.posY, e.boundingBox.maxZ - e.posZ);

		// System.out.println(e.boundingBox.maxZ-e.boundingBox.minZ);

		this.posX = e.posX;
		this.posY = e.posY;
		this.posZ = e.posZ;

	}

}
