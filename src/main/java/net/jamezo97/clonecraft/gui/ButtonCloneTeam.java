package net.jamezo97.clonecraft.gui;

import org.lwjgl.opengl.GL11;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.PlayerTeam;
import net.jamezo97.clonecraft.clone.sync.Syncer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.StatCollector;

public class ButtonCloneTeam extends GuiColourButton{

	PlayerTeam team;
	
	int colourSelected;
	
	EntityClone clone;
	
	public ButtonCloneTeam(int par1, int par2, int par3, int par4, int par5, PlayerTeam team, EntityClone clone) {
		super(par1, par2, par3, par4, par5, "");
		this.clone = clone;
		this.displayString = StatCollector.translateToLocal(team.getUnloc());
		int colour = team.teamColour | 0xff000000;
		
		int red = (colour>>16) & 0xff;
		int green = (colour>>8) & 0xff;
		int blue = (colour) & 0xff;
		
		int colourOutline;
		{
			if(red > green && red > blue){
				red += 100;
				green -= 100;
				blue -= 100;
			}else if(green > red && green > blue){
				red -= 100;
				green += 100;
				blue -= 100;
			}else if(blue > green && blue > red){
				red -= 100;
				green -= 100;
				blue += 100;
			}else{
				red += 100;
				green += 100;
				blue += 100;
			}
			if(red > 255){red = 255;}else if(red < 0){red = 0;}
			if(green > 255){green = 255;}else if(green < 0){green = 0;}
			if(blue > 255){blue = 255;}else if(blue < 0){blue = 0;}
			colourOutline = 0xff000000 | (red << 16) | (green << 8) | blue;
		}
		red = 255-red;
		green = 255-green;
		blue = 255-blue;
		
		red += 80;
		green += 80;
		blue += 80;
		if(red > 255){red = 255;}else if(red < 0){red = 0;}
		if(green > 255){green = 255;}else if(green < 0){green = 0;}
		if(blue > 255){blue = 255;}else if(blue < 0){blue = 0;}
		colourSelected = 0xff000000 | ((red)<<16) | ((green)<<8) | (blue);
		
		
		red = (colour>>16) & 0xff;
		green = (colour>>8) & 0xff;
		blue = (colour) & 0xff;
		int colourFrom;
		{
			red -= 90;
			green -= 90;
			blue -= 90;
			if(red > 255){red = 255;}else if(red < 0){red = 0;}
			if(green > 255){green = 255;}else if(green < 0){green = 0;}
			if(blue > 255){blue = 255;}else if(blue < 0){blue = 0;}
			colourFrom = 0xff000000 | (red << 16) | (green << 8) | blue;
		}
		int colourTo = colour;
		this.setColours(colourFrom, colourTo, colourOutline);
		this.team = team;
	}
	
	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
		boolean pressed = super.mousePressed(par1Minecraft, par2, par3);
		if(pressed){
			clone.team = this.team;
			clone.getWatcher().sendValueToServer(Syncer.ID_TEAM);
		}
		return pressed;
	}

	@Override
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
		if(this.selected){
//			System.out.println("Draw");
			int size = 2;
			
			this.drawTriColourRectVert(this.xPosition-size, this.yPosition-size, this.xPosition+width+size, this.yPosition+height+size, this.xPosition + width/2, 0xffff0000, 0xff00ff00, 0xff0000ff);
			
//			this.drawRect(this.xPosition-size, this.yPosition-size, this.xPosition+width+size, this.yPosition+height+size, colourSelected);
		}
		
		super.drawButton(p_146112_1_, p_146112_2_, p_146112_3_);
	}
	
	public void drawTriColourRectVert(int xPos, int yPos, int xPos2, int yPos2, int midPosX, int colourA, int colourB, int colourC){
		
		
		
		
		float a1 = (float)(colourA >> 24 & 255) / 255.0F;
        float r1 = (float)(colourA >> 16 & 255) / 255.0F;
        float g1 = (float)(colourA >> 8 & 255) / 255.0F;
        float b1 = (float)(colourA & 255) / 255.0F;
        float a2 = (float)(colourB >> 24 & 255) / 255.0F;
        float r2 = (float)(colourB >> 16 & 255) / 255.0F;
        float g2 = (float)(colourB >> 8 & 255) / 255.0F;
        float b2 = (float)(colourB & 255) / 255.0F;
        float a3 = (float)(colourC >> 24 & 255) / 255.0F;
        float r3 = (float)(colourC >> 16 & 255) / 255.0F;
        float g3 = (float)(colourC >> 8 & 255) / 255.0F;
        float b3 = (float)(colourC & 255) / 255.0F;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r1, g1, b1, a1);
        tessellator.addVertex((double)xPos, (double)yPos, (double)this.zLevel);
        tessellator.addVertex((double)xPos, (double)yPos2, (double)this.zLevel);
        tessellator.setColorRGBA_F(r2, g2, b2, a2);
        tessellator.addVertex((double)midPosX, (double)yPos2, (double)this.zLevel);
        tessellator.addVertex((double)midPosX, (double)yPos, (double)this.zLevel);
        tessellator.draw();
        
        
        tessellator.startDrawingQuads();
        tessellator.setColorRGBA_F(r2, g2, b2, a2);
        tessellator.addVertex((double)midPosX, (double)yPos, (double)this.zLevel);
        tessellator.addVertex((double)midPosX, (double)yPos2, (double)this.zLevel);
        tessellator.setColorRGBA_F(r3, g3, b3, a3);
        tessellator.addVertex((double)xPos2, (double)yPos2, (double)this.zLevel);
        tessellator.addVertex((double)xPos2, (double)yPos, (double)this.zLevel);
        tessellator.draw();
        
        
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		
		
		
		
