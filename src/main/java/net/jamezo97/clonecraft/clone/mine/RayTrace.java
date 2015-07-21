package net.jamezo97.clonecraft.clone.mine;

import java.util.ArrayList;

import net.jamezo97.physics.Vector;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class RayTrace {
	
	
	/**
	 * Ray traces Blocks, and returns all collisions between the two points.
	 * @param v1 The Vector to start tracing from
	 * @param v2 The Vector to trace to
	 */
	public static ChunkCoordinates[] rayTraceBlocks(Vector v1, Vector v2, World world){
		Vector directionVector = v2.subtract(v1);
		
		
		
		//Length of 1 unit.
		Vector unitDirectionVector = directionVector.toUnitVector();
		
		//For every one unit of distance traveled, this is how much x, y and z change.
		double xPerUnit = unitDirectionVector.x; //xPerUnit
		double yPerUnit = unitDirectionVector.y; //yPerUnit
		double zPerUnit = unitDirectionVector.z; //zPerUnit
		
		double unitPerX = xPerUnit==0?Double.MAX_VALUE:1/xPerUnit;
		double unitPerY = yPerUnit==0?Double.MAX_VALUE:1/yPerUnit;
		double unitPerZ = zPerUnit==0?Double.MAX_VALUE:1/zPerUnit;
		
		int xSign = xPerUnit >= 0?1:-1;
		int ySign = yPerUnit >= 0?1:-1;
		int zSign = zPerUnit >= 0?1:-1;
		
		//Starting Positions
		double x = v1.x;
		double y = v1.y;
		double z = v1.z;
		
		ArrayList<ChunkCoordinates> hitBlocks = new ArrayList<ChunkCoordinates>();
		
		double maxAddedUnits = directionVector.getModulus();
		
		double addedUnits = 0;
		
		int maxIterations = (int)Math.ceil(maxAddedUnits*2)+10;
		
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
		
		while(maxIterations-- > 0)
		{
//			System.out.println(nextWholeX);
			//Change in the unit vector for the components to move the amount they need to reach
			//the next wholeNumber
			dxu = (nextWholeX - x) * unitPerX;
			if(dxu == 0)
			{
				dxu = 100;
				nextWholeX += xSign;
			}
			dyu = (nextWholeY - y) * unitPerY;
			if(dyu == 0)
			{
				dyu = 100;
				nextWholeX += ySign;
			}
			dzu = (nextWholeZ - z) * unitPerZ;
			if(dzu == 0)
			{
				dzu = 100;
				nextWholeZ += zSign;
			}
			
			//Change in unit vector. We want the smallest change
			du = dxu<dyu?(dxu<dzu?dxu:dzu):(dyu<dzu?dyu:dzu);
			
			x += du * xPerUnit;
			y += du * yPerUnit;
			z += du * zPerUnit;
			
//			System.out.println(du + ": " + addedUnits + ", " + maxAddedUnits);
			
			addedUnits += du;
			
			if(addedUnits >= maxAddedUnits /*|| Math.abs(addedUnits - maxAddedUnits) < 0.0000000001*/)
			{
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
						break;
					}
				}
				
				if(!contains)
				{
					hitBlocks.add(c2);
				}
				
			}
			
		}
		
		if(maxIterations <= 0)
		{
			System.err.println("RAY TRACE BLOCKS RAN OVERTIME");
		}

		AxisAlignedBB bb = null;
		
		
		//The vector goes from these points
		double xBegin = v1.x;
		double yBegin = v1.y;
		double zBegin = v1.z;
		
		
		for(int a = 0; a < hitBlocks.size(); a++)
		{
			ChunkCoordinates cc = hitBlocks.get(a);
			
			if(cc.posY > 255 || cc.posY < 0)
			{
				hitBlocks.remove(a);
				a--;

//				System.out.println("REMOVEY: " + cc.posY);
				continue;
			}
			Block block = world.getBlock(cc.posX, cc.posY, cc.posZ);
			
			int meta = world.getBlockMetadata(cc.posX, cc.posY, cc.posZ);
			
			if((bb = block.getCollisionBoundingBoxFromPool(world, cc.posX, cc.posY, cc.posZ)) != null && block.canCollideCheck(meta, false))
			{	
				if((bb.maxX-bb.minX) == 1 && (bb.maxY-bb.minY) == 1 && (bb.maxZ-bb.minZ) == 1)
				{
					//It's a solid block
					continue;
				}
				
				
				//Calculate at what times the x component will be inside the bb, the ycomp, and z comp. Then if they all overlap at some point, it intersect.
				//The vector lengths to reach the 3 components faces of the bounding box
//				double xuMin, xuMax, yuMin, yuMax, zuMin, zuMax;
				
				//			X How much x has to travel to reach the minX and maxX positions
				//How  many units the vector needs to move to extend this far
				double dxMax, dxMin;
				if(unitPerX != 0)
				{
					double dxu1 = (bb.minX - xBegin) * unitPerX;
					double dxu2 = (bb.maxX - xBegin) * unitPerX;
					dxMax = Math.max(dxu1, dxu2);
					dxMin = Math.min(dxu1, dxu2);
				}
				else
				{
					dxMax = Double.MAX_VALUE;
					dxMin = -Double.MAX_VALUE;
				}
				
				double dyMax, dyMin;
				if(unitPerY != 0)
				{
					double dyu1 = (bb.minY - yBegin) * unitPerY;
					double dyu2 = (bb.maxY - yBegin) * unitPerY;
					dyMax = Math.max(dyu1, dyu2);
					dyMin = Math.min(dyu1, dyu2);
				}
				else
				{
					dyMax = Double.MAX_VALUE;
					dyMin = -Double.MAX_VALUE;
				}
				
				double dzMax, dzMin;
				if(unitPerZ != 0)
				{
					double dzu1 = (bb.minZ - zBegin) * unitPerZ;
					double dzu2 = (bb.maxZ - zBegin) * unitPerZ;
					dzMax = Math.max(dzu1, dzu2);
					dzMin = Math.min(dzu1, dzu2);
				}
				else
				{
					dzMax = Double.MAX_VALUE;
					dzMin = -Double.MAX_VALUE;
				}
				
				double dMin = Math.max(Math.max(dxMin, dyMin), dzMin);
				double dMax = Math.min(Math.min(dxMax, dyMax), dzMax);
				
				//It does collide
				if(dMin > dMax)
				{
					hitBlocks.remove(a);
					a--;
					continue;
				}
			}
			else
			{
				hitBlocks.remove(a);
				a--;
				continue;
			}
		}
		return hitBlocks.toArray(new ChunkCoordinates[hitBlocks.size()]);
	}
	
/*	public static boolean rayTraceBB(AxisAlignedBB aabb, Vector from, Vector to){
		
	}*/
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
