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

public class GuiHandler implements IGuiHandler{

	static GuiHandler INSTANCE;
	
	public GuiHandler(){
		INSTANCE = this;
	}
	
	//Return Containers
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == CENTRIFUGE){
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityCentrifuge){
				return new ContainerCentrifuge(player, (TileEntityCentrifuge)te);
			}
		}else if(ID == LIFEINDUCER){
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityLifeInducer){
				return new ContainerLifeInducer(player.inventory, (TileEntityLifeInducer)te);
			}
		}else if(ID == CLONE){
			Entity e = world.getEntityByID(x);
			if(e instanceof EntityClone){
				return new ContainerTransferPlayerItems(player, (EntityClone)e);
			}
		}
		return null;
	}

	//Return GUI's
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == CENTRIFUGE){
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityCentrifuge){
				return new GuiCentrifuge(player, (TileEntityCentrifuge)te);
			}
		}else if(ID == LIFEINDUCER){
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityLifeInducer){
				return new GuiLifeInducer(player.inventory, (TileEntityLifeInducer)te);
			}
		}else if(ID == CLONE){
			Entity e = world.getEntityByID(x);
			if(e instanceof EntityClone){
				return new GuiTransferPlayerItems((EntityClone)e, player, false);
			}
		}
		return null;
	}
	public static int CENTRIFUGE = 0;
	public static int LIFEINDUCER = 1;
	public static int CLONE = 2;
}



















//MAY have gone over the top here. Made more code than I saved, increased chance of error, not helpful at all, but was fun to code. Shame I never got to test if it works.
// BUT IVE GOT BIGGER FISH TO FRY MATE


//package net.jamezo97.clonecraft;
//
//import java.lang.reflect.InvocationTargetException;
//import java.util.HashMap;
//
//import net.minecraft.client.gui.Gui;
//import net.minecraft.entity.player.EntityPlayer;
//import net.minecraft.inventory.Container;
//import net.minecraft.world.World;
//import cpw.mods.fml.common.network.IGuiHandler;
//
//public class GuiHandler implements IGuiHandler{
//
//	static GuiHandler INSTANCE;
//	
//	public GuiHandler(){
//		INSTANCE = this;
//		
//		registerGui(CENTRIFUGE, net.jamezo97.clonecraft.gui.GuiCentrifuge.class, net.jamezo97.clonecraft.gui.container.ContainerCentrifuge.class, null, null);
//		
//	}
//	
//	//Return Containers
//	@Override
//	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		if(idToEntry.containsKey(ID)){
//			return idToEntry.get(ID).instantiateServer(player, world, x, y, z);
//		}
//		return null;
//	}
//
//	//Return GUI's
//	@Override
//	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
//		if(idToEntry.containsKey(ID)){
//			return idToEntry.get(ID).instantiateClient(player, world, x, y, z);
//		}
//		return null;
//	}
//
//	
//	
//	public void registerGui(int ID, Class<? extends Gui> guiClass, Class<? extends Container> containerClass, ParamGet getClient, ParamGet getServer){
//		idToEntry.put(ID, new GuiEntry(guiClass, containerClass, getClient, getServer));
//	}
//	
//	HashMap<Integer, GuiEntry> idToEntry = new HashMap<Integer, GuiEntry>();
//	
//	static int CENTRIFUGE = 0;
//	
//	public class GuiEntry{
//		
//		Class<? extends Gui> guiClass;
//		Class<? extends Container> containerClass;
//		
//		ParamGet getClient;
//		ParamGet getServer;
//		
//		public GuiEntry(Class<? extends Gui> guiClass, Class<? extends Container> containerClass, ParamGet getClient, ParamGet getServer){
//			this.guiClass = guiClass;
//			this.containerClass = containerClass;
//			this.getClient = getClient;
//			this.getServer = getServer;
//		}
//		
//		public Container instantiateServer(EntityPlayer player, World world, int x, int y, int z){
//			if(getServer != null){
//				try {
//					return containerClass.getConstructor(getServer.getTypes()).newInstance(getServer.getParameters(player, world, x, y, z));
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					e.printStackTrace();
//				} catch (SecurityException e) {
//					e.printStackTrace();
//				}
//			}else{
//				try {
//					return containerClass.newInstance();
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//			return null;
//		}
//		
//		public Gui instantiateClient(EntityPlayer player, World world, int x, int y, int z){
//			if(getClient != null){
//				try {
//					return guiClass.getConstructor(getClient.getTypes()).newInstance(getClient.getParameters(player, world, x, y, z));
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				} catch (IllegalArgumentException e) {
//					e.printStackTrace();
//				} catch (InvocationTargetException e) {
//					e.printStackTrace();
//				} catch (NoSuchMethodException e) {
//					e.printStackTrace();
//				} catch (SecurityException e) {
//					e.printStackTrace();
//				}
//			}else{
//				try {
//					return guiClass.newInstance();
//				} catch (InstantiationException e) {
//					e.printStackTrace();
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//			return null;
//		}
//		
//	}
//	
//	public interface ParamGet {
//		
//		public Class[] getTypes();
//		
//		public Object[] getParameters(EntityPlayer player, World world, int x, int y, int z);
//		
//	}
//}
