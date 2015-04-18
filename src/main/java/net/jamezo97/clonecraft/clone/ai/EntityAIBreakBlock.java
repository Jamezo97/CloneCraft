package net.jamezo97.clonecraft.clone.ai;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockStem;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * AI to make the clone mine blocks. Has mutex bits of 1 (00000001).
 * @author James
 *
 */
public class EntityAIBreakBlock extends EntityAIBase {
	
	EntityClone clone;
	
	public boolean isMining = false;
	boolean mineTypeInWay = false;
	int x, y, z, blockId, meta;
	int beenFindingFor = 0;
	
	int viewDistance = 32;
	
	public EntityAIBreakBlock(EntityClone clone, int viewDistance){
		this.clone = clone;
		this.viewDistance = viewDistance;
		this.setMutexBits(1);
	}

	@Override
	public boolean shouldExecute() {
		if(clone != null && clone.getOptions() != null && clone.getOptions().breakBlocks.get()){
			return true;
		}/*else if(currentlyMiningBlock){
            clone.worldObj.destroyBlockInWorldPartially(clone.getEntityId(), this.x, this.y, this.z, -1);
		}*/
		return false;
	}
/*
	@Override
	public boolean continueExecuting() {
		resetIfNeed();
		if(beenFindingFor > 40){
			if(!currentlyMiningBlock){
				x = 0;
				y = 0;
				z = 0;
				isMining = false;
			}
			beenFindingFor = 0;
		}
		if(!isMining){
			if(clone.inventory.currentItem > 0){
				clone.inventory.currentItem = 0;
			}
			findAndSelectBlock();
		}else if(clone.getNavigator().noPath() && !inRangeToBreak()){
			if(!selectBlock(x, y, z)){
				findAndSelectBlock();
			}
		}
		if(isMining){
			double distance1 = getDistance(x, y, z);
			double distance2 = getDistance(x+1, y+1, z+1);
			if(distance2 < distance1){
				distance1 = distance2;
			}
			if(distance1 < clone.getReach()){
				if(canSeeBlockDirectly(x, y, z)){
					clone.selectToolForBlock(blockId(), meta());
					if(!currentlyMiningBlock){
						currentlyMiningBlock = true;
					}
					if(currentlyMiningBlock){
						updateBlockHit();
					}
				}else{
					currentlyMiningBlock = false;
					blockDamage = 0;
					stepSoundTickCounter = 0;
				}
				

			}else{
				currentlyMiningBlock = false;
				blockDamage = 0;
				stepSoundTickCounter = 0;
			}
		}
		return super.continueExecuting();
	}
	
	public void updateBlockHit(){
        if (this.blockHitDelay > 0)
        {
            --this.blockHitDelay;
            return;
        }
		clone.swingItem();
		blockDamage += getPlayerRelativeBlockHardness(x, y, z);
		Block block = Block.blocksList[clone.worldObj.getBlockId(x, y, z)];
		if(block != null){
	        if (this.stepSoundTickCounter % 4.0F == 0.0F && block != null)
	        {
	        	playSound(block);
	        }
	        ++this.stepSoundTickCounter;
            if (this.blockDamage >= 1.0F)
            {
                this.currentlyMiningBlock = false;
                this.onPlayerDestroyBlock(x, y, z);
                this.blockDamage = 0.0F;
                this.stepSoundTickCounter = 0.0F;
                this.blockHitDelay = 5;
            }
            clone.worldObj.destroyBlockInWorldPartially(clone.entityId, this.x, this.y, this.z, (int)(this.blockDamage * 10.0F) - 1);
		}else{
			System.out.println("ERROR! Block is null on hit!?!");
		}
	}
	
	
	@SideOnly(value = Side.CLIENT)
	private void playSound(Block block){
		Minecraft.getMinecraft().sndManager.playSound(block.stepSound.getStepSound(), (float)x + 0.5F, (float)y + 0.5F, (float)z + 0.5F, (block.stepSound.getVolume() + 1.0F) / 8.0F, block.stepSound.getPitch() * 0.5F);
	}

	private void onPlayerDestroyBlock(int x, int y, int z) {
		Block block = Block.blocksList[clone.worldObj.getBlockId(x, y, z)blockId()];
		if(block != null){
			int oldId = blockId();//clone.worldObj.getBlockId(x, y, z);
			clone.worldObj.playAuxSFX(2001, x, y, z, block.blockID + (clone.worldObj.getBlockMetadata(x, y, z) << 12));
			//block.dropBlockAsItem(clone.worldObj, x, y, z, meta(), 1);
			block.harvestBlock(clone.worldObj, new FakeEntityPlayer(clone), x, y, z, meta());
			clone.worldObj.setBlock(x, y, z, 0, 0, 2);
            
			ItemStack var9 = clone.getCurrentEquippedItem();

            if (var9 != null)
            {
            	boolean var7 = Item.itemsList[var9.itemID].onBlockDestroyed(var9, clone.worldObj, block.blockID, x, y, z, clone);

                if (var9.stackSize == 0)
                {
                    clone.destroyCurrentEquippedItem();
                }
            }
            if(clone.getOptions().farming.value() && block instanceof BlockCrops){
            	int id = CloneCraftBlockAccessor.getSeedItem(((BlockCrops)block));
            	if(clone.inventory.hasItemInBar(id)){
            		if(clone.inventory.consumeInventoryItem(id)){
            			clone.worldObj.setBlock(x, y, z, block.blockID, 0, 2);
            			clone.swingItem();
            		}
            	}
            }else if(clone.getOptions().farming.value() && clone.inventory.hasItemInBar(ItemHoe.class) && y > 1){
            	int id = clone.worldObj.getBlockId(x, y-1, z);
            	if(id == Block.dirt.blockID && block.isOpaqueCube()){
            		int[][] variance = new int[][]{{1, -1, 0}, {-1, -1, 0},{0, -1, 1}, {0, -1, -1}};
            		int valids = 0;
            		for(int a = 0; a < variance.length; a++){
            			int[] va = variance[a];
            			int sideId = clone.worldObj.getBlockId(x + va[0], y + va[0], z + va[2]);
            			if(sideId == Block.tilledField.blockID){
            				valids++;
            			}
            		}
            		if(valids >= 1){
            			Block toBeTilled = Block.blocksList[id];
            			if(toBeTilled != null){
            				ItemStack toUse = null;
            				for(int a = 0; a < 9; a++){
            					ItemStack stack = clone.inventory.mainInventory[a];
            					if(stack != null && ItemHoe.class.isAssignableFrom(stack.getItem().getClass())){
            						clone.inventory.currentItem = a;
            						toUse = stack;
            						break;
            					}
            				}
            				if(toUse != null){
            					toUse.getItem().onItemUse(toUse, clone.getEntityPlayer(), clone.worldObj, x, y-1, z, 1, 0, 0, 0);
            				}
            				
            			}
            		}
            	}
            }
            if(!checkBlockAbove(oldId)){
            	x = 0;
            	y = 0;
            	z = 0;
            	isMining = false;
            }
		}
	}

	private boolean checkBlockAbove(int oldId) {
		if(!mineTypeInWay){
			y = y + 1;
			int id = clone.worldObj.getBlockId(x, y, z);
			int meta = clone.worldObj.getBlockMetadata(x, y, z);
			if(id == oldId && canBreak(id, meta, x, y, z) && getDistance(x, y, z) < clone.getReach() && canSeeBlockDirectly(x, y, z) && clone.getOptions().canBreakBlock(id, meta)){
				return true;
			}
		}
		return false;
	}

	float blockDamage = 0, stepSoundTickCounter = 0;
	
	int blockHitDelay = 0;
	
	boolean currentlyMiningBlock = false;
	

	
	public boolean findAndSelectBlock(){
		int pPosX, pPosY, pPosZ;
		pPosX = (int)clone.posX;
		pPosY = (int)Math.floor(clone.posY+(clone.getEyeHeight()/clone.scale));
		pPosZ = (int)clone.posZ;
		ArrayList<int[]> blocksValid = new ArrayList<int[]>();
		for(int y = pPosY - viewDistance; y < pPosY + viewDistance(clone.scale>.62?5:4); y++){
			for(int z = pPosZ - viewDistance; z < pPosZ + viewDistance; z++){
				for(int x = pPosX - viewDistance; x < pPosX + viewDistance; x++){
					int blockId = clone.worldObj.getBlockId(x, y, z);
					if(blockId > 0){
						int blockMeta = clone.worldObj.getBlockMetadata(x, y, z);
						if(canBreak(blockId, blockMeta, x, y, z)){
							blocksValid.add(new int[]{x, y, z, blockId, blockMeta});	
						}
					}
				}
			}
		}
		ArrayList<int[]> sorted = new ArrayList<int[]>();
		while(!blocksValid.isEmpty()){
			double distance = 0;
			int index = -1;
			for(int a = 0; a < blocksValid.size(); a++){
				int[] blockInfo = blocksValid.get(a);
				double distanceTo = clone.getDistanceSqFromEye(blockInfo[0]+.5, blockInfo[1]+.5, blockInfo[2]+.5);
				if(index == -1 || distanceTo < distance){
					index =  a;
					distance = distanceTo;
				}
			}
			if(index == -1){
				System.out.println("Erm, error? That's not right... :(");
				index = 0;
			}
			sorted.add(blocksValid.remove(index));
		}
		
		for(int a = 0; a < sorted.size(); a++){
			int[] blockPos = sorted.get(a);
			if(clone.getDistanceSq(blockPos[0], blockPos[1], blockPos[2]) < 1){
				if(selectBlock(blockPos[0], blockPos[1], blockPos[2])){
					return true;
				}
			}
			double distance = -1;
			
			double[][] variances = new double[][]{{.001, .001, .001}, {.999, .001, .001}, {.999, .001, .999}, {.001, .001, .999},
					{.001, .999, .001}, {.999, .999, .001}, {.999, .999, .999}, {.001, .999, .999}};
			//MovingObjectPosition blockToBreak = null;
			
			double distance2 = -1;
			
			for(int b = 0; b < variances.length; b++){
				double[] variant = variances[b];
				double x = blockPos[0] + variant[0], y = blockPos[1] + variant[1], z = blockPos[2] + variant[2];
				CanSeeEntry entry = this.canSeePosition(x, y, z);
				if(entry.getMop() == null){
					if(selectBlock(blockPos[0], blockPos[1], blockPos[2])){
						mineTypeInWay = false;
						break;
					}
				}else if(entry.canSeeAtAll){
					if((x == blockPos[0] && y == blockPos[1] && z == blockPos[2]) || !selectBlock(blockPos[0], blockPos[1], blockPos[2])){
						if(clone.getOptions().breakExtraBlocks.value()){
							if(selectBlock(entry.mop.blockX, entry.mop.blockY, entry.mop.blockZ)){
								mineTypeInWay = true;
								break;
							}
						}
					}else{
						break;
					}

				}
			}
			if(isMining){
				break;
			}

		}

		return isMining;
	}
	
	public CanSeeEntry canSeePosition(double x, double y, double z){
		CanSeeEntry entry = new CanSeeEntry();
		
		double fromX = clone.posX, fromY = clone.posY + clone.getEyeHeight()/clone.scale, fromZ = clone.posZ;
		
		MovingObjectPosition hit = clone.worldObj.rayTraceBlocks_do_do(Vec3.createVectorHelper(fromX, fromY, fromZ), Vec3.createVectorHelper(x, y, z), false, true);
		entry.setMop(hit);
		
		if(hit != null){
			//EntityAIBreakBlock.add(fromX, fromY, fromZ, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, hit.sideHit);
			if(hit.blockX == Math.floor(x) && hit.blockY == Math.floor(y) && hit.blockZ == Math.floor(z)){
				entry.setMop(null);
				hit = null;
			}
		}else{
			EntityAIBreakBlock.add(fromX, fromY, fromZ, x, y, z, 7);
		}
		

		
		int max = 32;
		
		float gradientYX = ((float)y - (float)fromY) / ((float)x - (float)fromX);//for every x across, go up y;
		float gradientYZ = ((float)y - (float)fromY) / ((float)z - (float)fromZ);//for every z across, go up y;
		
		float gradientXY = ((float)x - (float)fromX) / ((float)y - (float)fromY);//for every y up, go across x
		float gradientZY = ((float)z - (float)fromZ) / ((float)y - (float)fromY);//for every y up, go across z;
		
		float gradientXZ = ((float)x - (float)fromX) / ((float)z - (float)fromZ);//for every z go x;
		float gradientZX = ((float)z - (float)fromZ) / ((float)x - (float)fromX);//for every x go z;
		
		while(hit != null && max-- > 0){
			if(hit.blockX == (int)x && hit.blockY == (int)y && hit.blockZ == (int)z){
				entry.setSee(true);
				break;
			}
			if(!clone.isSeeThroughBlock(hit.blockX, hit.blockY, hit.blockZ)){
				break;
			}
			double hitX = hit.hitVec.xCoord, hitY = hit.hitVec.yCoord, hitZ = hit.hitVec.zCoord;
			int bHitX = hit.blockX, bHitY = hit.blockY, bHitZ = hit.blockZ;
			double newX = hitX, newY = hitY, newZ = hitZ;
			int sideHit = hit.sideHit;
			while(max > 0){
				if(sideHit == 0){
					newX = hitX + gradientXY;
					if(newX > bHitX + 1){
						newX = bHitX + 1;
						newY = hitY + (bHitX + 1 - hitX) * gradientYX;
					}else if(newX < bHitX){
						newX = bHitX;
						newY = hitY - (hitX - bHitX) * gradientYX;
					}
					newZ = hitZ + gradientZY;
					if(newZ > bHitZ + 1){
						newZ = bHitZ + 1;
						newY = hitY + (bHitZ + 1 - hitZ) * gradientYZ;
					}else if(newZ < bHitZ){
						newZ = bHitZ;
						newY = hitY - (hitZ - bHitZ) * gradientYZ;
					}
					if(newY % 1 == 0){
						newY = hitY + 1;
					}
				}else if(sideHit == 1){
					newX = hitX - gradientXY;
					if(newX > bHitX + 1){
						newX = bHitX + 1;
						newY = hitY + (bHitX + 1 - hitX) * gradientYX;
					}else if(newX < bHitX){
						newX = bHitX;
						newY = hitY - (hitX - bHitX) * gradientYX;
					}
					newZ = hitZ - gradientZY;
					if(newZ > bHitZ + 1){
						newZ = bHitZ + 1;
						newY = hitY + (bHitZ + 1 - hitZ) * gradientYZ;
					}else if(newZ < bHitZ){
						newZ = bHitZ;
						newY = hitY - (hitZ - bHitZ) * gradientYZ;
					}
					if(newY % 1 == 0){
						newY = hitY - 1;
					}
				}else if(sideHit == 2){
					newX = hitX + gradientXZ;
					if(newX > bHitX+1){
						newX = bHitX + 1;
						newZ = hitZ + (bHitX + 1 - hitX) * gradientZX;
					}else if(newX < bHitX){
						newX = bHitX;
						newZ = hitZ - (hitX - bHitX) * gradientZX;
					}
					newY = hitY + gradientYZ;
					if(newY > bHitY+1){
						newY = bHitY + 1;
						newZ = hitZ + (bHitY + 1 - hitY) * gradientZY;
					}else if(newY < bHitY){
						newY = bHitY;
						newZ = hitZ - (hitY - bHitY) * gradientZY;
					}
					if(newZ % 1.0 == 0){
						newZ = hitZ + 1;
					}
				}else if(sideHit == 3){
					newX = hitX - gradientXZ;
					if(newX > bHitX+1){
						newX = bHitX + 1;
						newZ = hitZ + (bHitX + 1 - hitX) * gradientZX;
					}else if(newX < bHitX){
						newX = bHitX;
						newZ = hitZ - (hitX - bHitX) * gradientZX;
					}
					newY = hitY - gradientYZ;
					if(newY > bHitY+1){
						newY = bHitY + 1;
						newZ = hitZ + (bHitY + 1 - hitY) * gradientZY;
					}else if(newY < bHitY){
						newY = bHitY;
						newZ = hitZ - (hitY - bHitY) * gradientZY;
					}
					if(newZ % 1.0 == 0){
						newZ = hitZ - 1;
					}
				}else if(sideHit == 4){
					newY = hitY + gradientYX;
					if(newY > bHitY + 1){
						newY = bHitY + 1;
						newX = bHitX + (bHitY + 1 - hitY) * gradientXY;
					}else if(newY < bHitY){
						newY = bHitY;
						newX = bHitX - (hitY - bHitY) * gradientXY;
					}
					newZ = hitZ + gradientZX;
					if(newZ > bHitZ + 1){
						newZ = bHitZ + 1;
						newX = bHitX + (bHitZ + 1 - hitZ) * gradientXZ;
					}else if(newZ < bHitZ){
						newZ = bHitZ;
						newX = bHitX - (hitZ - bHitZ) * gradientXZ;
					}
					if(newX % 1.0 == 0){
						newX = hitX+1;
					}
				}else if(sideHit == 5){
					newY = hitY - gradientYX;
					if(newY > bHitY+1){
						newY = bHitY + 1;
						newX = hitX + (bHitY+1 - hitY) * gradientXY;
					}else if(newY < bHitY){
						newY = bHitY;
						newX = hitX - (hitY - bHitY) * gradientXY;
					}
					newZ = hitZ - gradientZX;
					if(newZ > bHitZ+1){
						newZ = bHitZ + 1;
						newX = hitX + (bHitZ+1 - hitZ) * gradientXZ;
					}else if(newZ < bHitZ){
						newZ = bHitZ;
						newX = hitX - (hitZ - bHitZ) * gradientXZ;
					}
					if(newX % 1.0 == 0){
						newX = hitX-1;
					}
				}
				int newHitX = (int)Math.floor(newX), newHitY = (int)Math.floor(newY), newHitZ = (int)Math.floor(newZ);
				int oldSideHit = sideHit;
				if(newX < hitX && newX % 1 == 0){
					newHitX--;
					sideHit = 5;
				}else if(newY < hitY && newY % 1 == 0){
					newHitY--;
					sideHit = 1;
				}else if(newZ < bHitZ+1 && newZ % 1 == 0){
					newHitZ--;
					sideHit = 3;
				}else{
					break;
				}
				if(clone.worldObj.getBlockId(newHitX, newHitY, newHitZ) == 0){
					if(newX < hitX && newX % 1 == 0){
						newX = newX - .001;
					}else if(newY < hitY && newY % 1 == 0){
						newY = newY - .001;
					}else if(newZ < bHitZ+1 && newZ % 1 == 0){
						newZ = newZ - .001;
					}
					break;
				}
				//EntityAIBreakBlock.add(hitX, hitY, hitZ, newX, newY, newZ, oldSideHit);
				if((newHitX<=x && x<newHitX+1) && (newHitY<=y && y<newHitY+1) && (newHitZ<=z && z<newHitZ+1)){
					entry.setSee(true);
					return entry;
				}
				max--;
				hitX = newX;
				hitY = newY;
				hitZ = newZ;
				bHitX = (int)Math.floor(hitX);
				bHitY = (int)Math.floor(hitY);
				bHitZ = (int)Math.floor(hitZ);
			}
			//EntityAIBreakBlock.add(hitX, hitY, hitZ, newX, newY, newZ, sideHit);
			
			hit = clone.worldObj.rayTraceBlocks_do_do(Vec3.createVectorHelper(newX, newY, newZ), Vec3.createVectorHelper(x, y, z), false, true);
			
			if(hit == null){
				//EntityAIBreakBlock.add(newX, newY, newZ, x, y, z, 6);
				entry.setSee(true);
			}else{
				//EntityAIBreakBlock.add(newX, newY, newZ, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, hit.sideHit);
			}
			if(hit != null && hit.blockX == Math.floor(x) && hit.blockY == Math.floor(y) && hit.blockZ == Math.floor(z)){
				entry.setSee(true);
				break;
			}
		}
		return entry;
	}



	public boolean canSeeBlockDirectly(int x, int y, int z){
		double[][] variances = new double[][]{{.001, .001, .001}, {.999, .001, .001}, {.999, .001, .999}, {.001, .001, .999},
				{.001, .999, .001}, {.999, .999, .001}, {.999, .999, .999}, {.001, .999, .999}};
		for(int a = 0; a < variances.length; a++){
			MovingObjectPosition trace = clone.worldObj.rayTraceBlocks_do_do(Vec3.createVectorHelper(clone.posX, clone.posY + clone.getEyeHeight()/clone.scale, clone.posZ), Vec3.createVectorHelper(x + variances[a][0], y + variances[a][1], z + variances[a][2]), false, true);
			if(trace == null){
				return true;
			}else{
				if(trace.blockX == x && trace.blockY == y && trace.blockZ == z){
					return true;
				}
			}
		}

		return false;
	}
	
	public boolean inRangeToBreak(){
		double distance = clone.getDistanceSq(x, y, z);
		if(distance < 16){
			return true;
		}
		return false;
	}
	
	public void updatePath(){
		clone.moveTo(x+.5, y, z+.5);
	}
	
	public double getDistance(int x, int z){
		return Math.sqrt(((clone.posX - x) * (clone.posX - x)) + ((clone.posZ - z) * (clone.posZ - z)));
	}
	
	

	private boolean selectBlock(int blockX, int blockY, int blockZ) {
		x = blockX;
		y = blockY;
		z = blockZ;
		if(clone.getDistanceSq(x, y, z) > 1024){
			return false;
		}
		updatePath();
		if(!clone.getNavigator().noPath() || (canSeeBlockDirectly(x, y, z) && (getDistance(x, y, z) < clone.getReach() || getDistance(x+1, y+1, z+1) < clone.getReach())) || getDistance(x+.5, y+.5, z+.5) < 1){
			return isMining = true;
		}
		return isMining = false;
	}
	
	public double getDistance(double x, double y, double z){
        double var7 = clone.posX - x;
        double var9 = (clone.posY + clone.getEyeHeight()/clone.scale) - y;
        double var11 = clone.posZ - z;
        return (double)MathHelper.sqrt_double(var7 * var7 + var9 * var9 + var11 * var11);
	}
	
	public void resetIfNeed(){
		if(isMining){
			if(blockId() == 0 || (!mineTypeInWay?!canBreak(blockId(), meta(), x, y, z):false)){
				if(this.currentlyMiningBlock){
					currentlyMiningBlock = false;
					stepSoundTickCounter = 0;
					blockDamage = 0;
//					System.out.println("Reset: " + blockId() + "|" + mineTypeInWay + "|" + canBreak(blockId(), meta(), x, y, z));
					clone.worldObj.destroyBlockInWorldPartially(clone.entityId, x, y, z, -1);
				}
				x = 0;
				y = 0;
				z = 0;
				isMining = false;
				beenFindingFor = 0;
				clone.getNavigator().clearPathEntity();

			}else if(currentlyMiningBlock && blockDamage > 0 && !canSeeBlockDirectly(x, y, z)){
				blockDamage = 0;
				isMining = false;
				clone.worldObj.destroyBlockInWorldPartially(clone.entityId, x, y, z, -1);
				x = 0;
				y = 0;
				z = 0;
				beenFindingFor = 0;
			}else{
				beenFindingFor++;
			}
		}
	}
	
	public boolean canBreak(int blockId, int blockMeta, int x1, int y1, int z1){
		if((blockId == Block.pumpkin.blockID || blockId == Block.melon.blockID) && clone.getOptions().farming.value()){
			boolean can = false;
			int[][] variances = new int[][]{{1,0,0},{-1,0,0},{0,0,1},{0,0,-1}};
			for(int a = 0; a < variances.length; a++){
				int [] var = variances[a];
				int id = clone.worldObj.getBlockId(x1+var[0], y1+var[1], z1+var[2]);
				Block block = Block.blocksList[id];
				if(block instanceof BlockStem){
					can = true;
					break;
				}
			}
			if(!can){
				return false;
			}
		}
		if(clone.getOptions().canBreakBlock(blockId, blockMeta)){
			return true;
		}else if(blockId == Block.wood.blockID){
			if(blockMeta > 7){
				if(clone.getOptions().canBreakBlock(blockId, blockMeta-8)){
					return true;
				}
			}else if(blockMeta > 3){
				if(clone.getOptions().canBreakBlock(blockId, blockMeta-4)){
					return true;
				}
			}
		}else if(blockId == Block.leaves.blockID){
			if(clone.getOptions().canBreakBlock(blockId, blockMeta & 3)){
				return true;
			}
		}else if(blockId == Block.doorWood.blockID || blockId == Block.doorIron.blockID){
			if(clone.getOptions().canBreakBlock(blockId, 0)){
				return true;
			}
		}else if(blockId == Block.pumpkin.blockID || blockId == Block.melon.blockID){
			if(clone.getOptions().canBreakBlock(blockId, 0)){
				return true;
			}
		}
		return false;
	}
	
	public int blockId(){
		return clone.worldObj.getBlockId(x, y, z);
	}
	
	public int meta(){
		return clone.worldObj.getBlockMetadata(x, y, z);
	}
	
    public float getPlayerRelativeBlockHardness(int par3, int par4, int par5)
    {
    	int blockId = clone.worldObj.getBlockId(par3, par4, par5);
    	if(blockId > 0 && Block.blocksList.length > blockId){
    		Block block = Block.blocksList[blockId];
            float var6 = block.getBlockHardness(clone.worldObj, par3, par4, par5);
            return var6 < 0.0F ? 0.0F : (!clone.canHarvestBlock(block) ? 1.0F / var6 / 100.0F : clone.getCurrentPlayerStrVsBlock(block) / var6 / 30.0F);
    	}
    	System.out.println("ERROR: Could not get block hardness");
    	return 0;
    }*/
/*	static long sinceLast = 0;
	static DrawLine drawing = null;
	public static void render(float par3) {
		draw1();
	}
	
	public static void draw2(){
		if(draws.size() > 0){
			if(sinceLast++ > 40){
				drawing = draws.remove(0);
				drawing.print();
				sinceLast = 0;
			}
			if(drawing != null){
				GL11.glPushMatrix();
				GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
				if(drawing != null){
					drawing.render();
				}
				GL11.glPopMatrix();
			}


			canAdd = false;
		}else{
			canAdd = true;
		}
	}
	
	public static void draw1(){
		ArrayList<DrawLine> from = new ArrayList<DrawLine>();
		
		if(draws.size() == 0 && oldDraws.size() > 0){
			from = oldDraws;
		}else{
			from = draws;
			oldDraws.clear();
		}
		
		GL11.glPushMatrix();
		GL11.glTranslated(-RenderManager.renderPosX, -RenderManager.renderPosY, -RenderManager.renderPosZ);
		for(int a = 0; a < from.size(); a++){
			DrawLine line = (DrawLine)from.get(a);
			if(line != null){
				line.render();
			}
		}
		
		
		GL11.glPopMatrix();
		while(!draws.isEmpty()){
			oldDraws.add(draws.remove(0));
		}
	}
	static boolean canAdd = true;
	public static void add(Vec3 vec1, Vec3 vec2, int side){
		if(canAdd){
			draws.add(new DrawLine(vec1, vec2, side));
		}
	}
	
	public static void add(double d1, double d2, double d3, double d4, double d5, double d6, int side){
		EntityAIBreakBlock.add(Vec3.createVectorHelper(d1, d2, d3), Vec3.createVectorHelper(d4, d5, d6), side);
	}
	
	static ArrayList<DrawLine> draws = new ArrayList<DrawLine>();
	static ArrayList<DrawLine> oldDraws = new ArrayList<DrawLine>();*/

	
	 

}