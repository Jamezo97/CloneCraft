package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;
import net.jamezo97.clonecraft.gui.container.ContainerCentrifuge;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class Handler0SpinCentrifuge extends Handler{
	
//	int x, y, z;
	
	public Handler0SpinCentrifuge(){}
	
//	public Handler0SpinCentrifuge(){
////		this.x = x;
////		this.y = y;
////		this.z = z;
//	}

	@Override
	public void read(ByteBuf buf) {
//		this.x = buf.readInt();
//		this.y = buf.readInt();
//		this.z = buf.readInt();
	}

	@Override
	public void write(ByteBuf buf) {
//		buf.writeInt(x);
//		buf.writeInt(y);
//		buf.writeInt(z);
	}

	@Override
	public void handle(Side side, EntityPlayer player) {
		if(side == Side.SERVER){
			if(player.openContainer != null && player.openContainer instanceof ContainerCentrifuge){
				((ContainerCentrifuge)player.openContainer).tE.setCanStart();
			}
		}
	}
	
}
