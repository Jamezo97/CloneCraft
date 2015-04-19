package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;

public class SyncItemOffer extends Sync{
	
	public SyncItemOffer(int id) {
		super(id);
	}
	
	ItemStack oldStack = null;

	@Override
	public boolean checkNeedsUpdating(EntityClone clone) {
		return !oldStack.areItemStacksEqual(oldStack, clone.getOfferedItem());
	}

	@Override
	public void updateValues(EntityClone clone) {
		oldStack = clone.getOfferedItem();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException {
		if(clone.getOfferedItem() == null){
			out.writeBoolean(false);
		}
		CompressedStreamTools.write(clone.getOfferedItem().writeToNBT(new NBTTagCompound()), p_74800_1_)
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		if(in.readBoolean()){
			
		}else{
//			clone.getShareAI().set
		}
	}

	

}
