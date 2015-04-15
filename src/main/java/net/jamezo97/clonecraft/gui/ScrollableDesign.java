package net.jamezo97.clonecraft.gui;

public class ScrollableDesign {
	
	final float scale;
	final float scaleI;
	final int scrollBarWidth;
	final int scrollBarBeginEndSectionHeight;
	final int upDownButtonHeight;
	
	public ScrollableDesign(int scrollBarWidth, int scrollBarBeginEndSectionHeight, int upDownButtonHeight, int textureSize){
		this.scrollBarWidth = scrollBarWidth;
		this.scrollBarBeginEndSectionHeight = scrollBarBeginEndSectionHeight;
		this.upDownButtonHeight = upDownButtonHeight;
		this.scale = 256.0f / ((float)textureSize);
		this.scaleI = 1.0f / scale;
	}

}
