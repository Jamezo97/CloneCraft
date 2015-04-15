package net.jamezo97.clonecraft.raytrace;

import java.util.ArrayList;

import net.jamezo97.clonecraft.render.Lightning;

public class Line {
	
	public double x1, x2, y1, y2, z1, z2;
	
	public Line(Point from, Point to){
		this(from.x, from.y, from.z, to.x, to.y, to.z);
	}
	
	public Line(double x1, double y1, double z1, double x2, double y2, double z2){
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
	}

	@Override
	public String toString() {
		return x1 + ", " + y1 + ", " + z1 + ", " + x2 + ", " + y2 + ", " + z2;
	}

	
	
}
