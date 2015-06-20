package net.jamezo97.clonecraft.schematic;

import net.jamezo97.clonecraft.network.Handler11SendSchematic;

public class SchematicBuilder {
	
	public Schematic schematic;
	
	public String name;
	
	public String sender;
	
	long lastUpdateTime = System.currentTimeMillis();
	
	int dataReceived = 0;
	
	int dataShouldReceive = 0;
	
	public SchematicBuilder(Schematic schematic, String name, String sender)
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
		
		if(dataReceived < dataShouldReceive)
		{
			//The schematic has not been fully downloaded. 
			return false;
		}
		
		return true;
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
