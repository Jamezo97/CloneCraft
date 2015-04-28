package net.jamezo97.clonecraft;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.recipe.CloneCraftCraftingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ServerChatEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
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
			
			renderSelectedClone(p);
			GL11.glPopMatrix();
		}
	}
	
	long countDown = -1;
	
	int countDownBegin = 1000;
	
	@SideOnly(value = Side.CLIENT)
	public void renderSelectedClone(float p){
		if(countDown == -1 || selectedClone == null){
			return;
		}
		if(!selectedClone.isEntityAlive()){
			selectedClone = null;
			countDown = -1;
			return;
		}
		long diff = countDown-System.currentTimeMillis();
		float intensity = 1.0f;
		if(diff > 100){
			intensity = (float)diff / (float)countDownBegin;
		}
		else
		{
			intensity = 200f / (float)countDownBegin;
		}
		RenderEntityOverlay rs = new RenderEntityOverlay(selectedClone, 0xff000000 | 0xff0000);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		rs.setOpacity(intensity);
		
//		rs.rotate(0);
		
		rs.render(p);
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event){
//		System.out.println("Input");
		if(ClientProxy.kb_select.isPressed())
		{
//			System.out.println("Pressed");
			if(Minecraft.getMinecraft().thePlayer != null)
			{
				MovingObjectPosition mop = rayTraceWithEntities(100, Minecraft.getMinecraft().thePlayer, 1);//Minecraft.getMinecraft().objectMouseOver;//Minecraft.getMinecraft().thePlayer.rayTrace(100, 1);
				if(mop != null && mop.entityHit instanceof EntityClone)
				{
					selectedClone = (EntityClone)mop.entityHit;
					countDown = System.currentTimeMillis() + countDownBegin;
//					System.out.println("Hit clone");
				}
				else
				{
//					System.out.println("nope clone: " + mop.hitVec);
					countDown = -1;
					selectedClone = null;
				}
			}
		}
	}
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event){
		CCPostRender.checkRenderables();
	}
	
	EntityClone selectedClone = null;

	@SubscribeEvent
	public void onChat(ServerChatEvent event){
		if(event.message.startsWith("\\"))
		{
			//CloneCraft commands begins with a backward slash
			String command = event.message.substring(1);
			
			List clonesRaw = event.player.worldObj.getEntitiesWithinAABB(EntityClone.class, event.player.boundingBox.expand(64, 48, 64));
			
			if(clonesRaw.size() == 0)
			{
				return;
			}
			
			EntityClone clone;
			
			ArrayList<CloneEntry> cloneEntries = null;
			
			for(int a = 0; a < clonesRaw.size(); a++)
			{
				clone = ((EntityClone)clonesRaw.get(a));
				
				if(clone.getOptions().command.get() && clone.canUseThisEntity(event.player))
				{
					if(cloneEntries == null)
					{
						cloneEntries = new ArrayList<CloneEntry>();
					}
					
					if(command.toLowerCase().contains(clone.getCommandSenderName().toLowerCase()))
					{
//						System.out.println("Contains");
						cloneEntries.add(new CloneEntry(clone, (8192+(float)clone.getDistanceSqToEntity(event.player))));
					}
					else
					{
						//root(64*64+64*64)^2 = 8192 = max distance between clone and player..
						cloneEntries.add(new CloneEntry(clone, 8192-(float)clone.getDistanceSqToEntity(event.player)));
					}
				}
			}
//			System.out.println(cloneEntries);
			if(cloneEntries == null || cloneEntries.size() == 0)
			{
				event.setCanceled(true);
				return;
			}
			
//			Collections.sort(cloneEntries, CloneEntry.cloneEntryCompare);
			
			float largest = 0f;
			
			EntityClone bestClone = null;
			
			for(int a = 0; a < cloneEntries.size(); a++)
			{
				if(bestClone == null)
				{
					largest = cloneEntries.get(a).confidence;
					bestClone = cloneEntries.get(a).clone;
				}
				else if(cloneEntries.get(a).confidence > largest)
				{
					largest = cloneEntries.get(a).confidence;
					bestClone = cloneEntries.get(a).clone;
				}
			}
			
			if(bestClone != null)
			{
				bestClone.getInterpretter().parseInput(command, event.player);
				event.setCanceled(true);
			}
		}
	}
	
	
	
	public static class CloneEntry{
		
		EntityClone clone;
		float confidence;
		
		public CloneEntry(EntityClone clone, float confidence){
			this.clone = clone;
			this.confidence = confidence;
		}
		
		
		public static Comparator<CloneEntry> cloneEntryCompare =  new Comparator<CloneEntry>(){

			@Override
			public int compare(CloneEntry c1, CloneEntry c2) {
				if(c1.confidence > c2.confidence){
					return 1;
				}else if(c1.confidence < c2.confidence){
					return -1;
				}
				return 0;
			}
			
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	public MovingObjectPosition rayTraceWithEntities(double d0, EntityLivingBase renderViewEntity, float partial){
		//Minecraft mc = Minecraft.getMinecraft();
		
		Entity pointedEntity = null;
		
		if (renderViewEntity != null)
        {
            if (renderViewEntity.worldObj != null)
            {
//                double d0 = (double)mc.playerController.getBlockReachDistance();
            	MovingObjectPosition objectMouseOver = renderViewEntity.rayTrace(d0, partial);
                double d1 = d0;
                Vec3 vec3 = renderViewEntity.getPosition(partial);

//                if (mc.playerController.extendedReach())
//                {
//                    d0 = 6.0D;
//                    d1 = 6.0D;
//                }
//                else
//                {
//                    if (d0 > 3.0D)
//                    {
//                        d1 = 3.0D;
//                    }
//
//                    d0 = d1;
//                }

//                if (mc.objectMouseOver != null)
//                {
//                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
//                }

                Vec3 vec31 = renderViewEntity.getLook(partial);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                pointedEntity = null;
                Vec3 vec33 = null;
                float f1 = 1.0F;
                List list = renderViewEntity.worldObj.getEntitiesWithinAABBExcludingEntity(renderViewEntity, renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity == renderViewEntity.ridingEntity && !entity.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        pointedEntity = entity;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }
                                else
                                {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null/* && (d2 < d1 || mc.objectMouseOver == null)*/)
                {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

//                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
//                    {
//                        pointedEntity = pointedEntity;
//                    }
                }
                return objectMouseOver;
            }
        }
		return null;
	}
	

}
