package net.jamezo97.clonecraft.clone.ai.block;

import java.util.ArrayList;
import java.util.Collections;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class DefaultBlockFinder implements BlockFinder{

	ArrayList<UnbreakableEntry> entries = new ArrayList<UnbreakableEntry>();
	
	int searchWidthRadius = 16;
	int searchHeightRadius = 16;
	
	long lastSearch = System.currentTimeMillis();
	
	@Override
	public ChunkCoordinates getNextBlock(EntityAIMine ai) {
		
		if(System.currentTimeMillis() - lastSearch < 500)
		{
			//Don't search more than twice a second, as this is a pretty heavy function.
			return null;
		}
		lastSearch = System.currentTimeMillis();
		
		
		EntityClone clone = ai.clone;
		World world = clone.worldObj;
		
		int minX = (int) Math.round(clone.posX - searchWidthRadius);
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
					
					if(clone.getOptions().breakables.canBreak(x, y, z))
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
			
			if(clone.canSeeBlock(cc, block))
			{
				return cc;
			}
		}
		
		
		return null;
	}

	@Override
	public void onFinished(EntityAIMine entityAI, ChunkCoordinates coordinates, ItemStack stack, Block block, int meta) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean mustBeCloseToBreak() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void saveState(NBTTagCompound nbt) {}

	@Override
	public void loadState(NBTTagCompound nbt) {}

	@Override
	public void cantBreakBlock(ChunkCoordinates cc, Block break_block) {
		
	}

	public void cloneStateChanged(){}
	
	
}
