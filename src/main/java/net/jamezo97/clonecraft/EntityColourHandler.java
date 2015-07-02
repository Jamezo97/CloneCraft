package net.jamezo97.clonecraft;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;

public class EntityColourHandler {

	static IntHashMap idToColourP = new IntHashMap();
	static IntHashMap idToColourS = new IntHashMap();
	
	//Stores two short values in each int, assigned to an entity id.
	//Each short represents how many 
	static IntHashMap idTried = new IntHashMap();

	public static int getPrimaryColourForEntityId(int id)
	{
		if(idToColourP.containsItem(id))
		{
			return (Integer)idToColourP.lookup(id);
		}
		tryGetColours(id);
		
		if(idToColourP.containsItem(id))
		{
			return (Integer)idToColourP.lookup(id);
		}
		else
		{
			return 0xffff00ff;
		}
	}
	
	public static int getSecondaryColourForEntityId(int id)
	{
		if(idToColourS.containsItem(id))
		{
			return (Integer)idToColourS.lookup(id);
		}
		
		tryGetColours(id);
		
		if(idToColourS.containsItem(id))
		{
			return (Integer)idToColourS.lookup(id);
		}
		else
		{
			return 0xffdd00dd;
		}
	}
	
	private static void tryGetColours(int id)
	{
		if(!idTried.containsItem(id))
		{
			//TODO Change this to stop it from skipping eggs, change it back to 7
			idTried.addKey(id, 7);
		}
		
		int tried = (Integer)idTried.lookup(id);
		
		if(!triedCustom(tried))
		{
			if(custom_idToColourP.containsItem(id))
			{
				idToColourP.addKey(id, custom_idToColourP.lookup(id));
				idToColourS.addKey(id, custom_idToColourS.lookup(id));
				idTried.removeObject(id);
				return;
			}
			else
			{
				tried = removeCustom(tried);
			}
		}
		
		if(!triedEgg(tried))
		{
			if(CCEntityList.idToColour.containsKey(id))
			{
				int[] colours = CCEntityList.idToColour.get(id);
				
				if(colours != null)
				{
					idToColourP.addKey(id, colours[0]);
					idToColourS.addKey(id, colours[1]);
					return;
				}
				else
				{
					tried = removeEgg(tried);
				}
			}
			else
			{
				tried = removeEgg(tried);
			}
		}
		
		if(!triedTexture(tried) && Minecraft.getMinecraft().theWorld != null)
		{
			Class c = CCEntityList.idToClass.get(id);
			
			if(c != null)
			{
				Render ren = RenderManager.instance.getEntityClassRenderObject(c);
				
				if(ren != null)
				{
					Entity e = CCEntityList.createEntityByID(id, Minecraft.getMinecraft().theWorld);
					
					if(e != null)
					{
						ResourceLocation rl = Reflect.executeMethod(Reflect.Render_getEntityTexture, ren, ResourceLocation.class, e);
						
						if(rl != null)
						{
							TextureManager manager = Minecraft.getMinecraft().renderEngine;
							
							if(manager != null)
							{
								IResourceManager par1ResourceManager = Reflect.getFieldValueAndCast(Reflect.TextureManager_theResourceManager, manager, IResourceManager.class);
								
								try
								{
									IResource iresource = par1ResourceManager.getResource(rl);
						            InputStream inputstream = iresource.getInputStream();
						            BufferedImage bufferedimage = ImageIO.read(inputstream);
						            
						            int[] rgb = bufferedimage.getRGB(0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), new int[bufferedimage.getWidth()*bufferedimage.getHeight()], 0, bufferedimage.getWidth());
						            long red = 0, green = 0, blue = 0;
						            int count = 0;
						            int a, r, g, b;
						            
						            int[] rc = new int[256];
						            int[] gc = new int[256];
						            int[] bc = new int[256];
						            
						            for(int i = 0; i < rgb.length; i++)
						            {
						            	a = (rgb[i]>>24) & 0xff;
						            	
						            	if(a == 255)
						            	{
						            		r = (rgb[i]>>16) & 0xff;
						            		g = (rgb[i]>>8) & 0xff;
						            		b = (rgb[i]) & 0xff;
						            		
						            		rc[r]++;
						            		gc[g]++;
						            		bc[b]++;
						            		
						            		red += r;
						            		green += g;
						            		blue += b;
						            		count++;
						            	}
						            }
						            
						            int ir = (int) (red / ((float)count));
						            int ig = (int) (green / ((float)count));
						            int ib = (int) (blue / ((float)count));
						            int colourA = (ir << 16) | (ig << 8) | ib;
						            
						            int highest = 0;
						            int total = 0;
						            
						            for(int j = 0; j < 3; j++)
						            {
						            	int[] counts = null;
						            	
						            	switch(j){
							            case 0: counts = rc; break;
							            case 1: counts = gc; break;
							            case 2: counts = bc; break;
							            }
						            	
						            	for(int i = 0; i < 255; i++)
						            	{
							            	if(counts[i] > highest)
							            	{
							            		highest = counts[i];
							            	}
							            }
						            	
							            count = 0;
							            total = 0;
							            
							            for(int i = 0; i < 255; i++)
							            {
							            	if(counts[i] == highest)
							            	{
							            		count += i;
							            		total++;
							            	}
							            }
							            
							            count = (int)(count / ((float)total));
							            
							            switch(j){
							            case 0: ir = count; break;
							            case 1: ig = count; break;
							            case 2: ib = count; break;
							            }
						            }
						            
						            int colourB = (ir << 16) | (ig << 8) | ib;
						            
						            idToColourP.addKey(id, colourA);
									idToColourS.addKey(id, colourB);
									
									return;
								}
								catch(Exception es)
								{
									es.printStackTrace();
									tried = removeTexture(tried);
								}
							}
						}
						else
						{
							tried = removeTexture(tried);
						}
					}
					else
					{
						tried = removeTexture(tried);
					}
				}
				else
				{
					tried = removeTexture(tried);
				}
			}
			else
			{
				tried = removeTexture(tried);
			}
		}
		
