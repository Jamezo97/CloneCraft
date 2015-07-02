package net.jamezo97.clonecraft;

import java.io.File;

import net.minecraftforge.common.config.Configuration;

public class CConfig {
	
	public boolean OWNERS_ENABLED, DEBUG_ENABLED, LIFEINDUCER_EXPLODE;
	
	public int SYNC_FREQUENCY, SYNC_FORCE;
	
	
	public String ID_NEEDLE;
	public String ID_TESTTUBE;
	public String ID_EMPTYEGG;
	public String ID_SPAWNEGG;
	public String ID_GROWBALL;
	public String ID_STERILIZER;
	public String ID_CENTRIFUGE;
	public String ID_LIFEINDUCER;
	public String ID_ANTENNA;

	public String ID_ENTITY_CLONE;
	public String ID_ENTITY_SPAWNEGG;
	
	public CConfig(File file){
		
		Configuration c = new Configuration(file);
		
		c.load();
		
		OWNERS_ENABLED = c.get("Options", "Owners Enabled", false, "Allow clones to be owned by players (and thus unusable by others)").getBoolean(false);
		DEBUG_ENABLED = c.get("Options", "Debug Enabled", false, "Faster processes, extra commands. Don't enable unless debugging").getBoolean(false);
		
		LIFEINDUCER_EXPLODE = c.get("Options", "Life Inducer Damage Blocks", false, "If set to true, when a life inducer is broken without being discharged, the explosion will also damage blocks.").getBoolean(false);
		
		SYNC_FREQUENCY = c.get("Options", "Sync Frequency", 2, "How often clones are checked for changes and synced between server and client. A higher values reduces the frequency").getInt(2);
		
		SYNC_FORCE = c.get("Options", "Sync Force Timer", 0, "How many ticks before Clones have all values forcibly synchronized (0 = Off, 200 = Every 10 seconds)").getInt(-1);
		
		
		c.setCategoryComment("Item/Block IDs", "If another mod just so happens to use one of these strings, then feel free to change these. However keep in mind that this will"
				+ " cause itmes/blocks that are currently in the world with these id's to dissapear if you modify it here. Also, you might not be able to connect to a "
				+ "server either.. So keep it in mind. Also anything you rename here, may need to be renamed in the en_US.lang file in the assets/clonecraft/lang folder of this mod.");
		
		ID_NEEDLE = c.get("Item/Block IDs", "NeedleID", "ccNeedle").getString();
		ID_TESTTUBE = c.get("Item/Block IDs", "TestTubeID", "ccTestTube").getString();
		ID_EMPTYEGG = c.get("Item/Block IDs", "EmptyEggID", "ccEmptyEgg").getString();
		ID_SPAWNEGG = c.get("Item/Block IDs", "SpawnEggID", "ccSpawnEgg").getString();
		ID_GROWBALL = c.get("Item/Block IDs", "GrowBallID", "ccGrowBall").getString();
		ID_STERILIZER = c.get("Item/Block IDs", "SterilizerID", "ccSterilizer").getString();
		ID_CENTRIFUGE = c.get("Item/Block IDs", "CentrifugeID", "ccCentrifuge").getString();
		ID_LIFEINDUCER = c.get("Item/Block IDs", "LifeInducerID", "ccLifeInducer").getString();
		ID_ANTENNA = c.get("Item/Block IDs", "AntennaID", "ccAntenna").getString();
		

		ID_ENTITY_CLONE = c.get("Entity IDs", "EntityCloneID", "ccEntityClone").getString();
		ID_ENTITY_SPAWNEGG = c.get("Entity IDs", "EntitySpawnEggID", "ccEntitySpawnEgg").getString();
		c.save();
		
	}
	
	public boolean areOwnersEnabled(){
		return OWNERS_ENABLED;
	}

}
