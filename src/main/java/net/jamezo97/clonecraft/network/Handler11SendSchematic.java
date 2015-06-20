package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.io.File;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import cpw.mods.fml.relauncher.Side;

public class Handler11SendSchematic extends Handler{
	
	
	
	
	@Override
	public void handle(Side side, EntityPlayer player)
	{
		this.schematicName = this.schematicName.replace("/", "_").replace("\\\\", "_");
		CloneCraft.INSTANCE.schematicList.receiveData(this, schematicName, (side==Side.SERVER?"SERVER":player.getCommandSenderName()));
	}
	
	
	
	
	public EntityPlayerMP sendTo = null;
	
	public void doSend()
	{
		if(sendTo == null)
		{
			this.sendToServer();
		}
		else
		{
			this.sendToPlayers(sendTo);
		}
	}
	
	
	
	String schematicName = "";
	
	//The size of the schematic, to verify.
	public int xSize, ySize, zSize;
	
	//The position to start setting block ids/metas in the schematic file.
	public int offset;
	
	//Block IDs
	public int[] ids;
	
	//Data IDs
	public int[] data;
	
	public Handler11SendSchematic()
	{
		
	}
	
	public Handler11SendSchematic(Schematic schem, int offset, int len)
	{
		this.xSize = schem.xSize;
		this.ySize = schem.ySize;
		this.zSize = schem.zSize;
		
		this.schematicName = schem.name;
	
		this.offset = offset;
		
		ids = new int[len];
		data = new int[len];
		
		System.arraycopy(schem.blockIds, offset, ids, 0, len);
		System.arraycopy(schem.blockMetas, offset, data, 0, len);
	}

	@Override
	public void read(ByteBuf buf) {
		{
			short len = buf.readShort();
			this.schematicName = "";
			for(int a = 0; a < len; a++)
			{
				this.schematicName += buf.readChar();
			}
		}
		this.xSize = buf.readInt();
		this.ySize = buf.readInt();
		this.zSize = buf.readInt();
		
		this.offset = buf.readInt();
		
		int len = buf.readInt();
	
		this.ids = new int[len];
		this.data = new int[len];
		
		for(int a = 0; a < len; a++)
		{
			this.ids[a] = buf.readInt();
			this.data[a] = buf.readInt();
		}
	}

	@Override
	public void write(ByteBuf buf) {
		{
			buf.writeShort(this.schematicName.length());
			char[] chars = this.schematicName.toCharArray();
			for(int a = 0; a < chars.length; a++)
			{
				buf.writeChar(chars[a]);
			}
		}
		
		buf.writeInt(this.xSize);
		buf.writeInt(this.ySize);
		buf.writeInt(this.zSize);
		
		buf.writeInt(this.offset);
		
		buf.writeInt(this.ids.length);
		
		for(int a = 0; a < this.ids.length; a++)
		{
			buf.writeInt(this.ids[a]);
			buf.writeInt(this.data[a]);
		}
		
	}

}
