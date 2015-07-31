package net.jamezo97.clonecraft;

import java.io.File;

import net.jamezo97.clonecraft.block.BlockAntenna;
import net.jamezo97.clonecraft.block.BlockCentrifuge;
import net.jamezo97.clonecraft.block.BlockLifeInducer;
import net.jamezo97.clonecraft.block.BlockSterilizer;
import net.jamezo97.clonecraft.build.BlockItemRegistry;
import net.jamezo97.clonecraft.build.BlockLists;
import net.jamezo97.clonecraft.build.CustomBuilders;
import net.jamezo97.clonecraft.build.RotationMapping;
import net.jamezo97.clonecraft.clone.ai.ImportantBlockRegistry;
import net.jamezo97.clonecraft.item.ItemEmptyEgg;
import net.jamezo97.clonecraft.item.ItemNeedle;
import net.jamezo97.clonecraft.item.ItemRotateMapper;
import net.jamezo97.clonecraft.item.ItemSpawnEgg;
import net.jamezo97.clonecraft.item.ItemTestTube;
import net.jamezo97.clonecraft.item.ItemWoodStaff;
import net.jamezo97.clonecraft.network.PacketHandler;
import net.jamezo97.clonecraft.recipe.RecipeClearDNAItem;
import net.jamezo97.clonecraft.recipe.RecipeEmptyEggToSpawnEgg;
import net.jamezo97.clonecraft.recipe.RecipeNeedleTestTubeRecipe;
import net.jamezo97.clonecraft.recipe.RecipeStemCells;
import net.jamezo97.clonecraft.recipe.RecipeTestTubeNeedle;
import net.jamezo97.clonecraft.schematic.SchematicList;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.RecipeSorter;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = CloneCraft.MODID, version = CloneCraft.VERSION, name = CloneCraft.NAME)
public class CloneCraft {

	/*
	 * As chunks are generated, randomly create 'towns'.
	 * While the player is near a town, make the clones do stuff, make it look like a town.
	 * When player leaves vicinity, town stops.
	 * When loaded again, determine how much time has passed, and change accordingly, adding new buildings
	 * new clones, new roads, new mines
	 * Start off with just one family inside a house
	 * Add crops, more houses
	 * Add farms, animal pens, church, mineshaft, town center, shops
	 * As more families procreate, build more houses. Type of houses depend on biome
	 * If you attack a town clone, other will come to the rescue
	 * 
	 * Eventually add sounds, make them talk?
	 * 
	 * 
	 * 
	 * 
	 * Extend Wander ability. If multiple tries to wander somewhere fail, check if stuck. 
	 * If stuck, and the 'Mine' and 'Pickup items' options are turned on, teleport out of the hole. Or maybe dig the surrounding blocks
	 * to escape, if they are stone.
	 * 
	 * 
	 * */
	
	
	final static String MODID = "clonecraft";
	final static String VERSION = "3.0";
	final static String NAME = "CloneCraft";
	
	@SidedProxy(modId = "clonecraft", clientSide="net.jamezo97.clonecraft.ClientProxy", serverSide="net.jamezo97.clonecraft.CommonProxy")
	public static CommonProxy proxy;
	
	public static CloneCraft INSTANCE;
	public static CloneCraftCreativeTabAll creativeTabAll;
	public static CloneCraftCreativeTab creativeTab;
	
	
	public CloneCraft()
	{
		CloneCraft.INSTANCE = this;
		creativeTab = new CloneCraftCreativeTab("clonecraft");
		creativeTabAll = new CloneCraftCreativeTabAll("clonecraftAll");
	}
	
	
	public ItemNeedle itemNeedle;
	public ItemTestTube itemTestTube;
	public ItemEmptyEgg itemEmptyEgg;
	public ItemSpawnEgg itemSpawnEgg;
	
	public ItemWoodStaff itemWoodStaff;
	public ItemRotateMapper itemRotateMapper;
	
	public BlockSterilizer blockSterilizer;
	public BlockCentrifuge blockCentrifuge;
	public BlockLifeInducer blockLifeInducer;
	public BlockAntenna blockAntenna;
	
	public CConfig config = null;
	
	public SchematicList schematicList = null;
	
	
	
