package net.jamezo97.clonecraft;

import java.nio.IntBuffer;

import net.jamezo97.clonecraft.clone.BreakableBlocks;
import net.jamezo97.clonecraft.gui.GuiScrollableBlocks;
import net.jamezo97.clonecraft.render.CentrifugeRenderHandler;
import net.jamezo97.clonecraft.render.LifeInducerRenderHandler;
import net.jamezo97.clonecraft.render.RenderClone;
import net.jamezo97.clonecraft.render.RenderSpawnEgg;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import org.lwjgl.BufferUtils;

import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy{

	public static CentrifugeRenderHandler rh;
	
	@Override
	public void load(CloneCraft craft) {
		super.load(craft);
		RenderingRegistry.registerEntityRenderingHandler(net.jamezo97.clonecraft.entity.EntitySpawnEgg.class, new RenderSpawnEgg());
		
		RenderingRegistry.registerEntityRenderingHandler(net.jamezo97.clonecraft.clone.EntityClone.class, new RenderClone());
		
		int nextRenderID = RenderingRegistry.getNextAvailableRenderId();
//		CentrifugeRenderHandler rh;
		RenderingRegistry.registerBlockHandler(rh = new CentrifugeRenderHandler(nextRenderID));
		CloneCraft.INSTANCE.blockCentrifuge.setRenderID(nextRenderID);
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(TileEntityCentrifuge.class, rh);
		
		nextRenderID = RenderingRegistry.getNextAvailableRenderId();
		LifeInducerRenderHandler lh;
		RenderingRegistry.registerBlockHandler(lh = new LifeInducerRenderHandler(nextRenderID));
		CloneCraft.INSTANCE.blockLifeInducer.setRenderID(nextRenderID);
		TileEntityRendererDispatcher.instance.mapSpecialRenderers.put(TileEntityLifeInducer.class, lh);
	}

	@Override
	public void postInit(CloneCraft cloneCraft) {
		super.postInit(cloneCraft);
		Reflect.init(Side.CLIENT);
		
//		IntBuffer buf = BufferUtils.createIntBuffer(5);
//		System.out.println(buf.capacity());
		
//		GuiScrollableBlocks.bc.getColourFor(BreakableBlocks.conjoin(1,0));
		
		
		
		
	}
	
	

	
	
}
