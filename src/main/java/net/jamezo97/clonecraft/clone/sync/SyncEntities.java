package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.AttackableEntities;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.nbt.NBTTagCompound;

public class SyncEntities extends Sync{

	public SyncEntities(int id) {
		super(id);
	}
	
	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return false;
	}

	@Override
	public void updateValues(EntityClone clone) {
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)throws IOException {
		int[] data = clone.getOptions().attackables.exportInt();
		out.writeInt(data.length);
		for(int a = 0; a < data.length; a++){
			out.writeInt(data[a]);
		}
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		int size = in.readInt();
		if(size > -1){
			int[] data = new int[size];
			for(int a = 0; a < size; a++){
				data[a] = in.readInt();
			}
			clone.getOptions().attackables.importInt(data);
		}
	}

	@Override
	public int getChannel() {
		return 1;
	}

	
	

}