	public void initItemsAndBlocks()
	{
		itemNeedle = (ItemNeedle) new ItemNeedle().setTextureName("clonecraft:needle").setUnlocalizedName("ccNeedle");
		GameRegistry.registerItem(itemNeedle, config.ID_NEEDLE);
		
		itemTestTube = (ItemTestTube) new ItemTestTube().setTextureName("clonecraft:testTube").setUnlocalizedName("ccTestTube");
		GameRegistry.registerItem(itemTestTube, config.ID_TESTTUBE);
		
		itemEmptyEgg = (ItemEmptyEgg) new ItemEmptyEgg().setTextureName("clonecraft:spawnEggOut").setUnlocalizedName("ccEmptyEgg");
		GameRegistry.registerItem(itemEmptyEgg, config.ID_EMPTYEGG);
		
		itemSpawnEgg = (ItemSpawnEgg) new ItemSpawnEgg().setUnlocalizedName("ccSpawnEgg");
		GameRegistry.registerItem(itemSpawnEgg, config.ID_SPAWNEGG);
		
		itemWoodStaff = (ItemWoodStaff)new ItemWoodStaff().setTextureName("clonecraft:woodStaff").setUnlocalizedName("ccWoodStaff");
		GameRegistry.registerItem(itemWoodStaff, config.ID_WOODSTAFF);
		
		itemRotateMapper = (ItemRotateMapper)new ItemRotateMapper().setTextureName("clonecraft:rotateMapper").setUnlocalizedName("ccRotateMapper");
		GameRegistry.registerItem(itemRotateMapper, config.ID_ROTATE);
		itemRotateMapper.setCreativeTab(creativeTab);
		
		
		blockSterilizer = (BlockSterilizer) new BlockSterilizer().setBlockName("sterilizer").setStepSound(Block.soundTypeMetal).setHardness(3.5f).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(blockSterilizer, config.ID_STERILIZER);
		
		blockCentrifuge = (BlockCentrifuge) new BlockCentrifuge().setBlockName("centrifuge").setStepSound(Block.soundTypeMetal).setHardness(3.5f).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(blockCentrifuge, config.ID_CENTRIFUGE);
		
		blockLifeInducer = (BlockLifeInducer) new BlockLifeInducer().setBlockName("lifeInducer").setStepSound(Block.soundTypeMetal).setHardness(3.5f).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(blockLifeInducer, config.ID_LIFEINDUCER);
		
		blockAntenna = (BlockAntenna) new BlockAntenna().setBlockName("antenna").setStepSound(Block.soundTypeMetal).setHardness(3.5f).setCreativeTab(creativeTab);
		GameRegistry.registerBlock(blockAntenna, config.ID_ANTENNA);
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		this.dataDirectory = new File(proxy.getBaseFolder(), "CloneCraft");
		
		if(!this.dataDirectory.exists())
		{
			this.dataDirectory.mkdirs();
		}

		config = new CConfig(event.getSuggestedConfigurationFile());
		
		
		
		initItemsAndBlocks();
		
		
		proxy.preInit(this);
	}
	
	File dataDirectory;
	
	public File getDataDir()
	{
		return dataDirectory;
	}
	
	CCEventListener eventListener;
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init(this);
		
		PacketHandler.initPackets();
		
		FMLCommonHandler.instance().bus().register(eventListener = new CCEventListener());
		MinecraftForge.EVENT_BUS.register(eventListener);
		loadRecipes();
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		proxy.postInit(this);
		CCEntityList.initEntities();
		schematicList = new SchematicList(this);
		BlockLists.init();
		CustomBuilders.init();
		BlockItemRegistry.init();
		
