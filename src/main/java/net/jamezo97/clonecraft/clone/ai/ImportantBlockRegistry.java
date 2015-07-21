package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.util.BlockHandler;
import net.jamezo97.clonecraft.util.IntelligentSearch;
import net.jamezo97.clonecraft.util.RelativeCoord;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;

/**
 * Stores blocks which the clone deems important for future use. This way, when they wish to access a chest, or furnace,
 * they can search this registry for blocks they can currently see, and blocks they have seen in the past.
 * @author James
 *
 */
public class ImportantBlockRegistry implements BlockHandler
{

	EntityClone clone;
	
	HashMap<Long, Chunk> indexToChunk = new HashMap<Long, Chunk>();
	
	IntelligentSearch searcher;
	
	public ImportantBlockRegistry(EntityClone clone)
	{
		this.clone = clone;
		searcher = new IntelligentSearch(clone, 32, 10, this);
	}
	
	/**
	 * Called every 5 seconds. Scans the environment for blocks, remembers the important ones.
	 */
	public void onUpdate()
	{
		//Intelligently searches around the clone as they move or stand still to maximise effectiveness vs computation time per tick
		//world.getBlock can be rather expensive when thousands of blocks are checked. (32^3)
		
		
		if(clone.ticksExisted % 1 == 0)
		{
			searcher.search();
			/*int posX = (int)Math.floor(clone.posX);
			int posZ = (int)Math.floor(clone.posZ);
			
			for(int x = posX - 16; x <= posX + 16; x+=16)
			{
				for(int z = posZ - 16; z <= posZ + 16; z+=16)
				{
					Chunk c = this.getExistingChunkAt(x, z);
					
					if(c != null)
					{
						c.confirmData(clone.worldObj);
					}
				}
			}*/
		}

		
		
	}
	
	@Override
	public void handleBlock(int x, int y, int z)
	{
		
		Block block = clone.worldObj.getBlock(x, y, z);
		
//		clone.worldObj.setBlock(x, y, z, Blocks.glass);
		
/*		System.out.println(x + ", " + y + ", " + z);
		
		if(block != Blocks.dirt && block != Blocks.air && block != Blocks.grass && block != Blocks.bedrock)
		{
			
			System.out.println("Found diff block: " + ", " + x + ", " + y + ", " + z);
		}
		else
		{
			
		}*/
		
		if(isBlockImportant(block))
		{
			if(clone.canSeeBlock(new ChunkCoordinates(x, y, z), block))
			{
				this.rememberBlock(Block.getIdFromBlock(block), x, y, z);
			}
		}
	}
	
	public ArrayList<RelativeCoord> getNearbyBlocks(Block block, int radius)
	{
		if(this.isBlockImportant(block))
		{
			return getNearbyBlocks(Block.getIdFromBlock(block), radius);
		}
		return new ArrayList<RelativeCoord>();
	}
	
	
	private ArrayList<RelativeCoord> getNearbyBlocks(int blockID, int radius)
	{
		
		Block theBlock = Block.getBlockById(blockID);
		
		ArrayList<RelativeCoord> coords = new ArrayList<RelativeCoord>();
		
		if(isBlockImportant(theBlock))
		{
			int cloneXint = (int)Math.floor(clone.posX);
			int cloneZint = (int)Math.floor(clone.posZ);
			
			int radiusSq = radius * radius;
			
			for(int searchZ = cloneZint - radius; searchZ <= cloneZint + radius; searchZ += Chunk.SIZE)
			{
				for(int searchX = cloneXint - radius; searchX <= cloneXint + radius; searchX += Chunk.SIZE)
				{
					Chunk chunk = getExistingChunkAt(searchX, searchZ);
					
					
					
					if(chunk != null && !chunk.isEmpty())
					{
//						chunk.confirmData(clone.worldObj);
						
						for(int a = 0; a < chunk.getBlocks().size(); a++)
						{
							int data = chunk.getBlocks().get(a);
							
							if((data & 0xFFFF) == blockID)
							{
								int x = ((data >> 28) & 0xF) + chunk.x * Chunk.SIZE;
								int y = (data >> 20) & 0xFF;
								int z = ((data >> 16) & 0xF) + chunk.z * Chunk.SIZE;
								
								if(clone.worldObj.getBlock(x, y, z) == theBlock)
								{
									RelativeCoord coord = new RelativeCoord(x, y, z, clone.posX, clone.posY, clone.posZ);
									
									if(coord.distanceSq <= radiusSq)
									{
										coords.add(coord);
									}
								}
								else
								{
									chunk.getBlocks().remove(a--);
								}
							}
						}
					}
				}
			}
		}
		
		
		
		return coords;
	}
			
	
	public NBTTagCompound saveTo(NBTTagCompound nbt)
	{
		NBTTagList chunks = new NBTTagList();
		for(Entry<Long, Chunk> entry : indexToChunk.entrySet())
		{
			Chunk chunk = entry.getValue();
			
			if(!chunk.isEmpty())
			{
				NBTTagCompound saveTo = new NBTTagCompound();
				chunk.saveTo(saveTo);
				chunks.appendTag(saveTo);
			}
		}
		
		nbt.setTag("ChunkData", chunks);
		
		return nbt;
	}
	
