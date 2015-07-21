package net.jamezo97.clonecraft.schematic;

import net.jamezo97.clonecraft.network.Handler11SendSchematic;
import net.jamezo97.clonecraft.network.Handler12BuildSchematic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

public class SchematicBuilder {
	
	public Schematic schematic;
	
	public String name;
	
	public EntityPlayer sender;
	
	long lastUpdateTime = System.currentTimeMillis();
	
	int dataReceived = 0;
	
	int dataShouldReceive = 0;
	
	Handler12BuildSchematic buildOnceDone = null;
	
	public SchematicBuilder(Schematic schematic, String name, EntityPlayer sender)
	{
		this.schematic = schematic;
		this.name = name;
		this.sender = sender;
		dataShouldReceive = schematic.blockIds.length;
	}
	
	public boolean add(Handler11SendSchematic handler)
	{
		System.arraycopy(handler.ids, 0, schematic.blockIds, handler.offset, handler.ids.length);
		System.arraycopy(handler.data, 0, schematic.blockMetas, handler.offset, handler.data.length);
		
		lastUpdateTime = System.currentTimeMillis();
		
		dataReceived += handler.ids.length;
		
		if(handler.buildData != null)
		{
			this.buildOnceDone = handler.buildData;
		}
		
		if(handler.tileEntities != null)
		{
			schematic.loadTileEntities(handler.tileEntities);
		}
		
		System.out.println(String.format("%d/%d Received", dataReceived, dataShouldReceive));
		
		if(dataReceived < dataShouldReceive)
		{
			//The schematic has not been fully downloaded. 
			return false;
		}
		
		return true;
	}
	
	/**
	 * Called once the schematic has been loaded, build, saved, and stored.
	 */
	public void schematicFinalized()
	{
		if(this.buildOnceDone != null)
		{
			this.buildOnceDone.build(sender, null);
		}
		else
		{
			sender.addChatMessage(
					new ChatComponentText("No build data was sent. Please try building again.")
					.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
		}
	}
	
	/**
	 * Discard if no new data has been received for over 60 seconds.
	 * @return Boolean true if it should be discarded.
	 */
	public boolean shouldDiscard()
	{
		return this.lastUpdateTime + 60000 < System.currentTimeMillis();
	}

}
