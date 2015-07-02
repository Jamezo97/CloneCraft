package net.jamezo97.physics;

public class Spring {
	
	public double posX;
	public double posY;
	public double posZ;
	
	public double length;
	
	public double springConstant;
	
	Vector direction;
	
	public Spring(double posX, double posY, double posZ, double length, double springConstant)
	{
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.length = length;
		this.springConstant = springConstant;
	}
	
	/**
	 * Forces the spring to always apply the force in the given direction.
	 * @param vector The direction vector. If set to null, the spring applies the force in the direction of the particle
	 * @return This spring
	 * @see Spring#getDirectionVector()
	 */
	public Spring setConstantDirection(Vector vector)
	{
		this.direction = vector.toUnitVector();
		return this;
	}
	
	/**
	 * Gets the vector which the spring must apply the force in. Null if no such force was set.
	 * @return The unit vector representing the restricting force direction
	 * @see Spring#setConstantDirection(Vector)
	 */
	public Vector getDirectionVector()
	{
		return direction;
	}
	
	/**
	 * Calculates the distance between the particle and the spring, and applies the restoring force
	 * to the particle, in the direction of the particle, or that which has been set by setConstantDirection
	 * @param particle
	 * @param time The time period to apply it across
	 * @see Spring#setConstantDirection(Vector)
	 */
	public void apply(Particle particle, double changeInTime)
	{
		double dx = particle.posX - posX;
		double dy = particle.posY - posY;
		double dz = particle.posZ - posZ;
	
		double dxyz = Math.sqrt(dx*dx + dy*dy + dz*dz);
		
		double dxyzApprox = Math.round(dxyz * 1000) / 1000.0;
		
		//If the mass is not at the resting position of the spring
		if(dxyz != length)
		{
			double dist = dxyz - length;
			
			//F = -kx
			//F = kg.m/s.s; F.s = kg.m/s = Impulse
			//Impulse = kg.m/s; /kg= m.s = Velocity
			double velocity = ((-springConstant * dist) * changeInTime) / particle.mass;
			
			if(velocity < 0 && this.direction != null)
			{
				velocity *= -1;
			}

			particle.addVelocity((this.direction == null?new Vector(dx, dy, dz).toUnitVector():direction).multiply(velocity));
		}
	}

}