		tried = incrementAttempt(tried);
		
		if(getAttempt(tried) > 3 || (tried & 7) == 0)
		{
			idToColourP.addKey(id, 0xffff00ff);
			idToColourS.addKey(id, 0xffff00ff);
			idTried.removeObject(id);
		}
	}
	
	private static int incrementAttempt(int tried)
	{
		return (tried & 31) | (((tried >> 5)+1) << 5);
	}

	private static int setAttempt(int tried, int attempt)
	{
		//Trim to first 5 bits, then replace 5 bits with attempt number
		return (tried & 31) | (attempt << 5);
	}
	
	private static int getAttempt(int tried)
	{
		return tried >> 5;
	}
	
	private static int removeCustom(int tried){return tried & (~1);}
	private static int removeEgg(int tried){return tried & (~2);}
	private static int removeTexture(int tried){return tried & (~4);}

	private static boolean triedCustom(int tried){return (tried & 1)==0;}
	private static boolean triedEgg(int tried){return (tried & 2)==0;}
	private static boolean triedTexture(int tried){return (tried & 4)==0;}

	
	static IntHashMap custom_idToColourP = new IntHashMap();
	static IntHashMap custom_idToColourS = new IntHashMap();
	
	static
	{
		//Human
		addCustomColour(CCEntityList.classToId.get(EntityClone.class), 0xffd72a2a, 0xfffad4ad);
		//TNT
		addCustomColour(CCEntityList.classToId.get(EntityTNTPrimed.class), 0xffdc1e1e, 0xffededed);
		//EnderCrystal
		addCustomColour("EnderCrystal", 0xff666666, 0xff9d14a9);
	}
	
	
	
	private static void addCustomColour(String id, int primary)
	{
		addCustomColour(id, primary, primary);
	}
	
	private static void addCustomColour(int id, int primary)
	{
		addCustomColour(id, primary, primary);
	}
	
	private static void addCustomColour(String id, int primary, int secondary)
	{
		addCustomColour(CloneCraftHelper.getEntityIdFromString(id), primary, secondary);
	}
	
	private static void addCustomColour(int id, int primary, int secondary)
	{
		if(id != -1)
		{
			custom_idToColourP.addKey(id, primary);
			custom_idToColourS.addKey(id, secondary);
		}
	}
	
}