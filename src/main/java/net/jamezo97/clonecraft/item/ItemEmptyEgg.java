package net.jamezo97.clonecraft.item;

import javax.swing.Icon;

import net.jamezo97.clonecraft.CloneCraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemEmptyEgg extends Item{

	public IIcon inside;
	
	public ItemEmptyEgg() {
		setMaxStackSize(16);
		this.setCreativeTab(CloneCraft.creativeTab);
	}

	
	
	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack) {
		return StatCollector.translateToLocal("cc.item.emptyegg.name");
	}



	@Override
	public void registerIcons(IIconRegister ir) {
		super.registerIcons(ir);
		inside = ir.registerIcon("CloneCraft:spawnEggIn");
		((ItemSpawnEgg)CloneCraft.INSTANCE.itemSpawnEgg).setIcons(itemIcon, inside);
	}
	
	
	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return 0xffdfce9b;
	}

	@Override
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if(pass == 1){
			return inside;
		}
		return itemIcon;
	}
	
	
	

}
