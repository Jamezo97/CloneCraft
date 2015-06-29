package net.minecraft.server;

import java.io.File;

public class MinecraftServerAccessor {
	
	public static File getDataDirectory(MinecraftServer server)
	{
		return server.getDataDirectory();
	}

}
