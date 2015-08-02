package net.jamezo97.clonecraft.entity;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntitySpawnEgg extends EntityThrowable
{

	public ItemStack stack;

	String spawnedBy;

	public EntitySpawnEgg(World par1World, EntityLivingBase par2EntityLivingBase, ItemStack stack, String username)
	{
		super(par1World, par2EntityLivingBase);
		this.stack = stack.copy();
		this.dataWatcher.updateObject(3, stack);
		spawnedBy = username;
	}

	public EntitySpawnEgg(World par1World, double x, double y, double z, ItemStack stack)
	{
		super(par1World, x, y, z);
		this.stack = stack.copy();
		this.dataWatcher.updateObject(3, stack);
		spawnedBy = "";
	}

	public EntitySpawnEgg(World par1World)
	{
		super(par1World);

	}

	@Override
	protected void entityInit()
	{
		super.entityInit();
		this.dataWatcher.addObject(3, new ItemStack(Blocks.stone));
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		if (stack != null)
		{
			NBTTagCompound nbtStack = new NBTTagCompound();
			stack.writeToNBT(nbtStack);
			nbt.setTag("ItemStack", nbtStack);
		}
		nbt.setString("Spawner", spawnedBy);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		NBTTagCompound nbtStack = nbt.getCompoundTag("ItemStack");
		if (!nbtStack.hasNoTags())
		{
			stack = ItemStack.loadItemStackFromNBT(nbtStack);
			if (stack != null)
			{
				this.dataWatcher.updateObject(3, stack);
			}
		}
		this.spawnedBy = nbt.getString("Spawner");
	}

	@Override
	protected void onImpact(MovingObjectPosition mop)
	{
		if (stack != null)
		{
			if (!worldObj.isRemote)
			{
				float addY = 0;

				if (EntityTNTPrimed.class == CCEntityList.idToClass.get(new ItemData(stack).getId()))
				{
					mop.hitVec.yCoord += 0.5;
				}
				Entity entity = new ItemData(stack).spawn(mop, this);

				if (entity != null)
				{
					entity.worldObj.playSoundAtEntity(entity, "clonecraft:general.pop", 1.0f, 0.9f + (worldObj.rand.nextFloat() / 5));
					if (entity instanceof EntityLivingBase && stack.hasDisplayName())
					{
						((EntityLiving) entity).setCustomNameTag(stack.getDisplayName());
					}
					if (entity instanceof EntityClone)
					{
						((EntityClone) entity).onSpawnedBy(spawnedBy);
					}
				}
			}
			else
			{
				worldObj.playSound(posX, posY, posZ, "clonecraft.pop", 1.0f, 1.0f, false);
			}
			worldObj.removeEntity(this);
			this.setDead();
		}
	}

	/*
	 * public void updateData() { new Handler2UpdateEntitySpawnEgg(stack,
	 * entityId).sendToAllWatching(this); }
	 */

}
