package net.jamezo97.clonecraft.command.parameter;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ParamPosition extends Parameter{
	
	WordSet ws_point = new WordSet("over there");
	WordSet ws_you = new WordSet("there", "that position", "your position");
	WordSet ws_me = new WordSet("my position", "this sport", "this position");
//	WordSet ws_this = new WordSet("this position", "this spot", "my position");
	
	@Override
	public PGuess findParameters(EntityClone clone, EntityPlayer sender, String[] words) {
		PGuess pguess = new PGuess(this);
		System.out.println(ws_me.containsWord(words));
		if(ws_point.containsWord(words) != -1)
		{
			
			
			Vec3 vec3 = sender.getPosition(1);
			vec3.yCoord += sender.getEyeHeight();
	        Vec3 vec31 = sender.getLook(1);
	        Vec3 vec32 = vec3.addVector(vec31.xCoord * 150, vec31.yCoord * 150, vec31.zCoord * 150);
	        MovingObjectPosition mop =  sender.worldObj.func_147447_a(vec3, vec32, false, false, true);
			
//			MovingObjectPosition mop = sender.rayTrace(150, 1.0f);
//			sender.canEntityBeSeen(p_70685_1_)
			if(mop != null)
			{
				if(mop.entityHit != null)
				{
					
					pguess.add(new ParamGuess(new ChunkCoordinates((int)Math.floor(mop.entityHit.posX), (int)Math.floor(mop.entityHit.posY), (int)Math.floor(mop.entityHit.posZ)), 0.5f));
				}
				else
				{
					
					int x = mop.blockX;
					int y = mop.blockY;
					int z = mop.blockZ;
					
					//
					//East +x	2
					//West -x	3
					//South +z	5
					//North -z	4
//					Bottom = 0, Top = 1, East = 2, West = 3, North = 4, South = 5.
					switch(mop.sideHit){
					case 0:  y-=2; break;
					case 1:  y++; break;
					case 2:  x++; break;
					case 3:  x--; break;
					case 4:  z--; break;
					case 5:  z++; break;
					}
					

					if(World.doesBlockHaveSolidTopSurface(clone.worldObj, x, y-1, z)){
						Block b1 = clone.worldObj.getBlock(x, y, z);
						Block b2 = clone.worldObj.getBlock(x, y+1, z);
						if(b1 == Blocks.air && b2 == Blocks.air)
						{
							System.out.println("ray Traced " + x + ", " + y + ", " + z);
							pguess.add(new ParamGuess(new ChunkCoordinates(x, y, z), 0.9f));
						}
						else
						{
							pguess.add(new ParamGuess(null, 0f));
						}
					}
					else
					{
						pguess.add(new ParamGuess(null, 0f));
					}
					
				}
			}
			
		}
		
		if(ws_you.containsWord(words) != -1)
		{
			pguess.add(new ParamGuess(new ChunkCoordinates((int)Math.floor(clone.posX), (int)Math.floor(clone.posY), (int)Math.floor(clone.posZ)), 0.5f));
		}
		
		if(ws_me.containsWord(words) != -1)
		{
			pguess.add(new ParamGuess(new ChunkCoordinates((int)Math.floor(sender.posX), (int)Math.floor(sender.posY), (int)Math.floor(sender.posZ)), 0.5f));
		}
		
		return pguess;
	}

	@Override
	public String getDefaultAskString() {
		return "Where?";
	}
	
	

}
