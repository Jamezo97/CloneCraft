package net.jamezo97.clonecraft.clone;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.network.Handler8UpdateAttackEntities;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;

public class AttackableEntities {
	
	CloneOptions options;
	
	EntityClone clone;
	
	public boolean isDirty = false;
	
	ArrayList<Integer> selectedEntities = new ArrayList<Integer>();
	
	public AttackableEntities(CloneOptions options)
	{
		this.options = options;
		this.clone = options.clone;
	}
	
	public boolean isDirty()
	{
		return isDirty;
	}
	
	public AttackableEntities setDirty(boolean dirty)
	{
		this.isDirty = dirty;
		return this;
	}
	
	
	public NBTTagCompound save(NBTTagCompound nbt)
	{
		int[] data = toPrimitive(this.selectedEntities);
		nbt.setIntArray("AttEnt", data);
		return nbt;
	}
	
	public NBTTagCompound load(NBTTagCompound nbt)
	{
		if(nbt.hasKey("AttEnt"))
		{
			NBTBase tag = nbt.getTag("AttEnt");
			
			if(tag instanceof NBTTagIntArray)
			{

				int[] data = ((NBTTagIntArray)tag).func_150302_c();
				addPrimitive(this.selectedEntities, data);
				setDirty(false);
			}
			
		}
		
		return nbt;
	}
	
	public void toggleEntity(int entityId)
	{
		set(entityId, !canAttack(entityId));
	}
	
	public int set(int entityId, boolean selected)
	{
		if(!selected && selectedEntities.contains(entityId))
		{
			selectedEntities.remove((Object)entityId);
			setDirty(true);
			return -1;
		}
		
		if(!isValidEntity(entityId)){return 0;}
		
		if(selected && !selectedEntities.contains(entityId))
		{
			selectedEntities.add(entityId);
			setDirty(true);
			return 1;
		}
		return 0;
	}
	
	public boolean canAttack(int id)
	{
		return selectedEntities.contains(id);
	}
	

	public void tick(EntityClone clone)
	{
		if(clone.worldObj.isRemote)
		{
			if(isDirty())
			{
				this.setDirty(false);
				Handler8UpdateAttackEntities handler = new Handler8UpdateAttackEntities(clone.getEntityId(), this.toPrimitive(selectedEntities));
				handler.sendToServer();
			}
		}
		else
		{
			if(isDirty())
			{
				this.setDirty(false);
				Handler8UpdateAttackEntities handler = new Handler8UpdateAttackEntities(clone.getEntityId(), this.toPrimitive(selectedEntities));
				handler.sendToOwnersWatching(clone);
			}
		}
		
	}
	
	public void clear()
	{
		this.selectedEntities.clear();
		setDirty(true);
	}
	
	
	public void importInt(int[] data)
	{
		int prev = this.selectedEntities.size();
		addPrimitive(this.selectedEntities, data);
		
		if(this.selectedEntities.size() != prev)
		{
			this.setDirty(true);
		}
	}
	
	public int[] exportInt()
	{
		return this.toPrimitive(selectedEntities);
	}

	public ArrayList<Integer> getArray()
	{
		return selectedEntities;
	}
	
	public void selectAll()
	{
		this.clear();
		this.addPrimitive(selectedEntities, validEntitiesArray);
	}
	
	public void selectMobs()
	{
		for(int a = 0; a < validEntitiesArray.length; a++)
		{
			Class c = EntityList.getClassFromID(validEntitiesArray[a]);
			
			if(c != null && (IMob.class.isAssignableFrom(c)))
			{
				this.set(validEntitiesArray[a], true);
			}
		}
	}
	
	public void selectAnimals()
	{
		for(int a = 0; a < validEntitiesArray.length; a++)
		{
			Class c = EntityList.getClassFromID(validEntitiesArray[a]);
			
			if(c != null && (IAnimals.class.isAssignableFrom(c)) && (!IMob.class.isAssignableFrom(c)))
			{
				this.set(validEntitiesArray[a], true);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static ArrayList<Integer> validEntities = new ArrayList<Integer>();
	
	/**
	 * Array of MC entity Ids
	 */
	public static int[] validEntitiesArray;
	
	private static ArrayList<Class> invalids = new ArrayList<Class>();
	
	static{
		
		invalids.add(EntityMob.class);
		invalids.add(EntityLiving.class);
		
		load();
		validEntitiesArray = toPrimitive(validEntities);
	}
	
	private static void load(){
		HashMap<Integer, Class> idToClass = (HashMap<Integer, Class>) EntityList.IDtoClassMapping;
		for(Entry<Integer, Class> entry : idToClass.entrySet()){
			int entityId = entry.getKey();
			Class entityClass = entry.getValue();
			if(EntityLivingBase.class.isAssignableFrom(entityClass) && !invalids.contains(entityClass)){
				validEntities.add(entityId);
			}
		}
	}
	
	public static boolean isValidEntity(int entityId){
		return validEntities.contains(entityId);
	}
	
	public static int[] toPrimitive(ArrayList<Integer> list){
		int[] data = new int[list.size()];
		for(int a = 0; a < list.size(); a++){
			data[a] = list.get(a);
		}
		return data;
	}
	
	public static void addPrimitive(ArrayList<Integer> list, int[] data){
		for(int a = 0; a < data.length; a++){
			list.add(data[a]);
		}
	}

	public boolean isAttackable(EntityLivingBase entity) {
		return validEntities.contains(EntityList.getEntityID(entity));
	}






	


}
