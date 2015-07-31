package net.jamezo97.clonecraft.build;

import java.util.ArrayList;

import scala.actors.threadpool.Arrays;
import net.minecraft.block.Block;

public class Mapping
{
	ArrayList<int[]> quads = new ArrayList<int[]>();
	
	private Block block;
	
	public Mapping(Block block)
	{
		this.block = block;
	}
	
	public boolean areSimilarButRotated(int meta1, int meta2)
	{
		for(int a = 0; a < quads.size(); a++)
		{
			int[] quad = quads.get(a);
			
			int found = 0;
			for(int b = 0; b < quad.length && found != 3; b++)
			{
				if(quad[b] == meta1)
				{
					found |= 1;
				}
				else if(quad[b] == meta2)
				{
					found |= 2;
				}
			}
			
			if(found == 3)
			{
				return true;
			}
		}
		return false;
	}
	
	
	public boolean doesClash(int[] metas) 
	{
		
		/*String s = "";
		for(int a = 0; a < quads.size(); a++)
		{
			
			if(a != 0)
			{
				s += ", ";
			}
			
			s += "{";
			for(int b = 0; b < quads.get(a).length; b++)
			{
				if(b != 0)
				{
					s += ", ";
				}
				s += quads.get(a)[b];
			}

			s += "}";
		}
		System.out.println(s);
		System.out.println(Arrays.toString(metas));*/
		
		for(int a = 0; a < quads.size(); a++)
		{
			for(int b = 0; b < quads.get(a).length; b++)
			{
				for(int c = 0; c < metas.length; c++)
				{
					if(quads.get(a)[b] == metas[c])	
					{
						return true;
					}
				}
			}
		}
		
		
		
		
		return false;
	}

	public boolean addQuad(int... metas)
	{
		boolean clashed = false;
		if(metas != null && metas.length == 4)
		{
			for(int a = 0; a < quads.size(); a++)
			{
				for(int b = 0; a >= 0 && a < quads.size() && b < quads.get(a).length; b++)
				{
					for(int c = 0; c < metas.length; c++)
					{
						if(quads.get(a)[b] == metas[c])	
						{
							quads.remove(a);
							b = 100;
							a--;
							System.out.println("Clashed. Removing clash.");
							clashed = true;
							break;
						}
					}
				}
			}
			
			quads.add(metas);
		}
		
		/*String s = "";
		for(int a = 0; a < quads.size(); a++)
		{
			
			if(a != 0)
			{
				s += ", ";
			}
			
			s += "{";
			for(int b = 0; b < quads.get(a).length; b++)
			{
				if(b != 0)
				{
					s += ", ";
				}
				s += quads.get(a)[b];
			}

			s += "}";
		}
		System.out.println("AAA" + s);*/
		
		return !clashed;
	}
	
	/**
	 * Translates the given block meta data to the correct value for the given schematic rotation
	 * @param currentMeta The meta data of the block to be placed with no rotation
	 * @param rotate The amount of 90 degree rotations, clockwise when viewed from above
	 * @return The new block meta data which represents a correctly rotated block.
	 */
	public int map(int currentMeta, int rotate)
	{
		for(int a = 0; a < quads.size(); a++)
		{
			int[] quad = quads.get(a);
			
			for(int b = 0; b < quad.length; b++)
			{
				if(quad[b] == currentMeta)
				{
					int c = b + rotate;
					
					if(c >= quad.length)
					{
						c -= quad.length;
					}
					
					if(c < quad.length)
					{
						return quad[c];
					}
				}
			}
		}
		
		return currentMeta;
	}

	
	
}
