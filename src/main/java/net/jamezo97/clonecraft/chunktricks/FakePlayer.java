package net.jamezo97.clonecraft.chunktricks;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class FakePlayer extends EntityPlayer
{

	public FakePlayer(World p_i45324_1_)
	{
		super(p_i45324_1_, new GameProfile(null, "Steve"));
	}
	
	

	@Override
	public void setCurrentItemOrArmor(int p_70062_1_, ItemStack p_70062_2_)
	{
		super.setCurrentItemOrArmor(p_70062_1_, p_70062_2_);
	}



	@Override
	public boolean canPlayerEdit(int x, int y, int z, int something, ItemStack p_82247_5_)
	{
		return true;
	}

	@Override
	public void addChatMessage(IChatComponent p_145747_1_)
	{
	}

	@Override
	public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
	{
		return false;
	}

	@Override
	public ChunkCoordinates getPlayerCoordinates()
	{
		return new ChunkCoordinates((int)Math.floor(posX), (int)Math.floor(posY), (int)Math.floor(posZ));
	}
	
	
	
	

}
