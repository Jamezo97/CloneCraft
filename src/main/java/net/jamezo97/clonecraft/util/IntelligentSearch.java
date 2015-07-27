package net.jamezo97.clonecraft.util;

import java.util.ArrayList;
import java.util.Collections;

import net.minecraft.entity.Entity;

/**
 * Intelligently searches around an entity for blocks.
 * If the entity is moving, it searches in a slab like pattern, perpendicular to the direction of movement.
 * If the entity is stationary, it searches in all directions outwards, expanding the radius each time.
 * @author James
 *
 */
public class IntelligentSearch {
	
	Entity entity = null;
	
	int stationaryRadius, walkingRadius;
	
	float speedCutoff;
	
	BlockHandler handler;
	
	/**
	 * 
	 * @param entity The entity to follow
	 * @param searchRadius The radius to search around the entity.
	 * @param tickRate How many times this will be called every second
	 */
	public IntelligentSearch(Entity entity, int stationaryRadius, int walkingRadius, BlockHandler handler)
	{
		this.entity = entity;
		this.stationaryRadius = stationaryRadius;
		this.walkingRadius = walkingRadius;
		this.handler = handler;
		
		ensureRadiusCalculations(stationaryRadius);
	}
	
	long nextSearch = 0;
	long nextImmediateSearch = 0;
	
	int lastX = 0, lastY = 0, lastZ = 0;
	
	int movingTicks = 0;
	
	int currentIndex = 0;
	
	public void search()
	{
		if(handler == null)
		{
			return;
		}
		
		int posX = (int)Math.floor(entity.posX);
		int posY = (int)Math.floor(entity.posY);
		int posZ = (int)Math.floor(entity.posZ);
		
//		double motion = entity.motionX * entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ;
		
//		System.out.println(motion);
		
		//If we're moving.
		if(posX != lastX || posY != lastY || posZ != lastZ)
		{

			currentIndex = 0;
			
			nextSearch = System.currentTimeMillis() + 5000;
			
			if(movingTicks++ > 0)
			{
				
				
				//Unsigned
				int dXU = Math.abs(posX - lastX);
				int dYU = Math.abs(posY - lastY);
				int dZU = Math.abs(posZ - lastZ);
				
				int MAXSEARCH = 3;
				
				if(dXU + dYU + dZU > MAXSEARCH)
				{
					//Do something else
//					System.out.println("Entity moved too fast. Skipping inbetween block search.");
				}
				else
				{
					int startX = lastX;
					int startY = lastY;
					int startZ = lastZ;
					
					//Signed values
					int dXS = (int)Math.signum(posX - lastX);
					int dYS = (int)Math.signum(posY - lastY);
					int dZS = (int)Math.signum(posZ - lastZ);
					
					for(int a = 0; a < MAXSEARCH && dXU + dYU + dZU != 0; a++)
					{
						int xRadius = 0;
						int yRadius = 0;
						int zRadius = 0;
						
						switch(a%3)
						{
							case 0: 
							{
								if(dXU != 0)
								{
									yRadius = walkingRadius;
									zRadius = walkingRadius;
									dXU--;
									startX += dXS;
								}
								break;
							}
							
							case 1: 
							{
								if(dYU != 0)
								{
									xRadius = walkingRadius;
									zRadius = walkingRadius;
									dYU--;
									startY += dYS;
								}
								break;
							}
							case 2: 
							{
								if(dZU != 0)
								{
									xRadius = walkingRadius;
									yRadius = walkingRadius;
									dZU--;
									startZ += dZS;
								}
								break;
							}
						}
						
						if(xRadius + yRadius + zRadius == 0)
						{
							if(dXU + dYU + dZU == 0)
							{
								break;
							}
							continue;
						}
						
						int maxX = startX + xRadius;
						int maxY = startY + yRadius;
						int maxZ = startZ + zRadius;
						
						for(int y = startY - yRadius; y <= maxY; y++)
						{
							for(int z = startZ - zRadius; z <= maxZ; z++)
							{
								for(int x = startX - xRadius; x <= maxX; x++)
								{
									handler.handleBlock(x, y, z);
								}
							}
						}
					}
				}
			}
		}
		else
		{
			
			
			
			if(System.currentTimeMillis() > this.nextImmediateSearch)
			{
				movingTicks = 0;
				
				int MAX = 512;
				
				for(int a = 0; a < MAX; a++, a++)
				{
					handler.handleBlock(posX + offsets[stationaryRadius][a][0], 
										posY + offsets[stationaryRadius][a][1], 
										posZ + offsets[stationaryRadius][a][2]);
				}
				
				this.nextImmediateSearch = System.currentTimeMillis() + 2000;
			}
			else if(System.currentTimeMillis() > this.nextSearch)
			{
				movingTicks = 0;
				
				int MAX = Math.min(150, offsets[stationaryRadius].length - currentIndex);

				for(int a = 0; a < MAX; a++, currentIndex++)
				{
					handler.handleBlock(posX + offsets[stationaryRadius][currentIndex][0], 
										posY + offsets[stationaryRadius][currentIndex][1], 
										posZ + offsets[stationaryRadius][currentIndex][2]);
				}
				
				if(currentIndex >= offsets[stationaryRadius].length)
				{
					currentIndex = 0;
					this.nextSearch = System.currentTimeMillis() + 10000;
				}
			}
		}
		
		lastX = posX;
		lastY = posY;
		lastZ = posZ;
	}
	


	static int[][][] offsets = new int[512][][];
	
	
	public static void ensureRadiusCalculations(int radius)
	{
		if(radius > 511)
		{
			throw new RuntimeException("Radius must be less than 512");
		}
		
		if(offsets[radius] == null)
		{
			ArrayList<Coord> coords = new ArrayList<Coord>();
			
			int radiusSq = radius * radius;
			
			for(int y = -radius; y <= radius; y++)
			{
				for(int z = -radius; z <= radius; z++)
				{
					for(int x = -radius; x <= radius; x++)
					{
						Coord coord = new Coord(x, y, z);
						if(coord.distSq <= radiusSq)
						{
							coords.add(new Coord(x, y, z));
						}
					}
				}
			}
			
			Collections.sort(coords);
			
			int[][] offset = new int[coords.size()][3];
			
			for(int a = 0; a < coords.size(); a++)
			{
				Coord c = coords.get(a);
				offset[a][0] = c.x;
				offset[a][1] = c.y;
				offset[a][2] = c.z;
			}
			
			offsets[radius] = offset;
			
		}
	}
	
	private static class Coord implements Comparable<Coord>
	{
		
		int distSq;
		
		int x;
		int y;
		int z;
		
		public Coord(int x, int y, int z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.distSq = x*x + y*y + z*z;
		}
		
		public int getDistSq()
		{
			return distSq;
		}

		@Override
		public int compareTo(Coord o)
		{
			return Integer.compare(distSq, o.distSq);
		}
		
		
		
	}
	
	
	

}
