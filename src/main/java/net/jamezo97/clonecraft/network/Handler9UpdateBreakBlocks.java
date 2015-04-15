package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.BreakableBlocks;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class Handler9UpdateBreakBlocks extends Handler{
	
	long[] data;
	int entityId;
	
	public Handler9UpdateBreakBlocks(int entityId, long[] data) {
		this.entityId = entityId;
		this.data = data;
	}
	
	public Handler9UpdateBreakBlocks() {
	}

	
	
	@Override
	public void handle(Side side, EntityPlayer player) {
		EntityClone clone = this.getUsableClone(player, entityId);
		if(clone == null){ return;}
		BreakableBlocks breakables = clone.getOptions().breakables;
		if(side == Side.CLIENT){
			breakables.clear();
			breakables.importLong(data);
			breakables.setDirty(false);
		}else if(side == Side.SERVER){
			if(!equals(breakables.getArray(), data)){
				breakables.clear();
				breakables.importLong(data);
				breakables.setDirty(false);
				sendToOwnersWatchingExcluding(clone, player);
			}
		}
	}
	
	public boolean equals(ArrayList<Long> data1, long[] data2){
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
		data = new long[size];
		for(int a = 0; a < data.length; a++){
			data[a] = buf.readLong();
		}
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(data.length);
		for(int a = 0; a < data.length; a++){
			buf.writeLong(data[a]);
		}
	}

	

	

	
	
}
