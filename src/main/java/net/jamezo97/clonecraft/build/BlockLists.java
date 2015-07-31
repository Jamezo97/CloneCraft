package net.jamezo97.clonecraft.build;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFire;

public class BlockLists {

	public static ArrayList<Block> doors = new ArrayList<Block>();
	
	public static ArrayList<Block> fires = new ArrayList<Block>();

	
	public boolean isDoor(Block block)
	{
		return doors.contains(block);
	}
	
	public boolean isFire(Block block)
	{
		return fires.contains(block);
	}
	
	public static void init()
	{
		for(Object key : Block.blockRegistry.getKeys())
		{
			Object blockObj = Block.blockRegistry.getObject(key);
			
			if(blockObj instanceof Block)
			{
				Block block = (Block)blockObj;
				
				if(block instanceof BlockFire)
				{
					fires.add(block);
				}
				else if(block instanceof BlockDoor)
				{
					doors.add(block);
				}
			}
		}
		
		
	}
}
