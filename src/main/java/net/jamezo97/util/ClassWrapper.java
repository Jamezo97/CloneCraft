package net.jamezo97.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;


public class ClassWrapper
{
	
	StoredWrapperInfo wrapperInfo;
	
	public final Class<?> parentClass;
	
	private boolean ignoreNoObj = false;
	
	public ClassWrapper(Class<?> parentClass)
	{
		this.parentClass = parentClass;
		
		if(!classToWrapperInfo.containsKey(parentClass))
		{
			classToWrapperInfo.put(parentClass, new StoredWrapperInfo());
		}
		
		wrapperInfo = classToWrapperInfo.get(parentClass);
	}
	
	Object wrappedObj = null;
	
	public void setWrappedObject(Object wrappedObject)
	{
		this.wrappedObj = wrappedObject;
	}
	
	public void registerCustomIDtoParamTypes(int id, Class<?>... paramTypes)
	{
		wrapperInfo.registerCustomIDtoParamTypes(id, paramTypes);
	}
	
	/**
	 * Call this method when you wish to forward the method to the wrapped object.
	 * Ideally, try to use this as little as possible, as a large amount of reflection is used.
	 * @param id - The unique ID for the method.
	 * @param args - The arguments passed to the method. If these argument types are not unique to this method, then an error
	 * will be thrown whilst trying to find the method in the wrapped class. If this occurs, register the method's argument
	 * types manually using 'registerCustomIDtoParamTypes'. Pass in the method id, and the parameter class types to help the
	 * automated process along.
	 * @return
	 */
	public Object methodCalled(int id, Object... args)
	{
		if(wrappedObj != null)
		{
			if(args == null) args = new Object[0];
			
			if(!wrapperInfo.idToMethod.containsKey(id))
			{
				StackTraceElement[] element = Thread.currentThread().getStackTrace();
				
				if(element.length > 2)
				{
					StackTraceElement above = element[2];
					
					String methodName = above.getMethodName();
					
					
					if(wrapperInfo.idToCustomParams.containsKey(id))
					{
						Class<?>[] argsType = wrapperInfo.idToCustomParams.get(id);
						
						if(argsType.length != args.length)
						{
							throw new WrapperException("Custom argument types don't match input parameter length for method ID " + id + ".");
						}
						else
						{
							for(int a = 0; a < args.length; a++)
							{
								if(args[a] != null && !argsType[a].isAssignableFrom(args[a].getClass()))
								{
									throw new WrapperException("Custom argument types don't match input parameter types for method ID " + id 
											+ ": " + argsType[a] + " is not a super class of " + args[a].getClass() + ".");
								}
							}
						}
						
						Method theMethod = null;
						
						Class<?> current = parentClass;
						
						while(current != null)
						{
							try
							{
								Method method = current.getDeclaredMethod(methodName, argsType);
								theMethod = method;
								break;
							}
							catch (NoSuchMethodException e)
							{
								e.printStackTrace();
							}
							catch (SecurityException e)
							{
								e.printStackTrace();
							}
							
							current = current.getSuperclass();
						}
						
						if(theMethod != null)
						{
							wrapperInfo.idToMethod.put(id, theMethod);
						}
						else
						{
							throw new WrapperException("No such method '" + methodName + "' for id " + id + ". Custom argument type registration failed to match any known declared method.");
						}
					}
					else
					{
						ArrayList<Method> validMethods = new ArrayList<Method>();
						
						Class<?> current = parentClass;
						
						Class<?>[] paramTypes;
						
						while(current != null)
						{
							Method[] methods = current.getDeclaredMethods();
							for(int a = 0; a < methods.length; a++)
							{
								if(methods[a].getName().equals(methodName) && (paramTypes=methods[a].getParameterTypes()).length == args.length)
								{
									int b;
									for(b = 0; b < paramTypes.length; b++)
									{
										//If the current argument exists, and is not valid
										if(args[b] != null && !(convertToNonPrim(paramTypes[b]).isAssignableFrom(convertToNonPrim(args[b].getClass()))))
										{
											b = -1;
											break;
										}
									}
									
									if(b != -1)
									{
										if(!validMethods.contains(methods[a]))
										{
											//If we looped through the whole thing without failure, it must be a valid method type
											validMethods.add(methods[a]);
										}
									}
									
								}
							}
							
							current = current.getSuperclass();
						}
						
						if(validMethods.size() == 1)
						{
							wrapperInfo.idToMethod.put(id, validMethods.get(0));
						}
						else if(validMethods.size() > 1)
						{
							throw new WrapperException("Too many matching method types for '" + methodName + "' (ID: " + id + "). Consider registering custom param types in the ClassWrapper module.");
						}
						else
						{
							throw new WrapperException("No matching method types for '" + methodName + "' (ID: " + id + "). Consider registering custom param types in the ClassWrapper module.");
						}
					}
					
				}
			}
			
			if(wrapperInfo.idToMethod.containsKey(id))
			{
				try
				{
					return wrapperInfo.idToMethod.get(id).invoke(wrappedObj, args);
				}
				catch (IllegalAccessException e)
				{
					e.printStackTrace();
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
				}
				catch (InvocationTargetException e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				throw new WrapperException("Failed to find any method for id '" + id + "'. May cause instability. Aborting.");
			}
			
		}
		else if(!ignoreNoObj)
		{
			throw new WrapperException("Wrapped object does not exist for class wrapper!");
		}
		
		return null;
	}
	
	public static Class<?> convertToNonPrim(Class<?> c1)
	{
		if(primToWrapper.containsKey(c1))
		{
			return primToWrapper.get(c1);
		}
		return c1;
	}

	
	private static final HashMap<Class<?>, StoredWrapperInfo> classToWrapperInfo = new HashMap<Class<?>, StoredWrapperInfo>();
	
	private class StoredWrapperInfo
	{
		
		public HashMap<Integer, Method> idToMethod = new HashMap<Integer, Method>();
		
		public HashMap<Integer, Class<?>[]> idToCustomParams = new HashMap<Integer, Class<?>[]>();

		public void registerCustomIDtoParamTypes(int id, Class<?>[] paramTypes)
		{
			idToCustomParams.put(id, paramTypes);
		}

		
	}
	
	public static class WrapperException extends RuntimeException
	{

		/**
		 * 
		 */
		private static final long serialVersionUID = -8401525165368148197L;

		public WrapperException(String message)
		{
			super(message);
		}

		
		
	}
	
	private static HashMap<Class<?>, Class<?>> primToWrapper = new HashMap<Class<?>, Class<?>>();
	
	static
	{
		primToWrapper.put(boolean.class, Boolean.class);
		primToWrapper.put(byte.class, Byte.class);
		primToWrapper.put(short.class, Short.class);
		primToWrapper.put(int.class, Integer.class);
		primToWrapper.put(long.class, Long.class);
		primToWrapper.put(float.class, Float.class);
		primToWrapper.put(double.class, Double.class);
		primToWrapper.put(char.class, Character.class);
	}

	/**
	 * When set to true, this will not throw an error if the wrapped object is missing.
	 * @param b
	 */
	public void ignoreNullWrappedObj(boolean b)
	{
		this.ignoreNoObj = b;
	}

}
