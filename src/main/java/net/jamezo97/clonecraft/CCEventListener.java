package net.jamezo97.clonecraft;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.jamezo97.clonecraft.recipe.CloneCraftCraftingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.world.WorldEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class CCEventListener {
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load param)
	{
		if(!param.world.isRemote)
		{
			if(param.world.provider.dimensionId == 0)
			{
				File loadFrom = new File(param.world.getSaveHandler().getWorldDirectory(), "CloneCraft.dat");
				
				if(loadFrom.exists())
				{
					Closeable stream = null;
					NBTTagCompound nbt = null;
					
					try
					{
						nbt = CompressedStreamTools.readCompressed((FileInputStream)(stream = new FileInputStream(loadFrom)));
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					finally
					{
						if(stream != null)
						{
							try {
								stream.close();
							} catch (Exception e2) {}
						}
					}
					
					if(nbt != null)
					{
						CloneCraft.INSTANCE.loadWorldData(nbt);
					}
				}
			}
		}
	}
	
	

	@SubscribeEvent
	public void onWorldSave(WorldEvent.Save param)
	{
		if(!param.world.isRemote)
		{
			if(param.world.provider.dimensionId == 0)
			{
				File saveTo = new File(param.world.getSaveHandler().getWorldDirectory(), "CloneCraft.dat");
				Closeable stream = null;
				
				NBTTagCompound nbt = CloneCraft.INSTANCE.saveWorldData(new NBTTagCompound());
				
				if(nbt != null)
				{
					try
					{
						CompressedStreamTools.writeCompressed(nbt, (FileOutputStream)(stream = new FileOutputStream(saveTo)));
					}
					catch (FileNotFoundException e)
					{
						e.printStackTrace();
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
					finally
					{
						if(stream != null)
						{
							try {
								stream.close();
							} catch (Exception e2) {}
						}
					}

				}
			}
		}
	}
	
	
	
	
	
	
	@SubscribeEvent
	public void ItemCraftedEvent(ItemCraftedEvent param){
		CloneCraftCraftingHandler.onCrafting(param.player, param.crafting, param.craftMatrix);
	}
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void renderLast(RenderWorldLastEvent event)
	{
		EntityLivingBase renderView = Minecraft.getMinecraft().renderViewEntity;
		
		if(renderView != null)
		{
			float p = event.partialTicks;
			GL11.glPushMatrix();
			GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
			GL11.glTranslated(-CloneCraftHelper.interpolate(renderView.prevPosX, renderView.posX, p), -CloneCraftHelper.interpolate(renderView.prevPosY, renderView.posY, p), -CloneCraftHelper.interpolate(renderView.prevPosZ, renderView.posZ, p));
			CCPostRender.postRender(p);
			
			renderSelectedClone(p);
			GL11.glPopMatrix();
		}
	}
	
	long countDown = -1;
	
	int countDownBegin = 1000;
	
	@SideOnly(value = Side.CLIENT)
	public void renderSelectedClone(float p){
		if(countDown == -1 || selectedClone == null){
			return;
		}
		if(!selectedClone.isEntityAlive()){
			selectedClone = null;
			countDown = -1;
			return;
		}
		long diff = countDown-System.currentTimeMillis();
		float intensity = 1.0f;
		if(diff > 100){
			intensity = (float)diff / (float)countDownBegin;
		}
		else
		{
			intensity = 200f / (float)countDownBegin;
		}
		RenderEntityOverlay rs = new RenderEntityOverlay(selectedClone, 0xff000000 | 0xff0000);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glDisable(GL11.GL_LIGHTING);
		
		rs.setOpacity(intensity);
		
//		rs.rotate(0);
		
		rs.render(p);
		
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

/*	@SubscribeEvent
	public void onBreakBlock(BlockEvent.BreakEvent event)
	{
		if(event.getPlayer().getCurrentEquippedItem() != null && event.getPlayer().getCurrentEquippedItem().getItem() == CloneCraft.INSTANCE.itemWoodStaff)
		{
			event.setCanceled(true);
		}
	}*/
	
	
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event)
	{
		if(ClientProxy.rotate.isPressed())
		{
			rotate++;
		}
		/*if(ClientProxy.kb_select.isPressed())
		{
			if(Minecraft.getMinecraft().thePlayer != null)
			{
				MovingObjectPosition mop = rayTraceWithEntities(100, Minecraft.getMinecraft().thePlayer, 1);//Minecraft.getMinecraft().objectMouseOver;//Minecraft.getMinecraft().thePlayer.rayTrace(100, 1);
				if(mop != null && mop.entityHit instanceof EntityClone)
				{
					selectedClone = (EntityClone)mop.entityHit;
					countDown = System.currentTimeMillis() + countDownBegin;
				}
				else
				{
					countDown = -1;
					selectedClone = null;
				}
			}
		}*/
		
		
		
	}
	
	public void onServerTick(TickEvent.ServerTickEvent event)
	{
		//If we're running a dedicated server, not an integrated one (where the client and server both share the same schematicList object)
		if(!(MinecraftServer.getServer() instanceof IntegratedServer))
		{
			CloneCraft.INSTANCE.schematicList.onUpdate();
		}
		
	}
	
	boolean leftPressed = false;
	boolean rightPressed = false;
	boolean upPressed = false;
	boolean downPressed = false;
	boolean forwardPressed = false;
	boolean backwardPressed = false;
	
	public static int moveX = 0;
	public static int moveY = 0;
	public static int moveZ = 0;
	
	public static int rotate = 0;
	
	//Displacements
	private static float disX = 0;
	private static float disY = 0;
	private static float disZ = 0;
	
	//Velocities
	private static float velX = 0;
	private static float velY = 0;
	private static float velZ = 0;
	
	@SideOnly(value = Side.CLIENT)
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		CCPostRender.removeExpiredRenderables();
		
		CloneCraft.INSTANCE.schematicList.onUpdate();
		
		if(Minecraft.getMinecraft().theWorld != null)
		{
			onClientWorldTick(null);
		}
		
		rotate = 0;
	}
	
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event)
	{
		//onClientWorldTick(event);
	}
	
	float[][] multipliers = new float[][]{
			{-1, +1},
			{-1, -1},
			{+1, -1},
			{+1, +1}
	};
	
	//viewingQuadrant
	int vq = 0;
	
	@SideOnly(value = Side.CLIENT)
	public void onClientWorldTick(TickEvent.WorldTickEvent event)
	{
		CCPostRender.onTick();
		
		{
			if(!leftPressed && !rightPressed && !upPressed && !downPressed && !forwardPressed && !backwardPressed
					&& Minecraft.getMinecraft().thePlayer != null)
			{
				float rotate = (Minecraft.getMinecraft().thePlayer.rotationYawHead + 45) % 360.0f;
				
				if (rotate < 0) { rotate += 360; }
				
				vq = (int)(rotate / 90);
				/*0
				   UP:	Z+
				RIGHT: 	X-
				1
				   UP:	X-
				RIGHT: 	Z-
				2
				   UP:	Z-
				RIGHT: 	X+
				3
				   UP:	X+
				RIGHT: 	Z+*/

				
			}
			
			moveX = (int)(Math.signum(disX) * Math.floor(Math.abs(disX)));
			moveY = (int)(Math.signum(disY) * Math.floor(Math.abs(disY)));
			moveZ = (int)(Math.signum(disZ) * Math.floor(Math.abs(disZ)));
			
			disX -= moveX;
			disY -= moveY;
			disZ -= moveZ;
			
			if(vq == 0 || vq == 2)
			{
				disX += velX;
				disZ += velZ;
			}
			else
			{
				disX += velZ;
				disZ += velX;
			}
			disY += velY;
			
			
			
			float increase = 0.02f;
			
			
			
			if(!leftPressed && !rightPressed && velX != 0)
			{
				velX = 0;
			}
			
			if(!upPressed && !downPressed && velY != 0)
			{
				velY = 0;
			}
			
			if(!forwardPressed && !backwardPressed && velZ != 0)
			{
				velZ = 0;
			}
			
//			System.out.println(vq);
			
			checkMoveBindings();
		}
	}
	
	@SideOnly(value = Side.CLIENT)
	public void checkMoveBindings()
	{
		
		float increase = 0.01f;
		
		if(ClientProxy.moveLeft.getIsKeyPressed())
		{
			if(!leftPressed)
			{
				if(vq == 0 || vq == 2)
				{
					moveX -= multipliers[vq][0];
				}
				else
				{
					moveZ -= multipliers[vq][0];
				}
			}
			velX -= increase * multipliers[vq][0];
			leftPressed = true;
		}
		else
		{
			leftPressed = false;
		}
		
		if(ClientProxy.moveRight.getIsKeyPressed())
		{
			if(!rightPressed)
			{
				if(vq == 0 || vq == 2)
				{
					moveX += multipliers[vq][0];
				}
				else
				{
					moveZ += multipliers[vq][0];
				}
			}
			velX += increase * multipliers[vq][0];
			rightPressed = true;
		}
		else
		{
			rightPressed = false;
		}
		
		if(ClientProxy.moveUp.getIsKeyPressed())
		{
			if(!upPressed){moveY++;}
			velY += increase;
			upPressed = true;
		}
		else
		{
			upPressed = false;
		}
		
		if(ClientProxy.moveDown.getIsKeyPressed())
		{
			if(!downPressed){moveY--;}
			velY -= increase;
			downPressed = true;
		}
		else
		{
			downPressed = false;
		}
		
		
		if(ClientProxy.moveForward.getIsKeyPressed())
		{
			if(!forwardPressed)
			{
				if(vq == 0 || vq == 2)
				{
					moveZ += multipliers[vq][1];
				}
				else
				{
					moveX += multipliers[vq][1];
				}
			}
			velZ += increase * multipliers[vq][1];
			forwardPressed = true;
		}
		else
		{
			forwardPressed = false;
		}
		
		if(ClientProxy.moveBackward.getIsKeyPressed())
		{
			if(!backwardPressed)
			{
				if(vq == 0 || vq == 2)
				{
					moveZ -= multipliers[vq][1];
				}
				else
				{
					moveX -= multipliers[vq][1];
				}
			}
			velZ -= increase * multipliers[vq][1];
			backwardPressed = true;
		}
		else
		{
			backwardPressed = false;
		}
	}
	
	EntityClone selectedClone = null;
	
	private static class CurrentEntry{
		
		EntityClone[] clones = null;
		
		long timeoutAt = 0;
		
		public CurrentEntry(EntityClone[] clones, long timeoutAt){
			this.clones = clones;
			this.timeoutAt = timeoutAt;
		}
		
		public CurrentEntry(EntityClone[] clones){
			this(clones, System.currentTimeMillis() + 60000);
		}
		
		public boolean hasTimedOut(){
			return System.currentTimeMillis() >= timeoutAt;
		}
		
		
		
	}
	
	HashMap<String, CurrentEntry> playerToCurrent = new HashMap<String, CurrentEntry>();
	
	

	@SubscribeEvent
	public void onChat(ServerChatEvent event){
		if(event.message.startsWith("\\"))
		{
			//CloneCraft commands begins with a backward slash
			String command = event.message.substring(1);
			
			
			if(playerToCurrent.containsKey(event.player.getCommandSenderName()))
			{
				CurrentEntry entry = playerToCurrent.get(event.player.getCommandSenderName());
				boolean remove = false;
				if(entry == null || entry.hasTimedOut())
				{
					EntityClone[] clones = entry.clones;
					
					for(int a = 0; a < clones.length; a++)
					{
						if(clones[a].getInterpretter().parseInput(command, event.player, true))
						{
							remove = true;
						}
					}
				}
				if(remove)
				{
					playerToCurrent.remove(event.player.getCommandSenderName());
				}
				event.setCanceled(true);
				return;
			}
			
			List clonesRaw = event.player.worldObj.getEntitiesWithinAABB(EntityClone.class, event.player.boundingBox.expand(64, 48, 64));
			
			if(clonesRaw.size() == 0)
			{
				return;
			}
			
			EntityClone clone;
			
			boolean selectEveryone = false;
			
			ArrayList<CloneEntry> cloneEntries = null;
			
			{
				int index;
				String[] words = command.toLowerCase().replace("!", " ").replace("?", " ").replace(",", " ").split(" ");
				for(int a = 0; a < WordSet.size(); a++)
				{
					
					if((index = WordSet.get(a).containsWord(words)) != -1)
					{
						int point = index & 0xffff;
						int size = (index >> 16) & 0xffff;
						//Set the verb, and the potential subject to nothing.
						//i.e. Only has to erase the quantity of 'everyone' from the word.
						
						for(int b = point; b < size && (b) < words.length; b++){
							words[b] = "";
						}
					}
				}
				
				WordSet pluralWordset = new WordSet("everyone", "every one", "everybody", "every body", "all of you");
				
				if((index = pluralWordset.containsWord(words)) != -1)
				{
					selectEveryone = true;
				}
			}
			
			
			for(int a = 0; a < clonesRaw.size(); a++)
			{
				clone = ((EntityClone)clonesRaw.get(a));
				
				if(clone.isEntityAlive() && clone.getOptions().command.get() && clone.canUseThisEntity(event.player))
				{
					if(cloneEntries == null)
					{
						cloneEntries = new ArrayList<CloneEntry>();
					}
					
					if(selectEveryone)
					{
						cloneEntries.add(new CloneEntry(clone,0));
					}
					else if(command.toLowerCase().contains(clone.getCommandSenderName().toLowerCase()))
					{
						cloneEntries.add(new CloneEntry(clone, (8192+(float)clone.getDistanceSqToEntity(event.player))));
					}
					else
					{
						//root(64*64+64*64)^2 = 8192 = max distance between clone and player..
						cloneEntries.add(new CloneEntry(clone, 8192-(float)clone.getDistanceSqToEntity(event.player)));
					}
				}
			}

			if(cloneEntries == null || cloneEntries.size() == 0)
			{
				event.setCanceled(true);
				return;
			}
			
			if(selectEveryone)
			{
				float largest = 0f;
				
				EntityClone[] bestClones = new EntityClone[cloneEntries.size()];
				boolean success = false;
				for(int a = 0; a < cloneEntries.size(); a++)
				{
					bestClones[a] = cloneEntries.get(a).clone;
					if(bestClones[a].getInterpretter().parseInput(command, event.player, true)){
						success = true;
					}
				}
				event.setCanceled(true);
				
				if(!success)
				{
					playerToCurrent.put(event.player.getCommandSenderName(), new CurrentEntry(bestClones));
				}
			
			}
			else
			{
				float largest = 0f;
				
				EntityClone bestClone = null;
				
				for(int a = 0; a < cloneEntries.size(); a++)
				{
					if(bestClone == null)
					{
						largest = cloneEntries.get(a).confidence;
						bestClone = cloneEntries.get(a).clone;
					}
					else if(cloneEntries.get(a).confidence > largest)
					{
						largest = cloneEntries.get(a).confidence;
						bestClone = cloneEntries.get(a).clone;
					}
				}
				
				if(bestClone != null)
				{
					bestClone.getInterpretter().parseInput(command, event.player, true);
					event.setCanceled(true);
				}
			}
		}
	}
	
	
	
	public static class CloneEntry{
		
		EntityClone clone;
		float confidence;
		
		public CloneEntry(EntityClone clone, float confidence){
			this.clone = clone;
			this.confidence = confidence;
		}
		
		
		public static Comparator<CloneEntry> cloneEntryCompare =  new Comparator<CloneEntry>(){

			@Override
			public int compare(CloneEntry c1, CloneEntry c2) {
				if(c1.confidence > c2.confidence){
					return 1;
				}else if(c1.confidence < c2.confidence){
					return -1;
				}
				return 0;
			}
			
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	public MovingObjectPosition rayTraceWithEntities(double d0, EntityLivingBase renderViewEntity, float partial){
		//Minecraft mc = Minecraft.getMinecraft();
		
		Entity pointedEntity = null;
		
		if (renderViewEntity != null)
        {
            if (renderViewEntity.worldObj != null)
            {
//                double d0 = (double)mc.playerController.getBlockReachDistance();
            	MovingObjectPosition objectMouseOver = renderViewEntity.rayTrace(d0, partial);
                double d1 = d0;
                Vec3 vec3 = renderViewEntity.getPosition(partial);

//                if (mc.playerController.extendedReach())
//                {
//                    d0 = 6.0D;
//                    d1 = 6.0D;
//                }
//                else
//                {
//                    if (d0 > 3.0D)
//                    {
//                        d1 = 3.0D;
//                    }
//
//                    d0 = d1;
//                }

//                if (mc.objectMouseOver != null)
//                {
//                    d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
//                }

                Vec3 vec31 = renderViewEntity.getLook(partial);
                Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
                pointedEntity = null;
                Vec3 vec33 = null;
                float f1 = 1.0F;
                List list = renderViewEntity.worldObj.getEntitiesWithinAABBExcludingEntity(renderViewEntity, renderViewEntity.boundingBox.addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
                double d2 = d1;

                for (int i = 0; i < list.size(); ++i)
                {
                    Entity entity = (Entity)list.get(i);

                    if (entity.canBeCollidedWith())
                    {
                        float f2 = entity.getCollisionBorderSize();
                        AxisAlignedBB axisalignedbb = entity.boundingBox.expand((double)f2, (double)f2, (double)f2);
                        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                        if (axisalignedbb.isVecInside(vec3))
                        {
                            if (0.0D < d2 || d2 == 0.0D)
                            {
                                pointedEntity = entity;
                                vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                                d2 = 0.0D;
                            }
                        }
                        else if (movingobjectposition != null)
                        {
                            double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                            if (d3 < d2 || d2 == 0.0D)
                            {
                                if (entity == renderViewEntity.ridingEntity && !entity.canRiderInteract())
                                {
                                    if (d2 == 0.0D)
                                    {
                                        pointedEntity = entity;
                                        vec33 = movingobjectposition.hitVec;
                                    }
                                }
                                else
                                {
                                    pointedEntity = entity;
                                    vec33 = movingobjectposition.hitVec;
                                    d2 = d3;
                                }
                            }
                        }
                    }
                }

                if (pointedEntity != null/* && (d2 < d1 || mc.objectMouseOver == null)*/)
                {
                    objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);

//                    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame)
//                    {
//                        pointedEntity = pointedEntity;
//                    }
                }
                return objectMouseOver;
            }
        }
		return null;
	}
	
	
	

}
