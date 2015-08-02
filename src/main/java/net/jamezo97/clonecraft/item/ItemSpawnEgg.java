package net.jamezo97.clonecraft.item;

import java.util.List;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.CloneCraftHelper;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.jamezo97.clonecraft.entity.EntitySpawnEgg;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemSpawnEgg extends Item{

	IIcon inside;
	IIcon insideStripe;

	public ItemSpawnEgg() {
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		this.setCreativeTab(CloneCraft.INSTANCE.creativeTabAll);
	}

	@Override
	public void registerIcons(IIconRegister ir)
	{
		insideStripe = ir.registerIcon("CloneCraft:spawnEggInStripe");
	}

	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	
	
	@Override
	public int getRenderPasses(int metadata)
	{
		return 3;
	}

	@Override
	public String getItemStackDisplayName(ItemStack par1ItemStack)
	{
		ItemData dna = new ItemData(par1ItemStack);
		return (par1ItemStack.getItemDamage()==1?"":StatCollector.translateToLocal("cc.unactivated") + " ") + "\247" + CloneCraftHelper.getClosestColourChar(dna.getPrimaryColour()) + dna.getCurrentEntityNameTrans() + "\247f " + StatCollector.translateToLocal("cc.item.spawnegg.name");
	}



	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass)
	{
		if(pass == 1)
		{
			if(par1ItemStack.getItemDamage() == 0)
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2)
	{
		if(par2 == 0)
		{
			return 0xffffffff;
		}
		else if(par2 == 1)
		{
			return new ItemData(par1ItemStack).getPrimaryColour();
		}
		else if(par2 == 2)
		{
			return new ItemData(par1ItemStack).getSecondaryColour();
		}
		return 0xffff00ff;
	}

	@Override
	public IIcon getIcon(ItemStack stack, int pass)
	{
		if(pass == 1)
		{
			return inside;
		}
		else if(pass == 2)
		{
			return insideStripe;
		}
		return itemIcon;
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int damage, int pass)
	{
		if(pass == 1)
		{
			return inside;
		}
		else if(pass == 2)
		{
			return insideStripe;
		}
		return itemIcon;
	}

	public void setIcons(IIcon outside, IIcon inside)
	{
		this.inside = inside;
		this.itemIcon = outside;
	}

	@Override
	public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) 
	{
		if (par3World.isRemote || par1ItemStack.getItemDamage() == 0)
        {
            return true;
        }
        else
        {
            Block block = par3World.getBlock(par4, par5, par6);
            par4 += Facing.offsetsXForSide[par7];
            par5 += Facing.offsetsYForSide[par7];
            par6 += Facing.offsetsZForSide[par7];
            float addY = 0;
            if(EntityTNTPrimed.class == CCEntityList.idToClass.get(new ItemData(par1ItemStack).getId()))
            {
            	addY = 0.5f;
            }
            double d0 = 0.0D;

            if (par7 == 1 && block.getRenderType() == 11)
            {
                d0 = 0.5D;
            }
            
            Entity entity = new ItemData(par1ItemStack).spawnEntity(par4 + 0.5, par5+addY, par6 + 0.5, par3World);
            
            if(entity instanceof EntityClone)
            {
				((EntityClone)entity).onSpawnedBy(par2EntityPlayer.getCommandSenderName());
			}
            
            par3World.playSoundAtEntity(entity, "clonecraft:general.pop", 1.0f, 1.0f);
            
            if (entity != null)
            {
                if (entity instanceof EntityLivingBase && par1ItemStack.hasDisplayName())
                {
                    ((EntityLiving)entity).setCustomNameTag(par1ItemStack.getDisplayName());
                }

                if (!par2EntityPlayer.capabilities.isCreativeMode)
                {
                    --par1ItemStack.stackSize;
                }
            }

            return true;
        }
	}

	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
	{
		if (!par2World.isRemote){
			if(par1ItemStack.getItemDamage() == 1){
				

				par2World.playSoundAtEntity(par3EntityPlayer, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));


				EntitySpawnEgg e = new EntitySpawnEgg(par2World, par3EntityPlayer, par1ItemStack, par3EntityPlayer.getCommandSenderName());
				
				par2World.spawnEntityInWorld(e);

				if (!par3EntityPlayer.capabilities.isCreativeMode)
				{
					--par1ItemStack.stackSize;
				}
				
			}
		}

		return par1ItemStack;
	}

	@Override
	public void getSubItems(Item item, CreativeTabs par2CreativeTabs, List list)
	{
		ItemData data = new ItemData();
		ItemStack egg;
		
		for(Entry<Integer, Class> entry : CCEntityList.idToClass.entrySet())
		{
			int id = entry.getKey();
			Class entityClass = entry.getValue();
			if(((EntityLiving.class.isAssignableFrom(entityClass) || 
					CloneCraftHelper.isValid(CCEntityList.classToString, entityClass))) && 
					!CloneCraftHelper.isInvalid(CCEntityList.classToString, entityClass))
			{
				data.fill(id);
				egg = new ItemStack(item, 1, 1);
				data.save(egg);
				list.add(egg);
			}
		}


	}





}
