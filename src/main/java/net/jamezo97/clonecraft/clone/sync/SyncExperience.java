package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;

public class SyncExperience extends Sync{
	
	int experienceLevel;
	int experienceTotal;
	float experience;
	
	public SyncExperience(int id) {
		super(id);
	}

	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return isDirty || clone.experienceLevel != experienceLevel || clone.experienceTotal != experienceTotal || clone.experience != experience;
	}

	@Override
	public void updateValues(EntityClone clone) {
		experienceLevel = clone.experienceLevel;
 		experienceTotal = clone.experienceTotal;
		experience = clone.experience;
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone) throws IOException {
		out.writeInt(clone.experienceLevel);
		out.writeInt(clone.experienceTotal);
		out.writeFloat(clone.experience);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		experienceLevel = clone.experienceLevel = in.readInt();
		experienceTotal = clone.experienceTotal = in.readInt();
		experience = clone.experience = in.readFloat();
	}

	
	//Send to owners only
	@Override
	public int getChannel() {
		return 1;
	}

	

	
	
}
