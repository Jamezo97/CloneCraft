package net.jamezo97.clonecraft.clone.mine;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.mine.RayTrace.Collision;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

public class RayTrace {
	
	
	/**
	 * Ray traces Blocks, and returns all collisions between the two points.
	 * @param v1 The Vector to start tracing from
	 * @param v2 The Vector to trace to
	 */
	public static ChunkCoordinates[] rayTraceBlocks(Vector v1, Vector v2){
		Vector directionVector = v2.subtract(v1);
		//Length of 1 unit.
		Vector unitDirectionVector = directionVector.toUnitVector();
		
		//For every one unit of distance traveled, this is how much x, y and z change.
		double xComp = unitDirectionVector.x;
		double yComp = unitDirectionVector.y;
		double zComp = unitDirectionVector.z;
		
		double unitPerX = xComp==0?Double.MAX_VALUE:1/xComp;
		double unitPerY = yComp==0?Double.MAX_VALUE:1/yComp;
		double unitPerZ = zComp==0?Double.MAX_VALUE:1/zComp;
		
		int xSign = xComp >= 0?1:-1;
		int ySign = yComp >= 0?1:-1;
		int zSign = zComp >= 0?1:-1;
		
		//Starting Positions
		double x = v1.x;
		double y = v1.y;
		double z = v1.z;
		
		ArrayList<ChunkCoordinates> hitBlocks = new ArrayList<ChunkCoordinates>();
		
		double maxAddedUnits = directionVector.getLength();
		
		double addedUnits = 0;
		
		int maxIterations = 5000;
		
		double nextWholeX, nextWholeY, nextWholeZ;
		
		double dxu, dyu, dzu;
		
		double du;
		
		int xCoord, yCoord, zCoord;
		
		ChunkCoordinates c1, c2;
		//Perform the opposite operations to begin with
		nextWholeX = xSign==1?Math.ceil(x):Math.floor(x);
		nextWholeY = ySign==1?Math.ceil(y):Math.floor(y);
		nextWholeZ = zSign==1?Math.ceil(z):Math.floor(z);
		
		boolean contains;
		
//		boolean allAreWhole = false;
		
		while(maxIterations-- > 0)
		{
//			nextWholeX = xSign==1?Math.ceil(x):Math.floor(x);
//			if(nextWholeX == x){nextWholeX += xSign;}
//			nextWholeY = ySign==1?Math.ceil(y):Math.floor(y);
//			if(nextWholeY == y){nextWholeY += ySign;}
//			nextWholeZ = zSign==1?Math.ceil(z):Math.floor(z);
//			if(nextWholeZ == z){nextWholeZ += zSign;}
			
//			double dx = nextWholeX - x;
//			double dy = nextWholeY - y;
//			double dz = nextWholeZ - z;
			
			//System.out.println(dx + ", " + xSign + "," + (nextWholeX == x));
			
			//Change in the unit vector for the components to move the amount they need to reach
			//the next wholeNumber
			dxu = (nextWholeX - x) * unitPerX;
			dyu = (nextWholeY - y) * unitPerY;
			dzu = (nextWholeZ - z) * unitPerZ;
			
//			int theNextType = dxu<dyu?(dxu<dzu?0:2):(dyu<dzu?1:2);
			
			//Change in unit vector. We want the smallest change
			du = dxu<dyu?(dxu<dzu?dxu:dzu):(dyu<dzu?dyu:dzu);
			
			x += du * xComp;
			y += du * yComp;
			z += du * zComp;
			
			//System.out.println(addedUnits + ", " + maxAddedUnits);
			
			addedUnits += du;
			
			if(addedUnits >= maxAddedUnits){
				break;
			}
			
			xCoord = (int)Math.floor(x);
			yCoord = (int)Math.floor(y);
			zCoord = (int)Math.floor(z);
			
			
			if(xCoord == x)
			{
				nextWholeX += xSign;
				if(xSign == 1)
				{
					//It's going in the positive direction, so it hit xyz-1 first, then xyz
					c1 = new ChunkCoordinates(xCoord-1, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				}
				else
				{
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord-1, yCoord, zCoord);
				}
			}
			else if(yCoord == y)
			{
				nextWholeY += ySign;
				if(xSign == 1)
				{
					//It's going in the positive direction, so it hit xyz-1 first, then xyz
					c1 = new ChunkCoordinates(xCoord, yCoord-1, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				}
				else
				{
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord-1, zCoord);
				}
			}
			else if(zCoord == z)
			{
				nextWholeZ += zSign;
				if(xSign == 1)
				{
					//It's going in the positive direction, so it hit xyz-1 first, then xyz
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord-1);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				}
				else
				{
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord-1);
				}
			}
			else
			{
				//This shouldn't happen
				c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				c2 = null;
			}
			
			contains = false;
			
			for(int a = hitBlocks.size()-1; a >= 0 && a >= hitBlocks.size()-6; a--)
			{
				if(hitBlocks.get(a).equals(c1))
				{
					contains = true;
				}
			}
			if(!contains)
			{
				hitBlocks.add(c1);
			}
			
			if(c2 != null)
			{
				contains = false;
				
				for(int a = hitBlocks.size()-1; a >= 0 && a >= hitBlocks.size()-6; a--)
				{
					if(hitBlocks.get(a).equals(c2))
					{
						contains = true;
					}
				}
				
				if(!contains)
				{
					hitBlocks.add(c2);
				}
				
			}
			
