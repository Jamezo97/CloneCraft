package net.jamezo97.physics;

public class PastParticle {
	
	public double posX = 0, posY = 0, posZ = 0;
	
	public double velX = 0, velY = 0, velZ = 0;
	
	public double accX = 0, accY = 0, accZ = 0;
	
	public PastParticle(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}

	public void load(Particle from)
	{
		this.posX = from.posX;
		this.posY = from.posY;
		this.posZ = from.posZ;
		
		this.velX = from.velX;
		this.velY = from.velY;
		this.velZ = from.velZ;
	
		this.accX = from.accX;
		this.accY = from.accY;
		this.accZ = from.accZ;
	}

}
