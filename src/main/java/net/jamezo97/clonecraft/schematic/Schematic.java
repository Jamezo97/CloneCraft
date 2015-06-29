package net.jamezo97.clonecraft.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

public class Schematic {
	
	public String name;
	
	public int xSize, ySize, zSize;
	
	private int layerSize;
	
	public short[] blockIds;
	public short[] blockMetas;
	
	
	boolean isHashDirty = true;
	long lastHash;
	
	public long myHashCode()
	{
		if(isHashDirty)
		{
			lastHash = (((long)Arrays.hashCode(blockIds)) << 32) | ((long)Arrays.hashCode(blockMetas));
			isHashDirty = false;
		}
		return lastHash;
	}
	
	public Schematic(String name, int xSize, int ySize, int zSize)
	{
		this(name, xSize, ySize, zSize, new short[xSize*ySize*zSize], new short[xSize*ySize*zSize]);
	}
	
	public Schematic(String name, int xSize, int ySize, int zSize, short[] blockIds, short[] blockMetas)
	{
		this.name = name;
		
		this.xSize = xSize;
		this.ySize = ySize;
		this.zSize = zSize;
		this.blockIds = blockIds;
		this.blockMetas = blockMetas;
		
		int length = xSize*ySize*zSize;
		
		if(blockIds.length != length || blockMetas.length != length)
		{
			System.err.println("x*y*z Size Does not match block and meta list array lengths!");
			this.xSize = 0;
			this.ySize = 0;
			this.zSize = 0;
			this.blockIds = new short[0];
			this.blockMetas = new short[0];
		}
		
		this.layerSize = this.xSize * this.zSize;
	}
	
	public void buildInstantly(int startX, int startY, int startZ, World world)
	{
		int maxX = startX + xSize;
		int maxY = startY + ySize;
		int maxZ = startZ + zSize;
		
		int flag = 0;
		
		if(xSize*ySize*zSize < 10000)
		{
			flag = 2;
		}
		
		int pos = 0;
		
		int said = 0;
		
		for(int y = startY; y < maxY; y++)
		{
			for(int z = startZ; z < maxZ; z++)
			{
				for(int x = startX; x < maxX; x++)
				{
					if(this.blockIdAt(pos) != 0 && y > -1 && y < 256)
					{
						world.setBlock(x, y, z, this.blockAt(pos), this.blockMetaAt(pos), flag);
					}
					pos++;
				}
			}
		}
	}
	
	public int posToIndex(int x, int y, int z)
	{
		return y * (xSize*zSize) + z*(xSize) + x;
	}
	
	public int[] indexToPos(int index)
	{
		int y = index / (layerSize);
		index -= y * (layerSize);
		int z = index / xSize;
		int x = index % xSize;
		return new int[]{x, y, z};
	}
	
	public int blockIdAt(int pos)
	{
		return this.blockIds[pos];
	}
	
	public Block blockAt(int pos)
	{
		return Block.getBlockById(this.blockIds[pos]);
	}
	
	public int blockMetaAt(int pos)
	{
		return this.blockMetas[pos];
	}
	
	public static Schematic loadFrom(NBTTagCompound nbt)
	{
		if(nbt.hasKey("Width") && nbt.hasKey("Height") && nbt.hasKey("Length") &&
				nbt.hasKey("Blocks") && nbt.hasKey("Data"))
		{
			int Width = nbt.getShort("Width");
			int Height = nbt.getShort("Height");
			int Length = nbt.getShort("Length");
			
			byte[] Blocks_byte = nbt.getByteArray("Blocks");
			byte[] Data_byte = nbt.getByteArray("Data");
			
			if(Blocks_byte.length == Data_byte.length && Blocks_byte.length == Width*Height*Length)
			{
				short[] Blocks = new short[Blocks_byte.length];
				short[] Data = new short[Data_byte.length];
				
				for(int a = 0; a < Blocks.length; a++)
				{
					Blocks[a] = (short)(Blocks_byte[a] & 0xFF);
					Data[a] = (short)(Data_byte[a] & 0xFF);
				}
				
				return new Schematic("Schematic_" + Width + "_" + Height + "_" + Length, Width, Height, Length, Blocks, Data);
			}
		}
		return null;
	}
	
	
	public static Schematic loadFrom(InputStream stream) throws IOException
	{
		return loadFrom(CompressedStreamTools.readCompressed(stream));
	}
	
