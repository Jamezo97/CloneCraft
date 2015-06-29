package net.jamezo97.clonecraft.clone;

import net.jamezo97.clonecraft.CCEventListener;
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
		if(clone.getBuildAI().renderOverlay())
		{
			GL11.glTranslatef(clone.getBuildAI().posX, clone.getBuildAI().posY, clone.getBuildAI().posZ);
			
			clone.getBuildAI().getSchematic().storeOnGPU(true);
			
			clone.getBuildAI().getSchematic().render();
		
			GL11.glTranslatef(-clone.getBuildAI().posX, -clone.getBuildAI().posY, -clone.getBuildAI().posZ);
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
