package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface CustomBuilder {

	/**
	 * Is this custom builder able to build the given block at the given location in the world.
	 * @param ai The AI Object that is trying to build it
	 * @param block The block that needs to be placed
	 * @param meta The metadata of the block to be placed
	 * @param world The world to place the block in
	 * @param x The x position of the block to be placed in the world
	 * @param y The y position of the block to be placed in the world
	 * @param z The z position of the block to be placed in the world
	 * @return True or false depending on whether it can be built.
	 */
//	boolean isBuildValid(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z);

	/**
	 * Actually build the block in the world.
	 * @param ai The AI Object that is trying to build it
	 * @param block The block that needs to be placed
	 * @param meta The metadata of the block to be placed
	 * @param world The world to place the block in
	 * @param x The x position of the block to be placed in the world
	 * @param y The y position of the block to be placed in the world
	 * @param z The z position of the block to be placed in the world
	 * @param using The ItemStack which should be used to build this.
	 * @return Whether something was built
	 */
	boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using);
	
	/**
	 * Returns the item that this custom builder needs in order to build what it needs to build.
	 * @param ai The AI Object that is trying to build it
	 * @param block The block that needs to be placed
	 * @param meta The metadata of the block to be placed
	 * @param world The world to place the block in
	 * @param x The x position of the block to be placed in the world
	 * @param y The y position of the block to be placed in the world
	 * @param z The z position of the block to be placed in the world
	 */
	ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z);
	
	/**
	 * Returns which build state this Custom Builder should be executed in.
	 * @return
	 */
	boolean isCorrectBuildstate(int buildState);

	/**
	 * Tells the build AI, whether to ignore changed meta data values. If a block is placed, such as redstone, and then a block update changes its power setting, 
	 * its metadata value will change. On another build state run through, this will be detected, and unless this returns true, it will be broken and placed again.
	 * @param ai
	 * @param x
	 * @param y
	 * @param z
	 * @param world
	 * @param block
	 * @param metaAtPos
	 * @param metaToPlace
	 * @return
	 */
	boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z,
			World world, Block block, int metaAtPos, int metaToPlace);

	boolean isBlockNormalizable();

}
