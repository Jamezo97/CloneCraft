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
		return oldStack != clone.getShareAI().getOfferedItem();
	}

	@Override
	public void updateValues(EntityClone clone) {
		oldStack = clone.getShareAI().getOfferedItem();
	}

	@Override
	public void write(DataOutputStream out, EntityClone clone)
			throws IOException {
		
		NBTTagCompound nbt = null;
		ItemStack onOffer = clone.getShareAI().getOfferedItem();
		
		if(onOffer != null)
		{
			nbt = onOffer.writeToNBT(new NBTTagCompound());
		}
		
		writeNBTTag(nbt, out);
	}

	@Override
	public void read(DataInputStream in, EntityClone clone) throws IOException {
		
		NBTTagCompound nbt = this.readNBTTag(in);
		ItemStack stack = null;
		
		if(nbt != null)
		{
			stack = ItemStack.loadItemStackFromNBT(nbt);
		}
		
		clone.getShareAI().setOfferedItem(stack);
	}

	
	//Copied from PacketBuffer.class
	public void writeNBTTag(NBTTagCompound stack, DataOutputStream out) throws IOException
    {
        if (stack == null)
        {
            out.writeShort(-1);
        }
        else
        {
            byte[] abyte = CompressedStreamTools.compress(stack);
            out.writeShort((short)abyte.length);
            out.write(abyte);
        }
    }

    /**
     * Reads a compressed NBTTagCompound from this buffer
     */
    public NBTTagCompound readNBTTag(DataInputStream in) throws IOException
    {
        short short1 = in.readShort();

        if (short1 < 0)
        {
            return null;
        }
        else
        {
            byte[] abyte = new byte[short1];
            in.readFully(abyte);
            return CompressedStreamTools.func_152457_a(abyte, new NBTSizeTracker(2097152L));
        }
    }
	

}
