package net.jamezo97.clonecraft.raytrace;

import net.minecraft.block.Block;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class RayTracer
{

	public static Point rayTraceSimpleGetEnd(World world, Vec3 va, Vec3 vb, int accuracy, BlockChecker checker)
	{

		double dx = vb.xCoord - va.xCoord;
		double dy = vb.yCoord - va.yCoord;
		double dz = vb.zCoord - va.zCoord;
		int ix, iy, iz;
		double x, y, z;
		Block b;
		for (int a = 1; a <= accuracy; a++)
		{
			x = va.xCoord + (((float) a) / ((float) accuracy)) * dx;
			y = va.yCoord + (((float) a) / ((float) accuracy)) * dy;
			z = va.zCoord + (((float) a) / ((float) accuracy)) * dz;
			ix = (int) Math.floor(x);
			iy = (int) Math.floor(y);
			iz = (int) Math.floor(z);
			b = world.getBlock(ix, iy, iz);
			if (!checker.isBlockSeeThru(b, ix, iy, iz, world))
			{
				if (x - ix > b.getBlockBoundsMinX() && x - ix < b.getBlockBoundsMaxX() && y - iy > b.getBlockBoundsMinY() && y - iy < b.getBlockBoundsMaxY()
						&& z - iz > b.getBlockBoundsMinZ() && z - iz < b.getBlockBoundsMaxZ())
				{
					return new Point(va.xCoord + (((float) a) / ((float) accuracy)) * dx, va.yCoord + (((float) a) / ((float) accuracy)) * dy, va.zCoord
							+ (((float) a) / ((float) accuracy)) * dz);
				}

			}
		}

		return null;
	}

	public static boolean rayTraceSimple(World world, Vec3 va, Vec3 vb, int accuracy, BlockChecker checker)
	{

		double dx = vb.xCoord - va.xCoord;
		double dy = vb.yCoord - va.yCoord;
		double dz = vb.zCoord - va.zCoord;
		int x, y, z;
		Block b;
		for (int a = 1; a < accuracy; a++)
		{
			x = (int) Math.floor(va.xCoord + (((float) a) / ((float) accuracy)) * dx);
			y = (int) Math.floor(va.yCoord + (((float) a) / ((float) accuracy)) * dy);
			z = (int) Math.floor(va.zCoord + (((float) a) / ((float) accuracy)) * dz);
			b = world.getBlock(x, y, z);
			if (!checker.isBlockSeeThru(b, x, y, z, world))
			{
				return false;
			}
		}

		return true;
	}
}
