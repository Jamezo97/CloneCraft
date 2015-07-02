package net.jamezo97.clonecraft.clone;

import com.mojang.authlib.GameProfile;

import net.minecraft.block.Block;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecartHopper;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

/**
 * A class to interface the clone with functions which require an EntityPlayer (seems as how a clone is basically just an automated player)
 */
public class FakePlayer extends EntityPlayer{

	EntityClone clone;
	
	
	public FakePlayer(EntityClone clone) {
		super(clone.worldObj, new FakeGameProfile(clone));
		this.clone = clone;
		this.inventory = clone.inventory;
		this.foodStats = clone.foodStats;
	}
	
	public void updateFrom(EntityClone clone) {
		this.onGround = clone.onGround;
	}
	

	@Override
	public FoodStats getFoodStats() {
		return clone.foodStats;
	}



	@Override
	public boolean isEntityAlive() {
		return clone.isEntityAlive();
	}

	@Override
	public void addChatMessage(IChatComponent c) {
		System.out.println("Send message to Clone '" + clone.getCommandSenderName() + "': " + c.getFormattedText());
	}

	@Override
	public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_) {
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates() {
		return new ChunkCoordinates((int)Math.floor(posX), (int)Math.floor(posY), (int)Math.floor(posZ));
	}



	@Override
	public void addPotionEffect(PotionEffect p_70690_1_) {
		clone.addPotionEffect(p_70690_1_);
	}



	@Override
	public ItemStack getItemInUse() {
		
		return clone.getItemInUse();
	}

	@Override
	public int getItemInUseCount() {
		
		return clone.getItemInUseCount();
	}

	@Override
	public boolean isUsingItem() {
		
		return clone.isUsingItem();
	}

	@Override
	public int getItemInUseDuration() {
		
		return clone.getItemInUseDuration();
	}

	@Override
	public void stopUsingItem() {
		
		clone.stopUsingItem();
	}

	@Override
	public void clearItemInUse() {
		
		clone.clearItemInUse();
	}
	


	@Override
	public void onUpdate() {
		
		clone.onUpdate();
	}

	@Override
	public int getMaxInPortalTime() {
		
		return clone.getMaxInPortalTime();
	}



	@Override
	public int getPortalCooldown() {
		
		return clone.getPortalCooldown();
	}

	@Override
	public void playSound(String p_85030_1_, float p_85030_2_, float p_85030_3_) {
		
		clone.playSound(p_85030_1_, p_85030_2_, p_85030_3_);
	}

	@Override
	protected void updateItemUse(ItemStack p_71010_1_, int p_71010_2_) {
		
		clone.updateItemUse(p_71010_1_, p_71010_2_);
	}

	@Override
	protected void onItemUseFinish() {
		
		clone.onItemUseFinish();
	}

	@Override
	public void handleHealthUpdate(byte p_70103_1_) {
		
		clone.handleHealthUpdate(p_70103_1_);
	}
	
	
	
	
	@Override
	public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
		
		return clone.attackEntityFrom(p_70097_1_, p_70097_2_);
	}

	@Override
	protected void damageArmor(float p_70675_1_) {
		
		clone.damageArmor(p_70675_1_);
	}

	@Override
	public int getTotalArmorValue() {
		
		return clone.getTotalArmorValue();
	}
	
	@Override
	public double getYOffset() {
		
		return clone.getYOffset();
	}
	
	@Override
	public void setDead() {
		
		clone.setDead();
	}

	@Override
	public boolean isEntityInsideOpaqueBlock() {
		
		return clone.isEntityInsideOpaqueBlock();
	}
	
	@Override
	public boolean isPlayerSleeping() {
		
		return clone.isPlayerSleeping();
	}

	

	@Override
	public void moveEntityWithHeading(float p_70612_1_, float p_70612_2_) {
		
		clone.moveEntityWithHeading(p_70612_1_, p_70612_2_);
	}

	@Override
	public float getAIMoveSpeed() {
		
		return clone.getAIMoveSpeed();
	}


	@Override
	public void onKillEntity(EntityLivingBase p_70074_1_) {
		
		clone.onKillEntity(p_70074_1_);
	}

	@Override
	public void setInWeb() {
		
		clone.setInWeb();
	}

	@Override
	public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_) {
		
		return clone.getItemIcon(p_70620_1_, p_70620_2_);
	}

	

	@Override
	public boolean shouldHeal() {
		
		return clone.shouldHeal();
	}

	@Override
	public void setItemInUse(ItemStack p_71008_1_, int p_71008_2_) {
		
		clone.setItemInUse(p_71008_1_, p_71008_2_);
	}

	

	@Override
	public boolean getAlwaysRenderNameTagForRender() {
		
		return clone.getAlwaysRenderNameTagForRender();
	}

	

	@Override
	public String getCommandSenderName() {
		
		return clone.getCommandSenderName();
	}

	

	@Override
	public ItemStack getEquipmentInSlot(int p_71124_1_) {
		
		return clone.getEquipmentInSlot(p_71124_1_);
	}

	@Override
	public ItemStack getHeldItem() {
		
		return clone.getHeldItem();
	}

	@Override
	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_) {
		
		clone.setCurrentItemOrArmor(p_70062_1_, p_70062_2_);
	}

	@Override
	public boolean isInvisibleToPlayer(EntityPlayer p_98034_1_) {
		
		return clone.isInvisibleToPlayer(p_98034_1_);
	}

	@Override
	public ItemStack[] getLastActiveItems() {
		
		return clone.getLastActiveItems();
	}

	

	@Override
	public boolean isPushedByWater() {
		
		return clone.isPushedByWater();
	}
	@Override
	public Team getTeam() {
		
		return clone.getTeam();
	}

	@Override
	public IChatComponent func_145748_c_() {
		
		return clone.func_145748_c_();
	}

	@Override
	public void setAbsorptionAmount(float p_110149_1_) {
		
		clone.setAbsorptionAmount(p_110149_1_);
	}

	@Override
	public float getAbsorptionAmount() {
		
		return clone.getAbsorptionAmount();
	}
	
	@Override
	public Vec3 getPosition(float par1) {
		
		return clone.getPosition(par1);
	}



	@Override
	public void mountEntity(Entity p_70078_1_) {
		
		clone.mountEntity(p_70078_1_);
	}

	@Override
	public void updateRidden() {
		
		clone.updateRidden();
	}
	
	@Override
	public void onLivingUpdate() {
		
		clone.onLivingUpdate();
	}
	
	@Override
	public void onDeath(DamageSource p_70645_1_) {
		
		clone.onDeath(p_70645_1_);
	}
	
	@Override
	public void addToPlayerScore(Entity p_70084_1_, int p_70084_2_) {
		
		clone.addToPlayerScore(p_70084_1_, p_70084_2_);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		
		clone.readEntityFromNBT(p_70037_1_);
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		
		clone.writeEntityToNBT(p_70014_1_);
	}
	
	@Override
	public float getEyeHeight() {
		
		return clone.getEyeHeight();
	}




	
