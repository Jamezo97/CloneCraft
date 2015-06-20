package net.jamezo97.clonecraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.entity.EntityArrowCC;
import net.jamezo97.clonecraft.render.Renderable;
import net.jamezo97.clonecraft.render.RenderableManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class CCPostRender {

	public static void postRender(float partial) {
		for (Entry<RenderableManager, Renderable> entry : handlerToRender
				.entrySet()) {
			GL11.glPushMatrix();
			entry.getValue().render(partial);
			GL11.glPopMatrix();
		}
		if (Minecraft.getMinecraft().thePlayer != null) {
			EntityPlayer p_77615_3_ = Minecraft.getMinecraft().thePlayer;
//			System.out.println(p_77615_3_.rotationYaw);
			RenderOverlay render = null;
			if (p_77615_3_.getItemInUse() != null && p_77615_3_.getItemInUse().getItemUseAction() == EnumAction.bow) {
				ItemStack p_77615_1_ = p_77615_3_.getItemInUse();
				int p_77615_4_ = p_77615_3_.getItemInUseCount();
				World p_77615_2_ = p_77615_3_.worldObj;
				// (ItemStack p_77615_1_, World p_77615_2_, EntityPlayer
				// p_77615_3_, int p_77615_4_

				
				ItemBow ItemBow = Items.bow;

				int j = ItemBow.getMaxItemUseDuration(p_77615_1_) - p_77615_4_;

				float f = (float) j / 20.0F;
				f = (f * f + f * 2.0F) / 3.0F;

				if ((double) f < 0.1D) {
					return;
				}

				if (f > 1.0F) {
					f = 1.0F;
				}

				EntityArrowCC entityarrow = new EntityArrowCC(p_77615_2_, p_77615_3_, f * 2.0F);
				int stepsMax = 200;
				
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glLineWidth(3f);
				GL11.glBegin(GL11.GL_LINE_STRIP);
				GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
				while(stepsMax-- >= 0){
					GL11.glVertex3d(entityarrow.posX, entityarrow.posY, entityarrow.posZ);
					if(entityarrow.inGround || entityarrow.isDead || entityarrow.entityHit != null){
//						System.out.println(entityarrow.isDead);
						break;
					}
					entityarrow.onUpdate();
				}
				
				GL11.glEnd();
				
				
				
				
					
				int xTile = entityarrow.field_145791_d;
				int yTile = entityarrow.field_145792_e;
				int zTile = entityarrow.field_145789_f;
				
				if(xTile != -1&& yTile != -1 && zTile != -1){
					render = new RenderOverlay(0xffff0000);
					render.setBounds(xTile, yTile, zTile, xTile+1, yTile+1, zTile+1);
				}else if(entityarrow.entityHit != null){
		            render = new RenderOverlay(0xffff0000);
		            Entity e = entityarrow.entityHit;
		            AxisAlignedBB ax = e.getBoundingBox();
		            if(ax != null){
						render.setBounds(ax.minX, ax.minY, ax.minZ, ax.maxX, ax.maxY, ax.maxZ);
		            }else{
		            	
		            	float width2 = e.width;
		            	if(width2 < 0.01){
		            		width2 = 0.5f;
		            	}else{
		            		width2 /= 2.0f;
		            	}
		            	float height = e.height;
		            	if(height <0.01){
		            		height = 1.5f;
		            	}
		            	
						render.setBounds(e.posX-width2, e.posY, e.posZ-width2, e.posX+width2, e.posY+height, e.posZ+width2);
		            }
//		            render.rotate(e.getRotationYawHead());
				}


			}

			if(render != null){
				render.render(partial);
			}
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
		}
	}

	public static void checkRenderables() {
		ArrayList<RenderableManager> toRemove = new ArrayList<RenderableManager>();
		for (Entry<RenderableManager, Renderable> entry : handlerToRender
				.entrySet()) {
			if (!entry.getKey().canRenderContinue(entry.getValue()))
			{
				toRemove.add(entry.getKey());
			}
		}
		for (int a = 0; a < toRemove.size(); a++) {
			handlerToRender.remove(toRemove.get(a));
		}
	}

	static HashMap<RenderableManager, Renderable> handlerToRender = new HashMap<RenderableManager, Renderable>();

	public static void addRenderable(RenderableManager manager,
			Renderable render) {
		handlerToRender.put(manager, render);
	}

	public static void removeRenderable(RenderableManager manager,
			Renderable render) {
		if (handlerToRender.containsKey(manager)) {
			handlerToRender.remove(manager);
		}
	}
	
	public static class ArrowAccurate extends EntityArrow{

		public ArrowAccurate(World p_i1756_1_, EntityLivingBase p_i1756_2_, float p_i1756_3_) {
			super(p_i1756_1_, p_i1756_2_, p_i1756_3_);
			
	        this.setLocationAndAngles(p_i1756_2_.posX, p_i1756_2_.posY + (double)p_i1756_2_.getEyeHeight(), p_i1756_2_.posZ, p_i1756_2_.rotationYaw, p_i1756_2_.rotationPitch);
	        this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
	        this.posY -= 0.10000000149011612D;
	        this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
	        this.setPosition(this.posX, this.posY, this.posZ);
	        this.yOffset = 0.0F;
	        this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
	        this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
	        this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
	        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, p_i1756_3_ * 1.5F, 0.0F);
		}
		
	}

}
