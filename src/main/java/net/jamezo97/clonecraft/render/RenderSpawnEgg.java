package net.jamezo97.clonecraft.render;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.entity.EntitySpawnEgg;
import net.jamezo97.clonecraft.item.ItemData;
import net.jamezo97.clonecraft.item.ItemSpawnEgg;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class RenderSpawnEgg extends Render
{

	ItemSpawnEgg item;

	public RenderSpawnEgg()
	{
		item = CloneCraft.INSTANCE.itemSpawnEgg;
	}

	@Override
	public void doRender(Entity entity, double par2, double par4, double par6, float par8, float par9)
	{
		if (entity instanceof EntitySpawnEgg)
		{
			EntitySpawnEgg egg = (EntitySpawnEgg) entity;
			if (egg != null)
			{
				ItemStack stack = egg.getDataWatcher().getWatchableObjectItemStack(3);
				if (stack != null)
				{
					GL11.glPushMatrix();
					GL11.glTranslatef((float) par2, (float) par4, (float) par6);
					GL11.glEnable(GL12.GL_RESCALE_NORMAL);
					GL11.glScalef(0.5F, 0.5F, 0.5F);
					this.bindEntityTexture(entity);
					for (int a = 0; a < item.getRenderPasses(stack.getItemDamage()); a++)
					{
						IIcon ico = item.getIcon(stack, a);
						if (ico != null)
						{
							int colour = item.getColorFromItemStack(stack, a); // Colour(egg.getDataWatcher().getWatchableObjectInt(3));
							float r = ((float) ((colour >> 16) & 0xff)) / 255.0f;
							float g = ((float) ((colour >> 8) & 0xff)) / 255.0f;
							float b = ((float) (colour & 0xff)) / 255.0f;

							Tessellator tessellator = Tessellator.instance;
							GL11.glPushMatrix();
							GL11.glColor3f(r, g, b);
							this.func_77026_a(tessellator, ico);
							GL11.glPopMatrix();
						}
					}
					GL11.glDisable(GL12.GL_RESCALE_NORMAL);
					GL11.glPopMatrix();
				}
			}
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity par1Entity)
	{
		return TextureMap.locationItemsTexture;
	}

	private void func_77026_a(Tessellator par1Tessellator, IIcon par2Icon)
	{
		float f = par2Icon.getMinU();
		float f1 = par2Icon.getMaxU();
		float f2 = par2Icon.getMinV();
		float f3 = par2Icon.getMaxV();
		float f4 = 1.0F;
		float f5 = 0.5F;
		float f6 = 0.25F;
		GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
		par1Tessellator.startDrawingQuads();
		par1Tessellator.setNormal(0.0F, 1.0F, 0.0F);
		par1Tessellator.addVertexWithUV((double) (0.0F - f5), (double) (0.0F - f6), 0.0D, (double) f, (double) f3);
		par1Tessellator.addVertexWithUV((double) (f4 - f5), (double) (0.0F - f6), 0.0D, (double) f1, (double) f3);
		par1Tessellator.addVertexWithUV((double) (f4 - f5), (double) (f4 - f6), 0.0D, (double) f1, (double) f2);
		par1Tessellator.addVertexWithUV((double) (0.0F - f5), (double) (f4 - f6), 0.0D, (double) f, (double) f2);
		par1Tessellator.draw();
	}
}
