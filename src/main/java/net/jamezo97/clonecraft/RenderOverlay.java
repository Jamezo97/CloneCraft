package net.jamezo97.clonecraft;

import net.jamezo97.clonecraft.render.Renderable;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

public class RenderOverlay implements Renderable
{

	RenderManager renderManager;

	double posX = Double.MAX_VALUE;
	double posY = Double.MAX_VALUE;
	double posZ = Double.MAX_VALUE;

	double minX, minY, minZ;
	double maxX, maxY, maxZ;

	public Colour defaultColour;
	/**
	 * 0: -X -Y -Z 1:
	 */
	public Colour[] cornerColours = new Colour[8];

	public RenderOverlay(Colour colour)
	{
		renderManager = RenderManager.instance;
		this.defaultColour = colour;
	}

	public void setBounds(double minX, double minY, double minZ, double maxX, double maxY, double maxZ)
	{
		double temp;
		if (minX > maxX)
		{
			temp = minX;
			minX = maxX;
			maxX = temp;
		}
		if (minY > maxY)
		{
			temp = minY;
			minY = maxY;
			maxY = temp;
		}
		if (minZ > maxZ)
		{
			temp = minZ;
			minZ = maxZ;
			maxZ = temp;
		}

		this.minX = minX;
		this.minY = minY;
		this.minZ = minZ;

		this.maxX = maxX;
		this.maxY = maxY;
		this.maxZ = maxZ;
	}

	float opacity = 0.2f;

	public void setOpacity(float opacity)
	{
		this.opacity = opacity;
	}

	public void renderOverlay(float partial)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		GL11.glLineWidth(2.0f);

		float b = 0.01f;

		double minX = this.minX - b;
		double maxX = this.maxX + b;

		double minY = this.minY - b;
		double maxY = this.maxY + b;

		double minZ = this.minZ - b;
		double maxZ = this.maxZ + b;

		if (posX != Double.MAX_VALUE && posY != Double.MAX_VALUE && posZ != Double.MAX_VALUE)
		{
			GL11.glTranslated(posX, posY, posZ);
		}

		GL11.glRotatef(rotate % 360, 0, 1, 0);

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glColor4f(defaultColour.getRedF(), defaultColour.getGreenF(), defaultColour.getBlueF(), defaultColour.getAlphaF() * opacity);

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, minZ);

		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, minY, minZ);

		GL11.glEnd();

		GL11.glBegin(GL11.GL_QUADS);

		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, minY, minZ);

		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(maxX, maxY, minZ);

		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, minZ);

		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, minY, minZ);

		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(minX, minY, minZ);

		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);

		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);

		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	float rotate = 0.0f;

	@Override
	public void render(float p)
	{
		renderOverlay(p);
	}

	public void rotate(float rotationYaw)
	{
		double midX = (maxX - minX) / 2.0d + minX;
		double midY = (maxY - minY) / 2.0d + minY;
		double midZ = (maxZ - minZ) / 2.0d + minZ;
		minX = minX - midX;
		minY = minY - midY;
		minZ = minZ - midZ;

		maxX = maxX - midX;
		maxY = maxY - midY;
		maxZ = maxZ - midZ;

		this.posX = midX;
		this.posY = midY;
		this.posZ = midZ;

		rotate = rotationYaw;

	}

	@Override
	public void onTick()
	{

	}

	@Override
	public void onRemoved()
	{

	}

}
