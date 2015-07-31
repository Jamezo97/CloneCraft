package net.jamezo97.clonecraft.build;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import net.jamezo97.clonecraft.chunktricks.FakePlayer;
import net.jamezo97.clonecraft.chunktricks.FakeSmallWorld;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSign;
import net.minecraft.item.ItemStack;

/**
 * Maps the blocks to their items.
 * @author James
 *
 */
public class BlockItemRegistry {
	
	public static HashMap<Block, Item> blockToItem = new HashMap<Block, Item>();
	
	//In theory should be faster.
	public static HashMap<Integer, Item> idToItem = new HashMap<Integer, Item>();
	
	
	static Random rand = new Random();
	
	public static void registerBlockItem(Block block, Item item)
	{
		blockToItem.put(block, item);
		idToItem.put(Block.getIdFromBlock(block), item);
	}
	
	/**
	 * Adds all the custom bindings.
	 */
	static void initCustom()
	{
		/*registerBlockItem(Blocks.standing_sign, Items.sign);
		registerBlockItem(Blocks.wall_sign, Items.sign);
		registerBlockItem(Blocks.unpowered_comparator, Items.comparator);
		registerBlockItem(Blocks.powered_comparator, Items.comparator);
		registerBlockItem(Blocks.cauldron, Items.cauldron);
		registerBlockItem(Blocks.tripwire, Items.string);
		registerBlockItem(Blocks.flower_pot, Items.flower_pot);

		*/
//		registerBlockItem(Blocks.redstone_wire, Items.redstone);
//		
//		registerBlockItem(Blocks.cake, Items.cake);
	}
	
	
	
	/**
	 * Checks every item and tests to see which items place blocks in the world. Adds blocks and items to the hash map.
	 */
	public static void init2()
	{
		try
		{
			
		
			float[][] sideOffsets = new float[][]{
					{0.5f, 0.0f, 0.5f},//0  X Y Z
					{0.5f, 1.0f, 0.5f},//1  X Y Z
					{0.5f, 0.5f, 0.0f},//2  X Y Z
					{0.5f, 0.5f, 1.0f},//3  X Y Z
					{0.0f, 0.5f, 0.5f},//4  X Y Z
					{1.0f, 0.5f, 0.5f} //5  X Y Z
				};
			
			int[][] offsets = new int[][]{
					{0, -1, 0},	//0
					{0, 1, 0},	//1
					{0, 0, -1},	//2
					{0, 0, 1},	//3
					{-1, 0, 0},	//4
					{1, 0, 0}	//5
			};
			
			FakeSmallWorld fakeWorld = new FakeSmallWorld();
			
			fakeWorld.isRemote = false;
		
			for(int y = 77; y <= 83; y++)
			{
				for(int z = 9; z <= 15; z++)
				{
					for(int x = 9; x <= 15; x++)
					{
						fakeWorld.setBlockForget(x, y, z, Blocks.stone, 0, 0);
					}
				}
			}
			
			int[][] xyz = new int[][]{
					{12, 77, 12},	//0
					{ 0, 57,  0},	//1
					{12, 80, 9},	//2
					{12, 80, 15},	//3
					{9, 80, 12},	//4
					{15, 80, 12},	//5
					
					
			};
			
			for(int a = 0; a < xyz.length; a++)
			{
				fakeWorld.setBlockForget(xyz[a][0], xyz[a][1], xyz[a][2], Blocks.cobblestone, 0, 0);
			}
			
			
		//		Schematic schem = Schematic.createSchematic(0, 0, 0, 15, 255, 15, fakeWorld);
		//		schem.name = "TEST";
		//		schem.saveTo(new File("C:/Users/James/Desktop/Schematic/AAChunk.schematic"));
			
			
			FakePlayer fp = new FakePlayer(fakeWorld);
			
			
			
			for(Object key : Item.itemRegistry.getKeys())
			{
				Item item = (Item)Item.itemRegistry.getObject(key);
				
				if(item == null) { continue; }
				
				ItemStack stack = new ItemStack(item, 64, 0);
				
				Block block = null;
				
				for(int a = 0; a < 6; a++)
				{
					int x = xyz[a][0];
					int y = xyz[a][1];
					int z = xyz[a][2];
					
					try
					{
						stack.tryPlaceItemIntoWorld(fp, fakeWorld, x, y, z, a, sideOffsets[a][0], sideOffsets[a][1], sideOffsets[a][2]);
						
						block = fakeWorld.getBlock(x + offsets[a][0], y + offsets[a][1], z + offsets[a][2]);
						
						
						if(block != null && block != Blocks.air)
						{
							if(!blockToItem.containsKey(block))
							{
								registerBlockItem(block, item);
								
								/*if(block.getItemDropped(0, rand, 0) == item)
								{
									normalizeable.add(Block.getIdFromBlock(block));
								}*/
							}
						}
					}
					catch(Throwable t){}
					
					fakeWorld.resetWorld();
				}
			}
			
			
		}
		catch(Throwable t1)
		{
			t1.printStackTrace();
		}
		
		
		for(Object key : Block.blockRegistry.getKeys())
		{
			Object obj = Block.blockRegistry.getObject(key);
			
			if(obj instanceof Block)
			{
				Block block = (Block)obj;
				
				if(block == Blocks.air)
				{
					continue;
				}
				
				if(!blockToItem.containsKey(block))
				{
					Item item = Item.getItemFromBlock(block);
					
					if(item != null)
					{
						registerBlockItem(block, item);
					}
					else if(!blockToItem.containsKey(block) && !CustomBuilders.customBuilderMap.containsKey(block) && !"tile.null".equals(block.getUnlocalizedName()))
					{
						for(Entry<Block, Item> entry : blockToItem.entrySet())
						{
							if(entry.getKey().getClass() == block.getClass())
							{
								try
								{
									Item i = entry.getKey().getItemDropped(0, rand, 0);
									Item i2 = block.getItemDropped(0, rand, 0);
									
									if(i == i2 && i != null)
									{
										registerBlockItem(block, entry.getValue());
										break;
									}
								}
								catch(Throwable t){}
							}
						}
						
						if(!blockToItem.containsKey(block))
						{
							System.out.println("There is no Block to Item mapping for the block " + block.getUnlocalizedName() + ", " + block.getClass());
						}
					}
				}
			}
		}
		
	}
	
	
	public static void init()
	{
		init2();
		initCustom();
	
	}


	public static ItemStack getItemstack(Block block, int meta) 
	{
		Item item = BlockItemRegistry.blockToItem.get(block);
		
		if(item != null)
		{
			ItemStack stack = new ItemStack(item);
			
			Item dropped = block.getItemDropped(meta, rand, 0);
			
			if(dropped == null || item == block.getItemDropped(meta, rand, 0))
			{
				meta = block.damageDropped(meta);
			}
			
			stack.setItemDamage(meta);
			
			return stack;
		}
		
		return null;
	}
	

	static ArrayList<Integer> normalizeable = new ArrayList<Integer>();
	static ArrayList<Integer> normalizeableItem = new ArrayList<Integer>();


	
	public static int normalizeMeta(Block block, int meta)
	{
		if(normalizeable.contains(Block.getIdFromBlock(block)))
		{
			return block.damageDropped(meta);
		}
		else if(normalizeableItem.contains(Block.getIdFromBlock(block)))
		{
			int damage = block.damageDropped(meta);
			int quantity = block.quantityDropped(rand);
			
			if(quantity == damage && quantity != 1)
			{
				return 0;
			}
			
			return damage;
		}
		return meta;
	}

	

}
