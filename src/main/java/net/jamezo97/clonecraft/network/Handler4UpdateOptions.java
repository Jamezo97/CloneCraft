package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.Set;

import net.jamezo97.clonecraft.clone.CloneOption;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import cpw.mods.fml.relauncher.Side;

public class Handler4UpdateOptions extends Handler{

	int entityId;
	
	int intValue;
	
	public Handler4UpdateOptions(EntityClone clone, int intValue) {
		this.entityId = clone.getEntityId();
		this.intValue = intValue;
	}
	
	public Handler4UpdateOptions() {
		
	}

	@Override
	public void handle(Side side, EntityPlayer player) {
		if(side == Side.SERVER){
			Entity e = player.worldObj.getEntityByID(entityId);
			if(e != null && e instanceof EntityClone){
				EntityClone clone = (EntityClone)e;
				if(clone.canUseThisEntity(player)){
					clone.getOptions().fromInteger(intValue);
				}
			}
		}
	}

	@Override
	public void read(ByteBuf buf) {
		entityId = buf.readInt();
		intValue = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(entityId);
		buf.writeInt(intValue);
	}
	
	
	
	
	
	
	
	
	
//	int[] data = null;
//	
//	int entityId = -1;
//	
////	ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("multiplayer.player.left", new Object[] {this.playerEntity.func_145748_c_()});
////    chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
//	
//	public Handler4UpdateOptions(EntityClone clone, CloneOption... options){
//		this.entityId = clone.getEntityId();
//		data = new int[options.length];
//		for(int a = 0; a < options.length; a++){
//			if(options[a] != null){
//				data[a] = options[a].write();
//			}
//		}
//	}
//	
//	public Handler4UpdateOptions(EntityClone clone ,int... data){
//		this.entityId = clone.getEntityId();
//		this.data = data;
//	}
//	
//	public Handler4UpdateOptions(EntityClone clone){
//		this.entityId = clone.getEntityId();
//		this.data = clone.getOptions().toIntArray();
//	}
//	
//	public Handler4UpdateOptions(){
//		
//	}
//	
////	public void handleClient(EntityPlayer player, EntityClone clone){
////		clone.getOptions().loadFromInt(data);
////	}
//	
//	public void handleServer(EntityPlayer player, EntityClone clone){
//		if(clone.canUseThisEntity(player.getCommandSenderName())){
//			clone.getOptions().loadFromInt(data);
////			Set<EntityPlayer> wp= clone.getWatchingEntities();
////			HandlerPacket packet = this.getPacket();
////			for(EntityPlayer watcher : wp){
////				if(watcher != player && watcher instanceof EntityPlayerMP){
////					PacketHandler.net.sendTo(packet, (EntityPlayerMP)watcher);
////				}
////			}
//		}else{
//			ChatComponentText text = new ChatComponentText("You can't do that");
//			text.getChatStyle().setColor(EnumChatFormatting.DARK_RED);
//			player.addChatMessage(text);
//			System.out.println(player.getCommandSenderName() + " tried to modify clone which they are not an owner of! Hackz mate");
//		}
//	}
//
//	@Override
//	public void handle(Side side, EntityPlayer player) {
//		if(data != null){
//			Entity e = player.worldObj.getEntityByID(entityId);
//			if(e != null && e instanceof EntityClone){
//				if(side == Side.CLIENT){
////					handleClient(player, ((EntityClone)e));
//				}else if(side == Side.SERVER){
//					handleServer(player, ((EntityClone)e));
//				}
//			}
//			
//		}
//	}
//
//	@Override
//	public void read(ByteBuf buf) {
//		if(data == null){
//			buf.writeShort(-1);
//			return;
//		}
//		buf.writeShort(data.length);
//		buf.writeInt(entityId);
//		for(int a = 0; a < data.length; a++){
//			buf.writeInt(data[a]);
//		}
//	}
//
//	@Override
//	public void write(ByteBuf buf) {
//		int size = buf.readShort();
//		if(size > -1){
//			entityId = buf.readInt();
//			data = new int[size];
//			for(int a = 0; a < size; a++){
//				data[a] = buf.readInt();
//			}
//		}
//	}
	
	

}
