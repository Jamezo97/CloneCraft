package net.jamezo97.clonecraft;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.gui.GuiCentrifuge;
import net.jamezo97.clonecraft.gui.GuiLifeInducer;
import net.jamezo97.clonecraft.gui.GuiTransferPlayerItems;
import net.jamezo97.clonecraft.gui.container.ContainerCentrifuge;
import net.jamezo97.clonecraft.gui.container.ContainerLifeInducer;
import net.jamezo97.clonecraft.gui.container.ContainerTransferPlayerItems;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

	static GuiHandler INSTANCE;

	public GuiHandler()
	{
		INSTANCE = this;
	}

	// Return Containers
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == CENTRIFUGE)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityCentrifuge)
			{
				return new ContainerCentrifuge(player, (TileEntityCentrifuge) te);
			}
		}
		else if (ID == LIFEINDUCER)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityLifeInducer)
			{
				return new ContainerLifeInducer(player.inventory, (TileEntityLifeInducer) te);
			}
		}
		else if (ID == CLONE)
		{
			Entity e = world.getEntityByID(x);
			if (e instanceof EntityClone)
			{
				return new ContainerTransferPlayerItems(player, (EntityClone) e);
			}
		}
		return null;
	}

	// Return GUI's
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
	{
		if (ID == CENTRIFUGE)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityCentrifuge)
			{
				return new GuiCentrifuge(player, (TileEntityCentrifuge) te);
			}
		}
		else if (ID == LIFEINDUCER)
		{
			TileEntity te = world.getTileEntity(x, y, z);
			if (te != null && te instanceof TileEntityLifeInducer)
			{
				return new GuiLifeInducer(player.inventory, (TileEntityLifeInducer) te);
			}
		}
		else if (ID == CLONE)
		{
			Entity e = world.getEntityByID(x);
			if (e instanceof EntityClone)
			{
				return new GuiTransferPlayerItems((EntityClone) e, player, false);
			}
		}
		return null;
	}

	public static int CENTRIFUGE = 0;
	public static int LIFEINDUCER = 1;
	public static int CLONE = 2;
}