		RotationMapping.init();

	}
	
	
	public void loadRecipes()
	{
		GameRegistry.addRecipe(new ItemStack(itemNeedle), new Object[]{
			"XXX", "X X", " Y ", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('Y'), Items.gold_ingot
		});
		GameRegistry.addRecipe(new ItemStack(itemTestTube, 2), new Object[]{
			"X X", "X X", "XXX", Character.valueOf('X'), Blocks.glass_pane
		});

		GameRegistry.addRecipe(new ItemStack(itemTestTube, 6), new Object[]{
			"X X", "X X", "XXX", Character.valueOf('X'), Blocks.glass
		});

		GameRegistry.addRecipe(new ItemStack(blockCentrifuge), new Object[]{
			"XYX", "YDY", "XYX", Character.valueOf('X'), Blocks.piston, Character.valueOf('Y'), Items.iron_ingot, Character.valueOf('D'), Items.diamond
		});
		GameRegistry.addRecipe(new ItemStack(blockSterilizer), new Object[]{
			"X X", "XYX", "XZX", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('Y'), Items.bucket, Character.valueOf('Z'), Items.redstone
		});
		GameRegistry.addRecipe(new ItemStack(blockLifeInducer), new Object[]{
			"XWX", "XYX", "XZX", Character.valueOf('W'), Items.gold_ingot, Character.valueOf('X'), Items.iron_ingot, Character.valueOf('Y'), Blocks.soul_sand, 
			Character.valueOf('Z'), Blocks.chest
		});
		GameRegistry.addShapedRecipe(new ItemStack(blockAntenna, 9), new Object[]{
			"XYX", "XYX", "XYX", Character.valueOf('X'), Items.iron_ingot, Character.valueOf('Y'), Items.gold_ingot
		});
		
		GameRegistry.addShapedRecipe(new ItemStack(itemWoodStaff, 1), new Object[]{
			"  Y", " X ", "X  ", Character.valueOf('X'), Items.stick, Character.valueOf('Y'), Blocks.planks
		});
		
		/*GameRegistry.addShapelessRecipe(new ItemStack(this.itemGrowBall), new Object[]{
			//Melon, BoneMeal, Brown Mushroom, Charcoal, Jungle Leaves
			Items.melon, new ItemStack(Items.dye, 1, 15), Blocks.brown_mushroom, new ItemStack(Items.coal, 1, 1), new ItemStack(Blocks.leaves, 1, 3)
		});*/
		//Ink, Slime, furnace, diamond, iron, gold
//		GameRegistry.addRecipe(new ItemStack(dnaSequencer), new Object[]{
//			"ABA", "ACA", "ADA", 
//			Character.valueOf('A'), Items.ingotIron, 
//			Character.valueOf('B'), new ItemStack(Items.dyePowder, 1, 0), 
//			Character.valueOf('C'), Items.slimeBall, 
//			Character.valueOf('D'), Blocks.furnaceIdle, 
//		});
//		GameRegistry.addRecipe(new ItemStack(geneExtractor), new Object[]{
//			"ABC", "BDB", "EFG", 
//			Character.valueOf('A'), Items.flint, 
//			Character.valueOf('B'), Items.redstone, 
//			Character.valueOf('C'), Items.bucketEmpty, 
//			Character.valueOf('D'), Blocks.blockIron, 
//			Character.valueOf('E'), new ItemStack(needle, 1, 0), 
//			Character.valueOf('F'), Items.diamond, 
//			Character.valueOf('G'), Blocks.furnaceIdle
//		});
		//Flint, Bucket, Needle, Redstone, Diamond, Furnace, IronBlock
		GameRegistry.addShapelessRecipe(new ItemStack(itemEmptyEgg), new Object[]{
			Items.egg
		});
		
		
//		GameRegistry.registerCraftingHandler(new CloneCraftCraftingHandler());

		GameRegistry.addRecipe(new RecipeNeedleTestTubeRecipe());
		GameRegistry.addRecipe(new RecipeTestTubeNeedle());
		GameRegistry.addRecipe(new RecipeClearDNAItem());
		GameRegistry.addRecipe(new RecipeEmptyEggToSpawnEgg());
		GameRegistry.addRecipe(new RecipeStemCells());

		RecipeSorter.register("clonecraft:needleToTube", RecipeNeedleTestTubeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped");
		RecipeSorter.register("clonecraft:TubeToDna", RecipeTestTubeNeedle.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped");
		RecipeSorter.register("clonecraft:clearData", RecipeClearDNAItem.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped");
		RecipeSorter.register("clonecraft:emptyEgg", RecipeEmptyEggToSpawnEgg.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped");
		RecipeSorter.register("clonecraft:stemCell", RecipeStemCells.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shaped");
	}
	
	public static boolean isGuiOpen() {
		return Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof net.minecraft.client.gui.GuiIngameMenu;
	}
	
	
	
	public NBTTagCompound saveWorldData(NBTTagCompound nbt)
	{
//		CCBlockList.saveIdToId(nbt);
		
		return nbt;
	}
	
	public void loadWorldData(NBTTagCompound nbt)
	{
		ImportantBlockRegistry.determineImportantBlocks();
//		CCBlockList.loadIdToId(nbt);
		
		
	}
	
}
