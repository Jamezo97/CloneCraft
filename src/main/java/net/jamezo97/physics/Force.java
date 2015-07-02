package net.jamezo97.physics;

public class Force {
	
	Vector theForce;
	
	public Force(Vector vec)
	{
		this.theForce = vec;
	}
	
	public Force(double amplitude, Vector direction)
	{
		this.theForce = direction.toUnitVector().multiply(amplitude);
	}
	
	public void apply(Particle particle)
	{
		particle.accX += theForce.x/particle.mass;
		particle.accY += theForce.y/particle.mass;
		particle.accZ += theForce.z/particle.mass;
	}

}
