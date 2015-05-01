package net.jamezo97.clonecraft.gui;


import net.jamezo97.framebuffer.FBException;
import net.jamezo97.framebuffer.FrameBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


public abstract class GuiScrollable extends Gui{
	
	int minSWidth;
	int minSHeight;
	int minScrollBarHeight;
	
	ResourceLocation scrollableTex;

	int xPosition, yPosition, width, height;

	boolean useFrameBuffer = false;
	
	FrameBuffer frameBuffer;
	
	int scrollBarWidth;
	int scrollBarBorder;
	int buttonHeight;
	
	int textureSize;
	
	int scale;
	float scaleI;
	
	//0 is at top
	//1 is scrolled completely to the bottom
	float scrolled = 0.0F;
	
	public GuiScrollable(int xPosition, int yPosition, int width, int height, ResourceLocation resource, 
			int scrollBarWidth, int scrollBarBorder, int buttonHeight, int textureSize) {
		
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		
		this.setDimensions(width, height, resource, scrollBarWidth, scrollBarBorder, buttonHeight, textureSize);
		
//		try {
			frameBuffer = new FrameBuffer((int)((width-scrollBarWidth))*2, (int)((height))*2);
			frameBuffer.setClearColour(0x00000000);
//		} catch (FBException e) {
//			useFrameBuffer = false;
//			e.printStackTrace();
//		}
	}
	
	public GuiScrollable(int xPosition, int yPosition, int width, int height) {
		this(xPosition, yPosition, width, height, new ResourceLocation("clonecraft:textures/gui/ScrollableTex2.png"), 
				9, 2, 7, 64);
	}
	
	int colour_BG = 0xffaaaaaa;
	int colour_SCROLL = 0xffbbbbbb;
	
	public GuiScrollable setColourPallete(int background, int scrollbar){
		this.colour_BG = background;
		this.colour_SCROLL = scrollbar;
		return this;
	}
	
	public void setDimensions(int width, int height, 
			ResourceLocation resource, 
			int scrollBarWidth, int scrollBarBorder, int buttonHeight, int textureSize){
		
		this.scrollableTex = resource;
		
		this.scrollBarWidth = scrollBarWidth;
		this.scrollBarBorder = scrollBarBorder;
		this.buttonHeight = buttonHeight;
		
		this.scale = 256 / textureSize;
		this.scaleI = 1.0f / scale;
		
		this.textureSize = textureSize;
		
		minScrollBarHeight = scrollBarBorder*2+1;
		
		minSWidth = scrollBarWidth+1;
		minSHeight = minScrollBarHeight + buttonHeight*2;
		
		if(width < minSWidth){width = minSWidth;}
		if(height < minSHeight){height = minSHeight;}
		
		this.width = width;
		this.height = height;
	}
	
	public boolean mousePress(int mX, int mY, int button){
		if(mX >= xPosition && mX < xPosition+width-this.scrollBarWidth && mY >= yPosition && mY < yPosition+height){
			float entriesViewableFloat = ((float)this.height) / ((float)this.getEntryHeight());
			//The entry to begin with, including partitions
			float beginEntryFloat = scrolled * (((float)this.getEntryCount()-entriesViewableFloat));
			//If this value is less than 0(when there is no scrollbar needed), force the value to be 0
			if(beginEntryFloat < 0){beginEntryFloat = 0;}
			int beginEntryFloor = (int)Math.floor(beginEntryFloat);
//			System.out.println(this.height + ", " + this.getEntryHeight());
			//The percentage of entry cut off (little - big = negative value)
			float difference = beginEntryFloor - beginEntryFloat;
			//The beginning renderPosition (percent of missing entry multiplied by the height of the entry)
			int beginRenderY = (int)Math.floor(difference * this.getEntryHeight());

			int maxEntryCount = (int)Math.ceil(entriesViewableFloat)+1;
			
			int minY = beginRenderY + this.yPosition;
			int maxY = minY + this.getEntryHeight();
			
			for(int a = beginEntryFloor; a < beginEntryFloor+maxEntryCount; a++){
				if(a >= this.getEntryCount()){
					break;
				}
				
				if(mY >= minY && mY < maxY){
					this.entryClicked(a);
				}
				
				minY += this.getEntryHeight();
				maxY += this.getEntryHeight();
			}
			
			return true;
		}
		return false;
	}
	
	public boolean isMouseOver(int mX, int mY){
		return mX >= xPosition && mX < xPosition+width && mY >= yPosition && mY < yPosition+height;
	}
	
	
	
	int totalHeight, barSectionHeight, scrollBarHeight, positionDown;
	
