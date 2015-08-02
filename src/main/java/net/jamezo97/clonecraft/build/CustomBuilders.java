package net.jamezo97.clonecraft.build;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CustomBuilders {
	
	public static HashMap<Block, CustomBuilder> customBuilderMap = new HashMap<Block, CustomBuilder>();
	
	public static void registerCustomBuilder(CustomBuilder builder, Block theBlock)
	{
		customBuilderMap.put(theBlock, builder);
	}
	
	
	public static boolean hasCustomBuilder(EntityAIBuild ai, int buildState, Block block, int meta, World world, int x, int y, int z)
	{
		if(customBuilderMap.containsKey(block))
		{
			CustomBuilder builder = customBuilderMap.get(block);
			
			if(builder.isCorrectBuildstate(buildState))
			{
				return true;
			}
		}
		return false;
	}
	
	public static ItemStack getRequiredItemstack(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		if(customBuilderMap.containsKey(block))
		{
			CustomBuilder builder = customBuilderMap.get(block);
			
			return builder.getRequiredItemToBuild(ai, block, meta, world, x, y, z);
		}
		return null;
	}
	
	public static boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		if(customBuilderMap.containsKey(block))
		{
			CustomBuilder builder = customBuilderMap.get(block);
			
			return builder.shouldIgnoreChangedMeta(ai, x, y, z, world, block, metaAtPos, metaToPlace);
		}
		return false;
	}
	
	/**
	 * Returns 0 if nothing happens. 1 if something could happen, but not during this build state. 2 If something tried to build, but failed. 3 if something was built.
	 * @param ai
	 * @param buildState
	 * @param block
	 * @param meta
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param stack
	 * @return
	 */
	public static int doCustomBuild(EntityAIBuild ai, int buildState, Block block, int meta, World world, int x, int y, int z, ItemStack stack)
	{
		if(customBuilderMap.containsKey(block))
		{
			CustomBuilder builder = customBuilderMap.get(block);

			if(builder.isCorrectBuildstate(buildState))
			{
				if(builder.doCustomBuild(ai, block, meta, world, x, y, z, stack))
				{
					return 3;
				}
				return 2;
			}
			return 1;
		}
		return 0;
	}

	public static void init() 
	{
		{
			DoorCustomBuilder door = new DoorCustomBuilder();
			
			SlabCustomBuilder slab = new SlabCustomBuilder();

			for(Object key : Block.blockRegistry.getKeys())
			{
				Block block = (Block)Block.blockRegistry.getObject(key);
				if(block instanceof BlockDoor)
				{
					registerCustomBuilder(door, block);
				}
				else if(block instanceof BlockSlab)
				{
					registerCustomBuilder(slab, block);
				}
			}
		}
		
		{
			PistonCustomBuilder pc = new PistonCustomBuilder();

			registerCustomBuilder(pc, Blocks.piston);
			registerCustomBuilder(pc, Blocks.sticky_piston);
			registerCustomBuilder(pc, Blocks.piston_head);
			
		}
		
		registerCustomBuilder(new FireCustomBuilder(), Blocks.fire);

		registerCustomBuilder(new PlantCustomBuilder(Items.wheat_seeds), Blocks.wheat);
		
		registerCustomBuilder(new PlantCustomBuilder(Items.carrot), Blocks.carrots);
		registerCustomBuilder(new PlantCustomBuilder(Items.potato), Blocks.potatoes);
		
		registerCustomBuilder(new PlantCustomBuilder(Items.pumpkin_seeds), Blocks.pumpkin_stem);
		registerCustomBuilder(new PlantCustomBuilder(Items.melon_seeds), Blocks.melon_stem);
		

		registerCustomBuilder(new PlantCustomBuilder(Items.nether_wart), Blocks.nether_wart);
		registerCustomBuilder(new PlantCustomBuilder(Items.reeds), Blocks.reeds);
		registerCustomBuilder(new PlantCustomBuilder(new ItemStack(Items.dye, 1, 3)), Blocks.cocoa);
		
		registerCustomBuilder(new PlantCustomBuilder(Items.skull), Blocks.skull);
		
		
		registerCustomBuilder(new BedCustomBuilder(), Blocks.bed);
		
		registerCustomBuilder(new CakeCustomBuilder(), Blocks.cake);

		registerCustomBuilder(new GrassCustomBuilder(), Blocks.grass);
		registerCustomBuilder(new GrassCustomBuilder(), Blocks.farmland);

		registerCustomBuilder(new LiquidCustomBuilder(Items.water_bucket, Blocks.flowing_water), Blocks.water);
		registerCustomBuilder(new LiquidCustomBuilder(Items.lava_bucket, Blocks.flowing_lava), Blocks.lava);
		
		registerCustomBuilder(new LiquidCustomBuilder(Items.water_bucket, Blocks.flowing_water), Blocks.flowing_water);
		registerCustomBuilder(new LiquidCustomBuilder(Items.lava_bucket, Blocks.flowing_lava), Blocks.flowing_lava);
	}


	

}
