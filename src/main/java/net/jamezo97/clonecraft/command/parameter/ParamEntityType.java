package net.jamezo97.clonecraft.command.parameter;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.jamezo97.util.SimpleList;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Returns an entity type. i.e. Creeper, Skeleton, Player. 
 * But not the actual entity in the world. Just the class type..
 * @author James
 *
 */
public class ParamEntityType extends Parameter{

	@Override
	public PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		PGuess guesses = new PGuess(this);
		for(int a = 0; a < words.length; a++)
		{
			String word = words[a];
			
			for(int b = 0; b < entries.size(); b++)
			{
				KeywordToClass entry = entries.get(b);
				
				for(int c = 0; c < entry.names.length; c++)
				{
					if(WordSet.areWordsSimilar(entry.names[c], word, true))
					{
						guesses.add(new ParamGuess(entry.clazz, 0.5f));
					}
				}
			}
		}
		return guesses;
	}
	
	private static ArrayList<KeywordToClass> entries = new ArrayList<KeywordToClass>();
	
	public static void registerKeyword(Class clazz, String... names){
		entries.add(new KeywordToClass(clazz, names));
	}
	
	static{
		registerKeyword(EntityPlayer.class, "me", "him", "her", "them");
		registerKeyword(EntityClone.class, "yourself");
		for(Object objEntry : EntityList.stringToClassMapping.entrySet())
		{
			if(objEntry instanceof Entry)
			{
				Entry entry = (Entry<String,Class>)objEntry;
				
				String name = (String)entry.getKey();
				Class clazz = (Class)entry.getValue();
				
				registerKeyword(clazz, name.toLowerCase());
			}
		}
	}
	
	public static class KeywordToClass{
		
		public Class clazz;
		
		public String[] names;
		
		public KeywordToClass(Class clazz, String[] names){
			this.clazz = clazz;
			this.names = names;
		}
		
	}

	@Override
	public String getDefaultAskString() {
		return "What kind of entity? (me, you, creeper etc)";
	}

	
}
