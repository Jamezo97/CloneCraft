package net.jamezo97.clonecraft.schematic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.network.Handler;
import net.jamezo97.clonecraft.network.Handler11SendSchematic;
import net.jamezo97.clonecraft.network.Handler12BuildSchematic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

/**
 * Helps bridge the gap between server and client. Can be used to send/receive schematic files
 * between the client and server, by splitting them up into smaller packets
 * and then recompiling them back on the other side.
 * @author James
 *
 */
public class SchematicList {

	CloneCraft cloneCraft;
	
	File baseFolder;
	
	public SchematicList(CloneCraft cloneCraft)
	{
		this.cloneCraft = cloneCraft;
		this.baseFolder = new File(cloneCraft.getDataDir(), "Schematics");
		if(!baseFolder.exists())
		{
			baseFolder.mkdirs();
		}
		reloadSchematics();
	}
	
	ArrayList<SchematicBuilder> builders = new ArrayList<SchematicBuilder>();
	
	ArrayList<Handler> timedPackets = new ArrayList<Handler>();
	
	
	long nextUpdate = System.currentTimeMillis()+10000;
	
	public void onUpdate()
	{
		if(!timedPackets.isEmpty())
		{
			for(int a = 0; a < 4 && a < timedPackets.size(); a++)
			{
				timedPackets.remove(a).doSend();
			}
		}
		
		//Discard old builders
		for(int a = 0; a < builders.size(); a++)
		{
			if(builders.get(a).shouldDiscard())
			{
				SchematicBuilder builder = builders.remove(a);
				a--;
				
				if(builder.sender != null)
				{
					builder.sender.addChatMessage(
							new ChatComponentText("Full Schematic File was not received. Aborting build.")
							.setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				}
			}
		}
		
		if(nextUpdate < System.currentTimeMillis())
		{
			reloadSchematics();
			//nextUpdate is updated in the reloadSchematics method
		}
	}
	
	/**
	 * 
	 * @param schem The schematic to send
	 * @param sendTo Who to send it to. If null, then it is sent to the client.
	 * @param The final Packet to send once the rest have been sent.
	 */
	public void sendSchematic(Schematic schem, EntityPlayerMP sendTo, Handler12BuildSchematic build)
	{
		
		//32Kilobytes Max
		//Say 30 Kilobytes. Two arrays of shorts. Makes 4 Bytes per block.
		//30 / 4 = 7.5K. So max of ~ 7500
		
		int SEGMENTSIZE = 7000;
		
		int segmentTotal = schem.blockIds.length / SEGMENTSIZE + 1;
		
		int segment = 0;
		
		for(int a = 0; a < schem.blockIds.length;)
		{
			int length = Math.min(SEGMENTSIZE, schem.blockIds.length-a);
			
			Handler11SendSchematic handler;
			
			timedPackets.add(handler = new Handler11SendSchematic(schem, a, length, ++segment, segmentTotal));
			
			handler.archiveRecipient(sendTo);
			
			if(a == 0)
			{
				handler.loadInitialData(build);
				handler.tileEntities = schem.tileEntities.toArray(new NBTTagCompound[schem.tileEntities.size()]);
			}
			
			a += length;
		}
	}

	public void receiveData(Handler11SendSchematic handler, String name, EntityPlayer sender)
	{
		SchematicBuilder builder = null;
		
		for(int a = 0; a < builders.size(); a++)
		{
			if(builders.get(a).sender.equals(sender) && builders.get(a).name.equals(name))
			{
				builder = builders.get(a);
				break;
			}
		}
		
		if(builder == null)
		{
			builder = new SchematicBuilder(new Schematic(name, handler.xSize, handler.ySize, handler.zSize), name, sender);
			builders.add(builder);
		}
		
		//If the builder has finished building the schematic
		if(builder.add(handler))
		{
			builders.remove(builder);
			
			Schematic schem = builder.schematic;
			
			schematicReceived(schem, builder.sender);
			
			builder.schematicFinalized();
		}
	}
	
	public void schematicReceived(Schematic schem, EntityPlayer sender)
	{
		File saveTo = new File(baseFolder, sender.getCommandSenderName() + "/" + schem.name + ".schematic");
		
		if(!saveTo.getParentFile().exists())
		{
			saveTo.getParentFile().mkdirs();
		}
		
		schem.saveTo(saveTo);
		
		reloadSchematics();
	}
	
	ArrayList<SchematicEntry> schematics = new ArrayList<SchematicEntry>();
	
	public ArrayList<SchematicEntry> getSchematics()
	{
		return schematics;
	}
	
	public void reloadSchematics()
	{
		//First, remove all schematics that no longer exist on the HDD

		nextUpdate = System.currentTimeMillis() + 30000;
		
		long l1 = System.currentTimeMillis();
		
		for(int a = 0; a < schematics.size(); a++)
		{
			if(!schematics.get(a).fileLoc.exists())
			{
				System.out.println("Deleted Schematic: " + schematics.get(a).schem.name);
				schematics.remove(a--).schem.delete();
			}
		}
		
		
		//Now load all of the current schematics on the hard disk.
		ArrayList<File> schematicFiles = new ArrayList<File>();
		
		ArrayList<File> searchFolders = new ArrayList<File>();
		searchFolders.add(baseFolder);
		
		while(!searchFolders.isEmpty())
		{
			File[] fileList = searchFolders.remove(0).listFiles();
			
			for(int a = 0; a < fileList.length; a++)
			{
				if(fileList[a].isDirectory())
				{
					searchFolders.add(fileList[a]);
				}
				else
				{
					if(fileList[a].getName().toLowerCase().endsWith(".schematic"))
					{
						schematicFiles.add(fileList[a]);
					}
				}
			}
		}
		
		//Now check if existing loaded schematics have been updated on file:
		
		for(int a = 0; a < schematics.size(); a++)
		{
			for(int b = 0; b < schematicFiles.size(); b++)
			{	
				if(schematics.get(a).fileLoc.equals(schematicFiles.get(b)))
				{
					//Remove the file, as we won't be using it later on because we handle it here.
					File schemFile = schematicFiles.get(b);
					
					//Just in case if the server limits access to these files.
					try
					{
						//Check if the dates are different
						long oldDate = schematics.get(a).lastModified;
						long newDate = schemFile.lastModified();
						
						//If the file has changed.
						if(newDate > oldDate)
						{
							System.out.println("Updated Schematic: " + schematics.get(a).schem.name);
							//Reload the schematic file.
							Schematic schem = Schematic.loadFrom(schematicFiles.get(b));
							
							if(schem != null)
							{
								schematics.get(a).lastModified = newDate;
								schematics.get(a).schem.delete();
								schematics.get(a).schem = schem;
								System.out.println("Updated " + schem);
							}
							else
							{
								System.out.println("New schematic file could not load. Deleting schematic from list.");
								schematics.remove(a--).schem.delete();
							}
						}
					}
					catch(Exception e)
					{
						System.err.println("Could not gain access to file system to determine last modified date. :(");
						System.err.println("Schematic files won't refresh properly :(");
						e.printStackTrace();
					}
					schematicFiles.remove(b--);
					continue;
				}
			}
		}
		
		//Now we have an array of schematic files, which are new. So let's load them!
		
		boolean loaded = false;
		
		for(int a = 0; a < schematicFiles.size(); a++)
		{
			loaded = true;
			File theFile = schematicFiles.get(a);
			
			Schematic loadedSchem = Schematic.loadFrom(theFile);
			
			if(loadedSchem != null)
			{
				loadedSchem.name = getSchematicNameFromFile(theFile);
				
				long modified = 0;
				
				try
				{
					modified = theFile.lastModified();
				}
				catch(Exception e)
				{
					System.err.println("Could not determine last modified date. Schematics may not update correctly due to your system's limitations");
					e.printStackTrace();
				}
				
				SchematicEntry entry;
				
				this.schematics.add(entry = new SchematicEntry(loadedSchem, theFile, modified));
				System.out.println("Loaded: " + entry);
			}
		}
		
		Collections.sort(schematics);
		
		long l2 = System.currentTimeMillis();
		
		if(loaded)
		{
			System.out.println("Loaded " + this.schematics.size() + " schematics in " + (l2-l1) + "ms");
		}
		
	}
	
	public String getSchematicNameFromFile(File schematicFile)
	{
		String localizedName = schematicFile.getAbsolutePath();
		localizedName = localizedName.substring(this.baseFolder.getAbsolutePath().length()+1);//Remove the "C:\\Users/AppData.../CloneCraft/Schematics/" part
		localizedName = localizedName.substring(0, localizedName.length()-10);//Remove the .schematic at the end
		return localizedName;
	}
	
	public SchematicEntry getSchematic(long schematicHash, int xSize, int ySize, int zSize) {
		
		for(int a = 0; a < schematics.size(); a++)
		{
			Schematic schem = schematics.get(a).schem;
			
			//It's the same schematic!
			if(schem.xSize == xSize && schem.ySize == ySize && schem.zSize == zSize && schem.myHashCode() == schematicHash)
			{
				return schematics.get(a);
			}
		}
		
		return null;
	}

	/**
	 * Deletes any Buffers stored on the GPU which are no longer needed. However if the schematic is rendered again
	 * the buffers will be reloaded.
	 */
	public void cleanSchematics()
	{
		for(int a = 0; a < this.schematics.size(); a++)
		{
			SchematicEntry schemEntry = this.schematics.get(a);
			schemEntry.schem.cleanGPU();
		}
	}

	
	
	

}
