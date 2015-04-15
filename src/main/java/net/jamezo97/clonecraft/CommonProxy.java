package net.jamezo97.clonecraft;

import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	
	
	public void load(CloneCraft craft){
		
		
		
		NetworkRegistry.INSTANCE.registerGuiHandler(craft, new GuiHandler());

		EntityRegistry.registerModEntity(net.jamezo97.clonecraft.entity.EntitySpawnEgg.class, "EntitySpawnEgg", 0, craft, 80, 10, true);
		
		EntityRegistry.registerModEntity(net.jamezo97.clonecraft.clone.EntityClone.class, "EntityClone", 1, craft, 512, 2, true);
		
		GameRegistry.registerTileEntity(net.jamezo97.clonecraft.tileentity.TileEntitySterilizer.class, "Sterilizer");
		
		GameRegistry.registerTileEntity(net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge.class, "Centrifuge");
		
		GameRegistry.registerTileEntity(net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer.class, "LifeInducer");
		
		
		
	}

	public void postInit(CloneCraft cloneCraft) {
		Reflect.init(Side.SERVER);
	}
	
}
