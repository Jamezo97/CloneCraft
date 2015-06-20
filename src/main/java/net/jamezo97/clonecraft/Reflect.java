package net.jamezo97.clonecraft;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Reflect {

	
	public static Map EntityList_classToIDMapping = null;

	public static Method Render_getEntityTexture;
	
	public static Field TextureManager_theResourceManager, Minecraft_isGamePaused, EffectRenderer_fxLayers, EntityAINearestAttackableTarget_targetClass;
	
	public static void init(Side side) {
		if(side == Side.CLIENT){
			loadClient();
		}else{
			loadCommon();
		}
		
		
	}
	static boolean clientLoaded = false;
	@SideOnly(value = Side.CLIENT)
	public static void loadClient(){
		if(!clientLoaded){
			clientLoaded = true;
			Render_getEntityTexture = getMethodViaReturnType(Render.class, ResourceLocation.class, 0);
			TextureManager_theResourceManager = getFieldByType(TextureManager.class, IResourceManager.class, 0);
			Minecraft_isGamePaused = getFieldTypeAfterFieldType(boolean.class, 0, Session.class, 0, Minecraft.class, "isGamePaused");
			EffectRenderer_fxLayers = getFieldByType(EffectRenderer.class, List[].class, 0);
		}
	}
	static boolean commonLoaded = false;
	public static void loadCommon(){
		if(!commonLoaded){
			commonLoaded = true;
			EntityAINearestAttackableTarget_targetClass = getFieldByType(EntityAINearestAttackableTarget.class, Class.class, 0);
			try {
				EntityList_classToIDMapping = (Map)getFieldByMapValueTypes(EntityList.class, null, Class.class, Integer.class).get(null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
//			System.out.println("I GOT THIS:" + EntityList_classToIDMapping);
		}
	}
	
	public static Object getFieldValue(Field f, Object instance){
		try {
			return f.get(instance);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T extends Object> T getFieldValueAndCast(Field f, Object instance, Class<T> type){
		try {
			return (T) f.get(instance);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T extends Object> T executeMethod(Method m, Object instance, Class<T> returnType, Object... params){
		try {
			return (T) (m.invoke(instance, params));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static Field getFieldTypeAfterFieldType(Class<?> type1, int type1Index, Class<?> type2, int type2Index, Class<?> containingClass, String nameForError) {
		Field[] fields = containingClass.getDeclaredFields();
		for(int a = 0; a < fields.length; a++){
			Field field = fields[a];
			if(type2Index > -1){
				if(field.getType() == type2){
					type2Index--;
					continue;
				}
			}else{
				if(field.getType() == type1){
					type1Index--;
					if(type1Index < 0){
						field.setAccessible(true);
						return field;
					}
				}
			}
		}
		return null;
	}
	
	public static Field getFieldByType(Class from, Class type, int index){
		Field[] fields = from.getDeclaredFields();
		for(int a = 0; a < fields.length; a++){
			Field f = fields[a];
			if(f.getType() == type){
				if(index == 0){
					f.setAccessible(true);
					return f;
				}
				index--;
			}
		}
		return null;
	}
	
	public static Field getFieldByMapValueTypes(Class from, Object instance, Class keyType, Class valueType){
		Field[] fields = from.getDeclaredFields();
		for(int a = 0; a < fields.length; a++)
		{
			Field f = fields[a];
			if(Map.class.isAssignableFrom(f.getType()))
			{
				f.setAccessible(true);
				try {
					Map map = (Map)f.get(instance);
					Object entry = map.entrySet().iterator().next();
					if(entry instanceof Entry)
					{
						Entry theEntry = (Entry)entry;
						if(theEntry.getKey() != null && theEntry.getValue() != null)
						{
							if(keyType.isAssignableFrom(theEntry.getKey().getClass()) &&
									valueType.isAssignableFrom(theEntry.getValue().getClass()))
							{
								return f;
							}
						}
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	public static Method getMethodViaReturnType(Class from, Class returnType, int index){
		Method[] methods = from.getDeclaredMethods();
		for(int a = 0; a < methods.length; a++){
			if(returnType == methods[a].getReturnType()){
				if(index == 0){
					methods[a].setAccessible(true);
					return methods[a];
				}
				index--;
			}
		}
		return null;
	}

}
