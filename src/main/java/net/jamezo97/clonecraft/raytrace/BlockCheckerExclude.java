package net.jamezo97.clonecraft.raytrace;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockCheckerExclude implements BlockChecker
{

	IntPos[] pos = null;

	public BlockCheckerExclude setExclusions(IntPos... c)
	{
		pos = c;
		return this;
	}

	@Override
	public boolean isBlockSeeThru(Block block, int x, int y, int z, World world)
	{
		if (block == Blocks.air || block == Blocks.water)
		{
			return true;
		}
		for (int a = 0; a < pos.length; a++)
		{
			if (pos[a].x == x && pos[a].y == y && pos[a].z == z)
			{
				return true;
			}
		}
		return false;
	}

}
