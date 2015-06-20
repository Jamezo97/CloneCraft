package net.jamezo97.clonecraft.clone.ai.block;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;

public class Unbreakables {
	
	int maxSize = 30;
	
	ArrayList<UnbreakableEntry> entries = new ArrayList<UnbreakableEntry>();
	
	public void clear()
	{
		entries.clear();
	}
	
	public boolean canBreakBlock(Block block, int meta, int x, int y, int z)
	{
		for(int a = 0; a < entries.size(); a++)
		{
			UnbreakableEntry entry = entries.get(a);
			if(entry.coordinates.posX == x && entry.coordinates.posY == y && entry.coordinates.posZ == z)
			{
				if(entry.block == block && entry.meta == meta)
				{
					return false;
				}
				else
				{
					//Otherwise, the block has changed! Let's remove it.
					entries.remove(a);
					a--;
				}
			}
		}
		return true;
	}
	
	public void addUnbreakable(ChunkCoordinates coordinates, Block block, int meta)
	{
		if(entries.size() >= maxSize)
		{
			entries.remove(0);
		}

		entries.add(new UnbreakableEntry(coordinates, block, meta));
	}

	public void pickedUp(EntityClone clone, ItemStack stack)
	{
		for(int a = 0; a < entries.size(); a++)
		{
			UnbreakableEntry entry = entries.get(a);
			if(clone.canItemHarvestBlock(stack, entry.coordinates, entry.block, entry.meta))
			{
				entries.remove(a);
				a--;
			}
		}
	}
	

}
