package net.jamezo97.clonecraft.command;

import java.util.List;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.command.parameter.PGuess;
import net.jamezo97.clonecraft.command.parameter.ParamGuess;
import net.jamezo97.clonecraft.command.parameter.Parameter;
import net.jamezo97.clonecraft.command.parameter.Parameters;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.command.word.WordSet;
import net.jamezo97.clonecraft.network.Handler6KillClone;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class CommandKill extends Command{
	
	

	public CommandKill() {
		super(null, new Parameter[]{Parameters.p_entityType/*, Parameters.p_courtesy*/}, new Parameter[]{Parameters.p_quantity, Parameters.p_courtesy});
	}

	@Override
	public CommandTask getCommandExecutionDelegate() {
		
		return new CommandKillTask(this);
		
		/*return new CommandTaskOnce(){
			
			

			@Override
			public void execute() {
				
				
				Object toKill = this.paramSet.getParamValue(Parameters.p_entityType).getBestGuess().value;
				
				if(toKill == EntityClone.class)
				{
					this.commander.addChatComponentMessage(new ChatComponentText("Okay then!"));
					
					this.clone.commitSuicide();
					
					new Handler6KillClone(this.clone).sendToAllWatching(this.clone);
				}
				else
				{
					if(toKill instanceof EntityLivingBase)
					{
						if(this.clone.canAttackEntity((EntityLivingBase)toKill)){
							this.commander.addChatComponentMessage(new ChatComponentText("Righteo!"));
							this.clone.setAttackTarget((EntityLivingBase)toKill);
							this.clone.setPath(this.clone.getNavigator().getPathToEntityLiving((EntityLivingBase)toKill));
						}
					}
					
				}
			}
		};*/
		
	}
	
	

	@Override
	public String[] getBonusWords() {
		return new String[]{"commit", "go"};
	}

	@Override
	public WordSet getRequiredVerbs() {
		return WordSet.attack;
	}
	
	
	public static class CommandKillTask extends CommandTask{

		Class[] classesToAttack = null;
		int amountToKill = 1;
		
		public CommandKillTask(Command commandBase) {
			super(commandBase);
		}

		@Override
		public Parameter taskInit(EntityClone clone, EntityPlayer sender, CurrentParams params) {
			PGuess p_entityType = params.getParamValue(Parameters.p_entityType);
			
			p_entityType.sort();
			
			Class[] classes = p_entityType.castToArray(Class.class);
			classesToAttack = classes;
			
			String name = "of those";
			
			if(classes.length == 1 && classes[0] == EntityClone.class)
			{
				PGuess p_courtesy = params.getParamValue(Parameters.p_courtesy);
				
				if(p_courtesy != null && p_courtesy.getBestGuess() != null)
				{
					clone.say("Okay then!", sender);
					
					
					clone.commitSuicide();
					
					new Handler6KillClone(clone).sendToAllWatching(clone);
					amountToKill = 0;
					return null;
				}
				else
				{
					return Parameters.p_courtesy;
				}
				
				
			}
			else if(classes.length == 1)
			{
				name = (String)EntityList.classToStringMapping.get(classes[0]);
			}
			
			PGuess p_quantity = params.getParamValue(Parameters.p_quantity);
			

			
			
			if(p_quantity != null)
			{
				ParamGuess guess = p_quantity.getBestGuess();
				
				if(guess != null && guess.value instanceof Integer)
				{
					int theAmount = (Integer)guess.value;
					amountToKill = theAmount;
				}
			}

			clone.say("Okay, I'll try and kill " + amountToKill + " " + name + (amountToKill>1?"s":"") + "!", sender);
			
			return null;
		}

		

		@Override
		public boolean shouldExecute() {
			if(amountToKill > 0)
			{
				if(clone.getAttackTarget() == null || (!clone.shouldProvokeAttack(clone.getAttackTarget()) && !clone.canAttackEntity(clone.getAttackTarget())))
				{
					EntityLivingBase attack = this.getClosestEntityToAttackExcluding(null);
					if(attack != null)
					{
						clone.setAttackTarget(attack);
						clone.setPath(clone.getNavigator().getPathToEntityLiving(attack));
						return true;
					}
					else
					{
						//If the current target is because of an act of revenge, and is not a selected enemy
						if(clone.shouldProvokeAttack(clone.getAttackTarget()))
						{
							clone.setAttackTarget(null);
						}
					}
				}
				return clone.getAttackTarget() != null || lastAttacked != null;
			}
			return false;
		}

		
		EntityLivingBase lastAttacked = null;
		@Override
		public void updateTask() {
			
			if(lastAttacked != null && !lastAttacked.isEntityAlive())
			{
				amountToKill--;
				if(amountToKill == 0)
				{
					clone.getCommandAI().clear();
					clone.setAttackTarget(null);
					clone.getNavigator().clearPathEntity();
					clone.say("Okay! I'm done!", clone.getPlayerByName(this.commanderName));
				}
				lastAttacked = null;
			}

			if(clone.getAttackTarget() != null && clone.getAttackTarget().isEntityAlive())
			{
				lastAttacked = clone.getAttackTarget();
			}
		}

		@Override
		public void saveTask(NBTTagCompound nbt) {
			nbt.setInteger("amount", this.amountToKill);
			
			int[] ids = new int[this.classesToAttack.length];
			
			int index = 0;
			
			for(int a = 0; a < this.classesToAttack.length; a++)
			{
				Integer ID = CCEntityList.classToId.get(this.classesToAttack[a]);
				if(ID != null)
				{
					ids[index++] = ID;
				}
				else
				{
					int[] newIds = new int[ids.length - 1];
					System.arraycopy(ids, 0, newIds, 0, newIds.length);
					ids = newIds;
				}
			}
			
			nbt.setIntArray("AttackIds", ids);
			
		}

		@Override
		public void loadTask(NBTTagCompound nbt) {
			this.amountToKill = nbt.getInteger("amount");
			
			int[] array = nbt.getIntArray("AttackIds");
			
			this.classesToAttack = new Class[array.length];
			
			try{
				int index = 0;
				
				for(int a = 0; a < array.length; a++)
				{
					Class theClass = CCEntityList.idToClass.get(array[a]);
					
					if(theClass != null)
					{
						this.classesToAttack[index++] = theClass;
					}
					else
					{
						Class[] newClass = new Class[this.classesToAttack.length-1];
						System.arraycopy(this.classesToAttack, 0, newClass, 0, newClass.length);
						this.classesToAttack = newClass;
					}
				}
			}catch(Exception e){classesToAttack = null;}
			
		}

		public EntityLivingBase getClosestEntityToAttackExcluding(EntityLivingBase entityExclude){
			if(classesToAttack == null || classesToAttack.length == 0){
				//Well, there's nothing I can do anymore, so forget about me.
				clone.getCommandAI().clear();
				return null;
			}
			List list = clone.worldObj.getEntitiesWithinAABBExcludingEntity(clone, clone.boundingBox.expand(32D, 16D, 32D));
			
			if(entityExclude != null)
			{
				list.remove(entityExclude);
			}
			
			
			EntityLivingBase closestValid = null;
			
			double distance = Double.MAX_VALUE;
			
			for(int a = 0; a < list.size(); a++)
			{
				if(list.get(a) instanceof EntityLivingBase)
				{
					
					EntityLivingBase entity = (EntityLivingBase)list.get(a);
					
					if(!entity.isEntityAlive() || entity.getHealth() < 0){
						continue;
					}
					if(!clone.canEntityBeSeen(entity)){
						continue;
					}
					
					boolean found = false;
					
					for(int b = 0; b < this.classesToAttack.length; b++)
					{
						if(this.classesToAttack[b] == entity.getClass())
						{
							found = true;
							break;
						}
					}
					
					if(found)
					{
						double d = entity.getDistanceSqToEntity(clone);
						
						if(d < distance)
						{
							distance = d;
							closestValid = entity;
						}
					}
					
					
				}
			}
			
			return closestValid;
		}
		
		
		
		
		
		
		
	}
	

}
