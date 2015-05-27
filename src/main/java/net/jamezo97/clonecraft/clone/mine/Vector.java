package net.jamezo97.clonecraft.clone.mine;

import net.minecraft.util.Vec3;

public class Vector {

	public double x, y, z;
	
	public Vector(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector multiply(double c){
		return new Vector(c*x, c*y, c*z);
	}
	
	public Vector add(Vector vector){
		return new Vector(x+vector.x, y+vector.y, z+vector.z);
	}
	/**
	 * Subtracts the Vector parameter from this Vector
	 * @return
	 */
	public Vector subtract(Vector vector){
		return new Vector(x-vector.x, y-vector.y, z-vector.z);
	}
	
	public double dotProduct(Vector v){
		return v.x*x + v.y*y + v.z*z;
	}
	
	public double getModulus(){
		return Math.sqrt(x*x+y*y+z*z);
	}
	
	public double getLength(){
		return getModulus();
	}
	
	public Vector toUnitVector(){
		double mod = getModulus();
		if(mod == 0)
		{
			return new Vector(0, 0, 0);
		}
		return new Vector(x/mod, y/mod, z/mod);
	}
	
	public double component(Vector inDirectionOf){
		return this.dotProduct(inDirectionOf.toUnitVector());
	}
	
	public Vector projection(Vector inDirectionOf){
		Vector unit = inDirectionOf.toUnitVector();
		//Just the component multiplied by the unit verctor.
		return unit.multiply(this.dotProduct(unit));
	}

	public static Vector fromVec3(Vec3 vec3) {
		return new Vector(vec3.xCoord, vec3.yCoord, vec3.zCoord);
	}
	
}
