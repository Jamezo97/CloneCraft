package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncScale extends Sync{

	public SyncScale(int id) {
		super(id);
	}
	
	float lastScale = 0.5f;
	
	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return clone.getScale() != lastScale;
	}

	@Override
	public void updateValues(EntityClone clone) {
		lastScale = clone.getScale();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException {
		out.writeFloat(clone.getScale());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		clone.setScale(in.readFloat());
	}

	

	
	
}
