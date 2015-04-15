package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.sync.ByteIn;
import net.jamezo97.clonecraft.clone.sync.ByteOut;
import net.jamezo97.clonecraft.clone.sync.Sync;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class Handler2UpdateCloneData extends Handler{
	
	byte[] data;
	
	int entityId;
	
	public Handler2UpdateCloneData() {
	}
	
	public Handler2UpdateCloneData(EntityClone clone) {
		entityId = clone.getEntityId();
	}
	
	public Handler2UpdateCloneData(Sync sync, EntityClone clone) {
		this(clone);
		open();
		add(clone, sync);
		close();
	}

	public void add(EntityClone clone, Sync sync){
		if(out == null || !out.isOpen()){
			open();
			if(out == null || !out.isOpen()){
				return;
			}
		}
		try {
			out.getDataStream().writeInt(sync.id);
			sync.write(out.getDataStream(), clone);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	ByteOut out;
	public void open(){
		out = new ByteOut();
	}
	public void close(){
		if(out != null){
			data = out.close();
		}
	}
	
	public void handleServer(EntityPlayer player){
		if(data != null && data.length != 0){
			Entity entity = player.worldObj.getEntityByID(entityId);
			if(entity instanceof EntityClone){
				EntityClone clone = (EntityClone)entity;
				if(clone.canUseThisEntity(player)){
					ByteIn in = new ByteIn(data);
					ArrayList<Sync> toUpdate = new ArrayList<Sync>();
					if(in.isOpen()){
						DataInputStream d = in.getDataStream();
						try {
							Sync sync;
							int id;
							while(d.available()>3){
								id = d.readInt();
								if(d.available() > 0){
									sync = clone.getWatcher().getSync(id);
									if(sync != null){
										if(sync.canBeEditedByClient()){
											sync.read(d, clone);
											toUpdate.add(sync);
										}else{
											System.out.println(player.getCommandSenderName() + " attempted to edit a Sync value(" + id + ") which cannot be edited by the client!");
											break;
										}
									}
								}else{
									System.out.println("No data to be read by syncer (" + id + ")?");
								}
							}
							if(d.available() > 0 && d.available() < 4){
								System.out.println("Avail Bytes: " + d.available() + ", Not enough for integer ID");
							}
						} catch (IOException e) {
							e.printStackTrace();
						}finally{
							in.close();
						}
					}
					
					if(toUpdate.size() > 0){
						data = null;
						open();
						for(int a = 0; a < toUpdate.size(); a++){
							this.add(clone, toUpdate.get(a));
						}
						close();
						this.sendToAllWatching(clone);
					}
				}
			}
		}
	}
	
	public void handleClient(EntityPlayer player){
		if(data != null && data.length != 0){
			Entity entity = player.worldObj.getEntityByID(entityId);
			if(entity instanceof EntityClone){
				EntityClone clone = (EntityClone)entity;
				ByteIn in = new ByteIn(data);
				if(in.isOpen()){
					DataInputStream d = in.getDataStream();
					try {
						Sync sync;
						int id;
						while(d.available() > 3){
							id = d.readInt();
							if(d.available() > 0){
								sync = clone.getWatcher().getSync(id);
								if(sync != null){
									sync.read(d, clone);
								}
							}else{
								System.out.println("No data to be read by syncer (" + id + ")?");
							}
						}
						if(d.available() > 0 && d.available() < 4){
							System.out.println("Avail Bytes: " + d.available() + ", Not enough for integer ID");
						}
					} catch (IOException e) {
						e.printStackTrace();
					}finally{
						in.close();
					}
				}
			}
		}
	}
	
	@Override
	public void handle(Side side, EntityPlayer player) {
		if(side == Side.SERVER){
			handleServer(player);
		}else{
			handleClient(player);
		}
	}

	@Override
	public void read(ByteBuf buf) {
		entityId = buf.readInt();
		int size = buf.readInt();
		if(size > 0){
			data = new byte[size];
			buf.readBytes(data);
		}
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(entityId);
		if(data != null && data.length > 0){
			buf.writeInt(data.length);
			buf.writeBytes(data);
		}else{
			buf.writeInt(-1);
		}
	}

	public boolean isEmpty() {
		return data == null || data.length == 0;
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	int entityId;
//	
//	EntityClone clone;
//	Watchable watcher;
//
//	public Handler2UpdateCloneData(EntityClone clone, Watchable watcher) {
//		this.clone = clone;
//		entityId = clone.getEntityId();
//		this.watcher = watcher;
//	}
//
//	@Override
//	public void handle(Side side, EntityPlayer player) {
//		if(side == Side.SERVER) return;
//		Entity e = player.worldObj.getEntityByID(entityId);
//		if(e != null && e instanceof EntityClone){
//			watcher.handle(data, (EntityClone)e);
//		}
//	}
//	
//	Object[] data;
//
//	@Override
//	public void read(ByteBuf buf) {
//		entityId = buf.readInt();
//		watcher = CloneWatcher.getWatcherById(buf.readInt());
//		data = watcher.read(buf);
//	}
//
//	@Override
//	public void write(ByteBuf buf) {
//		buf.writeInt(entityId);
//		buf.writeInt(watcher.getId());
//		watcher.write(buf, clone);
//	}
	
	
	

}
