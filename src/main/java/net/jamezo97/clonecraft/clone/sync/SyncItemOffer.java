package net.jamezo97.clonecraft.clone.sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;

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
		CompressedStreamTools.write(clone.getOfferedItem().writeToNBT(new NBTTagCompound()), out);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		if(in.readBoolean()){
			clone.getShareAI().setOfferedItem(null);
		}else{
			ItemStack stack = ItemStack.loadItemStackFromNBT(CompressedStreamTools.func_152456_a(in, NBTSizeTracker.field_152451_a));
			
			clone.getShareAI().setOfferedItem(stack);
		}
	}

	

}
