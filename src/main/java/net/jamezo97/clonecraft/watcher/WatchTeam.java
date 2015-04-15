package net.jamezo97.clonecraft.watcher;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.PlayerTeam;

public class WatchTeam extends Watchable{

	public WatchTeam(int id) {
		super(id);
	}

	@Override
	public int getSize() {
		return 1;
	}

	@Override
	public void write(ByteBuf buf, EntityClone clone) {
		buf.writeByte(clone.team.teamID);
	}

	@Override
	public Object[] read(ByteBuf buf) {
		Object[] data = new Object[1];
		data[0] = PlayerTeam.getByID(buf.readByte());
		return data;
	}

	@Override
	public Object get(EntityClone clone, int index) {
		return clone.team.teamID;
	}

	@Override
	public void handle(Object[] data, EntityClone clone) {
		clone.team = (PlayerTeam)data[0];
	}

}