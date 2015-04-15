package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class Handler6KillClone extends Handler{

	int entityId;
	
	public Handler6KillClone(EntityClone clone) {
		this.entityId = clone.getEntityId();
	}
	
	public Handler6KillClone() {
		
	}

	@Override
	public void handle(Side side, EntityPlayer player) {
		if(side == Side.SERVER){
			Entity e = player.worldObj.getEntityByID(entityId);
			if(e != null && e instanceof EntityClone){
				EntityClone clone = (EntityClone)e;
				if(clone.canUseThisEntity(player)){
					sendToAllWatching(clone);
					clone.commitSuicide();
				}
			}
		}else if(side == Side.CLIENT){
			Entity e = player.worldObj.getEntityByID(entityId);
			if(e != null && e instanceof EntityClone){
				EntityClone clone = (EntityClone)e;
				clone.spawnExplosionParticles();
			}
		}
	}


	@Override
	public void read(ByteBuf buf) {
		entityId = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(entityId);
	}

	
	
	
}
