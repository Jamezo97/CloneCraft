package net.jamezo97.clonecraft.block;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.util.Constants.NBT;

public class CCBlockList {
	
	/*private static NBTTagList backedUp = new NBTTagList();
	
	public static NBTTagCompound saveBlockList(NBTTagCompound nbt)
	{
		NBTTagList nameIdList = new NBTTagList();
		
		int saved = 0;
		
		for(Object key : Block.blockRegistry.getKeys())
		{
			String name = (String)key;
			
			Block block = (Block)Block.blockRegistry.getObject(name);
			
			int blockId = Block.blockRegistry.getIDForObject(block);

			short part1 = (short) ((blockId >> 16) & 0xFFFF);
			short part2 = (short) ((blockId) & 0xFFFF);
			
			String theString = "";

			theString += (char) part1;
			theString += (char) part2;
			
			if(block != null && name != null && name.length() > 0)
			{
				theString += name;
				
				saved++;
				
				nameIdList.appendTag(new NBTTagString(theString));
			}
		}
		
		if(saved > 0)
		{
			System.out.println("Saved " + saved + " block id mappings.");
		}
		
		nbt.setTag("nameToId", nameIdList);
		
		nbt.setTag("nameToIdBackups", backedUp);
		
		return nbt;
	}
	
	public static HashMap<Integer, Integer> blockIdToId = null;
	
	public static void loadBlockList(NBTTagCompound nbt)
	{
		HashMap<Integer, Integer> intToIntMapping = new HashMap<Integer, Integer>();
		
		if(nbt.hasKey("nameToId"))
		{
			NBTTagList list = nbt.getTagList("nameToId", NBT.TAG_STRING);
			
			if(list.tagCount() > 0)
			{
				for(int a = 0; a < list.tagCount(); a++)
				{
					String s = list.getStringTagAt(a);
				
					if(s != null && s.length() > 2)
					{
						int oldId = (((int)s.charAt(0)) << 16) | ((int)s.charAt(1));
						
						String blockUID = s.substring(2);
						
						Block block = Block.getBlockFromName(blockUID);
						
						if(block != null)
						{
							int newId = Block.getIdFromBlock(block);
							
							if(newId != oldId)
							{
								System.out.println("Found non matching block ids for block " + blockUID + ". Old ID: " + oldId + ", New ID: " + newId);
								intToIntMapping.put(oldId, newId);
							}
						}
					}
				}
			}
		}
		
		if(nbt.hasKey("nameToIdBackups"))
		{
			nbt.getTagList("nameToIdBackups", NBT.TAG_COMPOUND);
			
		}
		
		if(intToIntMapping.size() > 0)
		{
			
		}
		
		blockIdToId = intToIntMapping;
	}*/

}
