package net.jamezo97.clonecraft;

import java.util.List;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.entity.EntitySpawnEgg;
import net.jamezo97.clonecraft.tileentity.TileEntityCentrifuge;
import net.jamezo97.clonecraft.tileentity.TileEntityLifeInducer;
import net.jamezo97.clonecraft.tileentity.TileEntitySterilizer;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {

	
	
	public void load(CloneCraft craft){
		
		
		
		NetworkRegistry.INSTANCE.registerGuiHandler(craft, new GuiHandler());

		EntityRegistry.registerModEntity(EntitySpawnEgg.class, "EntitySpawnEgg", 0, craft, 80, 10, true);
		
		EntityRegistry.registerModEntity(EntityClone.class, "EntityClone", 1, craft, 512, 2, true);
		
		GameRegistry.registerTileEntity(TileEntitySterilizer.class, "Sterilizer");
		
		GameRegistry.registerTileEntity(TileEntityCentrifuge.class, "Centrifuge");
		
		GameRegistry.registerTileEntity(TileEntityLifeInducer.class, "LifeInducer");
		
		
		
		
	}

	public void postInit(CloneCraft cloneCraft) {
		Reflect.init(Side.SERVER);
		
		BlockDispenser.dispenseBehaviorRegistry.putObject(cloneCraft.itemSpawnEgg, new BehaviorDefaultDispenseItem()
        {
		    /**
		     * Dispense the specified stack, play the dispense sound and spawn particles.
		     */
		    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack)
		    {
		        World world = blockSource.getWorld();
		        IPosition iposition = BlockDispenser.func_149939_a(blockSource);
		        EnumFacing enumfacing = BlockDispenser.func_149937_b(blockSource.getBlockMetadata());
		        
		        EntitySpawnEgg iprojectile = new EntitySpawnEgg(blockSource.getWorld(), iposition.getX(), iposition.getY(), iposition.getZ(), stack);
		        
		        
		        
//		        IProjectile iprojectile = this.getProjectileEntity(world, iposition);
		        iprojectile.setThrowableHeading((double)enumfacing.getFrontOffsetX(), (double)((float)enumfacing.getFrontOffsetY() + 0.1F), (double)enumfacing.getFrontOffsetZ(), this.func_82500_b(), this.func_82498_a());
		        world.spawnEntityInWorld((Entity)iprojectile);
		        stack.splitStack(1);
		        return stack;
		    }

		    /**
		     * Play the dispense sound from the specified block.
		     */
		    protected void playDispenseSound(IBlockSource p_82485_1_)
		    {
		        p_82485_1_.getWorld().playAuxSFX(1002, p_82485_1_.getXInt(), p_82485_1_.getYInt(), p_82485_1_.getZInt(), 0);
		    }


		    protected float func_82498_a()
		    {
		        return 6.0F;
		    }

		    protected float func_82500_b()
		    {
		        return 1.1F;
		    }
        });
		
		/*EntityRegistry.*/addSpawn(EntityClone.class, 1, 1, 10, EnumCreatureType.creature, BiomeGenBase.getBiomeGenArray());
	}
	
	
	
	public static void addSpawn(Class <? extends EntityLiving > entityClass, int weightedProb, int min, int max, EnumCreatureType typeOfCreature, BiomeGenBase... biomes)
    {
        for (BiomeGenBase biome : biomes)
        {
        	if(biome == null)
        	{
        		continue;
        	}
        	
            @SuppressWarnings("unchecked")
            List<SpawnListEntry> spawns = biome.getSpawnableList(typeOfCreature);

            for (SpawnListEntry entry : spawns)
            {
                //Adjusting an existing spawn entry
                if (entry.entityClass == entityClass)
                {
                    entry.itemWeight = weightedProb;
                    entry.minGroupCount = min;
                    entry.maxGroupCount = max;
                    break;
                }
            }

            spawns.add(new SpawnListEntry(entityClass, weightedProb, min, max));
        }
    }
	
}
