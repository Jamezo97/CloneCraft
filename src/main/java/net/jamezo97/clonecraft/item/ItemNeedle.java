package net.jamezo97.clonecraft.item;

import java.util.List;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.CloneCraftHelper;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemNeedle extends Item{

	IIcon bloodIcon;
	IIcon DNAIcon;
	IIcon stemCellIcon;
	
	public ItemNeedle() {
		super();
		this.setMaxStackSize(64);
		this.setHasSubtypes(true);
		setCreativeTab(CreativeTabs.tabAllSearch);
	}
	
	public CreativeTabs[] getCreativeTabs()
    {
        return new CreativeTabs[]{CloneCraft.INSTANCE.creativeTab, CloneCraft.INSTANCE.creativeTabAll};
    }
	
	
    public String getItemStackDisplayName(ItemStack stack)
    {
    	int type = stack.getItemDamage();
    	ItemData data = new ItemData(stack);
    	String s = StatCollector.translateToLocal("cc.item.needle.name");
    	
    	if(type == 0)
    	{
    		if(data.isDirty())
    		{
        		s = StatCollector.translateToLocal("cc.dirty") + " " + s;
    		}
    		else
    		{
        		s = StatCollector.translateToLocal("cc.empty") + " " + s;
    		}
    	}
    	else if(type == 1)
    	{
    		if(data.id != -1)
    		{
        		return data.getCurrentEntityNameTrans() + " " + StatCollector.translateToLocal("cc.blood");
    		}
    	}
    	else if(type == 2)
    	{
    		if(data.id != -1)
    		{
        		return data.getCurrentEntityNameTrans() + " " + StatCollector.translateToLocal("cc.dna");
    		}
    	}
    	else if(type == 3)
    	{
    		return "Human Stem Cells";
    	}
    	return s;
    }

    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass)
    {
    	if(pass == 0 || stack.getItemDamage() == 0)
    	{
    		return 0xffffffff;
    	}
    	else
    	{
    		if(stack.getItemDamage() == 3)
    		{
    			return 0xff9933ee;
    		}
    		else
    		{
        		return new ItemData(stack).getPrimaryColour();
    		}
    	}
    }

    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        return true;
    }
    
    

    @Override
	public boolean hitEntity(ItemStack par1ItemStack, EntityLivingBase hit, EntityLivingBase playerParam)
    {
    	if(playerParam instanceof EntityPlayer)
    	{
    		EntityPlayer player = (EntityPlayer)playerParam;
    		if(par1ItemStack.getItemDamage() == 3)
    		{
    			if(hit instanceof EntityClone)
    			{
    				((EntityClone)hit).injectStemcells(!player.isSneaking());
    				player.worldObj.playSoundAtEntity(hit, "clonecraft:needle.inject", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) +  0.5F);
    				
    				if(!hit.worldObj.isRemote)
    				{
    					if(par1ItemStack.stackSize == 1 && !player.capabilities.isCreativeMode)
        				{
        					new ItemData(par1ItemStack).empty().save(par1ItemStack);
        					par1ItemStack.setItemDamage(0);
        				}
        				else if(par1ItemStack.stackSize > 1)
        				{
        					par1ItemStack.stackSize--;
        					ItemStack newStack = par1ItemStack.copy();
        					newStack.stackSize = 1;
        					new ItemData(newStack).empty().save(newStack);
        					newStack.setItemDamage(0);
        					
        					CloneCraftHelper.addToInventory(player.inventory, 0, 36, newStack);
            				
            				if(newStack.stackSize > 0)
        					{
            					CloneCraftHelper.dropAtEntity(player, newStack.copy());
            				}
        				}
    				}
    				
    				
    				return false;
    			}
    		}
    		else
    		{
    			return tryFill(par1ItemStack, hit.worldObj, hit, player.inventory);
    		}
    		
    	}
    	return false;
	}

	/**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer par3EntityPlayer)
    {
    	if(par1ItemStack.getItemDamage() == 0)
    	{
    		tryFill(par1ItemStack, par2World, par3EntityPlayer, par3EntityPlayer.inventory);
    	}
    	
    	return par1ItemStack;
    }
    
    public boolean tryFill(ItemStack stackBase, World world, EntityLivingBase entity, InventoryPlayer inven){
    	//Stack to use
    	
    	if(world.isRemote)
    	{
    		return false;
    	}
    	
    	ItemStack stack = stackBase;
    	
    	if(stackBase.stackSize > 1)
    	{
    		stack = stackBase.copy();
    		stack.stackSize = 1;
    	}
    	
    	ItemData data = new ItemData(stack);
    	
    	if(!data.isDirty() && stack.getItemDamage() == 0)
    	{
    		data.fill(entity);
    		
    		if(data.getId() != -1)
    		{
    			world.playSoundAtEntity(entity, "clonecraft:needle.extract", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) +  0.5F);
    			
    			if(!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).capabilities.isCreativeMode)
    			{
        			entity.attackEntityFrom(DamageSource.generic, 2.0f);
    				entity.addPotionEffect(new PotionEffect(Potion.confusion.getId(), 400));
        			entity.addPotionEffect(new PotionEffect(Potion.weakness.getId(), 400));
    			}
    			
    			data.save(stack);
    			stack.setItemDamage(1);
    			
    			if(stack != stackBase)
    			{
    				stackBase.stackSize--;

    				CloneCraftHelper.addToInventory(inven, 0, 36, stack);
    				
    				if(stack.stackSize > 0)
					{
    					CloneCraftHelper.dropAtEntity(entity, stack);
    				}
    			}
    			return true;
    		}
    	}
    	return false;
    }


    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    

    @Override
	public int getRenderPasses(int metadata)
    {
		return 2;
	}


	/**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int dam, int pass)
    {
    	if(pass == 0)
    	{
            return this.itemIcon;
    	}
    	else if(pass == 1)
    	{
    		if(dam == 1)
    		{
    			return this.bloodIcon;
    		}
    		else if(dam == 2)
    		{
    			return this.DNAIcon;
    		}
    		else if(dam == 3)
    		{
    			return this.stemCellIcon;
    		}
    	}
    	return this.itemIcon;
    }

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		if (tab == CloneCraft.INSTANCE.creativeTabAll || tab == null)
		{
			list.add(new ItemData().setDirty().save(new ItemStack(item, 1, 0)));

			
			for (Entry<Integer, Class> entry : CCEntityList.idToClass.entrySet())
			{
				int id = entry.getKey();
				
				if (((EntityLiving.class.isAssignableFrom(entry.getValue()) || 
						CloneCraftHelper.isValid(CCEntityList.classToString, entry.getValue()))) && 
						!CloneCraftHelper.isInvalid(CCEntityList.classToString, entry.getValue()))
				{
					list.add(new ItemData().fill(id).save(new ItemStack(item, 1, 1)));
					list.add(new ItemData().fill(id).save(new ItemStack(item, 1, 2)));
				}
			}
			
		}
		if(tab == CloneCraft.creativeTab || tab == null)
		{
			list.add(new ItemStack(item, 1, 0));
			list.add(new ItemData().empty().setDirty().save(new ItemStack(item, 1, 3)));
		}
	}
	
	

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister ir)
    {
        super.registerIcons(ir);
        this.bloodIcon = ir.registerIcon("clonecraft:needleBlood");
        this.DNAIcon = ir.registerIcon("clonecraft:needleDNA");
        this.stemCellIcon = ir.registerIcon("clonecraft:needleStemcells");
    }
	

	
	
}
