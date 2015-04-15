package net.jamezo97.clonecraft;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class CloneCraftHelper {
	
	
	//ItemStack stack, contains remaining items. (stackSize == 0  if no items remaining, i.e. all was placed in inventory)
	public static void addToInventory(IInventory inven, int aBegin, int aEnd, ItemStack stack){
		System.out.println("ADD TO INVEN");
		ItemStack slot = null;
		int emptyIndex = -1;
		int jic = 40;
		while(stack.stackSize > 0 && jic-- > 0){
			slot = null;
			emptyIndex = -1;
			for(int a = aBegin; a < aEnd; a++){
				slot = inven.getStackInSlot(a);
				if(slot != null){
					System.out.println(slot.getItem());
				}else{
					System.out.println("null");
				}
				if(emptyIndex == -1 && slot == null){
					emptyIndex = a;
				}
		        if (slot != null && slot.getItem() == stack.getItem() && slot.isStackable() && slot.stackSize < slot.getMaxStackSize() && slot.stackSize < inven.getInventoryStackLimit() && (!slot.getHasSubtypes() || slot.getItemDamage() == stack.getItemDamage()) && ItemStack.areItemStackTagsEqual(slot, stack)){
		        	break;
		        }
		        if(a+1 == aEnd){
		        	slot = null;
		        }
			}
			
			if(slot == null){
				if(emptyIndex == -1){
					break;
				}else{
					inven.setInventorySlotContents(emptyIndex, stack);
				}
			}else if(slot != null){
				int toRemove = slot.getMaxStackSize() - slot.stackSize;
				if(toRemove > inven.getInventoryStackLimit()-slot.stackSize){
					toRemove = inven.getInventoryStackLimit()-slot.stackSize;
				}else if(toRemove > stack.stackSize){
					toRemove = stack.stackSize;
				}
				stack.stackSize -= toRemove;
				slot.stackSize += toRemove;
			}
		}
		//Drop on ground
		
	}

	public static void dropAtEntity(EntityLivingBase entity, ItemStack stack) {
		EntityItem item = new EntityItem(entity.worldObj);
		item.delayBeforeCanPickup = 20;
		item.setEntityItemStack(stack);
		item.setPosition(entity.posX, entity.posY + entity.getEyeHeight()==0?entity.height/2:entity.getEyeHeight(), entity.posZ);
		Random rand = new Random();
		
		item.addVelocity(rand.nextFloat()*0.3f, rand.nextFloat()*0.2f, rand.nextFloat()*0.3f);
		
		entity.worldObj.spawnEntityInWorld(item);
	}
	
	public static char getClosestColourChar(int colour){
		int[] colours = new int[]{0x0000aa, 0x00aa00, 0x00aaaa, 0xaa0000, 0xaa00aa, 0xffaa00, 0x2a2a2a, 0x555555, 0x5555ff, 0x55ff55, 0x55ffff, 0xff5555, 0xff55ff, 0xffff55, 0xffffff};
		char[] colourCodes = "123456789abcdef".toCharArray();
		int r1 = (colour >> 16) & 0xff;
		int g1 = (colour >> 8) & 0xff;
		int b1 = colour & 0xff;

		int closestIndex = -1;
		int diff = -1;
		for(int a = 0; a < colours.length; a++){
			int r2 = (colours[a] >> 16) & 0xff;
			int g2 = (colours[a] >> 8) & 0xff;
			int b2 = colours[a] & 0xff;

			int diffR = r1>r2?r1-r2:r2-r1;
			int diffG = g1>g2?g1-g2:g2-g1;
			int diffB = b1>b2?b1-b2:b2-b1;

			int totalDiff = diffR + diffG + diffB;
			if(closestIndex < 0 || totalDiff < diff){
				diff = totalDiff;
				closestIndex = a;
			}
		}
		if(closestIndex > -1){
			return colourCodes[closestIndex];
		}
		return ' ';
	}
	
	public static boolean isInvalid(Map<Class, String> classToString, Class entityClass){
		String name = classToString.get(entityClass);
		if(name != null){
			String[] names = new String[]{"Mob", "Monster"};
			for(int a = 0; a < names.length; a++){
				if(names[a].equals(name)){
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isValid(Map<Class, String> classToString, Class entityClass) {
		String name = classToString.get(entityClass);
		if(name != null){
			String[] names = new String[]{"EnderCrystal", "PrimedTnt"};
			for(int a = 0; a < names.length; a++){
				if(names[a].equals(name)){
					return true;
				}
			}
		}
		return false;
	}
	
	static HashMap<String, Integer> nameToId = new HashMap<String, Integer>();
	
	public static int getEntityIdFromString(String s){
		if(nameToId.containsKey(s)){
			return nameToId.get(s);
		}
		Class c = (Class)EntityList.stringToClassMapping.get(s);
		if(c != null){
			Iterator<Entry<Integer, Class>> it = EntityList.IDtoClassMapping.entrySet().iterator();
			for(;it.hasNext();){
				Entry<Integer, Class> next = it.next();
				if(next.getValue() == c){
					nameToId.put(s, next.getKey());
					return next.getKey();
				}
			}
		}
		
		return -1;
	}

	public static float remainder(float divideMe, float byMe){
		return (float) (divideMe - (Math.floor(divideMe/byMe)*byMe));
	}
	
	public static int remainder(int divideMe, int byMe){
		return (int) (divideMe - (Math.floor(divideMe/byMe)*byMe));
	}

	public static double interpolate(double prev, double now, float partial) {
		return prev + partial*(now-prev);
	}
	
}
