package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.Random;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;

public class Handler7CloneClones extends Handler{
	
	
	int entityId;

	int amount;
	
	@Override
	public void read(ByteBuf buf) {
		entityId = buf.readInt();
		amount = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(amount);
	}
	

	public Handler7CloneClones(){

	}

	public Handler7CloneClones(EntityClone clone, int amount){
		this.entityId = clone.getEntityId();
		this.amount = amount;
	}

	@Override
	public void handle(Side side, EntityPlayer player) {
		if(side == Side.SERVER){
			if(amount > 50){
				amount = 50;
			}
			if(amount > 0){
				if(player.capabilities.isCreativeMode){
					Random r = new Random();
					Entity e = player.worldObj.getEntityByID(entityId);
					if(e != null && e instanceof EntityClone){
						EntityClone toCopy = (EntityClone)e;
						if(toCopy != null && toCopy.canUseThisEntity(player)){
							NBTTagCompound cloneTag = new NBTTagCompound();
							toCopy.writeToNBT(cloneTag);
							for(int a = 0; a < amount; a++){
								EntityClone newClone = new EntityClone(toCopy.worldObj);
								newClone.readFromNBT(cloneTag);
								newClone.posX += (r.nextFloat()-.5f) / 10.0f;
								newClone.posZ += (r.nextFloat()-.5f) / 10.0f;
								newClone.worldObj.spawnEntityInWorld(newClone);
							}
						}
					}
					
				}
				
			}
		}
	}

	
	



}
