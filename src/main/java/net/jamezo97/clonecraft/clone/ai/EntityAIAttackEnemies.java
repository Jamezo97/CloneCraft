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
//		System.out.println("Should" + clone.getOptions().fight.get());
		return clone.getOptions().fight.get();
//		if(clone != null && clone.getOptions() != null && !clone.isEatingFood()){
//			return clone.options.fight.value();
//		}
//		return false;
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
		System.out.println((clone.getAttackTarget()));
		if(clone.getAttackTarget() == null || !clone.shouldAttack(clone.getAttackTarget())){
			EntityLivingBase attack = this.getClosestEntityToAttackExcluding(null);
			if(attack != null){
				clone.setAttackTarget(attack);
				clone.setPath(clone.getNavigator().getPathToEntityLiving(attack));
			}else{
				clone.setAttackTarget(null);
			}
		}else if(clone.getAttackTarget().isEntityAlive()){
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
			clone.attackEntity(clone.getAttackTarget());
			if(clone.prevPosX == clone.posX && clone.prevPosY == clone.posY && clone.prevPosZ == clone.posZ && clone.getAttackTarget().getHealth() == lastHealth){
				noImprovement++;
				if(noImprovement > 30){
					EntityLivingBase newEntity = getClosestEntityToAttackExcluding(clone.getAttackTarget());
					if(newEntity != null){
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
		
		
		
//		if(clone.getTarget() != null){
//			lastHealth = clone.getTarget().getHealth();
//		}
//		if(clone.getTarget() == null){
//			EntityLivingBase newEntity = getClosestEntityToAttackExcluding(null);
//			if(newEntity != null){
//				clone.setTarget(newEntity);
//				clone.setPathToEntity(clone.worldObj.getPathEntityToEntity(clone, clone.getTarget(), 16.0F, true, false, false, true));
//			}
//		}else if(clone.getTarget().isEntityAlive()){
//			if(rand.nextInt(10) == 0){
//				EntityLivingBase newEntity = getClosestEntityToAttackExcluding(null);
//				if(newEntity != null && newEntity != clone.getTarget()){
//					double d1 = clone.getDistanceSqToEntity(clone.getTarget());
//					double d2 = clone.getDistanceSqToEntity(newEntity);
//					if(d2 < d1){
//						clone.setTarget(newEntity);
//						clone.setPathToEntity(clone.worldObj.getPathEntityToEntity(clone, clone.getTarget(), 16.0F, true, false, false, true));
//					}
//				}
//			}else if(update++ > 1){
//				update = 0;
//				clone.setPathToEntity(clone.worldObj.getPathEntityToEntity(clone, clone.getTarget(), 16.0F, true, false, false, true));
//			}
//			if((clone.getOptions().shouldAttack(clone.getTarget())?false:!clone.revenge) && !(clone.team == PlayerTeam.Evil && clone.getTarget() instanceof EntityPlayer)){
//				clone.setTarget(null);
//				clone.setPathToEntity(null);
//			}else{
//				if(clone.getNavigator().noPath()){
//					clone.setPathToEntity(clone.worldObj.getPathEntityToEntity(clone, clone.getTarget(), 16.0F, true, false, false, true));
//				}
//				clone.attackEntity(clone.getTarget(), clone.getDistanceToEntityFromHead(clone.getTarget()));
//			}
//			if(clone.prevPosX == clone.posX && clone.prevPosY == clone.posY && clone.prevPosZ == clone.posZ && clone.getTarget().getHealth() == lastHealth){
//				noImprovement++;
//				if(noImprovement > 30){
//					EntityLivingBase newEntity = getClosestEntityToAttackExcluding(clone.getTarget());
//					if(newEntity != null){
//						clone.setTarget(newEntity);
//						clone.setPathToEntity(clone.worldObj.getPathEntityToEntity(clone, clone.getTarget(), 16.0F, true, false, false, true));
//					}
//				}
//			}else{
//				noImprovement = 0;
//			}
//		}else{
//			clone.setTarget(null);
//			clone.setPathToEntity(null);
//			if(clone.attackDelay > 3){
//				clone.attackDelay = 3;
//			}
//		} 
		return super.continueExecuting();
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
					if(clone.shouldAttack(e) && clone.canEntityBeSeen(e)){
						double distancesqr = distanceSquared(clone, e);
						if(distancesqr < distance || distance == -1){
							distance = distancesqr;
							toAttack = e;
						}
					}
				}
//				if(e instanceof EntityPlayer && clone.team == PlayerTeam.Traitor){
//					double distancesqr = distanceSquared(clone, e);
//					if(distancesqr < distance || distance == -1){
//						distance = distancesqr;
//						toAttack = e;
//					}
//				}
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
