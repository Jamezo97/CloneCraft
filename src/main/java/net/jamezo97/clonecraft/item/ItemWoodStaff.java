package net.jamezo97.clonecraft.item;

import net.jamezo97.clonecraft.ClientProxy;
import net.jamezo97.clonecraft.CloneCraft;
import net.jamezo97.clonecraft.gui.GuiSaveSchematic;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemWoodStaff extends Item{

	public ItemWoodStaff()
	{
		super();
		this.setCreativeTab(CloneCraft.creativeTab);
		
		
	}
	
	public ChunkCoordinates pos1, pos2;
	
	long useTimeLast = 0;

	@Override
	public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack)
	{
		if(entityLiving instanceof EntityPlayer && entityLiving.worldObj.isRemote && entityLiving.worldObj.getTotalWorldTime() != useTimeLast)
		{
			clientSwing(entityLiving);
		}
		return super.onEntitySwing(entityLiving, stack);
	}
	
	@SideOnly(value = Side.CLIENT)
	public void clientSwing(EntityLivingBase entityLiving)
	{
		if(Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectType.MISS)
		{
			int posX = (int) Math.floor(entityLiving.posX);
			int posY = (int) Math.floor(entityLiving.posY-1);
			int posZ = (int) Math.floor(entityLiving.posZ);
			clientClickBlock(posX, posY, posZ, (EntityPlayer)entityLiving);
		}
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int posX, int posY, int posZ, int side, float f1, float f2, float f3)
	{
		/*if(true && world.isRemote)
		{
			Block block = world.getBlock(posX, posY, posZ);
			int meta = world.getBlockMetadata(posX, posY, posZ);
			
			System.out.println(block + ", " + meta + ", " + Integer.toBinaryString(meta));
		}*/
		
		
		
		if(world.isRemote)
		{
			useTimeLast = world.getTotalWorldTime();
			clientClickBlock(posX, posY, posZ, player);
			return true;
		}
		return super.onItemUse(stack, player, world, posX, posY, posZ, side, f1, f2, f3);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
	{
		if(world.isRemote)
		{
			saveSchematic(player);
		}
		return super.onItemRightClick(stack, world, player);
	}
	
	boolean displayInfo = true;
	
	@SideOnly(value = Side.CLIENT)
	public void clientClickBlock(int posX, int posY, int posZ, EntityPlayer player)
	{
		boolean modify = ClientProxy.selectModifier.getIsKeyPressed();
		
		if(!modify && (pos1 == null || pos2 != null))
		{
			pos1 = new ChunkCoordinates(posX, posY, posZ);
			player.addChatMessage(new ChatComponentText("Corner 1 Selected (" + posX + ", " + posY + ", " + posZ + ").").
					setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
			if(pos2 == null && displayInfo)
			{
				player.addChatMessage(new ChatComponentText("Now select the second corner by right clicking with the 'modifier key' pressed (see Controls for key binding)").
						setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GRAY)));
				displayInfo = false;
			}
		}
		else
		{
			pos2 = new ChunkCoordinates(posX, posY, posZ);
			player.addChatMessage(new ChatComponentText("Corner 2 Selected (" + posX + ", " + posY + ", " + posZ + ").").
					setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
		}
	}
	
	@SideOnly(value = Side.CLIENT)
	public void saveSchematic(EntityPlayer player)
	{
		
		
		
		
		boolean modify = ClientProxy.selectModifier.getIsKeyPressed();
		if(modify)
		{
			pos1 = null;
			pos2 = null;
			player.addChatMessage(new ChatComponentText("Cleared Selection").
					setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
			displayInfo = true;
		}
		else
		{
			if(pos1 != null)
			{
				if(pos2 != null)
				{
					Minecraft.getMinecraft().displayGuiScreen(new GuiSaveSchematic(pos1, pos2));
				}
				else
				{
					player.addChatMessage(new ChatComponentText("You must select the second corner!").
							setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				}
			}
			else
			{
				player.addChatMessage(new ChatComponentText("You must select the first corner!").
						setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
			}
		}
	}
	

}
