package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;

public class Handler1CentrifugeItemStacks extends Handler{

	int x, y, z;

//	int count;
	
	ItemStack[] stacks;
//	int[] positions;
	
	public Handler1CentrifugeItemStacks(int x, int y, int z, ItemStack[] stacks) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		this.stacks = stacks;
//		this.positions = new int[stacks.length];
//		count = 0;
//		for(int a = 0; a < stacks.length; a++){
//			if(stacks[a] != null){
//				this.stacks[count] = stacks[a];
//				this.positions[count] = a;
//				count++;
//			}
//		}
	}
	
	public Handler1CentrifugeItemStacks() {
		
	}

	@Override
	public void handle(Side side, EntityPlayer player) {
//		System.out.println("Receive: " + count);
		if(side == Side.CLIENT){
			TileEntity te = player.worldObj.getTileEntity(x, y, z);
			if(te instanceof TileEntityCentrifuge){
				for(int a = 0; a < stacks.length; a++){
					((TileEntityCentrifuge) te).setInventorySlotContents(a, stacks[a]);
				}
			}
		}
	}

	@Override
	public void read(ByteBuf buf) {
		x = buf.readInt();
		y = buf.readInt();
		z = buf.readInt();
		int count = buf.readShort();
		stacks = new ItemStack[count];
//		for(int a = 0; a < count; a++){
//			positions[a] = buf.readShort();
//		}
		ByteBufWrapper wrapper = new ByteBufWrapper(buf);
		boolean read;
		for(int a = 0; a < count; a++){
			read = buf.readBoolean();
			if(read){
				try {
					stacks[a] = ItemStack.loadItemStackFromNBT(CompressedStreamTools.func_152456_a(wrapper, NBTSizeTracker.field_152451_a));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeShort(stacks.length);
		ByteBufWrapper wrapper = new ByteBufWrapper(buf);
		for(int a = 0; a < stacks.length; a++){
			if(stacks[a] != null){
				buf.writeBoolean(true);
				try {
					CompressedStreamTools.write(stacks[a].writeToNBT(new NBTTagCompound()), wrapper);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				buf.writeBoolean(false);
			}
			
		}
	}



	
	
}
