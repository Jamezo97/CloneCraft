package net.jamezo97.clonecraft.gui;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatAllowedCharacters;

public class GuiOnlyTextField extends GuiTextField{

	String allowedChars;
	
	public GuiOnlyTextField(FontRenderer par1FontRenderer, int par2, int par3, int par4, int par5, String allowedChars) {
		super(par1FontRenderer, par2, par3, par4, par5);
		this.allowedChars = allowedChars;
	}
	
	public boolean textboxKeyTyped(char p_146201_1_, int p_146201_2_){
		if(!isAllowedCharacter(p_146201_1_)){
			return false;
		}
		return super.textboxKeyTyped(p_146201_1_, p_146201_2_);
    }
	
	private boolean isAllowedCharacter(char par1) {
		if(allowedChars.indexOf(par1) > -1){
			return true;
		}
		return false;
	}
	
	

}