	public void clear()
	{
		this.indexToChunk.clear();
	}
	
	public void readFrom(NBTTagCompound nbt)
	{
		NBTTagList chunks = nbt.getTagList("", NBT.TAG_LIST);
		
		for(int a = 0; a < chunks.tagCount(); a++)
		{
			NBTTagCompound loadFrom = chunks.getCompoundTagAt(a);
			
			Chunk chunk = Chunk.createChunkFrom(loadFrom);
			
			if(!chunk.isEmpty())
			{
				long index = (chunk.x) << 32 | (chunk.z);
				
				this.indexToChunk.put(index, chunk);
			}
		}
	}
	
	public void rememberBlock(int blockID, int x, int y, int z)
	{
		Chunk chunk = this.getChunkAt(x, z);
		
		chunk.rememberWorldBlock(blockID, x, y, z);
	}
	
	public Chunk getChunkAt(int x, int z)
	{
		return getChunk(x >> 4, z >> 4);
	}
	
	public boolean chunkExistsAt(int x, int z)
	{
		return chunkExists(x >> 4, z >> 4);
	}
	
	public Chunk getExistingChunkAt(int x, int z)
	{
		return getChunk(x >> 4, z >> 4);
	}
	
	
	public boolean chunkExists(int x, int z)
	{
		long index = ((long)(x + 1073741824) << 32L) | (z + 1073741824);
		
		return indexToChunk.containsKey(index);
	}
	
	public Chunk getExistingChunk(int x, int z)
	{
		long index = ((long)(x + 1073741824) << 32L) | (z + 1073741824);
		
		if(indexToChunk.containsKey(index))
		{
			return indexToChunk.get(index);
		}
		return null;
	}
	
	public Chunk getChunk(int x, int z)
	{
		long index = ((long)(x + 1073741824) << 32L) | (z + 1073741824);
		
//		System.out.println(index);
//		System.out.println(Integer.toBinaryString(x) + ", " + Integer.toBinaryString(z) + ", " + Long.toBinaryString(index));
		
		if(indexToChunk.containsKey(index))
		{
			return indexToChunk.get(index);
		}
		Chunk newChunk = new Chunk(x, z);
		indexToChunk.put(index, newChunk);
		return newChunk;
	}

	
	
	public static class Chunk
	{
		public static final int SIZE = 16;
		
		public final int x, z;
		
		/**
		 * Contains 4 Values. <4Bits - X> <8Bits Y> <4Bits - Z> <16Bits BlockID>
		 */
		ArrayList<Integer> blocks = new ArrayList<Integer>();
		
		public Chunk(int x, int z)
		{
			this.x = x;
			this.z = z;
		}

		public void confirmData(World world)
		{
			for(int a = 0; a < blocks.size(); a++)
			{
				int data = blocks.get(a);
				
				int blockId = data & 0xFFFF;
				
				int x = ((data >> 28) & 0xF) + this.x * Chunk.SIZE;
				int y = (data >> 20) & 0xFF;
				int z = ((data >> 16) & 0xF) + this.z * Chunk.SIZE;
				
				Block atPos = world.getBlock(x, y, z);
				
				if(Block.getIdFromBlock(atPos) != blockId)
				{
					blocks.remove(a--);
				}
			}
			
			
		}

		public static Chunk createChunkFrom(NBTTagCompound nbt)
		{
			if(nbt.hasKey("x") && nbt.hasKey("z"))
			{
				int x = nbt.getInteger("x");
				int z = nbt.getInteger("z");
				
				Chunk chunk = new Chunk(x, z);
			
				chunk.loadFrom(nbt);
				
				return chunk;
			}
			
			return null;
		}
		
		public void saveTo(NBTTagCompound nbt)
		{
			nbt.setInteger("x", x);
			nbt.setInteger("z", z);
			
			int[] array = new int[blocks.size()];
			
			for(int a = 0; a < blocks.size(); a++)
			{
				array[a] = blocks.get(a);
			}
			
			nbt.setIntArray("Blocks", array);
		}
		
