package net.jamezo97.clonecraft.tileentity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

/**
 * Used for storing information about TileEntities, such as whether they contain items. 
 * Also helps remove items from these TileEntities
 * 
 * 
 * <br>
 * Well that failed pretty well. My sterilizer doesn't contains an array of ItemStacks.  So it can't be cleared. Whatever. I tried.
 * @author James
 *
 */
public class CCTileEntityRegistry {
	
	/**
	 * A value used to represent the fact that a class is assignable from {@link net.minecraft.inventory.IInventory}
	 */
	public static final Object INVENTORY = new Object();
	
	/**
	 * Contains the mapping between TileEntity classes and either:
	 * <ul><li>The fields inside said class which represent ItemStacks. (a Field[] array)</li>
	 * <li>The {@link CCTileEntityRegistry#INVENTORY} value</li></ul>
	 * 
	 */
	private static HashMap<Class<? extends TileEntity>, Object> classToItemstackFields = new HashMap<Class<? extends TileEntity>, Object>();
	
	/**
	 * Checks if the given TileEntity object, contains any ItemStacks, regardless of their public, private or protected visibility
	 * @param tileEntity
	 * @return True if the tile entity contains ItemStack fields.
	 */
	public static boolean containsItems(TileEntity tileEntity)
	{
		if(tileEntity == null)
		{
			return false;
		}
		
		return containsItems(tileEntity.getClass());
	}
	
	public static boolean containsItems(Class<? extends TileEntity> clazz)
	{
		if(clazz == null)
		{
			return false;
		}
		
		ensureLoaded(clazz);
		
		return classToItemstackFields.get(clazz) != null;
	}
	
	public static boolean clearItemstacks(TileEntity te)
	{
		if(te != null)
		{
			ensureLoaded(te.getClass());
			
			Object value = classToItemstackFields.get(te.getClass());
			
			if(value != null)
			{
				if(value == INVENTORY)
				{
					IInventory inven = (IInventory)te;
					try
					{
						for(int a = 0; a < inven.getSizeInventory(); a++)
						{
							inven.setInventorySlotContents(a, null);
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					return true;
				}
				else if(value instanceof Field[])
				{
					Field[] fields = (Field[])value;
					for(int a = 0; a < fields.length; a++)
					{
						if(fields[a].getType() == ItemStack.class)
						{
							try
							{
								fields[a].set(te, null);
							}
							catch (Throwable t)
							{
								t.printStackTrace();
							}
						}
						else if(fields[a].getType().isArray())
						{
							try
							{
								Object o = fields[a].get(te);
								
								if(o != null && o instanceof ItemStack[])
								{
									ItemStack[] stacks = (ItemStack[])o;
									for(int b = 0; b < stacks.length; b++)
									{
										stacks[a] = null;
									}
									//Just in case if values through reflection are mutable.. or something.
									fields[a].set(te, stacks);
								}
								return true;
							}
							catch (Throwable t)
							{
								t.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		return false;
	}

	private static void ensureLoaded(Class<? extends TileEntity> clazz)
	{
		if(!classToItemstackFields.containsKey(clazz) || clazz == TileEntitySterilizer.class)
		{
			classToItemstackFields.put(clazz, null);
			
			if(IInventory.class.isAssignableFrom(clazz))
			{
				classToItemstackFields.put(clazz, INVENTORY);
			}
			else
			{
				//It's not an instance of IInventory. But it may still contains ItemStacks, it's just not being done properly. And we can't have people cheating because
				//of other peoples bad coding! Stand back! Time to do some science!.. Actually, some java reflection magick
				ArrayList<Field> allStackFields = new ArrayList<Field>();
				
				Class<? extends TileEntity> currentClass = clazz;
				
				int max = 10;
				
				while(currentClass != null && max-- > 0)
				{
					Field[] fields = currentClass.getDeclaredFields();
					
					for(int a = 0; a < fields.length; a++)
					{
						Field field = fields[a];
						
//						System.out.priint
						
						if(java.lang.reflect.Modifier.isStatic(field.getModifiers()))
						{
							//Don't bother with static fields.
							continue;
						}
						
						Class type = field.getType();
						
						if(type == ItemStack.class)
						{
							allStackFields.add(field);
							field.setAccessible(true);
//							System.out.println("Found ItemStack field");
						}
						else if(type.isArray() && type.getComponentType() == ItemStack.class)
						{
							allStackFields.add(field);
							field.setAccessible(true);
//							System.out.println("Found ItemStack[] field");
						}
					}
					
					Class newClass = currentClass.getSuperclass();
					
					if(newClass != null && TileEntity.class.isAssignableFrom(newClass))
					{
						currentClass = newClass;
					}
					else
					{
						break;
					}
				}
				
				if(allStackFields.size() != 0)
				{
//					System.out.println("Loaded " + allStackFields.size() + " ItemStack fields");
					classToItemstackFields.put(clazz, allStackFields.toArray(new Field[allStackFields.size()]));
				}
				else
				{
//					System.out.println("There are no ItemStack fields");
				}
			}
		}
	}

}
