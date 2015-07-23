package net.jamezo97.clonecraft.clone.ai.block;

import java.util.ArrayList;
import java.util.Collections;

import net.jamezo97.clonecraft.build.CustomBuilder;
import net.jamezo97.clonecraft.build.CustomBuilders;
import net.jamezo97.clonecraft.build.PlantCustomBuilder;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCocoa;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class DefaultBlockFinder implements BlockFinder{

	Unbreakables unbreakables = new Unbreakables();
	
	int searchWidthRadius = 16;
	int searchHeightRadius = 16;
	
	long lastSearch = System.currentTimeMillis();
	
	@Override
	public ChunkCoordinates getNextBlock(EntityAIMine ai) {
		
		if(System.currentTimeMillis() - lastSearch < 200)
		{
			//Don't search more than twice a second, as this is a pretty heavy function.
			return null;
		}
		lastSearch = System.currentTimeMillis();
		
		
		EntityClone clone = ai.clone;
		World world = clone.worldObj;
		
		int beginX = (int)Math.floor(clone.posX);
		int beginY = (int)Math.floor(clone.posY);
		int beginZ = (int)Math.floor(clone.posZ);
		
		int x, y, z;
		
		for(int a = 0; a < searchX.length; a++)
		{
			y = searchY[a] + beginY;
			if(y < 0 || y > 255)
			{
				continue;
			}
			

			x = searchX[a] + beginX;
			z = searchZ[a] + beginZ;
				
			if(clone.getOptions().breakables.canBreak(x, y, z) && 
					unbreakables.canBreakBlock(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), x, y, z))
			{

				Block block = world.getBlock(x, y, z);
				ChunkCoordinates cc = new ChunkCoordinates(x, y, z);
				if(clone.canSeeBlock(cc, block))
				{
					return new ChunkCoordinates(x, y, z);
				}
				
			}
		}
		
		
		/*int minX = (int) Math.round(clone.posX - searchWidthRadius);
		int minY = (int) Math.round(clone.posY - searchHeightRadius)+1;//About the center of the clone
		int minZ = (int) Math.round(clone.posZ - searchWidthRadius);
		
		ArrayList<BlockCoord> coords = new ArrayList<BlockCoord>();
		
		for(int z = minZ; z < minZ + (searchWidthRadius*2); z++)
		{
			for(int y = minY; y < minY + (searchHeightRadius*2); y++)
			{
				for(int x = minX; x < minX + (searchWidthRadius*2); x++)
				{
					if(y < 0 || y > 255)
					{
						continue;
					}
					
					if(clone.getOptions().breakables.canBreak(x, y, z) && 
							unbreakables.canBreakBlock(world.getBlock(x, y, z), world.getBlockMetadata(x, y, z), x, y, z))
					{
						coords.add(new BlockCoord(x, y, z, clone));
					}
				}
			}
		}
		
		Collections.sort(coords);
		//Should be sorted now, by closest first
		
		for(int a = 0; a < coords.size(); a++)
		{
			BlockCoord entry = coords.get(a);
			
			Block block = world.getBlock(entry.posX, entry.posY, entry.posZ);
			
			ChunkCoordinates cc = new ChunkCoordinates(entry.posX, entry.posY, entry.posZ);
			
			if(true || clone.canSeeBlock(cc, block))
			{
				return cc;
			}
		}*/
		
		
		return null;
	}

	@Override
	public void onFinished(EntityAIMine entityAI, ChunkCoordinates coordinates, ItemStack stack, Block block, int meta)
	{
		CustomBuilder builder = CustomBuilders.customBuilderMap.get(block);
		
		if(builder instanceof PlantCustomBuilder)
		{
			ItemStack need = ((PlantCustomBuilder)builder).getRequiredItem();
			
			if(need != null)
			{
				if(entityAI.clone.inventory.consume(need))
				{
					if(block instanceof BlockCocoa)
					{
						meta = meta & 3;
					}
					else
					{
						meta = 0;
					}
					
					entityAI.clone.worldObj.setBlock(coordinates.posX, coordinates.posY, coordinates.posZ, block, meta, 3);
				
					entityAI.clone.swingItem();
				}
			}
			
		}
	}

	@Override
	public boolean mustBeCloseToBreak() {
		return true;
	}

	@Override
	public void saveState(NBTTagCompound nbt) {}

	@Override
	public void loadState(NBTTagCompound nbt) {}

	@Override
	public void cantBreakBlock(ChunkCoordinates cc, Block theBlock, int theMeta) {
		unbreakables.addUnbreakable(cc, theBlock, theMeta);
	}

	@Override
	public void clonePickedUp(EntityClone clone, ItemStack stack)
	{
		unbreakables.pickedUp(clone, stack);
	}

	
	
	
	static int[] searchX;
	static int[] searchY;
	static int[] searchZ;
	
	static int searchRadius = 16;
	
	static
	{
		prebuildSearchList();
	}
	
	static void prebuildSearchList()
	{
		ArrayList<SearchPos> positions = new ArrayList<SearchPos>();
		
		for(int z = -searchRadius; z <= searchRadius; z++)
		{
			for(int y = -searchRadius; y <= searchRadius; y++)
			{
				for(int x = -searchRadius; x <= searchRadius; x++)
				{
					positions.add(new SearchPos(x, y, z));
				}
			}
		}
		
		Collections.sort(positions);
		
		searchX = new int[positions.size()];
		searchY = new int[positions.size()];
		searchZ = new int[positions.size()];
		
		for(int a = 0; a < positions.size(); a++)
		{
			searchX[a] = positions.get(a).x;
			searchY[a] = positions.get(a).y;
			searchZ[a] = positions.get(a).z;
		}
	}
	
	static class SearchPos implements Comparable<SearchPos>{
		
		int x, y, z;
		double d;
		
		public SearchPos(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.d = x*x + y*y + z*z;
		}

		@Override
		public int compareTo(SearchPos arg0) {
			if(this.d > arg0.d)
			{
				return 1;
			}
			else if(this.d < arg0.d)
			{
				return -1;
			}
			return 0;
		}
		
		
		
	}
	
	
	
}
