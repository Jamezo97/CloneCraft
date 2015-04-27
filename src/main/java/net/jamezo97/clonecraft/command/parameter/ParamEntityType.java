package net.jamezo97.clonecraft.command.parameter;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.clone.EntityClone;
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
	public ParamGuess[] findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		SimpleList<ParamGuess> guesses = new SimpleList<ParamGuess>();
		for(int a = 0; a < words.length; a++)
		{
			String word = words[a];
			
			for(int b = 0; b < entries.size(); b++)
			{
				KeywordToClass entry = entries.get(b);
				
				for(int c = 0; c < entry.names.length; c++)
				{
					if(entry.names[c].equals(word))
					{
						guesses.add(new ParamGuess(entry.clazz, 0.5f));
					}
				}
			}
		}
		return guesses.size()==0?null:guesses.toArray(new ParamGuess[guesses.size()]);
	}
	
	private static ArrayList<KeywordToClass> entries = new ArrayList<KeywordToClass>();
	
	public static void registerKeyword(Class clazz, String... names){
		entries.add(new KeywordToClass(clazz, names));
	}
	
	static{
		registerKeyword(EntityPlayer.class, "me", "him", "her", "them");
		
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

	
}
