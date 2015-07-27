package net.jamezo97.clonecraft.item;

import java.util.Arrays;

import net.jamezo97.clonecraft.build.RotationMapping;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class ItemRotateMapper extends Item{

	Block currentBlock = null;
	
	int[] metas = new int[4];
	
	int index = 0;
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int posX, int posY, int posZ, int side, float f1, float f2, float f3) 
	{
		if(world.isRemote)
		{
			
			Block clicked = world.getBlock(posX, posY, posZ);
			if(currentBlock == null || currentBlock != clicked)
			{
				
				if(currentBlock != null && currentBlock != clicked)
				{
					player.addChatMessage(new ChatComponentText("Different block. Reset selection.").
							setChatStyle(new ChatStyle().setColor(EnumChatFormatting.RED)));
				}
				
				currentBlock = clicked;
				index = 0;
				
				metas[index] = world.getBlockMetadata(posX, posY, posZ);
				
				player.addChatMessage(new ChatComponentText("First block selected. (" + metas[index] + ")").
						setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
				
				
				player.addChatMessage(new ChatComponentText("Now select three of the same block in order of increasing rotation.").
						setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
				
				player.addChatMessage(new ChatComponentText("Rotation is clockwise when viewed from above.").
						setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)));
				index++;
			}
			else if(currentBlock == clicked)
			{
		
				metas[index] = world.getBlockMetadata(posX, posY, posZ);
				
				player.addChatMessage(new ChatComponentText("Set block " + (index+1) + " (" + metas[index] + ")").
						setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
				
				if(index == 3)
				{
					
					System.out.println("Save: " + Arrays.toString(metas));
					
					if(RotationMapping.addMapping(clicked, metas))
					{
						player.addChatMessage(new ChatComponentText("Mapped rotations!").
								setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)));
						System.out.println("Added mapping!");
						
						RotationMapping.save();
					}
					else
					{
						player.addChatMessage(new ChatComponentText("New mapping clashes with old mapping. Removing old mapping. Inserting new one.").
								setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_RED)));
						player.addChatMessage(new ChatComponentText("Mapped rotations!").
								setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_PURPLE)));
						RotationMapping.save();
					}
					index = 0;
					currentBlock = null;
					metas = new int[4];
				}
				else
				{
					index++;
					
				}
			}
			
			return true;
		}
		
		
		
		
		
		
		
		return super.onItemUse(stack, player, world, posX, posY, posZ, side, f1, f2, f3);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) 
	{
		if(world.isRemote)
		{
			currentBlock = null;
			index = 0;
			metas = new int[4];
			
			player.addChatMessage(new ChatComponentText("Reset selections").
					setChatStyle(new ChatStyle().setColor(EnumChatFormatting.DARK_GREEN)));
		}
		
		
		return super.onItemRightClick(stack, world, player);
	}

	
	
}
