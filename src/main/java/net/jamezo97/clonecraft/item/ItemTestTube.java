package net.jamezo97.clonecraft.item;

import java.util.List;
import java.util.Map.Entry;

import net.jamezo97.clonecraft.CCEntityList;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.CloneCraftHelper;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;

public class ItemTestTube extends Item{
	
	IIcon blood, separated;
	
	public ItemTestTube()
	{
		this.setHasSubtypes(true);
		this.setMaxStackSize(64);
		setCreativeTab(CreativeTabs.tabAllSearch);
	}
	
	@Override
	public boolean requiresMultipleRenderPasses()
	{
		return true;
	}

	@Override
	public int getColorFromItemStack(ItemStack stack, int pass)
	{
		if(stack.getItemDamage() == 0 || pass == 0)
		{
			return 0xffffffff;
		}
		else
		{
			ItemData data = new ItemData(stack);
			return data.getPrimaryColour();
		}
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack)
	{
		ItemData data = new ItemData(stack);
		
		if(stack.getItemDamage() == 0)
		{
			return (data.isDirty()?StatCollector.translateToLocal("cc.dirty"):StatCollector.translateToLocal("cc.empty")) + " " + StatCollector.translateToLocal("cc.item.testTube.name");
		}
		else
		{
			String name = data.getCurrentEntityNameTrans();
			
			if(name != null)
			{
				if(stack.getItemDamage() == 1)
				{
					return name + " " + StatCollector.translateToLocal("cc.blood");
				}
				else if(stack.getItemDamage() == 2)
				{
					return StatCollector.translateToLocal("cc.separated") + " " + name + " " + StatCollector.translateToLocal("cc.blood");
				}
			}
		}
		return "Broken Testube, Sorry!! D:";
	}

	@Override
	public void getSubItems(Item item, CreativeTabs tab, List list)
	{
		if(tab == null || tab == CloneCraft.creativeTab)
		{
			list.add(new ItemStack(item, 1, 0));
		}
		
		if(tab == null || tab == CloneCraft.creativeTabAll)
		{
			ItemData data = new ItemData();
			ItemStack stack;
			
			data.isDirty = true;
			data.empty();
			stack = new ItemStack(item, 1, 0);
			data.save(stack);
			list.add(stack);
			
			/*data.fill(0);
			
			stack = new ItemStack(item, 1, 1);
			data.save(stack);
			list.add(stack);

			stack = new ItemStack(item, 1, 2);
			data.save(stack);
			list.add(stack);*/
			
			
			
			
			for(Entry<Integer, Class> entry : CCEntityList.idToClass.entrySet())
			{
				int id = entry.getKey();
				Class entityClass = entry.getValue();
				
				if(((EntityLiving.class.isAssignableFrom(entityClass) || 
						CloneCraftHelper.isValid(CCEntityList.classToString, entityClass))) && 
						!CloneCraftHelper.isInvalid(CCEntityList.classToString, entityClass))
				{
					
					data.fill(id);
					
					//Blood
					stack = new ItemStack(item, 1, 1);
					data.save(stack);
					list.add(stack);
					
					//Separated
					stack = new ItemStack(item, 1, 2);
					data.save(stack);
					list.add(stack);
				}
			}
		}
	}

	@Override
	public IIcon getIconFromDamageForRenderPass(int dam, int pass)
	{
		if(pass == 0)
		{
			return this.itemIcon;
		}
		else
		{
			if(dam == 0)
			{
				return this.itemIcon;
			}
			else if(dam == 1)
			{
				return this.blood;
			}
			else if(dam == 2)
			{
				return this.separated;
			}
		}
		return this.itemIcon;
	}

	@Override
	public CreativeTabs[] getCreativeTabs()
	{
		return new CreativeTabs[]{CloneCraft.creativeTab, CloneCraft.creativeTabAll};
	}

	@Override
	public void registerIcons(IIconRegister ir)
	{
		super.registerIcons(ir);
		this.blood = ir.registerIcon("CloneCraft:testTubeBlood");
		this.separated = ir.registerIcon("CloneCraft:testTubeSeparated");
	}

	
	
	
}
