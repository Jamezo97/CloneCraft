package net.jamezo97.clonecraft.gui;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;

import javax.imageio.ImageIO;

import net.jamezo97.clonecraft.clone.BreakableBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class BlockColourer
{

	HashMap<Long, Integer> blockToColour = new HashMap<Long, Integer>();

	public int getColourFor(long block)
	{
		if (blockToColour.containsKey(block))
		{
			return blockToColour.get(block);
		}
		else
		{
			int id = BreakableBlocks.getId(block);
			int data = BreakableBlocks.getMeta(block);
			Block b = Block.getBlockById(id);
			ItemStack stack = new ItemStack(b, 0, data);
			int colour = getColour(b, data);
			blockToColour.put(block, colour);
			return colour;
		}
	}

	private int getColour(Block bloc, int meta)
	{// ItemStack stack){
		int colourMult = bloc.getRenderColor(meta);

		float rMult = (float) (colourMult >> 16 & 255) / 255.0F;
		float gMult = (float) (colourMult >> 8 & 255) / 255.0F;
		float bMult = (float) (colourMult & 255) / 255.0F;

		loadBigTex();
		if (theImage != null)
		{

			int i = getSize();// Minecraft.getGLMaximumTextureSize();
			long redBin = 0;
			long greenBin = 0;
			long blueBin = 0;
			int pixelCount = 0;

			int[] colours;// = new int[16*16];

			int red, green, blue, alpha;

			// for(int a = 0; a < 6; a++){
			IIcon icon = bloc.getIcon(1, meta);
			if (icon != null)
			{
				int x = sar(icon.getMinU(), i);
				int y = sar(icon.getMinV(), i / 2);
				int width = sar(icon.getMaxU() - icon.getMinU(), i);
				int height = sar(icon.getMaxV() - icon.getMinV(), i / 2);
				// System.out.println(width + ", " + height);
				// colours = ;
				colours = theImage.getRGB(x, y, width, height, new int[width * height], 0, width);

				BufferedImage output = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				output.setRGB(0, 0, width, height, colours, 0, width);

				// try {
				// ImageIO.write(output, "PNG", new
				// File("C:/Users/James/Desktop/Output/" +
				// bloc.getLocalizedName() + "_" + meta + ".png"));
				// } catch (IOException e) {
				// e.printStackTrace();
				// }

				for (int b = 0; b < colours.length; b++)
				{
					alpha = (colours[b] >> 24) & 0xff;
					red = (colours[b] >> 16) & 0xff;
					green = (colours[b] >> 8) & 0xff;
					blue = (colours[b]) & 0xff;
					if (alpha > 40)
					{
						redBin += red;
						greenBin += green;
						blueBin += blue;
						pixelCount++;
					}

				}
			}
			// }
			if (pixelCount > 0)
			{

				red = (int) Math.round(redBin / ((float) pixelCount) * rMult);
				green = (int) Math.round(greenBin / ((float) pixelCount) * gMult);
				blue = (int) Math.round(blueBin / ((float) pixelCount) * bMult);

				red += 50;
				if (red > 255)
				{
					red = 255;
				}
				green += 50;
				if (green > 255)
				{
					green = 255;
				}
				blue += 50;
				if (blue > 255)
				{
					blue = 255;
				}

				// red = (int)Math.round(redBin/((float)pixelCount)*rMult);
				// green = (int)Math.round(greenBin / pixelCount);
				// blue = (int)Math.round(blueBin / pixelCount);
				int colourFinal = 0xff000000 | (red << 16) | (green << 8) | blue;
				return colourFinal;
			}
		}
		return 0xffddcccc;
	}

	/**
	 * Scale and round
	 * 
	 * @param f
	 * @param imageSize
	 * @return
	 */
	public int sar(float f, int imageSize)
	{
		return (int) Math.round(f * imageSize);
	}

	BufferedImage theImage;

	boolean attemptedLoad = false;

	public void loadBigTex()
	{
		if (attemptedLoad)
		{
			return;
		}
		attemptedLoad = true;

		int size = getSize();
		if (size > 0)
		{
			IntBuffer data = BufferUtils.createIntBuffer(size * size);

			GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, data);

			BufferedImage bigImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

			int[] data2 = transfer(data, size * size);

			bigImage.setRGB(0, 0, size, size, data2, 0, size);
			theImage = bigImage;

			// try {
			// ImageIO.write(theImage, "PNG", new
			// File("C:/Users/James/Desktop/BigOne.png"));
			// } catch (IOException e) {
			// e.printStackTrace();
			// }
		}
		else
		{
			System.out.println("Size is invalid: " + size);
		}

		// ITextureObject i_tex_obj =
		// Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture);
		// if(i_tex_obj != null){
		// int GL_ID = i_tex_obj.getGlTextureId();
		// GL11.glBindTexture(GL11.GL_TEXTURE_2D, GL_ID);
		// int i = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0,
		// GL11.GL_TEXTURE_WIDTH);
		// IntBuffer data = BufferUtils.createIntBuffer(i*i);
		// // GL11.glTexImage2D(GL11.GL_TEXTURE_2D, i1, GL11.GL_RGBA,
		// p_147946_2_ >> i1, p_147946_3_ >> i1, 0, GL12.GL_BGRA,
		// GL12.GL_UNSIGNED_INT_8_8_8_8_REV, (IntBuffer)null);
		// //
		// // GL11.glTexImage2D(target, level, internalformat, width, height,
		// border, format, type, pixels)
		//
		// GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA,
		// GL12.GL_UNSIGNED_INT_8_8_8_8_REV, data);
		//
		// BufferedImage bigImage = new BufferedImage(i, i,
		// BufferedImage.TYPE_INT_ARGB);
		// // data.rewind();
		// bigImage.setRGB(0, 0, i, i, data.array(), 0, i);
		// theImage = bigImage;
		// }else{
		// System.out.println("Tex Object doesn't exist");
		// }
	}

	private int[] transfer(IntBuffer data, int len)
	{
		if (data.capacity() == len)
		{
			int[] d = new int[len];
			data.rewind();
			data.get(d);
			return d;
		}
		return null;
	}

	int size = 0;

	public int getSize()
	{
		if (size == 0)
		{
			ITextureObject i_tex_obj = Minecraft.getMinecraft().getTextureManager().getTexture(TextureMap.locationBlocksTexture);
			if (i_tex_obj != null)
			{
				int GL_ID = i_tex_obj.getGlTextureId();
				GL11.glBindTexture(GL11.GL_TEXTURE_2D, GL_ID);
				int i = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
				return i;
			}
			size = -1;
		}
		return size;
	}

}
