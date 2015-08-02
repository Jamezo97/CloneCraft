package net.jamezo97.clonecraft.clone;

import net.jamezo97.clonecraft.render.Renderable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

public class RenderSelection implements Renderable
{

	EntityClone clone;

	RenderManager renderManager;

	int minX, minY, minZ;
	int maxX, maxY, maxZ;

	public RenderSelection()
	{
		minX = minY = minZ = maxX = maxY = maxZ = 0;
	}

	public RenderSelection(EntityClone clone)
	{
		this.clone = clone;
		renderManager = RenderManager.instance;
		setBounds(r(clone.posX + 4), r(clone.posY + 5), r(clone.posZ + 4), r(clone.posX + 6), r(clone.posY + 7), r(clone.posZ + 6));
	}

	public int r(double d)
	{
		return (int) Math.round(d);
	}

	public void setBounds(int minX, int minY, int minZ, int maxX, int maxY, int maxZ)
	{
		int temp;
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

	@Override
	public void onRemoved()
	{

	}

	@Override
	public void render(float p)
	{
		GL11.glDisable(GL11.GL_TEXTURE_2D);

		float done = getDone();// = 0.07f;

		GL11.glColor4f((1 - (done)) * 1.0f, (done) * 1.0f, 0.0f, 1.0f);

		GL11.glLineWidth(2.0f);

		float b = 0.01f;

		float minX = this.minX - b;
		float maxX = this.maxX + b;

		float minY = this.minY - b;
		float maxY = this.maxY + b;

		float minZ = this.minZ - b;
		float maxZ = this.maxZ + b;

		GL11.glBegin(GL11.GL_LINES);
		// GL11.glVertex3f(minX, minY, minZ);
		// GL11.glVertex3f(minX, maxY, minZ);
		GL11.glVertex3f(maxX, minY, minZ);
		GL11.glVertex3f(maxX, maxY, minZ);
		GL11.glVertex3f(maxX, minY, maxZ);
		GL11.glVertex3f(maxX, maxY, maxZ);
		GL11.glVertex3f(minX, minY, maxZ);
		GL11.glVertex3f(minX, maxY, maxZ);
		GL11.glEnd();

		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3f(minX, maxY, minZ);
		GL11.glVertex3f(maxX, maxY, minZ);
		GL11.glVertex3f(maxX, maxY, maxZ);
		GL11.glVertex3f(minX, maxY, maxZ);
		GL11.glVertex3f(minX, maxY, minZ);

		GL11.glVertex3f(minX, minY, minZ);
		GL11.glVertex3f(maxX, minY, minZ);
		GL11.glVertex3f(maxX, minY, maxZ);
		GL11.glVertex3f(minX, minY, maxZ);
		GL11.glVertex3f(minX, minY, minZ);
		GL11.glEnd();

		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// GL11.glDisable(GL11.GL_CULL_FACE);

		GL11.glColor4f((1 - (done)) * 1.0f, (done) * 1.0f, 0.0f, 0.2f);

		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3f(maxX, minY, minZ);
		GL11.glVertex3f(maxX, minY, maxZ);
		GL11.glVertex3f(minX, minY, maxZ);
		GL11.glVertex3f(minX, minY, minZ);

		GL11.glVertex3f(minX, maxY, minZ);
		GL11.glVertex3f(minX, maxY, maxZ);
		GL11.glVertex3f(maxX, maxY, maxZ);
		GL11.glVertex3f(maxX, maxY, minZ);

		GL11.glVertex3f(minX, minY, minZ);
		GL11.glVertex3f(minX, minY, maxZ);
		GL11.glVertex3f(minX, maxY, maxZ);
		GL11.glVertex3f(minX, maxY, minZ);

		GL11.glVertex3f(maxX, maxY, minZ);
		GL11.glVertex3f(maxX, maxY, maxZ);
		GL11.glVertex3f(maxX, minY, maxZ);
		GL11.glVertex3f(maxX, minY, minZ);

		GL11.glVertex3f(minX, maxY, minZ);
		GL11.glVertex3f(maxX, maxY, minZ);
		GL11.glVertex3f(maxX, minY, minZ);
		GL11.glVertex3f(minX, minY, minZ);

		GL11.glVertex3f(minX, minY, maxZ);
		GL11.glVertex3f(maxX, minY, maxZ);
		GL11.glVertex3f(maxX, maxY, maxZ);
		GL11.glVertex3f(minX, maxY, maxZ);
		GL11.glEnd();

		GL11.glDisable(GL11.GL_BLEND);

		GL11.glEnable(GL11.GL_TEXTURE_2D);

		// GL11.glDisable(GL11.GL_CULL_FACE);
		renderStatus(clone, "Percent Done", done, this.minX + (this.maxX - this.minX) / 2, (this.maxY) + 0.9, this.minZ + (this.maxZ - this.minZ) / 2, p,
				(this.maxX - this.minX) > (this.maxZ - this.minZ) ? (this.maxZ - this.minZ) : (this.maxX - this.minX));
		// GL11.glEnable(GL11.GL_CULL_FACE);
	}

	public void renderStatus(EntityClone clone, String s, float done, double x, double y, double z, float partial, int width)
	{
		float f = 1.0f / 256.0f;
		double d3 = clone.getDistanceSqToEntity(this.renderManager.livingPlayer);
		double maxDistanceSqr = 400;

		if (d3 <= (double) (maxDistanceSqr))
		{
			FontRenderer fontrenderer = Minecraft.getMinecraft().fontRenderer;
			float f1 = 0.06F;
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.0F, (float) y, (float) z);
			GL11.glNormal3f(0.0F, 1.0F, 0.0F);
			GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
			GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
			GL11.glScalef(-f1, -f1, f1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDepthMask(false);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			OpenGlHelper.glBlendFunc(770, 771, 1, 0);
			Tessellator tessellator = Tessellator.instance;

			String percent = "" + Math.round(done * 1000.0) / 10.0f + "%";
			int sWidth = fontrenderer.getStringWidth(percent);
			int j = width * 5;// 40;
			if (sWidth / 2 > j)
			{
				j = sWidth / 2 + 1;
			}

			j = 50;
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			{

				tessellator.startDrawingQuads();
				// fontrenderer.getStringWidth(s) / 2;
				tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0F, 0.1F);
				tessellator.addVertex((double) (-j), (double) (-1), 0.0D);
				tessellator.addVertex((double) (-j), (double) (8), 0.0D);
				tessellator.addVertex((double) (j), (double) (8), 0.0D);
				tessellator.addVertex((double) (j), (double) (-1), 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				// fontrenderer.getStringWidth(s) / 2;
				tessellator.setColorRGBA_F(1 - done, done, 0.0F, 0.2F);
				tessellator.addVertex((double) (-j), (double) (-1), 0.0D);
				tessellator.addVertex((double) (-j), (double) (8), 0.0D);
				tessellator.addVertex((double) (j - (1 - done) * (j * 2)), (double) (8), 0.0D);
				tessellator.addVertex((double) (j - (1 - done) * (j * 2)), (double) (-1), 0.0D);
				tessellator.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				fontrenderer.drawString(percent, -sWidth / 2, 0, 0x44ffffff);
				fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, -10, 0x44ffffdd);
			}
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				tessellator.startDrawingQuads();
				// fontrenderer.getStringWidth(s) / 2;
				tessellator.setColorRGBA_F(1.0f, 1.0f, 1.0F, 0.25F);
				tessellator.addVertex((double) (-j), (double) (-1), 0.0D);
				tessellator.addVertex((double) (-j), (double) (8), 0.0D);
				tessellator.addVertex((double) (j), (double) (8), 0.0D);
				tessellator.addVertex((double) (j), (double) (-1), 0.0D);
				tessellator.draw();

				tessellator.startDrawingQuads();
				// fontrenderer.getStringWidth(s) / 2;
				tessellator.setColorRGBA_F(1 - done, done, 0.0F, 0.75F);
				tessellator.addVertex((double) (-j), (double) (-1), 0.0D);
				tessellator.addVertex((double) (-j), (double) (8), 0.0D);
				tessellator.addVertex((double) (j - (1 - done) * (j * 2)), (double) (8), 0.0D);
				tessellator.addVertex((double) (j - (1 - done) * (j * 2)), (double) (-1), 0.0D);
				tessellator.draw();
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				fontrenderer.drawString(percent, -sWidth / 2, 0, 0xffffffff);
				fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, -10, 0xffffffdd);
			}

			fontrenderer.drawString(percent, -sWidth / 2, 0, 0xffffffff);
			fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, -10, 0xffffffdd);
			// fontrenderer.drawString(percent,
			// -fontrenderer.getStringWidth(percent) / 2, 0, -1);
			// fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2,
			// -10, -1);

			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDepthMask(true);

			// GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glPopMatrix();
		}
	}

	float done = 0.0f;

	public void tick()
	{
		done += 0.01f;
		if (done > 1)
		{
			done -= 1;
		}
	}

	public float getDone()
	{
		return (float) (Math.sin(done * 2 * Math.PI) + 1.0f) / 2.0f;
	}

	@Override
	public void onTick()
	{

	}

}
