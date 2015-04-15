package net.jamezo97.clonecraft;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CloneCraftCreativeTab extends CreativeTabs{

	 public CloneCraftCreativeTab(String par2Str) {
		super(par2Str);
	}

	@SideOnly(Side.CLIENT)
     public Item getTabIconItem()
     {
		return CloneCraft.INSTANCE.itemNeedle;
     }
	
}
