package net.jamezo97.clonecraft.gui;

import net.jamezo97.clonecraft.clone.CloneOption;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.clone.PlayerTeam;
import net.jamezo97.clonecraft.clone.sync.Syncer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

public class GuiCloneTeams extends GuiScreen
{

	GuiScreen parent;

	EntityClone clone;

	public GuiCloneTeams(EntityClone clone, GuiScreen parent)
	{
		this.clone = clone;
		this.parent = parent;
	}

	int minButtonWidth = 140;
	int minButtonHeight = 20;
	int spacing = 4;

	// Top section height
	int topSectionHeight = 80;

	@Override
	public void initGui()
	{

		initGuiComponents();

		Keyboard.enableRepeatEvents(true);
	}

	public void initGuiComponents()
	{

		int w = width;
		int h = height - topSectionHeight;
		PlayerTeam[] allTeams = PlayerTeam.values();

		int teamAmount = allTeams.length + 1;

		int minButtonWidth = 100;
		int minButtonHeight = 20;

		int qAccross = 2;
		int qDown = (int) Math.ceil(teamAmount / ((double) qAccross));

		int bWidth = (w - spacing) / qAccross - spacing;
		int bHeight = (h - spacing) / qDown - spacing;

		while (bHeight < 18)
		{
			qAccross++;
			qDown = (int) Math.ceil(teamAmount / ((double) qAccross));

			bWidth = (w - spacing) / qAccross - spacing;
			bHeight = (h - spacing) / qDown - spacing;
			if (bWidth < minButtonWidth)
			{
				qAccross--;
				qDown = (int) Math.ceil(teamAmount / ((double) qAccross));

				bWidth = (w - spacing) / qAccross - spacing;
				bHeight = (h - spacing) / qDown - spacing;
				break;
			}
		}

		float scaleW = (float) (bWidth) / ((float) minButtonWidth);
		float scaleH = (float) (bHeight) / ((float) minButtonHeight);
		float scale = scaleW < scaleH ? scaleW : scaleH;
		if (scale < 1)
		{
			scale = 1;
		}
		else
		{
			int scalePoints = 256;
			while (scalePoints >= 1)
			{
				if (scale > scalePoints)
				{
					scale = scalePoints;
					break;
				}
				scalePoints /= 2;
			}
		}
		int a = 0;
		for (; a < teamAmount - 1; a++)
		{
			buttonList.add(new ButtonCloneTeam(-100, spacing + (spacing + bWidth) * (a / qDown), spacing + topSectionHeight + spacing + (spacing + bHeight)
					* (a % qDown), bWidth, bHeight, allTeams[a], clone).setScale(scale));
		}

		buttonList.add(new GuiColourButton(0, spacing + (spacing + bWidth) * (a / qDown), spacing + topSectionHeight + spacing + (spacing + bHeight)
				* (a % qDown), bWidth, bHeight, "Done").setColours(0xff888888, 0xff777777, 0xffcccccc).setScale(scale));

	}

	@Override
	public void drawScreen(int par1, int par2, float par3)
	{
		drawDefaultBackground();

		int selectedColour = 0x22aaaaaa;

		for (int a = 0; a < buttonList.size(); a++)
		{
			if (buttonList.get(a) instanceof ButtonCloneTeam)
			{
				ButtonCloneTeam button = ((ButtonCloneTeam) buttonList.get(a));
				if (clone.getCTeam() == button.team)
				{
					button.setSelected(true);
					selectedColour = button.team.teamColour & 0x00ffffff | 0x22000000;
				}
				else
				{
					button.setSelected(false);
				}
			}
		}

		super.drawScreen(par1, par2, par3);

		for (int a = 0; a < 8; a++)
		{
			this.drawRect(0 + a, 0 + a, width - a, topSectionHeight - a, selectedColour);

		}

		for (int a = 0; a < buttonList.size(); a++)
		{
			if (buttonList.get(a) instanceof ButtonCloneTeam)
			{
				if (((ButtonCloneTeam) buttonList.get(a)).isHovering(par1, par2))
				{
					PlayerTeam team = ((ButtonCloneTeam) buttonList.get(a)).team;
					String info = team.getInfo();
					int sWidth = mc.fontRenderer.getStringWidth(info);
					String[] data;
					if (sWidth > width / 1.5)
					{
						data = splitString(info, (int) Math.ceil(sWidth / ((double) (width / 1.5))));
					}
					else
					{
						data = new String[]
						{ info };
					}

					int middleY = topSectionHeight / 2 - 4;

					int txtHeight = 7;
					int spacing = 4;
					int totalHeight = data.length * (txtHeight + spacing) - spacing;
					int heightHalf = totalHeight / 2;

					for (int b = 0; b < data.length; b++)
					{
						int yPos = middleY - heightHalf + (b * (txtHeight + spacing));
						this.drawCenteredString(mc.fontRenderer, data[b], width / 2, yPos, 0xffffffff);
					}
					break;
				}
			}
		}
	}

	public String[] splitString(String s, int sections)
	{
		if (sections < 1)
		{
			sections = 1;
		}
		String[] split = new String[sections];
		char[] characters = s.toCharArray();
		int startPos = 0;
		int endPos;
		for (int a = 0; a < sections; a++)
		{
			endPos = (int) Math.ceil((s.length() * (a + 1) / ((double) sections)));
			if (a + 1 != sections)
			{
				boolean broke = false;
				for (int b = endPos; b > startPos; b--)
				{
					if (characters[b] == ' ')
					{
						split[a] = s.substring(startPos, b);
						startPos = b + 1;
						broke = true;
						break;
					}
				}
				if (broke)
				{
					continue;
				}
			}
			else
			{
				endPos = s.length();
			}
			split[a] = s.substring(startPos, endPos);
			startPos = endPos;

		}
		return split;
	}

	@Override
	protected void keyTyped(char par1, int par2)
	{
		if (par2 == 1)
		{
			mc.displayGuiScreen(parent);
			if (parent == null)
			{
				mc.setIngameFocus();
			}
		}
	}

	@Override
	protected void actionPerformed(GuiButton gb)
	{
		if (gb.id == 0)
		{
			mc.displayGuiScreen(parent);
			if (parent == null)
			{
				mc.setIngameFocus();
			}
		}
	}

	@Override
	public void onGuiClosed()
	{
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean doesGuiPauseGame()
	{
		return false;
	}

}
