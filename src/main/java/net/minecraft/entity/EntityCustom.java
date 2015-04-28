package net.minecraft.entity;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Random;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public class EntityCustom extends EntityLivingBase{

	Class<? extends EntityLivingBase> entityType = null;
	
	EntityLivingBase wrappedEntity = null;

	public EntityCustom(World world) {
		super(world);
	}
	
	public EntityLivingBase getEntity(){
		if(wrappedEntity == null && entityType != null)
		{
			Constructor[] constructors = entityType.getConstructors();
			
			for(int a = 0; a < constructors.length; a++)
			{
				if(constructors[a].getParameterTypes() != null && 
						constructors[a].getParameterTypes().length == 1 &&
						constructors[a].getParameterTypes()[0] == World.class)
				{
					try {
						wrappedEntity = (EntityLivingBase)constructors[a].newInstance(worldObj);
						break;
					} catch (Exception e) {
						//That should have worked...
						e.printStackTrace();
					}
				}
			}
		}
		return wrappedEntity;
	}
	
	//Lines: 1449
	protected void entityInit() {
		if(getEntity() == null){ super.entityInit(); }else{ getEntity().entityInit();}
	}

	protected void applyEntityAttributes() {
		if(getEntity() == null){ super.applyEntityAttributes(); }else{ getEntity().applyEntityAttributes();}
	}

	protected void updateFallState(double p_70064_1_, boolean p_70064_3_) {
		if(getEntity() == null){ super.updateFallState(p_70064_1_, p_70064_3_); }else{ getEntity().updateFallState(p_70064_1_, p_70064_3_);}
	}

	public boolean canBreatheUnderwater() {
		if(getEntity() == null){ return super.canBreatheUnderwater(); }else{ return getEntity().canBreatheUnderwater();}
	}

	public void onEntityUpdate() {
		if(getEntity() == null){ super.onEntityUpdate(); }else{ getEntity().onEntityUpdate();}
	}

	public boolean isChild() {
		if(getEntity() == null){ return super.isChild(); }else{ return getEntity().isChild();}
	}

	protected void onDeathUpdate() {
		if(getEntity() == null){ super.onDeathUpdate(); }else{ getEntity().onDeathUpdate();}
	}

	protected boolean func_146066_aG() {
		if(getEntity() == null){ return super.func_146066_aG(); }else{ return getEntity().func_146066_aG();}
	}

	protected int decreaseAirSupply(int p_70682_1_) {
		if(getEntity() == null){ return super.decreaseAirSupply(p_70682_1_); }else{ return getEntity().decreaseAirSupply(p_70682_1_);}
	}

	protected int getExperiencePoints(EntityPlayer p_70693_1_) {
		if(getEntity() == null){ return super.getExperiencePoints(p_70693_1_); }else{ return getEntity().getExperiencePoints(p_70693_1_);}
	}

	protected boolean isPlayer() {
		if(getEntity() == null){ return super.isPlayer(); }else{ return getEntity().isPlayer();}
	}

	public Random getRNG() {
		if(getEntity() == null){ return super.getRNG(); }else{ return getEntity().getRNG();}
	}

	public EntityLivingBase getAITarget() {
		if(getEntity() == null){ return super.getAITarget(); }else{ return getEntity().getAITarget();}
	}

	public int func_142015_aE() {
		if(getEntity() == null){ return super.func_142015_aE(); }else{ return getEntity().func_142015_aE();}
	}

	public void setRevengeTarget(EntityLivingBase p_70604_1_) {
		if(getEntity() == null){ super.setRevengeTarget(p_70604_1_); }else{ getEntity().setRevengeTarget(p_70604_1_);}
	}

	public EntityLivingBase getLastAttacker() {
		if(getEntity() == null){ return super.getLastAttacker(); }else{ return getEntity().getLastAttacker();}
	}

	public int getLastAttackerTime() {
		if(getEntity() == null){ return super.getLastAttackerTime(); }else{ return getEntity().getLastAttackerTime();}
	}

	public void setLastAttacker(Entity p_130011_1_) {
		if(getEntity() == null){ super.setLastAttacker(p_130011_1_); }else{ getEntity().setLastAttacker(p_130011_1_);}
	}

	public int getAge() {
		if(getEntity() == null){ return super.getAge(); }else{ return getEntity().getAge();}
	}

	public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		if(getEntity() == null){ super.writeEntityToNBT(p_70014_1_); }else{ getEntity().writeEntityToNBT(p_70014_1_);}
	}

	public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		if(getEntity() == null){ super.readEntityFromNBT(p_70037_1_); }else{ getEntity().readEntityFromNBT(p_70037_1_);}
	}

	protected void updatePotionEffects() {
		if(getEntity() == null){ super.updatePotionEffects(); }else{ getEntity().updatePotionEffects();}
	}

	public void clearActivePotions() {
		if(getEntity() == null){ super.clearActivePotions(); }else{ getEntity().clearActivePotions();}
	}

	public Collection getActivePotionEffects() {
		if(getEntity() == null){ return super.getActivePotionEffects(); }else{ return getEntity().getActivePotionEffects();}
	}

	public boolean isPotionActive(int p_82165_1_) {
		if(getEntity() == null){ return super.isPotionActive(p_82165_1_); }else{ return getEntity().isPotionActive(p_82165_1_);}
	}

	public boolean isPotionActive(Potion p_70644_1_) {
		if(getEntity() == null){ return super.isPotionActive(p_70644_1_); }else{ return getEntity().isPotionActive(p_70644_1_);}
	}

	public PotionEffect getActivePotionEffect(Potion p_70660_1_) {
		if(getEntity() == null){ return super.getActivePotionEffect(p_70660_1_); }else{ return getEntity().getActivePotionEffect(p_70660_1_);}
	}

	public void addPotionEffect(PotionEffect p_70690_1_) {
		if(getEntity() == null){ super.addPotionEffect(p_70690_1_); }else{ getEntity().addPotionEffect(p_70690_1_);}
	}

	public boolean isPotionApplicable(PotionEffect p_70687_1_) {
		if(getEntity() == null){ return super.isPotionApplicable(p_70687_1_); }else{ return getEntity().isPotionApplicable(p_70687_1_);}
	}

	public boolean isEntityUndead() {
		if(getEntity() == null){ return super.isEntityUndead(); }else{ return getEntity().isEntityUndead();}
	}

	public void removePotionEffectClient(int p_70618_1_) {
		if(getEntity() == null){ super.removePotionEffectClient(p_70618_1_); }else{ getEntity().removePotionEffectClient(p_70618_1_);}
	}

	public void removePotionEffect(int p_82170_1_) {
		if(getEntity() == null){ super.removePotionEffect(p_82170_1_); }else{ getEntity().removePotionEffect(p_82170_1_);}
	}

	protected void onNewPotionEffect(PotionEffect p_70670_1_) {
		if(getEntity() == null){ super.onNewPotionEffect(p_70670_1_); }else{ getEntity().onNewPotionEffect(p_70670_1_);}
	}

	protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_) {
		if(getEntity() == null){ super.onChangedPotionEffect(p_70695_1_, p_70695_2_); }else{ getEntity().onChangedPotionEffect(p_70695_1_, p_70695_2_);}
	}

	protected void onFinishedPotionEffect(PotionEffect p_70688_1_) {
		if(getEntity() == null){ super.onFinishedPotionEffect(p_70688_1_); }else{ getEntity().onFinishedPotionEffect(p_70688_1_);}
	}

	public void heal(float p_70691_1_) {
		if(getEntity() == null){ super.heal(p_70691_1_); }else{ getEntity().heal(p_70691_1_);}
	}

	public void setHealth(float p_70606_1_) {
		if(getEntity() == null){ super.setHealth(p_70606_1_); }else{ getEntity().setHealth(p_70606_1_);}
	}

	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		if(getEntity() == null){ return super.attackEntityFrom(p_70097_1_, p_70097_2_); }else{ return getEntity().attackEntityFrom(p_70097_1_, p_70097_2_);}
	}

	public void renderBrokenItemStack(ItemStack p_70669_1_) {
		if(getEntity() == null){ super.renderBrokenItemStack(p_70669_1_); }else{ getEntity().renderBrokenItemStack(p_70669_1_);}
	}

	public void onDeath(DamageSource p_70645_1_) {
		if(getEntity() == null){ super.onDeath(p_70645_1_); }else{ getEntity().onDeath(p_70645_1_);}
	}

	protected void dropEquipment(boolean p_82160_1_, int p_82160_2_) {
		if(getEntity() == null){ super.dropEquipment(p_82160_1_, p_82160_2_); }else{ getEntity().dropEquipment(p_82160_1_, p_82160_2_);}
	}

	public void knockBack(Entity p_70653_1_, float p_70653_2_, double p_70653_3_, double p_70653_5_) {
		if(getEntity() == null){ super.knockBack(p_70653_1_, p_70653_2_, p_70653_3_, p_70653_5_); }else{ getEntity().knockBack(p_70653_1_, p_70653_2_, p_70653_3_, p_70653_5_);}
	}

	protected String getHurtSound() {
		if(getEntity() == null){ return super.getHurtSound(); }else{ return getEntity().getHurtSound();}
	}

	protected String getDeathSound() {
		if(getEntity() == null){ return super.getDeathSound(); }else{ return getEntity().getDeathSound();}
	}

	protected void dropRareDrop(int p_70600_1_) {
		if(getEntity() == null){ super.dropRareDrop(p_70600_1_); }else{ getEntity().dropRareDrop(p_70600_1_);}
	}

	protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
		if(getEntity() == null){ super.dropFewItems(p_70628_1_, p_70628_2_); }else{ getEntity().dropFewItems(p_70628_1_, p_70628_2_);}
	}

	public boolean isOnLadder() {
		if(getEntity() == null){ return super.isOnLadder(); }else{ return getEntity().isOnLadder();}
	}

	public boolean isEntityAlive() {
		if(getEntity() == null){ return super.isEntityAlive(); }else{ return getEntity().isEntityAlive();}
	}

	protected void fall(float p_70069_1_) {
		if(getEntity() == null){ super.fall(p_70069_1_); }else{ getEntity().fall(p_70069_1_);}
	}

	protected String func_146067_o(int p_146067_1_) {
		if(getEntity() == null){ return super.func_146067_o(p_146067_1_); }else{ return getEntity().func_146067_o(p_146067_1_);}
	}

	public void performHurtAnimation() {
		if(getEntity() == null){ super.performHurtAnimation(); }else{ getEntity().performHurtAnimation();}
	}

	public int getTotalArmorValue() {
		if(getEntity() == null){ return super.getTotalArmorValue(); }else{ return getEntity().getTotalArmorValue();}
	}

	protected void damageArmor(float p_70675_1_) {
		if(getEntity() == null){ super.damageArmor(p_70675_1_); }else{ getEntity().damageArmor(p_70675_1_);}
	}

	protected float applyArmorCalculations(DamageSource p_70655_1_, float p_70655_2_) {
		if(getEntity() == null){ return super.applyArmorCalculations(p_70655_1_, p_70655_2_); }else{ return getEntity().applyArmorCalculations(p_70655_1_, p_70655_2_);}
	}

	protected float applyPotionDamageCalculations(DamageSource p_70672_1_, float p_70672_2_) {
		if(getEntity() == null){ return super.applyPotionDamageCalculations(p_70672_1_, p_70672_2_); }else{ return getEntity().applyPotionDamageCalculations(p_70672_1_, p_70672_2_);}
	}

	protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
		if(getEntity() == null){ super.damageEntity(p_70665_1_, p_70665_2_); }else{ getEntity().damageEntity(p_70665_1_, p_70665_2_);}
	}

	public CombatTracker func_110142_aN() {
		if(getEntity() == null){ return super.func_110142_aN(); }else{ return getEntity().func_110142_aN();}
	}

	public EntityLivingBase func_94060_bK() {
		if(getEntity() == null){ return super.func_94060_bK(); }else{ return getEntity().func_94060_bK();}
	}

	public void swingItem() {
		if(getEntity() == null){ super.swingItem(); }else{ getEntity().swingItem();}
	}

	public void handleHealthUpdate(byte p_70103_1_) {
		if(getEntity() == null){ super.handleHealthUpdate(p_70103_1_); }else{ getEntity().handleHealthUpdate(p_70103_1_);}
	}

	protected void kill() {
		if(getEntity() == null){ super.kill(); }else{ getEntity().kill();}
	}

	protected void updateArmSwingProgress() {
		if(getEntity() == null){ super.updateArmSwingProgress(); }else{ getEntity().updateArmSwingProgress();}
	}

	public IAttributeInstance getEntityAttribute(IAttribute p_110148_1_) {
		if(getEntity() == null){ return super.getEntityAttribute(p_110148_1_); }else{ return getEntity().getEntityAttribute(p_110148_1_);}
	}

	public BaseAttributeMap getAttributeMap() {
		if(getEntity() == null){ return super.getAttributeMap(); }else{ return getEntity().getAttributeMap();}
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		if(getEntity() == null){ return super.getCreatureAttribute(); }else{ return getEntity().getCreatureAttribute();}
	}

	public ItemStack getHeldItem() {
		if(getEntity() == null){ return null; }else{ return getEntity().getHeldItem();}
	}

	public ItemStack getEquipmentInSlot(int p_71124_1_) {
		if(getEntity() == null){ return null; }else{ return getEntity().getEquipmentInSlot(p_71124_1_);}
	}

	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_) {
		if(getEntity() == null){  }else{ getEntity().setCurrentItemOrArmor(p_70062_1_, p_70062_2_);}
	}

	public void setSprinting(boolean p_70031_1_) {
		if(getEntity() == null){ super.setSprinting(p_70031_1_); }else{ getEntity().setSprinting(p_70031_1_);}
	}

	public ItemStack[] getLastActiveItems() {
		if(getEntity() == null){ return null; }else{ return getEntity().getLastActiveItems();}
	}

	protected float getSoundVolume() {
		if(getEntity() == null){ return super.getSoundVolume(); }else{ return getEntity().getSoundVolume();}
	}

	protected float getSoundPitch() {
		if(getEntity() == null){ return super.getSoundPitch(); }else{ return getEntity().getSoundPitch();}
	}

	protected boolean isMovementBlocked() {
		if(getEntity() == null){ return super.isMovementBlocked(); }else{ return getEntity().isMovementBlocked();}
	}

	public void setPositionAndUpdate(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
		if(getEntity() == null){ super.setPositionAndUpdate(p_70634_1_, p_70634_3_, p_70634_5_); }else{ getEntity().setPositionAndUpdate(p_70634_1_, p_70634_3_, p_70634_5_);}
	}

	public void dismountEntity(Entity p_110145_1_) {
		if(getEntity() == null){ super.dismountEntity(p_110145_1_); }else{ getEntity().dismountEntity(p_110145_1_);}
	}

	public boolean getAlwaysRenderNameTagForRender() {
		if(getEntity() == null){ return super.getAlwaysRenderNameTagForRender(); }else{ return getEntity().getAlwaysRenderNameTagForRender();}
	}

	public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_) {
		if(getEntity() == null){ return super.getItemIcon(p_70620_1_, p_70620_2_); }else{ return getEntity().getItemIcon(p_70620_1_, p_70620_2_);}
	}

	protected void jump() {
		if(getEntity() == null){ super.jump(); }else{ getEntity().jump();}
	}

	public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
		if(getEntity() == null){ super.moveEntityWithHeading(p_70612_1_, p_70612_2_); }else{ getEntity().moveEntityWithHeading(p_70612_1_, p_70612_2_);}
	}

	protected boolean isAIEnabled() {
		if(getEntity() == null){ return super.isAIEnabled(); }else{ return getEntity().isAIEnabled();}
	}

	public float getAIMoveSpeed() {
		if(getEntity() == null){ return super.getAIMoveSpeed(); }else{ return getEntity().getAIMoveSpeed();}
	}

	public void setAIMoveSpeed(float p_70659_1_) {
		if(getEntity() == null){ super.setAIMoveSpeed(p_70659_1_); }else{ getEntity().setAIMoveSpeed(p_70659_1_);}
	}

	public boolean attackEntityAsMob(Entity p_70652_1_) {
		if(getEntity() == null){ return super.attackEntityAsMob(p_70652_1_); }else{ return getEntity().attackEntityAsMob(p_70652_1_);}
	}

	public boolean isPlayerSleeping() {
		if(getEntity() == null){ return super.isPlayerSleeping(); }else{ return getEntity().isPlayerSleeping();}
	}

	public void onUpdate() {
		if(getEntity() == null){ super.onUpdate(); }else{ getEntity().onUpdate();}
	}

	protected float func_110146_f(float p_110146_1_, float p_110146_2_) {
		if(getEntity() == null){ return super.func_110146_f(p_110146_1_, p_110146_2_); }else{ return getEntity().func_110146_f(p_110146_1_, p_110146_2_);}
	}

	public void onLivingUpdate() {
		if(getEntity() == null){ super.onLivingUpdate(); }else{ getEntity().onLivingUpdate();}
	}

	protected void updateAITasks() {
		if(getEntity() == null){ super.updateAITasks(); }else{ getEntity().updateAITasks();}
	}

	protected void collideWithNearbyEntities() {
		if(getEntity() == null){ super.collideWithNearbyEntities(); }else{ getEntity().collideWithNearbyEntities();}
	}

	protected void collideWithEntity(Entity p_82167_1_) {
		if(getEntity() == null){ super.collideWithEntity(p_82167_1_); }else{ getEntity().collideWithEntity(p_82167_1_);}
	}

	public void updateRidden() {
		if(getEntity() == null){ super.updateRidden(); }else{ getEntity().updateRidden();}
	}

	public void setPositionAndRotation2(double p_70056_1_, double p_70056_3_, double p_70056_5_, float p_70056_7_, float p_70056_8_, int p_70056_9_) {
		if(getEntity() == null){ super.setPositionAndRotation2(p_70056_1_, p_70056_3_, p_70056_5_, p_70056_7_, p_70056_8_, p_70056_9_); }else{ getEntity().setPositionAndRotation2(p_70056_1_, p_70056_3_, p_70056_5_, p_70056_7_, p_70056_8_, p_70056_9_);}
	}

	protected void updateAITick() {
		if(getEntity() == null){ super.updateAITick(); }else{ getEntity().updateAITick();}
	}

	protected void updateEntityActionState() {
		if(getEntity() == null){ super.updateEntityActionState(); }else{ getEntity().updateEntityActionState();}
	}

	public void setJumping(boolean p_70637_1_) {
		if(getEntity() == null){ super.setJumping(p_70637_1_); }else{ getEntity().setJumping(p_70637_1_);}
	}

	public void onItemPickup(Entity p_71001_1_, int p_71001_2_) {
		if(getEntity() == null){ super.onItemPickup(p_71001_1_, p_71001_2_); }else{ getEntity().onItemPickup(p_71001_1_, p_71001_2_);}
	}

	public boolean canEntityBeSeen(Entity p_70685_1_) {
		if(getEntity() == null){ return super.canEntityBeSeen(p_70685_1_); }else{ return getEntity().canEntityBeSeen(p_70685_1_);}
	}

	public Vec3 getLookVec() {
		if(getEntity() == null){ return super.getLookVec(); }else{ return getEntity().getLookVec();}
	}

	public Vec3 getLook(float p_70676_1_) {
		if(getEntity() == null){ return super.getLook(p_70676_1_); }else{ return getEntity().getLook(p_70676_1_);}
	}

	public float getSwingProgress(float p_70678_1_) {
		if(getEntity() == null){ return super.getSwingProgress(p_70678_1_); }else{ return getEntity().getSwingProgress(p_70678_1_);}
	}

	public Vec3 getPosition(float p_70666_1_) {
		if(getEntity() == null){ return super.getPosition(p_70666_1_); }else{ return getEntity().getPosition(p_70666_1_);}
	}

	public MovingObjectPosition rayTrace(double p_70614_1_, float p_70614_3_) {
		if(getEntity() == null){ return super.rayTrace(p_70614_1_, p_70614_3_); }else{ return getEntity().rayTrace(p_70614_1_, p_70614_3_);}
	}

	public boolean isClientWorld() {
		if(getEntity() == null){ return super.isClientWorld(); }else{ return getEntity().isClientWorld();}
	}

	public boolean canBeCollidedWith() {
		if(getEntity() == null){ return super.canBeCollidedWith(); }else{ return getEntity().canBeCollidedWith();}
	}

	public boolean canBePushed() {
		if(getEntity() == null){ return super.canBePushed(); }else{ return getEntity().canBePushed();}
	}

	public float getEyeHeight() {
		if(getEntity() == null){ return super.getEyeHeight(); }else{ return getEntity().getEyeHeight();}
	}

	protected void setBeenAttacked() {
		if(getEntity() == null){ super.setBeenAttacked(); }else{ getEntity().setBeenAttacked();}
	}

	public float getRotationYawHead() {
		if(getEntity() == null){ return super.getRotationYawHead(); }else{ return getEntity().getRotationYawHead();}
	}

	public void setRotationYawHead(float p_70034_1_) {
		if(getEntity() == null){ super.setRotationYawHead(p_70034_1_); }else{ getEntity().setRotationYawHead(p_70034_1_);}
	}

	public float getAbsorptionAmount() {
		if(getEntity() == null){ return super.getAbsorptionAmount(); }else{ return getEntity().getAbsorptionAmount();}
	}

	public void setAbsorptionAmount(float p_110149_1_) {
		if(getEntity() == null){ super.setAbsorptionAmount(p_110149_1_); }else{ getEntity().setAbsorptionAmount(p_110149_1_);}
	}

	public Team getTeam() {
		if(getEntity() == null){ return super.getTeam(); }else{ return getEntity().getTeam();}
	}

	public boolean isOnSameTeam(EntityLivingBase p_142014_1_) {
		if(getEntity() == null){ return super.isOnSameTeam(p_142014_1_); }else{ return getEntity().isOnSameTeam(p_142014_1_);}
	}

	public boolean isOnTeam(Team p_142012_1_) {
		if(getEntity() == null){ return super.isOnTeam(p_142012_1_); }else{ return getEntity().isOnTeam(p_142012_1_);}
	}

	public void curePotionEffects(ItemStack curativeItem) {
		if(getEntity() == null){ super.curePotionEffects(curativeItem); }else{ getEntity().curePotionEffects(curativeItem);}
	}

	public boolean shouldRiderFaceForward(EntityPlayer player) {
		if(getEntity() == null){ return super.shouldRiderFaceForward(player); }else{ return getEntity().shouldRiderFaceForward(player);}
	}

	public void func_152111_bt() {
		if(getEntity() == null){ super.func_152111_bt(); }else{ getEntity().func_152111_bt();}
	}

	public void func_152112_bu() {
		if(getEntity() == null){ super.func_152112_bu(); }else{ getEntity().func_152112_bu();}
	}

	public int getEntityId() {
		if(getEntity() == null){ return super.getEntityId(); }else{ return getEntity().getEntityId();}
	}

	public void setEntityId(int p_145769_1_) {
		if(getEntity() == null){ super.setEntityId(p_145769_1_); }else{ getEntity().setEntityId(p_145769_1_);}
	}

	public DataWatcher getDataWatcher() {
		if(getEntity() == null){ return super.getDataWatcher(); }else{ return getEntity().getDataWatcher();}
	}

	public boolean equals(Object p_equals_1_) {
		if(getEntity() == null){ return super.equals(p_equals_1_); }else{ return getEntity().equals(p_equals_1_);}
	}

	public int hashCode() {
		if(getEntity() == null){ return super.hashCode(); }else{ return getEntity().hashCode();}
	}

	protected void preparePlayerToSpawn() {
		if(getEntity() == null){ super.preparePlayerToSpawn(); }else{ getEntity().preparePlayerToSpawn();}
	}

	public void setDead() {
		if(getEntity() == null){ super.setDead(); }else{ getEntity().setDead();}
	}

	protected void setSize(float p_70105_1_, float p_70105_2_) {
		if(getEntity() == null){ super.setSize(p_70105_1_, p_70105_2_); }else{ getEntity().setSize(p_70105_1_, p_70105_2_);}
	}

	protected void setRotation(float p_70101_1_, float p_70101_2_) {
		if(getEntity() == null){ super.setRotation(p_70101_1_, p_70101_2_); }else{ getEntity().setRotation(p_70101_1_, p_70101_2_);}
	}

	public void setPosition(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
		if(getEntity() == null){ super.setPosition(p_70107_1_, p_70107_3_, p_70107_5_); }else{ getEntity().setPosition(p_70107_1_, p_70107_3_, p_70107_5_);}
	}

	public void setAngles(float p_70082_1_, float p_70082_2_) {
		if(getEntity() == null){ super.setAngles(p_70082_1_, p_70082_2_); }else{ getEntity().setAngles(p_70082_1_, p_70082_2_);}
	}

	public int getMaxInPortalTime() {
		if(getEntity() == null){ return super.getMaxInPortalTime(); }else{ return getEntity().getMaxInPortalTime();}
	}

	protected void setOnFireFromLava() {
		if(getEntity() == null){ super.setOnFireFromLava(); }else{ getEntity().setOnFireFromLava();}
	}

	public void setFire(int p_70015_1_) {
		if(getEntity() == null){ super.setFire(p_70015_1_); }else{ getEntity().setFire(p_70015_1_);}
	}

	public void extinguish() {
		if(getEntity() == null){ super.extinguish(); }else{ getEntity().extinguish();}
	}

	public boolean isOffsetPositionInLiquid(double p_70038_1_, double p_70038_3_, double p_70038_5_) {
		if(getEntity() == null){ return super.isOffsetPositionInLiquid(p_70038_1_, p_70038_3_, p_70038_5_); }else{ return getEntity().isOffsetPositionInLiquid(p_70038_1_, p_70038_3_, p_70038_5_);}
	}

	public void moveEntity(double p_70091_1_, double p_70091_3_, double p_70091_5_) {
		if(getEntity() == null){ super.moveEntity(p_70091_1_, p_70091_3_, p_70091_5_); }else{ getEntity().moveEntity(p_70091_1_, p_70091_3_, p_70091_5_);}
	}

	protected String getSwimSound() {
		if(getEntity() == null){ return super.getSwimSound(); }else{ return getEntity().getSwimSound();}
	}

	protected void func_145775_I() {
		if(getEntity() == null){ super.func_145775_I(); }else{ getEntity().func_145775_I();}
	}

	protected void func_145780_a(int p_145780_1_, int p_145780_2_, int p_145780_3_, Block p_145780_4_) {
		if(getEntity() == null){ super.func_145780_a(p_145780_1_, p_145780_2_, p_145780_3_, p_145780_4_); }else{ getEntity().func_145780_a(p_145780_1_, p_145780_2_, p_145780_3_, p_145780_4_);}
	}

	public void playSound(String p_85030_1_, float p_85030_2_, float p_85030_3_) {
		if(getEntity() == null){ super.playSound(p_85030_1_, p_85030_2_, p_85030_3_); }else{ getEntity().playSound(p_85030_1_, p_85030_2_, p_85030_3_);}
	}

	protected boolean canTriggerWalking() {
		if(getEntity() == null){ return super.canTriggerWalking(); }else{ return getEntity().canTriggerWalking();}
	}

	public AxisAlignedBB getBoundingBox() {
		if(getEntity() == null){ return super.getBoundingBox(); }else{ return getEntity().getBoundingBox();}
	}

	protected void dealFireDamage(int p_70081_1_) {
		if(getEntity() == null){ super.dealFireDamage(p_70081_1_); }else{ getEntity().dealFireDamage(p_70081_1_);}
	}

	public boolean isWet() {
		if(getEntity() == null){ return super.isWet(); }else{ return getEntity().isWet();}
	}

	public boolean isInWater() {
		if(getEntity() == null){ return super.isInWater(); }else{ return getEntity().isInWater();}
	}

	public boolean handleWaterMovement() {
		if(getEntity() == null){ return super.handleWaterMovement(); }else{ return getEntity().handleWaterMovement();}
	}

	protected String getSplashSound() {
		if(getEntity() == null){ return super.getSplashSound(); }else{ return getEntity().getSplashSound();}
	}

	public boolean isInsideOfMaterial(Material p_70055_1_) {
		if(getEntity() == null){ return super.isInsideOfMaterial(p_70055_1_); }else{ return getEntity().isInsideOfMaterial(p_70055_1_);}
	}

	public boolean handleLavaMovement() {
		if(getEntity() == null){ return super.handleLavaMovement(); }else{ return getEntity().handleLavaMovement();}
	}

	public void moveFlying(float p_70060_1_, float p_70060_2_, float p_70060_3_) {
		if(getEntity() == null){ super.moveFlying(p_70060_1_, p_70060_2_, p_70060_3_); }else{ getEntity().moveFlying(p_70060_1_, p_70060_2_, p_70060_3_);}
	}

	public int getBrightnessForRender(float p_70070_1_) {
		if(getEntity() == null){ return super.getBrightnessForRender(p_70070_1_); }else{ return getEntity().getBrightnessForRender(p_70070_1_);}
	}

	public float getBrightness(float p_70013_1_) {
		if(getEntity() == null){ return super.getBrightness(p_70013_1_); }else{ return getEntity().getBrightness(p_70013_1_);}
	}

	public void setWorld(World p_70029_1_) {
		if(getEntity() == null){ super.setWorld(p_70029_1_); }else{ getEntity().setWorld(p_70029_1_);}
	}

	public void setPositionAndRotation(double p_70080_1_, double p_70080_3_, double p_70080_5_, float p_70080_7_, float p_70080_8_) {
		if(getEntity() == null){ super.setPositionAndRotation(p_70080_1_, p_70080_3_, p_70080_5_, p_70080_7_, p_70080_8_); }else{ getEntity().setPositionAndRotation(p_70080_1_, p_70080_3_, p_70080_5_, p_70080_7_, p_70080_8_);}
	}

	public void setLocationAndAngles(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
		if(getEntity() == null){ super.setLocationAndAngles(p_70012_1_, p_70012_3_, p_70012_5_, p_70012_7_, p_70012_8_); }else{ getEntity().setLocationAndAngles(p_70012_1_, p_70012_3_, p_70012_5_, p_70012_7_, p_70012_8_);}
	}

	public float getDistanceToEntity(Entity p_70032_1_) {
		if(getEntity() == null){ return super.getDistanceToEntity(p_70032_1_); }else{ return getEntity().getDistanceToEntity(p_70032_1_);}
	}

	public double getDistanceSq(double p_70092_1_, double p_70092_3_, double p_70092_5_) {
		if(getEntity() == null){ return super.getDistanceSq(p_70092_1_, p_70092_3_, p_70092_5_); }else{ return getEntity().getDistanceSq(p_70092_1_, p_70092_3_, p_70092_5_);}
	}

	public double getDistance(double p_70011_1_, double p_70011_3_, double p_70011_5_) {
		if(getEntity() == null){ return super.getDistance(p_70011_1_, p_70011_3_, p_70011_5_); }else{ return getEntity().getDistance(p_70011_1_, p_70011_3_, p_70011_5_);}
	}

	public double getDistanceSqToEntity(Entity p_70068_1_) {
		if(getEntity() == null){ return super.getDistanceSqToEntity(p_70068_1_); }else{ return getEntity().getDistanceSqToEntity(p_70068_1_);}
	}

	public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
		if(getEntity() == null){ super.onCollideWithPlayer(p_70100_1_); }else{ getEntity().onCollideWithPlayer(p_70100_1_);}
	}

	public void applyEntityCollision(Entity p_70108_1_) {
		if(getEntity() == null){ super.applyEntityCollision(p_70108_1_); }else{ getEntity().applyEntityCollision(p_70108_1_);}
	}

	public void addVelocity(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
		if(getEntity() == null){ super.addVelocity(p_70024_1_, p_70024_3_, p_70024_5_); }else{ getEntity().addVelocity(p_70024_1_, p_70024_3_, p_70024_5_);}
	}

	public void addToPlayerScore(Entity p_70084_1_, int p_70084_2_) {
		if(getEntity() == null){ super.addToPlayerScore(p_70084_1_, p_70084_2_); }else{ getEntity().addToPlayerScore(p_70084_1_, p_70084_2_);}
	}

	public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
		if(getEntity() == null){ return super.isInRangeToRender3d(p_145770_1_, p_145770_3_, p_145770_5_); }else{ return getEntity().isInRangeToRender3d(p_145770_1_, p_145770_3_, p_145770_5_);}
	}

	public boolean isInRangeToRenderDist(double p_70112_1_) {
		if(getEntity() == null){ return super.isInRangeToRenderDist(p_70112_1_); }else{ return getEntity().isInRangeToRenderDist(p_70112_1_);}
	}

	public boolean writeMountToNBT(NBTTagCompound p_98035_1_) {
		if(getEntity() == null){ return super.writeMountToNBT(p_98035_1_); }else{ return getEntity().writeMountToNBT(p_98035_1_);}
	}

	public boolean writeToNBTOptional(NBTTagCompound p_70039_1_) {
		if(getEntity() == null){ return super.writeToNBTOptional(p_70039_1_); }else{ return getEntity().writeToNBTOptional(p_70039_1_);}
	}

	public void writeToNBT(NBTTagCompound p_70109_1_) {
		if(getEntity() == null){ super.writeToNBT(p_70109_1_); }else{ getEntity().writeToNBT(p_70109_1_);}
	}

	public void readFromNBT(NBTTagCompound p_70020_1_) {
		if(getEntity() == null){ super.readFromNBT(p_70020_1_); }else{ getEntity().readFromNBT(p_70020_1_);}
	}

	protected boolean shouldSetPosAfterLoading() {
		if(getEntity() == null){ return super.shouldSetPosAfterLoading(); }else{ return getEntity().shouldSetPosAfterLoading();}
	}

	public void onChunkLoad() {
		if(getEntity() == null){ super.onChunkLoad(); }else{ getEntity().onChunkLoad();}
	}

	protected NBTTagList newDoubleNBTList(double... p_70087_1_) {
		if(getEntity() == null){ return super.newDoubleNBTList(p_70087_1_); }else{ return getEntity().newDoubleNBTList(p_70087_1_);}
	}

	protected NBTTagList newFloatNBTList(float... p_70049_1_) {
		if(getEntity() == null){ return super.newFloatNBTList(p_70049_1_); }else{ return getEntity().newFloatNBTList(p_70049_1_);}
	}

	public EntityItem dropItem(Item p_145779_1_, int p_145779_2_) {
		if(getEntity() == null){ return super.dropItem(p_145779_1_, p_145779_2_); }else{ return getEntity().dropItem(p_145779_1_, p_145779_2_);}
	}

	public EntityItem func_145778_a(Item p_145778_1_, int p_145778_2_, float p_145778_3_) {
		if(getEntity() == null){ return super.func_145778_a(p_145778_1_, p_145778_2_, p_145778_3_); }else{ return getEntity().func_145778_a(p_145778_1_, p_145778_2_, p_145778_3_);}
	}

	public EntityItem entityDropItem(ItemStack p_70099_1_, float p_70099_2_) {
		if(getEntity() == null){ return super.entityDropItem(p_70099_1_, p_70099_2_); }else{ return getEntity().entityDropItem(p_70099_1_, p_70099_2_);}
	}

	public float getShadowSize() {
		if(getEntity() == null){ return super.getShadowSize(); }else{ return getEntity().getShadowSize();}
	}

	public boolean isEntityInsideOpaqueBlock() {
		if(getEntity() == null){ return super.isEntityInsideOpaqueBlock(); }else{ return getEntity().isEntityInsideOpaqueBlock();}
	}

	public boolean interactFirst(EntityPlayer p_130002_1_) {
		if(getEntity() == null){ return super.interactFirst(p_130002_1_); }else{ return getEntity().interactFirst(p_130002_1_);}
	}

	public AxisAlignedBB getCollisionBox(Entity p_70114_1_) {
		if(getEntity() == null){ return super.getCollisionBox(p_70114_1_); }else{ return getEntity().getCollisionBox(p_70114_1_);}
	}

	public void updateRiderPosition() {
		if(getEntity() == null){ super.updateRiderPosition(); }else{ getEntity().updateRiderPosition();}
	}

	public double getYOffset() {
		if(getEntity() == null){ return super.getYOffset(); }else{ return getEntity().getYOffset();}
	}

	public double getMountedYOffset() {
		if(getEntity() == null){ return super.getMountedYOffset(); }else{ return getEntity().getMountedYOffset();}
	}

	public void mountEntity(Entity p_70078_1_) {
		if(getEntity() == null){ super.mountEntity(p_70078_1_); }else{ getEntity().mountEntity(p_70078_1_);}
	}

	public float getCollisionBorderSize() {
		if(getEntity() == null){ return super.getCollisionBorderSize(); }else{ return getEntity().getCollisionBorderSize();}
	}

	public void setInPortal() {
		if(getEntity() == null){ super.setInPortal(); }else{ getEntity().setInPortal();}
	}

	public int getPortalCooldown() {
		if(getEntity() == null){ return super.getPortalCooldown(); }else{ return getEntity().getPortalCooldown();}
	}

	public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
		if(getEntity() == null){ super.setVelocity(p_70016_1_, p_70016_3_, p_70016_5_); }else{ getEntity().setVelocity(p_70016_1_, p_70016_3_, p_70016_5_);}
	}

	public boolean isBurning() {
		if(getEntity() == null){ return super.isBurning(); }else{ return getEntity().isBurning();}
	}

	public boolean isRiding() {
		if(getEntity() == null){ return super.isRiding(); }else{ return getEntity().isRiding();}
	}

	public boolean isSneaking() {
		if(getEntity() == null){ return super.isSneaking(); }else{ return getEntity().isSneaking();}
	}

	public void setSneaking(boolean p_70095_1_) {
		if(getEntity() == null){ super.setSneaking(p_70095_1_); }else{ getEntity().setSneaking(p_70095_1_);}
	}

	public boolean isSprinting() {
		if(getEntity() == null){ return super.isSprinting(); }else{ return getEntity().isSprinting();}
	}

	public boolean isInvisible() {
		if(getEntity() == null){ return super.isInvisible(); }else{ return getEntity().isInvisible();}
	}

	public boolean isInvisibleToPlayer(EntityPlayer p_98034_1_) {
		if(getEntity() == null){ return super.isInvisibleToPlayer(p_98034_1_); }else{ return getEntity().isInvisibleToPlayer(p_98034_1_);}
	}

	public void setInvisible(boolean p_82142_1_) {
		if(getEntity() == null){ super.setInvisible(p_82142_1_); }else{ getEntity().setInvisible(p_82142_1_);}
	}

	public boolean isEating() {
		if(getEntity() == null){ return super.isEating(); }else{ return getEntity().isEating();}
	}

	public void setEating(boolean p_70019_1_) {
		if(getEntity() == null){ super.setEating(p_70019_1_); }else{ getEntity().setEating(p_70019_1_);}
	}

	protected boolean getFlag(int p_70083_1_) {
		if(getEntity() == null){ return super.getFlag(p_70083_1_); }else{ return getEntity().getFlag(p_70083_1_);}
	}

	protected void setFlag(int p_70052_1_, boolean p_70052_2_) {
		if(getEntity() == null){ super.setFlag(p_70052_1_, p_70052_2_); }else{ getEntity().setFlag(p_70052_1_, p_70052_2_);}
	}

	public int getAir() {
		if(getEntity() == null){ return super.getAir(); }else{ return getEntity().getAir();}
	}

	public void setAir(int p_70050_1_) {
		if(getEntity() == null){ super.setAir(p_70050_1_); }else{ getEntity().setAir(p_70050_1_);}
	}

	public void onStruckByLightning(EntityLightningBolt p_70077_1_) {
		if(getEntity() == null){ super.onStruckByLightning(p_70077_1_); }else{ getEntity().onStruckByLightning(p_70077_1_);}
	}

	public void onKillEntity(EntityLivingBase p_70074_1_) {
		if(getEntity() == null){ super.onKillEntity(p_70074_1_); }else{ getEntity().onKillEntity(p_70074_1_);}
	}

	protected boolean func_145771_j(double p_145771_1_, double p_145771_3_, double p_145771_5_) {
		if(getEntity() == null){ return super.func_145771_j(p_145771_1_, p_145771_3_, p_145771_5_); }else{ return getEntity().func_145771_j(p_145771_1_, p_145771_3_, p_145771_5_);}
	}

	public void setInWeb() {
		if(getEntity() == null){ super.setInWeb(); }else{ getEntity().setInWeb();}
	}

	public String getCommandSenderName() {
		if(getEntity() == null){ return super.getCommandSenderName(); }else{ return getEntity().getCommandSenderName();}
	}

	public Entity[] getParts() {
		if(getEntity() == null){ return super.getParts(); }else{ return getEntity().getParts();}
	}

	public boolean isEntityEqual(Entity p_70028_1_) {
		if(getEntity() == null){ return super.isEntityEqual(p_70028_1_); }else{ return getEntity().isEntityEqual(p_70028_1_);}
	}

	public boolean canAttackWithItem() {
		if(getEntity() == null){ return super.canAttackWithItem(); }else{ return getEntity().canAttackWithItem();}
	}

	public boolean hitByEntity(Entity p_85031_1_) {
		if(getEntity() == null){ return super.hitByEntity(p_85031_1_); }else{ return getEntity().hitByEntity(p_85031_1_);}
	}

	public String toString() {
		if(getEntity() == null){ return super.toString(); }else{ return getEntity().toString();}
	}

	public boolean isEntityInvulnerable() {
		if(getEntity() == null){ return super.isEntityInvulnerable(); }else{ return getEntity().isEntityInvulnerable();}
	}

	public void copyLocationAndAnglesFrom(Entity p_82149_1_) {
		if(getEntity() == null){ super.copyLocationAndAnglesFrom(p_82149_1_); }else{ getEntity().copyLocationAndAnglesFrom(p_82149_1_);}
	}

	public void copyDataFrom(Entity p_82141_1_, boolean p_82141_2_) {
		if(getEntity() == null){ super.copyDataFrom(p_82141_1_, p_82141_2_); }else{ getEntity().copyDataFrom(p_82141_1_, p_82141_2_);}
	}

	public void travelToDimension(int p_71027_1_) {
		if(getEntity() == null){ super.travelToDimension(p_71027_1_); }else{ getEntity().travelToDimension(p_71027_1_);}
	}

	public float func_145772_a(Explosion p_145772_1_, World p_145772_2_, int p_145772_3_, int p_145772_4_, int p_145772_5_, Block p_145772_6_) {
		if(getEntity() == null){ return super.func_145772_a(p_145772_1_, p_145772_2_, p_145772_3_, p_145772_4_, p_145772_5_, p_145772_6_); }else{ return getEntity().func_145772_a(p_145772_1_, p_145772_2_, p_145772_3_, p_145772_4_, p_145772_5_, p_145772_6_);}
	}

	public boolean func_145774_a(Explosion p_145774_1_, World p_145774_2_, int p_145774_3_, int p_145774_4_, int p_145774_5_, Block p_145774_6_, float p_145774_7_) {
		if(getEntity() == null){ return super.func_145774_a(p_145774_1_, p_145774_2_, p_145774_3_, p_145774_4_, p_145774_5_, p_145774_6_, p_145774_7_); }else{ return getEntity().func_145774_a(p_145774_1_, p_145774_2_, p_145774_3_, p_145774_4_, p_145774_5_, p_145774_6_, p_145774_7_);}
	}

	public int getMaxSafePointTries() {
		if(getEntity() == null){ return super.getMaxSafePointTries(); }else{ return getEntity().getMaxSafePointTries();}
	}

	public int getTeleportDirection() {
		if(getEntity() == null){ return super.getTeleportDirection(); }else{ return getEntity().getTeleportDirection();}
	}

	public boolean doesEntityNotTriggerPressurePlate() {
		if(getEntity() == null){ return super.doesEntityNotTriggerPressurePlate(); }else{ return getEntity().doesEntityNotTriggerPressurePlate();}
	}

	public void addEntityCrashInfo(CrashReportCategory p_85029_1_) {
		if(getEntity() == null){ super.addEntityCrashInfo(p_85029_1_); }else{ getEntity().addEntityCrashInfo(p_85029_1_);}
	}

	public boolean canRenderOnFire() {
		if(getEntity() == null){ return super.canRenderOnFire(); }else{ return getEntity().canRenderOnFire();}
	}

	public UUID getUniqueID() {
		if(getEntity() == null){ return super.getUniqueID(); }else{ return getEntity().getUniqueID();}
	}

	public boolean isPushedByWater() {
		if(getEntity() == null){ return super.isPushedByWater(); }else{ return getEntity().isPushedByWater();}
	}

	public IChatComponent func_145748_c_() {
		if(getEntity() == null){ return super.func_145748_c_(); }else{ return getEntity().func_145748_c_();}
	}

	public void func_145781_i(int p_145781_1_) {
		if(getEntity() == null){ super.func_145781_i(p_145781_1_); }else{ getEntity().func_145781_i(p_145781_1_);}
	}

	public NBTTagCompound getEntityData() {
		if(getEntity() == null){ return super.getEntityData(); }else{ return getEntity().getEntityData();}
	}

	public boolean shouldRiderSit() {
		if(getEntity() == null){ return super.shouldRiderSit(); }else{ return getEntity().shouldRiderSit();}
	}

	public ItemStack getPickedResult(MovingObjectPosition target) {
		if(getEntity() == null){ return super.getPickedResult(target); }else{ return getEntity().getPickedResult(target);}
	}

	public UUID getPersistentID() {
		if(getEntity() == null){ return super.getPersistentID(); }else{ return getEntity().getPersistentID();}
	}

	public boolean shouldRenderInPass(int pass) {
		if(getEntity() == null){ return super.shouldRenderInPass(pass); }else{ return getEntity().shouldRenderInPass(pass);}
	}

	public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
		if(getEntity() == null){ return super.isCreatureType(type, forSpawnCount); }else{ return getEntity().isCreatureType(type, forSpawnCount);}
	}

	public String registerExtendedProperties(String identifier, IExtendedEntityProperties properties) {
		if(getEntity() == null){ return super.registerExtendedProperties(identifier, properties); }else{ return getEntity().registerExtendedProperties(identifier, properties);}
	}

	public IExtendedEntityProperties getExtendedProperties(String identifier) {
		if(getEntity() == null){ return super.getExtendedProperties(identifier); }else{ return getEntity().getExtendedProperties(identifier);}
	}

	public boolean canRiderInteract() {
		if(getEntity() == null){ return super.canRiderInteract(); }else{ return getEntity().canRiderInteract();}
	}

	public boolean shouldDismountInWater(Entity rider) {
		if(getEntity() == null){ return super.shouldDismountInWater(rider); }else{ return getEntity().shouldDismountInWater(rider);}
	}



}
