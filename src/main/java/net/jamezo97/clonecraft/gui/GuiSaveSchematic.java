package net.jamezo97.clonecraft.gui;

import java.io.File;

import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.schematic.Schematic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class GuiSaveSchematic extends GuiScreen{
	
	GuiRenderSchematic renderSchem = null;
	
	GuiTextField text;
	
	Schematic theSchematic = null;
	
	int infoWidth = 200;
	
	String info = "";
	

	public GuiSaveSchematic(ChunkCoordinates pos1, ChunkCoordinates pos2)
	{
		super();
		
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.theWorld;
		
		if(world != null)
		{
			int minX = Math.min(pos1.posX, pos2.posX);
			int minY = Math.min(pos1.posY, pos2.posY);
			int minZ = Math.min(pos1.posZ, pos2.posZ);
			
			int maxX = Math.max(pos1.posX, pos2.posX);
			int maxY = Math.max(pos1.posY, pos2.posY);
			int maxZ = Math.max(pos1.posZ, pos2.posZ);
			
			int size = (maxX-minX) * (maxY-minY) * (maxZ-minZ);
			
			int maxSize = 200*200*200;
			
			if(size < maxSize)
			{
				theSchematic = Schematic.createSchematic(minX, minY, minZ, maxX, maxY, maxZ, world);
			}
		}
		
		infoWidth = 200;
		
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partial)
	{
		this.drawDefaultBackground();
		this.drawRect(200, 0, width, height, 0x99ffffff);
		
		this.drawString(mc.fontRenderer, "Schematic Name:", 5, 5, 0xffffffff);
		text.drawTextBox();
		
		this.drawString(mc.fontRenderer, "Schematic Stats:", 5, 50, 0xff99ddff);
		
		if(this.theSchematic != null)
		{
			int size = theSchematic.blockIds.length*2 + theSchematic.tileEntities.size() * 50;
			
			String suffix = "";
			if(size < 10000)
			{
				suffix = " Bytes";
			}
			else
			{
				suffix = " KBytes";
				size /= 1024;
			}

			
			Object[][] twoParts = new Object[][]{
					{"Width: ", theSchematic.xSize + ""},
					{"Height: ", theSchematic.ySize + ""},
					{"Length: ", theSchematic.zSize + "", 5},
					{"Block Count: ", theSchematic.blockIds.length + ""},
					{"TileEntity Count: ", theSchematic.tileEntities.size() + "", 5},
					{"HashCode: ", Long.toHexString(theSchematic.myHashCode())}
			};
			
			int max = 10;
			
			for(int a = 0; a < twoParts.length; a++)
			{
				int len = mc.fontRenderer.getStringWidth(twoParts[a][0].toString());
				if(len > max)
				{
					max = len;
				}
			}
			max += 10;
			
			int y = 70;
			
			for(int a = 0; a < twoParts.length; a++)
			{
				this.drawString(mc.fontRenderer, twoParts[a][0].toString(), max - mc.fontRenderer.getStringWidth(twoParts[a][0].toString()), y, 0xbbbbff);
				this.drawString(mc.fontRenderer, twoParts[a][1].toString(), max, y, 0xffffff);
				
				y += 10;
				
				if(twoParts[a].length > 2)
				{
					y += (Integer)twoParts[a][2];
				}
			}
			
			if(info != null && info.length() > 0)
			{
				this.drawString(mc.fontRenderer, info, (infoWidth - mc.fontRenderer.getStringWidth(info))/2, height - 70, 0xffdd5533);
			}
			
			
			
			//info
			
		}
		
		
		
		
		super.drawScreen(mouseX, mouseY, partial);
	}

	@Override
	protected void keyTyped(char character, int charCode)
	{
		super.keyTyped(character, charCode);
		renderSchem.keyTyped(character, charCode);
		
		String s1 = text.getText();
		text.textboxKeyTyped(character, charCode);
		
		if(!s1.equals(text.getText()))
		{
			this.setOverwriteState(false);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int button)
	{
		super.mouseClicked(mouseX, mouseY, button);
		renderSchem.mouseClicked(mouseX, mouseY, button);
		text.mouseClicked(mouseX, mouseY, button);
		
	}

	@Override
	protected void mouseClickMove(int mX, int mY, int lastButton, long downTime)
	{
		super.mouseClickMove(mX, mY, lastButton, downTime);
		renderSchem.mouseClickMove(mX, mY, lastButton, downTime);
	}

	@Override
	protected void actionPerformed(GuiButton button) 
	{
		if(button.id == 0 || button.id == 1)
		{
			if(this.theSchematic != null)
			{
				String name = this.text.getText();
				
				if(name.length() > 0)
				{
					this.theSchematic.name = name;
					

					File baseFolder = new File(CloneCraft.INSTANCE.getDataDir(), "Schematics");
					
					File saveTo = new File(baseFolder, this.theSchematic.name + ".schematic");
					
					if(!saveTo.getParentFile().exists())
					{
						saveTo.getParentFile().mkdirs();
					}

					if(!saveTo.exists() || currentState)
					{
						this.theSchematic.saveTo(saveTo);
						this.setOverwriteState(false);
						
						this.mc.displayGuiScreen(null);
						this.mc.setIngameFocus();
						
						if(this.mc.thePlayer != null)
						{
							this.mc.thePlayer.addChatMessage(new ChatComponentText("Schematic '" + name + "' saved!").
									setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
							CloneCraft.INSTANCE.schematicList.reloadSchematics();
						}
					}
					else
					{
						this.setOverwriteState(true);
					}
					
					
				}
			}
		}
		else if(button.id == 2)
		{
			this.setOverwriteState(false);
		}
	}
	
	boolean currentState = false;
	
	boolean hasNewTickOccured = true;
	
	public void setOverwriteState(boolean state)
	{
		if(hasNewTickOccured)
		{
			hasNewTickOccured = false;
			save.visible = save.enabled = !state;
			noCancel.visible = noCancel.enabled = state;
			yesSave.visible = yesSave.enabled = state;
			currentState = state;
			
			if(state)
			{
				this.info = "A file with that name already exists!";
			}
			else
			{
				this.info = "";
			}
		}
		
	}
	
	

	@Override
	public boolean doesGuiPauseGame() 
	{
		return false;
	}

	GuiButton save, noCancel, yesSave;
	
	@Override
	public void initGui()
	{
		renderSchem = new GuiRenderSchematic(200, 0, width-200, height);
		renderSchem.toRender = theSchematic;
		renderSchem.doRender = true;
		
		this.buttonList.add(this.renderSchem);
		this.buttonList.add(save = new GuiButton(0, 5, height - 25, infoWidth-10, 20, "Save Schematic"));
		this.buttonList.add(yesSave = new GuiButton(1, 5, height - 50, infoWidth-10, 20, "Yes Overwrite"));
		this.buttonList.add(noCancel = new GuiButton(2, 5, height - 25, infoWidth-10, 20, "No, I'll Rename it"));
		
		this.text = new GuiTextField(mc.fontRenderer, 5, 20, infoWidth-10, 20);
		
		
		setOverwriteState(false);
		
		if(this.theSchematic != null)
		{
			this.text.setText(this.theSchematic.name);
		}
	}

	@Override
	public void updateScreen()
	{
		if(this.theSchematic == null)
		{
			mc.displayGuiScreen(null);
			mc.setIngameFocus();
		}
		this.text.updateCursorCounter();
		hasNewTickOccured = true;
	}

	@Override
	public void onGuiClosed()
	{
		if(this.theSchematic != null)
		{
			this.theSchematic.cleanGPU();
		}
	}

	
	
}
