package net.jamezo97.clonecraft.build;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class FireCustomBuilder implements CustomBuilder{
	
	ItemStack required = new ItemStack(Items.flint_and_steel, 0, 0);

	@Override
	public boolean doCustomBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z, ItemStack stack)
	{
		if(world.getBlock(x, y-1, z).isOpaqueCube())
		{
			if (world.isAirBlock(x, y, z))
            {
                world.playSoundEffect((double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D, "fire.ignite", 1.0F, world.rand.nextFloat() * 0.4F + 0.8F);
                world.setBlock(x, y, z, Blocks.fire);
                
                if(stack != null)
                {
                    stack.damageItem(1, ai.clone);
                }
                return true;
            }
		}
		return false;
	}

	@Override
	public ItemStack getRequiredItemToBuild(EntityAIBuild ai, Block block, int meta, World world, int x, int y, int z)
	{
		return required;
	}


	@Override
	public boolean isCorrectBuildstate(int buildState)
	{
		return buildState == 2;
	}

	@Override
	public boolean shouldIgnoreChangedMeta(EntityAIBuild ai, int x, int y, int z, World world, Block block, int metaAtPos, int metaToPlace)
	{
		return true;
	}
	
	@Override
	public boolean isBlockNormalizable()
	{
		return false;
	}

}
