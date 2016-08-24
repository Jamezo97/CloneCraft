package net.jamezo97.clonecraft;

import java.util.Random;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.PlayerTeam;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import cpw.mods.fml.common.IWorldGenerator;

public class CloneCraftVillageGenerator implements IWorldGenerator
{

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider)
	{
		if(random.nextInt(100) == 0)
		{
			int y;
			for(y = 255; y > 0; y--)
			{
				if(world.getBlock(chunkX*16, y, chunkZ*16) == Blocks.air)
				{
					continue;
				}
				else
				{
					break;
				}
			}
			
			Schematic schem = CloneCraft.INSTANCE.schematicList.getRandom(random);
			if(schem != null && schem.blockIds.length < 50000)
			{
				schem.buildInstantly(chunkX*16, y, chunkZ*16, world);
				EntityClone clone = new EntityClone(world);
				clone.setPosition(chunkX*16 + schem.xSize/2, y + schem.ySize/2, chunkZ*16 + schem.zSize/2);
				clone.setCTeam(PlayerTeam.Rampant);
				world.spawnEntityInWorld(clone);
			}
		}
		
	}

}
