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
			
			ChunkCoordinates cc = new ChunkCoordinates((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
			
			if(!hitBlocks.contains(cc))
			{
				hitBlocks.add(cc);
			}
			
		}
		
		if(maxIterations <= 0)
		{
			System.err.println("RAY TRACE BLOCKS RAN OVERTIME");
		}
		
		return hitBlocks.toArray(new ChunkCoordinates[hitBlocks.size()]);
	}
	
	
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