//		int j1;
//
//        if (xPos < xPos2)
//        {
//            j1 = xPos;
//            xPos = xPos2;
//            xPos2 = j1;
//        }
//
//        if (yPos < yPos2)
//        {
//            j1 = yPos;
//            yPos = yPos2;
//            yPos2 = j1;
//        }
//
//        
//        
//        float a1 = (float)(colourA >> 24 & 255) / 255.0F;
//        float r1 = (float)(colourA >> 16 & 255) / 255.0F;
//        float g1 = (float)(colourA >> 8 & 255) / 255.0F;
//        float b1 = (float)(colourA & 255) / 255.0F;
//        
//        
//        float a2 = (float)(colourA >> 24 & 255) / 255.0F;
//        float r2 = (float)(colourA >> 16 & 255) / 255.0F;
//        float g2 = (float)(colourA >> 8 & 255) / 255.0F;
//        float b2 = (float)(colourA & 255) / 255.0F;
//        
//        
//        float a3 = (float)(colourA >> 24 & 255) / 255.0F;
//        float r3 = (float)(colourA >> 16 & 255) / 255.0F;
//        float g3 = (float)(colourA >> 8 & 255) / 255.0F;
//        float b3 = (float)(colourA & 255) / 255.0F;
//        Tessellator tessellator = Tessellator.instance;
//        GL11.glEnable(GL11.GL_BLEND);
//        GL11.glDisable(GL11.GL_TEXTURE_2D);
//        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
//        
//        tessellator.setColorRGBA_F(a1, r1, g1, b1);
//        tessellator.setColorRGBA_F(a2, r2, g2, b2);
//        tessellator.setColorRGBA_F(a3, r3, g3, b3);
//        
//        
//        tessellator.startDrawingQuads();
//        tessellator.setColorRGBA_F(a1, r1, g1, b1);
//        tessellator.addVertex((double)xPos, (double)yPos2, 0.0D);
//        tessellator.setColorRGBA_F(a2, r2, g2, b2);
//        tessellator.addVertex((double)midPosX, (double)yPos2, 0.0D);
//        tessellator.addVertex((double)midPosX, (double)yPos, 0.0D);
//        tessellator.setColorRGBA_F(a1, r1, g1, b1);
//        tessellator.addVertex((double)xPos, (double)yPos, 0.0D);
//        
//
//        tessellator.setColorRGBA_F(a2, r2, g2, b2);
//        tessellator.addVertex((double)midPosX, (double)yPos2, 0.0D);
//        tessellator.setColorRGBA_F(a3, r3, g3, b3);
//        tessellator.addVertex((double)xPos2, (double)yPos2, 0.0D);
//        tessellator.addVertex((double)xPos2, (double)yPos, 0.0D);
//        tessellator.setColorRGBA_F(a2, r2, g2, b2);
//        tessellator.addVertex((double)midPosX, (double)yPos, 0.0D);
//        
//        tessellator.draw();
//        GL11.glEnable(GL11.GL_TEXTURE_2D);
//        GL11.glDisable(GL11.GL_BLEND);
	}

	


	boolean selected = false;
	
	public void setSelected(boolean b) {
		selected = b;
	}

	
	

	
	
}
