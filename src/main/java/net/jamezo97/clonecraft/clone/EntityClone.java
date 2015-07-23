package net.jamezo97.clonecraft.clone;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import net.jamezo97.clonecraft.CCPostRender;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.GuiHandler;
import net.jamezo97.clonecraft.Reflect;
import net.jamezo97.clonecraft.build.EntityAIBuild;
import net.jamezo97.clonecraft.clone.ai.EntityAIAttackEnemies;
import net.jamezo97.clonecraft.clone.ai.EntityAICloneLookIdle;
import net.jamezo97.clonecraft.clone.ai.EntityAICloneWalkToItems;
import net.jamezo97.clonecraft.clone.ai.EntityAICloneWander;
import net.jamezo97.clonecraft.clone.ai.EntityAICommand;
import net.jamezo97.clonecraft.clone.ai.EntityAIFetchItemStack;
import net.jamezo97.clonecraft.clone.ai.EntityAIFollowCloneOwner;
import net.jamezo97.clonecraft.clone.ai.EntityAIOpenDoorClone;
import net.jamezo97.clonecraft.clone.ai.EntityAIReturnGuard;
import net.jamezo97.clonecraft.clone.ai.EntityAIShare;
import net.jamezo97.clonecraft.clone.ai.ImportantBlockRegistry;
import net.jamezo97.clonecraft.clone.ai.Notifier;
import net.jamezo97.clonecraft.clone.ai.block.DefaultBlockFinder;
import net.jamezo97.clonecraft.clone.ai.block.EntityAIMine;
import net.jamezo97.clonecraft.clone.mine.RayTrace;
import net.jamezo97.clonecraft.clone.sync.Syncer;
import net.jamezo97.clonecraft.command.Command;
import net.jamezo97.clonecraft.command.Commands;
import net.jamezo97.clonecraft.command.Interpretter;
import net.jamezo97.clonecraft.command.task.CommandTask;
import net.jamezo97.clonecraft.entity.EntityExplodeCollapseFX;
import net.jamezo97.clonecraft.musics.MusicBase;
import net.jamezo97.clonecraft.render.Renderable;
import net.jamezo97.clonecraft.render.RenderableManager;
import net.jamezo97.physics.Particle;
import net.jamezo97.physics.Spring;
import net.jamezo97.physics.Vector;
import net.jamezo97.util.SimpleList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import net.minecraft.entity.ai.EntityLookHelper;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.CloneCraftWorld;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeHooks;

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
	
	PlayerTeam team = PlayerTeam.Good;
	
	public InventoryClone inventory;
	Syncer watcher;
	CloneOptions options;
	
	Interpretter interpretter;
	
	float defaultScale = 1.0f;
	
	float lastScaleUpdate = 0.5f;
	float preciseScale = 0.5f;
	float maxScale = 1.25f;

	float defaultGrowthFactor = 0.000020833f; //Approximately 20 Minutes
	float maxGrowthFactor = 100f;
	float growthFactor = defaultGrowthFactor;
	float maxGrowthSpeed;
	
	long lastAccesed = 0;

	ImportantBlockRegistry iBlockReg;
	

	public EntityClone(World world)
	{
		super(world);
		
		boolean set = Reflect.setFieldValue(Reflect.EntityLiving_navigator, this, new ClonePathNavigate(this, world));
		
		if(!set)
		{
			System.err.println("Failed to replace Clone navigator with custom navigator. Paths may not be calculated nicely.");
		}
		
		inventory = new InventoryClone(this);
		watcher = new Syncer(this, 1);
		options = new CloneOptions(this);
		ticksExisted = -1;

		initAI();

		initSounds();
		postInit();
		
		if(!world.isRemote)
		{
			interpretter = new Interpretter(this);
			
			iBlockReg = new ImportantBlockRegistry(this);
			
			this.setName(options.female.get()?NameRegistry.getGirlName():NameRegistry.getBoyName());
		}
		else
		{
			initCustomRender();
		}
		
		ignoreFrustumCheck = true;
		
		maxGrowthSpeed = maxGrowthFactor / defaultGrowthFactor;
		
		maxScale = 1.25f;
		
		this.actualHeight = this.height = 0.9f;
		
		if(this.boundingBox != null)
		{
			this.boundingBox.maxY = this.boundingBox.minY + this.height;
		}
		
		this.getNavigator().setAvoidsWater(true);
		this.getNavigator().setBreakDoors(true);
		this.getNavigator().setCanSwim(true);
		this.getNavigator().setEnterDoors(true);
	}
	
	
	
	CloneExtraRender extraRender = null;
	
	@SideOnly(value = Side.CLIENT)
	public void initCustomRender()
	{
		CCPostRender.addRenderable(this, extraRender = new CloneExtraRender(this));
	}
	
	public Interpretter getInterpretter()
	{
		return this.interpretter;
	}
	
	public ImportantBlockRegistry getIBlockReg()
	{
		return iBlockReg;
	}
	
	public boolean isAIEnabled()
    {
        return true;
    }
	
	EntityAIMine aiBreakBlocks;
	
	EntityAIShare aiShareItems;
	
	EntityAICommand aiCommand;
	
	EntityAIBuild aiBuild;
	
	EntityAIFetchItemStack aiFetch;
	
	DefaultBlockFinder defaultBlockFinder;
	
	
	
	
	public void initAI()
	{
		
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIAttackEnemies(this));
		this.tasks.addTask(2, new EntityAIOpenDoorClone(this, true));
		this.tasks.addTask(3, new EntityAIFollowCloneOwner(this));
		
		this.tasks.addTask(4, aiCommand = new EntityAICommand(this));
		
		this.tasks.addTask(5, aiFetch = new EntityAIFetchItemStack(this));
		
		this.tasks.addTask(6, aiBreakBlocks = new EntityAIMine(this));
		this.tasks.addTask(7, aiBuild = new EntityAIBuild(this));
		this.tasks.addTask(8, new EntityAICloneWalkToItems(this));
		this.tasks.addTask(9, new EntityAIReturnGuard(this));
		
		this.tasks.addTask(18, aiShareItems = new EntityAIShare(this));
		this.tasks.addTask(19, new EntityAICloneLookIdle(this));
		this.tasks.addTask(20, new EntityAICloneWander(this, 1.0F));
		
		
		this.aiBreakBlocks.setBlockFinder(getDefaultBlockFinder());
	}
	
	public EntityAIFetchItemStack getFetchAI()
	{
		return this.aiFetch;
	}
	
	public EntityAIShare getShareAI()
	{
		return aiShareItems;
	}
	
	public DefaultBlockFinder getDefaultBlockFinder()
	{
		if(defaultBlockFinder == null)
		{
			defaultBlockFinder = new DefaultBlockFinder();
		}
		return defaultBlockFinder;
	}
	
	public EntityAIMine getBlockAI(){
		return aiBreakBlocks;
	}
	
	public EntityAICommand getCommandAI(){
		return aiCommand;
	}
	
	public EntityAIBuild getBuildAI()
	{
		return aiBuild;
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
	protected void entityInit()
	{
		super.entityInit();
	}
	
	public boolean isIndependent()
	{
		return this.getCTeam() == PlayerTeam.Rampant || this.getCTeam() == PlayerTeam.Evil;
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
	
	public static EntityClone renderFocusedClone = null;
	
	@Override
	protected boolean interact(EntityPlayer player)
	{
		if(!player.isSneaking() && this.canUseThisEntity(player))
		{
			if(!worldObj.isRemote)
			{
				if(!this.hasOwner() && CloneCraft.INSTANCE.config.areOwnersEnabled())
				{
					setOwner(player.getCommandSenderName());
					player.addChatComponentMessage(new ChatComponentText("You now own " + this.getName()).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GOLD)));
				}
			}
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

	int sneakCountdown = 0;
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		
		if(!worldObj.isRemote)
		{

			pickupNearbyItems();
			this.foodStats.onUpdate(this);
			checkHunger();
			
			if(this.isSprinting() && this.getNavigator().noPath())
			{
				this.setSprinting(false);
			}
			else if(this.getOptions().sprint.get() && !this.getNavigator().noPath())
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
			if(this.getAttackTarget() != null)
			{
				this.getLookHelper().setLookPositionWithEntity(this.getAttackTarget(), 10, this.getVerticalFaceSpeed());
			}
			
			this.getPlayerInterface().updateFrom(this);
			
			if(this.getAttackTarget() == null && this.getNavigator().noPath() && this.ticksExisted % 10 == 0 && this.rand.nextFloat() > 0.98f)
			{
				if(this.options.female.get())
				{
					this.worldObj.playSoundAtEntity(this, "CloneCraft:woman.idle", (this.rand.nextFloat() * 0.2f + 0.9f), (this.rand.nextFloat() * 0.3f + 1.4f));
				}
				else
				{
					this.worldObj.playSoundAtEntity(this, "CloneCraft:man.idle", (this.rand.nextFloat() * 0.2f + 0.9f), (this.rand.nextFloat() * 0.2f + 1.1f));
				}
			}
			
			if(this.options.smartMemory.get())
			{
				iBlockReg.onUpdate();
			}
			
			if(this.isIndependent())
			{
				if(this.getAttackTarget() != null)
				{
					this.options.sprint.set(true);
					this.setSprinting(true);
				}
				else
				{
					this.options.sprint.set(false);
				}
			}
		}
		else
		{
			
			if(stemCellInjectTime > 0)
			{
				stemCellInjectTime--;
			}
			
			updateBPhysics();
		}
		
		if(displayMessageCooldown > 0)
		{
			displayMessageCooldown--;
		}
		
		if(sneakCountdown == 0)
		{
			Block atHead = worldObj.getBlock((int)Math.floor(posX), (int)Math.floor(posY + actualHeight), (int)Math.floor(posZ));
			if(atHead != null && atHead.isNormalCube())
			{
				this.setSneaking(true);
			}
			else
			{
				this.setSneaking(false);
			}
		}
		else
		{
			sneakCountdown--;
		}
		
		if(this.playerInterface != null)
		{
			this.playerInterface.posX = this.posX;
			this.playerInterface.posY = this.posY;
			this.playerInterface.posZ = this.posZ;
		}
		
		if(this.isSneaking())
		{
			this.height = this.actualHeight * 0.88f;
			this.boundingBox.maxY = this.boundingBox.minY + this.height;
		}
		else
		{
			this.height = this.actualHeight;
			this.boundingBox.maxY = this.boundingBox.minY + this.height;
		}
		
		if(this.preciseScale == this.maxScale)
		{
			this.stepHeight = 1.0f;
		}
		else
		{
			this.stepHeight = 0.5f;
		}
		
		this.updateScale();
		this.updateExperience();
		this.updateUsingItem();
		this.updateArmSwingProgress();
		this.options.onTick();

		if(!worldObj.isRemote){/*long l1=System.nanoTime();*/watcher.tick();/*System.out.println((System.nanoTime()-l1) / 1000000.0f);*/}
	}
	
	
	public void openCloseChest(int x, int y, int z, boolean open)
	{
		TileEntity te = this.worldObj.getTileEntity(x, y, z);
		
		if(te instanceof TileEntityChest)
		{
			TileEntityChest tec = (TileEntityChest)te;
			if(tec.adjacentChestZNeg != null)
			{
				tec = tec.adjacentChestZNeg;
			}
			else if(tec.adjacentChestXNeg != null)
			{
				tec = tec.adjacentChestXNeg;
			}
			if(open)
			{
				tec.openInventory();
			}
			else
			{
				tec.closeInventory();
			}
		}
	}
	
	
	@Override
	public EntityLookHelper getLookHelper()
	{
		return super.getLookHelper();
	}

	@Override
	public void moveEntity(double toX, double toY, double toZ) 
	{
		if(this.preciseScale > this.defaultScale && (toX != 0 || toZ != 0 || toY != 0) && toX != toZ)
		{
			float height = this.height;
			this.height = 1.8f;
			this.boundingBox.maxY = this.boundingBox.minY + this.height;
			
			super.moveEntity(toX - (this.posX - posX), toY - (this.posY - posY), toZ - (this.posZ - posZ));
			
			this.height = height;
			this.boundingBox.maxY = this.boundingBox.minY + height;
		}
		else
		{
			super.moveEntity(toX, toY, toZ);
		}
	}

	@Override
	public boolean isInRangeToRender3d(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
		return true;
	}

	@Override
	public boolean isInRangeToRenderDist(double p_70112_1_) {
		return true;
	}

	@Override
	public void onEntityUpdate() 
	{
		super.onEntityUpdate();
		if (!this.worldObj.isRemote && this.worldObj.difficultySetting == EnumDifficulty.PEACEFUL && this.getHealth() < this.getMaxHealth() && this.ticksExisted % 20 * 12 == 0)
		{
			this.heal(1);
		}
	}
	
	
	

	@Override
	public void onLivingUpdate() 
	{
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
	public void onDeath(DamageSource p_70645_1_)
	{
		super.onDeath(p_70645_1_);
		
		if(!worldObj.isRemote)
		{
			this.inventory.dropAllItems();
			
			if(this.isIndependent())
			{
				this.experienceTotal += (this.preciseScale+0.5) * 10000;
			}
			
			dropAllXP();
		}
	}


	public void onSpawnedBy(String spawnedBy) 
	{
		this.setOwner(spawnedBy);
	}
	
	//==================================================================================================================
	//TODO Message display
	//==================================================================================================================
	
	int displayMessageCooldown = 0;
	
	String displayMessage = "";
	
	int displayMessageColour = 0;
	
	public String getDisplayMessage()
	{
		return this.displayMessage;
	}
	
	public int getDisplayMessageColour()
	{
		return this.displayMessageColour;
	}
	
	public int getDisplayMessageCooldown()
	{
		return this.displayMessageCooldown;
	}

	public void setDisplayMessage(String s) 
	{
		if(s == null)
		{
			s = "";
		}
		
		this.displayMessage = s;
	}

	public void setDisplayMessageColour(int colour) 
	{
		this.displayMessageColour = colour;
	}

	public void setDisplayMessageCooldown(int cooldown)
	{
		if(displayMessageCooldown != cooldown)
		{
			this.getWatcher().getSync(Syncer.ID_MESG).setDirty();
		}
		this.displayMessageCooldown = cooldown;
	}
	
	//==================================================================================================================
	//TODO B Physics. B Coz Why not
	//==================================================================================================================
	
	Particle meParticle;
	
	public double lastBLX, lastBLY;
	
	public double lastBRX, lastBRY;

	public Particle leftBParticle;
	
	public Particle rightBParticle;
	
	public double maxBDisp = 5;
	
	Spring[] springs = new Spring[4];
	
	double netMomentum = 0;
	
	double physicsTimeStep = 0.05; // 1/20 = 0.050 = 50 ms
	
	@SideOnly(value = Side.CLIENT)
	public void updateBPhysics()
	{
		if(this.getOptions().female.get())
		{
			
			double len = maxBDisp = 4;
			
			if(meParticle == null)
			{
				meParticle = new Particle();
				leftBParticle = new Particle();
				rightBParticle = new Particle();
				
				leftBParticle.setPosition(posX, posY, posZ);
				rightBParticle.setPosition(posX, posY, posZ);
				
				leftBParticle.accY = 300;
				rightBParticle.accY = 300;
				
				leftBParticle.accX = 50;
				rightBParticle.accX = -50;
				
				
				leftBParticle.resX = leftBParticle.resY = leftBParticle.resZ = 0.65;
				rightBParticle.resX = rightBParticle.resY = rightBParticle.resZ = 0.65;
				
				int k = 210;
				
				//Above Spring.
				springs[0] = new Spring(0, len, 0, len, k*0.75);
				
				//Below Spring.
				springs[1] = new Spring(0, -len, 0, len, k);
				
				//Left Spring
				springs[2] = new Spring(-len, 0, 0, len, k*3);
				
				//Right Spring
				springs[3] = new Spring(len, 0, 0, len, k*3);

			}
			
			this.lastBLX = leftBParticle.posX;
			this.lastBLY = leftBParticle.posY;
			
			this.lastBRX = rightBParticle.posX;
			this.lastBRY = rightBParticle.posY;
			
			if(this.ticksExisted < 3)
			{
				leftBParticle.setPosition(posX, posY, posZ);
				rightBParticle.setPosition(posX, posY, posZ);
			}
			
			springs[0].posX = posX;
			springs[0].posY = posY + len;
			springs[0].posZ = posZ;
			springs[0].length = len;
			
			springs[1].posX = posX;
			springs[1].posY = posY - len;
			springs[1].posZ = posZ;
			springs[1].length = len;
			
			springs[2].posX = posX - len;
			springs[2].posY = posY;
			springs[2].posZ = posZ;
			springs[2].length = len;
			
			springs[3].posX = posX + len;
			springs[3].posY = posY;
			springs[3].posZ = posZ;
			springs[3].length = len;
			

			leftBParticle.mass = 0.95 + this.getScale()/(20);
			rightBParticle.mass = 0.95 + this.getScale()/(20);
			
			meParticle.setPosition(posX, posY, posZ);
			meParticle.tickBackward(physicsTimeStep);//50 ms, 20 ticks per second
			
			Vector impulse = meParticle.theImpulse;
			
			impulse = impulse.multiply((-1) * (this.getRNG().nextFloat() * 0.6f + 0.7f));
			
			impulse.y = impulse.y * impulse.y;
			
			impulse.x = 0;
			impulse.z = 0;
			
			
		
			this.leftBParticle.addVelocity(impulse);
			this.rightBParticle.addVelocity(impulse);
			
			float f6 = this.prevLimbSwingAmount + (this.limbSwingAmount - this.prevLimbSwingAmount) * 0.5f;
            float f7 = this.limbSwing - this.limbSwingAmount * (1.0F - 0.5f);

            if (f6 > 1.0F)
            {
                f6 = 1.0F;
            }
            
            double armSwing = Math.cos(f7) * f6 * 1;
            
            this.leftBParticle.velY += armSwing;

            this.rightBParticle.velY -= armSwing;
            
            float rotate = this.renderYawOffset - this.prevRenderYawOffset;
            
            if(rotate != 0)
            {
            	this.leftBParticle.velX -= rotate * (this.getRNG().nextFloat() * 0.1f + 0.95f)  * 0.2;
                
                this.rightBParticle.velX -= rotate * (this.getRNG().nextFloat() * 0.1f + 0.95f) * 0.2;
            }
            
			for(int a = 0; a < 4; a++)
			{
				Spring spring = springs[a];
				
				if(a == 0 || a == 1)
				{
					spring.apply(leftBParticle, physicsTimeStep);
					spring.apply(rightBParticle, physicsTimeStep);
				}
				else if(a == 2)
				{
					spring.apply(leftBParticle, physicsTimeStep);
				}
				else if(a == 3)
				{
					spring.apply(rightBParticle, physicsTimeStep);
				}
				
			}
			
			leftBParticle.tickForward(physicsTimeStep);
			rightBParticle.tickForward(physicsTimeStep);
			
			Particle[] particles = new Particle[]{leftBParticle, rightBParticle};

			double minX = posX - len;
			double maxX = posX + len;
			
			double minY = posY - len;
			double maxY = posY + len;
			
				
			for(int b = 0; b < particles.length; b++)
			{
				Particle pr = particles[b];
				if(pr.posX > maxX){pr.posX = maxX; pr.velX = 0;}
				if(pr.posX < minX){pr.posX = minX; pr.velX = 0;}
				
				if(pr.posY > maxY){pr.posY = maxY; pr.velY = 0;}
				if(pr.posY < minY){pr.posY = minY; pr.velY = 0;}
			}	

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
	public ChunkCoordinates getGuardPosition()
	{
		return guardPosition;
	}
	
	public boolean isGuardPositionSet()
	{
		return guardPosition.posX != Integer.MAX_VALUE || guardPosition.posY != Integer.MAX_VALUE || guardPosition.posZ != Integer.MAX_VALUE; 
	}
	
	public void teleportToGuardPosition()
	{
		if(isGuardPositionSet())
		{
			setPosition(this.getGuardPosition().posX+0.5, this.getGuardPosition().posY+1, this.getGuardPosition().posZ+0.5);
		}
	}
	
	public void setGuardPosition(ChunkCoordinates cc)
	{
		this.guardPosition = cc;
	}
	
	//==================================================================================================================
	//TODO Age, Scaling and Growing Up
	//==================================================================================================================
	
	//preciseScale, visibleScale
	
	private float lastScale = 0.5f;
	
	public float getInterpolatedScale(float partial)
	{
		return (preciseScale-lastScale)*partial + lastScale;
	}
	
	/**
	 * How many ticks until the clone can start shrinking.
	 */
	public int shrinkCooldown = 0;
	
	/**
	 * The scale value the clone wishes to reach.
	 */
	public double aimForScale = 0.5f;
	
	public int getShrinkCooldown()
	{
		return shrinkCooldown;
	}
	
	int stemCellInjectTime;
	
	public int getStemCellCooldown()
	{
		return stemCellInjectTime;
	}
	
	public void injectStemcells(boolean increaseHeight)
	{
		if(this.aimForScale < this.maxScale)
		{
			
			if(this.aimForScale >= this.defaultScale)
			{
				if(this.aimForScale == this.defaultScale || (increaseHeight && this.aimForScale < this.maxScale))
				{
					this.aimForScale += 0.05D;
					this.shrinkCooldown += 300;
				}
				else
				{
					this.shrinkCooldown += 1200;
				}
			}
			else
			{
				this.aimForScale += 0.1D;
			}
			
		}
		else
		{
			this.shrinkCooldown += 1200;
		}
		
		if(worldObj.isRemote)
		{
			stemCellInjectTime += 120;
			stemCellInjectTime /= 2;
		}
	}
	
	public void setShrinkCooldown(int cool)
	{
		shrinkCooldown = cool;
	}
	
	public double getAimScale()
	{
		return this.aimForScale;
	}
	
	public void setAimScale(double aimForScale)
	{
		this.aimForScale = aimForScale;
	}
	
	public void updateScale()
	{
		lastScale = preciseScale;
		
		
		setScale(preciseScale + growthFactor);
		
		if(lastScaleUpdate != preciseScale)
		{
			if(Math.abs(lastScaleUpdate-preciseScale) > 0.02)
			{
				lastScaleUpdate = preciseScale;
			}
		}

		if(shrinkCooldown > 0)
		{
			shrinkCooldown--;
			
			if(this.aimForScale < this.defaultScale)
			{
				this.shrinkCooldown = 0;
			}
		}
		
		
		if(this.aimForScale > this.maxScale)
		{
			this.aimForScale = this.maxScale;
		}
		
		this.maxScale = 1.25f;

		if(this.preciseScale < this.defaultScale)
		{
			this.aimForScale += this.defaultGrowthFactor;
		}
		
		if(this.aimForScale > defaultScale && shrinkCooldown == 0)
		{
			//We have grown, and need to shrink down
			double diff = (this.defaultScale - this.aimForScale);
			
			this.aimForScale += diff/2000.0f - 0.000005f;
			
			if(this.aimForScale <= this.defaultScale)
			{
				this.aimForScale = this.defaultScale;
			}
		}
		
		double diff = this.aimForScale - this.preciseScale;
		
		if(diff < 0.0004 && diff > -0.0004)
		{
			this.preciseScale = (float)this.aimForScale;
			this.growthFactor = 0;
		}
		else
		{
			this.growthFactor = (float)(diff / 50.0f + Math.signum(diff) * 0.00005f);
		}
	}
	
	public void setScale(float scale)
	{
		if(scale > maxScale)
		{
			if(preciseScale > maxScale)
			{
				preciseScale = maxScale;
			}
			scale = maxScale;
		}
		
		double lastMaxHealth = Math.round(this.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());

		this.setSize(Math.min(0.6f, 0.6f*scale), 1.8f*scale);
		
		stepHeight = Math.max(0.5f, preciseScale>1?((preciseScale-1)*2 + 0.5f):preciseScale * 0.5f);
		
//		this.jumpMovementFactor = Math.max(0.5f, preciseScale>1?((preciseScale-1)*2 + 0.5f):preciseScale * 0.5f);
		
		this.preciseScale = scale;
		
		if(preciseScale > maxScale)
		{
			preciseScale = maxScale;
		}
		
		double newMaxHealth = Math.round(20.0D * preciseScale);
		
		
		
		if(lastMaxHealth != newMaxHealth)
		{
			this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(newMaxHealth);
			this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3 + 0.1f * (preciseScale-1));
			
			this.heal((float)(newMaxHealth-lastMaxHealth));
			
			if(this.getHealth() <= 0)
			{
				this.heal(1.0f);
			}
			
		}
		
		if(this.getHealth() > newMaxHealth)
		{
			this.setHealth((float)newMaxHealth);
		}
	}
	
	
	float boundingBoxShift = 0;
	
	float actualHeight = 0.5f;
	
	protected void setSize(float newWidth, float newHeight)
    {
        
		float f2;
        //Only update the width periodically, otherwise clones will
        //get stuck in walls a lot.
        {
        	if(Math.abs(newWidth - this.width) > 0.01)
        	{
        		
        		f2 = this.width;
        		
        		this.width = newWidth;
        		
        		double oldX = this.boundingBox.maxX;
        		double oldZ = this.boundingBox.maxZ;
        		
        		double nextX = Math.ceil(oldX);
        		double nextZ = Math.ceil(oldZ);
        		
            	this.boundingBox.maxX = this.boundingBox.minX + (double)this.width;
                this.boundingBox.maxZ = this.boundingBox.minZ + (double)this.width;
                
                float shiftX = (float)(oldX - this.boundingBox.maxX)/2.0f;
            	float shiftZ = shiftX;
                
                //The bounding box now crosses the border to the next block.
                if(this.boundingBox.maxX > nextX)
                {
                	//Move it back into position, so we don't clip the walls.
                	//Unless we are truly too large for the defined space
                	shiftX = (float)(nextX - this.boundingBox.maxX);
                }
                
                if(this.boundingBox.maxZ > nextZ)
                {
                	shiftZ = (float)(nextZ - this.boundingBox.maxZ);
                }

                if(this.ticksExisted > 0)
                {
                	this.moveEntity(shiftX , 0.0D, shiftZ);
                }
                else
                {
                	this.boundingBox.minX += shiftX;
                	this.boundingBox.minZ += shiftZ;
                	
                	this.boundingBox.maxX += shiftX;
                	this.boundingBox.maxZ += shiftZ;
                }
        	}
        }
        
        //Always resize the height, to make the Name and Stats move smoothly with scale.
        {
        	this.actualHeight = newHeight;
        	if(this.isSneaking())
        	{
        		this.height = newHeight * 0.88f;
        	}
            this.boundingBox.maxY = this.boundingBox.minY + (double)this.height;
        }

        f2 = newWidth % 2.0F;

        if ((double)f2 < 0.375D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_1;
        }
        else if ((double)f2 < 0.75D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_2;
        }
        else if ((double)f2 < 1.0D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_3;
        }
        else if ((double)f2 < 1.375D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_4;
        }
        else if ((double)f2 < 1.75D)
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_5;
        }
        else
        {
            this.myEntitySize = Entity.EnumEntitySize.SIZE_6;
        }
    }
	
	
	public float getScale()
	{
		return preciseScale;
	}
	
	public float getMaxScale()
	{
		return maxScale;
	}

	
	public float getGrowthFactor()
	{
		return growthFactor;
	}
	
	public void setGrowthFactor(float growthFactor)
	{
		this.growthFactor = growthFactor;
		
		if(this.growthFactor > maxGrowthFactor)
		{
			this.growthFactor = maxGrowthFactor;
		}
		else if(this.growthFactor < -this.maxGrowthFactor)
		{
			this.growthFactor = -this.maxGrowthFactor;
		}
	}
	
	
	
	@Override
	public float getEyeHeight() {
		return super.getEyeHeight() * (preciseScale) * (this.isSneaking()?0.88f:1.0f);
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
		
		for(Iterator<?> it = this.getActivePotionEffects().iterator(); it.hasNext();)
		{
			PotionEffect pe = (PotionEffect)it.next();
			list.add(pe.getPotionID());
		}
		
		for(int a = 0; a < list.size(); a++)
		{
			this.removePotionEffect(list.get(a));
		}
    }
	//==================================================================================================================
	//TODO Attacking
	//==================================================================================================================
	

	
	public PathEntity moveTo(double x, double y, double z)
	{
		PathEntity path;
		getNavigator().setPath(path = getNavigator().getPathToXYZ(x, y, z), 1.0f);
		return path;
	}
	
	public PathEntity moveToEntity(Entity e)
	{
		PathEntity path;
		getNavigator().setPath(path = getNavigator().getPathToEntityLiving(e), 1.0f);
		return path;
	}
	
	public void setPath(PathEntity path)
	{
		getNavigator().setPath(path, 1.0f);
	}
	
	public PlayerTeam getCTeam()
	{
		return this.team;
	}
	
	public PlayerTeam setCTeam(PlayerTeam team)
	{
		if(team != this.team)
		{
			this.setAttackTarget(null);
		}
		return this.team = team;
	}
	
	/**
	 * Returns whether the clone should provoke an attack against the entity. Because of teams or Mob selection.
	 * @param entity
	 * @return
	 */
	public boolean shouldProvokeAttack(EntityLivingBase entity){
		if(entity == null){
			return false;
		}
		if(entity instanceof FakePlayer)
		{
			return getCTeam().doesAttackTeam(((FakePlayer)entity).clone.getCTeam()) && ((FakePlayer)entity).clone.getOptions().fight.get();
		}
		else if(entity instanceof EntityPlayer)
		{
			return (getCTeam() == PlayerTeam.Evil || getCTeam() == PlayerTeam.Rampant) && !((EntityPlayer)entity).capabilities.isCreativeMode;
		}
		else if(entity instanceof EntityClone)
		{
			return getCTeam().doesAttackTeam(((EntityClone)entity).getCTeam()) && ((EntityClone)entity).getOptions().fight.get();
		}
		else
		{
			return (getCTeam() == PlayerTeam.Rampant && this.options.attackables.isAttackable(entity)) || this.options.attackables.canAttack(EntityList.getEntityID(entity));
		}
	}
	
	/**
	 * Returns whether the clone is capable of attacking the entity. Even if it's not a selected entity. Used for retaliation
	 * @param entity
	 * @return
	 */
	public boolean canAttackEntity(EntityLivingBase entity)
	{
		if(entity == null || !entity.isEntityAlive())
		{
			return false;
		}
		if(entity instanceof FakePlayer)
		{
			return getCTeam().doesAttackTeam(((FakePlayer)entity).clone.getCTeam());
		}
		else if(entity instanceof EntityPlayer)
		{
			return (getCTeam() != PlayerTeam.Good) && !((EntityPlayer)entity).capabilities.isCreativeMode;
		}
		else if(entity instanceof EntityClone)
		{
			return getCTeam().doesAttackTeam(((EntityClone)entity).getCTeam());
		}
		return true;
	}
	
	int attackTimer = 2;
	
	public void attackEntity(EntityLivingBase attack)
	{
		if(attackTimer > 0)
		{
			attackTimer--;
		}
		
		if(this.foodStats.getFoodLevel() < criticalEatingPoint && this.isEatingFood())
		{
			return;
		}
		
		if(!this.canEntityBeSeen(attack))
		{
			selectBestDamageItem(true);
			return;
		}
		
		//Squared
		int attackDist = ((attack instanceof EntityPlayer)?4:12);
		
		double distanceSquared = this.getDistanceSqToEntity(attack);
		
		if(distanceSquared < attackDist)
		{
			selectBestDamageItem(true);
			this.clearItemInUse();
			
			if((float)attack.hurtResistantTime <= (float)attack.maxHurtResistantTime / 2.0F && attackTimer == 0)
			{
				this.swingItem();
				this.attackTargetEntityWithCurrentItem(attack);
				attackTimer = this.getRNG().nextInt(10)+8;
			}
		}
		else if(distanceSquared < 400)
		{
			if(selectBestBow())
			{
				ItemStack current = inventory.getCurrentItem();
				if(current != null)
				{
					if(this.getItemInUse() != current)
					{
						this.setItemInUse(current, 20);
					}
				}
			}
		}
		
	}
	
	public boolean selectBestBow()
	{
		if(!inventory.hasItem(Items.arrow))
		{
			return false;
		}
		
		int index = -1;
		float best = -1;
		
		for(int a = 0; a < 9; a++)
		{
			if(inventory.getStackInSlot(a) != null)
			{
				ItemStack stack = inventory.getStackInSlot(a);
				
				if(stack.getItemUseAction() == EnumAction.bow)
				{
					float goodness = getBowGoodness(stack);
					
					if(goodness > best)
					{
						best = goodness;
						index = a;
					}
				}
			}
		}
		if(index != -1)
		{
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
	        
	        if(EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, this.getHeldItem()) == 0){
	        	this.inventory.consumeInventoryItem(Items.arrow);
	        }
	        
			
		}
	}
	
	public void selectBestDamageItem(boolean searchWhole){
		int index = -1;
		float best = -1;
		for(int a = 0; a < (searchWhole?36:9); a++)
		{
			if(inventory.getStackInSlot(a) != null)
			{
				ItemStack stack = inventory.getStackInSlot(a);
				float goodness = getWeaponGoodness(stack);
				if(goodness > best){
					best = goodness;
					index = a;
				}
				
			}
		}
		if(index != -1){
			if(index < 9)
			{
				inventory.currentItem = index;
			}
			else
			{
				inventory.currentItem = inventory.putStackOnHotbar(index);
			}
		}
		else if(!searchWhole)
		{
			selectBestDamageItem(true);
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
                    
                    f *= Math.sqrt(preciseScale);
                    
                    i += Math.round((preciseScale-1)*4);
                    
                    if(i < 0)
                    {
                    	i = 0;
                    }
                    
                    f += i;
                    
//                    System.out.println(f);

                    EntityPlayer me = this.getPlayerInterface();
                    me.posX = this.posX;
                    me.posY = this.posY;
                    me.posZ = this.posZ;
                    
//                    me.rotationPitch = this.rotationPitch;
//                    me.rotationYaw = me.rotationYawHead = this.rotationYawHead;
                    
                    boolean flag2 = p_71059_1_.attackEntityFrom(DamageSource.causePlayerDamage(me), f);

                    
                    
                    if (flag2)
                    {
                        if (i > 0)
                        {
                            p_71059_1_.addVelocity((double)(-MathHelper.sin(this.rotationYawHead * (float)Math.PI / 180.0F) * (float)i * 0.5F), 0.1D, (double)(MathHelper.cos(this.rotationYawHead * (float)Math.PI / 180.0F) * (float)i * 0.5F));
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
	public boolean attackEntityFrom(DamageSource damageSource, float damageAmount) 
    {
    

    	

    	{
    		Entity e = damageSource.getEntity();
    		
    		if(e instanceof EntityPlayer)
    		{
    			//Show the user that the clone has been injected with something.
    			ItemStack hitWith = ((EntityPlayer) e).getCurrentEquippedItem();
    			if(hitWith != null && hitWith.getItem() == CloneCraft.INSTANCE.itemNeedle && hitWith.getItemDamage() == 3)
    			{
    				this.hurtTime = 3;
    				return true;
    			}
    		}
    		
    		if(e != null && !e.worldObj.isRemote)
    		{
    			double dx = this.posX - e.posX;
    			double dz = e.posZ - this.posZ;
    			
    			double angle = Math.atan2(dx, dz);
    			
    			this.rotationYawHead = this.rotationYaw = (float)(angle * 180.0 / Math.PI);
    		}
    	}


    	if(this.preciseScale > this.defaultScale && damageSource == DamageSource.inWall)
    	{
    		return false;
    	}

		if(damageSource instanceof EntityDamageSource)
		{
			if(this.getOptions().retaliate.get() && this.getOptions().fight.get())
			{
				EntityDamageSource entityDamageSource = (EntityDamageSource)damageSource;
				
				Entity damager = entityDamageSource.getEntity();
				
				if(damager instanceof FakePlayer)
				{
					damager = ((FakePlayer)damager).clone;
				}
				
				
				if(damager instanceof EntityPlayer)
				{
					EntityPlayer player = ((EntityPlayer)damager);
					
					if(player.capabilities.isCreativeMode || (this.ownerName != null && this.ownerName.equals(player.getCommandSenderName())))
					{
						return super.attackEntityFrom(damageSource, damageAmount);
					}
				}
				if(damager instanceof EntityLivingBase)
				{
					if(this.canAttackEntity((EntityLivingBase)damager))
					{
						this.setAttackTarget((EntityLivingBase)damager);
						this.setPath(this.getNavigator().getPathToEntityLiving(damager));
					}
					
					List list = this.worldObj.getEntitiesWithinAABB(EntityClone.class, this.boundingBox.expand(8, 8, 8));
					
					for(int a = 0; a < list.size(); a++)
					{
						if(list.get(a) instanceof EntityClone)
						{
							EntityClone clone = (EntityClone)list.get(a);
							
							if(clone.options.retaliate.get() && clone.getAttackTarget() == null && clone.getTeam() == this.getTeam())
							{
								clone.setAttackTarget((EntityLivingBase)damager);
								clone.setPath(clone.getNavigator().getPathToEntityLiving(damager));
							}
						}
					}
				}
			}
		}
		/*if(damageSource.getDamageType().equals("fall") && this.getHealth() - damageAmount <= 0)
		{
			return false;
		}*/
		
		return super.attackEntityFrom(damageSource, damageAmount);
	}
    
	
	@Override
	public void knockBack(Entity p_70653_1_, float power, double dx, double dz) {
		super.knockBack(p_70653_1_, power, dx, dz);
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
    		
    		for(int a = 0; a < modifiedAttackEntities.size(); a++)
    		{
    			if(!modifiedAttackEntities.get(a).isEntityAlive())
    			{
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
	//TODO Block Breaking / Building
	//==================================================================================================================
	
    Material[] pickaxeMaterials = new Material[]{
    	Material.anvil,
    	Material.iron,
    	Material.piston,
    	Material.rock
    };
    
    Material[] shovelMaterials = new Material[]{
    	Material.clay,
    	Material.craftedSnow,
    	Material.grass,
    	Material.ground,
    	Material.sand,
    	Material.snow
    };
    
    Material[] axeMaterials = new Material[]{
    	Material.plants,
    	Material.wood
    };
    
    Material[] swordMaterials = new Material[]{
    	Material.web
    };
    
    Material[] shearMaterials = new Material[]{
    	Material.cactus,
    	Material.sponge,
    	Material.leaves,
    	Material.cloth
    };
    
    /**
     * Tries to select the best item/tool in the clone's inventory for the given block
     * If it finds a tool, that is capable of harvesting the block, then it returns true.
     * Otherwise false
     * @param theCoords 
     * @param block The block you wish to break
     * @return True if the clone can currently break the block.
     */
    public boolean selectBestItemForBlock(ChunkCoordinates theCoords, Block block, int meta){
    	if(block == Blocks.air)
    	{
    		return false;
    	}
    	
    	Material material = block.getMaterial();
    	
    	if(material.isLiquid())
    	{
    		return false;
    	}
    	
    	ArrayList<ItemSelectEntry> itemList = new ArrayList<ItemSelectEntry>();
    	if(isMaterial(pickaxeMaterials, material))
    	{
    		ArrayList<ItemSelectEntry<ItemPickaxe>> list = this.selectItemByClass(ItemPickaxe.class);
    		for(int a = 0; a < list.size(); a++){itemList.add(list.get(a));}
    	}
    	else if(isMaterial(axeMaterials, material))
    	{
    		ArrayList<ItemSelectEntry<ItemAxe>> list = this.selectItemByClass(ItemAxe.class);
    		for(int a = 0; a < list.size(); a++){itemList.add(list.get(a));}
    	}
    	else if(isMaterial(shovelMaterials, material))
    	{
    		ArrayList<ItemSelectEntry<ItemSpade>> list = this.selectItemByClass(ItemSpade.class);
    		for(int a = 0; a < list.size(); a++){itemList.add(list.get(a));}
    	}
    	else if(isMaterial(swordMaterials, material))
    	{
    		ArrayList<ItemSelectEntry<ItemSword>> list = this.selectItemByClass(ItemSword.class);
    		for(int a = 0; a < list.size(); a++){itemList.add(list.get(a));}
    	}
    	else if(isMaterial(shearMaterials, material))
    	{
    		ArrayList<ItemSelectEntry<ItemShears>> list = this.selectItemByClass(ItemShears.class);
    		for(int a = 0; a < list.size(); a++){itemList.add(list.get(a));}
    	}
    	
    	for(int a = 0; a < itemList.size(); a++)
    	{
    		ItemSelectEntry entry = itemList.get(a);
    		
    		if(!this.canItemHarvestBlock(entry.stack, theCoords, block, meta))
    		{
    			itemList.remove(a);
    			a--;
    			continue;
    		}
    		
    		if(entry.theItem instanceof ItemTool)
    		{
    			ItemTool tool = (ItemTool)entry.theItem;
    			entry.priority = tool.func_150893_a(entry.stack, block);
    		}
    	}
    	
    	if(itemList.size() > 0)
    	{
    		
    		Collections.sort(itemList);
    		int slot = inventory.putStackOnHotbar(itemList.get(itemList.size()-1).inventoryIndex);
    		this.inventory.currentItem = slot;
    		return true;
    	}
    	
    	if(material.isToolNotRequired())
    	{
    		//By this point, there is no specific tool we want to use.
    		//So just fists will do.
    		//To avoid damaging good items that don't need to be used,
    		//try to select an empty slot, or at least make one.
    		this.inventory.trySelectEmptySlot();
    		return true;
    	}
    	
    	return false;
    }
    
    public boolean canItemHarvestBlock(ItemStack stack, ChunkCoordinates theCoords, Block block, int meta)
    {
    	//If the relative hardness is 0, then it will never be broken.
		float hardness = block.getPlayerRelativeBlockHardness(this.getPlayerInterface(), this.worldObj, theCoords.posX, theCoords.posY, theCoords.posZ);
		
		if(hardness == 0)
		{
			return false;
		}
		
		int harvestLevel = block.getHarvestLevel(meta);
		String harvestTool = block.getHarvestTool(meta);
		
		if(harvestTool != null && stack.getItem().getHarvestLevel(stack, harvestTool) < harvestLevel)
		{
			return false;
		}
		return true;
    }
    
    public static class ItemSelectEntry<E extends Item> implements Comparable<ItemSelectEntry>{

    	public E theItem;
    	public int inventoryIndex;
    	public ItemStack stack;
    	
    	public float priority = 0;
    	
		public ItemSelectEntry(int inventoryIndex, ItemStack stack, E item) {
			this.theItem = item;
			this.inventoryIndex = inventoryIndex;
			this.stack = stack;
		}

		@Override
		public int compareTo(ItemSelectEntry entry) {
			if(priority > entry.priority)
			{
				return 1;
			}
			else if(priority < entry.priority)
			{
				return -1;
			}
			return 0;
		}
		
		
		
    }
    
    public <E extends Item> ArrayList<ItemSelectEntry<E>> selectItemByClass(Class<? extends E> type)
    {
    	ArrayList<ItemSelectEntry<E>> list = new ArrayList<ItemSelectEntry<E>>();
    	
    	for(int a = 0; a < this.inventory.mainInventory.length; a++)
    	{
    		ItemStack theStack = this.inventory.mainInventory[a];
    		if(theStack != null)
    		{
    			if(theStack.getItem() != null && type.isAssignableFrom(theStack.getItem().getClass()))
    			{
    				ItemSelectEntry<E> entry = new ItemSelectEntry<E>(a, theStack, (E) theStack.getItem());
    				list.add(entry);
    			}
    		}
    	}
    	
    	return list;
    }
    
    
    
    

    
	//==================================================================================================================
	//TODO Packet Handling
	//==================================================================================================================
	
	public void sendToServer(Packet packet){
		if(Minecraft.getMinecraft().thePlayer != null)
		{
			Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(packet);
		}
	}
	
	public void sendToPlayers(Packet packet, Object... players)
	{
		if(players == null)return;
		for(int a = 0; a < players.length; a++)
		{
			if(players[a] != null && players[a] instanceof EntityPlayerMP)
			{
				((EntityPlayerMP)players[a]).playerNetServerHandler.sendPacket(packet);
			}
		}
	}
	
	public void sendToAllWatching(Packet packet)
	{
		Set<EntityPlayer> watching = this.getWatchingEntities();
		if(watching != null)
		{
			for(EntityPlayer p : watching)
			{
				if(p instanceof EntityPlayerMP)
				{
					((EntityPlayerMP)p).playerNetServerHandler.sendPacket(packet);
				}
			}
		}
	}
	
	public void sendToOwnersWatching(Packet packet)
	{
		Set<EntityPlayer> watching = this.getWatchingEntities();
		if(watching != null)
		{
			for(EntityPlayer p : watching)
			{
				if(p instanceof EntityPlayerMP && this.canUseThisEntity(p))
				{
					((EntityPlayerMP)p).playerNetServerHandler.sendPacket(packet);
				}
			}
		}
	}
	
	public void sendToOwnersWatchingExcluding(Packet packet, EntityPlayer exclude)
	{
		Set<EntityPlayer> watching = this.getWatchingEntities();
		
		if(watching != null)
		{
			for(EntityPlayer p : watching)
			{
				if(p != exclude && p instanceof EntityPlayerMP && this.canUseThisEntity(p))
				{
					((EntityPlayerMP)p).playerNetServerHandler.sendPacket(packet);
				}
			}
		}
	}

	
	public void sendToPlayer(Packet p, Object player)
	{
		if(player != null && player instanceof EntityPlayerMP)
		{
			((EntityPlayerMP)player).playerNetServerHandler.sendPacket(p);
		}
	}
	
	public void sendToPlayers(Packet p, EntityPlayerMP... players)
	{
		for(int a = 0; a < players.length; a++)
		{
			players[a].playerNetServerHandler.sendPacket(p);
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
	
	FakePlayer playerInterface = null;
	
	public FakePlayer getPlayerInterface(){
		if(playerInterface == null){
			playerInterface = new FakePlayer(this);
		}
		return playerInterface;
	}
	
	//==================================================================================================================
	//TODO Eating
	//==================================================================================================================
	
	private int criticalEatingPoint = 5;
	
	public boolean isEatingFood()
	{
		if(this.getFlag(4))
		{
			return true;
			/*ItemStack currentItem = this.getHeldItem();
			
			if(currentItem != null && currentItem.getItem() instanceof ItemFood)
			{
				return true;
			}*/
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
		if(!this.getFlag(4) && this.foodStats.getFoodLevel() < 20 && (this.getAttackTarget() == null || this.foodStats.getFoodLevel() < criticalEatingPoint))
		{
			ItemStack foodStack;
			ItemFood food;
			for(int a = 0; a < this.inventory.mainInventory.length; a++)
			{
				if(this.inventory.mainInventory[a] != null && this.inventory.mainInventory[a].getItem() instanceof ItemFood)
				{
					
					foodStack = this.inventory.mainInventory[a];
					food = (ItemFood)this.inventory.mainInventory[a].getItem();
					if(foodStack.stackSize > 0)
					{
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
		nbt.setDouble("scaleAim", this.aimForScale);
		nbt.setInteger("shrinkCooldown", this.shrinkCooldown);
		

		nbt.setInteger("displayMessageCooldown", this.displayMessageCooldown);
		nbt.setInteger("displayMessageColour", this.displayMessageColour);
		nbt.setString("displayMessage", this.displayMessage);
	
		
		nbt.setIntArray("guardPosition", new int[]{
				guardPosition.posX,
				guardPosition.posY,
				guardPosition.posZ
		});
		
		CommandTask task = this.getCommandAI().getRunningTask();
		
		if(task != null)
		{
			NBTTagCompound nbtBase = new NBTTagCompound();
			nbtBase.setInteger("CommAITaskID", task.commandBase.getId());
			
			if(task.getPlayerName() != null && task.getPlayerName().length() > 0)
			{
				nbtBase.setString("CommAITaskPlayer", task.getPlayerName());
			}
			task.saveTask(nbtBase);
			
			nbt.setTag("CommAITask", nbtBase);
		}
		
		nbt.setTag("aiBuild", aiBuild.saveBuildState(new NBTTagCompound()));
		nbt.setTag("aiFetch", aiFetch.writeToNBT(new NBTTagCompound()));
		
		this.foodStats.writeNBT(nbt);
		this.options.writeNBT(nbt);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt)
	{
//		Thread.dumpStack();
		super.readEntityFromNBT(nbt);
		this.setName(nbt.getString("Name_U"));

		this.setOwner(nbt.getString("Owner"));
		this.team = PlayerTeam.getByID(nbt.getInteger("Team"));
		this.inventory.readFromNBT(nbt.getTagList("Inventory", 10));
		this.setAimScale(nbt.getDouble("scaleAim"));
		this.shrinkCooldown = nbt.getInteger("shrinkCooldown");
		
		this.displayMessageCooldown = nbt.getInteger("displayMessageCooldown");
		this.displayMessageColour = nbt.getInteger("displayMessageColour");
		this.displayMessage = nbt.getString("displayMessage");

		
		float scale = nbt.getFloat("scale");
		
		if(scale != 0)
		{
			this.setScale(scale);
		}
		
		int[] guardPos = nbt.getIntArray("guardPosition");
		
		if(guardPos != null && guardPos.length == 3)
		{
			this.guardPosition = new ChunkCoordinates(
					guardPos[0],
					guardPos[1],
					guardPos[2]
					);
		}

		
		if(nbt.hasKey("CommAITask"))
		{
			NBTTagCompound nbtBase = nbt.getCompoundTag("CommAITask");
			
			if(nbtBase.hasKey("CommAITaskID"))
			{
				int id = nbtBase.getInteger("CommAITaskID");
				Command command = Commands.getCommandById(id);
				
				if(command != null)
				{
					CommandTask task = command.getCommandExecutionDelegate();
					
					if(task != null)
					{
						task.loadTask(nbtBase);
						task.setClone(this);
						task.setPlayerName(nbtBase.getString(""));
						this.getCommandAI().setTask(task);
					}
				}
			}
		}

		this.aiBuild.loadBuildState(nbt.getCompoundTag("aiBuild"));
		this.aiFetch.readFromNBT(nbt.getCompoundTag("aiFetch"));
		
		
		this.foodStats.readNBT(nbt);
		this.options.readFromNBT(nbt);
		
		//Set the watchable object, otherwise the CloneOptions may be rest when loaded.
		this.dataWatcher.updateObject(ID_OPTIONS, Integer.valueOf(options.toInteger()));
	}
	
	public Set<EntityPlayer> getWatchingEntities(){
		if(this.worldObj instanceof WorldServer){
			return ((WorldServer)this.worldObj).getEntityTracker().getTrackingPlayers(this);
		}
		return null;
	}
	
	//==================================================================================================================
	//kkk
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
			return getPlayerByName(ownerName);
		}
		
		return null;
	}
	
	public EntityPlayer getPlayerByName(String name)
	{
		Object player;
		
		for(int a = 0; a < worldObj.playerEntities.size(); a++)
		{
			player = (EntityPlayer)worldObj.playerEntities.get(a);
			
			if(player != null && player instanceof EntityPlayer && ((EntityPlayer)player).getCommandSenderName().equals(name))
			{
				return (EntityPlayer)player;
			}
		}
		
		return null;
	}
	
	public void doJump()
	{
		this.jump();
	}
	
	
	@Override
	protected void jump()
	{
		this.motionY = Math.max(0.42, 0.42 + (this.preciseScale - this.defaultScale) * 0.2);

        if (this.isPotionActive(Potion.jump))
        {
            this.motionY += (double)((float)(this.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting())
        {
            float f = this.rotationYaw * 0.017453292F;
            this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
            this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
        }

        this.isAirBorne = true;
        ForgeHooks.onLivingJump(this);
	}

	public String getOwnerName()
	{
		return ownerName;
	}
	//==================================================================================================================
	//TODO Items & Experience
	//==================================================================================================================
	private void pickupNearbyItems()
	{
		if(!this.isEntityAlive() || !this.getOptions().pickUp.get())
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
				ItemStack picked = pickupItem(eItem.getEntityItem());
				
				if(picked != null)
				{
					this.getFetchAI().pickedUpItem(picked);
					
					if((picked.getItem() == Items.milk_bucket && picked.stackSize > 0) && 
							(this.getShrinkCooldown() > 0 || this.aimForScale > this.defaultScale))
					{
						this.forceEatFood(this.inventory.getSlotForItem(Items.milk_bucket));
					}
				}
				
				if(eItem.getEntityItem().stackSize == 0)
				{
					worldObj.removeEntity(eItem);
				}
			}
		}
	}
	
	public ItemStack pickupItem(ItemStack stack)
	{
		if(stack.stackSize > 0 && stack.getItem() instanceof ItemArmor)
		{
			ItemArmor item = (ItemArmor)stack.getItem();
			int armorSlot = 3-item.armorType;
			ItemStack slotArmor = inventory.armorItemInSlot(armorSlot);
			
			if(slotArmor == null)
			{
				ItemStack pickedUp = stack.copy();
				inventory.setArmour(armorSlot, stack.copy());
				stack.stackSize = 0;
				worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				return pickedUp;
			}
			else if(slotArmor.getItem() instanceof ItemArmor && isBetterArmorType(stack, slotArmor, item, (ItemArmor)slotArmor.getItem()) && inventory.canFullyFit(slotArmor))
			{
				ItemStack pickedUp = stack.copy();
				inventory.tryFitInInventory(slotArmor);
				inventory.setArmour(armorSlot, stack.copy());
				stack.stackSize = 0;
				worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				return pickedUp;
			}
		}
		
		int removed = inventory.tryFitInInventory(stack);
		
		if(removed > 0)
		{
			worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
		}
		
		ItemStack pickedUp = stack.copy();
		pickedUp.stackSize = removed;
		return pickedUp;
	}

	public boolean isBetterArmorType(ItemStack stackBetterDur, ItemStack thanMe, ItemArmor isBetterThan, ItemArmor me)
	{
		if(getArmourDamageReduction(stackBetterDur, isBetterThan) <= getArmourDamageReduction(thanMe, me))
		{
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

	
	public void goFetchItem(ItemStack fetch, Notifier notifier)
	{
		this.aiFetch.setNotifier(notifier);
		this.aiFetch.fetchItem(fetch);
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
		if(slot == 0)
		{
			inventory.setInventorySlotContents(this.inventory.currentItem, stack);
		}
		else
		{
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
	
	public ItemStack getOfferedItem()
	{
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

	public void setName(String uneditedName)
	{
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
	public void updateLocalSkin(String editedName)
	{
		//Checks to make sure the local skin actually exists. If it doesn't it sets the currentResource to the default (steve) resource

		currentResource = new ResourceLocation("textures/" + editedName);
		SimpleTexture tex = new SimpleTexture(currentResource);
		
		try
		{
			tex.loadTexture(Minecraft.getMinecraft().getResourceManager());
		}
		catch(Exception e)
		{
			currentResource = defaultResource;
		}
	}

	@SideOnly(value = Side.CLIENT)
	public void updateSkin(String username)
	{
		ResourceLocation resource = new ResourceLocation("skins/" + username);
		TextureManager texturemanager = Minecraft.getMinecraft().getTextureManager();
		ITextureObject object = texturemanager.getTexture(resource);
		
		if(object == null || !(object instanceof ThreadDownloadImageData))
		{
			object = new ThreadDownloadImageData(null, "http://skins.minecraft.net/MinecraftSkins/" + username + ".png", NameRegistry.getDefaultSkinForClone(this), new ImageBufferDownload());
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
		if(currentResource != null)
		{
			return currentResource;
		}
		return defaultResource;
	}


	//==================================================================================================================
	//TODO Item Using
	//==================================================================================================================
	
	boolean isItemInUse = false;
	
	public void updateUsingItem()
	{
		if (this.itemInUse != null)
        {
			
            ItemStack itemstack = this.getHeldItem();

            if (itemstack == this.itemInUse)
            {
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
		
		if(worldObj.isRemote)
		{
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
	
	public void swingItem()
    {
        super.swingItem();
        if(this.worldObj.isRemote && this.leftBParticle != null && this.rightBParticle != null)
        {
        	this.leftBParticle.velY += 50f;
        	this.rightBParticle.velY -= 50f;
        }
    }
	
	/**
     * Gets the Icon Index of the item currently held
     */
    @SideOnly(Side.CLIENT)
    public IIcon getItemIcon(ItemStack p_70620_1_, int p_70620_2_)
    {
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
	protected boolean canDespawn()
    {
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
            
            if(!worldObj.isRemote && this.itemInUse.getItemUseAction() == EnumAction.bow)
            {
            	shootBow(this.itemInUse);
            }

            if(this.itemInUse.getItem() == Items.milk_bucket && this.aimForScale > this.defaultScale)
            {
            	this.aimForScale = this.defaultScale;
            	this.setShrinkCooldown(0);
            }
            
            this.clearItemInUse();
        }
    }
    
    //==================================================================================================================
    //TODO Experience
    //==================================================================================================================
    
	public void transferXP(EntityPlayer player)
	{
		player.addExperience(experienceTotal);
		experience = 0;
		experienceLevel = 0;
		experienceTotal = 0;
	}
	//Experience
	
	int xpCooldown = 0;
	
	public void updateExperience()
	{
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

	public void addExperience(int par1)
	{
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
		this.setDead();
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
	
	@SideOnly(value = Side.CLIENT)
	@Override
	public boolean canRenderContinue(Renderable r)
	{
		return this.isEntityAlive() && this.worldObj == Minecraft.getMinecraft().theWorld;
	}
	
	public final static int ID_OPTIONS = 12;
	
	@Override
	public void onRemoved() 
	{
		
	}
	
	public void say(String string, int radius)
	{
		this.say(string, radius, EnumChatFormatting.WHITE);
	}

	public void say(String string, int radius, EnumChatFormatting color)
	{
		ChatComponentText chat = new ChatComponentText("<" + this.getName() + "> " + string);
		chat.setChatStyle(new ChatStyle().setColor(color));
		List lstPlayers = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.boundingBox.expand(radius, radius, radius));
		
		for(int a = 0; a < lstPlayers.size(); a++)
		{
			if(lstPlayers.get(a) instanceof EntityPlayer)
			{
				((EntityPlayer)lstPlayers.get(a)).addChatMessage(chat);
			}
		}
		
		this.setDisplayMessage(string);
		this.setDisplayMessageColour(0xffffffff);
		this.setDisplayMessageCooldown(40 + string.length() * 2);
	}

	public void say(String string, EntityPlayer... sender)
	{
		ChatComponentText chat = new ChatComponentText("<" + this.getName() + "> " + string);
		
		if(sender != null)
		{
			for(int a = 0; a < sender.length; a++)
			{
				sender[a].addChatComponentMessage(chat);
			}
		}
		else
		{
			for(int a = 0; a < this.worldObj.playerEntities.size(); a++)
			{
				((EntityPlayer)this.worldObj.playerEntities.get(a)).addChatMessage(chat);
			}
		}
		
		this.setDisplayMessage(string);
		this.setDisplayMessageColour(0xffffffff);
		this.setDisplayMessageCooldown(40 + string.length() * 2);
		
	}

	public boolean canSeeBlock(ChunkCoordinates cc, Block break_block)
	{
		Vector from = Vector.fromVec3(Vec3.createVectorHelper(this.posX, this.posY, this.posZ)).add(new Vector(0, this.getEyeHeight(),0));
		Vector to = new Vector(cc.posX+0.5, cc.posY+0.5, cc.posZ+0.5);
		
		ChunkCoordinates[] collisions = RayTrace.rayTraceBlocks(from, to, worldObj);
		
		if(collisions == null || collisions.length == 0)
		{
			return true;
		}
		
		for(int a = 0; a < collisions.length; a++)
		{
			if(collisions[a].posX == cc.posX && collisions[a].posY == cc.posY && collisions[a].posZ == cc.posZ)
			{
				continue;
			}
			
			
			Block atPos = worldObj.getBlock(collisions[a].posX, collisions[a].posY, collisions[a].posZ);
			
			if(!isBlockSeeThru(atPos, collisions[a]))
			{
				
				return false;
			}
		}
		return true;
	}
	

	
	static Material[] seeThruMaterials = new Material[]{
			Material.glass,
			Material.ice,
			Material.leaves,
			Material.portal,
			Material.water,
			Material.vine,
			Material.web,
	};
	
	public static boolean isBlockSeeThru(Block block, ChunkCoordinates cc)
	{
		Material m = block.getMaterial();
		
		if(block.isOpaqueCube() || m.isOpaque())
		{
			return false;
		}
		
		return isMaterial(seeThruMaterials, m);
	}
	
	public static boolean isMaterial(Material[] materials, Material material)
	{
    	for(int a = 0; a < materials.length; a++)
    	{
    		if(material == materials[a])
    		{
    			return true;
    		}
    	}
    	return false;
    }

	


	


	
}