	public static Schematic loadFrom(File file)
	{
		InputStream input = null;
		
		try
		{
			input = new FileInputStream(file);
			Schematic schem = loadFrom(input);
			schem.name = file.getName();
			
			if(schem.name.toLowerCase().endsWith(".schematic"))
			{
				schem.name = schem.name.substring(0, schem.name.length()-10);
			}
			
			return schem;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(input != null)
			{
				try
				{
					input.close();
				}
				catch(Exception e){}
			}
		}
		
		return null;
	}
	
	public NBTTagCompound saveTo(NBTTagCompound nbt)
	{
		nbt.setShort("Width", (short)this.xSize);
		nbt.setShort("Height", (short)this.ySize);
		nbt.setShort("Length", (short)this.zSize);
		
		byte[] Block_byte = new byte[this.blockIds.length];
		byte[] Data_byte = new byte[this.blockMetas.length];
		
		for(int a = 0; a < this.blockIds.length; a++)
		{
			Block_byte[a] = (byte)this.blockIds[a];
			Data_byte[a] = (byte)this.blockMetas[a];
		}
		
		nbt.setByteArray("Blocks", Block_byte);
		nbt.setByteArray("Data", Data_byte);
		
		NBTTagList emptyList = new NBTTagList();
		
		nbt.setTag("Entities", emptyList);
		nbt.setTag("TileEntities", emptyList);
		
		return nbt;
	}
	
	public void saveTo(OutputStream out) throws IOException
	{
		NBTTagCompound nbt = this.saveTo(new NBTTagCompound());
		
		CompressedStreamTools.writeCompressed(nbt, out);
	}
	
	public boolean saveTo(File file)
	{
		OutputStream output = null;
		
		try
		{
			output = new FileOutputStream(file);
			
			this.saveTo(output);
			
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(output != null)
			{
				try
				{
					output.close();
				}
				catch(Exception e){}
			}
		}
		
		return false;
	}
	
	public boolean coordExists(int x, int y, int z)
	{
		return !(x < 0 || y < 0 || z < 0 || x >= xSize || y >= ySize || z >= zSize);
	}

	@Override
	public String toString() {
		return name + ": {Width(X): " + xSize + ", Height(Y): " + ySize + ", Length(Z): " + zSize + ", hash: " + myHashCode() + "}";
	}
	
	public void delete()
	{
		this.xSize = 0;
		this.ySize = 0;
		this.zSize = 0;
		this.blockIds = new short[0];
		this.blockMetas = new short[0];
		this.layerSize = 0;
		this.cleanGPU();
	}
	
	
	IBlockAccess schematicBlockAccess = null;
	
	ByteBuffer byteBuffer;
	
	IntBuffer intBuffer;
	FloatBuffer floatBuffer;
	ShortBuffer shortBuffer;
	
	int vertexCount = -1;
	
	private boolean storeOnGPU = true;
	
	/**
	 * If we were originally storing the buffer on the GPU, and now we don't want to. Remove it from the buffer.
	 * <br>It will be added back later if this state is toggled back to true. 
	 * @see Schematic#ensureRenderBuffer()
	*/
	public void storeOnGPU(boolean state)
	{
		if(state == false && storeOnGPU)
		{
			cleanGPU();
		}
		storeOnGPU = state;
	}
	
	int bufferHandler = -1;
	