	public void updateScrollBarPosition(){
		totalHeight = this.getEntryCount() * this.getEntryHeight();
		barSectionHeight = this.height - (this.buttonHeight*2);
		if(totalHeight == 0){
			scrollBarHeight = barSectionHeight;
		}else{
			scrollBarHeight = barSectionHeight*barSectionHeight/totalHeight;
		}
		
		if(scrollBarHeight > barSectionHeight){
			scrollBarHeight = barSectionHeight;
		}else if(scrollBarHeight < minScrollBarHeight){
			scrollBarHeight = minScrollBarHeight;
		}
		positionDown = (int)Math.round((barSectionHeight - scrollBarHeight) * scrolled)+this.buttonHeight;
	}
	int bmx, bmy;
	
	float bScrolled;
	
	int lmx, lmy;
	
	int mouseDown;
	
	int mouseDownTimer = 0;
	
	public void updateMouse(int mX, int mY){
		int dWheel;
		if((dWheel = Mouse.getDWheel()) != 0){
			dWheel *= dWheel * (dWheel<0?1:-1);
			this.scrolled += ((float)dWheel) / ((float)totalHeight)/1000.0f;
			
		}
		if(Mouse.isButtonDown(0)){
			mouseDownTimer++;
			if(mouseDown == 1){
				updateScrollBarPosition();
				
				int dy = mY - bmy;
				
				this.scrolled = bScrolled + (((float)dy) / ((float)barSectionHeight-scrollBarHeight));
				
			}else if(mouseDown == 2){
				if(mouseDownTimer > 50){
					this.scrolled -= Math.min(((Math.pow(mouseDownTimer-50, 1.7)) / ((float)16))+2, totalHeight/10.0f)/((float)totalHeight);
				}else{
					this.scrolled -= 1.0f/((float)totalHeight);
				}
				
			}else if(mouseDown == 3){
				if(mouseDownTimer > 50){
					this.scrolled += Math.min(((Math.pow(mouseDownTimer-50, 1.7)) / ((float)16))+2, totalHeight/10.0f)/((float)totalHeight);
				}else{
					this.scrolled += 1.0f/((float)totalHeight);
				}
			}else{
				if(mX >= this.xPosition + width-9 && mX < this.xPosition+width){
					updateScrollBarPosition();
					if(mY > this.yPosition + positionDown && mY < this.yPosition + positionDown + scrollBarHeight){
						mouseDown = 1;
						this.bmx = mX;
						this.bmy = mY;
						this.bScrolled = scrolled;
					}else if(mY >= this.yPosition && mY < this.yPosition + (this.buttonHeight)){
						//Scroll Up Button
						mouseDown = 2;
					}else if(mY >= this.yPosition + height - (this.buttonHeight) && mY < this.yPosition + height){
						//Scroll Down Button
						mouseDown = 3;
					}else if(mY >= this.yPosition && mY < this.yPosition + height){
						int mYBase = mY - (this.yPosition+(this.buttonHeight)) - (this.scrollBarHeight/2);
						scrolled = mYBase / ((float)barSectionHeight-scrollBarHeight);
						//Empty Bar Spot
					}
				}
			}
		}else{
			mouseDownTimer = 0;
			mouseDown = 0;
		}
		lmx = mX;
		lmy = mY;
		if(scrolled > 1){
			scrolled = 1;
		}else if(scrolled < 0){
			scrolled = 0;
		}
	}
	
	
	public void draw(int mX, int mY){
		totalHeight = this.getEntryCount() * this.getEntryHeight();
		
		this.updateMouse(mX, mY);
		
		frameBuffer.beginRender();
		GL11.glDisable(GL11.GL_CULL_FACE);
		GL11.glScalef(2.0f, 2.0f, 2.0f);
		this.doScrollDraw(mX, mY);
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		GL11.glEnable(GL11.GL_CULL_FACE);
		frameBuffer.endRender();
		
		
		this.drawRect(xPosition, yPosition, xPosition+width-this.scrollBarWidth, yPosition+height, this.colour_BG);
		
		this.drawRect(xPosition+width-this.scrollBarWidth, yPosition, xPosition+width, yPosition+height, this.colour_SCROLL);
		
		
		
		//Render Scrollable Section
		frameBuffer.bindTexture();
		float u = frameBuffer.getUMax();
		float v = frameBuffer.getVMax();
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glTexCoord2f(0, 0);
		GL11.glVertex3f(xPosition, yPosition, 0);
		GL11.glTexCoord2f(0, v);
		GL11.glVertex3f(xPosition, yPosition+height, 0);
		GL11.glTexCoord2f(u, v);
		GL11.glVertex3f(xPosition+width-scrollBarWidth, yPosition+height, 0);
		GL11.glTexCoord2f(u, 0);
		GL11.glVertex3f(xPosition+width-scrollBarWidth, yPosition, 0);
		GL11.glEnd();
		
		Minecraft.getMinecraft().renderEngine.bindTexture(scrollableTex);
//		int s = 8;
//		float sI = 0.125F;
		
		//28
		int innerBarHeight = this.textureSize - this.scrollBarBorder*2;
		//30
		int bottomTexBegin = this.textureSize - this.scrollBarBorder;
		
		GL11.glScalef(scaleI, scaleI, scaleI);
		int minX = xPosition+width-scrollBarWidth;
		
		if(this.mouseDown == 2){
			this.drawTexturedModalRect((minX)*scale, yPosition*scale, 0, buttonHeight*2*scale, scrollBarWidth*scale, buttonHeight*scale);
		}else{
			this.drawTexturedModalRect((minX)*scale, yPosition*scale, 0, 0, scrollBarWidth*scale, buttonHeight*scale);
		}
		
		if(this.mouseDown == 3){
			this.drawTexturedModalRect((minX)*scale, (yPosition+height-buttonHeight)*scale, 0, (buttonHeight*3)*scale, scrollBarWidth*scale, buttonHeight*scale);
		}else{
			this.drawTexturedModalRect((minX)*scale, (yPosition+height-buttonHeight)*scale, 0, buttonHeight*scale, scrollBarWidth*scale, buttonHeight*scale);
		}
		
		
		
		
		updateScrollBarPosition();
		
		int minXU = scrollBarWidth;
		
		if(this.mouseDown == 1){
			minXU += this.scrollBarWidth;
		}
		
		this.drawTexturedModalRect((minX)*scale, (yPosition + positionDown)*scale, minXU*scale, 0, scrollBarWidth*scale, scrollBarBorder*scale);
		this.drawTexturedModalRect((minX)*scale, (yPosition + positionDown + scrollBarHeight - scrollBarBorder)*scale, minXU*scale, bottomTexBegin*scale, scrollBarWidth*scale, scrollBarBorder*scale);
		
		int sections = (scrollBarHeight-(this.scrollBarBorder*2)) / innerBarHeight+1;

		
		for(int a = 0; a < sections ; a++){
			if(a + 1 == sections){
				this.drawTexturedModalRect((minX)*scale, (yPosition + scrollBarBorder + positionDown + a*innerBarHeight)*scale, minXU*scale, scrollBarBorder*scale, scrollBarWidth*scale, (scrollBarHeight-(this.scrollBarBorder*2) - a*innerBarHeight)*scale);
			}else{
				this.drawTexturedModalRect((minX)*scale, (yPosition + scrollBarBorder + positionDown + a*innerBarHeight)*scale, minXU*scale, scrollBarBorder*scale, scrollBarWidth*scale, innerBarHeight*scale);
			}
		}
		GL11.glScalef(scale, scale, scale);
	}
	
	