		public void loadFrom(NBTTagCompound nbt)
		{
			int[] array = nbt.getIntArray("Blocks");
			
			for(int a = 0; a < array.length; a++)
			{
				blocks.add(array[a]);
			}
		}

		public boolean isEmpty()
		{
			return blocks.isEmpty();
		}

		public ArrayList<Integer> getBlocks()
		{
			return blocks;
		}

		/**
		 * Saves block to storage.
		 * Assumes x and z coordinates have already been transformed for this chunk, and thus should be in the range between 0 and Chunk.SIZE (16)
		 * @param block Block to save
		 * @param x Chunk x coordinate of the block
		 * @param y Chunk y coordinate of the block
		 * @param z Chunk z coordinate of the block
		 */
		public void rememberChunkBlock(int blockID, int x, int y, int z) 
		{
			if(x < 0 || z < 0 || x > 15 || z > 15)
			{
				System.err.println("Chunk coordinates are out of bounds(rememberChunkBlock): " + x + ", " + z);
				return;
			}
			
			int toSave = ((x & 0xF) << 28) | ((y & 0xFF) << 20) | ((z & 0xF) << 16) | (blockID & 0xFFFF);
			
			addBlockData(toSave);
		}
		
		/**
		 * Saves the block to storage
		 * @param block Block to save
		 * @param x World x coordinate of the block
		 * @param y World y coordinate of the block
		 * @param z World z coordinate of the block
		 */
		public void rememberWorldBlock(int blockID, int x, int y, int z) 
		{
			rememberChunkBlock(blockID, x - this.x * SIZE, y, z - this.z * SIZE);
		}
		
		public int forgetChunkBlock(int x, int y, int z)
		{
			if(x < 0 || z < 0 || x > 15 || z > 15)
			{
				System.err.println("Chunk coordinates are out of bounds (forgetChunkBlock): " + x + ", " + z);
				return -1;
			}
			int posData = ((x & 0xF) << 28) | ((y & 0xFF) << 20) | ((z & 0xF) << 16);
			
			for(int a = 0; a < blocks.size(); a++)
			{
				if((blocks.get(a) & posData) == posData)
				{
					return blocks.remove(a--);
				}
			}
			return -1;
		}
		
		public int forgetWorldBlock(int x, int y, int z) 
		{
			return forgetChunkBlock(x - this.x * SIZE, y, z - this.z * SIZE);
		}
		
		/**
		 * Adds the block data, but first removes any duplicate positions
		 * @param theData
		 */
		public void addBlockData(int theData)
		{
			int posData = theData & 0xFFFF0000;
			
			for(int a = 0; a < blocks.size(); a++)
			{
				if((blocks.get(a) & posData) == posData)
				{
					blocks.remove(a--);
				}
			}
			blocks.add(theData);
		}
	}
	
	
	static int[] importantBlocks = null;
	
	
	public static boolean isBlockImportant(Block block)
	{
		return block == Blocks.chest || block == Blocks.crafting_table || block == Blocks.furnace;
/*		
		
		System.out.println(block + ": " + Arrays.binarySearch(importantBlocks, Block.getIdFromBlock(block)));
		return false;*/
	}
	
	
	
	public static void determineImportantBlocks()
	{
		ArrayList<Integer> important = new ArrayList<Integer>();
		
		Block[] importants = new Block[]{
				Blocks.chest,
				Blocks.trapped_chest/*,
				Blocks.log,
				Blocks.log2,
				Blocks.leaves,
				Blocks.redstone_ore,
				Blocks.lapis_ore,
				Blocks.diamond_ore,
				Blocks.gold_ore,
				Blocks.iron_ore,
				Blocks.coal_ore,
				Blocks.cocoa,
				Blocks.anvil,
				Blocks.bed,
				Blocks.brewing_stand,
				Blocks.cake,
				Blocks.carrots,
				Blocks.potatoes,
				Blocks.crafting_table,
				Blocks.emerald_ore,
				Blocks.enchanting_table,
				Blocks.fence_gate,
				Blocks.furnace,
				Blocks.jukebox,
				Blocks.melon_block,*/
		};
		
		importantBlocks = new int[importants.length];
		
		for(int a = 0; a < important.size(); a++)
		{
			importantBlocks[a] = Block.getIdFromBlock(importants[a]);
		}
		
		Arrays.sort(importantBlocks);
	}


}
