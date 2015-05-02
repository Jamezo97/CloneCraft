package net.minecraft.world;

import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.Entity;



public class CloneCraftWorld {
	
	public static void spawnParticle(EntityFX entity){
		World world = entity.worldObj;
		if(entity != null && world != null){
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.renderViewEntity != null){
				for(int a = 0; a < world.worldAccesses.size(); a++){
					int i = mc.gameSettings.particleSetting;
					if (i == 1 && entity.worldObj.rand.nextInt(3) == 0){
						i = 2;
					}
					double d6 = mc.renderViewEntity.posX - entity.posX;
		            double d7 = mc.renderViewEntity.posY - entity.posY;
		            double d8 = mc.renderViewEntity.posZ - entity.posZ;
		            double d9 = 16.0D;

		            if(d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9){
		            	return;
		            }else if (i > 1){
		            	return;
		            }else{
		            	mc.effectRenderer.addEffect((EntityFX)entity);
		            }
				}
			}
		}
	}
	
	public static void spawnParticleInRange(EntityFX entity, double distance){
		World world = entity.worldObj;
		if(entity != null && world != null){
			Minecraft mc = Minecraft.getMinecraft();
			if(mc.renderViewEntity != null){
				for(int a = 0; a < world.worldAccesses.size(); a++){
					int i = mc.gameSettings.particleSetting;
					if (i == 1 && entity.worldObj.rand.nextInt(3) == 0){
						i = 2;
					}
					double d6 = mc.renderViewEntity.posX - entity.posX;
		            double d7 = mc.renderViewEntity.posY - entity.posY;
		            double d8 = mc.renderViewEntity.posZ - entity.posZ;
		            double d9 = distance;//16.0D;

		            if(d6 * d6 + d7 * d7 + d8 * d8 > d9 * d9){
		            	return;
		            }else if (i > 1){
		            	return;
		            }else{
		            	mc.effectRenderer.addEffect((EntityFX)entity);
		            }
				}
			}

		}
	}
	
	public static void spawnParticleIgnoreDistance(EntityFX entity){
		World world = entity.worldObj;
		if(entity != null && world != null){
			Minecraft mc = Minecraft.getMinecraft();
			for(int a = 0; a < world.worldAccesses.size(); a++){
				int i = mc.gameSettings.particleSetting;
				if (i == 1 && entity.worldObj.rand.nextInt(3) == 0){
					i = 2;
				}
				if (i > 1){
	            	return;
	            }else{
	            	mc.effectRenderer.addEffect((EntityFX)entity);
	            }
			}

		}
	}
	
	public static boolean chunkExists(World world, int par1, int par2){
        return world.chunkProvider.chunkExists(par1, par2);
    }
	
	public static void onEntityAdded(WorldServer world, Entity e){
		world.onEntityAdded(e);
	}
	
	public Set getActiveChunks(World world){
		return world.activeChunkSet;
	}

}
