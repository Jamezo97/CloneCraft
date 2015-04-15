package net.jamezo97.clonecraft.gui;

import org.lwjgl.opengl.GL11;

import net.jamezo97.clonecraft.clone.CloneOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;

public class ButtonCloneOption extends GuiColourButton{

	CloneOption option;
	
	public ButtonCloneOption(int par1, int par2, int par3, int par4, int par5, CloneOption option, float scale) {
		super(par1, par2, par3, par4, par5, "");
		this.option = option;
		updateText();
		this.scaleText = scale;
	}

	public void updateText(){
		this.displayString = option.getName() + ": " + (option.get()?"On":"Off");
	}

	@Override
	public boolean mousePressed(Minecraft par1Minecraft, int par2, int par3) {
		boolean pressed = super.mousePressed(par1Minecraft, par2, par3);
		if(pressed){
			option.set(!option.get());
		}
		return pressed;
	}

	@Override
	public void prepColours() {
		if(option.get()){
        	colourFrom = 0xff55aa55;
            colourTo = 0xff449944;
            colourOutline = 0xff55ff55;
        }else{
        	colourFrom = 0xffaa5555;
            colourTo = 0xff994444;
            colourOutline = 0xffff5555;
        }
	}

	@Override
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
		this.updateText();
		super.drawButton(p_146112_1_, p_146112_2_, p_146112_3_);
	}

	
	

}
