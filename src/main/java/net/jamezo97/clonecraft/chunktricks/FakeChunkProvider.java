package net.jamezo97.clonecraft.chunktricks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.ChunkProviderFlat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FakeChunkProvider implements IChunkProvider
{
    private static final Logger logger = LogManager.getLogger();

    
    /** The mapping between ChunkCoordinates and Chunks that ChunkProviderClient maintains. */
    private LongHashMap chunkMapping = new LongHashMap();
    /**
     * This may have been intended to be an iterable version of all currently loaded chunks (MultiplayerChunkCache),
     * with identical contents to chunkMapping's values. However it is never actually added to.
     */
    private List chunkListing = new ArrayList();
    /** Reference to the World object. */
    private World worldObj;
    private static final String __OBFID = "CL_00000880";
    
    
    private final Block[] cachedBlockIDs = new Block[256];
    private final byte[] cachedBlockMetadata = new byte[256];

    public FakeChunkProvider(World world)
    {
        this.worldObj = world;
        
    	
    	for(int y = 0; y < 256; y++)
    	{
			Block set = Blocks.air;
			
    		if(y == 0)
    		{
    			set = Blocks.bedrock;
    		}
    		else if(y < 40)
    		{
    			set = Blocks.stone;
    		}
    		else if(y < 57)
    		{
    			set = Blocks.dirt;
    		}
    		else if(y < 58)
    		{
    			set = Blocks.grass;
    		}
    		
    		cachedBlockIDs[y] = set;
    		cachedBlockMetadata[y] = 0;
    	}
    }

    /**
     * Checks to see if a chunk exists at x, y
     */
	@Override
    public boolean chunkExists(int p_73149_1_, int p_73149_2_)
    {
        return true;
    }

    /**
     * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
     * to false
     */
    public void unloadChunk(int p_73234_1_, int p_73234_2_)
    {
        Chunk chunk = this.provideChunk(p_73234_1_, p_73234_2_);

        if (!chunk.isEmpty())
        {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(p_73234_1_, p_73234_2_));
        this.chunkListing.remove(chunk);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
	@Override
    public Chunk loadChunk(int p_73158_1_, int p_73158_2_)
    {
        Chunk chunk = new Chunk(this.worldObj, p_73158_1_, p_73158_2_);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_), chunk);
        this.chunkListing.add(chunk);
        chunk.isChunkLoaded = true;
        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
	@Override
    public Chunk provideChunk(int x, int z)
    {
        Chunk chunk = (Chunk)this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        return chunk == null ? generateEmptyChunk(x, z) : chunk;
    }
    
    public Chunk generateEmptyChunk(int x, int z)
    {
    	Chunk chunk = new Chunk(this.worldObj, x, z);
    	
    	int l;

        for (int k = 0; k < this.cachedBlockIDs.length; ++k)
        {
            Block block = this.cachedBlockIDs[k];

            if (block != null)
            {
                l = k >> 4;
                ExtendedBlockStorage extendedblockstorage = chunk.getBlockStorageArray()[l];

                if (extendedblockstorage == null)
                {
                    extendedblockstorage = new ExtendedBlockStorage(k, !this.worldObj.provider.hasNoSky);
                    chunk.getBlockStorageArray()[l] = extendedblockstorage;
                }

                for (int i1 = 0; i1 < 16; ++i1)
                {
                    for (int j1 = 0; j1 < 16; ++j1)
                    {
                        extendedblockstorage.func_150818_a(i1, k & 15, j1, block);
                        extendedblockstorage.setExtBlockMetadata(i1, k & 15, j1, this.cachedBlockMetadata[k]);
                    }
                }
            }
        }
    	
    	
    	this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(x, z), chunk);
        this.chunkListing.add(chunk);
        chunk.isChunkLoaded = true;
    	
    	return chunk;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
	@Override
    public boolean saveChunks(boolean p_73151_1_, IProgressUpdate p_73151_2_)
    {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
	@Override
    public void saveExtraData() {}

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
	@Override
    public boolean unloadQueuedChunks()
    {
        long i = System.currentTimeMillis();
        Iterator iterator = this.chunkListing.iterator();

        while (iterator.hasNext())
        {
            Chunk chunk = (Chunk)iterator.next();
            chunk.func_150804_b(System.currentTimeMillis() - i > 5L);
        }

        if (System.currentTimeMillis() - i > 100L)
        {
            logger.info("Warning: Clientside chunk ticking took {} ms", new Object[] {Long.valueOf(System.currentTimeMillis() - i)});
        }

        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
	@Override
    public boolean canSave()
    {
        return false;
    }

    /**
     * Populates chunk with ores etc etc
     */
	@Override
    public void populate(IChunkProvider p_73153_1_, int p_73153_2_, int p_73153_3_) {}

    /**
     * Converts the instance data to a readable string.
     */
	@Override
    public String makeString()
    {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    /**
     * Returns a list of creatures of the specified type that can spawn at the given location.
     */
	@Override
    public List getPossibleCreatures(EnumCreatureType p_73155_1_, int p_73155_2_, int p_73155_3_, int p_73155_4_)
    {
        return null;
    }

	@Override
    public ChunkPosition func_147416_a(World p_147416_1_, String p_147416_2_, int p_147416_3_, int p_147416_4_, int p_147416_5_)
    {
        return null;
    }

	@Override
    public int getLoadedChunkCount()
    {
        return this.chunkListing.size();
    }

	@Override
    public void recreateStructures(int p_82695_1_, int p_82695_2_) {}
}