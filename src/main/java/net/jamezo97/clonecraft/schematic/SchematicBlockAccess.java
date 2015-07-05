package net.jamezo97.clonecraft.schematic;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ChunkProviderClient;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(value = Side.CLIENT)
public class SchematicBlockAccess extends World{
	
	Minecraft mc;
	
	public SchematicBlockAccess()
	{
		 super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(0), new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.DEFAULT), new Profiler());
		 this.mc = Minecraft.getMinecraft();
	}
	
	public SchematicBlockAccess loadData(Schematic schem, int posX, int posY, int posZ/*, WorldClient world*/)
	{
		this.schem = schem;
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
//		this.world = world;
		
		return this;
	}

	public Schematic schem;
	
//	WorldClient world;
	
	int posX, posY, posZ;

	@Override
	public Block getBlock(int x, int y, int z)
	{
		if(schem.coordExists(x, y, z))
		{
			return schem.blockAt(schem.posToIndex(x, y, z));
		}
		return Blocks.air;
	}

	@Override
	public TileEntity getTileEntity(int x, int y, int z) 
	{
		schem.getTileEntity(x, y, z, this);
		return null;
	}

	@Override
	public int getLightBrightnessForSkyBlocks(int p_72802_1_, int p_72802_2_, int p_72802_3_, int p_72802_4_) {
//		world.getLightBrightnessForSkyBlocks(p_72802_4_, p_72802_4_, p_72802_4_, p_72802_4_);
		return 0/*15728880*/; //15 << 20 | 15 << 4
	}

	@Override
	public int getBlockMetadata(int x, int y, int z) 
	{
		if(schem.coordExists(x, y, z))
		{
			return schem.blockMetaAt(schem.posToIndex(x, y, z));
		}
		return 0;
	}

	@Override
	public int isBlockProvidingPowerTo(int p_72879_1_, int p_72879_2_, int p_72879_3_, int p_72879_4_) 
	{
		return 0;
	}

	@Override
	public boolean isAirBlock(int x, int y, int z) 
	{
		if(schem.coordExists(x, y, z))
		{
			return schem.blockIdAt(schem.posToIndex(x, y, z)) == 0;
		}
		return true;
	}

	@Override
	public BiomeGenBase getBiomeGenForCoords(int x, int z) {
		return BiomeGenBase.forest;
	}

	@Override
	public int getHeight() {
		return 256;
	}

	@Override
	public boolean extendedLevelsInChunkCache() {
		return false;
	}

	@Override
	public boolean isSideSolid(int x, int y, int z, ForgeDirection side, boolean _default) {
		return _default;
	}

	IChunkProvider clientChunkProvider;
	
	@Override
	protected IChunkProvider createChunkProvider() {
		this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
	}

	@Override
	protected int func_152379_p()
    {
        return this.mc.gameSettings.renderDistanceChunks;
    }

	@Override
	public Entity getEntityByID(int p_73045_1_)
	{
		return null;
	}

	
	
}
