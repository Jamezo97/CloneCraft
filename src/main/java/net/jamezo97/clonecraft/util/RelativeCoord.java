package net.jamezo97.clonecraft.util;

public class RelativeCoord implements Comparable<RelativeCoord>{

	public int xPos, yPos, zPos;
	
	public double xPosR, yPosR, zPosR;
	
	public double distanceSq;
	
	public RelativeCoord(int xPos, int yPos, int zPos, double xPosR, double yPosR, double zPosR)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.zPos = zPos;
		
		this.xPosR = xPosR;
		this.yPosR = yPosR;
		this.zPosR = zPosR;
		
		double dx = xPos - xPosR - 0.5;
		double dy = yPos - yPosR - 0.5;
		double dz = zPos - zPosR - 0.5;
		
		distanceSq = dx * dx + dy * dy + dz * dz;
	}

	@Override
	public int compareTo(RelativeCoord o)
	{
		if(this.distanceSq > o.distanceSq)
		{
			return 1;
		}
		else if(this.distanceSq < o.distanceSq)
		{
			return -1;
		}
		return 0;
	}
	
	

}
