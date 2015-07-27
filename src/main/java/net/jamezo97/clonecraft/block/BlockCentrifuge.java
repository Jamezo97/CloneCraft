package net.jamezo97.clonecraft.block;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.GuiHandler;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCentrifuge extends BlockContainer{

	
	
	public BlockCentrifuge() {
		super(Material.iron);
	}
	
	
	

	@Override
	public void registerBlockIcons(IIconRegister ir) 
	{
		this.blockIcon = ir.registerIcon("clonecraft:side");
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z)
	{
		int meta = iba.getBlockMetadata(x, y, z) & 3;
		
		if(meta == 0 || meta == 2)
		{
			this.setBlockBounds(0.0625F, 0.0F, 0.1875F, 0.9375F, 0.5625F, 0.8125F);
		}
		else
		{
			this.setBlockBounds(0.1875F, 0.0F, 0.0625F, 0.8125F, 0.5625F, 0.9375F);
		}
		
    }

	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x, int y, int z) 
	{
		setBlockBoundsBasedOnState(par1World, x, y, z);
		return super.getSelectedBoundingBoxFromPool(par1World, x, y, z);
	}

	



	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) 
	{
		setBlockBoundsBasedOnState(par1World, x, y, z);
		return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
	}


	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) 
	{
		TileEntityCentrifuge te = getTE(world, x, y, z);
		if(te != null)
		{
			te.dropAllItems();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}


	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(CloneCraft.INSTANCE, GuiHandler.CENTRIFUGE, world, i, j, k);
		return true;
	}



	public TileEntityCentrifuge getTE(World world, int x, int y, int z)
	{
		TileEntity te = world.getTileEntity(x, y, z);
		
		if(te instanceof TileEntityCentrifuge)
		{
			return (TileEntityCentrifuge)te;
		}
		return null;
	}


	@Override
	public TileEntity createNewTileEntity(World world, int metaData) 
	{
		return new TileEntityCentrifuge();
	}

	


	@Override
	public boolean renderAsNormalBlock() 
	{
		return false;
	}

	int renderID = 0;
	
	@Override
	public int getRenderType() 
	{
		return renderID;
	}
	
	 public boolean isOpaqueCube()
	 {
		 return false;
	 }



	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		byte b0 = 0;
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

//        System.out.println(l);
        
        world.setBlockMetadataWithNotify(x, y, z, l, 3);
	}


	

	public void setRenderID(int nextRenderID)
	{
		renderID = nextRenderID;
	}

	
	
	@Override
	public void onNeighborBlockChange(World world, int posX, int posY, int posZ, Block nBlock) 
	{
		super.onNeighborBlockChange(world, posX, posY, posZ, nBlock);
		
		int oldMeta = world.getBlockMetadata(posX, posY, posZ);
		
		boolean isPowered = world.isBlockIndirectlyGettingPowered(posX, posY, posZ);
		
		//0, 1, 2, 3 - 2 bits. 1 2, 2+1
		if(isPowered != ((oldMeta & 4) == 4))
		{
			oldMeta &= 3;
			
			//Don't cause a block update.
			world.setBlockMetadataWithNotify(posX, posY, posZ, oldMeta | ((isPowered?1:0) << 2), 2);
			
			if(isPowered)
			{
				TileEntity te = world.getTileEntity(posX, posY, posZ);
				
				if(te instanceof TileEntityCentrifuge)
				{
					((TileEntityCentrifuge)te).toggleOnOffFromPower();
				}
			}
			
			
		}
		
//		if(world.is)
	}





	/**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int side)
    {
//    	System.out.println(side);
        return ((TileEntityCentrifuge)p_149736_1_.getTileEntity(p_149736_2_, p_149736_3_, p_149736_4_)).calcRedstoneFromInventory(side);
    }
}
