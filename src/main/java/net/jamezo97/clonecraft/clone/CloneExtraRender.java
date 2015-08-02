package net.jamezo97.clonecraft.clone;

import net.jamezo97.clonecraft.CCEventListener;
import net.jamezo97.clonecraft.RenderBlockOverlay;
import net.jamezo97.clonecraft.build.EntityAIBuild;
import net.jamezo97.clonecraft.render.Renderable;
import net.minecraft.util.ChunkCoordinates;

import org.lwjgl.opengl.GL11;

public class CloneExtraRender implements Renderable{
	
	EntityClone clone;
	
	public CloneExtraRender(EntityClone clone)
	{
		this.clone = clone;
	}

	RenderBlockOverlay renderHighlightBlock = new RenderBlockOverlay(0, 0, 0, 0xffdd2222);
	
	@Override
	public void render(float partialTicks)
	{
		EntityAIBuild buildAi = clone.getBuildAI();

		if(buildAi.renderOverlay())
		{
			int x = buildAi.posX;
			int y = buildAi.posY;
			int z = buildAi.posZ;
			
			switch(buildAi.getRotate())
			{
			case 1: x++; break;
			case 2: z++; x++; break;
			case 3: z++; break;
			}
			
			GL11.glTranslatef(x, y, z);
			
			
			GL11.glRotatef(-buildAi.getAngularRotation(), 0, 1, 0);
			
			buildAi.getSchematic().storeOnGPU(true);
			
			buildAi.getSchematic().render();
			
			GL11.glRotatef(buildAi.getAngularRotation(), 0, 1, 0);
		
			GL11.glTranslatef(-x, -y, -z);
		}
		
		ChunkCoordinates cc = clone.blockHighlight;
		
		if(cc.posY != -100)
		{
			renderHighlightBlock.setBlockBounds(cc.posX, cc.posY, cc.posZ);
			renderHighlightBlock.render(partialTicks);
			
			if(clone.getBuildAI().isRunning())
			{
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				
				GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				
			
				GL11.glLineWidth(4.0f);
				GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex3d(clone.posX, clone.posY + clone.getEyeHeight(), clone.posZ);
				GL11.glVertex3d(cc.posX+0.5f, cc.posY+0.5f, cc.posZ+0.5f);
				GL11.glEnd();

				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
		}
	}

	@Override
	public void onTick()
	{
		EntityAIBuild buildAI = clone.getBuildAI();
		
		if(buildAI.renderOverlay())
		{
			if(EntityClone.renderFocusedClone == null)
			{
				EntityClone.renderFocusedClone = this.clone;
			}
			else if(EntityClone.renderFocusedClone != this.clone)
			{
				if(!EntityClone.renderFocusedClone.getBuildAI().renderOverlay())
				{
					EntityClone.renderFocusedClone = this.clone;
				}
			}
			
			if(EntityClone.renderFocusedClone == null || EntityClone.renderFocusedClone == this.clone)
			{
				buildAI.posX += CCEventListener.moveX;
				buildAI.posY += CCEventListener.moveY;
				buildAI.posZ += CCEventListener.moveZ;
				
				if(CCEventListener.rotate != 0)
				{
					int oldRotate = buildAI.getRotate();
					buildAI.incrementRotate();
					int newRotate = buildAI.getRotate();
					
					
					int posXNew = buildAI.posX;
					int posZNew = buildAI.posZ;
					
					int posXOld = posXNew;
					int posZOld = posZNew;
					
					switch(oldRotate)
					{
					case 1: posXOld++; break;
					case 2: posXOld++; posZOld++; break;
					case 3: posZOld++; break;
					}
					
					switch(newRotate)
					{
					case 1: posXNew++; break;
					case 2: posXNew++; posZNew++; break;
					case 3: posZNew++; break;
					}
					
					
					int[] offsets = new int[]{buildAI.getSchematic().xSize, 0, buildAI.getSchematic().zSize};

					double midXOld = posXOld + (offsets[EntityAIBuild.offsetIndexes[oldRotate][0]]/2.0d) * (double)EntityAIBuild.xzMultipliers[oldRotate][0];
					double midZOld = posZOld + (offsets[EntityAIBuild.offsetIndexes[oldRotate][1]]/2.0d) * (double)EntityAIBuild.xzMultipliers[oldRotate][1];

					double midX = posXNew + (offsets[EntityAIBuild.offsetIndexes[newRotate][0]]/2.0d) * (double)EntityAIBuild.xzMultipliers[newRotate][0];
					double midZ = posZNew + (offsets[EntityAIBuild.offsetIndexes[newRotate][1]]/2.0d) * (double)EntityAIBuild.xzMultipliers[newRotate][1];


					double diffX = midXOld - midX;
					double diffZ = midZOld - midZ;
					
					if(diffX != Math.round(diffX))
					{
						if(newRotate == 0 || newRotate == 1)
						{
							diffX += 0.1;
						}
						else
						{
							diffX -= 0.1;
						}
					}
					
					if(diffZ != Math.round(diffZ))
					{
						if(newRotate == 0 || newRotate == 1)
						{
							diffZ += 0.1;
						}
						else
						{
							diffZ -= 0.1;
						}
					}
					
					buildAI.posX += Math.round(diffX);
					buildAI.posZ += Math.round(diffZ);
					
				}
			}
			
			
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
