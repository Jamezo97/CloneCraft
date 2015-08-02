package net.jamezo97.clonecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.entity.EntityBubbleFXAir;
import net.jamezo97.clonecraft.item.ItemData;
import net.minecraft.block.Block;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.CloneCraftWorld;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySterilizer extends TileEntity
{

	ItemStack currentItem = null;

	public int dirtiness = 0;

	@Override
	public void readFromNBT(NBTTagCompound nbt)
	{
		super.readFromNBT(nbt);
		NBTTagList list = nbt.getTagList("items", 10);
		for (int a = 0; a < list.tagCount(); a++)
		{
			NBTTagCompound tag = (NBTTagCompound) list.getCompoundTagAt(a);
			items.add(new CleanEntry(tag));
		}
		dirtiness = nbt.getInteger("dirtiness");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt)
	{
		super.writeToNBT(nbt);
		NBTTagList list = new NBTTagList();
		for (int a = 0; a < items.size(); a++)
		{
			list.appendTag(items.get(a).save(new NBTTagCompound()));
		}
		nbt.setTag("items", list);

		nbt.setInteger("dirtiness", dirtiness);
	}

	int maxDirtiness = 32;

	Random r = new Random();
	int ticksSinceLastBubbleRand = 0;

	@Override
	public void updateEntity()
	{
		if (hasFire() && hasWater())
		{
			if (worldObj.isRemote)
			{
				if (r.nextInt(10) < 2)
				{
					spawnBubble(.1);
					if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 3 && ticksSinceLastBubbleRand == 0 && r.nextInt(125) == 0)
					{
						worldObj.playSound(xCoord + 0.5, yCoord + 1.05, zCoord + 0.5, "CloneCraft:general.bubbles", 0.05f, 1.0f + (r.nextFloat() / 20.0f),
								false);
						ticksSinceLastBubbleRand = 100;
					}
				}
				if (ticksSinceLastBubbleRand == 0 && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 3)
				{
					// 2.443
					// 48.86
					worldObj.playSound(xCoord + 0.5, yCoord + 1.05, zCoord + 0.5, "CloneCraft:general.bubbles.loop", 0.05f, 1.0f + (r.nextFloat() / 20.0f),
							false);
					ticksSinceLastBubbleRand = 48;
				}
				if (ticksSinceLastBubbleRand > 0)
				{
					ticksSinceLastBubbleRand--;
				}
			}
			if (!worldObj.isRemote)
			{
				if (items.size() > 0)
				{
					for (int a = 0; a < items.size();)
					{
						if (items.get(a).update())
						{
							items.get(a).eject(worldObj, true);
							items.remove(a);
							dirtiness++;
							if (dirtiness >= maxDirtiness)
							{
								dropItems();
								worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 2, 2);
							}
						}
						else
						{
							a++;
						}
					}

				}
				if (dirtiness >= maxDirtiness)
				{
					dropItems();
					worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 2, 2);
				}
				else
				{
					if (items.size() < maxCapacity && items.size() + dirtiness < maxDirtiness)
					{
						absorbItem();
					}
					if (items.size() > 0 && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 3)
					{
						worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 3, 2);
					}
				}
			}
		}
		else
		{
			if (!worldObj.isRemote)
			{
				if (items.size() > 0)
				{
					dropItems();
				}

			}
		}
		if (!worldObj.isRemote)
		{
			if (items.isEmpty() && worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 3)
			{
				worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 1, 2);
			}
		}
		if (worldObj.isRemote)
		{
			if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) == 3)
			{
				spawnBubble(r.nextFloat() / 5 + .8);
			}
		}
	}

	public void absorbItem()
	{
		List list = worldObj.getEntitiesWithinAABB(EntityItem.class,
				AxisAlignedBB.getBoundingBox(xCoord + .1, yCoord + .3, zCoord + .1, xCoord + .9, yCoord + 1.2, zCoord + .9));
		for (int a = 0; a < list.size(); a++)
		{
			Object o = list.get(a);
			if (o != null && o instanceof EntityItem)
			{
				EntityItem entityItem = (EntityItem) o;
				ItemStack stack = entityItem.getEntityItem();
				if (stack != null && stack.getItem() == CloneCraft.INSTANCE.itemNeedle || stack.getItem() == CloneCraft.INSTANCE.itemTestTube)
				{
					ItemData data = new ItemData(stack);
					if (stack.getItemDamage() == 1 || data.isDirty() || data.isFull())
					{
						if (stack.stackSize == 1)
						{
							worldObj.removeEntity(entityItem);
							items.add(new CleanEntry(stack, 600));
						}
						else
						{
							stack.stackSize--;
							ItemStack stackCopy = currentItem = stack.copy();
							stackCopy.stackSize = 1;
							items.add(new CleanEntry(stackCopy, 600));
						}
						return;
					}
				}
			}
		}
	}

	int maxCapacity = 16;

	ArrayList<CleanEntry> items = new ArrayList<CleanEntry>();

	public void dropItems()
	{
		for (int a = 0; a < items.size(); a++)
		{
			items.get(a).eject(worldObj, false);
		}
		items.clear();
	}

	public boolean hasWater()
	{
		int id = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
		if (id == 1 || id == 3)
		{
			return true;
		}
		return false;
	}

	public boolean hasFire()
	{
		if (this.yCoord > 0)
		{
			Block block = worldObj.getBlock(xCoord, yCoord - 1, zCoord);
			if (block == Blocks.fire)
			{
				return true;
			}
		}
		return false;
	}

	@SideOnly(value = Side.CLIENT)
	public void spawnBubble(double upwards)
	{
		for (int a = 0; a < 2; a++)
		{
			EntityFX entityfx = new EntityBubbleFXAir(worldObj, xCoord + 0.1875 + (r.nextFloat() * 0.625), yCoord + 1.01, zCoord + 0.1875
					+ (r.nextFloat() * 0.625), r.nextFloat() / 8.0f, upwards, r.nextFloat() / 8.0f);
			CloneCraftWorld.spawnParticle(entityfx);
		}
	}

	public class CleanEntry
	{

		public int timeToGo;

		public ItemStack stack;

		private CleanEntry(NBTTagCompound nbt)
		{
			this.load(nbt);
		}

		public CleanEntry(ItemStack stack, int timeToGo)
		{
			this.stack = stack;
			this.timeToGo = timeToGo;
		}

		public void eject(World worldObj, boolean cleanse)
		{
			if (cleanse)
			{
				stack.setTagCompound(null);
				stack.setItemDamage(0);
				/*
				 * ItemData data = new ItemData(stack); data.cleanse();
				 * data.save(); stack.setItemDamage(0);
				 */
			}
			EntityItem entity = new EntityItem(worldObj, xCoord + .5, yCoord + 1, zCoord + .5, stack);
			entity.delayBeforeCanPickup = 10;
			entity.motionY = .5;
			worldObj.spawnEntityInWorld(entity);
		}

		public boolean update()
		{
			timeToGo--;
			return isReady();
		}

		public boolean isReady()
		{
			return timeToGo < 1;
		}

		public NBTTagCompound save(NBTTagCompound nbt)
		{
			nbt.setInteger("timeToGo", timeToGo);
			stack.writeToNBT(nbt);
			return nbt;
		}

		public NBTTagCompound load(NBTTagCompound nbt)
		{
			timeToGo = nbt.getInteger("timeToGo");
			stack = ItemStack.loadItemStackFromNBT(nbt);
			return nbt;
		}

	}

	// HA Wow, that was frustrating.

	// I've been trying to spawn bubbles. Now these bubbles originally did work,
	// but then after modifying the code a bit, it started to crash, however it
	// crashed... retardedly. It would get a list, from the world class. And
	// then it would iterate through that list. The code would work fine, until
	// I started spawning bubbles, at which point, for some reason, the list
	// would be modified and then the for loop would eventually grab either a
	// null value, or try to access an incorrect index of the array list, ie,
	// one which is out of bounds. I spent ages trying to remedy the problem. I
	// eventually figured it had to have something to do with seperate threads,
	// one which runs through the list, and one which modifies it while it is
	// running. However the code which spawned the bubbles ran on the same
	// thread as the world. The world class would return a list, which was never
	// re-created. It was defined outside of the method, and cleared each time
	// the method was run. The result being that any change to the list,
	// resulted in a change in the list everywhere else.
	// So, turns out, simple mistake, I was spawning bubbles in the client
	// world, from the server. Because the server is held within the same
	// instance of the client, I could access one from the other. And because
	// each world, serever and client, are run on different threads, the client
	// world would be running through updating it's entities, and would call the
	// list. Then while it is iterating through that list, I would then
	// inadventently modify it, causing it to be cleared. The iteration would
	// then fail and crash the game. That also explains why so many pigs kept
	// dying in a hole I had made. Because sometimes the iteration would
	// complete, but not run through all the entities, causing some to not be
	// updated, and shoved into a wall by the others, at which point it would
	// die. AMAZING. I love programming.

	// Shorter er version: It kept crashing. However the errors should have been
	// impossible to occur. The error? While iterating through an array list,
	// the list would suddenly change in size or values. The for loop would then
	// try to get an object which did not exist. This was made possible, because
	// of how the method returned the list. Instead of creating a new one each
	// time the method was run, it would clear one which was universally
	// declared in that class. It would then fill it with the data and return
	// the list. Each time the method was run, the list would be cleared. So
	// every time the list was modified, all copies of it would be modified as
	// well. So I was calling the method from a seperate thread which called it
	// in the first place. So the first call would get the objects, and it would
	// start iterating. Then it would run a second time, clear the list, and
	// then the for loop would try to get an index which did not exist, just at
	// the exact millisecond of which the list had just been cleared. One of the
	// hardest most retarded errors I've had in a while.
}
