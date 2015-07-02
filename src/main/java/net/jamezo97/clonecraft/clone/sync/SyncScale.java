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

	double lastAim = 0.5f;
	
	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return Math.abs(clone.getScale() - lastScale) > 0.003 || Math.abs(clone.getAimScale() - lastAim) > 0.003;
	}

	@Override
	public void updateValues(EntityClone clone) {
		lastScale = clone.getScale();
		lastAim = clone.getAimScale();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException {
//		System.out.println("Write");
		out.writeFloat(clone.getScale());
		out.writeFloat((float)clone.getAimScale());
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		clone.setScale(in.readFloat());
		clone.setAimScale(in.readFloat());
	}

	

	
	
}
