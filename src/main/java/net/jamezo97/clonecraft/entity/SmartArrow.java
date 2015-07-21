package net.jamezo97.clonecraft.entity;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class SmartArrow extends Entity implements IProjectile{

    public int arrowShake;
    
    float gravityAcceleration;
	
    float power;
	//EntityArrow

	public SmartArrow(World world, EntityLivingBase entityFrom, EntityLivingBase entityTo, float power) {
		super(world);
		this.power = power;
		setupArrow(entityFrom, entityTo);
	}
	


	@Override
	protected void entityInit() {
		gravityAcceleration = 9.8f/20.0f;
	}



	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt) {
		
	}



	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt) {
		
	}
	
	//We want the arrow to pass through the X Y Z point
	//First, convert 3d space in 2d space. Integrating X and Z into one plane, viewed perpendicular to the line
	//between this arrow and the destination.
	
	@Override
	public void setThrowableHeading(double x, double y, double z, float pitch, float yaw) {
		//Converted into 2D triangle problem
		double HYPOT = Math.sqrt(sqr(this.posX-x) + sqr(this.posY-y) + sqr(this.posZ-z));
		double ADJAC = Math.sqrt(sqr(this.posX-x) + sqr(this.posZ-z));
		double OPPOS = y - this.posY;
		
		//The point X and Y to pass through relative to this entities position
		double pX = ADJAC;
		double pY = OPPOS;
		
		
		
		
		
		//Create two equations for horizontal distance travelled and vertical distance, both using pronumeral theta, work backwards for answr
		//
	}
	
	public double getTimeToTop(double u, double a){
		return 0.0d;
	}
	
	public double getTime(double u, double a, double x){
		return (-u + antiSquare(u*u+2*a*x)) / a;
	}
	
	public double antiSquare(double d){
		if(d < 0){
			return -Math.sqrt(d);
		}else{
			return Math.sqrt(d);
		}
	}
	
	public double sqr(double d){
		return d*d;
	}


	//Speed == distance coverered per tick
	public void setupArrow(EntityLivingBase from, EntityLivingBase to){
		double distance = Math.sqrt(to.getDistanceSqToEntity(from));
		float ticks = ((float)distance) / power;
		this.posX = from.posX;
		this.posY = from.posY;
		this.posZ = from.posZ;
		this.setThrowableHeading(to.posX+to.motionX*ticks, to.posY+to.getEyeHeight()+to.motionY*ticks, to.posZ+to.motionZ*ticks, 0.0f, 0.0f);
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		
		this.posX += this.motionX;
		this.posY += this.motionY;
		this.posZ += this.motionZ;
		
		if(checkCollided()){
			return;
		}
		
		this.motionY -= 9.8d;
		if(this.isInWater()){
			motionX *= 0.5f;
			motionY *= 0.5f;
			motionZ *= 0.5f;
		}
		
		
		
		
		
		
		
	}
	
	public boolean checkCollided(){
		
		
		if(this.ticksExisted > 20){
			return true;
		}
		return false;
	}

	int knockBackStr = 1;

	public void setKnockbackStrength(int j) {
		knockBackStr = j;
	}

	double damage = 1.0d;

	public void setDamage(double d) {
		this.damage = d;
	}

	public double getDamage() {
		return damage;
	}

	
	
	
	
	
	
	
}
