package net.jamezo97.clonecraft.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler
{
	public static SimpleNetworkWrapper net;

	public static void initPackets()
	{
		net = NetworkRegistry.INSTANCE.newSimpleChannel("CLONECRAFT");
		registerMessage(HandlerPacketHandler.class, HandlerPacket.class);
	}

	private static int nextPacketId = 0;

	private static void registerMessage(Class packet, Class message)
	{
		net.registerMessage(packet, message, nextPacketId, Side.CLIENT);
		net.registerMessage(packet, message, nextPacketId, Side.SERVER);
		nextPacketId++;
	}
}