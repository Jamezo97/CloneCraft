package net.jamezo97.clonecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.entity.EntitySparkFX;
import net.jamezo97.clonecraft.raytrace.BlockCheckerExclude;
import net.jamezo97.clonecraft.raytrace.IntPos;
import net.jamezo97.clonecraft.raytrace.Line;
import net.jamezo97.clonecraft.raytrace.Point;
import net.jamezo97.clonecraft.raytrace.RayTracer;
import net.jamezo97.clonecraft.render.Lightning;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.CloneCraftWorld;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLifeInducer extends TileEntityBase{

	public int power = 0;
	int maxPower = 160;
	
	public TileEntityLifeInducer() {
		super(27);
	}

	@Override
	public String getTileEntityName() {
		return "Transmogrifier";
	}

	
	
	@Override
	public void readFromNBT(NBTTagCompound nbt) 
	{
		super.readFromNBT(nbt);
		power = nbt.getInteger("power");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) 
	{
		super.writeToNBT(nbt);
		nbt.setInteger("power", power);
	}

	public boolean isValidItem(ItemStack stack) 
	{
		if(stack == null || (stack.getItem() == CloneCraft.INSTANCE.itemSpawnEgg && stack.getItemDamage() == 0))
		{
			return true;
		}
		return false;
	}
	public void onBroken() 
	{
		if(power > 0)
		{
			explode(worldObj, xCoord, yCoord, zCoord, power);
		}
	}



	public void explode(World world, int x, int y, int z, int power) 
	{
		List entities = world.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+1, z+1).expand(64, 48, 64));
		double xMid = x+.5;
		double yMid = y-.5;
		double zMid = z+.5;

		double storedEnergy = ((double)power) / 14d;

		for(int a = 0; a < entities.size(); a++)
		{
			Entity e = (Entity)entities.get(a);
			
			if(e != null)
			{

				double xE = e.posX;
				double yE = e.posY;
				double zE = e.posZ;

				double dX = xE - xMid;
				double dY = yE - yMid;
				double dZ = zE - zMid;

				double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
				float attack = 4.0f*((float)storedEnergy)/4f / (float)distance;
				e.attackEntityFrom(DamageSource.generic, attack);
				e.velocityChanged = false;

				dX /= distance;
				dY /= distance;
				dZ /= distance;



				e.motionX += dX*storedEnergy;
				e.motionY += dY*storedEnergy;
				e.motionZ += dZ*storedEnergy;
				e.velocityChanged = true;
			}
		}
		
		if(CloneCraft.INSTANCE.config.LIFEINDUCER_EXPLODE)
		{
			int radius = power * 12 / maxPower;
			
			int radiusSquare = radius * radius;
			
			double dampen = 1.5;
			
			Random rand = new Random();
			
			Explosion explosm = new Explosion(this.worldObj, null, x, y, z, radius/2.0f);
			
			for(int xx = x-radius; xx < x+radius; xx++)
			{
				int dx = xx-x;
				int dx2 = dx*dx;
				
				for(int yy = y-radius; yy < y+radius; yy++)
				{
					int dy = yy-y;
					int dy2 = dy*dy;
					
					for(int zz = z-radius; zz < z+radius; zz++)
					{
						int dz = zz-z;
						int dz2 = dz * dz;
						
						double dxyz = dx2 + dy2 + dz2;
						
						if(dxyz <= radiusSquare)
						{
							Block block = this.worldObj.getBlock(xx, yy, zz);
							
							if(block != Blocks.air && block.getBlockHardness(worldObj, xx, yy, zz) < 40 && block.getExplosionResistance(null, worldObj, xx, yy, zz, x, y, z) < 20)
							{
								int meta = this.worldObj.getBlockMetadata(xx, yy, zz);
								
								if(rand.nextInt(10) > 1 && block.isOpaqueCube())
								{
									double velocityX = 0;
									if(dx != 0)
									{
										velocityX = radius/dx/dampen * (rand.nextFloat()*0.01 + 0.99);
									}
									
									double velocityY = 0;
									if(dy != 0)
									{
										velocityY = radius/dy/dampen * (rand.nextFloat()*0.01 + 0.99);
									}
									
									double velocityZ = 0;
									if(dz != 0)
									{
										velocityZ = radius/dz/dampen * (rand.nextFloat()*0.01 + 0.99);
									}
									
									EntityFallingBlock falling = new EntityFallingBlock(this.worldObj, xx, yy, zz, block, meta);
									
									falling.field_145812_b = 1; //Set time to 1
									
									falling.setVelocity(velocityX, velocityY, velocityZ);
									
									block.onBlockDestroyedByExplosion(this.worldObj, xx, yy, zz, explosm);
									
									this.worldObj.setBlock(xx, yy, zz, Blocks.air);
									
									this.worldObj.spawnEntityInWorld(falling);
								}
								else
								{
									block.dropBlockAsItem(this.worldObj, xx, yy, zz, meta, 0);
									worldObj.setBlock(xx, yy, zz, Blocks.air);
								}
								
							}
						}
					}
				}
			}
		}
		
		
		this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "random.explode", 0.9f, 0.95f + worldObj.rand.nextFloat()*0.1f);
		
	}

	int ticks = -1;
	int removePlayerLight = -1;
	

	public ArrayList<Lightning> elec = new ArrayList<Lightning>();
	

	public void updateVisuals()
	{
		if(worldObj.isRemote)
		{
			if(removePlayerLight != -1 && ticks >= removePlayerLight)
			{
				electrocuteEntity = null;
				removePlayerLight = -1;
			}
			
			updateLightning();
			
			if(this.power > 0 && ticks % 20 == 0)
			{
				this.worldObj.playSound(this.xCoord, this.yCoord, this.zCoord, "clonecraft:elec.zap", 1.0f, 1.0f, false);
			}
			
			if(power > 0)
			{
				if(speed > 0.1)
				{
					speed -= 0.010f;
				}
			}
			else
			{
				if(speed > 0)
				{
					speed -= 0.010f;
				}
			}
			
			if(speed < 0.1 && power > 0)
			{
				speed = 0.1f;
			}
			else if(speed < 0 && power == 0)
			{
				speed = 0;
			}
			
			if(speed > 0)
			{
				lastSpin = spin;
				spin += speed;
				
				if(lastSpin >= 20)
				{
					spin -= 20;
					lastSpin -= 20;
				}
			}
		}
		else
		{
			int chance = 50 - power* 50 / maxPower ;
			if(chance == 0)
			{
				chance = 1;
			}
			
//			System.out.println(chance);
			
			if(this.power > 0 && ticks % 5 == 0 && worldObj.rand.nextInt(chance) == 0)
			{
				List l = worldObj.getEntitiesWithinAABB(EntityLivingBase.class, electrocuteSearch);
				Vec3 midPos = Vec3.createVectorHelper(xCoord+0.5, yCoord+0.5, zCoord+0.5);
				
				if(l.size() != 0)
				{
					double dSquare = -1;
					EntityLivingBase electrocute = null;
					EntityLivingBase temp;
					boolean flag = false;
					Vec3 tempVec;
					
					for(int a = 0; a < l.size(); a++)
					{
						temp = (EntityLivingBase)l.get(a);
						
						if(temp instanceof EntityPlayer)
						{
							InventoryPlayer ip = ((EntityPlayer)temp).inventory;
							ItemStack stack = ip.armorItemInSlot(0);
							if(stack != null && (stack.getItem() == Items.leather_boots || stack.getItem() == Items.diamond_boots))
							{
								//If the player is insulated
								continue;
							}
						}
						
						double distance = (tempVec=Vec3.createVectorHelper(temp.posX, temp.posY, temp.posZ)).squareDistanceTo(midPos);
						
						if(electrocute == null || distance < dSquare)
						{
							if(RayTracer.rayTraceSimple(worldObj, midPos, tempVec, 20, new BlockCheckerExclude().setExclusions(new IntPos(xCoord, yCoord, zCoord))))
							{
								electrocute = temp;
								dSquare = distance;
							}
						}
					}
					
					if(electrocute != null)
					{
						electrocuteEntity(electrocute, dSquare);
					}
				}
				
			}
			checkSendPower();
		}
	}
	//Up to 60 ticks, 3 seconds
	int chargeUp = 0;
	int[] charges = new int[27];
	
	int nextChargeStart = 0;
	@Override
	public void updateEntity() 
	{
		ticks++;
		updateVisuals();
		
		if(!worldObj.isRemote)
		{
			int added = 0;
			if(power > 0)
			{
				for(int a = 0; a < items.length; a++)
				{
					if(added >= 9)
					{
						break;
					}
					
					if(items[a] != null)
					{
						if(items[a].getItem() == CloneCraft.INSTANCE.itemSpawnEgg && items[a].getItemDamage() == 0)
						{
							if(charges[a] != 0 || ticks >= nextChargeStart)
							{
								charges[a]++;
								added++;
								if(charges[a] >= 60)
								{
									items[a].setItemDamage(1);
									power--;
									charges[a] = 0;
									this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "random.explode", 0.9f, 0.95f + worldObj.rand.nextFloat()*0.1f);
									this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "clonecraft:block.zoom", 1.0f, 0.95f + worldObj.rand.nextFloat()*0.1f);
									this.sendClientInfo(23, 0);
								}
								else if(charges[a] == 1)
								{
									this.worldObj.playSoundEffect(this.xCoord, this.yCoord, this.zCoord, "clonecraft:elec.chargeUp", 1.0f, 0.95f + worldObj.rand.nextFloat()*0.1f);
									nextChargeStart = ticks + 1 + worldObj.rand.nextInt(8);
									break;
								}
							}
							
						}
						else
						{
							charges[a] = 0;
						}
					}
					else
					{
						charges[a] = 0;
					}
				}
			}
		}
	}
	
	private void electrocuteEntity(EntityLivingBase entity, double distance) 
	{
		distance += 0.5;
		double dx = entity.posX - (xCoord+0.5);
		double dy = (entity.posY+entity.height/2) - (yCoord+0.5);
		double dz = entity.posZ - (zCoord+0.5);
		
		entity.motionX += dx/distance*3;
		entity.motionY += dy/distance*3;
		entity.motionZ += dz/distance*3;
		entity.velocityChanged = true;
		this.sendClientInfo(22, entity.getEntityId());
		
		entity.attackEntityFrom(DamageSource.generic,(float)(12 / distance));
	}



	//				0			1			2			3
	//Layer 1 - BottomLeft, Bottom Right, Top Right, Top Left
	//				4			5			6			

	double[][] points = {{0, 0, 0}, {1, 0, 0}, {1, 0, 1}, {0, 0, 1}, {0, 0.5, 0}, {1, 0.5, 0}, {1, 0.5, 1}, {0, 0.5, 1}};

	int[][] lines = {{0, 1}, {1, 2}, {2, 3}, {3, 0}, {4, 5}, {5, 6}, {6, 7}, {7, 4}, {0, 4}, {1, 5}, {2, 6}, {3, 7}};
	
	@SideOnly(value = Side.CLIENT)
	public void updateLightning()
	{
		elec.clear();
		
		if(this.power > 0)
		{
			updateExternalLightning();
			double[] from, to;
			
			for(int a = 0; a < lines.length; a++)
			{
				from = points[lines[a][0]];
				to = points[lines[a][1]];
				
				for(int b = 0; b < 2; b++)
				{
					elec.add(new Lightning(from[0], from[1], from[2], to[0], to[1], to[2], 8, false, false).setColours(0x5555cc, 0x5555cc));
				}
			}
			
			double x, y, z;
			
			if(electrocuteEntity != null)
			{
				this.lightLines[0] = new Line(0.5, 0.5, 0.5, electrocuteEntity.posX-xCoord, electrocuteEntity.posY-yCoord + electrocuteEntity.height/2, electrocuteEntity.posZ-zCoord);
			}
			
			for(int a = 0; a < lightLines.length; a++)
			{
				if(lightLines[a] != null)
				{
					elec.add(new Lightning(lightLines[a], 10, true, true).setColours(0x22ffff, 0x5555cc));
					x = xCoord+lightLines[a].x2;
					y = yCoord+lightLines[a].y2;
					z = zCoord+lightLines[a].z2;
					
					for(int b = 0; b < 10; b++)
					{
						EntityFX e = new EntitySparkFX(worldObj, x, y, z, 1);
						CloneCraftWorld.spawnParticle(e);
					}
				}
			}
			
		}
	}

	int index = -1;

	Line[] lightLines = new Line[4];
	
	@SideOnly(value = Side.CLIENT)
	public void updateExternalLightning()
	{
		if(ticks % 8 == 0)
		{
			index++;
			
			if(index > 3)
			{
				index = 0;
			}
			lightLines[index] = null;
			int x, y, z;
			Block b;
			Point p;
			
			for(int a = 0; a < 10; a++)
			{
				x = worldObj.rand.nextInt(10)-5;
				y = worldObj.rand.nextInt(10)-5;
				z = worldObj.rand.nextInt(10)-5;
				
				if(x*x + y*y + z*z < 1.9)
				{
					continue;
				}
				
				x += xCoord;
				y += yCoord;
				z += zCoord;
				if(y > 255)
				{
					y = 255;
				}
				else if(y < 0)
				{
					y = 0;
				}

				b = worldObj.getBlock(x, y, z);
				
				if(b != null && b.getMaterial() == Material.iron || b.getMaterial() == Material.anvil || b.getMaterial() == Material.circuits || b.getMaterial() == Material.grass || b.getMaterial() == Material.ground || b.getMaterial() == Material.water)
				{
					if(null != (p = RayTracer.rayTraceSimpleGetEnd(worldObj, Vec3.createVectorHelper(xCoord+0.5, yCoord+0.5, zCoord+0.5), Vec3.createVectorHelper(x+0.5, y+0.5, z+0.5), 20, new BlockCheckerExclude().setExclusions(new IntPos(xCoord, yCoord, zCoord)))))
					{
						IntPos pos = p.floor();
						
						if(pos.x == x && pos.y == y && pos.z == z)
						{
							lightLines[index] = new Line(worldObj.rand.nextFloat(), worldObj.rand.nextFloat()/2, worldObj.rand.nextFloat(), p.x-xCoord, p.y-yCoord, p.z-zCoord);
							break;
						}
						
					}
				}
			}
		}
	}

	Entity electrocuteEntity = null;
	
	float speed = 0.0f;
	public float lastSpin = 0;
	public float spin = 0;
	
	@Override
	public boolean receiveClientEvent(int key, int value) 
	{
		if(key == 20)
		{
			power = value;
		}
		else if(key == 21)
		{
			if(power != value)
			{
				speed += 1.0f;
				if(speed > 2)
				{
					speed = 2;
				}
			}
			power = value;
		}
		else if(key == 22)
		{
			electrocuteEntity = worldObj.getEntityByID(value);
			
			if(electrocuteEntity != null)
			{
				this.removePlayerLight = ticks + 10;
			}
		}
		else if(key == 23)
		{
			if(this.worldObj.isRemote)
			{
				spawnSparks(xCoord+0.5, yCoord+1, zCoord+0.5);
			}
		}
		else
		{
			return super.receiveClientEvent(key, value);
		}
		return true;
	}
	
	@SideOnly(value = Side.CLIENT)
	public void spawnSparks(double x, double y, double z)
	{
		EntityFX e;
		
		for(int a = 0; a < 70; a++)
		{
			e = new EntitySparkFX(worldObj, x, y, z, 4);
			CloneCraftWorld.spawnParticle(e);
		}
	}
	
	AxisAlignedBB playerSearch;
	AxisAlignedBB electrocuteSearch;
	
	public void validate()
	{
		super.validate();
		updateSearch();
	}
	 
	public void updateSearch()
	{
		playerSearch = AxisAlignedBB.getBoundingBox(xCoord+0.5 - 32, yCoord+0.5 - 32, zCoord+0.5 - 32, xCoord+0.5 + 32, yCoord+0.5 + 32, zCoord+0.5 + 32);
		electrocuteSearch = AxisAlignedBB.getBoundingBox(xCoord+0.5 - 6, yCoord+0.5 - 6, zCoord+0.5 - 6, xCoord+0.5 + 4, yCoord+0.5 + 4, zCoord+0.5 + 4);
	}
	
	 
	int lastSize = 0;
	
	public void checkSendPower()
	{
		if(playerSearch != null)
		{
			List l = worldObj.getEntitiesWithinAABB(EntityPlayer.class, playerSearch);
			
			if(l.size() != lastSize)
			{
				lastSize = l.size();
				sendPowerToClient();
			}
		}
	}
	
	public void sendPowerToClient()
	{
		this.sendClientInfo(20, power);
	}

	public void charge() 
	{
		if(power <= maxPower-16)
		{
			power += 16;
			this.sendClientInfo(21, power);
		}
	}

	public void discharge() 
	{
		power = 0;
		this.sendPowerToClient();
	}

	
	

	
	
}
