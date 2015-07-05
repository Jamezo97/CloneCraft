package net.jamezo97.clonecraft.clone;

import net.jamezo97.clonecraft.CCEventListener;
import net.jamezo97.clonecraft.clone.ai.EntityAIBuild;
import net.jamezo97.clonecraft.render.Renderable;

import org.lwjgl.opengl.GL11;

public class CloneExtraRender implements Renderable{
	
	EntityClone clone;
	
	public CloneExtraRender(EntityClone clone)
	{
		this.clone = clone;
	}

	@Override
	public void render(float partialTicks)
	{
		EntityAIBuild buildAi = clone.getBuildAI();
		
		if(buildAi.renderOverlay())
		{
			GL11.glTranslatef(buildAi.posX, buildAi.posY, buildAi.posZ);
			
			GL11.glRotatef(buildAi.getAngularRotation(), 0, 1, 0);
			
			buildAi.getSchematic().storeOnGPU(true);
			
			buildAi.getSchematic().render();
			
			GL11.glRotatef(buildAi.getAngularRotation(), 0, 1, 0);
		
			GL11.glTranslatef(-buildAi.posX, -buildAi.posY, -buildAi.posZ);
		}
	}

	@Override
	public void onTick() {
		if(clone.getBuildAI().renderOverlay())
		{
			
			clone.getBuildAI().posX += CCEventListener.moveX;
			clone.getBuildAI().posY += CCEventListener.moveY;
			clone.getBuildAI().posZ += CCEventListener.moveZ;
		}
		
	}

	@Override
	public void onRemoved()
	{
		if(clone.getBuildAI().renderOverlay())
		{
			clone.getBuildAI().getSchematic().cleanGPU();
		}
	}
	
	

}
