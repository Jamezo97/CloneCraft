package net.jamezo97.clonecraft;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class CConfig {
	
	public boolean OWNERS_ENABLED, DEBUG_ENABLED;
	
	public int SYNC_FREQUENCY, SYNC_FORCE;
	
	public CConfig(File file){
		
		Configuration c = new Configuration(file);
		
		c.load();
		
		OWNERS_ENABLED = c.get("Options", "Owners Enabled", false, "Allow clones to be owned by players (and thus unusable by others)").getBoolean(false);
		DEBUG_ENABLED = c.get("Options", "Debug Enabled", false, "Faster processes, extra commands. Don't enable unless debugging").getBoolean(false);
		
		SYNC_FREQUENCY = c.get("Options", "Sync Frequency", 2, "How often clones are checked for changes and synced between server and client. A higher values reduces the frequency").getInt(2);
		
		SYNC_FORCE = c.get("Options", "Sync Force Timer", 0, "How many ticks before Clones have all values forcibly synchronized (0 = Off, 200 = Every 10 seconds)").getInt(-1);
		
		c.save();
		
	}
	
	public boolean areOwnersEnabled(){
		return OWNERS_ENABLED;
	}

}
