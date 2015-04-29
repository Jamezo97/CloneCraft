package net.jamezo97.clonecraft.command.parameter;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;

public class ParamEntity extends Parameter{

	WordSet wsClone = new WordSet("you", "yourself");
	WordSet wsPlayer = new WordSet("myself", "me");
	
	@Override
	public PGuess findParameters(final EntityClone clone, EntityPlayer sender, String[] words) {
		PGuess pguess = new PGuess(this);
		Map<?, ?> map = EntityList.stringToClassMapping;
		
		AxisAlignedBB aabb = clone.boundingBox.expand(32, 24, 32);
		
		List search = clone.worldObj.getEntitiesWithinAABBExcludingEntity(clone, aabb);
		
		Collections.sort(search, new Comparator(){

			@Override
			public int compare(Object o1, Object o2) {
				
				if(o1 == null && o2 == null)
				{
					return 0;
				}
				else if(o1 != null && o2 != null)
				{
					if(Entity.class.isAssignableFrom(o1.getClass()) && Entity.class.isAssignableFrom(o2.getClass()))
					{
						double d1 = clone.getDistanceSqToEntity(((Entity)o1));
						double d2 = clone.getDistanceSqToEntity(((Entity)o2));
						
						if(d1 > d2){
							return 1;
						}else if(d1 < d2){
							return -1;
						}else{
							return 0;
						}
					}
				}
				return -1;
			}
			
		});
		
		for(Entry<?, ?> entry : map.entrySet())
		{
			String name = ((String)entry.getKey()).toLowerCase();
			
			Class clazz = ((Class)entry.getValue());
			
			for(int a = 0; a < words.length; a++)
			{
				if(words[a].toLowerCase().equals(name))
				{
					for(int b = 0; b < search.size(); b++)
					{
						if(clazz.isAssignableFrom(search.get(b).getClass()))
						{
							pguess.add(new ParamGuess(search.get(b), 0.6f));
						}
					}
				}
			}
		}
		
		if(wsClone.containsWord(words) != -1)
		{
			pguess.add(new ParamGuess(clone, 0.4f));
		}
		
		if(wsPlayer.containsWord(words) != -1)
		{
			pguess.add(new ParamGuess(sender, 0.4f));
		}
		
		return pguess;
	}

	@Override
	public String getDefaultAskString() {
		return "What kind of Entity? (me, you, Jamezo97, skeleton etc)";
	}

	
	
}
