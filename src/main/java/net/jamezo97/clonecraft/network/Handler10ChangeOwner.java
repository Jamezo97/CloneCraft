package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;

public class Handler10ChangeOwner extends Handler{

	int cloneId;
	
	public Handler10ChangeOwner() {
		
	}
	
	public Handler10ChangeOwner(int cloneId) {
		this.cloneId = cloneId;
	}
	
	@Override
	public void handle(Side side, EntityPlayer player) {
		EntityClone clone = this.getUsableClone(player, cloneId);
		if(clone == null){
			player.addChatComponentMessage(new ChatComponentText("You shouldn't be able to do that...").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		}else{
			clone.setOwner(player.getCommandSenderName());
			player.addChatComponentMessage(new ChatComponentText("You now own this clone!").setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
		}
	}

	@Override
	public void read(ByteBuf buf) {
		cloneId = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(cloneId);
	}

	

}
