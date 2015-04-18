package net.minecraft.entity;

/**
 * A helper class to access protected methods within the net.minecraft.entity package.
 * @author James
 *
 */
public class EntityAccessor {
	
	public static boolean isAIEnabled(EntityLivingBase entity){
		return entity.isAIEnabled();
	}

}
