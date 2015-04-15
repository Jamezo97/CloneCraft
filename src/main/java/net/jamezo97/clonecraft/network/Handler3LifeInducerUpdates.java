package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.entity.EntitySparkFX;
import net.jamezo97.clonecraft.gui.container.ContainerLifeInducer;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.CloneCraftWorld;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Handler3LifeInducerUpdates extends Handler{


	int x;
	int y;
	int z;
	int type;
	int meta;
	
	public Handler3LifeInducerUpdates(){
		
	}
	
	public Handler3LifeInducerUpdates(int x, int y, int z, int type, int meta){
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.meta = meta;
	}
	
	@SideOnly(value = Side.CLIENT)
	public void client(EntityPlayer player){
		World world = player.worldObj;
		if(type == 0){
			for(double yV = y; yV < y+1; yV+= .02){
				EntityFX e = new EntitySparkFX(world, x+.5, yV, z+.5, 1);
				CloneCraftWorld.spawnParticle(e);
				world.playSound(x+.5, yV, z+.5, "clonecraft:block.zoom", .5f, 1.0f, false);
			}
		}else if(type == 1){
			for(double yV = y; yV < y+1; yV+= .0025){
				EntityFX e = new EntitySparkFX(world, x+.5, yV, z+.5, 4);
				CloneCraftWorld.spawnParticle(e);
				world.playSound(x+.5, yV, z+.5, "clonecraft:block.zoom", .5f, .2f, false);
				world.playSound(x+.5, yV, z+.5, "random.explode", .2f, 1.5f, false);
			}
		}
	}

	@Override
	public void handle(Side side, EntityPlayer player) {
		if(side == Side.CLIENT){
			client(player);
		}else if(side == Side.SERVER){
			World world = player.worldObj;
			TileEntity te = world.getTileEntity(x, y, z);
			if(te != null && te instanceof TileEntityLifeInducer && player.openContainer != null && player.openContainer instanceof ContainerLifeInducer){
				if(((ContainerLifeInducer)player.openContainer).inducer == te){
					((TileEntityLifeInducer)te).discharge();
				}
			}
		}
		
		
	}

	@Override
	public void read(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.type = buf.readInt();
		this.meta = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeInt(type);
		buf.writeInt(meta);
	}
	
}
