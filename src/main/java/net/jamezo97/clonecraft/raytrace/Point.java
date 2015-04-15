package net.jamezo97.clonecraft.raytrace;


public class Point{
	
	public double x, y, z;
	
	public Point(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public IntPos floor(){
		return new IntPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
	}
	
	public Point clone(){
		return new Point(x, y, z);
	}
	
}
