package net.jamezo97.clonecraft;

import java.util.HashMap;
import java.util.Map.Entry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.world.World;

public class CCEntityList {

	public static HashMap<Integer, String> idToString = new HashMap<Integer, String>();
	public static HashMap<String, Integer> stringToId = new HashMap<String, Integer>();

	public static HashMap<Integer, Class> idToClass = new HashMap<Integer, Class>();
	public static HashMap<Class, Integer> classToId = new HashMap<Class, Integer>();
	
	public static HashMap<String, Class> stringToClass = new HashMap<String, Class>();
	public static HashMap<Class, String> classToString = new HashMap<Class, String>();
	
	public static HashMap<Integer, int[]> idToColour = new HashMap<Integer, int[]>();
	public static HashMap<Class, int[]> classToColour = new HashMap<Class, int[]>();
	public static HashMap<String, int[]> stringToColour = new HashMap<String, int[]>();
	
	public static HashMap<Integer, Integer> myIdToMcId = new HashMap<Integer, Integer>();
	public static HashMap<Integer, Integer> mcIdToMyId = new HashMap<Integer, Integer>();
	
	CloneCraft cloneCraft;
	
	public static void initEntities()
	{
		
		HashMap<Class, Integer> mcClassToId = new HashMap<Class, Integer>();
		
		for(Object entryObj : EntityList.IDtoClassMapping.entrySet())
		{
			if(entryObj instanceof Entry)
			{
				Entry entry = (Entry)entryObj;
				Object id_obj = entry.getKey();
				Object class_obj = entry.getValue();
				
				if(class_obj instanceof Class && id_obj instanceof Integer)
				{
					Integer id = (Integer)id_obj;
					Class theClass = (Class)class_obj;
					
					mcClassToId.put(theClass, id);
				}
			}
		}
		
		
		for(Object entryObj : EntityList.classToStringMapping.entrySet())
		{
			if(entryObj instanceof Entry)
			{
				Entry entry = (Entry)entryObj;
				Object class_obj = entry.getKey();
				Object name_obj = entry.getValue();
				
				if(class_obj instanceof Class && name_obj instanceof String)
				{
					Class entityClass = (Class) class_obj;
					String name = (String)name_obj;
					Integer ID = name.hashCode();
					
					System.out.println(name);
					
					idToString.put(ID, name);
					stringToId.put(name, ID);
					
					idToClass.put(ID, entityClass);
					classToId.put(entityClass, ID);
					
					stringToClass.put(name, entityClass);
					classToString.put(entityClass, name);
					
					Integer mcID = mcClassToId.get(entityClass);
					
					if(mcID != null)
					{
						myIdToMcId.put(ID, mcID);
						mcIdToMyId.put(mcID, ID);
					}
					
					Object info_obj = EntityList.entityEggs.get(mcID);
					
					if(info_obj != null && info_obj instanceof EntityList.EntityEggInfo)
					{
						EntityList.EntityEggInfo info = (EntityList.EntityEggInfo)info_obj;
						int[] colours = new int[]{info.primaryColor, info.secondaryColor};
						idToColour.put(ID, colours);
						classToColour.put(entityClass, colours);
						stringToColour.put(name, colours);
					}
				}
			}
		}
	}

	public static int getEntityID(Entity e)
	{
		if(e == null)
		{
			return -1;
		}
		
		Class clazz = e.getClass();
		
		return CCEntityList.classToId.get(clazz);
	}

	public static Entity createEntityByID(int id, World world)
	{
		Class clazz = CCEntityList.idToClass.get(id);
		
		if(clazz != null)
		{
			try
			{
				Entity entity = (Entity)clazz.getConstructor(World.class).newInstance(world);
				return entity;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	public static int mcToMyId(int entityId) 
	{
		Integer integer = CCEntityList.mcIdToMyId.get(entityId);
		
		if(integer != null)
		{
			return integer;
		}
		return -1;
	}

}
