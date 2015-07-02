package net.jamezo97.physics;

public class Impulse {
	
	Vector theImpulse;
	
	public Impulse(Vector theImpulse)
	{
		this.theImpulse = theImpulse;
	}
	
	public Impulse(double amplitude, Vector direction)
	{
		this.theImpulse = direction.toUnitVector().multiply(amplitude);
	}
	
	public void apply(Particle particle)
	{
		particle.velX += theImpulse.x / particle.mass;
		particle.velY += theImpulse.y / particle.mass;
		particle.velZ += theImpulse.z / particle.mass;
	}

}
