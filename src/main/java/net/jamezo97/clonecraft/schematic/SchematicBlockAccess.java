package net.jamezo97.clonecraft.schematic;

import net.minecraft.block.Block;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class SchematicBlockAccess implements IBlockAccess{
	
	public Schematic schem;
	
	WorldClient world;
	
	int posX, posY, posZ;
	
	public SchematicBlockAccess(Schematic schem, int posX, int posY, int posZ, WorldClient world)
	{
		this.schem = schem;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.world = world;
	}

	@Override
	public Block getBlock(int x, int y, int z) {
		if(schem.coordExists(x, y, z))
		{
			return schem.blockAt(schem.posToIndex(x, y, z));
		}
		return Blocks.air;
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) {
		//This will probably cause a bug..
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
//		world.getLightBrightnessForSkyBlocks(p_72802_4_, p_72802_4_, p_72802_4_, p_72802_4_);
		return 0/*15728880*/; //15 << 20 | 15 << 4
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) {
		if(schem.coordExists(x, y, z))
		{
			return schem.blockMetaAt(schem.posToIndex(x, y, z));
		}
		return 0;
	}

	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_) {
		return 0;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) {
		if(schem.coordExists(x, y, z))
		{
			return schem.blockIdAt(schem.posToIndex(x, y, z)) == 0;
		}
		return true;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return world.getBiomeGenForCoords(x + posX, z + posZ);
	}

	@Override
	public int getHeight() {
		return world.getHeight();
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
		return _default;
	}

	
	
}
