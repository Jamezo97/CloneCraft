package net.jamezo97.clonecraft;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.RenderEntry;
import net.jamezo97.clonecraft.item.ItemWoodStaff;
import net.jamezo97.clonecraft.render.Renderable;
import net.jamezo97.clonecraft.render.RenderableManager;

import org.lwjgl.opengl.GL11;

public class CCPostRender {

	public static void postRender(float partial)
	{
		for(int a = 0; a < renderers.size(); a++)
		{
			GL11.glPushMatrix();
			renderers.get(a).renderer.render(partial);
			GL11.glPopMatrix();
		}
		
		renderSchematicSelectionOverlay(partial);
	}
	
	public static void onTick()
	{
		removeExpiredRenderables();
		
		for(int a = 0; a < renderers.size(); a++)
		{
			renderers.get(a).renderer.onTick();
		}
	}

	public static void removeExpiredRenderables() 
	{
		for(int a = 0; a < renderers.size(); a++)
		{
			if (!renderers.get(a).manager.canRenderContinue(renderers.get(a).renderer))
			{
				renderers.remove(a--).onRemoved();
			}
		}
	}

	static ArrayList<RenderEntry> renderers = new ArrayList<RenderEntry>();

	public static void addRenderable(RenderableManager manager, Renderable render)
	{
		renderers.add(new RenderEntry(manager, render));
	}

	public static boolean removeRenderable(RenderableManager manager) {
		for(int a = 0; a < renderers.size(); a++)
		{
			if(renderers.get(a).manager == manager)
			{
				renderers.remove(a);
				return true;
			}
		}
		return false;
	}

	private static RenderOverlay overlay, overlayc1, overlayc2;
	
	public static void renderSchematicSelectionOverlay(float partial)
	{
		ItemWoodStaff staff = CloneCraft.INSTANCE.itemWoodStaff;
		
		if(staff.pos1 != null && staff.pos2 != null)
		{
			int minX = Math.min(staff.pos1.posX, staff.pos2.posX);
			int minY = Math.min(staff.pos1.posY, staff.pos2.posY);
			int minZ = Math.min(staff.pos1.posZ, staff.pos2.posZ);
			
			int maxX = Math.max(staff.pos1.posX, staff.pos2.posX)+1;
			int maxY = Math.max(staff.pos1.posY, staff.pos2.posY)+1;
			int maxZ = Math.max(staff.pos1.posZ, staff.pos2.posZ)+1;
			
			if(overlay == null)
			{
				overlay = new RenderOverlay(new Colour(0xff2299ee));
			}
			
			if(overlayc1 == null)
			{
				overlayc1 = new RenderOverlay(new Colour(0xffee3333));
			}
			
			if(overlayc2 == null)
			{
				overlayc2 = new RenderOverlay(new Colour(0xff33ee33));
			}
			
			overlay.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
			overlayc1.setBounds(staff.pos1.posX-0.01, staff.pos1.posY-0.01, staff.pos1.posZ-0.01, staff.pos1.posX+1.01, staff.pos1.posY+1.01, staff.pos1.posZ+1.01);
			overlayc2.setBounds(staff.pos2.posX-0.02, staff.pos2.posY-0.02, staff.pos2.posZ-0.02, staff.pos2.posX+1.02, staff.pos2.posY+1.02, staff.pos2.posZ+1.02);
			
//			GL11.glDisable(GL11.GL_CULL_FACE);
			
			overlayc1.render(partial);
			overlayc2.render(partial);
//			overlay.cornerColours = staff.cornerColours;
			overlay.render(partial);
//			GL11.glEnable(GL11.GL_CULL_FACE);
			
		}
		
	}

}