	/**
	 * Ensures that the render buffer is setup, and ready to be rendered.
	 */
	private boolean ensureRenderBuffer()
	{
		if(vertexCount == -1)
		{
			int index = 0;
			
			//Let's hijack the Tessellator class and get it to do the rendering for us
																			//Texture, Brightness, Normals, Colour
			Tessellator.instance.setVertexState(new TesselatorVertexState(new int[0], 0, 0, true, true, true, true));
			
			IBlockAccess backup = RenderBlocks.getInstance().blockAccess;
			RenderBlocks.getInstance().blockAccess = new SchematicBlockAccess(this, 0, 0, 0, Minecraft.getMinecraft().theWorld);
			
			ArrayList<Integer> nonOpaque = new ArrayList<Integer>();
			
			for(int y = 0; y < ySize; y++)
			{
				for(int z = 0; z < zSize; z++)
				{
					for(int x = 0; x < xSize; x++, index++)
					{
						int blockId = this.blockIdAt(index);
						
						if(blockId > 0)
						{
							RenderBlocks.getInstance().renderBlockByRenderType(Block.getBlockById(blockId), x, y, z);
						}
					}
				}
			}
			
			//Well that was easy.
			
			TesselatorVertexState state = Tessellator.instance.getVertexState(0.0f, 0.0f, 0.0f);
			Tessellator.instance.setVertexState(new TesselatorVertexState(new int[0], 0, 0, true, false, false, false));
			
			vertexCount = state.getVertexCount();
			
			RenderBlocks.getInstance().blockAccess = backup;
			
			int[] rawBuffer = state.getRawBuffer();
			
			byteBuffer = GLAllocation.createDirectByteBuffer(vertexCount * 32);
			intBuffer = byteBuffer.asIntBuffer();
			intBuffer.put(rawBuffer, 0, state.getVertexCount()*8);
			
			floatBuffer = byteBuffer.asFloatBuffer();
			shortBuffer = byteBuffer.asShortBuffer();
		}
		
		if(storeOnGPU && bufferHandler == -1)
		{
			bufferHandler = GL15.glGenBuffers();
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferHandler);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		}
		
		return true;
	}
	

	public void cleanGPU()
	{
		if(bufferHandler != -1)
		{
			System.out.println("Cleaned");
			GL15.glDeleteBuffers(bufferHandler);
			bufferHandler = -1;
		}
	}
	
	public void render()
	{
		if(ensureRenderBuffer())
		{
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			GL11.glTranslatef(0.01f, 0.01f, 0.01f);
			
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			if(storeOnGPU)
			{
//				System.out.println();
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferHandler);
				
				GL11.glTexCoordPointer(2, GL11.GL_FLOAT, 32, /*3*4=*/12L);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				
				OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
				GL11.glTexCoordPointer(2, GL11.GL_SHORT, 32, /*14*2=*/28);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
				
				GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, 32, 20);
				GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
				
				GL11.glNormalPointer(GL11.GL_BYTE, 32, 24);
				GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
				
				GL11.glVertexPointer(3, GL11.GL_FLOAT, 32, 0);
				GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			}
			else
			{
				this.floatBuffer.position(3);
				GL11.glTexCoordPointer(2, 32, this.floatBuffer);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				
				OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
				this.shortBuffer.position(14);
				GL11.glTexCoordPointer(2, 32, this.shortBuffer);
				GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
				OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
				
				this.byteBuffer.position(20);
				GL11.glColorPointer(4, true, 32, this.byteBuffer);
				GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
				
				this.byteBuffer.position(24);
				GL11.glNormalPointer(32, this.byteBuffer);
				GL11.glEnableClientState(GL11.GL_NORMAL_ARRAY);
				
				this.floatBuffer.position(0);
				GL11.glVertexPointer(3, 32, this.floatBuffer);
				GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
			}
			
			
			GL11.glDrawArrays(GL11.GL_QUADS, 0, this.vertexCount);
			
			
			GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
			GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);
			OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);

			GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);

			GL11.glDisableClientState(GL11.GL_NORMAL_ARRAY);

			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			GL11.glTranslatef(-0.01f, -0.01f, -0.01f);
		}
	}

	
	
	
	
}
