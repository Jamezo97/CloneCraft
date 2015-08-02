package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SlabCustomBuilder implements CustomBuilder
{

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack using)
	{
		int req = 1;
		if (block instanceof BlockSlab)
		{
			BlockSlab slab = (BlockSlab) block;
			if (slab.quantityDropped(ai.clone.getRNG()) == 2)
			{
				req = 2;
			}
		}

		if (using.stackSize >= req)
		{
			world.setBlock(x, y, z, block, meta, 3);
			using.stackSize -= req;
			return true;
		}
		return false;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		if (block instanceof BlockSlab)
		{
			BlockSlab slab = (BlockSlab) block;
			Item item = slab.getItem(world, x, y, z);

			if (item != null)
			{
				int size = slab.quantityDropped(ai.clone.getRNG());
				int damage = slab.damageDropped(meta);
				return new ItemStack(item, size, damage);
			}
		}
		return null;
	}

	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 0;
	}

	@Override
	public boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		return false;
	}

	@Override
	public boolean isBlockNormalizable()
	{
		return true;
	}

}
