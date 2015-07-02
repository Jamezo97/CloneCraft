package net.jamezo97.clonecraft.block;

import java.util.List;
import java.util.Random;

import javax.swing.Icon;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.network.Handler3LifeInducerUpdates;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAntenna extends Block {

//	Icon topBottomTest;
	
	public BlockAntenna() {
		super(Material.iron);
		setBlockTextureName("CloneCraft:antenna");
		setBounds();
		this.setTickRandomly(true);
	}

	int renderID = 0;
	
	public void setRenderID(int nextRenderID) {
		renderID = nextRenderID;
	}
	

	@Override
	public int tickRate(World par1World) {
		return 0;
	}

	public int getTopAntenna(int x, int y, int z){
		
		return -1;
	}
	
	public int getBottomAntenna(int x, int y, int z){
		
		return -1;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		int blockMeta = world.getBlockMetadata(x, y, z);
		if(blockMeta == 0){
			if((world.isRaining() || world.isThundering()) && !world.isRemote){
				int topSolidBlock = world.getTopSolidOrLiquidBlock(x, z);
				
				if(rand.nextInt(world.isThundering()?16:28) == 0){
					int topBlock = world.getTopSolidOrLiquidBlock(x, z)-1;
					if(topBlock > -1){
						int topConductiveFromHere = y;
						int max = 256;
						while(max-- > 0){
							if(topConductiveFromHere == topBlock){
								break;
							}else if(world.getBlock(x, topConductiveFromHere+1, z) == this){
								topConductiveFromHere++;
							}
						}
						if(topConductiveFromHere == topBlock){
							int minX = x - 15;
							int minZ = z - 15;
							int maxX = x + 15;
							int maxZ = z + 15;
							for(int zC = minZ; zC <  maxZ; zC++){
								for(int xC = minX; xC <  maxX; xC++){
									if(zC != z && xC != x){
										int highest = world.getTopSolidOrLiquidBlock(xC, zC)-1;
										if(highest >= topBlock){
											return;
										}
									}
								}
							}
							EntityLightningBolt bolt = new EntityLightningBolt(world, x, topBlock+1, z);
							world.addWeatherEffect(bolt);
							chargeAntenna(x, topBlock, z, world);
						}
					}
				}
			}
		}else if(blockMeta == 1){
			world.setBlock(x, y, z, this, 0, 2);
			if(y - 1 > 0){
				if(world.getBlock(x, y-1, z) == this){
					chargeAntenna(x, y-1, z, world);
				}else if(world.getBlock(x, y-1, z) == CloneCraft.INSTANCE.blockLifeInducer){
					TileEntity e = world.getTileEntity(x, y-1, z);
					if(e == null || e instanceof TileEntityLifeInducer){
						chargeLifeMachine(x, y-1, z, world, (TileEntityLifeInducer)e);
					}
				}else{
					chargeLifeMachine(x, y-1, z, world, null);
					List entities = world.getEntitiesWithinAABB(Entity.class, getCollisionBoundingBoxFromPool(world, x, y, z).expand(10, 8, 10));
					for(int a = 0; a < entities.size(); a++){
						Entity e = (Entity)entities.get(a);
						if(e != null){
				    		double xMid = x+.5;
				    		double yMid = y-.5;
				    		double zMid = z+.5;
				    		
				    		double xE = e.posX;
				    		double yE = e.posY;
				    		double zE = e.posZ;
				    		
				    		double dX = xE - xMid;
				    		double dY = yE - yMid;
				    		double dZ = zE - zMid;
				    		
				    		double distance = /*Math.sqrt*/(dX * dX + dY * dY + dZ * dZ);
				    		e.attackEntityFrom(DamageSource.generic, 4.0f/(float)distance);
				    		dX /= distance;
				    		dY /= distance;
				    		dZ /= distance;
				    		
				    		e.motionX += dX*2;
				    		e.motionY += dY*2;
				    		e.motionZ += dZ*2;
				    		e.velocityChanged = true;
						}
					}
				}
			}
		}

	}
	
	
	
	
	@Override
	public boolean onBlockActivated(World world, int x,
			int y, int z, EntityPlayer p_149727_5_,
			int p_149727_6_, float p_149727_7_, float p_149727_8_,
			float p_149727_9_) {
		if(p_149727_5_.capabilities.isCreativeMode)
		{
			chargeAntenna(x, y, z, world);
		}
		
		return true;//super.onBlockActivated(world, x, y, z, p_149727_5_, p_149727_6_, p_149727_7_, p_149727_8_, p_149727_9_);
	}


	public void chargeAntenna(int x, int y, int z, World world)
	{
		if(!world.isRemote)
		{
			world.setBlock(x, y, z, this, 1, 2);
			world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
			new Handler3LifeInducerUpdates(x, y, z, 0, 0).sendToAllNear(x, y, z, 16, world.provider.dimensionId);
		}
	}

	public void chargeLifeMachine(int x, int y, int z, World world, TileEntityLifeInducer te){
		if(!world.isRemote)
		{
			if(te != null){
				te.charge();
			}
			new Handler3LifeInducerUpdates(x, y, z, 1, 0).sendToAllNear(x, y, z, 16, world.provider.dimensionId);
		}
		
	}

	@Override
	public IIcon getIcon(int par1, int par2) {
		//If the side is the top of bottom (0 or 1)
//		if(par1 < 2){
//			return CloneCraft.lifeInducer.getTopIconThing();
//		}
		if(par2 == 1){
			return charged;
		}
		return super.getIcon(par1, par2);
	}

    public void onEntityCollidedWithBlock(World par1World, int x, int y, int z, Entity e){
    	int meta = par1World.getBlockMetadata(x, y, z);
    	if(meta == 1)
    	{
    		e.attackEntityFrom(DamageSource.generic, 4.0f);
    		double xMid = x+.5;
    		double zMid = z+.5;
    		
    		double xE = e.posX;
    		double zE = e.posZ;
    		
    		double dX = xE - xMid;
    		double dZ = zE - zMid;
    		
    		double distance = Math.sqrt(dX * dX + dZ * dZ);
    		
    		e.motionX += dX*3;
    		e.motionZ += dZ*3;
    		e.motionY += .65;
    		
    		
    		
    	}
    }

	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return super.getLightValue(world, x, y, z);
	}


	IIcon charged = null;

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		super.registerBlockIcons(ir);
		charged = ir.registerIcon("CloneCraft:chargedAntenna");
	}

	
	


	@Override
	public int getBlockColor() {
		return super.getBlockColor();
	}



	@Override
	public boolean isOpaqueCube() {
		return false;
	}

    public void setBounds(){
    	setBlockBounds(.4f, 0, .4f, .6f, 1f, .6f);
    }

	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x, int y, int z) {
		setBounds();
		return super.getSelectedBoundingBoxFromPool(par1World, x, y, z);
	}

	



	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
		setBounds();
		return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
	}



	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}



	@Override
	public int getRenderType() {
		return renderID;
	}

}