	private void doScrollDraw(int mX, int mY) {
		//The amount of entries visible, including cut off parts (as decimal)
		float entriesViewableFloat = ((float)this.height) / ((float)this.getEntryHeight());
		//The entry to begin with, including partitions
		float beginEntryFloat = (scrolled * (((float)this.getEntryCount()-entriesViewableFloat)));
		//If this value is less than 0(when there is no scrollbar needed), force the value to be 0
		if(beginEntryFloat < 0){beginEntryFloat = 0;}
		int beginEntryFloor = (int)Math.floor(beginEntryFloat);
//		System.out.println(this.height + ", " + this.getEntryHeight());
		//The percentage of entry cut off (little - big = negative value)
		float difference = beginEntryFloor - beginEntryFloat;
		//The beginning renderPosition (percent of missing entry multiplied by the height of the entry)
		int beginRenderY = (int)Math.ceil(difference * this.getEntryHeight());

		int maxEntryCount = (int)Math.ceil(entriesViewableFloat)+1;
		GL11.glPushMatrix();
		GL11.glTranslatef(0, beginRenderY-this.getEntryHeight(), 0);
		for(int a = beginEntryFloor-1; a < beginEntryFloor+maxEntryCount+1; a++){
			if(a >= this.getEntryCount()){
				break;
			}else if(a < 0){
				GL11.glTranslatef(0, this.getEntryHeight(), 0);
				continue;
			}
			this.renderEntry(a, this.width - scrollBarWidth, this.getEntryHeight());
			GL11.glTranslatef(0, this.getEntryHeight(), 0);
		}
		GL11.glPopMatrix();
		
	}
	
	
	
	
	public abstract boolean isEntrySelected(int entryIndex);
	
	public abstract int getEntryCount();
	
	public abstract int getEntryHeight();
	
	public abstract void entryClicked(int entryIndex);
	
	public abstract void renderEntry(int entryIndex, int width, int height);
	
	
	

}
