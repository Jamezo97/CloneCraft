package net.jamezo97.clonecraft.clone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.jamezo97.clonecraft.CCPostRender;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.GuiHandler;
import net.jamezo97.clonecraft.Reflect;
import net.jamezo97.clonecraft.clone.ai.EntityAIAttackEnemies;
import net.jamezo97.clonecraft.clone.ai.EntityAIBreakBlock;
import net.jamezo97.clonecraft.clone.ai.EntityAICloneLookIdle;
import net.jamezo97.clonecraft.clone.ai.EntityAICloneWalkToItems;
import net.jamezo97.clonecraft.clone.ai.EntityAICloneWander;
import net.jamezo97.clonecraft.clone.ai.EntityAIFollowCloneOwner;
import net.jamezo97.clonecraft.clone.ai.EntityAIReturnGuard;
import net.jamezo97.clonecraft.clone.ai.EntityAIShare;
import net.jamezo97.clonecraft.clone.sync.Syncer;
import net.jamezo97.clonecraft.entity.EntityExplodeCollapseFX;
import net.jamezo97.clonecraft.musics.MusicBase;
import net.jamezo97.clonecraft.render.Renderable;
import net.jamezo97.clonecraft.render.RenderableManager;
import net.jamezo97.util.SimpleList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAccessor;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.CloneCraftWorld;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.google.common.collect.Multimap;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EntityClone extends EntityLiving implements RenderableManager{
	
	

	//For when fishing is implemented
	public EntityFishHook fishEntity;
	
	
	public int experienceLevel;
	public int experienceTotal;
	public float experience;
	
	public FoodStatsClone foodStats = new FoodStatsClone();
	
	public PlayerTeam team = PlayerTeam.Good;
	
	String[] names = {"Jamezo97", "joshua576", "freefaller", "milg8", "philbrush", "CaptainSparklez", "honeydew", "BlueXephos"};
	
	public InventoryClone inventory;
	Syncer watcher;
	CloneOptions options;
	
	float maxScale = 1.0f;
	
	float lastScaleUpdate = 0.5f;
	float preciseScale = 0.5f;

	public EntityClone(World world)
	{
		super(world);
		inventory = new InventoryClone(this);
		watcher = new Syncer(this, 1);
		options = new CloneOptions(this);
		ticksExisted = -1;

		initAI();

		initSounds();
		postInit();
	}
	
	public boolean isAIEnabled()
    {
        return true;
    }
	
	EntityAIBreakBlock aiBreakBlocks;
	
	EntityAIShare aiShareItems;
	
	public void initAI(){
		
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackEnemies(this));
		this.tasks.addTask(2, new EntityAIFollowCloneOwner(this));
		this.tasks.addTask(3, aiBreakBlocks = new EntityAIBreakBlock(this, 16));
		this.tasks.addTask(4, new EntityAICloneWalkToItems(this));
		this.tasks.addTask(5, new EntityAIReturnGuard(this));
		
		this.tasks.addTask(18, aiShareItems = new EntityAIShare(this));
		this.tasks.addTask(19, new EntityAICloneLookIdle(this));
		this.tasks.addTask(20, new EntityAICloneWander(this, 1.0F));
	}
	
	public EntityAIShare getShareAI(){
		return aiShareItems;
	}
	
	public EntityAIBreakBlock getBlockAI(){
		return aiBreakBlocks;
	}
	
	protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.30D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.attackDamage);
        this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(1.0D);
        //AIAttackEntities searches a 32x32*16 box. Round that 16 up a bit to 32,
        //    __________
        //  \/ (32x32)*3 =  ~56
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(56.0D);
    }
	
	public CloneOptions getOptions(){
		return options;
	}
	
	public void postInit(){
		this.dataWatcher.addObject(ID_OPTIONS, Integer.valueOf(options.toInteger()));
	}
	
	@Override
	protected void entityInit() {
		super.entityInit();
	}

	


	@Override
	protected void updateAITasks() {
		super.updateAITasks();
	}

	public Syncer getWatcher()
	{
		return watcher;
	}

	int pId = -1;
	
	int pIndex = 0;

	public void initSounds()
	{
		pId = this.getRNG().nextInt( MusicBase.getSize() );
		
	}
	
	public void playNextTing()
	{
		if(pId > -1)
		{
			if(pIndex >= 0 && pIndex < MusicBase.getSize(pId))
			{
				playSound("random.orb", 0.1F, MusicBase.getPitch(pId, pIndex));
			}
			pIndex++;
			if(pIndex >= MusicBase.getSize(pId))
			{
				pIndex = 0;
			}
		}
		else
		{
			playSound("random.orb", 0.1F,  0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
		}
	}
	
	@Override
	protected boolean interact(EntityPlayer player)
	{
		if(!player.isSneaking() && this.canUseThisEntity(player))
		{
			player.openGui(CloneCraft.INSTANCE, GuiHandler.CLONE, worldObj, this.getEntityId(), 0, 0);
			return true;
		}
		else
		{
			return false;
		}
	}

	
	
	

	

	public boolean shouldHeal(){
		return this.getHealth() > 0.0F && this.getHealth() < getMaxHealth();
	}
	
	RenderSelection renderSelection = null;
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		
		
		
		
		
		if(!worldObj.isRemote){

			pickupNearbyItems();
			this.foodStats.onUpdate(this);
			checkHunger();
			if(this.isSprinting() && this.getNavigator().noPath())
			{
				this.setSprinting(false);
			}
			else if(this.getOptions().sprint.get())
			{
				this.setSprinting(true);
			}
			this.makeOthersAttackMe();
			if(this.getOptions().guard.get())
			{
				if(!this.isGuardPositionSet())
				{
					this.guardPosition.posX = (int)Math.floor(posX);
					this.guardPosition.posY = (int)Math.floor(posY)-1;
					this.guardPosition.posZ = (int)Math.floor(posZ);
				}
			}
			else
			{
				this.guardPosition.posX = Integer.MAX_VALUE;
				this.guardPosition.posY = Integer.MAX_VALUE;
				this.guardPosition.posZ = Integer.MAX_VALUE;
			}
			if(this.getAttackTarget() != null){
				this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 10, this.getVerticalFaceSpeed());
			}
		}else{
			if(renderSelection == null){
				renderSelection = new RenderSelection(this);
				CCPostRender.addRenderable(this, renderSelection);
			}else{
				renderSelection.tick();
			}
		}
		this.updateScale();
		this.updateExperience();
		this.updateUsingItem();
		this.updateArmSwingProgress();
		this.options.onTick();
		if(!worldObj.isRemote){/*long l1=System.nanoTime();*/watcher.tick();/*System.out.println((System.nanoTime()-l1) / 1000000.0f);*/}
	}
	
	
	
	@Override
	public void onEntityUpdate() {
		super.onEntityUpdate();
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 * 12 == 0)
		{
			this.heal(1);
		}
	}
	
	
	

	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (this.isJumping)
        {
            if (this.isInWater())
            {
            	this.motionY += 0.01D;
            }
        }
	}

	@Override
	public void onDeath(DamageSource p_70645_1_) {
		super.onDeath(p_70645_1_);
		if(!worldObj.isRemote){
			this.inventory.dropAllItems();
			dropAllXP();
		}
	}



	

	public void onSpawnedBy(String spawnedBy) {
		this.setOwner(spawnedBy);
		if(!worldObj.isRemote){
			this.setName(names[worldObj.rand.nextInt(names.length)]);
		}
	}
	
	//==================================================================================================================
	//TODO Position Guard
	//==================================================================================================================
	
	//Clone has a 1 in 9903520314283042199192993792 chance of guarding the wrong position. Ha! 1 Million Million Million ish
	ChunkCoordinates guardPosition = new ChunkCoordinates(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE);
	
	/**
	 * 
	 * @return ChunkCoordinates representing the position the clone is guarding.
	 */
	public ChunkCoordinates getGuardPosition(){
		return guardPosition;
	}
	
	public boolean isGuardPositionSet(){
		return guardPosition.posX != Integer.MAX_VALUE || guardPosition.posY != Integer.MAX_VALUE || guardPosition.posZ != Integer.MAX_VALUE; 
	}
	
	public void teleportToGuardPosition(){
		if(isGuardPositionSet()){
			setPosition(this.getGuardPosition().posX+0.5, this.getGuardPosition().posY+1, this.getGuardPosition().posZ+0.5);
		}
	}
	
	//==================================================================================================================
	//TODO Age, Scaling and Growing Up
	//==================================================================================================================
	
	//preciseScale, visibleScale
	
	public void updateScale(){
		setScale(preciseScale + 0.0001f);
		
		if(lastScaleUpdate != preciseScale)
		{
			if(Math.abs(lastScaleUpdate-preciseScale) > 0.02){
				lastScaleUpdate = preciseScale;
			}
		}
	}
	
	public void setScale(float scale){
		if(scale > maxScale)
		{
			return;
		}
		double lastMaxHealth = Math.round(this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());

		this.preciseScale = scale;
		
		if(preciseScale > maxScale)
		{
			preciseScale = maxScale;
		}
		
		double newMaxHealth = Math.round(20.0D * preciseScale);
		
		if(lastMaxHealth != newMaxHealth)
		{
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newMaxHealth);
		}
		if(this.getHealth() > newMaxHealth)
		{
			this.setHealth((float)newMaxHealth);
		}
	}
	
	public float getScale() {
		return preciseScale;
	}
	
	public float getMaxScale(){
		return maxScale;
	}
	
	
	
	@Override
	public float getEyeHeight() {
		return super.getEyeHeight() * (preciseScale);
	}
	
	
	//==================================================================================================================
	//TODO Potion Effects
	//==================================================================================================================
	


	@Override
	protected void onNewPotionEffect(PotionEffect p_70670_1_)
    {
        super.onNewPotionEffect(p_70670_1_);
        this.watcher.getSync(Syncer.ID_POTS).setDirty();
    }
	@Override
    protected void onChangedPotionEffect(PotionEffect p_70695_1_, boolean p_70695_2_)
    {
        super.onChangedPotionEffect(p_70695_1_, p_70695_2_);
        this.watcher.getSync(Syncer.ID_POTS).setDirty();
    }
	@Override
    protected void onFinishedPotionEffect(PotionEffect p_70688_1_)
    {
        super.onFinishedPotionEffect(p_70688_1_);
        this.watcher.getSync(Syncer.ID_POTS).setDirty();
    }
	
	public void forceClearActivePotions()
    {
		SimpleList<Integer> list = new SimpleList<Integer>();
		for(Iterator<?> it = this.getActivePotionEffects().iterator(); it.hasNext();){
			PotionEffect pe = (PotionEffect)it.next();
			list.add(pe.getPotionID());
		}
		for(int a = 0; a < list.size(); a++){
			this.removePotionEffect(list.get(a));
		}
    }
	//==================================================================================================================
	//TODO Attacking
	//==================================================================================================================
	

	
	public PathEntity moveTo(double x, double y, double z) {
		PathEntity path;
		getNavigator().setPath(path = getNavigator().getPathToXYZ(x, y, z), 1.0f);
		return path;
	}
	
	public PathEntity moveToEntity(Entity e) {
		PathEntity path;
		getNavigator().setPath(path = getNavigator().getPathToEntityLiving(e), 1.0f);
		return path;
	}
	
	public void setPath(PathEntity path) {
		getNavigator().setPath(path, 1.0f);
	}
	
	/**
	 * Returns whether the clone should provoke an attack against the entity.
	 * @param entity
	 * @return
	 */
	public boolean shouldProvokeAttack(EntityLivingBase entity){
		if(entity == null){
			return false;
		}
		if(entity instanceof EntityPlayer)
		{
			return team == PlayerTeam.Evil && !((EntityPlayer)entity).capabilities.isCreativeMode;
		}
		else if(entity instanceof EntityClone)
		{
			return team.doesAttackTeam(((EntityClone)entity).team) && ((EntityClone)entity).getOptions().fight.get();
		}
		else
		{
			return this.options.attackables.canAttack(EntityList.getEntityID(entity));
		}
	}
	
	/**
	 * Returns whether the clone is capable of attacking the entity. Even if it's not a selected entity. Used for retaliation
	 * @param entity
	 * @return
	 */
	public boolean canAttackEntity(EntityLivingBase entity){
		if(entity == null)
		{
			return false;
		}
		if(entity instanceof EntityPlayer)
		{
			return team == PlayerTeam.Evil && !((EntityPlayer)entity).capabilities.isCreativeMode;
		}
		else if(entity instanceof EntityClone)
		{
			return team.doesAttackTeam(((EntityClone)entity).team);
		}
		return true;
	}
	
	int attackTimer = 2;
	
	public void attackEntity(EntityLivingBase attack){
		if(attackTimer > 0){
			attackTimer--;
		}
		if(this.foodStats.getFoodLevel() < criticalEatingPoint && this.isEatingFood()){
			return;
		}
		if(!this.canEntityBeSeen(attack)){
			selectBestDamageItem();
			return;
		}
		
		int attackDist = ((attack instanceof EntityPlayer)?4:12);
		
		double distanceSquared = this.getDistanceSqToEntity(attack);
		if(distanceSquared < attackDist){
			selectBestDamageItem();
//			if(distanceSquared < 12){
				if((float)attack.hurtResistantTime <= (float)attack.maxHurtResistantTime / 2.0F && attackTimer == 0){
					this.swingItem();
					this.attackTargetEntityWithCurrentItem(attack);
					attackTimer = this.getRNG().nextInt(3)+5;
				}
//			}
		}else if(distanceSquared < 400){
			if(selectBestBow()){
				ItemStack current = inventory.getCurrentItem();
				if(current != null){
					if(this.getItemInUse() != current){
//						System.out.println("USE BOW");
						this.setItemInUse(current, 20);//current.getMaxItemUseDuration());
					}
				}
			}
		}
		
	}
	
	public boolean selectBestBow(){
		if(!inventory.hasItem(Items.arrow)){
			return false;
		}
		int index = -1;
		float best = -1;
		for(int a = 0; a < 9; a++){
			if(inventory.getStackInSlot(a) != null){
				ItemStack stack = inventory.getStackInSlot(a);
				if(stack.getItemUseAction() == EnumAction.bow){
					float goodness = getBowGoodness(stack);
					if(goodness > best){
						best = goodness;
						index = a;
					}
				}
			}
		}
		if(index != -1){
			inventory.currentItem = index;
			return true;
		}
		return false;
	}
	
	public void shootBow(ItemStack bow){
		if(this.getAttackTarget() != null){
			
			int j1 = 20;
			
			float f = (float)j1 / 20.0F;
            f = (f * f + f * 2.0F) / 3.0F;

            if ((double)f < 0.1D)
            {
                return;
            }

            if (f > 1.0F)
            {
                f = 1.0F;
            }
            
//            if(true){
//            	return;
//            }
			
//            SmartArrow entityarrow = new SmartArrow(this.worldObj, this, this.getAttackTarget(), 2.0f);
            float temp = this.rotationYaw;
            this.rotationYaw = this.rotationYawHead;
            EntityArrow entityarrow = new EntityArrow(this.worldObj, this, f * 2.0F);
            entityarrow.canBePickedUp = 1;
            this.rotationYaw = temp;
            
//			float temp = this.rotationYaw;
//            this.rotationYaw = this.rotationYawHead;
//            EntityArrow entityarrow = new EntityArrow(this.worldObj, this, getAttackTarget(), 1.6F, 0.0f);
			
			
	        int i = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, this.getHeldItem());
	        int j = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, this.getHeldItem());
	        entityarrow.setDamage(f);

	        if (i > 0)
	        {
	            entityarrow.setDamage(entityarrow.getDamage() + (double)i * 0.5D + 0.5D);
	        }

	        if (j > 0)
	        {
	            entityarrow.setKnockbackStrength(j);
	        }

	        if (EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, this.getHeldItem()) > 0)
	        {
	            entityarrow.setFire(100);
	        }

	        this.playSound("random.bow", 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
	        this.worldObj.spawnEntityInWorld(entityarrow);
			this.inventory.consumeInventoryItem(Items.arrow);
		}
	}
	
	public void selectBestDamageItem(){
		int index = -1;
		float best = -1;
		for(int a = 0; a < 9; a++){
			if(inventory.getStackInSlot(a) != null){
				ItemStack stack = inventory.getStackInSlot(a);
				float goodness = getWeaponGoodness(stack);
				if(goodness > best){
					best = goodness;
					index = a;
				}
				
			}
		}
		if(index != -1){
			inventory.currentItem = index;
		}
	}
	
	public float getBowGoodness(ItemStack stack){
		float f1 = 0.0f;
		f1 += 4*EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
        f1 += 3*EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        f1 += 2*EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        f1 += 2*EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, stack);
        f1 += 1.5f*EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack);
        f1 += 1.5f*EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);
        return f1;
	}
	
	public float getWeaponGoodness(ItemStack stack){
		Multimap map = stack.getAttributeModifiers();
		Iterator iterator = map.entries().iterator();
		float f = 0;
        while (iterator.hasNext())
        {
            Entry entry = (Entry)iterator.next();
            if(((String)entry.getKey()).equals(SharedMonsterAttributes.attackDamage.getAttributeUnlocalizedName())){
            	f = (float)((AttributeModifier)entry.getValue()).getAmount();
            }
        }
//        System.out.println(f);
        int i = 0;
        float f1 = 0.0F;
        
        
        f1 += 4*EnchantmentHelper.getEnchantmentLevel(Enchantment.sharpness.effectId, stack);
        f1 += 3*EnchantmentHelper.getEnchantmentLevel(Enchantment.fireAspect.effectId, stack);
        f1 += 2*EnchantmentHelper.getEnchantmentLevel(Enchantment.baneOfArthropods.effectId, stack);
        f1 += 2*EnchantmentHelper.getEnchantmentLevel(Enchantment.smite.effectId, stack);
        f1 += 1.5f*EnchantmentHelper.getEnchantmentLevel(Enchantment.looting.effectId, stack);
        f1 += 1.5f*EnchantmentHelper.getEnchantmentLevel(Enchantment.unbreaking.effectId, stack);


        if (this.isSprinting())
        {
            ++f1;
        }

        if (f > 0.0F || f1 > 0.0F)
        {
            boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null;

            if (flag && f > 0.0F)
            {
                f *= 1.5F;
            }

            f += f1;
        }
        return f;
	}
	
	public void attackTargetEntityWithCurrentItem(Entity p_71059_1_)
    {
        ItemStack stack = inventory.getCurrentItem();
        if (stack != null && stack.getItem().onLeftClickEntity(stack, this.getPlayerInterface(), p_71059_1_))
        {
            return;
        }
        if (p_71059_1_.canAttackWithItem())
        {
            if (!p_71059_1_.hitByEntity(this))
            {
                float f = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
                int i = 0;
                float f1 = 0.0F;

                if (p_71059_1_ instanceof EntityLivingBase)
                {
                    f1 = EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)p_71059_1_);
                    i += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)p_71059_1_);
                }

                if (this.isSprinting())
                {
                    ++i;
                }

                if (f > 0.0F || f1 > 0.0F)
                {
                    boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.isOnLadder() && !this.isInWater() && !this.isPotionActive(Potion.blindness) && this.ridingEntity == null && p_71059_1_ instanceof EntityLivingBase;

                    if (flag && f > 0.0F)
                    {
                        f *= 1.5F;
                    }

                    f += f1;
                    boolean flag1 = false;
                    int j = EnchantmentHelper.getFireAspectModifier(this);

                    if (p_71059_1_ instanceof EntityLivingBase && j > 0 && !p_71059_1_.isBurning())
                    {
                        flag1 = true;
                        p_71059_1_.setFire(1);
                    }

                    boolean flag2 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(this.getPlayerInterface()), f);

                    if (flag2)
                    {
                        if (i > 0)
                        {
                            p_71059_1_.addVelocity((double)(-MathHelper.sin(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYaw * (float)Math.PI / 180.0F) * (float)i * 0.5F));
                            this.motionX *= 0.6D;
                            this.motionZ *= 0.6D;
                            this.setSprinting(false);
                        }

                        if (flag)
                        {
                            this.onCriticalHit(p_71059_1_);
                        }

                        if (f1 > 0.0F)
                        {
                            this.onEnchantmentCritical(p_71059_1_);
                        }

//                        if (f >= 18.0F)
//                        {
//                            this.triggerAchievement(AchievementList.overkill);
//                        }

                        this.setLastAttacker(p_71059_1_);

                        if (p_71059_1_ instanceof EntityLivingBase)
                        {
                            EnchantmentHelper.func_151384_a((EntityLivingBase)p_71059_1_, this);
                        }

                        EnchantmentHelper.func_151385_b(this, p_71059_1_);
                        ItemStack itemstack = this.inventory.getCurrentItem();
                        Object object = p_71059_1_;

                        if (p_71059_1_ instanceof EntityDragonPart)
                        {
                            IEntityMultiPart ientitymultipart = ((EntityDragonPart)p_71059_1_).entityDragonObj;

                            if (ientitymultipart != null && ientitymultipart instanceof EntityLivingBase)
                            {
                                object = (EntityLivingBase)ientitymultipart;
                            }
                        }

                        if (itemstack != null && object instanceof EntityLivingBase)
                        {
                            itemstack.hitEntity((EntityLivingBase)object, this.getPlayerInterface());

                            if (itemstack.stackSize <= 0)
                            {
                                this.destroyCurrentEquippedItem();
                            }
                        }

                        if (p_71059_1_ instanceof EntityLivingBase)
                        {

                            if (j > 0)
                            {
                                p_71059_1_.setFire(j * 4);
                            }
                        }

                        this.foodStats.addExhaustion(0.3F);
                    }
                    else if (flag1)
                    {
                        p_71059_1_.extinguish();
                    }
                }
            }
        }
    }
	
	/**
     * Returns the currently being used item by the player.
     */
    public ItemStack getCurrentEquippedItem()
    {
        return this.inventory.getCurrentItem();
    }

    /**
     * Destroys the currently equipped item from the player's inventory.
     */
    public void destroyCurrentEquippedItem()
    {
        this.inventory.setInventorySlotContents(this.inventory.currentItem, (ItemStack)null);
    }

    /**
     * Called when the player performs a critical hit on the Entity. Args: entity that was hit critically
     */
    public void onCriticalHit(Entity p_71009_1_) {}

    public void onEnchantmentCritical(Entity p_71047_1_) {}
    
    
    
    @Override
	public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) {
		if(damageSource instanceof EntityDamageSource){
			EntityDamageSource entityDamageSource = (EntityDamageSource)damageSource;
			
			Entity damager = entityDamageSource.getEntity();
			
			if(damager instanceof EntityPlayer)
			{
				if(this.canUseThisEntity((EntityPlayer)damager))
				{
					return super.attackEntityFrom(damageSource, damageAmount);
				}
			}
			if(damager instanceof EntityLivingBase){
				if(this.canAttackEntity((EntityLivingBase)damager)){
					this.setAttackTarget((EntityLivingBase)damager);
					this.setPath(this.getNavigator().getPathToEntityLiving(damager));
				}
			}
			
			
		}
		return super.attackEntityFrom(damageSource, damageAmount);
	}

	/**
     * An array listing the entities which have been modified so they attack this clone.
     * As they are removed from the world and killed etc, they are removed from this list.
     */
    private ArrayList<EntityLiving> modifiedAttackEntities = new ArrayList<EntityLiving>();
    
    /**
     * Makes other mobs attack this clone. 
     * Basically just adds an AI task to the surrounding hostile mobs to make them think the clone is the same as a player
     */
    public void makeOthersAttackMe(){
    	if(this.ticksExisted % 20 == 0)
    	{
    		
    		for(int a = 0; a < modifiedAttackEntities.size(); a++){
    			if(!modifiedAttackEntities.get(a).isEntityAlive()){
    				modifiedAttackEntities.remove(a--);
    			}
    		}

    		List mobs = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(50, 50, 50));
			for(int a = 0; a < mobs.size(); a++)
        	{
				Object mob = mobs.get(a);
				
        		if(mob != null && mob instanceof EntityLiving && !modifiedAttackEntities.contains(mob))
        		{
        			EntityLiving entity = (EntityLiving)mob;
        			
        			if(EntityAccessor.isAIEnabled(entity))
        			{
        				if(!(entity instanceof EntityCreature)){
        					continue;
        				}
        				
        				EntityCreature creature = (EntityCreature)entity;
        				
        				List targetTasks = entity.targetTasks.taskEntries;
            			
            			boolean cloneSet = false;
            			
            			boolean playerSet = false;
            			
            			for(int b = 0; b < targetTasks.size(); b++)
            			{
            				if(((EntityAITasks.EntityAITaskEntry)targetTasks.get(b)).action instanceof EntityAINearestAttackableTarget)
            				{
            					EntityAINearestAttackableTarget entityAI = (EntityAINearestAttackableTarget)((EntityAITasks.EntityAITaskEntry)targetTasks.get(b)).action;
            					Class targetClass = Reflect.getFieldValueAndCast(Reflect.EntityAINearestAttackableTarget_targetClass, entityAI, Class.class);
            					
            					if(targetClass != null)
            					{
            						if(EntityClone.class.isAssignableFrom(targetClass))
            						{
            							cloneSet = true;
            							break;
            						}
            						else if(EntityPlayer.class.isAssignableFrom(targetClass))
            						{
            							playerSet = true;
            						}
            					}
            				}
            			}
            			//If the mob attacks players, but not clones. Then let's make the mob attack clones too!
            			if(!cloneSet && playerSet)
            			{
            				entity.tasks.addTask(20, new EntityAIAttackOnCollide(creature, EntityClone.class, 1.0D, false));
            				entity.targetTasks.addTask(20, new EntityAINearestAttackableTarget(creature, EntityClone.class, 0, true));
            				
            				this.modifiedAttackEntities.add(entity);
            			}
        			}
        			else
        			{
        				//Well. I tried :P
        				if(IMob.class.isAssignableFrom(entity.getClass()))
        				{
        					if(entity instanceof EntityCreature){
        						((EntityCreature)entity).setAttackTarget(this);
        					}
        					entity.setAttackTarget(this);
        					entity.getNavigator().setPath(entity.getNavigator().getPathToEntityLiving(this), 1.0d);
        				}
        			}
        		}
        	}
    	}
    }

	//==================================================================================================================
	//TODO CloneOptions Updates
	//==================================================================================================================
	public int getCloneOptions(){
		return dataWatcher.getWatchableObjectInt(ID_OPTIONS);
	}

	
	
	
	//==================================================================================================================
	//TODO EntityPlayerInterface
	//==================================================================================================================
	
	EntityPlayer playerInterface = null;
	
	public EntityPlayer getPlayerInterface(){
		if(playerInterface == null){
			playerInterface = new FakePlayer(this);
		}
		return playerInterface;
	}
	
	//==================================================================================================================
	//TODO Eating
	//==================================================================================================================
	
	private int criticalEatingPoint = 5;
	
	public boolean isEatingFood(){
		if(this.getFlag(4)){
			ItemStack currentItem = this.getHeldItem();
			if(currentItem != null && currentItem.getItem() instanceof ItemFood){
				return true;
			}
		}
		return false;
	}
	
	public void forceEatFood(int inventorySlot){
		
		ItemStack foodStack = this.inventory.mainInventory[inventorySlot];
		
		if(foodStack == null)
		{
			return;
		}
		
		Item item = foodStack.getItem();
		
		if(item == null)
		{
			return;
		}
		
		EnumAction itemUse = item.getItemUseAction(foodStack);
		
		if(itemUse == EnumAction.eat || itemUse == EnumAction.drink || item instanceof ItemFood)
		{
			
			int slot = this.inventory.putStackOnHotbar(inventorySlot);
			
			this.inventory.currentItem = slot;
			
			this.setItemInUse(foodStack, item.getMaxItemUseDuration(foodStack));
			
		}
	}
	
	public void checkHunger(){
		if(!this.getFlag(4) && this.foodStats.getFoodLevel() < 20 && (this.getAttackTarget() == null || this.foodStats.getFoodLevel() < criticalEatingPoint)){
			ItemStack foodStack;
			ItemFood food;
			for(int a = 0; a < this.inventory.mainInventory.length; a++){
				if(this.inventory.mainInventory[a] != null && this.inventory.mainInventory[a].getItem() instanceof ItemFood){
					foodStack = this.inventory.mainInventory[a];
					food = (ItemFood)this.inventory.mainInventory[a].getItem();
					if(foodStack.stackSize > 0){
						forceEatFood(a);
						return;
					}
				}
			}
		}
	}
	
	//==================================================================================================================
	//TODO Save/Load
	//==================================================================================================================
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt)
	{
		super.writeEntityToNBT(nbt);
		nbt.setString("Name_U", this.nameUnedited);
		nbt.setString("Owner", ownerName);
		nbt.setInteger("Team", team.teamID);
		nbt.setTag("Inventory", this.inventory.writeToNBT(new NBTTagList()));
		nbt.setFloat("scale", this.preciseScale);
		this.foodStats.writeNBT(nbt);
		this.options.writeNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
		super.readEntityFromNBT(nbt);
		this.setName(nbt.getString("Name_U"));
//		System.out.println("Set name: " + this.nameUnedited);
		this.setOwner(nbt.getString("Owner"));
		this.team = PlayerTeam.getByID(nbt.getInteger("Team"));
		this.inventory.readFromNBT(nbt.getTagList("Inventory", 10));
		
		float scale = nbt.getFloat("scale");
		if(scale != 0){
			this.setScale(scale);
		}
		
		this.foodStats.readNBT(nbt);
		this.options.readFromNBT(nbt);
	}
	
	public Set<EntityPlayer> getWatchingEntities(){
		if(this.worldObj instanceof WorldServer){
			return ((WorldServer)this.worldObj).getEntityTracker().getTrackingPlayers(this);
		}
		return null;
	}
	
	//==================================================================================================================
	//TODO Owner
	//==================================================================================================================
	String ownerName = "";
	

	public boolean hasOwner()
	{
		return ownerName != null && ownerName.length() > 0;
	}
	public boolean canUseThisEntity(EntityPlayer player)
	{
		return canUseThisEntityUsername(player.getCommandSenderName()) || player.capabilities.isCreativeMode;
	}
	public boolean canUseThisEntityUsername(String username)
	{
		return (!hasOwner()||!CloneCraft.INSTANCE.config.areOwnersEnabled()?true:(ownerName.equals(username)));
	}
	public void setOwner(String ownerUsername)
	{
		ownerName = ownerUsername;
	}
	public EntityPlayer getOwner()
	{
		if(hasOwner())
		{
			Object player;
			for(int a = 0; a < worldObj.playerEntities.size(); a++)
			{
				player = (EntityPlayer)worldObj.playerEntities.get(a);
				if(player != null && player instanceof EntityPlayer && ((EntityPlayer)player).getCommandSenderName().equals(ownerName))
				{
					return (EntityPlayer)player;
				}
			}
		}
		return null;
	}
	public String getOwnerName()
	{
		return ownerName;
	}
	//==================================================================================================================
	//TODO Items & Experience
	//==================================================================================================================
	private void pickupNearbyItems() {
		if(!this.isEntityAlive())
		{
			return;
		}
		List list = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.boundingBox.expand(1D, 1.5D, 1D));
		EntityItem eItem;
		for(int a = 0; a < list.size(); a++)
		{
			eItem = (EntityItem)list.get(a);
			if(eItem.delayBeforeCanPickup == 0)
			{
				pickupItem(eItem.getEntityItem());
				if(eItem.getEntityItem().stackSize == 0)
				{
					worldObj.removeEntity(eItem);
				}
			}
		}
	}
	
	public void pickupItem(ItemStack stack){
		if(stack.stackSize > 0 && stack.getItem() instanceof ItemArmor)
		{
			ItemArmor item = (ItemArmor)stack.getItem();
			int armorSlot = 3-item.armorType;
			ItemStack slotArmor = inventory.armorItemInSlot(armorSlot);
			if(slotArmor == null)
			{
				inventory.setArmour(armorSlot, stack.copy());
				stack.stackSize = 0;
				worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				return;
			}
			else if(slotArmor.getItem() instanceof ItemArmor && isBetterArmorType(stack, slotArmor, item, (ItemArmor)slotArmor.getItem()) && inventory.canFullyFit(slotArmor))
			{
				inventory.tryFitInInventory(slotArmor);
				inventory.setArmour(armorSlot, stack.copy());
				stack.stackSize = 0;
				worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				return;
			}
		}
		int removed = inventory.tryFitInInventory(stack);
		if(removed > 0)
		{
			worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
	}

	public boolean isBetterArmorType(ItemStack stackBetterDur, ItemStack thanMe, ItemArmor isBetterThan, ItemArmor me){
		if(getArmourDamageReduction(stackBetterDur, isBetterThan) <= getArmourDamageReduction(thanMe, me))
		{
//			System.out.println("Check2: " + (isBetterThan != me ) + ", (" + stackBetterDur.getItemDamage() + ", " + thanMe.getItemDamage() + ")");
			if(isBetterThan != me || stackBetterDur.getItemDamage() >= thanMe.getItemDamage())
			{
				return false;
			}
		}
		return true;
	}
	
	

	@Override
	protected void damageArmor(float dam) {
		this.inventory.damageArmour(dam);
	}



	public float getArmourDamageReduction(ItemStack stack, ItemArmor armour){
		float reduction = armour.getArmorMaterial().getDamageReductionAmount(armour.armorType);
		reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.protection.effectId, stack)*1.5;
		reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.featherFalling.effectId, stack);
		reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.blastProtection.effectId, stack);
		reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.fireProtection.effectId, stack);
		reduction += EnchantmentHelper.getEnchantmentLevel(Enchantment.projectileProtection.effectId, stack);
		return reduction;
	}

	
	
	//==================================================================================================================
	//TODO Inventory
	//==================================================================================================================
	
	@Override
	public ItemStack getEquipmentInSlot(int slot)
	{
		if(slot == 0){
			return getHeldItem();
		}
		return inventory.armorItemInSlot(slot-1);
	}

	@Override
	public void setCurrentItemOrArmor(int slot, ItemStack stack)
	{
		if(slot == 0){
			inventory.setInventorySlotContents(this.inventory.currentItem, stack);
		}else{
			inventory.setArmour(slot-1, stack);
		}
	}

	@Override
	public ItemStack[] getLastActiveItems()
    {
        return this.inventory.armorInventory;
    }

	@Override
	public ItemStack getHeldItem()
	{
		return inventory.getCurrentItem();//inventory.getStackInSlot(this.inventory.currentItem);
	}
	
	public ItemStack getOfferedItem(){
		return this.getShareAI().getOfferedItem();
	}

	//==================================================================================================================
	//TODO Clone Name
	//==================================================================================================================
	
	public ResourceLocation defaultResource = new ResourceLocation("textures/entity/steve.png");

	public ResourceLocation currentResource = null;

	public String nameUnedited = "Steve";
	
	public String name = "Steve";

	public String getTranslatedEntityName(){
		return name;
	}

	public String getFullEditedName(){
		return nameUnedited;
	}

	public String getName(){
		return name;
	}

	public void setName(String uneditedName){
//		System.out.println("Set name: " + uneditedName);
		if(uneditedName == null || uneditedName.length() == 0){
			uneditedName = "Steve";
		}
		if(uneditedName.equals(nameUnedited)){
			return;
		}
		nameUnedited = uneditedName;
		if(!nameUnedited.startsWith("*")){
			if(worldObj.isRemote){
				updateSkin(nameUnedited);
			}
			name = nameUnedited;
		}else{
			String nameEdited = uneditedName.substring(1).replace("\\", "/");
			if(worldObj.isRemote){
				updateLocalSkin(nameEdited);
			}
			if(nameEdited.contains("/")){
				nameEdited = nameEdited.substring(nameEdited.lastIndexOf("/")+1);
			}
			if(nameEdited.contains(".")){
				nameEdited = nameEdited.substring(0, nameEdited.lastIndexOf("."));
			}
			if(nameEdited.length() > 0){
				nameEdited = nameEdited.substring(0,  1).toUpperCase() + nameEdited.substring(1);
			}
			this.name = nameEdited;
		}
	}


	@SideOnly(value = Side.CLIENT)
	public void updateLocalSkin(String editedName){
		//Checks to make sure the local skin actually exists. If it doesn't it sets the currentResource to the default (steve) resource

		currentResource = new ResourceLocation("textures/" + editedName);
		SimpleTexture tex = new SimpleTexture(currentResource);
		try{
			tex.loadTexture(Minecraft.getMinecraft().getResourceManager());
		}catch(Exception e){
			currentResource = defaultResource;
		}
	}

	@SideOnly(value = Side.CLIENT)
	public void updateSkin(String username){
		ResourceLocation resource = new ResourceLocation("skins/" + username);
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		ITextureObject object = texturemanager.getTexture(resource);
		if(object == null || !(object instanceof ThreadDownloadImageData)){
			object = new ThreadDownloadImageData(null, "http://skins.minecraft.net/MinecraftSkins/" + username + ".png", defaultResource, new ImageBufferDownload());
			texturemanager.loadTexture(resource, object);
		}
		currentResource = resource;
	}



	//Rendering Stuff
	
	@Override
	public String getCommandSenderName()
	{
		return name;
	}
	
	@SideOnly(value = Side.CLIENT)
	public ResourceLocation getTexture()
	{
		if(currentResource != null){
			return currentResource;
		}
		return defaultResource;
	}


	//==================================================================================================================
	//TODO Item Using
	//==================================================================================================================
	
	boolean isItemInUse = false;
	
	public void updateUsingItem(){
		if (this.itemInUse != null)
        {
            ItemStack itemstack = this.getHeldItem();

            if (itemstack == this.itemInUse)
            {
//            	System.out.println("Update " + itemInUseCount);
                if (itemInUseCount <= 0)
                {
                    this.onItemUseFinish();
                }
                else
                {
                    itemInUse.getItem().onUsingTick(itemInUse, getPlayerInterface(), itemInUseCount);
                    if (this.itemInUseCount <= 25 && this.itemInUseCount % 4 == 0)
                    {
                        this.updateItemUse(itemstack, 5);
                    }
    
                    if (--this.itemInUseCount == 0 && !this.worldObj.isRemote)
                    {
                        this.onItemUseFinish();
                    }
                }
            }
            else
            {
                this.clearItemInUse();
            }
        }
		
		if(worldObj.isRemote){
			if (this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != this.getItemInUse())
	        {
	            ItemStack itemstack = this.inventory.mainInventory[this.inventory.currentItem];
	            int count;
	            if(itemstack.getItemUseAction() == EnumAction.bow){
	            	count = 20;
	            }else{
	            	count = itemstack.getMaxItemUseDuration();
	            }
	            this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], count);
	            this.isItemInUse = true;
	        }
	        else if (this.isItemInUse && !this.isEating())
	        {
	            this.clearItemInUse();
	            this.isItemInUse = false;
	        }
		}
		
	}
	
	/**
     * Gets the Icon Index of the item currently held
     */
    @SideOnly(Side.CLIENT)
    public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_)
    {
//    	System.out.println("Get");
        IIcon iicon = super.getItemIcon(p_70620_1_, p_70620_2_);

        if (p_70620_1_.getItem() == Items.fishing_rod && this.fishEntity != null)
        {
            iicon = Items.fishing_rod.func_94597_g();
        }
        else
        {
            if (this.itemInUse != null && p_70620_1_.getItem() == Items.bow)
            {
                int j = 20-this.itemInUseCount;
                if (j >= 14)
                {
                    return Items.bow.getItemIconForUseDuration(2);
                }

                if (j > 7)
                {
                    return Items.bow.getItemIconForUseDuration(1);
                }

                if (j > 0)
                {
                    return Items.bow.getItemIconForUseDuration(0);
                }
            }
            iicon = p_70620_1_.getItem().getIcon(p_70620_1_, p_70620_2_, this.getPlayerInterface(), itemInUse, itemInUseCount);
        }

        return iicon;
    }
	
	
	/** This is the item that is in use when the player is holding down the useItemButton (e.g., bow, food, sword) */
    private ItemStack itemInUse;
    /** This field starts off equal to getMaxItemUseDuration and is decremented on each tick */
    private int itemInUseCount;
    
    /**
     * sets the itemInUse when the use item button is clicked. Args: itemstack, int maxItemUseDuration
     */
    public void setItemInUse(ItemStack stack, int maxDuration)
    {
        if (stack != this.itemInUse)
        {
            if (maxDuration <= 0) return;
            this.itemInUse = stack;
            this.itemInUseCount = maxDuration;

            if (!this.worldObj.isRemote)
            {
                this.setEating(true);
            }
        }
    }
	/**
     * returns the ItemStack containing the itemInUse
     */
    @SideOnly(Side.CLIENT)
    public ItemStack getItemInUse()
    {
        return this.itemInUse;
    }
    /**
     * Returns the item in use count
     */
    @SideOnly(Side.CLIENT)
    public int getItemInUseCount()
    {
        return this.itemInUseCount;
    }
    /**
     * Checks if the entity is currently using an item (e.g., bow, food, sword) by holding down the useItemButton
     */
    public boolean isUsingItem()
    {
        return this.itemInUse != null;
    }
    /**
     * gets the duration for how long the current itemInUse has been in use
     */
    @SideOnly(Side.CLIENT)
    public int getItemInUseDuration()
    {
        return this.isUsingItem() ? this.itemInUse.getMaxItemUseDuration() - this.itemInUseCount : 0;
    }

    public void stopUsingItem()
    {
        this.clearItemInUse();
    }

    public void clearItemInUse()
    {
        this.itemInUse = null;
        this.itemInUseCount = 0;

        if (!this.worldObj.isRemote)
        {
            this.setEating(false);
        }
    }
    
    
    /**
     * Plays sounds and makes particles for item in use state
     */
    protected void updateItemUse(ItemStack p_71010_1_, int p_71010_2_)
    {
        if (p_71010_1_.getItemUseAction() == EnumAction.drink)
        {
            this.playSound("random.drink", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (p_71010_1_.getItemUseAction() == EnumAction.eat)
        {
            for (int j = 0; j < p_71010_2_; ++j)
            {
                Vec3 vec3 = Vec3.createVectorHelper(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
                vec3.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
                vec3.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
                Vec3 vec31 = Vec3.createVectorHelper(((double)this.rand.nextFloat() - 0.5D) * 0.3D, (double)(-this.rand.nextFloat()) * 0.6D - 0.3D, 0.6D);
                vec31.rotateAroundX(-this.rotationPitch * (float)Math.PI / 180.0F);
                vec31.rotateAroundY(-this.rotationYaw * (float)Math.PI / 180.0F);
                vec31 = vec31.addVector(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
                String s = "iconcrack_" + Item.getIdFromItem(p_71010_1_.getItem());

                if (p_71010_1_.getHasSubtypes())
                {
                    s = s + "_" + p_71010_1_.getItemDamage();
                }

                this.worldObj.spawnParticle(s, vec31.xCoord, vec31.yCoord, vec31.zCoord, vec3.xCoord, vec3.yCoord + 0.05D, vec3.zCoord);
            }

            this.playSound("random.eat", 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
        }

    }
    
    

    @Override
	protected boolean canDespawn() {
    	return false;
	}

	/**
     * Used for when item use count runs out, ie: eating completed
     */
    protected void onItemUseFinish()
    {
        if (this.itemInUse != null)
        {
            this.updateItemUse(this.itemInUse, 16);
            int i = this.itemInUse.stackSize;
            ItemStack itemstack = this.itemInUse.onFoodEaten(this.worldObj, getPlayerInterface());

            if (itemstack != this.itemInUse || itemstack != null && itemstack.stackSize != i)
            {
                this.inventory.mainInventory[this.inventory.currentItem] = itemstack;

                if (itemstack != null && itemstack.stackSize == 0)
                {
                    this.inventory.mainInventory[this.inventory.currentItem] = null;
                }
            }
            if(!worldObj.isRemote && this.itemInUse.getItemUseAction() == EnumAction.bow){
            	shootBow(this.itemInUse);
            }
            this.clearItemInUse();
        }
    }
    
    //==================================================================================================================
    //TODO Experience
    //==================================================================================================================
    
	public void transferXP(EntityPlayer player) {
		player.addExperience(experienceTotal);
		experience = 0;
		experienceLevel = 0;
		experienceTotal = 0;
	}
	//Experience
	
		int xpCooldown = 0;
		
		public void updateExperience(){
			if(xpCooldown > 0){
				xpCooldown--;
			}
			List l = worldObj.getEntitiesWithinAABB(EntityXPOrb.class, boundingBox.expand(6, 2, 6));
			for(int a = 0; a < l.size(); a++){
				Object o = l.get(a);
				if(o instanceof EntityXPOrb){
					EntityXPOrb exp = (EntityXPOrb)o;
					if(isEntityAlive() && xpCooldown < 1 && getDistanceSqToEntityIncEye(exp) < 1 && !worldObj.isRemote){
						int value = exp.getXpValue();
						xpCooldown = 2;
						
						playNextTing();
						
//						playSound("random.orb", 0.1F, 0.5F * ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.8F));
						addExperience(value);
						exp.setDead();
						worldObj.removeEntity(exp);
					}else if(!dead){
						double var3 = (posX - exp.posX) / 8;
						double var5 = (posY + (double)getEyeHeight() - exp.posY) / 8;
						double var7 = (posZ - exp.posZ) / 8;
						double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
						double var11 = 1.0D - var9;
						if (var11 > 0.0D)
						{
							var11 *= var11;
							exp.motionX += var3 / var9 * var11 * 0.1D;
							exp.motionY += var5 / var9 * var11 * 0.1D;
							exp.motionZ += var7 / var9 * var11 * 0.1D;
						}
						exp.moveEntity(exp.motionX, exp.motionY, exp.motionZ);
					}
				}
			}
		}
		public double getDistanceSqToEntityIncEye(Entity par1Entity)
		{
			double d0 = this.posX - par1Entity.posX;
			double d1 = (this.posY+getEyeHeight()) - par1Entity.posY;
			double d2 = this.posZ - par1Entity.posZ;
			return d0 * d0 + d1 * d1 + d2 * d2;
		}
		/*	private void increaseLevel() {
			++this.experienceLevel;
		}*/
		public void addExperience(int par1) {
			int j = Integer.MAX_VALUE - this.experienceTotal;

			if (par1 > j)
			{
				par1 = j;
			}

			this.experience += (float)par1 / (float)this.xpBarCap();

			for (this.experienceTotal += par1; this.experience >= 1.0F; this.experience /= (float)this.xpBarCap())
			{
				this.experience = (this.experience - 1.0F) * (float)this.xpBarCap();
				this.addExperienceLevel(1);
			}
		}

		public void addExperienceLevel(int par1)
		{
			this.experienceLevel += par1;

			if (this.experienceLevel < 0)
			{
				this.experienceLevel = 0;
				this.experience = 0.0F;
				this.experienceTotal = 0;
			}

			if (par1 > 0 && this.experienceLevel % 5 == 0 /*&& (float)this.field_82249_h < (float)this.ticksExisted - 100.0F*/)
			{
				float f = this.experienceLevel > 30 ? 1.0F : (float)this.experienceLevel / 30.0F;
				this.worldObj.playSoundAtEntity(this, "random.levelup", f * 0.75F, 1.0F);
				//            this.field_82249_h = this.ticksExisted;
			}
		}

		public int xpBarCap() {
			return this.experienceLevel >= 30 ? 62 + (this.experienceLevel - 30) * 7
					: (this.experienceLevel >= 15 ? 17 + (this.experienceLevel - 15) * 3
							: 17);
		}
		
		private void dropAllXP() {
			if(experienceTotal > 0){
				Random r = new Random();
				int xp = experienceTotal;
				if(xp > 85){
					xp = 85;
				}
				if(xp < 4){
					EntityXPOrb xpOrb = new EntityXPOrb(worldObj, posX, posY+.5, posZ, experienceTotal);
					xpOrb.motionX = (r.nextFloat()*2-1)/5;
					xpOrb.motionY = (r.nextFloat()*2-1)/5;
					xpOrb.motionZ = (r.nextFloat()*2-1)/5;
					xpOrb.moveEntity(xpOrb.motionX, xpOrb.motionY, xpOrb.motionZ);
					worldObj.spawnEntityInWorld(xpOrb);
				}else{
					int[] split = splitXP(xp);
					for(int a = 0; a < split.length; a++){
						EntityXPOrb xpOrb = new EntityXPOrb(worldObj, posX, posY+.5, posZ, split[a]);
						xpOrb.motionX = (r.nextFloat()*2-1)/5;
						xpOrb.motionY = (r.nextFloat()*2-1)/5;
						xpOrb.motionZ = (r.nextFloat()*2-1)/5;
						xpOrb.moveEntity(xpOrb.motionX, xpOrb.motionY, xpOrb.motionZ);
						worldObj.spawnEntityInWorld(xpOrb);
					}
				}
			}
		}

		public int[] splitXP(int xp){
			int[] ret = new int[4];
			int size = xp/4;
			for(int a = 0; a < ret.length; a++){
				ret[a] = size;
			}
			ret[3] = xp-(size*4) + size;
			return ret;
		}
	
	//==================================================================================================================
    //TODO Death & Animation
    //==================================================================================================================
	
	public void commitSuicide() {
		this.setHealth(0);
		this.playSound(this.getDeathSound(), this.getSoundVolume(), this.getSoundPitch());
		this.onDeath(DamageSource.outOfWorld);
	}
	
	@SideOnly(value = Side.CLIENT)
	public void spawnExplosionParticles(){
		spawnExplosionParticles(posX, posY, posZ, height, worldObj);
	}

	@SideOnly(value = Side.CLIENT)
	public void spawnExplosionParticles(final double posX, final double posY, final double posZ, final double height, final World theWorld){
		theWorld.playSound(posX, posY, posZ, "random.explode", .5F, (1.0F + (theWorld.rand.nextFloat() - theWorld.rand.nextFloat()) * 0.1F) * 0.2F, false);
		Random r = new Random();
		for(int a = 0; a < 200; a++){
			double done = a/200.0;
			double yOffset = done*height*2 - height/2 ;
			double x = Math.sin(done * Math.PI * 10);
			double z = Math.cos(done * Math.PI * 10);
			theWorld.spawnParticle("reddust", posX + x, posY + yOffset, posZ + z, r.nextFloat()*.4+.2, r.nextFloat()*.1, r.nextFloat()*.1);
		}


		for(int a = 0; a < 200; a++){
			double done = ((double)a) / 200;

			double rads = (Math.PI*2) * done;

			double x = Math.sin(rads);
			double z = Math.cos(rads);
			CloneCraftWorld.spawnParticleInRange(new EntityExplodeCollapseFX(theWorld, posX, posY + height/2, posZ, x*25, r.nextFloat()*2 - 1, z*25), 40);
		}

		new Thread(){
			public boolean isGamePaused(){
				return Reflect.getFieldValueAndCast(Reflect.Minecraft_isGamePaused, Minecraft.getMinecraft(), Boolean.class);
			}
			
			List smokeFXLayer;
			
			public boolean canSpawnParticle(int amount){
				if(smokeFXLayer != null){
					return smokeFXLayer.size() + amount <= 3750;
				}
				return false;
			}

			public void run(){
				Minecraft mc = Minecraft.getMinecraft();
				List[] fxLayers = Reflect.getFieldValueAndCast(Reflect.EffectRenderer_fxLayers, mc.effectRenderer, List[].class);
				if(fxLayers != null && fxLayers.length > 0){
					smokeFXLayer = fxLayers[0];
				}
				long startTime = System.currentTimeMillis();
				for(int a = 0; a < 1000; a++){
					if(mc.theWorld == null){
						break;
					}
					double done = ((double)a) / 1000;

					double yoffset = Math.sin(done*Math.PI) * height * 10;

					double rads = (Math.PI*12) * done*4;

					double x = Math.sin(rads)/16.0;
					double z = Math.cos(rads)/16.0;
					if(canSpawnParticle(2)){
						CloneCraftWorld.spawnParticleIgnoreDistance(new EntitySmokeFX(theWorld, posX, posY + (height/2) +yoffset, posZ, x, 0, z));
						CloneCraftWorld.spawnParticleIgnoreDistance(new EntitySmokeFX(theWorld, posX, posY + (height/2) -yoffset, posZ, x, 0, z));
					}
					if(isGamePaused()){
						int maximum = 3600000;
						long sleepStartTime = System.currentTimeMillis();
						while(isGamePaused() && maximum-- > 0 && mc.theWorld != null){
							try{
								Thread.sleep(1);
							}catch(Exception e){
								e.printStackTrace();
							}
						}
						long sleepEndTime = System.currentTimeMillis();
						startTime += sleepEndTime - sleepStartTime;
					}else{
						long timeToGoTo = (a+1)*5 + startTime;
						long currentTime = System.currentTimeMillis();
						long timeToSleep = timeToGoTo - currentTime;
						if(timeToSleep > 0){
							try{
								Thread.sleep(timeToSleep);
							}catch(Exception e){
								e.printStackTrace();
							}
						}else if(timeToSleep > 5){
							int toSkip = (int)(timeToSleep / 5) - 1;
							a += toSkip;
						}
					}

				}


			}
		}.start();
	}

	private static double gd(Random r){
		return (r.nextDouble()*1.5d)-.75d;
	}
	
	//==================================================================================================================
	
	
	@Override
	public boolean canRenderContinue(Renderable r) {
		return this.isEntityAlive();
	}


	
	
	
	
	public final static int ID_OPTIONS = 12;


	
}
