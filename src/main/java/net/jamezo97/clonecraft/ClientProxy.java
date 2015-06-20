package net.jamezo97.clonecraft;

import net.jamezo97.clonecraft.entity.custom.RenderCustom;
import net.jamezo97.clonecraft.render.CentrifugeRenderHandler;
import net.jamezo97.clonecraft.render.LifeInducerRenderHandler;
import net.jamezo97.clonecraft.render.RenderClone;
import net.jamezo97.clonecraft.render.RenderSpawnEgg;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{

	public static CentrifugeRenderHandler rh;
	
	public static KeyBinding kb_select;
	
	@Override
	public void init(CloneCraft craft) {
		super.init(craft);
		
	}
	
	public void preInit(CloneCraft craft)
	{
		super.preInit(craft);
		RenderingRegistry.registerEntityRenderingHandler(net.jamezo97.clonecraft.entity.EntitySpawnEgg.class, new RenderSpawnEgg());
		
		RenderingRegistry.registerEntityRenderingHandler(net.jamezo97.clonecraft.clone.EntityClone.class, new RenderClone());
		
		RenderingRegistry.registerEntityRenderingHandler(net.minecraft.entity.EntityCustom.class, new RenderCustom());
		
		int nextRenderID = RenderingRegistry.getNextAvailableRenderId();


		RenderingRegistry.registerBlockHandler(rh = new CentrifugeRenderHandler(nextRenderID));
		CloneCraft.INSTANCE.blockCentrifuge.setRenderID(nextRenderID);
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(TileEntityCentrifuge.class, rh);
		
		nextRenderID = RenderingRegistry.getNextAvailableRenderId();
		
		LifeInducerRenderHandler lh;
		RenderingRegistry.registerBlockHandler(lh = new LifeInducerRenderHandler(nextRenderID));
		CloneCraft.INSTANCE.blockLifeInducer.setRenderID(nextRenderID);
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(TileEntityLifeInducer.class, lh);
		
		kb_select = new KeyBinding("key.selectClone", Keyboard.KEY_R, "key.categories.clonecraft");
		ClientRegistry.registerKeyBinding(kb_select);
	}

	@Override
	public void postInit(CloneCraft cloneCraft) {
		super.postInit(cloneCraft);
		Reflect.init(Side.CLIENT);
	}
	
	

	
	
}
