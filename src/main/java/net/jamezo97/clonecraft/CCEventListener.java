package net.jamezo97.clonecraft;

import net.jamezo97.clonecraft.recipe.CloneCraftCraftingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
//import cpw.mods.fml.common.event.

public class CCEventListener {
	
	@SubscribeEvent
	public void ItemCraftedEvent(ItemCraftedEvent param){
		CloneCraftCraftingHandler.onCrafting(param.player, param.crafting, param.craftMatrix);
	}
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void renderLast(RenderWorldLastEvent event){
		EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
		if(renderView != null){
			float p = event.partialTicks;
			GL11.glPushMatrix();
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glTranslated(-CloneCraftHelper.interpolate(renderView.prevPosX, renderView.posX, p), -CloneCraftHelper.interpolate(renderView.prevPosY, renderView.posY, p), -CloneCraftHelper.interpolate(renderView.prevPosZ, renderView.posZ, p));
			CCPostRender.postRender(p);
			GL11.glPopMatrix();
		}
	}
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event){
		CCPostRender.checkRenderables();
	}
	
	

}
