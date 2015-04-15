package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.AttackableEntities;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class Handler8UpdateAttackEntities extends Handler{
	
	int[] data;
	int entityId;
	
	public Handler8UpdateAttackEntities(int entityId, int[] data) {
		this.entityId = entityId;
		this.data = data;
	}
	
	public Handler8UpdateAttackEntities() {
	}

	
	
	@Override
	public void handle(Side side, EntityPlayer player) {
		EntityClone clone = this.getUsableClone(player, entityId);
		if(clone == null){ return;}
		AttackableEntities attackables = clone.getOptions().attackables;
		if(side == Side.CLIENT){
			attackables.clear();
			attackables.importInt(data);
			attackables.setDirty(false);
		}else if(side == Side.SERVER){
			if(!equals(attackables.getArray(), data)){
				attackables.clear();
				attackables.importInt(data);
				attackables.setDirty(false);
				sendToOwnersWatchingExcluding(clone, player);
			}
		}
	}
	
	public boolean equals(ArrayList<Integer> data1, int[] data2){
		if(data1.size() == data2.length){
			for(int a = 0; a < data2.length; a++){
				if(data1.get(a) != data2[a]){
					return false;
				}
			}
			return true;
		}
		
		return false;
	}

	@Override
	public void read(ByteBuf buf) {
		this.entityId = buf.readInt();
		int size = buf.readInt();
		data = new int[size];
		for(int a = 0; a < data.length; a++){
			data[a] = buf.readInt();
		}
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(data.length);
		for(int a = 0; a < data.length; a++){
			buf.writeInt(data[a]);
		}
	}

	

	

	
	
}
