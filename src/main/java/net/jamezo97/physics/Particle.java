package net.jamezo97.physics;

public class Particle {
	
	//The last state when timeElapsed was called (or this particle was created)
	private PastParticle last;
	
	public double posX = 0, posY = 0, posZ = 0;
	
	public double velX = 0, velY = 0, velZ = 0;
	
	public double accX = 0, accY = 0, accZ = 0;
	
	public Vector theImpulse = null;
	
	public Vector theForce = null;
	
	/**
	 * Resistances. Velocities are multiplied by these values every tick.
	 */
	public double resX = 1, resY = 1, resZ = 1;
	
	//Kilograms
	public double mass = 1.0;
	
	public Particle()
	{
		this(0, 0, 0);
	}
	
	public Particle(double posX, double posY, double posZ)
	{
		last = new PastParticle(posX, posY, posZ);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}
	
	public void setPosition(double x, double y, double z)
	{
		this.posX = x;
		this.posY = y;
		this.posZ = z;
	}
	
	/**
	 * Increments displacements by velocities, then increments velocities by accelerations
	 * @param seconds
	 */
	public void tickForward(double seconds)
	{
		velX *= resX;
		velY *= resY;
		velZ *= resZ;
		
		posX += seconds * velX;
		posY += seconds * velY;
		posZ += seconds * velZ;
	
		velX += seconds * accX;
		velY += seconds * accY;
		velZ += seconds * accZ;
		
		//Could be used for interpolation
		last.load(this);
	}
	
	public void tickBackward(double seconds)
	{
		//New velocities
		double nvx = (posX - last.posX) / seconds;
		double nvy = (posY - last.posY) / seconds;
		double nvz = (posZ - last.posZ) / seconds;
		
		theImpulse = new Vector(nvx - velX, nvy - velY, nvz - velZ).multiply(mass);
		velX = nvx;
		velY = nvy;
		velZ = nvz;
		
		//New accelerations
		double nax = (velX - last.velX) / seconds;
		double nay = (velY - last.velY) / seconds;
		double naz = (velZ - last.velZ) / seconds;
		
		theForce = new Vector(nax - accX, nay - accZ, naz - accZ).multiply(mass);
		accX = nax;
		accY = nay;
		accZ = naz;
		
		last.load(this);
	}
	
	public void addDisplacement(Vector vec)
	{
		this.posX += vec.x;
		this.posY += vec.y;
		this.posZ += vec.z;
	}
	
	public void addVelocity(Vector vec)
	{
		this.velX += vec.x;
		this.velY += vec.y;
		this.velZ += vec.z;
	}
	
	public void addAcceleration(Vector vec)
	{
		this.accX += vec.x;
		this.accY += vec.y;
		this.accZ += vec.z;
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
