package net.jamezo97.clonecraft.network;

import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.relauncher.Side;

public class HandlerPacket implements IMessage
{

	Handler handler;

	public HandlerPacket(Handler handler)
	{
		this.handler = handler;
	}

	public HandlerPacket()
	{
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		int id = buf.readInt();
		this.handler = Handler.getNewHandlerFromId(id);
		if (handler != null)
		{
			handler.read(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(Handler.getHandlerId(handler));
		if (handler != null)
		{
			handler.write(buf);
		}
	}

	public void handle(Side side, EntityPlayer player)
	{
		if (handler != null)
		{
			handler.handle(side, player);
		}
	}
}
