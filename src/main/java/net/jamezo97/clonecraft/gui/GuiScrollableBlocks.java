package net.jamezo97.clonecraft.gui;

import java.util.ArrayList;

import net.jamezo97.clonecraft.clone.BreakableBlocks;
import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class GuiScrollableBlocks extends GuiScrollable{
	
	BreakableBlocks breakables;
	
	ArrayList<Long> allViewable = new ArrayList<Long>();
	
	ArrayList<Long> viewable = new ArrayList<Long>();
	
	EntityClone clone;
	
	RenderItem itemRenderer;
	
	public GuiScrollableBlocks(int xPosition, int yPosition, int width, int height, EntityClone clone) {
		super(xPosition, yPosition, width, height);
		this.breakables = clone.getOptions().breakables;
		this.clone = clone;
		
		BreakableBlocks.addPrimitiveLong(allViewable, BreakableBlocks.validBlocksArray);
		updateViewable(null);
		itemRenderer = new RenderItem();
	}
	
	public void updateViewable(String searchCrit){
		searchCrit = searchCrit==null?null:searchCrit.toLowerCase();
		viewable.clear();
		long block;
		for(int a = 0; a < allViewable.size(); a++){
			block = allViewable.get(a);
			if(searchCrit == null || searchCrit.length() == 0 || getTransName(block).toLowerCase().contains(searchCrit)){
				viewable.add(block);
			}
		}
	}
	
	public String getTransName(long blockId)
	{
		int id = BreakableBlocks.getId(blockId);
		int meta = BreakableBlocks.getMeta(blockId);
		Block block = Block.getBlockById(id);
		
		if(block != null)
		{
			ItemStack stackTemp = new ItemStack(Block.getBlockById(id), 1, meta);
			return stackTemp.getDisplayName();
		}
		else
		{
			return "Unknown";
		}
		
	}

	@Override
	public boolean isEntrySelected(int entryIndex)
	{
		return breakables.canBreak(viewable.get(entryIndex));
	}

	@Override
	public int getEntryCount() {
		return viewable.size();
	}

	@Override
	public int getEntryHeight() {
		return 55;
	}

	@Override
	public void entryClicked(int entryIndex) {
		breakables.toggleBlock(viewable.get(entryIndex));
	}

	@Override
	public void renderEntry(int entryIndex, int width, int height) {
		long blockId = viewable.get(entryIndex);
		
		int colour = bc.getColourFor(blockId);
		
		if(this.isEntrySelected(entryIndex)){
			int total = ((colour>>16)&0xff) + ((colour>>8)&0xff) + ((colour)&0xff);
			total /= 3;
			
			int r = (colour>>16) & 0xff;
			int g = (colour>>8) & 0xff;
			int b = (colour) & 0xff;
			int tick = clone.ticksExisted;// + entryIndex*8;
			r *= (Math.sin(((float)tick) / 1.5f)+1)/25+0.92;
			g *= (Math.sin(((float)tick) / 1.5f)+1)/25+0.92;
			b *= (Math.sin(((float)tick) / 1.5f)+1)/25+0.92;
			colour = 0xff000000 | (r<<16) | (g<<8) | b;
			
			
			
//			System.out.println(total);
			this.drawRect(0, 0, width, height, total<128?0xffffffff:0xff000000);
			this.drawRect(2, 2, width-2, height-2, colour | 0xff000000);
		}else{
			this.drawRect(0, 0, width, height, colour | 0xff000000);
		}
		
//		int colour = bc.getColourFor(blockId);
//		if(this.isEntrySelected(entryIndex)){
//			this.drawRect(0, 0, width, height, 0xff000000);
//			this.drawRect(2, 2, width-2, height-2, 0xffcccccc);
//		}else{
//			this.drawRect(0, 0, width, height, 0xffcccccc);
//		}
		
		
		this.drawString(Minecraft.getMinecraft().fontRenderer, getTransName(blockId), 3, 3, 0xffffff);
		GL11.glPushMatrix();
//		GL11.glRotatef(clone.ticksExisted, 0, 1, 0);
		this.renderBlock(blockId);
		GL11.glPopMatrix();
		
//		this.renderEntity(width-20, +height-15, 23, (float)Math.sin(clone.ticksExisted/70.0f)*30, clone.ticksExisted*3, viewable.get(entryIndex).getEntity(clone.worldObj));

	}

	private void renderBlock(long blockData) {
		int id = BreakableBlocks.getId(blockData);
		int meta = BreakableBlocks.getMeta(blockData);
		ItemStack stack = new ItemStack(Block.getBlockById(id), 1, meta);
		this.renderItem(stack, 60, 8);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	public void renderItem(ItemStack var5, int par2, int par3){
		GL11.glPushMatrix();
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableGUIStandardItemLighting();

        GL11.glScalef(2, 2, 2);
//        GL11.glDisable(GL11.GL_DEPTH_TEST);
        itemRenderer.renderItemAndEffectIntoGUI(Minecraft.getMinecraft().fontRenderer, Minecraft.getMinecraft().renderEngine, var5, par2, par3);
	
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
        GL11.glPopMatrix();
	}
	
	
	
	
	

	public static BlockColourer bc;
	
	static{
		bc = new BlockColourer();
	}
}
