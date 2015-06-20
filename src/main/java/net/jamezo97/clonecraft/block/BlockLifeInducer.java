package net.jamezo97.clonecraft.block;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.GuiHandler;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockLifeInducer extends BlockContainer{
	
	public BlockLifeInducer(){
		super(Material.iron);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityLifeInducer();
	}

	
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}



	int renderID = 0;
	
	public void setRenderID(int id){
		this.renderID = id;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public int getRenderType() {
		return renderID;
	}
	
	


	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
		TileEntity te = par1World.getTileEntity(par2, par3, par4);
		if(te != null && te instanceof IInventory){
			if(te instanceof TileEntityLifeInducer){
				((TileEntityLifeInducer)te).dropAllItems();
				((TileEntityLifeInducer)te).onBroken();
			}
		}
		super.breakBlock(par1World, par2, par3, par4, par5, par6);
	}


	@Override
	public boolean onBlockActivated(World par1World, int par2, int par3, int par4, EntityPlayer par5EntityPlayer, int par6, float par7, float par8, float par9) {
		par5EntityPlayer.openGui(CloneCraft.INSTANCE, GuiHandler.LIFEINDUCER, par1World, par2, par3, par4);
		return true;
	}

	@Override
	public void registerBlockIcons(IIconRegister p_149651_1_) {
		this.blockIcon = null;
	}
	
	
}
