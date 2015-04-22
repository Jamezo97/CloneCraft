package net.jamezo97.clonecraft.clone.ai;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackEnemies extends EntityAIBase {

	EntityClone clone = null;
	
//	Random rand = new Random();
	
	public EntityAIAttackEnemies(EntityClone entityMyPerson) {
		clone = entityMyPerson;
		
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(clone.getOptions().fight.get())
		{
			if(clone.getAttackTarget() == null || (!clone.shouldProvokeAttack(clone.getAttackTarget()) && !clone.canAttackEntity(clone.getAttackTarget()))){
				EntityLivingBase attack = this.getClosestEntityToAttackExcluding(null);
				if(attack != null){
					clone.setAttackTarget(attack);
					clone.setPath(clone.getNavigator().getPathToEntityLiving(attack));
					return true;
				}else{
					//If the current target is because of an act of revenge, and is not a selected enemy
					if(clone.shouldProvokeAttack(clone.getAttackTarget())){
						clone.setAttackTarget(null);
					}
				}
			}
			return clone.getAttackTarget() != null;
		}
		
		return false;
	}

	int noImprovement = 0;
	
	int noSee = 0;
	
	int update = 0;
	
	float lastHealth = 0;
	
	public boolean canTargetBeSeen(){
		return noSee > 0;
	}
	
	
	
	@Override
	public boolean continueExecuting() {
		if(clone.getAttackTarget() == null || !clone.getAttackTarget().isEntityAlive()){
			clone.setAttackTarget(null);
			return false;
		}else{
			return true;
		}
	}
	
	
	
	@Override
	public void updateTask() {
		if(clone.getAttackTarget().isEntityAlive()){
			if(clone.getRNG().nextInt(10) == 0){
				EntityLivingBase attack = this.getClosestEntityToAttackExcluding(null);
				if(attack != null && attack != clone.getAttackTarget()){
					double d1 = clone.getDistanceSqToEntity(attack);
					double d2 = clone.getDistanceSqToEntity(clone.getAttackTarget());
					if(d1 < d2){
						clone.setAttackTarget(attack);
						clone.setPath(clone.getNavigator().getPathToEntityLiving(attack));
					}	
				}
			}else if(update++ > 1 || clone.getNavigator().noPath()){
				update = 0;
				clone.setPath(clone.getNavigator().getPathToEntityLiving(clone.getAttackTarget()));
			}
//			System.out.println("Attack");
			clone.attackEntity(clone.getAttackTarget());
			if(clone.prevPosX == clone.posX && clone.prevPosY == clone.posY && clone.prevPosZ == clone.posZ && clone.getAttackTarget().getHealth() == lastHealth){
				noImprovement++;
				if(noImprovement > 30){
					EntityLivingBase newEntity = getClosestEntityToAttackExcluding(clone.getAttackTarget());
					if(newEntity != null){
						noImprovement = 0;
						clone.setAttackTarget(newEntity);
						clone.setPath(clone.getNavigator().getPathToEntityLiving(clone.getAttackTarget()));
					}
				}
			}else{
				noImprovement = 0;
			}
			if(!clone.canEntityBeSeen(clone.getAttackTarget())){
				noSee++;
				if(noSee > 100){
					EntityLivingBase attack = this.getClosestEntityToAttackExcluding(clone.getAttackTarget());
					if(attack != null){
						clone.setAttackTarget(attack);
						clone.setPath(clone.getNavigator().getPathToEntityLiving(attack));
					}else{
						clone.setAttackTarget(null);
						clone.setPath(null);
					}
					noSee = 0;
				}
			}else{
				noSee = 0;
			}
		}else{
			clone.setAttackTarget(null);
			clone.setPath(null);
		}
	}

	public EntityLivingBase getClosestEntityToAttackExcluding(EntityLivingBase entity){
		List list = clone.worldObj.getEntitiesWithinAABBExcludingEntity(clone, clone.boundingBox.expand(32D, 16D, 32D));
		if(entity != null){
			list.remove(entity);
		}
		
		double distance = -1;
		EntityLivingBase toAttack = null;
		for(int a = 0; a < list.size(); a++){
			Object o = list.get(a);
			if(o != null && o instanceof EntityLivingBase){
				EntityLivingBase e = (EntityLivingBase)o;
				if(e.isEntityAlive() && clone.canEntityBeSeen(e)){
					if(clone.shouldProvokeAttack(e) && clone.canEntityBeSeen(e)){
						double distancesqr = distanceSquared(clone, e);
						if(distancesqr < distance || distance == -1){
							distance = distancesqr;
							toAttack = e;
						}
					}
				}
			}
		}
		return toAttack;
	}
	
	
	
	public double distanceSquared(Entity e1, Entity e2){
		double d1 = e1.posX - e2.posX;
		double d2 = e1.posY - e2.posY;
		double d3 = e1.posZ - e2.posZ;
		return d1*d1 + d2*d2 + d3*d3;
	}
	
	

}
