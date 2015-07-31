package net.jamezo97.clonecraft.build;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.csv.CSVTools;
import net.jamezo97.csv.Table;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class RotationMapping {
	
	public static HashMap<Block, Mapping> mappings = new HashMap<Block, Mapping>();
	
	public static final File mappingFile;
	
	static
	{
		mappingFile = new File(CloneCraft.INSTANCE.getDataDir(), "Mapping.csv");
	}
	
	private static void loadDefault()
	{
		
	}
	
	public static void init()
	{
		load();
	}
	
	public static int translate(Block block, int meta, int rotate)
	{
		if(rotate != 0 && mappings.containsKey(block))
		{
			return mappings.get(block).map(meta, rotate);
		}
		return meta;
	}
	
	public static boolean areSimilarButRotated(Block block, int meta1, int meta2)
	{
		if(meta1 == meta2)
		{
			return true;
		}
		if(mappings.containsKey(block))
		{
			return mappings.get(block).areSimilarButRotated(meta1, meta2);
		}
		return false;
	}
	
	public static void save()
	{
		Table toSave = new Table();
		
		for(Entry<Block, Mapping> entry : mappings.entrySet())
		{
			String theBlockName = Block.blockRegistry.getNameForObject(entry.getKey());
			
			
			if(theBlockName == null)
			{
				System.err.println("Loaded block mapping could not be saved. That's strange.. Skipping");
			}
			else
			{
				Mapping mapping = entry.getValue();
				
				for(int a = 0; a < mapping.quads.size(); a++)
				{
					int[] quad = mapping.quads.get(a);
					
					if(quad.length == 4)
					{
						toSave.addRow(new Object[]{theBlockName, quad[0], quad[1], quad[2], quad[3]});
					}
				}
			}
		}
		
		CSVTools.writeTableToFile(toSave, mappingFile);
	}
	
	public static void load()
	{
		mappings.clear();
		
		Table table = null;
		
		if(mappingFile.exists())
		{
			table = CSVTools.getCSV(mappingFile, true);
			
		}
		
		boolean wasDefault = false;
		
		if(table == null)
		{
			table = new Table();
			
			for(int a = 0; a < defaults1_7_10.length; a++)
			{
				table.addRow(defaults1_7_10[a]);
			}
			
			wasDefault = true;
		}

		for(int y = 0; y < table.getHeight(); y++)
		{
			if(table.getRow(y).length == 5)
			{
				String blockName = table.getString(0, y);

				int id1 = table.getInt(1, y);
				int id2 = table.getInt(2, y);
				int id3 = table.getInt(3, y);
				int id4 = table.getInt(4, y);
				
				
				
				Block block = Block.getBlockFromName(blockName);
				
				
				if(block != null && block != Blocks.air)
				{
					if(!mappings.containsKey(block))
					{
						mappings.put(block, new Mapping(block));
					}
					
					mappings.get(block).addQuad(id1, id2, id3, id4);
				}
			}
		}
		
		if(wasDefault)
		{
			//Don't save.
//			save();
		}
	
		System.out.println("Loaded " + mappings.size() + " block rotation mappings");

	}
	



	/**
	 * 
	 * @param block The block to map
	 * @param metas The four metas to map to.
	 * @return true if mapped successfully, false if it clashes. or failed. Probably clashed.
	 */
	public static boolean addMapping(Block block, int[] metas) 
	{
		if(block != null && block != Blocks.air)
		{
			if(!mappings.containsKey(block))
			{
				mappings.put(block, new Mapping(block));
			}
			
			
			return mappings.get(block).addQuad(metas);
		}
		
		return false;
	}
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Excel Thing
	//="{" & $I$1 & A1 & $I$1 & ", " & B1 & ", " & C1 & ", " & D1 & ", " & E1 & "},"
	
	
	public static final Object[][] defaults1_7_10 = new Object[][]{
		{"minecraft:unlit_redstone_torch", 1, 3, 2, 4},
		{"minecraft:lever", 3, 2, 4, 1},
		{"minecraft:lever", 5, 6, 5, 6},
		{"minecraft:lever", 12, 9, 11, 10},
		{"minecraft:lever", 13, 14, 13, 14},
		{"minecraft:nether_brick_stairs", 5, 7, 4, 6},
		{"minecraft:nether_brick_stairs", 3, 0, 2, 1},
		{"minecraft:torch", 3, 2, 4, 1},
		{"minecraft:golden_rail", 4, 2, 5, 3},
		{"minecraft:golden_rail", 1, 0, 1, 0},
		{"minecraft:ladder", 3, 4, 2, 5},
		{"minecraft:hopper", 0, 0, 0, 0},
		{"minecraft:hopper", 2, 5, 3, 4},
		{"minecraft:hopper", 12, 10, 13, 11},
		{"minecraft:oak_stairs", 5, 7, 4, 6},
		{"minecraft:oak_stairs", 3, 0, 2, 1},
		{"minecraft:pumpkin", 0, 1, 2, 3},
		{"minecraft:tripwire_hook", 3, 0, 1, 2},
		{"minecraft:standing_sign", 0, 4, 8, 12},
		{"minecraft:standing_sign", 15, 3, 7, 11},
		{"minecraft:standing_sign", 14, 2, 6, 10},
		{"minecraft:wooden_door", 0, 1, 2, 3},
		{"minecraft:wooden_door", 8, 8, 8, 8},
		{"minecraft:wooden_door", 5, 6, 7, 4},
		{"minecraft:acacia_stairs", 2, 1, 3, 0},
		{"minecraft:acacia_stairs", 6, 5, 7, 4},
		{"minecraft:jungle_stairs", 6, 5, 7, 4},
		{"minecraft:jungle_stairs", 3, 0, 2, 1},
		{"minecraft:quartz_block", 4, 3, 4, 3},
		{"minecraft:chest", 3, 4, 2, 5},
		{"clonecraft:ccCentrifuge", 2, 3, 0, 1},
		{"minecraft:detector_rail", 1, 0, 1, 0},
		{"minecraft:skull", 1, 1, 1, 1},
		{"minecraft:stone_brick_stairs", 3, 0, 2, 1},
		{"minecraft:stone_brick_stairs", 7, 4, 6, 5},
		{"minecraft:activator_rail", 1, 0, 1, 0},
		{"minecraft:log2", 8, 4, 8, 4},
		{"minecraft:log2", 9, 5, 9, 5},
		{"minecraft:birch_stairs", 2, 1, 3, 0},
		{"minecraft:birch_stairs", 6, 5, 7, 4},
		{"minecraft:stone_button", 4, 1, 3, 2},
		{"minecraft:wall_sign", 3, 4, 2, 5},
		{"minecraft:trapped_chest", 5, 3, 4, 2},
		{"minecraft:stone_stairs", 2, 1, 3, 0},
		{"minecraft:stone_stairs", 6, 5, 7, 4},
		{"minecraft:anvil", 0, 1, 2, 3},
		{"minecraft:anvil", 8, 9, 10, 11},
		{"minecraft:anvil", 7, 4, 5, 6},
		{"minecraft:sticky_piston", 3, 4, 2, 5},
		{"minecraft:lit_pumpkin", 3, 0, 1, 2},
		{"minecraft:dropper", 5, 3, 4, 2},
		{"minecraft:rail", 1, 0, 1, 0},
		{"minecraft:rail", 9, 6, 7, 8},
		{"minecraft:rail", 4, 2, 5, 3},
		{"minecraft:powered_repeater", 3, 0, 1, 2},
		{"minecraft:powered_repeater", 7, 4, 5, 6},
		{"minecraft:powered_repeater", 15, 12, 13, 14},
		{"minecraft:powered_repeater", 11, 8, 9, 10},
		{"minecraft:unpowered_comparator", 2, 3, 0, 1},
		{"minecraft:unpowered_comparator", 6, 7, 4, 5},
		{"minecraft:unpowered_comparator", 12, 13, 14, 15},
		{"minecraft:unpowered_comparator", 8, 9, 10, 11},
		{"minecraft:bed", 10, 11, 8, 9},
		{"minecraft:bed", 2, 3, 0, 1},
		{"minecraft:dispenser", 2, 5, 3, 4},
		{"minecraft:redstone_torch", 1, 3, 2, 4},
		{"minecraft:vine", 1, 2, 4, 8},
		{"minecraft:fence_gate", 2, 3, 0, 1},
		{"minecraft:fence_gate", 6, 7, 4, 5},
		{"minecraft:trapdoor", 5, 6, 4, 7},
		{"minecraft:trapdoor", 15, 13, 14, 12},
		{"minecraft:trapdoor", 1, 2, 0, 3},
		{"minecraft:trapdoor", 9, 10, 8, 11},
		{"minecraft:iron_door", 3, 0, 1, 2},
		{"minecraft:iron_door", 8, 8, 8, 8},
		{"minecraft:iron_door", 5, 6, 7, 4},
		{"minecraft:furnace", 3, 4, 2, 5},
		{"minecraft:spruce_stairs", 2, 1, 3, 0},
		{"minecraft:spruce_stairs", 6, 5, 7, 4},
		{"minecraft:unpowered_repeater", 4, 5, 6, 7},
		{"minecraft:unpowered_repeater", 12, 13, 14, 15},
		{"minecraft:unpowered_repeater", 2, 3, 0, 1},
		{"minecraft:unpowered_repeater", 10, 11, 8, 9},
		{"minecraft:wooden_button", 4, 1, 3, 2},
		{"minecraft:cocoa", 4, 5, 6, 7},
		{"minecraft:cocoa", 8, 9, 10, 11},
		{"minecraft:cocoa", 0, 1, 2, 3},
		{"minecraft:ender_chest", 3, 4, 2, 5},
		{"minecraft:piston", 3, 4, 2, 5},
		{"minecraft:brick_stairs", 3, 0, 2, 1},
		{"minecraft:brick_stairs", 4, 6, 5, 7},
		{"minecraft:quartz_stairs", 5, 7, 4, 6},
		{"minecraft:quartz_stairs", 3, 0, 2, 1},
		{"minecraft:dark_oak_stairs", 7, 4, 6, 5},
		{"minecraft:dark_oak_stairs", 3, 0, 2, 1},
		{"minecraft:sandstone_stairs", 3, 0, 2, 1},
		{"minecraft:sandstone_stairs", 7, 4, 6, 5},
		{"minecraft:log", 9, 5, 9, 5},
		{"minecraft:log", 10, 6, 10, 6},
		{"minecraft:log", 11, 7, 11, 7},
		{"minecraft:log", 8, 4, 8, 4}



	};


}