//	@Override
//	protected boolean isMovementBlocked() {
//		
//		return clone.isMovementBlocked();
//	}
//
//	@Override
//	public void closeScreen() {
//		
//		clone.closeScreen();
//	}
//
//	@Override
//	public void preparePlayerToSpawn() {
//		
//		clone.preparePlayerToSpawn();
//	}
//
//	@Override
//	protected void updateEntityActionState() {
//		
//		clone.updateEntityActionState();
//	}
//
//	
//	@Override
//	public int getScore() {
//		
//		return clone.getScore();
//	}
//
//	@Override
//	public void setScore(int p_85040_1_) {
//		
//		clone.setScore(p_85040_1_);
//	}
//
//	@Override
//	public void addScore(int p_85039_1_) {
//		
//		clone.addScore(p_85039_1_);
//	}
//
//	
//	@Override
//	protected String getHurtSound() {
//		
//		return clone.getHurtSound();
//	}
//
//	@Override
//	protected String getDeathSound() {
//		
//		return clone.getDeathSound();
//	}
//
//	@Override
//	public EntityItem dropOneItem(boolean p_71040_1_) {
//		
//		return clone.dropOneItem(p_71040_1_);
//	}
//
//	@Override
//	public EntityItem dropPlayerItemWithRandomChoice(ItemStack p_71019_1_,
//			boolean p_71019_2_) {
//		
//		return clone.dropPlayerItemWithRandomChoice(p_71019_1_, p_71019_2_);
//	}
//
//	@Override
//	public EntityItem func_146097_a(ItemStack p_146097_1_, boolean p_146097_2_,
//			boolean p_146097_3_) {
//		
//		return clone.func_146097_a(p_146097_1_, p_146097_2_, p_146097_3_);
//	}
//
//	@Override
//	public void joinEntityItemWithWorld(EntityItem p_71012_1_) {
//		
//		clone.joinEntityItemWithWorld(p_71012_1_);
//	}
//
//	@Override
//	public float getCurrentPlayerStrVsBlock(Block p_146096_1_,
//			boolean p_146096_2_) {
//		
//		return clone.getCurrentPlayerStrVsBlock(p_146096_1_, p_146096_2_);
//	}
//
//	@Override
//	public float getBreakSpeed(Block p_146096_1_, boolean p_146096_2_, int meta) {
//		
//		return clone.getBreakSpeed(p_146096_1_, p_146096_2_, meta);
//	}
//
//	@Override
//	public float getBreakSpeed(Block p_146096_1_, boolean p_146096_2_,
//			int meta, int x, int y, int z) {
//		
//		return clone.getBreakSpeed(p_146096_1_, p_146096_2_, meta, x, y, z);
//	}
//
//	@Override
//	public boolean canHarvestBlock(Block p_146099_1_) {
//		
//		return clone.canHarvestBlock(p_146099_1_);
//	}
//	
//	
//
//	@Override
//	public void displayGUIChest(IInventory p_71007_1_) {
//		
//		clone.displayGUIChest(p_71007_1_);
//	}
//
//	@Override
//	public void func_146093_a(TileEntityHopper p_146093_1_) {
//		
//		clone.func_146093_a(p_146093_1_);
//	}
//
//	@Override
//	public void displayGUIHopperMinecart(EntityMinecartHopper p_96125_1_) {
//		
//		clone.displayGUIHopperMinecart(p_96125_1_);
//	}
//
//	@Override
//	public void displayGUIHorse(EntityHorse p_110298_1_, IInventory p_110298_2_) {
//		
//		clone.displayGUIHorse(p_110298_1_, p_110298_2_);
//	}
//
//	@Override
//	public void displayGUIEnchantment(int p_71002_1_, int p_71002_2_,
//			int p_71002_3_, String p_71002_4_) {
//		
//		clone.displayGUIEnchantment(p_71002_1_, p_71002_2_, p_71002_3_, p_71002_4_);
//	}
//
//	@Override
//	public void displayGUIAnvil(int p_82244_1_, int p_82244_2_, int p_82244_3_) {
//		
//		clone.displayGUIAnvil(p_82244_1_, p_82244_2_, p_82244_3_);
//	}
//
//	@Override
//	public void displayGUIWorkbench(int p_71058_1_, int p_71058_2_,
//			int p_71058_3_) {
//		
//		clone.displayGUIWorkbench(p_71058_1_, p_71058_2_, p_71058_3_);
//	}
//
//	
//
//	@Override
//	protected void resetHeight() {
//		
//		clone.resetHeight();
//	}*/
//
//	
//	
//	@Override
//	public boolean canAttackPlayer(EntityPlayer p_96122_1_) {
//		
//		return clone.canAttackPlayer(p_96122_1_);
//	}
//
//	
//
//	@Override
//	public float getArmorVisibility() {
//		
//		return clone.getArmorVisibility();
//	}
//
//	@Override
//	protected void damageEntity(DamageSource p_70665_1_, float p_70665_2_) {
//		
//		clone.damageEntity(p_70665_1_, p_70665_2_);
//	}
//
//	@Override
//	public void func_146101_a(TileEntityFurnace p_146101_1_) {
//		
//		clone.func_146101_a(p_146101_1_);
//	}
//
//	@Override
//	public void func_146102_a(TileEntityDispenser p_146102_1_) {
//		
//		clone.func_146102_a(p_146102_1_);
//	}
//
//	@Override
//	public void func_146100_a(TileEntity p_146100_1_) {
//		
//		clone.func_146100_a(p_146100_1_);
//	}
//
//	@Override
//	public void func_146095_a(CommandBlockLogic p_146095_1_) {
//		
//		clone.func_146095_a(p_146095_1_);
//	}
//
//	@Override
//	public void func_146098_a(TileEntityBrewingStand p_146098_1_) {
//		
//		clone.func_146098_a(p_146098_1_);
//	}
//
//	@Override
//	public void func_146104_a(TileEntityBeacon p_146104_1_) {
//		
//		clone.func_146104_a(p_146104_1_);
//	}
//
//	@Override
//	public void displayGUIMerchant(IMerchant p_71030_1_, String p_71030_2_) {
//		
//		clone.displayGUIMerchant(p_71030_1_, p_71030_2_);
//	}
//
//	@Override
//	public void displayGUIBook(ItemStack p_71048_1_) {
//		
//		clone.displayGUIBook(p_71048_1_);
//	}
//
//	@Override
//	public boolean interactWith(Entity p_70998_1_) {
//		
//		return clone.interactWith(p_70998_1_);
//	}
//
//	@Override
//	public ItemStack getCurrentEquippedItem() {
//		
//		return clone.getCurrentEquippedItem();
//	}
//
//	@Override
//	public void destroyCurrentEquippedItem() {
//		
//		clone.destroyCurrentEquippedItem();
//	}
//
//
//	@Override
//	public void attackTargetEntityWithCurrentItem(Entity p_71059_1_) {
//		
//		clone.attackTargetEntityWithCurrentItem(p_71059_1_);
//	}
//
//	@Override
//	public void onCriticalHit(Entity p_71009_1_) {
//		
//		clone.onCriticalHit(p_71009_1_);
//	}
//
//	@Override
//	public void onEnchantmentCritical(Entity p_71047_1_) {
//		
//		clone.onEnchantmentCritical(p_71047_1_);
//	}
//
//	@Override
//	public void respawnPlayer() {
//		
//		clone.respawnPlayer();
//	}
//
//	
//
//	@Override
//	public GameProfile getGameProfile() {
//		
//		return clone.getGameProfile();
//	}
//
//	@Override
//	public EnumStatus sleepInBedAt(int p_71018_1_, int p_71018_2_,
//			int p_71018_3_) {
//		
//		return clone.sleepInBedAt(p_71018_1_, p_71018_2_, p_71018_3_);
//	}
//
//	@Override
//	public void wakeUpPlayer(boolean p_70999_1_, boolean p_70999_2_,
//			boolean p_70999_3_) {
//		
//		clone.wakeUpPlayer(p_70999_1_, p_70999_2_, p_70999_3_);
//	}
//
//	@Override
//	public float getBedOrientationInDegrees() {
//		
//		return clone.getBedOrientationInDegrees();
//	}
//
//	
//	
//	@Override
//	public boolean isPlayerFullyAsleep() {
//		
//		return clone.isPlayerFullyAsleep();
//	}
//
//	@Override
//	public int getSleepTimer() {
//		
//		return clone.getSleepTimer();
//	}
//
//	@Override
//	protected boolean getHideCape(int p_82241_1_) {
//		
//		return clone.getHideCape(p_82241_1_);
//	}
//
//	@Override
//	protected void setHideCape(int p_82239_1_, boolean p_82239_2_) {
//		
//		clone.setHideCape(p_82239_1_, p_82239_2_);
//	}
//
//	@Override
//	public void addChatComponentMessage(IChatComponent p_146105_1_) {
//		
//		clone.addChatComponentMessage(p_146105_1_);
//	}
//
//	@Override
//	public ChunkCoordinates getBedLocation() {
//		
//		return clone.getBedLocation();
//	}
//
//	@Override
//	public boolean isSpawnForced() {
//		
//		return clone.isSpawnForced();
//	}
//
//	@Override
//	public void setSpawnChunk(ChunkCoordinates p_71063_1_, boolean p_71063_2_) {
//		
//		clone.setSpawnChunk(p_71063_1_, p_71063_2_);
//	}
//
//	@Override
//	public void triggerAchievement(StatBase p_71029_1_) {
//		
//		clone.triggerAchievement(p_71029_1_);
//	}
//
//	@Override
//	public void addStat(StatBase p_71064_1_, int p_71064_2_) {
//		
//		clone.addStat(p_71064_1_, p_71064_2_);
//	}
//
//	@Override
//	public void jump() {
//		
//		clone.jump();
//	}
//	@Override
//	public void addMovementStat(double p_71000_1_, double p_71000_3_,
//			double p_71000_5_) {
//		
//		clone.addMovementStat(p_71000_1_, p_71000_3_, p_71000_5_);
//	}
//
//	@Override
//	protected void fall(float p_70069_1_) {
//		
//		clone.fall(p_70069_1_);
//	}
//
//	@Override
//	protected String func_146067_o(int p_146067_1_) {
//		
//		return clone.func_146067_o(p_146067_1_);
//	}
//	
//	@Override
//	public ItemStack getCurrentArmor(int p_82169_1_) {
//		
//		return clone.getCurrentArmor(p_82169_1_);
//	}
//
//	@Override
//	public void addExperience(int p_71023_1_) {
//		
//		clone.addExperience(p_71023_1_);
//	}
//
//	@Override
//	public void addExperienceLevel(int p_82242_1_) {
//		
//		clone.addExperienceLevel(p_82242_1_);
//	}
//
//	@Override
//	public int xpBarCap() {
//		
//		return clone.xpBarCap();
//	}
//
//	@Override
//	public void addExhaustion(float p_71020_1_) {
//		
//		clone.addExhaustion(p_71020_1_);
//	}
//
//	@Override
//	public FoodStats getFoodStats() {
//		
//		return clone.getFoodStats();
//	}
//
//	@Override
//	public boolean canEat(boolean p_71043_1_) {
//		
//		return clone.canEat(p_71043_1_);
//	}
//	
//	@Override
//	public boolean isCurrentToolAdventureModeExempt(int p_82246_1_,
//			int p_82246_2_, int p_82246_3_) {
//		
//		return clone.isCurrentToolAdventureModeExempt(p_82246_1_, p_82246_2_,
//				p_82246_3_);
//	}
//
//	@Override
//	public boolean canPlayerEdit(int p_82247_1_, int p_82247_2_,
//			int p_82247_3_, int p_82247_4_, ItemStack p_82247_5_) {
//		
//		return clone.canPlayerEdit(p_82247_1_, p_82247_2_, p_82247_3_, p_82247_4_,
//				p_82247_5_);
//	}
//
//	@Override
//	protected int getExperiencePoints(EntityPlayer p_70693_1_) {
//		
//		return clone.getExperiencePoints(p_70693_1_);
//	}
//
//	@Override
//	protected boolean isPlayer() {
//		
//		return clone.isPlayer();
//	}
//	
//	@Override
//	public void clonePlayer(EntityPlayer p_71049_1_, boolean p_71049_2_) {
//		
//		clone.clonePlayer(p_71049_1_, p_71049_2_);
//	}
//
//	@Override
//	protected boolean canTriggerWalking() {
//		
//		return clone.canTriggerWalking();
//	}
//
//	@Override
//	public void sendPlayerAbilities() {
//		
//		clone.sendPlayerAbilities();
//	}
//
//	@Override
//	public void setGameType(GameType p_71033_1_) {
//		
//		clone.setGameType(p_71033_1_);
//	}
//	
//	@Override
//	public World getEntityWorld() {
//		
//		return clone.getEntityWorld();
//	}
//
//	@Override
//	public InventoryEnderChest getInventoryEnderChest() {
//		
//		return clone.getInventoryEnderChest();
//	}
//	
//	@Override
//	public boolean getHideCape() {
//		
//		return clone.getHideCape();
//	}
//	
//	@Override
//	public Scoreboard getWorldScoreboard() {
//		
//		return clone.getWorldScoreboard();
//	}
//
//	
//
//	@Override
//	public void openGui(Object mod, int modGuiId, World world, int x, int y,
//			int z) {
//		
//		clone.openGui(mod, modGuiId, world, x, y, z);
//	}
//
//	
//
//	@Override
//	public ChunkCoordinates getBedLocation(int dimension) {
//		
//		return clone.getBedLocation(dimension);
//	}
//
//	@Override
//	public boolean isSpawnForced(int dimension) {
//		
//		return clone.isSpawnForced(dimension);
//	}
//
//	@Override
//	public void setSpawnChunk(ChunkCoordinates chunkCoordinates,
//			boolean forced, int dimension) {
//		
//		clone.setSpawnChunk(chunkCoordinates, forced, dimension);
//	}
//
//	@Override
//	public float getDefaultEyeHeight() {
//		
//		return clone.getDefaultEyeHeight();
//	}
//
//	@Override
//	public String getDisplayName() {
//		
//		return clone.getDisplayName();
//	}
//
//	@Override
//	public void refreshDisplayName() {
//		
//		clone.refreshDisplayName();
//	}
//
//	
//	@Override
//	protected void applyEntityAttributes() {
//		
//		clone.applyEntityAttributes();
//	}
//
//	@Override
//	protected void entityInit() {
//		
//		clone.entityInit();
//	}
//
//	@Override
//	public boolean isBlocking() {
//		
//		return clone.isBlocking();
//	}
//	
//	@Override
//	protected String getSwimSound() {
//		
//		return clone.getSwimSound();
//	}
//
//	@Override
//	protected String getSplashSound() {
//		
//		return clone.getSplashSound();
//	}
}