			/*if(!hitBlocks.contains(c1))
			{
				hitBlocks.add(c1);
			}
			if(c2 != null && !hitBlocks.contains(c2))
			{
				hitBlocks.add(c2);
			}*/
			
		}
		
		if(maxIterations <= 0)
		{
			System.err.println("RAY TRACE BLOCKS RAN OVERTIME");
		}
		
		return hitBlocks.toArray(new ChunkCoordinates[hitBlocks.size()]);
	}
	/*public static ChunkCoordinates[] rayTraceBlocks(Vector v1, Vector v2){
		Vector directionVector = v2.subtract(v1);
		//Length of 1 unit.
		Vector unitDirectionVector = directionVector.toUnitVector();
		
		//For every one unit of distance traveled, this is how much x, y and z change.
		double xComp = unitDirectionVector.x;
		double yComp = unitDirectionVector.y;
		double zComp = unitDirectionVector.z;
		
		double unitPerX = xComp==0?Double.MAX_VALUE:1/xComp;
		double unitPerY = yComp==0?Double.MAX_VALUE:1/yComp;
		double unitPerZ = zComp==0?Double.MAX_VALUE:1/zComp;
		
		int xSign = xComp >= 0?1:-1;
		int ySign = yComp >= 0?1:-1;
		int zSign = zComp >= 0?1:-1;
		
		//Starting Positions
		double x = v1.x;
		double y = v1.y;
		double z = v1.z;
		
		ArrayList<ChunkCoordinates> hitBlocks = new ArrayList<ChunkCoordinates>();
		
		double maxAddedUnits = directionVector.getLength();
		
		double addedUnits = 0;
		
		int maxIterations = 10000;
		
		while(maxIterations-- > 0)
		{
			double nextWholeX = xSign==1?Math.ceil(x):Math.floor(x);
			if(nextWholeX == x){nextWholeX += xSign;}
			double nextWholeY = ySign==1?Math.ceil(y):Math.floor(y);
			if(nextWholeY == y){nextWholeY += ySign;}
			double nextWholeZ = zSign==1?Math.ceil(z):Math.floor(z);
			if(nextWholeZ == z){nextWholeZ += zSign;}
			
			double dx = nextWholeX - x;
			double dy = nextWholeY - y;
			double dz = nextWholeZ - z;
			
			//System.out.println(dx + ", " + xSign + "," + (nextWholeX == x));
			
			//Change in the unit vector for the components to move the amount they need to reach
			//the next wholeNumber
			double dxu = dx * unitPerX;
			double dyu = dy * unitPerY;
			double dzu = dz * unitPerZ;
			
			//Change in unit vector. We want the smallest change
			double du = dxu<dyu?(dxu<dzu?dxu:dzu):(dyu<dzu?dyu:dzu);
			
			x += du * xComp;
			y += du * yComp;
			z += du * zComp;
			
			//System.out.println(addedUnits + ", " + maxAddedUnits);
			
			addedUnits += du;
			
			
			
			if(addedUnits >= maxAddedUnits){
				break;
			}
			
			int xCoord = (int)Math.floor(x);
			int yCoord = (int)Math.floor(y);
			int zCoord = (int)Math.floor(z);
			
			ChunkCoordinates c1;
			ChunkCoordinates c2;
			
			if(xCoord == x)
			{
				if(xSign == 1)
				{
					//It's going in the positive direction, so it hit xyz-1 first, then xyz
					c1 = new ChunkCoordinates(xCoord-1, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				}
				else
				{
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord-1, yCoord, zCoord);
				}
			}
			else if(yCoord == y)
			{
				if(xSign == 1)
				{
					//It's going in the positive direction, so it hit xyz-1 first, then xyz
					c1 = new ChunkCoordinates(xCoord, yCoord-1, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				}
				else
				{
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord-1, zCoord);
				}
			}
			else if(zCoord == z)
			{
				if(xSign == 1)
				{
					//It's going in the positive direction, so it hit xyz-1 first, then xyz
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord-1);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				}
				else
				{
					c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
					c2 = new ChunkCoordinates(xCoord, yCoord, zCoord-1);
				}
			}
			else
			{
				//This shouldn't happen
				c1 = new ChunkCoordinates(xCoord, yCoord, zCoord);
				c2 = null;
			}
			if(!hitBlocks.contains(c1))
			{
				hitBlocks.add(c1);
			}
			if(c2 != null && !hitBlocks.contains(c2))
			{
				hitBlocks.add(c2);
			}
			
		}
		
		if(maxIterations <= 0)
		{
			System.err.println("RAY TRACE BLOCKS RAN OVERTIME");
		}
		
		return hitBlocks.toArray(new ChunkCoordinates[hitBlocks.size()]);
	}*/
	
	
	public static class Collision implements Comparable<Collision>{
		
		@Override
		public int compareTo(Collision arg0) {
			if(distance > arg0.distance)
			{
				return 1;
			}
			else if(distance < arg0.distance)
			{
				return -1;
			}
			return 0;
		}

		double distance = 0;
		
		Entity entityHit = null;
		
		public Collision(Entity entityHit, double distance){
			this.entityHit = entityHit;
			this.distance = distance;
		}
		
		ChunkCoordinates blockHit = null;
		
		public Collision(ChunkCoordinates blockCoords, double distance){
			this.blockHit = blockCoords;
			this.distance = distance;
		}
		
	}

}
