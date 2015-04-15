package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncName extends Sync{

	String oldName = "";
	
	public SyncName(int id) {
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return isDirty || oldName == null || !oldName.equals(clone.nameUnedited);
	}

	@Override
	public void updateValues(EntityClone clone) {
		oldName = clone.nameUnedited;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException {
		out.writeUTF(clone.nameUnedited);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		clone.setName(oldName = in.readUTF());
	}

	@Override
	public int getChannel() {
		return 0;
	}
	
	public boolean canBeEditedByClient(){
		return true;
	}
	
	

	
	
}
