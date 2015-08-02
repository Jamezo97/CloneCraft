package net.jamezo97.clonecraft.clone.ai.block;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.util.ChunkCoordinates;

public class BlockCoord implements Comparable<BlockCoord>
{

	// Squared
	final double distance;

	int posX, posY, posZ;

	public BlockCoord(int posX, int posY, int posZ, EntityClone clone)
	{
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;

		double x = clone.posX - posX;
		double y = clone.posY - posY;
		double z = clone.posZ - posZ;

		distance = x * x + y * y + z * z;
	}

	@Override
	public int compareTo(BlockCoord blockCoord)
	{
		if (this.distance > blockCoord.distance)
		{
			return 1;
		}
		else if (this.distance < blockCoord.distance)
		{
			return -1;
		}
		return 0;
	}

}
