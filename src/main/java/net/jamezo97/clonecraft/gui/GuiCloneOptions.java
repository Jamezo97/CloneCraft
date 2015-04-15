package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.clone.CloneOption;
import net.jamezo97.clonecraft.clone.CloneOptions;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.sync.Syncer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Keyboard;

public class GuiCloneOptions extends GuiScreen{

	GuiScreen parent;

	EntityClone clone;

	public GuiCloneOptions(EntityClone clone, GuiScreen parent){
		this.clone = clone;
		this.parent = parent;
	}
	
	int buttonWidth = 140;
	int buttonHeight = 20;
	int spacing = 5;

	//Top section height
	int topSectionHeight = 80;
	//Top section + Name text field
	int topSectionHeight_b = 80+buttonHeight;
	
	int maxAccross = 0;
	int maxDown = 0;
	int maxButtons = 0;

	
	
	GuiTextField nameBox;

	@Override
	public void initGui() {
		
		initGuiComponents();

		
		
//		maxAccross = (width - spacing) / (buttonWidth + spacing);
//		maxDown = (height - spacing) / (buttonHeight + spacing);
//		maxButtons = maxAccross * maxDown;
//
//		CloneOptions op = clone.getOptions();
//		for(int a = 0; a < op.size(); a++){
//			CloneOption option = op.getOptionByIndex(a);
//			if(option != null){
//				buttonList.add(new ButtonCloneOption(-100, getX(a+1), getY(a+1), buttonWidth, buttonHeight, option));
//			}
//		}
//		buttonList.add(new GuiButton(0, getX((maxButtons-1)), getY((maxButtons-1)), buttonWidth, buttonHeight, "Done"));
//
//		nameBox = new GuiTextField(mc.fontRenderer, getX(0), getY(0), buttonWidth, buttonHeight);
//		nameBox.setText(clone.getFullEditedName());
//		lastTickName = clone.getFullEditedName();
		
		
		
		nameBox = new GuiTextField(mc.fontRenderer, spacing, 80+spacing, width-spacing*2, buttonHeight);
		nameBox.setText(clone.getFullEditedName());
		lastTickName = clone.getFullEditedName();
		Keyboard.enableRepeatEvents(true);	
	}
	
	public void initGuiComponents(){
		
		
		int w = width;
		int h = height-topSectionHeight_b;
		CloneOptions op = clone.getOptions();
		int optionsAmount = op.size()+1;

		int minButtonWidth = 100;
		int minButtonHeight = 20;
		
		int qAccross = 2;
		int qDown = (int)Math.ceil(optionsAmount/((double)qAccross));
		
		int bWidth = (w-spacing)/qAccross-spacing;
		int bHeight = (h-spacing)/qDown-spacing;
		
		while(bHeight < 18){
			qAccross++;
			qDown = (int)Math.ceil(optionsAmount/((double)qAccross));
			
			bWidth = (w-spacing)/qAccross-spacing;
			bHeight = (h-spacing)/qDown-spacing;
			if(bWidth < minButtonWidth){
				qAccross--;
				qDown = (int)Math.ceil(optionsAmount/((double)qAccross));
				
				bWidth = (w-spacing)/qAccross-spacing;
				bHeight = (h-spacing)/qDown-spacing;
				break;
			}
		}
		
		float scaleW = (float)(bWidth) / ((float)minButtonWidth);
		float scaleH = (float)(bHeight) / ((float)minButtonHeight);
		float scale = scaleW<scaleH?scaleW:scaleH;
		if(scale < 1){
			scale = 1;
		}else{
			int scalePoints = 256;
			while(scalePoints >= 1){
				if(scale > scalePoints){
					scale = scalePoints;
					break;
				}
				scalePoints /= 2;
			}
		}
		int a = 0;
		for(; a < optionsAmount-1; a++){
			CloneOption option = op.getOptionByIndex(a);
			if(option != null){
				buttonList.add(new ButtonCloneOption(-100, spacing+(spacing+bWidth)*(a/qDown), spacing+topSectionHeight_b+spacing+(spacing+bHeight)*(a%qDown), bWidth, bHeight, option, scale));
			}
		}
		
		buttonList.add(new GuiColourButton(0, spacing+(spacing+bWidth)*(a/qDown), spacing+topSectionHeight_b+spacing+(spacing+bHeight)*(a%qDown), bWidth, bHeight, "Done")
		.setColours(0xff888888, 0xff777777, 0xffcccccc).setScale(scale));
		
		
		
	}


