package net.jamezo97.clonecraft.clone.ai;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAICloneReturnToGuard extends EntityAIBase {
	EntityClone clone;
	public EntityAICloneReturnToGuard(EntityClone entityMyPerson) {
		clone = entityMyPerson;
	}
	@Override
	public boolean shouldExecute() {
		return false;
	}

	/*@Override
	public boolean shouldExecute() {
		return clone != null && clone.options.guard.value() && clone.getNavigator().noPath() && clone.getEntityToAttack() == null && clone.notMining();
	}

	@Override
	public boolean continueExecuting() {
		if(clone.getDistanceSq(clone.guardPosX+.5, clone.guardPosY, clone.guardPosZ+.5) > .5){
			returnToGuardPos();
		}
		return super.continueExecuting();
	}
	

	int tries = 0;
	public void returnToGuardPos(){
		int id1 = clone.worldObj.getBlockId(clone.guardPosX, clone.guardPosY, clone.guardPosZ);
		int id2 = clone.worldObj.getBlockId(clone.guardPosX, clone.guardPosY + 1, clone.guardPosZ);
		Block block1 = Block.blocksList[id1];
		Block block2 = Block.blocksList[id2];
		boolean areBlocksSeeThrough = false;
		if(!(id1 == 0 && id2 == 0)){
			if(block1 == null && block2 == null){
				areBlocksSeeThrough = true;
			}else{
				boolean isB1NoCollide = block1 == null;
				boolean isB2NoCollide = block2 == null;
				if(block1 != null){
					isB1NoCollide = block1.getCollisionBoundingBoxFromPool(clone.worldObj, clone.guardPosX, clone.guardPosY, clone.guardPosZ) == null;
				}
				if(block2 != null){
					isB1NoCollide = block2.getCollisionBoundingBoxFromPool(clone.worldObj, clone.guardPosX, clone.guardPosY+1, clone.guardPosZ) == null;
				}
				areBlocksSeeThrough = isB1NoCollide && isB2NoCollide;
			}
		}
		boolean hasBase = false;
		if(clone.guardPosY-1 > 0){
			int id = clone.worldObj.getBlockId(clone.guardPosX, clone.guardPosY-1, clone.guardPosZ);
			if(id != 0 && id < Block.blocksList.length){
				Block b = Block.blocksList[id];
				if(b != null){
					hasBase = b.getCollisionBoundingBoxFromPool(clone.worldObj, clone.guardPosX, clone.guardPosY-1, clone.guardPosZ) != null;
				}
			}
		}
		if(((id1 == 0 && id2 == 0) || areBlocksSeeThrough) && hasBase){
			if(clone.getDistanceSq(clone.guardPosX+.5, clone.guardPosY+.5, clone.guardPosZ+.5) < 1024){
				clone.getNavigator().setPath(clone.getNavigator().getPathToXYZ(clone.guardPosX+.5, clone.guardPosY+.5, clone.guardPosZ+.5), clone.getAIMoveSpeed());
				if(clone.getNavigator().noPath() && tries++ > 100){
					tries = 0;
					clone.setPosition(clone.guardPosX+.5, clone.guardPosY, clone.guardPosZ+.5);
				}
				if(!clone.getNavigator().noPath()){
					tries = 0;
					clone.saidError = false;
				}
			}else{
				tries = 0;
				clone.setPosition(clone.guardPosX+.5, clone.guardPosY, clone.guardPosZ+.5);
			}
		}else if(!clone.saidError){
			clone.saidError = true;
			EntityPlayer owner = clone.getOwnerPlayer();
			if(owner != null){
				owner.sendChatToPlayer(ChatMessageComponent.createFromText(clone.getName() + ": " + "Help! My guard position is now occupied by some blocks!"));
			}else{
				if(Minecraft.getMinecraft() != null){
					Minecraft mc = Minecraft.getMinecraft();
					if(mc.thePlayer != null){
						mc.thePlayer.addChatMessage("Clone " + "\2477" + clone.getName() + "\247f" + " can't return to his guard position. You might wanna check it out.");
					}
				}
			}
		}
	}*/
	
	

}
