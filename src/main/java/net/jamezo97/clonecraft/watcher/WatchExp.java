package net.jamezo97.clonecraft.watcher;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.clone.EntityClone;

public class WatchExp extends Watchable{

	public WatchExp(int id) {
		super(id);
	}

	@Override
	public int getSize() {
		return 3;
	}

	@Override
	public Object get(EntityClone clone, int index) {
		switch(index){
		case 0: return clone.experience;
		case 1: return clone.experienceLevel;
		case 2: return clone.experienceTotal;
		}
		return null;
	}

	@Override
	public void write(ByteBuf buf, EntityClone clone) {
		buf.writeFloat(clone.experience);
		buf.writeInt(clone.experienceLevel);
		buf.writeInt(clone.experienceTotal);
	}

	@Override
	public Object[] read(ByteBuf buf) {
		Object[] data = new Object[3];
		data[0] = buf.readFloat();
		data[1] = buf.readInt();
		data[2] = buf.readInt();
		return data;
	}

	@Override
	public void handle(Object[] data, EntityClone clone) {
		clone.experience = (Float)data[0];
		clone.experienceLevel = (Integer)data[1];
		clone.experienceTotal = (Integer)data[2];
	}

}
