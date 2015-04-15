package net.jamezo97.clonecraft.gui;

import java.util.ArrayList;

import net.jamezo97.clonecraft.EntityColourHandler;
import net.jamezo97.clonecraft.clone.AttackableEntities;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiScrollableEntities extends GuiScrollable{
	
	AttackableEntities attackables;
	
	ArrayList<AttackEntry> allViewable = new ArrayList<AttackEntry>();
	
	ArrayList<AttackEntry> viewable = new ArrayList<AttackEntry>();
	
	EntityClone clone;
	
	public GuiScrollableEntities(int xPosition, int yPosition, int width, int height, EntityClone clone) {
		super(xPosition, yPosition, width, height);
		this.attackables = clone.getOptions().attackables;
		this.clone = clone;
		
		
		for(int a = 0; a < AttackableEntities.validEntitiesArray.length; a++){
			int id = AttackableEntities.validEntitiesArray[a];
			allViewable.add(new AttackEntry(id));
		}
		updateViewable(null);
	}
	
	public void updateViewable(String searchCrit){
		searchCrit = searchCrit==null?null:searchCrit.toLowerCase();
		AttackEntry entry;
		viewable.clear();
		for(int a = 0; a < allViewable.size(); a++){
			entry = allViewable.get(a);
			if(searchCrit == null || searchCrit.length() == 0 || entry.getTransName().toLowerCase().contains(searchCrit)){
				viewable.add(entry);
			}
		}
	}

	@Override
	public boolean isEntrySelected(int entryIndex) {
		return attackables.canAttack(viewable.get(entryIndex).entityId);
	}

	@Override
	public int getEntryCount() {
		return viewable.size();
	}

	@Override
	public int getEntryHeight() {
		return 55;
	}

	@Override
	public void entryClicked(int entryIndex) {
		attackables.toggleEntity(viewable.get(entryIndex).entityId);
	}

	@Override
	public void renderEntry(int entryIndex, int width, int height) {
		int colour = (viewable.get(entryIndex).getBgColour());
		
		if(this.isEntrySelected(entryIndex)){
			int total = ((colour>>16)&0xff) + ((colour>>8)&0xff) + ((colour)&0xff);
			total /= 3;
			
			int r = (colour>>16) & 0xff;
			int g = (colour>>8) & 0xff;
			int b = (colour) & 0xff;
			int tick = clone.ticksExisted;// + entryIndex*8;
			r *= (Math.sin(((float)tick) / 1.5f)+1)/25+0.92;
			g *= (Math.sin(((float)tick) / 1.5f)+1)/25+0.92;
			b *= (Math.sin(((float)tick) / 1.5f)+1)/25+0.92;
			colour = 0xff000000 | (r<<16) | (g<<8) | b;
			
			
			
//			System.out.println(total);
			this.drawRect(0, 0, width, height, total<128?0xffffffff:0xff000000);
			this.drawRect(2, 2, width-2, height-2, colour | 0xff000000);
		}else{
			this.drawRect(0, 0, width, height, colour | 0xff000000);
		}
		
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, viewable.get(entryIndex).getTransName(), 3, 3, 0xffffff);

		this.renderEntity(width-20, +height-15, 23, (float)Math.sin(clone.ticksExisted/70.0f)*30, clone.ticksExisted*3, viewable.get(entryIndex).getEntity(clone.worldObj));

	}
	

	public static void renderEntity(int x, int y, float scale, float rotateX, float rotateY, EntityLivingBase entity)
    {
		if(entity == null)
		{
			return;
		}
//		if(entity.width > 1){
			double height = entity.boundingBox.maxY - entity.boundingBox.minY;
			scale *= (1.4 / height);
//		}
		float rotateYaw = 0;
		float rotatePitch = 0;
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x, (float)y, 50.0F);
        GL11.glScalef((float)(-scale), (float)scale, (float)scale);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        float f2 = entity.renderYawOffset;
        float f3 = entity.rotationYaw;
        float f4 = entity.rotationPitch;
        float f5 = entity.prevRotationYawHead;
        float f6 = entity.rotationYawHead;
        GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-((float)Math.atan((double)(rotatePitch / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
        entity.renderYawOffset = (float)Math.atan((double)(rotateYaw / 40.0F)) * 20.0F;
        entity.rotationYaw = (float)Math.atan((double)(rotateYaw / 40.0F)) * 40.0F;
        entity.rotationPitch = -((float)Math.atan((double)(rotatePitch / 40.0F))) * 20.0F;
        entity.rotationYawHead = entity.rotationYaw;
        entity.prevRotationYawHead = entity.rotationYaw;
        GL11.glTranslatef(0.0F, entity.yOffset, 0.0F);
        RenderManager.instance.playerViewY = 180.0F;
        GL11.glRotated(rotateX, 1, 0, 0);
		GL11.glRotated(rotateY, 0, 1, 0);
        RenderManager.instance.renderEntityWithPosYaw(entity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
        entity.renderYawOffset = f2;
        entity.rotationYaw = f3;
        entity.rotationPitch = f4;
        entity.prevRotationYawHead = f5;
        entity.rotationYawHead = f6;
        GL11.glPopMatrix();
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_CULL_FACE);
    }
	
	
	public class AttackEntry{
		
		int entityId;
		String name;
		EntityLivingBase entity;
		
		public AttackEntry(int entityId){
			this.entityId = entityId;
			Object temp = EntityList.classToStringMapping.get(EntityList.IDtoClassMapping.get(entityId));
			if(temp != null && temp instanceof String){
				this.name = (String)temp;
			}else{
				this.name = "Unknown!";
			}
		}
		
		public String getTransName(){
			return StatCollector.translateToLocal("entity." + name + ".name");
		}
		
		int colour = -1;
		public int getBgColour(){
			if(colour == -1){
				colour = EntityColourHandler.getPrimaryColourForEntityId(entityId);
				int red = (colour >> 16) & 0xff;
				int green = (colour >> 8) & 0xff;
				int blue = (colour) & 0xff;
				red += 50;
				green += 50;
				blue += 50;
				if(red>220){red=220;}
				if(green>220){green=220;}
				if(blue>220){blue=220;}
				colour = (red << 16) | (green << 8) | blue;
			}
			return colour;
		}
		
		boolean created = false;
		
		public EntityLivingBase getEntity(World world){
			if(entity != null){
				return entity;
			}else if(created) {
				return null;
			}
			created = true;

			Entity e = EntityList.createEntityByID(entityId, world);

			if(e != null){
				if(e instanceof EntityLivingBase){
					return (entity = ((EntityLivingBase)e));
				}
			}
			return null;
		}
		
	}
	
}
