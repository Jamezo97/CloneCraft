package net.jamezo97.clonecraft.item;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.EntityColourHandler;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.entity.EntitySpawnEgg;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemData {
	
	boolean isDirty = false;
	
	int id = -1;
	
	public ItemData(NBTTagCompound nbt)
	{
		if(nbt != null)
		{
			if(nbt.hasKey("itemData.dirty"))
			{
				this.isDirty = nbt.getBoolean("itemData.dirty");
			}
			if(nbt.hasKey("itemData.id"))
			{
				this.id = nbt.getInteger("itemData.id");
			}
			
		}	
	}
	
	public ItemData(){
		
	}
	
	public ItemData(ItemStack stack)
	{
		this(stack.getTagCompound());
	}
	
	public ItemStack save(ItemStack stack)
	{
		if(stack.getTagCompound() == null)
		{
			stack.setTagCompound(save());
		}
		else
		{
			save(stack.getTagCompound());
		}
		
		return stack;
	}
	
	public NBTTagCompound save()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		save(nbt);
		return nbt;
	}
	
	public NBTTagCompound save(NBTTagCompound nbt)
	{
		nbt.setBoolean("itemData.dirty", isDirty);
		nbt.setInteger("itemData.id", id);
		return nbt;
	}
	
	public boolean isDirty()
	{
		return isDirty;
	}
	
	public int getId()
	{
		return id;
	}
	
	
	public ItemData clean()
	{
		isDirty = false;
		return this;
	}
	
	public ItemData empty()
	{
		id = -1;
		return this;
	}
	
	public ItemData fill(Entity e)
	{
		if(e instanceof EntityPlayer)
		{
			this.id = CCEntityList.classToId.get(EntityClone.class);
			this.isDirty = true;
			return this;
		}
		return fill(CCEntityList.getEntityID(e));
	}
	
	public ItemData fill(int entityId)
	{
		if(CCEntityList.idToClass.containsKey(entityId))
		{
			this.id = entityId;
			this.isDirty = true;
		}
		
		return this;
	}
	
	/*public static int getColour(int entityId) 
	{
    	int[] colours = CCEntityList.idToColour.get(entityId);
    	
    	if(colours != null)
    	{
    		return colours[0];
    	}
    	else if(entityId == 0)
    	{
    		//Human
    		return 0xffd72a2a;
    	}
    	else if(entityId == -1)
    	{
    		//Unknown, broken probs
    		return 0xff000000;
    	}
    	else
    	{
    		//Yep, broken. Make it fluro pink yo
    		return 0xffff00ff;
    	}
	}

	public int getColour()
	{
    	return getColour(this.id);
	}*/
	
	public boolean isFull()
	{
		return id != -1;
	}
	
	public Entity spawn(MovingObjectPosition mop, EntitySpawnEgg egg)
	{
		if(isFull())
		{
			double x = mop.hitVec.xCoord, y = mop.hitVec.yCoord, z = mop.hitVec.zCoord;
			if(mop.entityHit != null){
				//				x += .01f;
				//				z += .01f;
			}else{
				switch(mop.sideHit){
				case 0: y-=2.01; break;
				case 2: z-=.501; break;
				case 3: z+=.501; break;
				case 4: x-=.501; break;
				case 5: x+=.501; break;
				}
			}

			//TODO That may have been important.
			
			
			
			return spawnEntity(x, y/*+0.001*/, z, egg.worldObj);
		}
		return null;
	}
	
	public Entity spawnEntity(double posX, double posY, double posZ, World world)
	{
		Entity toSpawn = null;
		try
		{
/*			if(id == 0)
			{
				toSpawn = new EntityClone(world);
			}
			else*/
			{
				toSpawn = CCEntityList.createEntityByID(id, world);
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		if(toSpawn != null)
		{
			toSpawn.setPosition(posX, posY, posZ);

			if(toSpawn instanceof EntityLiving)
			{
				((EntityLiving)toSpawn).onSpawnWithEgg(null);
			}
			
			world.spawnEntityInWorld(toSpawn);
			
			return toSpawn;
		}
		return toSpawn;
	}

	public String getCurrentEntityNameTrans()
	{
		if(id == 0)
		{
			return StatCollector.translateToLocal("cc.human");
		}
		else
		{
			String name = CCEntityList.idToString.get(id);
			
			if(name != null)
			{
				return StatCollector.translateToLocal("entity." + name + ".name");
			}
			else
			{
				return null;
			}
		}
	}

	/*public int getColour(ItemStack par1ItemStack) 
	{
		if(par1ItemStack.getItemDamage() == 0)
		{
			return 0xffffffff;
		}
		return getColour();
	}*/

	public int getPrimaryColour()
	{
		if(id == 0)
		{
			return 0xffd72a2a;
		}
		else
		{
			return EntityColourHandler.getPrimaryColourForEntityId(id);
		}
	}
	
	public int getSecondaryColour()
	{
		if(id == 0)
		{
			return 0xfff74a4a;
		}
		else
		{
			return EntityColourHandler.getSecondaryColourForEntityId(id);
		}
	}

	public void fillWith(ItemData nData)
	{
		this.fill(nData.id);
	}

	public ItemData setDirty()
	{
		this.isDirty = true;
		return this;
	}
	


}
