package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import net.jamezo97.clonecraft.build.BlockItemRegistry;
import net.jamezo97.clonecraft.chunktricks.*;

public class Handler14RequestBlockItemMapping extends Handler
{
	
	public static final Random rand = new Random();
	String blockString = "";
	String itemString = "";
	
	long securityToken = 0;

	public Handler14RequestBlockItemMapping()
	{
		
	}
	
	public Handler14RequestBlockItemMapping(Block... blocksFor)
	{
		this.setBlocks(blocksFor);
	}
	
	public Handler14RequestBlockItemMapping setBlocks(Block... blocks)
	{
		this.blockString = this.packBlocks(blocks);
		return this;
	}
	public Handler14RequestBlockItemMapping setItems(Item... items)
	{
		this.itemString = this.packItems(items);
		return this;
	}
	
	public String packBlocks(Block[] blocks)
	{
		if(blocks == null || blocks.length == 0){ return "";}
		
		String name = "";
		
		for(int a = 0; a < blocks.length; a++)
		{
			if(a != 0){name += "\n";}
			if(blocks[a] == null)
			{
				name += "";
			}
			else
			{
				name += Block.blockRegistry.getNameForObject(blocks[a]);
			}
		}
		
		return name;
	}
	
	public String packItems(Item[] items)
	{
		if(items == null || items.length == 0){ return "";}
		
		String name = "";
		
		for(int a = 0; a < items.length; a++)
		{
			if(a != 0){name += "\n";}
			if(items[a] == null)
			{
				name += "";
			}
			else
			{
				name += Item.itemRegistry.getNameForObject(items[a]);
			}
		}
		
		return name;
	}
	
	public Block[] unpackBlocks(String str)
	{
		if(str == null || str.length() == 0){return new Block[0];}
		
		String[] names = str.split("\n");
		
		Block[] blocks = new Block[names.length];
		
		for(int a = 0; a < names.length; a++)
		{
			if(names[a] != null && names[a].length() != 0)
			{
				blocks[a] = (Block)Block.blockRegistry.getObject(names[a]);
			}
		}
		
		return blocks;
	}
	
	public Item[] unpackItems(String str)
	{
		if(str == null || str.length() == 0){return new Item[0];}
		
		String[] names = str.split("\n");
		
		Item[] items = new Item[names.length];
		
		for(int a = 0; a < names.length; a++)
		{
			if(names[a] != null && names[a].length() != 0)
			{
				items[a] = (Item)Item.itemRegistry.getObject(names[a]);
			}
		}
		
		return items;
	}


	@Override
	public void handle(Side side, EntityPlayer player)
	{
		Block[] blocks = this.unpackBlocks(blockString);
		Item[] items = this.unpackItems(itemString);
		if(side == Side.CLIENT)
		{
			handleClient(blocks, items);
		}
		else
		{
			System.out.println("Handling server block register");
			if(Handler14RequestBlockItemMapping.tokens.contains(securityToken))
			{
				Handler14RequestBlockItemMapping.tokens.remove((Object)securityToken);
				
				if(items != null && blocks != null && items.length == blocks.length)
				{
					for(int a = 0; a < blocks.length; a++)
					{
						if(BlockItemRegistry.needToClientSearch.contains(blocks[a]))
						{
							System.out.println("Registered " + blocks[a] + ", " + items[a]);
							BlockItemRegistry.registerBlockItem(blocks[a], items[a]);
							BlockItemRegistry.needToClientSearch.remove(blocks[a]);
						}
					}
				}
			}
			else
			{
				//Token has expired.
			}
		}
	}
	
	public void handleClient(Block[] blocks, Item[] items)
	{
		System.out.println("Finding client blocks " + blocks.length);
		items = new Item[blocks.length];
		try
		{
			FakeSmallWorld world = new FakeSmallWorld();
			FakePlayer player = new FakePlayer(world);
			
			for(int a = 0; a < blocks.length; a++)
			{
				try
				{
					Block block = blocks[a];
					
					if(block == null)
					{
						continue;
					}
					
					world.setBlock(0, 70, 0, block, 0, 0);
					
					Item item = block.getItem(world, 0, 70, 0);
				
					if(item != null)
					{
						System.out.println("Found: " + block + ", " + item);
						items[a] = item;
					}
					
					world.resetWorld();
				}
				catch(Throwable t){}
			}
		}
		catch(Throwable t){}
		
		this.setBlocks(blocks);
		this.setItems(items);
		
		this.sendToServer();
		
	}

	@Override
	public void read(ByteBuf buf)
	{
		this.blockString = this.readUTF(buf);
		this.itemString = this.readUTF(buf);
		this.securityToken = buf.readLong();
	}

	@Override
	public void write(ByteBuf buf)
	{
		this.writeUTF(buf, blockString);
		this.writeUTF(buf, itemString);
		buf.writeLong(this.securityToken);
	}
	
	public void addToken(long token, EntityPlayerMP player)
	{
		for(int a = 0; a < tokens.size(); a++)
		{
			if(tokens.get(a).time+30000 < System.currentTimeMillis() || tokens.get(a).token == token)
			{
				tokens.remove(a--);
			}
		}
		
		this.securityToken = token;
		
		this.tokens.add(new TokenEntry(token,player));
	}
	
	private static ArrayList<TokenEntry> tokens = new ArrayList<TokenEntry>();
	
	public static class TokenEntry
	{
		public final long time;
		
		public final long token;
		
		public final EntityPlayerMP player;
		
		public TokenEntry(long token, EntityPlayerMP player)
		{
			this.token = token;
			this.player = player;
			this.time = System.currentTimeMillis();
		}

		@Override
		public boolean equals(Object obj)
		{
			if(obj instanceof Long)
			{
				return (Long)obj == token;
			}
			if(obj instanceof TokenEntry)
			{
				return ((TokenEntry)obj).token == token;
			}
			
			return false;
		}
	}
	
	
	
	
	
	
	static
	{
		
	}
	
	
	
}
