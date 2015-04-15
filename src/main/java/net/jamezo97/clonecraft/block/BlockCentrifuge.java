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
	public void registerBlockIcons(IIconRegister ir) {
		this.blockIcon = ir.registerIcon("clonecraft:side");
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iba, int x, int y, int z){
		int meta = iba.getBlockMetadata(x, y, z);
		if(meta == 0 || meta == 2){
			this.setBlockBounds(0.0625F, 0.0F, 0.1875F, 0.9375F, 0.5625F, 0.8125F);
		}else{
			this.setBlockBounds(0.1875F, 0.0F, 0.0625F, 0.8125F, 0.5625F, 0.9375F);
		}
		
    }

	
	@Override
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int x, int y, int z) {
		setBlockBoundsBasedOnState(par1World, x, y, z);
		return super.getSelectedBoundingBoxFromPool(par1World, x, y, z);
	}

	



	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int x, int y, int z) {
		setBlockBoundsBasedOnState(par1World, x, y, z);
		return super.getCollisionBoundingBoxFromPool(par1World, x, y, z);
	}


	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityCentrifuge te = getTE(world, x, y, z);
		if(te != null){
			te.dropAllItems();
		}
		super.breakBlock(world, x, y, z, block, meta);
	}


	public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(CloneCraft.INSTANCE, GuiHandler.CENTRIFUGE, world, i, j, k);
		return true;
	}



	public TileEntityCentrifuge getTE(World world, int x, int y, int z){
		TileEntity te = world.getTileEntity(x, y, z);
		if(te instanceof TileEntityCentrifuge){
			return (TileEntityCentrifuge)te;
		}
		return null;
	}


	@Override
	public TileEntity createNewTileEntity(World world, int metaData) {
		return new TileEntityCentrifuge();
	}

	


	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	int renderID = 0;
	
	@Override
	public int getRenderType() {
		return renderID;
	}
	
	 public boolean isOpaqueCube(){
		 return false;
	 }



	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		byte b0 = 0;
        int l = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        world.setBlockMetadataWithNotify(x, y, z, l, 3);
	}




	public void setRenderID(int nextRenderID) {
		renderID = nextRenderID;
	}

	
	
}
