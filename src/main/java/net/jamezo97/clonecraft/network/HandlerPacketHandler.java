package net.jamezo97.clonecraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class HandlerPacketHandler implements IMessageHandler<HandlerPacket, IMessage>
{

	@Override
	public IMessage onMessage(HandlerPacket message, MessageContext ctx)
	{
		if (ctx.side.isClient())
		{
			message.handle(Side.CLIENT, getPlayerClient());
		}
		else
		{
			message.handle(Side.SERVER, getPlayerServer(ctx));
		}
		return null;
	}

	@SideOnly(value = Side.CLIENT)
	public EntityPlayer getPlayerClient()
	{
		return Minecraft.getMinecraft().thePlayer;
	}

	public EntityPlayer getPlayerServer(MessageContext ctx)
	{
		return ctx.getServerHandler().playerEntity;
	}
}
