package net.jamezo97.clonecraft.chunktricks;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.IChunkProvider;

public class FakeSmallWorld extends World
{
	public FakeSmallWorld()
	{
		super(new EmptySaveHandler(), "MpServer", new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.DEFAULT), WorldProvider.getProviderForDimension(0), new Profiler());
	}

	FakeChunkProvider clientChunkProvider;
	
	@Override
	protected IChunkProvider createChunkProvider()
	{
		//1 Bedrock, 39 Stone, 15 Dirt, 1 Grass. Top Block = 55. Top air = 56
		this.clientChunkProvider = new FakeChunkProvider(this);//new ChunkProviderFlat(this, 0, false, "2;7,39x1,15x3,2;1;");
        return this.clientChunkProvider;
	}
	
	ArrayList<Placed> placed = new ArrayList<Placed>();
	
	
	public void resetWorld()
	{
		while(!placed.isEmpty())
		{
			Placed p = placed.remove(0);
			p.reset();
		}
	}
	
	private void remember(int x, int y, int z)
	{
		Placed place = new Placed(x, y, z, this);
		
		if(!this.placed.contains(place))
		{
			this.placed.add(place);
		}
	}
	
	public void setBlockForget(int x, int y, int z, Block block, int meta, int flag)
	{
		super.setBlock(x, y, z, block, meta, flag);
	}
	

	@Override
	public boolean setBlock(int x, int y, int z, Block block, int meta, int flag)
	{
		remember(x, y, z);
		return super.setBlock(x, y, z, block, meta, flag);
	}



	@Override
	public boolean setBlockMetadataWithNotify(int x, int y, int z, int meta, int flag)
	{
		remember(x, y, z);
		return super.setBlockMetadataWithNotify(x, y, z, meta, flag);
	}



	@Override
	protected int func_152379_p()
	{
		return 256;
	}

	@Override
	public Entity getEntityByID(int p_73045_1_)
	{
		return null;
	}

	public class Placed
	{
		int x, y, z;
		
		Block old;
		int oldMeta;
		
		FakeSmallWorld world;
		
		public Placed(int x, int y, int z, FakeSmallWorld world)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			
			this.world = world;
			
			this.old = world.getBlock(x, y, z);
			this.oldMeta = world.getBlockMetadata(x, y, z);
		}
		
		public void reset()
		{
			world.setBlockForget(x, y, z, old, oldMeta, 0);
		}

		public boolean equals(Object o)
		{
			if(o instanceof Placed)
			{
				Placed p = (Placed)o;
				return p.x == x && p.y == y && p.z == z;
			}
			return false;
		}
	}
	
	
}
