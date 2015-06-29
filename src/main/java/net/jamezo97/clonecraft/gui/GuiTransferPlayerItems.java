package net.jamezo97.clonecraft.gui;

import java.util.Collection;
import java.util.Iterator;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.InventoryClone;
import net.jamezo97.clonecraft.gui.container.ContainerTransferPlayerItems;
import net.jamezo97.clonecraft.network.Handler10ChangeOwner;
import net.jamezo97.clonecraft.network.Handler5TransferXP;
import net.jamezo97.clonecraft.network.Handler6KillClone;
import net.jamezo97.clonecraft.network.Handler7CloneClones;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiTransferPlayerItems extends GuiContainer{
	EntityClone clone;
	EntityPlayer thePlayer;
	boolean isGuiForDefault = false;

	protected static final ResourceLocation field_110330_c = new ResourceLocation("textures/gui/widgets.png");

	public GuiTransferPlayerItems(EntityClone otherPlayer, EntityPlayer mainPlayer, boolean b) {
		super(new ContainerTransferPlayerItems(mainPlayer, otherPlayer));
		clone = otherPlayer;
		thePlayer = mainPlayer;
		xSize = 176;
		ySize = 212;
		isGuiForDefault = b;
	}



	@Override
	protected void actionPerformed(GuiButton gb) {
		if(gb.id==0){
			new Handler5TransferXP(clone).sendToServer();
		}else if(gb.id == 1){
			mc.displayGuiScreen(new GuiCloneOptions(clone, this));
		}else if(gb.id == 2){
			mc.displayGuiScreen(new GuiCloneTeams(clone, this));
		}else if(gb.id == 3){
			buildUp += .19f;
			if(buildUp >= 1.0f){
				new Handler6KillClone(clone).sendToServer();
				mc.displayGuiScreen(null);
			}else{
				coloured.red = 1.0f;
				coloured.green = 1.0f-buildUp;
				coloured.blue = 1.0f-buildUp;
			}
		}
		else if(gb.id == 4){
			mc.displayGuiScreen(new GuiChooseAttackEntities(clone, this));
		}else if(gb.id == 5){
			mc.displayGuiScreen(new GuiChooseBlocksToBreak(clone, this));
		}else if(gb.id == 6){
			mc.displayGuiScreen(new GuiChooseSchematic(clone, this));
		}else if(gb.id == 7){
			new Handler10ChangeOwner(clone.getEntityId()).sendToServer();
		}else if(gb.id == 8){
			if(copyAmount != null){
				try{
					int amount = Integer.parseInt(copyAmount.getText());
					if(amount > 50){
						amount = 50;
					}
					if(amount > 0){
						Handler7CloneClones handler = new Handler7CloneClones(clone, amount);
						handler.sendToServer();
					}
				}catch(Exception e){
					mc.thePlayer.addChatMessage(new ChatComponentText("Could not parse text to number: " + copyAmount.getText()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				}	
			}
		}
	}

	GuiTextField copyAmount = null;

	GuiColouredButton coloured;
	float buildUp = 0;
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		buttonList.clear();
		int bottom = height-20;
		buttonList.add(new GuiButton(0, 5, 50, 100, 20, "Transfer XP"));
		buttonList.add(new GuiButton(1, 5, 75, 100, 20, "Options"));
		buttonList.add(new GuiButton(2, 5, 100, 100, 20, "Teams"));

		buttonList.add(new GuiButton(4, 5, 135, 110, 20, "Entities To Attack"));
		buttonList.add(new GuiButton(5, 5, 160, 110, 20, "Blocks To Break"));
		buttonList.add(new GuiButton(6, 5, 185, 110, 20, "Things To Build"));

		if(Minecraft.getMinecraft() != null){
			if(Minecraft.getMinecraft().getSession() == null || Minecraft.getMinecraft().getSession().getSessionID() == null || 
					Minecraft.getMinecraft().getSession().getSessionID().equals("-") || Minecraft.getMinecraft().thePlayer.capabilities.isCreativeMode){
				buttonList.add(new GuiButton(7, width-110, height-55, 100, 20, "Claim clone"));
			}
		}
		GuiButton copy;
		buttonList.add(copy = new GuiButton(8, width - 105, 30, 100, 20, "Copy Clone"));
		copyAmount = new GuiOnlyTextField(mc.fontRenderer, width-105, 5, 100, 20, "01234567890\b");
		if(!mc.playerController.isInCreativeMode()){
			copy.visible = false;
			copyAmount.setVisible(false);
		}

		buttonList.add(coloured = new GuiColouredButton(3, width-110, height-30, 100, 20, "Kill", 0xffffff, 0));
		super.initGui();
	}



	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
		if(copyAmount != null){
			copyAmount.drawTextBox();
		}
		
	}

	ResourceLocation backgroundImageResource = new ResourceLocation("clonecraft:textures/gui/transferplayeritems.png");

	protected void drawGuiContainerForegroundLayer()
	{
		mc.fontRenderer.drawString("Tranfer Items", ((xSize/2)-(mc.fontRenderer.getStringWidth("Tranfer Items")/2)), -8, 0xc0c0c0);
	}

	protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
	{
		mc.getTextureManager().bindTexture(backgroundImageResource);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int l = guiLeft;
		int i1 = guiTop;
		drawTexturedModalRect(l, i1, 0, 0, xSize, ySize);
		drawEffects();
		drawFood();
		drawXP();
		drawHealth();
		drawSelectedItemBox();
	}

	public void drawSelectedItemBox(){
		int x1 = guiLeft+7;
		int x2 = guiLeft + 24;
		int y1 = guiTop + ySize - 26;
		int y2 = guiTop + ySize - 9;
		x1 += clone.inventory.currentItem*18;
		x2 += clone.inventory.currentItem*18;
		int colour = 0xffeeee11;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		InventoryClone inventoryplayer = clone.inventory;
		this.drawRect(x1, y1, x2+1, y1+1, colour);
		this.drawRect(x2, y1, x2+1, y2+1, colour);
		this.drawRect(x1, y2, x2+1, y2+1, colour);
		this.drawRect(x1, y1, x1+1, y2+1, colour);
	}




	public boolean doesGuiPauseGame() {
		return false;
	}



	@Override
	protected void keyTyped(char par1, int par2) {
		if(copyAmount != null){
			copyAmount.textboxKeyTyped(par1, par2);
		}
		super.keyTyped(par1, par2);
	}

	long ticksOpen = 0;

	@Override
	public void updateScreen() {
		if(copyAmount != null){
			copyAmount.updateCursorCounter();
		}
		super.updateScreen();
	}



	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Keyboard.enableRepeatEvents(false);
	}

	private void drawHealth() {
		this.mc.getTextureManager().bindTexture(icons);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		int x = 10;
		int y = 10;
		double outOfHealth = ((double)clone.getMaxHealth())/2.0d;
		while(outOfHealth > 0){
			this.drawTexturedModalRect(x, y, 16, 0, 9, 9);
			outOfHealth = outOfHealth - 1;
			x = x + 9;
		}
		double health = ((double)clone.getHealth())/2.0d;

		x = 10;
		while(health > 0){
			if(health > .5){
				this.drawTexturedModalRect(x, y, 52, 0, 9, 9);
				health = health - 1;
			}else{
				this.drawTexturedModalRect(x, y, 61, 0, 9, 9);
				health = health - .5;
			}
			x = x + 9;
		}
	}



	@Override
	protected void mouseClicked(int i, int j, int k) {
		super.mouseClicked(i, j, k);
		copyAmount.mouseClicked(i, j, k);
	}

	private void drawXP() {
		this.mc.getTextureManager().bindTexture(icons);
		GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		int x = (width-182)/2;
		int y = height-10;
		float xpPercent = clone.experience;
		drawTexturedModalRect(x, y, 0, 64, 182, 5);
		drawTexturedModalRect(x, y, 0, 69, (int)Math.round(182f*xpPercent), 5);
		String s = "" + clone.experienceLevel;
		int k3 = (x+91)-((mc.fontRenderer.getStringWidth(s)) / 2);
		mc.fontRenderer.drawString(s, k3, height-12, 0x80ff20);
	}

	private void drawFood() {

		this.mc.getTextureManager().bindTexture(icons);
		int x = 10;
		int y = 25;
		int across = 10;
		int down = 1;
		this.drawRect(x-1, y-1, x+(across*9)+1, y+(down*9)+1, 0x50dddddd);
		double d = clone.foodStats.getFoodLevel();
		int add = 0;
		
		if (clone.isPotionActive(Potion.hunger))
		{
			add = 36;
		}
		
		double d1 = (d==0?0:d/2d);
		for(int a = 0; a < across; a++){
			for(int b = 0; b < down; b++){
				double level = 10-(b*5+a);
				int x1 = x + a*9;
				int y1 = y + b*9;
				byte type = 0;
				if(d1 > level-1 && d1 < level){
					type = 1;
				}else if(d1 >= level){
					type = 0;
				}else if(d1 < level){
					type = -1;
				}
				drawTexturedModalRect(x1, y1, 16, 27, 9, 9);
				if(type >= 0)drawTexturedModalRect(x1, y1, 52+(type*9)+add, 27, 9, 9);
			}
			clone.foodStats.getFoodLevel();
		}

	}
	
    private void drawEffects()
    {
        int i = this.guiLeft + xSize + 2;//this.guiLeft - 124;
        int j = this.guiTop + 40;
        boolean flag = true;
        Collection collection = this.clone.getActivePotionEffects();
        

        if (!collection.isEmpty())
        {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(GL11.GL_LIGHTING);
            int k = 33;

            if (collection.size() > 5)
            {
                k = 132 / (collection.size() - 1);
            }

            for (Iterator iterator = collection.iterator(); iterator.hasNext(); j += k)
            {
                PotionEffect potioneffect = (PotionEffect)iterator.next();
                Potion potion = Potion.potionTypes[potioneffect.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(field_147001_a);
                this.drawTexturedModalRect(i, j, 0, 166, 140, 32);

                if (potion.hasStatusIcon())
                {
                    int l = potion.getStatusIconIndex();
                    this.drawTexturedModalRect(i + 6, j + 7, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
                }

                potion.renderInventoryEffect(i, j, potioneffect, mc);
                if (!potion.shouldRenderInvText(potioneffect)) continue;
                String s1 = I18n.format(potion.getName(), new Object[0]);

                if (potioneffect.getAmplifier() == 1)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 2)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
                }
                else if (potioneffect.getAmplifier() == 3)
                {
                    s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
                }

                this.fontRendererObj.drawStringWithShadow(s1, i + 10 + 18, j + 6, 16777215);
                String s = Potion.getDurationString(potioneffect);
                this.fontRendererObj.drawStringWithShadow(s, i + 10 + 18, j + 6 + 10, 8355711);
            }
        }
    }







}
