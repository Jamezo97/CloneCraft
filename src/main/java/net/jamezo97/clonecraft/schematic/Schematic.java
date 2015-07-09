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
import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Schematic {
	
	public String name;
	
	public int xSize, ySize, zSize;
	
	private int layerSize;
	
	public short[] blockIds;
	public short[] blockMetas;
	
	HashMap<Integer, NBTTagCompound> posToTileEntity = new HashMap<Integer, NBTTagCompound>();
	
	public ArrayList<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();
	
	
	boolean isHashDirty = true;
	long lastHash;
	
	public long myHashCode()
	{
		if(isHashDirty)
		{
			lastHash = (((long)Arrays.hashCode(blockIds)) << 32) | ((long)Arrays.hashCode(blockMetas));
			
			for(Entry<Integer, NBTTagCompound> entry : posToTileEntity.entrySet())
			{
				lastHash += entry.getKey().hashCode() + entry.getValue().hashCode();
			}
			
			isHashDirty = false;
		}
		return lastHash;
	}
	
	public Schematic(String name, int xSize, int ySize, int zSize)
	{
		this(name, xSize, ySize, zSize, new short[xSize*ySize*zSize], new short[xSize*ySize*zSize]);
	}
	
	public Schematic(String name, int xSize, int ySize, int zSize, short[] blockIds, short[] blockMetas, NBTTagCompound[] tes)
	{
		this(name, xSize, ySize, zSize, blockIds, blockMetas);
		if(tes != null)
		{
			for(int a = 0; a < tes.length; a++)
			{
				NBTTagCompound nbtTag = tes[a];
				int posX = nbtTag.getInteger("x");
				int posY = nbtTag.getInteger("y");
				int posZ = nbtTag.getInteger("z");
				
				int index = this.posToIndex(posX, posY, posZ);
				
				if(index >= 0 && index < this.blockIds.length)
				{
					if(posToTileEntity.containsKey(index))
					{
						System.err.println("Schematic " + name + " contains duplicate TileEntities at position (" + posX + ", " + posY + ", " + posZ + "). Skipping");
					}
					else
					{
						this.posToTileEntity.put(index, nbtTag);
						this.tileEntities.add(nbtTag);
					}
				}
				else
				{
					System.err.println("Schematic " + name + " contains TileEntity outside of block list range. Skipping");	
				}
			}
		}
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
		
		Block block;
		int meta;
		
		for(int y = startY; y < maxY; y++)
		{
			for(int z = startZ; z < maxZ; z++)
			{
				for(int x = startX; x < maxX; x++)
				{
					if(this.blockIdAt(pos) != 0 && y > -1 && y < 256)
					{
						world.setBlock(x, y, z, block = this.blockAt(pos), meta = this.blockMetaAt(pos), flag);
						if(block.hasTileEntity(meta))
						{
							NBTTagCompound teData = this.posToTileEntity.get(pos);
							
							if(teData != null)
							{
								TileEntity te = world.getTileEntity(x, y, z);
								
								if(te != null)
								{
									te.readFromNBT(teData);
									te.xCoord = x;
									te.yCoord = y;
									te.zCoord = z;
								}
							}
						}
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
				((nbt.hasKey("Blocks") && nbt.hasKey("Data")) || nbt.hasKey("BlocksData")))
		{
			int Width = nbt.getShort("Width");
			int Height = nbt.getShort("Height");
			int Length = nbt.getShort("Length");
			
			byte[] Blocks_byte = nbt.getByteArray("Blocks");
			byte[] Data_byte = nbt.getByteArray("Data");
			
			NBTTagCompound[] tes = null;
			
			NBTTagList tileEntityList = nbt.getTagList("TileEntities", NBT.TAG_COMPOUND);
			
			if(tileEntityList != null)
			{
				tes = new NBTTagCompound[tileEntityList.tagCount()];
				for(int a = 0; a < tes.length; a++)
				{
					tes[a] = tileEntityList.getCompoundTagAt(a);
				}
			}
			
			short[] BlockID = null;
			short[] DataID = null;
			
			if(Blocks_byte.length == Data_byte.length && Blocks_byte.length == Width*Height*Length)
			{
				BlockID = new short[Blocks_byte.length];
				DataID = new short[Data_byte.length];
				
				for(int a = 0; a < BlockID.length; a++)
				{
					BlockID[a] = (short)(Blocks_byte[a] & 0xFF);
					DataID[a] = (short)(Data_byte[a] & 0xFF);
				}
			}
			else
			{
				int[] BlocksData = nbt.getIntArray("BlocksData");
				
				if(BlocksData.length == Width*Length*Height)
				{
					BlockID = new short[BlocksData.length];
					DataID = new short[BlocksData.length];
				
					for(int a = 0; a < BlocksData.length; a++)
					{
						BlockID[a] = (short) (BlocksData[a] >>    16);
						DataID[a] =   (short) (BlocksData[a] & 0xFFFF);
					}
				}
				else
				{
					System.out.println("Block data length is unequal to block count! " + BlocksData.length + ", " + (Width * Length * Height));
				}
			}
			
			if(BlockID != null && nbt.hasKey("nameToId"))
			{
				NBTTagList list = nbt.getTagList("nameToId", NBT.TAG_STRING);
				
				if(list.tagCount() > 0)
				{
					HashMap<Short, Short> intToIntMapping = new HashMap<Short, Short>();
					
					for(int a = 0; a < list.tagCount(); a++)
					{
						String s = list.getStringTagAt(a);
					
						if(s.length() > 1)
						{
							short oldId = (short) s.charAt(0);
							
							String blockUID = s.substring(1);
							
							Block block = Block.getBlockFromName(blockUID);
							
							if(block != null)
							{
								short newId = (short)Block.getIdFromBlock(block);
								
								
								
								if(newId != oldId)
								{
									System.out.println("Found non matching block ids for block " + blockUID + ". Old ID: " + oldId + ", New ID: " + newId);
									intToIntMapping.put(oldId, newId);
								}
							}
						}
					}
					
					int reAssigned = 0;
					
					for(int a = 0; a < BlockID.length; a++)
					{
						if(intToIntMapping.containsKey(BlockID[a]))
						{
							BlockID[a] = intToIntMapping.get(BlockID[a]);
							reAssigned++;
						}
					}
					if(reAssigned != 0)
					{
						System.out.println("Reassigned " + reAssigned + " block IDs");
					}
					
				}
			}
			
			if(BlockID != null)
			{
				return new Schematic("Schematic_" + Width + "_" + Height + "_" + Length, Width, Height, Length, BlockID, DataID, tes);
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
		
		if(true)
		{
			int[] BlocksData = new int[this.blockIds.length];
			
			for(int a = 0; a < this.blockIds.length; a++)
			{
				BlocksData[a] = (((int)this.blockIds[a]) << 16) | ((int)this.blockMetas[a]);
			}
			
			nbt.setIntArray("BlocksData", BlocksData);
		}
		/*else
		{
			byte[] Block_byte = new byte[this.blockIds.length];
			byte[] Data_byte = new byte[this.blockMetas.length];
			
			for(int a = 0; a < this.blockIds.length; a++)
			{
				Block_byte[a] = (byte)this.blockIds[a];
				Data_byte[a] = (byte)this.blockMetas[a];
			}
			
			nbt.setByteArray("Blocks", Block_byte);
			nbt.setByteArray("Data", Data_byte);
		}*/
		
		nbt.setTag("Entities", new NBTTagList());
		
		NBTTagList tes = new NBTTagList();
		
		for(int a = 0; a < this.tileEntities.size(); a++)
		{
			tes.appendTag(this.tileEntities.get(a));
		}
		
		nbt.setTag("TileEntities", tes);
		
		
		ArrayList<Short> shortList = new ArrayList<Short>();
		
		for(int a = 0; a < this.blockIds.length; a++)
		{
			if(!shortList.contains(this.blockIds[a]))
			{
				shortList.add(this.blockIds[a]);
			}
		}
		
		NBTTagList nameIdList = new NBTTagList();
		
		int saved = 0;
		
		for(int a = 0; a < shortList.size(); a++)
		{
			String theString = "";
			
			theString += (char) ( (short)shortList.get(a) );
			
			Block block = Block.getBlockById(shortList.get(a));
			
			if(block != null)
			{
				String name = Block.blockRegistry.getNameForObject(block);
				
				if(name != null && name.length() > 0)
				{
					theString += name;
					
					saved++;
					
					nameIdList.appendTag(new NBTTagString(theString));
				}
			}
		}
		
		if(saved > 0)
		{
			System.out.println("Saved " + saved + " block id mappings.");
		}
		
		nbt.setTag("nameToId", nameIdList);
		
		
		
		
		
		
		
		
		
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
	
	/**
	 * Creates a Schematic from what's currently in the world. min and max values are inclusive. i.e. from x = 2 to x = 3, includes 2 and 3, thus width = 2 (not 3-2 = 1)
	 * @param minX 
	 * @param minY
	 * @param minZ
	 * @param maxX
	 * @param maxY
	 * @param maxZ
	 * @param world The world from which to grab the block and tile entity data.
	 * @return
	 */
	public static Schematic createSchematic(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, World world)
	{
		int width = maxX - minX + 1;
		int height = maxY - minY + 1;
		int length = maxZ - minZ + 1;
		
		if(width <= 0 || height <= 0 || length <= 0)
		{
			System.err.println("Invalid schematic dimensions: " + width + ", " + height + ", " + length);
			return null;
		}
		
		short[] BlocksValues = new short[width*height*length];
		short[] MetasValues  = new short[width*height*length];
		
		ArrayList<NBTTagCompound> tileEntities = new ArrayList<NBTTagCompound>();
		
		
		int index = 0;
		
		for(int y = minY; y <= maxY; y++)
		{
			for(int z = minZ; z <= maxZ; z++)
			{
				for(int x = minX; x <= maxX; x++)
				{
					Block block = world.getBlock(x, y, z);
					
					if(block != Blocks.air)
					{
						BlocksValues[index] = (short)Block.getIdFromBlock(block);
						
						int meta;
						
						MetasValues[index] = (short)(meta=world.getBlockMetadata(x, y, z));
						
						
						
						if(block.hasTileEntity(meta))
						{
							TileEntity te = world.getTileEntity(x, y, z);
							
							
							
							if(te != null)
							{
								
								NBTTagCompound teNbt = new NBTTagCompound();
								te.writeToNBT(teNbt);
								
								teNbt.setInteger("x", x-minX);
								teNbt.setInteger("y", y-minY);
								teNbt.setInteger("z", z-minZ);
								
//								System.out.println("A");
								
								tileEntities.add(teNbt);
							}
						}
					}
					
					index++;
				}
			}
		}
		
		return new Schematic(String.format("Generated Schem %d %d %d", width, height, length), 
				width, height, length, BlocksValues, MetasValues, tileEntities.toArray(new NBTTagCompound[tileEntities.size()]));
	}

	public boolean hasTileEntityAt(int index)
	{
		return this.posToTileEntity.containsKey(index);
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
	
	
	
	
	
	
	
	
	
	
	
	@SideOnly(value = Side.CLIENT)
	public TileEntity getTileEntity(int x, int y, int z, World world)
	{
		int index = this.posToIndex(x, y, z);
		return this.getTileEntity(index, world);
	}
	
	@SideOnly(value = Side.CLIENT)
	public TileEntity getTileEntity(int index, World world)
	{
//		System.out.println("GIMME");
		try
		{
			Block block = this.blockAt(index);
			
			int meta = this.blockMetaAt(index);
			
			if(block != null && block.hasTileEntity(meta))
			{
				TileEntity te = block.createTileEntity(world, meta);
				
				if(te != null)
				{
					NBTTagCompound teNbt = this.posToTileEntity.get(index);
					
					if(teNbt != null)
					{
						te.readFromNBT(teNbt);
					}

					int[] pos = this.indexToPos(index);
					te.xCoord = pos[0];
					te.yCoord = pos[1];
					te.zCoord = pos[2];
					
					te.setWorldObj(world);
					
//					System.out.println("TILE ENTITY:: " + te);
					return te;
				}
			}
		}
		catch(Throwable t)
		{
			System.err.println("Error occured whilst creating TileEntity for render:");
			t.printStackTrace();
		}
		return null;
	}
	
//	@SideOnly(value = Side.CLIENT)
	World schematicBlockAccess = null;
	
	ByteBuffer byteBuffer;
	
	IntBuffer intBuffer;
	FloatBuffer floatBuffer;
	ShortBuffer shortBuffer;
	
	ArrayList<TileEntity> tileEntityRenders = new ArrayList<TileEntity>();
	
	int vertexCount = -1;
	
	/**
	 * If the schematic is just air, then there are no vertices, and attempting to retrieve the Tessellator state will result in a crash
	 * because the priority queue has a hissy fit or something. Dammit priority queue.
	 */
	boolean doNotRenderNoVertices = false;
	
	private boolean storeOnGPU = true;
	
	/**
	 * If we were originally storing the buffer on the GPU, and now we don't want to. Remove it from the buffer.
	 * <br>It will be added back later if this state is toggled back to true. 
	 * @see Schematic#ensureRenderBuffer()
	*/
	@SideOnly(value = Side.CLIENT)
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
	@SideOnly(value = Side.CLIENT)
	private boolean ensureRenderBuffer()
	{
		if(doNotRenderNoVertices)
		{
			return true;
		}
		
		
		
		try
		{
			if(vertexCount == -1)
			{
				if(schematicBlockAccess == null)
				{
					schematicBlockAccess = new SchematicBlockAccess();
				}
				
				
				
				
				int index = 0;
				
				//Let's hijack the Tessellator class and get it to do the rendering for us
																				//Texture, Brightness, Normals, Colour
				Tessellator.instance.setVertexState(new TesselatorVertexState(new int[0], 0, 0, true, true, true, true));
				
				IBlockAccess backup = RenderBlocks.getInstance().blockAccess;
				RenderBlocks.getInstance().blockAccess = schematicBlockAccess;
				((SchematicBlockAccess)schematicBlockAccess).loadData(this, 0, 0, 0);
				
				ArrayList<Integer> nonOpaque = new ArrayList<Integer>();
				
				boolean atLeastOneNonAir = false;
				
				Block block;
				int blockId;
				int meta;
				
				
				for(int y = 0; y < ySize; y++)
				{
					for(int z = 0; z < zSize; z++)
					{
						for(int x = 0; x < xSize; x++, index++)
						{
							blockId = this.blockIdAt(index);
							
							if(blockId > 0)
							{
								block = Block.getBlockById(blockId);
								meta = this.blockMetaAt(index);
								
								try
								{
									RenderBlocks.getInstance().renderBlockByRenderType(block, x, y, z);
									
									atLeastOneNonAir = true;
									
									if(block.hasTileEntity(meta))
									{
										TileEntity te = this.getTileEntity(index, schematicBlockAccess);
										
										if(te != null)
										{
											te.xCoord = x;
											te.yCoord = y;
											te.zCoord = z;
											tileEntityRenders.add(te);
										}
									}
								}
								catch(Throwable t)
								{
									System.err.println("Error occure whilst rendering block (id: " + blockId + ", block: " + block + ", " + meta + ":");
									t.printStackTrace();
								}
							}
						}
					}
				}
				
				
				
				//Well that was easy.
				if(atLeastOneNonAir)
				{
					TesselatorVertexState state = Tessellator.instance.getVertexState(0.0f, 0.0f, 0.0f);
					
					vertexCount = state.getVertexCount();
					
					RenderBlocks.getInstance().blockAccess = backup;
					
					int[] rawBuffer = state.getRawBuffer();
					
					byteBuffer = GLAllocation.createDirectByteBuffer(vertexCount * 32);
					intBuffer = byteBuffer.asIntBuffer();
					intBuffer.put(rawBuffer, 0, state.getVertexCount()*8);
					
					floatBuffer = byteBuffer.asFloatBuffer();
					shortBuffer = byteBuffer.asShortBuffer();
				}
				else
				{
					doNotRenderNoVertices = true;
				}
				
				Tessellator.instance.setVertexState(new TesselatorVertexState(new int[0], 0, 0, true, false, false, false));
			}
			
			if(!doNotRenderNoVertices && storeOnGPU && bufferHandler == -1)
			{
				bufferHandler = GL15.glGenBuffers();
				
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bufferHandler);
				GL15.glBufferData(GL15.GL_ARRAY_BUFFER, byteBuffer, GL15.GL_STATIC_DRAW);
				
				GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			}
		}
		catch(Throwable e)
		{
			System.err.println("A fatal error has occured during schematic render. This schematic will not be rendered. Sorry!");
			e.printStackTrace();
			doNotRenderNoVertices = true;
			
			cleanGPU();
			
			IBlockAccess schematicBlockAccess = null;
			
			byteBuffer = null;
			
			intBuffer = null;
			floatBuffer = null;
			shortBuffer = null;
			
			vertexCount = -1;
		}
		finally
		{
			
		}
		
		
		return true;
	}
	
	@SideOnly(value = Side.CLIENT)
	public void cleanGPU()
	{
		if(bufferHandler != -1)
		{
			GL15.glDeleteBuffers(bufferHandler);
			bufferHandler = -1;
		}
	}
	
	@SideOnly(value = Side.CLIENT)
	public void render()
	{
		if(ensureRenderBuffer())
		{
			if(doNotRenderNoVertices)
			{
				return;
			}
			
			int bindBufferPost = 0;
			
			try
			{
				bindBufferPost = GL11.glGetInteger(GL15.GL_ARRAY_BUFFER_BINDING);
			}
			catch(Throwable t)
			{
				t.printStackTrace();
				bindBufferPost = 0;
			}
			
			Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
			
			GL11.glTranslatef(0.01f, 0.01f, 0.01f);
			
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			
			if(storeOnGPU)
			{
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


			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, bindBufferPost);
//			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
			
			{
//				RenderHelper.enableStandardItemLighting();
				double lastX = TileEntityRendererDispatcher.instance.field_147560_j;
				double lastY = TileEntityRendererDispatcher.instance.field_147561_k;
				double lastZ = TileEntityRendererDispatcher.instance.field_147558_l;
				
				World lastWorld = TileEntityRendererDispatcher.instance.field_147550_f;
				
				TileEntityRendererDispatcher.instance.field_147560_j = 0;
				TileEntityRendererDispatcher.instance.field_147561_k = 0;
				TileEntityRendererDispatcher.instance.field_147558_l = 0;

				TileEntityRendererDispatcher.instance.field_147550_f = this.schematicBlockAccess;
				
				try
				{
					for (int i = 0; i < this.tileEntityRenders.size(); ++i)
		            {
		                TileEntity tile = this.tileEntityRenders.get(i);

		                
		                TileEntityRendererDispatcher.instance.renderTileEntityAt(tile, tile.xCoord, tile.yCoord, tile.zCoord, 0);
		            }
					
				}
				catch(Throwable t)
				{
					t.printStackTrace();
				}
				finally
				{
					TileEntityRendererDispatcher.instance.field_147560_j = lastX;
					TileEntityRendererDispatcher.instance.field_147561_k = lastY;
					TileEntityRendererDispatcher.instance.field_147558_l = lastZ;
					
					TileEntityRendererDispatcher.instance.field_147550_f = lastWorld;
				}
				
			}
			
			
			
			GL11.glTranslatef(-0.01f, -0.01f, -0.01f);
		}
	}



	
	
	
	
}
