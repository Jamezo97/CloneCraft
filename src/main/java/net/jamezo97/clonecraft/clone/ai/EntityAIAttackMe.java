/*package jamezo97.clonecraft.entity.clone.AI;

import jamezo97.clonecraft.entity.clone.EntityClone;

import java.util.List;

import net.minecraft.entity.CloneCraftEntityAccessor;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIAttackMe extends EntityAIBase {

	Class<?> entityBaseShouldAttackMe = null;
	
	EntityLiving entityToAttack = null;
	
	float distance = 16F;
	
	public EntityAIAttackMe(EntityLiving entityLiving, Class<?> class1, float f) {
		entityToAttack = entityLiving;
		entityBaseShouldAttackMe = class1;
		distance = f;
	}

	@Override
	public boolean shouldExecute() {
		if(distance > 0 && entityToAttack != null && entityToAttack.isEntityAlive() && entityBaseShouldAttackMe != null){
			if(entityToAttack instanceof EntityClone){
				EntityClone clone = (EntityClone)entityToAttack;
				return clone.options.fight.value();//?true:clone.getEntityToAttack() != null;
			}else{
				return true;
			}
			
		}
		return false;
	}

	@Override
	public void updateTask() {
		List entities = entityToAttack.worldObj.getEntitiesWithinAABB(entityBaseShouldAttackMe, entityToAttack.boundingBox.expand(distance, distance, distance));
		for(int a = 0; a < entities.size(); a++){
			Object o = entities.get(a);
			if(o != null && o instanceof EntityLiving){
				EntityLiving entity = (EntityLiving)o;
				if(entity.getAttackTarget() == null && entity.canEntityBeSeen(entityToAttack)){
					if(entity instanceof EntityCreature && !CloneCraftEntityAccessor.isAIEnabled(entity)){
						((EntityCreature)entity).setTarget(entityToAttack);
					}else{
						entity.setAttackTarget(entityToAttack);
					}
					entity.getNavigator().setPath(entity.getNavigator().getPathToEntityLiving(entityToAttack), entity.getAIMoveSpeed());
				}else if(entity.getAttackTarget() == entityToAttack && entity.getNavigator().noPath() && entity.getDistanceSqToEntity(entityToAttack) > 2){
					entity.getNavigator().setPath(entity.getNavigator().getPathToEntityLiving(entityToAttack), entity.getAIMoveSpeed());
				}
				if(entity.getAttackTarget() == entityToAttack && CloneCraftEntityAccessor.isAIEnabled(entity) && entity.getDistanceSqToEntity(entityToAttack) < 2.5){
					entity.attackEntityAsMob(entityToAttack);
				}
			}
		}
	}
	
	

	
	

}
*/