	@Override
	public void drawScreen(int par1, int par2, float par3) {
		drawDefaultBackground();


		nameBox.drawTextBox();
		
		
		super.drawScreen(par1, par2, par3);
		
		
		
		for(int a = 0; a < 8; a++){
			this.drawRect(0+a, 0+a, width-a, topSectionHeight-a, 0x22aaaaaa);
			
		}
		
		for(int a = 0; a < buttonList.size(); a++){
			if(buttonList.get(a) instanceof ButtonCloneOption){
				if(((ButtonCloneOption)buttonList.get(a)).isHovering(par1, par2)){
					CloneOption option = ((ButtonCloneOption)buttonList.get(a)).option;
					String info = option.getInfo();
					int sWidth = mc.fontRenderer.getStringWidth(info);
					String[] data;
					if(sWidth > width/1.5){
						data = splitString(info, (int)Math.ceil( sWidth/((double)(width/1.5)) ));
					}else{
						data = new String[]{info};
					}
					
					int middleY = topSectionHeight/2-4;
					
					int txtHeight = 7;
					int spacing = 4;
					int totalHeight = data.length*(txtHeight+spacing)-spacing;
					int heightHalf = totalHeight/2;
					
					for(int b = 0; b < data.length; b++){
						int yPos = middleY - heightHalf + (b * (txtHeight+spacing));
						this.drawCenteredString(mc.fontRenderer, data[b], width/2, yPos, 0xffffffff);
						
					}
					break;
				}
			}
		}
	}
	
	public String[] splitString(String s, int sections){
		if(sections < 1){
			sections = 1;
		}
		String[] split = new String[sections];
		char[] characters = s.toCharArray();
		int startPos = 0;
		int endPos;
		for(int a = 0; a < sections; a++){
			endPos = (int)Math.ceil((s.length()*(a+1) / ((double)sections)));
			if(a+1 != sections){
				boolean broke = false;
				for(int b = endPos; b > startPos; b--){
					if(characters[b] == ' '){
						split[a] = s.substring(startPos, b);
						startPos = b+1;
						broke = true;
						break;
					}
				}
				if(broke){
					continue;
				}
			}else{
				endPos = s.length();
			}
			split[a] = s.substring(startPos, endPos);
			startPos = endPos;
			
		}
		return split;
	}

	@Override
	protected void keyTyped(char par1, int par2) {
		if(par2 == 1){
			mc.displayGuiScreen(parent);
			if(parent == null){
				mc.setIngameFocus();
			}
		}else{
			nameBox.textboxKeyTyped(par1, par2);
		}

	}

	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		super.mouseClicked(par1, par2, par3);
		nameBox.mouseClicked(par1, par2, par3);
	}

	@Override
	protected void actionPerformed(GuiButton gb) {
		if(gb.id == 0){
			mc.displayGuiScreen(parent);
			if(parent == null){
				mc.setIngameFocus();
			}
		}
	}

	String lastTickName = "";

	@Override
	public void updateScreen() {
		if(!lastTickName.equals(nameBox.getText())){
			lastTickName = nameBox.getText();
			clone.setName(lastTickName);
			clone.getWatcher().sendValueToServer(Syncer.ID_NAME);
		}else if(!nameBox.isFocused() && !nameBox.getText().equals(clone.nameUnedited)){
			nameBox.setText(clone.nameUnedited);
		}
		nameBox.updateCursorCounter();
	}

	
	
	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}



	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}



}
