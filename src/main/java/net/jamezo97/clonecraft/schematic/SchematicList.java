package net.jamezo97.clonecraft.schematic;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.network.Handler11SendSchematic;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;

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
	
	public SchematicList(CloneCraft cloneCraft) {
		this.cloneCraft = cloneCraft;
		this.baseFolder = new File(cloneCraft.getDataDir(), "Schematics");
		if(!baseFolder.exists())
		{
			baseFolder.mkdirs();
		}
		reloadSchematics();
	}
	
	ArrayList<SchematicBuilder> builders = new ArrayList<SchematicBuilder>();
	
	ArrayList<Handler11SendSchematic> timedPackets = new ArrayList<Handler11SendSchematic>();
	
	
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
				builders.remove(a);
				a--;
			}
		}
		
		if(nextUpdate < System.currentTimeMillis())
		{
			reloadSchematics();
			nextUpdate = System.currentTimeMillis() + 10000;
		}
		
		//We're connected to ourselves, so the schematic folder will contains the same files, no need for synchronization
		if(MinecraftServer.getServer() instanceof IntegratedServer)
		{
			
		}
		else
		{
			
		}
	}
	
	/**
	 * 
	 * @param schem The schematic to send
	 * @param sendTo Who to send it to. If null, then it is sent to the client.
	 */
	public void sendSchematic(Schematic schem, EntityPlayerMP sendTo)
	{
		int SEGMENTSIZE = 1024;
		
		for(int a = 0; a < schem.blockIds.length;)
		{
			int length = Math.min(SEGMENTSIZE, schem.blockIds.length-a);
			
			Handler11SendSchematic handler;
			
			timedPackets.add(handler = new Handler11SendSchematic(schem, a, length));
			
			handler.sendTo = sendTo;
			
			a+= length;
		}
	}

	public void receiveData(Handler11SendSchematic handler, String name, String sender)
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
		}
	}
	
	public void schematicReceived(Schematic schem, String sender)
	{
		File saveTo = new File(baseFolder, sender + "/" + schem.name + ".schematic");
		
		if(!saveTo.getParentFile().exists())
		{
			saveTo.getParentFile().mkdirs();
		}
		
		//Don't use this. Overwrite old schematics instead. Because they are located within their own folders,
		//only the original uploader can modify them..
		/*int newCount = 1;
		
		while(saveTo.exists())
		{
			saveTo = new File(CloneCraft.INSTANCE.getDataDir(), "Schematics/" + sender + "/" + schem.name + "_" + (newCount++) + ".schematic");
		}*/
		
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
		
		for(int a = 0; a < schematics.size(); a++)
		{
			if(!schematics.get(a).fileLoc.exists())
			{
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
					File schemFile = schematicFiles.remove(b--);
					
					//Just in case if the server limits access to these files.
					try
					{
						//Check if the dates are different
						long oldDate = schematics.get(a).lastModified;
						long newDate = schemFile.lastModified();
						
						//If the file has changed.
						if(newDate > oldDate)
						{
							//Reload the schematic file.
							Schematic schem = Schematic.loadFrom(schematicFiles.get(b));
							
							if(schem != null)
							{
								schematics.get(a).lastModified = newDate;
								schematics.get(a).schem = schem;
							}
							
						}
					}
					catch(Exception e)
					{
						System.err.println("Could not gain access to file system to determine last modified date. :(");
						System.err.println("Schematic files won't refresh properly :(");
						e.printStackTrace();
					}
					
					continue;
				}
			}
		}
		
		//Now we have an array of schematic files, which are new. So let's load them!
		
		for(int a = 0; a < schematicFiles.size(); a++)
		{
			File theFile = schematicFiles.get(a);
			
			Schematic loadedSchem = Schematic.loadFrom(theFile);
			
			if(loadedSchem != null)
			{
				String localizedName = theFile.getAbsolutePath();
				localizedName = localizedName.substring(this.baseFolder.getAbsolutePath().length()+1);//Remove the "C:\\Users/AppData.../CloneCraft/Schematics/" part
				localizedName = localizedName.substring(0, localizedName.length()-10);//Remove the .schematic at the end
				loadedSchem.name = localizedName;
				
				long modified = 0;
				
				try
				{
					modified = theFile.lastModified();
				}
				catch(Exception e)
				{
					System.err.println("Could not determine last modified date. Schematics may not update correctly due to your systems limitations");
					e.printStackTrace();
				}
				
				SchematicEntry entry;
				
				this.schematics.add(entry = new SchematicEntry(loadedSchem, theFile, modified));
				System.out.println("Loaded: " + entry);
			}
		}
		
		Collections.sort(schematics);
	}
	
	

}
