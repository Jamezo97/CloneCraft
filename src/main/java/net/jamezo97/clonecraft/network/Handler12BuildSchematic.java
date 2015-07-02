package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.jamezo97.clonecraft.schematic.SchematicEntry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;

public class Handler12BuildSchematic extends Handler{
	
//	String schematicName;
	
	/**
	 * <b>Stage 0:</b> Check if a schematic exists, with the given hash identifier<br>
	 * <b>Stage 1:</b> Schematic did not exist, please send it to me.<br>
	 * <b>Stage 2:</b> Try to build the schematic again. If this fails, report it to the user and don't keep trying.
	 */
	int syncStage = 0;
	
	long schematicHash;
	
	int xSize, ySize, zSize;
	
	int posX, posY, posZ;
	
	int cloneEntityID;
	
	public Handler12BuildSchematic()
	{
		
	}
	
	public Handler12BuildSchematic(Schematic schematic, EntityClone builder)
	{
		schematicHash = schematic.myHashCode();
		
		this.xSize = schematic.xSize;
		this.ySize = schematic.ySize;
		this.zSize = schematic.zSize;
		
		this.posX = builder.getBuildAI().posX;
		this.posY = builder.getBuildAI().posY;
		this.posZ = builder.getBuildAI().posZ;
		
		this.cloneEntityID = builder.getEntityId();
	}

	@Override
	public void handle(Side side, EntityPlayer player) 
	{
		if(syncStage == 0 && side == Side.SERVER)
		{
			//Check if the schematic exists.
			SchematicEntry entry = getSchematic();
			
			//The Schematic Exists!
			
			if(entry != null)
			{
				//Start Building
				build(player, entry);
			}
			else
			{
				//Schematic does not exist. Request it from the client.
				this.syncStage = 1;
				
				this.sendToPlayer(player);
			}
		}
		else if(syncStage == 1 && side == Side.CLIENT)
		{
			//The other side wants the schematic file.
			SchematicEntry entry = getSchematic();
			
			if(entry != null)
			{
				player.addChatMessage(
						new ChatComponentText("Sending schematic file to server. Please wait.")
						.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
				this.syncStage = 2;
				CloneCraft.INSTANCE.schematicList.sendSchematic(entry.schem, null, this);
			}
			else
			{
				System.err.println("Internal error. Couldn't find original schematic to build. Aborting build.");
				player.addChatMessage(
						new ChatComponentText("Internal error whilst trying to initiate build. It has been aborted. Please try again.")
						.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			}
		}
	}
	
	public void build(EntityPlayer player, SchematicEntry entry)
	{
		if(entry == null)
		{
			entry = getSchematic();
		}
		
		if(entry != null)
		{
			//Start Building
			player.addChatMessage(
					new ChatComponentText("Start Building!")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
			
			EntityClone clone = this.getUsableClone(player, cloneEntityID);
			
			if(clone != null)
			{
				clone.getBuildAI().posX = posX;
				clone.getBuildAI().posY = posY;
				clone.getBuildAI().posZ = posZ;
				
				clone.getBuildAI().setSchematic(entry.schem);

				clone.getBuildAI().setBuilding(true);
				
//				entry.schem.buildInstantly(posX, posY, posZ, clone.worldObj);
			}
			else
			{
				player.addChatMessage(
						new ChatComponentText("Clone doesn't exist, or you can't use it. Hacker!?")
						.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			}
		}
		else
		{
			player.addChatMessage(
					new ChatComponentText("Schematic failed to send. Building failed. Aborting.")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		}
	}
	
	public SchematicEntry getSchematic()
	{
		return CloneCraft.INSTANCE.schematicList.getSchematic(schematicHash, xSize, ySize, zSize);
	}

	@Override
	public void read(ByteBuf buf) 
	{
/*		int len = buf.readShort();
		
		schematicName = "";
		
		for(int a = 0; a < len; a++)
		{
			schematicName += buf.readChar();
		}*/
		
		syncStage = buf.readInt();
		schematicHash = buf.readLong();

		xSize = buf.readInt();
		ySize = buf.readInt();
		zSize = buf.readInt();
		
		cloneEntityID = buf.readInt();
		
		posX = buf.readInt();
		posY = buf.readInt();
		posZ = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) 
	{
/*		char[] chars = schematicName.toCharArray();
		
		buf.writeShort(chars.length);
		
		for(int a = 0; a < chars.length; a++)
		{
			buf.writeChar(chars[a]);
		}*/
		
		buf.writeInt(syncStage);
		buf.writeLong(schematicHash);

		buf.writeInt(xSize);
		buf.writeInt(ySize);
		buf.writeInt(zSize);
		
		buf.writeInt(this.cloneEntityID);
		
		buf.writeInt(posX);
		buf.writeInt(posY);
		buf.writeInt(posZ);
	}




	
	
}
