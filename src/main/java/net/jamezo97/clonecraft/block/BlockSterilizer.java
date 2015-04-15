package net.jamezo97.clonecraft.block;

import java.util.List;

import net.jamezo97.clonecraft.tileentity.TileEntitySterilizer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockSterilizer extends BlockContainer{

	IIcon topEmpty = null;
	IIcon topFull = null;
	IIcon topDirty = null;
	
	public BlockSterilizer() {
		super(Material.rock);
		this.setBlockTextureName("CloneCraft:side");
	}

	

	@Override
	public void registerBlockIcons(IIconRegister ir) {
		super.registerBlockIcons(ir);
		topEmpty = ir.registerIcon("CloneCraft:sterilizerEmpty");
		topFull = ir.registerIcon("CloneCraft:sterilizerFull");
		topDirty = ir.registerIcon("CloneCraft:sterilizerDirty");
	}
	
	
	
	@Override
	public void breakBlock(World par1World, int x, int y, int z, Block block, int par6) {
		TileEntity te = par1World.getTileEntity(x, y, z);
		if(te != null && te instanceof TileEntitySterilizer){
			((TileEntitySterilizer)te).dropItems();
		}
		super.breakBlock(par1World, x, y, z, block, par6);
	}



	public IIcon getDefaultSideIcon(){
		return blockIcon;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		if(side == 1){
			if(meta == 1 || meta == 3){
				return topFull;
			}else if(meta == 2){
				return topDirty;
			}
			return topEmpty;
		}
		return super.getIcon(side, meta);
	}
	
	
	
	@Override
	public boolean onBlockActivated(World par1World, int x, int y, 
			int z, EntityPlayer par5EntityPlayer, int par6, float par7,
			float par8, float par9) {
		ItemStack stack = par5EntityPlayer.getCurrentEquippedItem();
		if(stack != null){
			Item stackItem = stack.getItem();
			int metaData = par1World.getBlockMetadata(x, y, z);
			if(stackItem == Items.bucket){
				if(metaData > 0){
					par1World.setBlockMetadataWithNotify(x, y, z, 0, 2);
					((TileEntitySterilizer)par1World.getTileEntity(x, y, z)).dirtiness = 0;
				}
				return true;
			}else if(stackItem == Items.water_bucket){
				if(metaData == 0){
					par1World.setBlockMetadataWithNotify(x, y, z, 1, 2);
					if(!par5EntityPlayer.capabilities.isCreativeMode){
						stack.func_150996_a(Items.bucket);
					}
				}
				return true;
			}
		}
		
		
		return false;
	}

	@Override
    public void addCollisionBoxesToList(World par1World, int par2, int par3, int par4, AxisAlignedBB par5AxisAlignedBB, List par6List, Entity par7Entity)
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(par1World, par2, par3, par4, par5AxisAlignedBB, par6List, par7Entity);
        this.setBlockBoundsForItemRender();
    }


    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return true;
    }

    public int getRenderType()
    {
        return 0;
    }
    

	/**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntitySterilizer();
	}
	
    
    
	

}
