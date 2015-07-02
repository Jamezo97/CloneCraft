package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;

public class Handler11SendSchematic extends Handler{
	
	
	
	
	@Override
	public void handle(Side side, EntityPlayer player)
	{
		String schematicName = this.schematicName.replace("/", "_").replace("\\\\", "_");
		
		
		player.addChatMessage(
				new ChatComponentText("Received " + segment + " of " + total)
				.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));

		CloneCraft.INSTANCE.schematicList.receiveData(this, schematicName, player);
	}
	
	public Handler12BuildSchematic buildData = null;
	
	public void loadInitialData(Handler12BuildSchematic build)
	{
		buildData = build;
	}

	
	
	String schematicName = "";
	
	//The size of the schematic, to verify.
	public int xSize, ySize, zSize;
	
	//The position to start setting block ids/metas in the schematic file.
	public int offset;
	
	public int segment = 0;
	
	public int total = 0;
	
	//Block IDs
	public short[] ids;
	
	//Data IDs
	public short[] data;
	
	public Handler11SendSchematic()
	{
		
	}
	
	public Handler11SendSchematic(Schematic schem, int offset, int len, int segment, int total)
	{
		this.xSize = schem.xSize;
		this.ySize = schem.ySize;
		this.zSize = schem.zSize;
		
		this.segment = segment;
		
		this.total = total;
		
		this.schematicName = schem.name;
	
		this.offset = offset;
		
		ids = new short[len];
		data = new short[len];
		
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
	
		this.ids = new short[len];
		this.data = new short[len];
		
		for(int a = 0; a < len; a++)
		{
			this.ids[a] = buf.readShort();
			this.data[a] = buf.readShort();
		}
		
		this.segment = buf.readShort();
		this.total = buf.readShort();
		
		boolean buildDataSent = buf.readBoolean();
		if(buildDataSent)
		{
			buildData = new Handler12BuildSchematic();
			buildData.read(buf);
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
			buf.writeShort(this.ids[a]);
			buf.writeShort(this.data[a]);
		}

		buf.writeShort(this.segment);
		buf.writeShort(this.total);
		
		if(this.buildData != null)
		{
			buf.writeBoolean(true);
			buildData.write(buf);
		}
		else
		{
			buf.writeBoolean(false);
		}
		
		
	}

}
