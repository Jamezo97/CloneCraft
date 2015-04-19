package net.jamezo97.clonecraft.render;

import java.util.UUID;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;

import com.mojang.authlib.GameProfile;

public class RenderClone extends RendererLivingEntity {
	
	
	
	
	
	
	public static final ResourceLocation steveTextures = new ResourceLocation("textures/entity/steve.png");
    public ModelBipedCustom modelBipedMain;
    public ModelBipedCustom modelArmorChestplate;
    public ModelBipedCustom modelArmor;

    public RenderClone()
    {
        super(new ModelBipedCustom(0.0F), 0.5F);
        this.modelBipedMain = (ModelBipedCustom)this.mainModel;
        this.modelArmorChestplate = new ModelBipedCustom(1.0F);
        this.modelArmor = new ModelBipedCustom(0.5F);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityClone p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        ItemStack itemstack = p_77032_1_.inventory.armorItemInSlot(3 - p_77032_2_);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                ItemArmor itemarmor = (ItemArmor)item;
                this.bindTexture(RenderBiped.getArmorResource(p_77032_1_, itemstack, p_77032_2_, null));
                ModelBiped modelbiped = p_77032_2_ == 2 ? this.modelArmor : this.modelArmorChestplate;
                modelbiped.bipedHead.showModel = p_77032_2_ == 0;
                modelbiped.bipedHeadwear.showModel = p_77032_2_ == 0;
                modelbiped.bipedBody.showModel = p_77032_2_ == 1 || p_77032_2_ == 2;
                modelbiped.bipedRightArm.showModel = p_77032_2_ == 1;
                modelbiped.bipedLeftArm.showModel = p_77032_2_ == 1;
                modelbiped.bipedRightLeg.showModel = p_77032_2_ == 2 || p_77032_2_ == 3;
                modelbiped.bipedLeftLeg.showModel = p_77032_2_ == 2 || p_77032_2_ == 3;
                modelbiped = net.minecraftforge.client.ForgeHooksClient.getArmorModel(p_77032_1_, itemstack, p_77032_2_, modelbiped);
                this.setRenderPassModel(modelbiped);
                modelbiped.onGround = this.mainModel.onGround;
                modelbiped.isRiding = this.mainModel.isRiding;
                modelbiped.isChild = this.mainModel.isChild;

                //Move outside if to allow for more then just CLOTH
                int j = itemarmor.getColor(itemstack);
                if (j != -1)
                {
                    float f1 = (float)(j >> 16 & 255) / 255.0F;
                    float f2 = (float)(j >> 8 & 255) / 255.0F;
                    float f3 = (float)(j & 255) / 255.0F;
                    GL11.glColor3f(f1, f2, f3);

                    if (itemstack.isItemEnchanted())
                    {
                        return 31;
                    }

                    return 16;
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);

                if (itemstack.isItemEnchanted())
                {
                    return 15;
                }

                return 1;
            }
        }

        return -1;
    }

    protected void func_82408_c(EntityClone p_82408_1_, int p_82408_2_, float p_82408_3_)
    {
        ItemStack itemstack = p_82408_1_.inventory.armorItemInSlot(3 - p_82408_2_);

        if (itemstack != null)
        {
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor)
            {
                this.bindTexture(RenderBiped.getArmorResource(p_82408_1_, itemstack, p_82408_2_, "overlay"));
                GL11.glColor3f(1.0F, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityClone clone, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        ItemStack itemstack = clone.getHeldItem();
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = itemstack != null ? 1 : 0;
       
        
        this.modelArmorChestplate.heldItemLeft = this.modelArmor.heldItemLeft = this.modelBipedMain.heldItemLeft = clone.getOfferedItem() != null ? 3 : 0;
        
        if (itemstack != null && clone.getItemInUseCount() > 0)
        {
            EnumAction enumaction = itemstack.getItemUseAction();

            if (enumaction == EnumAction.block)
            {
                this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
            }
            else if (enumaction == EnumAction.bow)
            {
                this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
            }
            else if (enumaction == EnumAction.eat || enumaction == EnumAction.drink)
            {
            	this.modelArmorChestplate.isEating = this.modelBipedMain.isEating = true;
            }
        }

        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = clone.isSneaking();
        double d3 = p_76986_4_ - (double)clone.yOffset;

        if (clone.isSneaking())
        {
            d3 -= 0.125D;
        }

        this.modelBipedMain.bipedRightArm.rotateAngleX = 50;
        
        super.doRender((EntityLivingBase)clone, p_76986_2_, d3, p_76986_6_, p_76986_8_, p_76986_9_);
        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmorChestplate.isEating = this.modelBipedMain.isEating = false;
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
        
      
        if(clone.getOptions().stats.get() && clone.canUseThisEntity(Minecraft.getMinecraft().thePlayer)){
        	renderStatus(clone,Math.round(clone.getHealth()) + "/" + Math.round(clone.getMaxHealth()), clone.getHealth() / clone.getMaxHealth(), p_76986_2_, p_76986_4_, p_76986_6_, p_76986_9_, 0);
        	renderStatus(clone, clone.foodStats.getFoodLevel() + "/20", clone.foodStats.getFoodLevel()/20.0f, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_9_, 1);
        }
        
       
    }
    
    public void renderStatus(EntityClone clone, String s, float done, double x, double y, double z, float partial, int type){
    	float f = 1.0f/256.0f;
    	double d3 = clone.getDistanceSqToEntity(this.renderManager.livingPlayer);
    	double maxDistanceSqr = 400;
    	
    	boolean flag = done>0;
    	if(done <= 0.4f && done > 0){
    		int speed = (int)(done * 100.0f);
    		if(speed == 0 || clone.ticksExisted%speed < (speed/2)){
    			flag = false;
    		}
    	}
    	
    	
        if (d3 <= (double)(maxDistanceSqr))
        {
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f1 = 0.009F;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)x + 0.0F, (float)y + clone.height + (type==0?0.28F:0.19f), (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.instance;


            int j = 50;
            
            if(true){
            	GL11.glDisable(GL11.GL_TEXTURE_2D);
            	 tessellator.startDrawingQuads();
                 //fontrenderer.getStringWidth(s) / 2;
                 tessellator.setColorRGBA_F(1-done, done, 0.0F, 0.75F);
                 tessellator.addVertex((double)(-j), (double)(-1), 0.0D);
                 tessellator.addVertex((double)(-j), (double)(8), 0.0D);
                 tessellator.addVertex((double)(j-(1-done)*(j*2)), (double)(8), 0.0D);
                 tessellator.addVertex((double)(j-(1-done)*(j*2)), (double)(-1), 0.0D);
                 tessellator.draw();
                 GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
           
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, -1);
            GL11.glDisable(GL11.GL_BLEND);
            //Render Icon
            if(flag){
            	this.bindTexture(GuiScreen.icons);
                GL11.glTranslatef(-j-5, 3, 0);
                GL11.glScalef(6, 6, 6);
                
                GL11.glBegin(GL11.GL_QUADS);
                if(type == 0){
                	 GL11.glTexCoord2f(52.0f*f, 9.0f*f);
                     GL11.glVertex3f(-1, 1, 0);
                     GL11.glTexCoord2f(61.0f*f, 9.0f*f);
                     GL11.glVertex3f(1, 1, 0);
                     GL11.glTexCoord2f(61.0f*f, 0f);
                     GL11.glVertex3f(1, -1, 0);
                     GL11.glTexCoord2f(52.0f*f, 0f);
                     GL11.glVertex3f(-1, -1, 0);
                }else{
                	 GL11.glTexCoord2f(61.0f*f, 36.0f*f);
                     GL11.glVertex3f(-1, 1, 0);
                     GL11.glTexCoord2f(52.0f*f, 36.0f*f);
                     GL11.glVertex3f(1, 1, 0);
                     GL11.glTexCoord2f(52.0f*f, 27.0f*f);
                     GL11.glVertex3f(1, -1, 0);
                     GL11.glTexCoord2f(61.0f*f, 27.0f*f);
                     GL11.glVertex3f(-1, -1, 0);
                }
               
                GL11.glEnd();
            }
            
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    protected void passSpecialRender(EntityLivingBase entity, double p_77033_2_, double p_77033_4_, double p_77033_6_)
    {
        float red = 0, green = 0, blue = 0;
        EntityClone clone = (EntityClone)entity;
        red = ((float)((clone.team.teamColour >> 16) & 0xff))/255.0f;
        green = ((float)((clone.team.teamColour >> 8) & 0xff))/255.0f;
        blue = ((float)((clone.team.teamColour) & 0xff))/255.0f;
    	
    	
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

        if (this.func_110813_b(entity))
        {
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            double d3 = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
            float f2 = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;

            if (d3 < (double)(f2 * f2))
            {
                String s = entity.func_145748_c_().getFormattedText()/* + ", " + clone.getOwnerName()*/;

                if (entity.isSneaking())
                {
                    FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
                    GL11.glPushMatrix();
                    GL11.glTranslatef((float)p_77033_2_ + 0.0F, (float)p_77033_4_ + entity.height + 0.5F, (float)p_77033_6_);
                    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                    GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                    GL11.glScalef(-f1, -f1, f1);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
                    GL11.glDepthMask(false);
                    GL11.glEnable(GL11.GL_BLEND);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    Tessellator tessellator = Tessellator.instance;
                    GL11.glDisable(GL11.GL_TEXTURE_2D);
                    tessellator.startDrawingQuads();
                    int i = fontrenderer.getStringWidth(s) / 2;
                    tessellator.setColorRGBA_F(1.0f, 1.0f, 0.0f, 0.25F);
                    tessellator.addVertex((double)(-i - 1), -1.0D, 0.0D);
                    tessellator.addVertex((double)(-i - 1), 8.0D, 0.0D);
                    tessellator.addVertex((double)(i + 1), 8.0D, 0.0D);
                    tessellator.addVertex((double)(i + 1), -1.0D, 0.0D);
                    tessellator.draw();
                    GL11.glEnable(GL11.GL_TEXTURE_2D);
                    GL11.glDepthMask(true);
                    fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, 553648127);
                    GL11.glEnable(GL11.GL_LIGHTING);
                    GL11.glDisable(GL11.GL_BLEND);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                }
                else
                {
                    this.func_96449_a(entity, p_77033_2_, p_77033_4_, p_77033_6_, s, f1, d3);
                }
            }
        }
    }
    
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityClone p_110775_1_)
    {
        return p_110775_1_.getTexture();
    }

    protected void renderEquippedItems(EntityClone clone, float partial)
    {
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        super.renderEquippedItems(clone, partial);
        super.renderArrowsStuckInEntity(clone, partial);
        ItemStack itemstack = clone.inventory.armorItemInSlot(3);

        if (itemstack != null)
        {
            GL11.glPushMatrix();
            this.modelBipedMain.bipedHead.postRender(0.0625F);
            float f1;

            if (itemstack.getItem() instanceof ItemBlock)
            {
                net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(itemstack, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
                boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, itemstack, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

                if (is3D || RenderBlocks.renderItemIn3d(Block.getBlockFromItem(itemstack.getItem()).getRenderType()))
                {
                    f1 = 0.625F;
                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                    GL11.glScalef(f1, -f1, -f1);
                }

                this.renderManager.itemRenderer.renderItem(clone, itemstack, 0);
            }
            else if (itemstack.getItem() == Items.skull)
            {
                f1 = 1.0625F;
                GL11.glScalef(f1, -f1, -f1);
                GameProfile gameprofile = null;

                if (itemstack.hasTagCompound())
                {
                    NBTTagCompound nbttagcompound = itemstack.getTagCompound();

                    if (nbttagcompound.hasKey("SkullOwner", 10))
                    {
                        gameprofile = NBTUtil.func_152459_a(nbttagcompound.getCompoundTag("SkullOwner"));
                    }
                    else if (nbttagcompound.hasKey("SkullOwner", 8) && !StringUtils.isNullOrEmpty(nbttagcompound.getString("SkullOwner")))
                    {
                        gameprofile = new GameProfile((UUID)null, nbttagcompound.getString("SkullOwner"));
                    }
                }

                TileEntitySkullRenderer.field_147536_b.func_152674_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getItemDamage(), gameprofile);
            }

            GL11.glPopMatrix();
        }

        float f2;


//        boolean flag = false;
        float f4;

//        if (flag && !p_77029_1_.isInvisible() && !p_77029_1_.getHideCape())
//        {
//            this.bindTexture(p_77029_1_.getLocationCape());
//            GL11.glPushMatrix();
//            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
//            double d3 = p_77029_1_.field_71091_bM + (p_77029_1_.field_71094_bP - p_77029_1_.field_71091_bM) * (double)p_77029_2_ - (p_77029_1_.prevPosX + (p_77029_1_.posX - p_77029_1_.prevPosX) * (double)p_77029_2_);
//            double d4 = p_77029_1_.field_71096_bN + (p_77029_1_.field_71095_bQ - p_77029_1_.field_71096_bN) * (double)p_77029_2_ - (p_77029_1_.prevPosY + (p_77029_1_.posY - p_77029_1_.prevPosY) * (double)p_77029_2_);
//            double d0 = p_77029_1_.field_71097_bO + (p_77029_1_.field_71085_bR - p_77029_1_.field_71097_bO) * (double)p_77029_2_ - (p_77029_1_.prevPosZ + (p_77029_1_.posZ - p_77029_1_.prevPosZ) * (double)p_77029_2_);
//            f4 = p_77029_1_.prevRenderYawOffset + (p_77029_1_.renderYawOffset - p_77029_1_.prevRenderYawOffset) * p_77029_2_;
//            double d1 = (double)MathHelper.sin(f4 * (float)Math.PI / 180.0F);
//            double d2 = (double)(-MathHelper.cos(f4 * (float)Math.PI / 180.0F));
//            float f5 = (float)d4 * 10.0F;
//
//            if (f5 < -6.0F)
//            {
//                f5 = -6.0F;
//            }
//
//            if (f5 > 32.0F)
//            {
//                f5 = 32.0F;
//            }
//
//            float f6 = (float)(d3 * d1 + d0 * d2) * 100.0F;
//            float f7 = (float)(d3 * d2 - d0 * d1) * 100.0F;
//
//            if (f6 < 0.0F)
//            {
//                f6 = 0.0F;
//            }
//
//            float f8 = p_77029_1_.prevCameraYaw + (p_77029_1_.cameraYaw - p_77029_1_.prevCameraYaw) * p_77029_2_;
//            f5 += MathHelper.sin((p_77029_1_.prevDistanceWalkedModified + (p_77029_1_.distanceWalkedModified - p_77029_1_.prevDistanceWalkedModified) * p_77029_2_) * 6.0F) * 32.0F * f8;
//
//            if (p_77029_1_.isSneaking())
//            {
//                f5 += 25.0F;
//            }
//
//            GL11.glRotatef(6.0F + f6 / 2.0F + f5, 1.0F, 0.0F, 0.0F);
//            GL11.glRotatef(f7 / 2.0F, 0.0F, 0.0F, 1.0F);
//            GL11.glRotatef(-f7 / 2.0F, 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//            this.modelBipedMain.renderCloak(0.0625F);
//            GL11.glPopMatrix();
//        }

        ItemStack itemstack1 = clone.getHeldItem();
        ItemStack itemstack2 = clone.getOfferedItem();
        if (itemstack1 != null)
        {
            renderItemstackInHand(itemstack1, 0, clone, partial);
        }
        if (itemstack2 != null)
        {
            renderItemstackInHand(itemstack2, 1, clone, partial);
        }
    }
    
    public void renderItemstackInHand(ItemStack stack, int hand, EntityClone clone, float partial){
    	float f2, f4;
    	
    	GL11.glPushMatrix();
        
        
        
        if(hand == 0){
        	this.modelBipedMain.bipedRightArm.postRender(0.0625F);
            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
        }else if(hand == 1){
        	this.modelBipedMain.bipedLeftArm.postRender(0.0625F);
            GL11.glTranslatef(0.0625F, 0.4375F, 0.0625F);
        }

        if (clone.fishEntity != null)
        {
            stack = new ItemStack(Items.stick);
        }

        EnumAction enumaction = null;

        if (clone.getItemInUseCount() > 0)
        {
            enumaction = stack.getItemUseAction();
        }

        net.minecraftforge.client.IItemRenderer customRenderer = net.minecraftforge.client.MinecraftForgeClient.getItemRenderer(stack, net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED);
        boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(net.minecraftforge.client.IItemRenderer.ItemRenderType.EQUIPPED, stack, net.minecraftforge.client.IItemRenderer.ItemRendererHelper.BLOCK_3D));

        if (is3D || stack.getItem() instanceof ItemBlock && RenderBlocks.renderItemIn3d(Block.getBlockFromItem(stack.getItem()).getRenderType()))
        {
            f2 = 0.5F;
            GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
            f2 *= 0.75F;
            GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(-f2, -f2, f2);
        }
        else if (stack.getItem() == Items.bow)
        {
            f2 = 0.625F;
            GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
            GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
            GL11.glScalef(f2, -f2, f2);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        }
        else if (stack.getItem().isFull3D())
        {
            f2 = 0.625F;

            if (stack.getItem().shouldRotateAroundWhenRendering())
            {
                GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, -0.125F, 0.0F);
            }

            if (clone.getItemInUseCount() > 0 && enumaction == EnumAction.block)
            {
                GL11.glTranslatef(0.05F, 0.0F, -0.1F);
                GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
            }

            GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
            GL11.glScalef(f2, -f2, f2);
            GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
        }
        else
        {
            f2 = 0.375F;
            GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
            GL11.glScalef(f2, f2, f2);
            GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
        }

        float f3;
        int k;
        float f12;

        if (stack.getItem().requiresMultipleRenderPasses())
        {
            for (k = 0; k < stack.getItem().getRenderPasses(stack.getItemDamage()); ++k)
            {
                int i = stack.getItem().getColorFromItemStack(stack, k);
                f12 = (float)(i >> 16 & 255) / 255.0F;
                f3 = (float)(i >> 8 & 255) / 255.0F;
                f4 = (float)(i & 255) / 255.0F;
                GL11.glColor4f(f12, f3, f4, 1.0F);
                this.renderManager.itemRenderer.renderItem(clone, stack, k);
            }
        }
        else
        {
            k = stack.getItem().getColorFromItemStack(stack, 0);
            float f11 = (float)(k >> 16 & 255) / 255.0F;
            f12 = (float)(k >> 8 & 255) / 255.0F;
            f3 = (float)(k & 255) / 255.0F;
            GL11.glColor4f(f11, f12, f3, 1.0F);
            this.renderManager.itemRenderer.renderItem(clone, stack, 0);
        }
        GL11.glPopMatrix();
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityClone p_77041_1_, float p_77041_2_)
    {
        float f1 = 0.9375F * p_77041_1_.getScale();
        GL11.glScalef(f1, f1, f1);
    }

    protected void func_96449_a(EntityClone p_96449_1_, double p_96449_2_, double p_96449_4_, double p_96449_6_, String p_96449_8_, float p_96449_9_, double p_96449_10_)
    {
        if (p_96449_10_ < 100.0D)
        {
            Scoreboard scoreboard = p_96449_1_.worldObj.getScoreboard();
            ScoreObjective scoreobjective = scoreboard.func_96539_a(2);

            if (scoreobjective != null)
            {
                Score score = scoreboard.func_96529_a(p_96449_1_.getCommandSenderName(), scoreobjective);

                if (p_96449_1_.isPlayerSleeping())
                {
                    this.func_147906_a(p_96449_1_, score.getScorePoints() + " " + scoreobjective.getDisplayName(), p_96449_2_, p_96449_4_ - 1.5D, p_96449_6_, 64);
                }
                else
                {
                    this.func_147906_a(p_96449_1_, score.getScorePoints() + " " + scoreobjective.getDisplayName(), p_96449_2_, p_96449_4_, p_96449_6_, 64);
                }

                p_96449_4_ += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * p_96449_9_);
            }
        }
        super.func_96449_a(p_96449_1_, p_96449_2_, p_96449_4_, p_96449_6_, p_96449_8_, p_96449_9_, p_96449_10_);
    }

    public void renderFirstPersonArm(EntityPlayer p_82441_1_)
    {
        float f = 1.0F;
        GL11.glColor3f(f, f, f);
        this.modelBipedMain.onGround = 0.0F;
        this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, p_82441_1_);
        this.modelBipedMain.bipedRightArm.render(0.0625F);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityClone p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
    {
//        if (p_77039_1_.isEntityAlive() && p_77039_1_.isPlayerSleeping())
//        {
//            super.renderLivingAt(p_77039_1_, p_77039_2_ + (double)p_77039_1_.field_71079_bU, p_77039_4_ + (double)p_77039_1_.field_71082_cx, p_77039_6_ + (double)p_77039_1_.field_71089_bV);
//        }
//        else
        {
            super.renderLivingAt(p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
        }
    }

    protected void rotateCorpse(EntityClone p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
//        if (p_77043_1_.isEntityAlive() && p_77043_1_.isPlayerSleeping())
//        {
//            GL11.glRotatef(p_77043_1_.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
//            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
//        }
//        else
        {
            super.rotateCorpse(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
        }
    }

    protected void func_96449_a(EntityLivingBase p_96449_1_, double p_96449_2_, double p_96449_4_, double p_96449_6_, String p_96449_8_, float p_96449_9_, double p_96449_10_)
    {
        this.func_96449_a((EntityClone)p_96449_1_, p_96449_2_, p_96449_4_, p_96449_6_, p_96449_8_, p_96449_9_, p_96449_10_);
    }

    /**
     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
     * entityLiving, partialTickTime
     */
    protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_)
    {
        this.preRenderCallback((EntityClone)p_77041_1_, p_77041_2_);
    }

    protected void func_82408_c(EntityLivingBase p_82408_1_, int p_82408_2_, float p_82408_3_)
    {
        this.func_82408_c((EntityClone)p_82408_1_, p_82408_2_, p_82408_3_);
    }

    /**
     * Queries whether should render the specified pass or not.
     */
    protected int shouldRenderPass(EntityLivingBase p_77032_1_, int p_77032_2_, float p_77032_3_)
    {
        return this.shouldRenderPass((EntityClone)p_77032_1_, p_77032_2_, p_77032_3_);
    }

    protected void renderEquippedItems(EntityLivingBase p_77029_1_, float p_77029_2_)
    {
        this.renderEquippedItems((EntityClone)p_77029_1_, p_77029_2_);
    }

    protected void rotateCorpse(EntityLivingBase p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_)
    {
        this.rotateCorpse((EntityClone)p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
    }

    /**
     * Sets a simple glTranslate on a LivingEntity.
     */
    protected void renderLivingAt(EntityLivingBase p_77039_1_, double p_77039_2_, double p_77039_4_, double p_77039_6_)
    {
        this.renderLivingAt((EntityClone)p_77039_1_, p_77039_2_, p_77039_4_, p_77039_6_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityLivingBase p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityClone)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return this.getEntityTexture((EntityClone)p_110775_1_);
    }

    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
        this.doRender((EntityClone)p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
    }
	
    
    
    
    
    
    
    
    protected void func_147906_a(Entity p_147906_1_, String p_147906_2_, double p_147906_3_, double p_147906_5_, double p_147906_7_, int p_147906_9_)
    {
    	
    	float red = 0, green = 0, blue = 0;
        if(p_147906_1_ instanceof EntityClone){
        	EntityClone clone = (EntityClone)p_147906_1_;
        	red = ((float)((clone.team.teamColour >> 16) & 0xff))/255.0f;
        	green = ((float)((clone.team.teamColour >> 8) & 0xff))/255.0f;
        	blue = ((float)((clone.team.teamColour) & 0xff))/255.0f;
        }
        double d3 = p_147906_1_.getDistanceSqToEntity(this.renderManager.livingPlayer);

        if (d3 <= (double)(p_147906_9_ * p_147906_9_))
        {
            FontRenderer fontrenderer = this.getFontRendererFromRenderManager();
            float f = 1.6F;
            float f1 = 0.016666668F * f;
            GL11.glPushMatrix();
            GL11.glTranslatef((float)p_147906_3_ + 0.0F, (float)p_147906_5_ + p_147906_1_.height + 0.5F, (float)p_147906_7_);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(-f1, -f1, f1);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDepthMask(false);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            Tessellator tessellator = Tessellator.instance;
            byte b0 = 0;

            if (p_147906_2_.equals("deadmau5"))
            {
                b0 = -10;
            }

            GL11.glDisable(GL11.GL_TEXTURE_2D);
            tessellator.startDrawingQuads();
            int j = fontrenderer.getStringWidth(p_147906_2_) / 2;
            tessellator.setColorRGBA_F(red, green, blue, 0.50F);
            tessellator.addVertex((double)(-j - 1), (double)(-1 + b0), 0.0D);
            tessellator.addVertex((double)(-j - 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(8 + b0), 0.0D);
            tessellator.addVertex((double)(j + 1), (double)(-1 + b0), 0.0D);
            tessellator.draw();
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, 553648127);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            fontrenderer.drawString(p_147906_2_, -fontrenderer.getStringWidth(p_147906_2_) / 2, b0, -1);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }
	
	
	

//	private static final ResourceLocation field_110826_a = new ResourceLocation("textures/entity/steve.png");
//    private ModelBiped modelBipedMain;
//    private ModelBiped modelArmorChestplate;
//    private ModelBiped modelArmor;
//
//    public RenderClone()
//    {
//        super(new ModelBiped(0.0F), 0.5F);
//        this.modelBipedMain = (ModelBiped)this.mainModel;
//        this.modelArmorChestplate = new ModelBiped(1.0F);
//        this.modelArmor = new ModelBiped(0.5F);
//    }
//
//    /**
//     * Set the specified armor model as the player model. Args: player, armorSlot, partialTick
//     */
//    protected int setArmorModel(EntityClone par1EntityClone, int par2, float par3)
//    {
//        ItemStack itemstack = par1EntityClone.inventory.armorItemInSlot(3 - par2);
//
//        if (itemstack != null)
//        {
//            Item item = itemstack.getItem();
//
//            if (item instanceof ItemArmor)
//            {
//                ItemArmor itemarmor = (ItemArmor)item;
//                this.bindTexture(RenderBiped.getArmorResource(par1EntityClone, itemstack, par2, null));
//                ModelBiped modelbiped = par2 == 2 ? this.modelArmor : this.modelArmorChestplate;
//                modelbiped.bipedHead.showModel = par2 == 0;
//                modelbiped.bipedHeadwear.showModel = par2 == 0;
//                modelbiped.bipedBody.showModel = par2 == 1 || par2 == 2;
//                modelbiped.bipedRightArm.showModel = par2 == 1;
//                modelbiped.bipedLeftArm.showModel = par2 == 1;
//                modelbiped.bipedRightLeg.showModel = par2 == 2 || par2 == 3;
//                modelbiped.bipedLeftLeg.showModel = par2 == 2 || par2 == 3;
//                modelbiped = ForgeHooksClient.getArmorModel(par1EntityClone, itemstack, par2, modelbiped);
//                this.setRenderPassModel(modelbiped);
//                modelbiped.onGround = this.mainModel.onGround;
//                modelbiped.isRiding = this.mainModel.isRiding;
//                modelbiped.isChild = this.mainModel.isChild;
//                float f1 = 1.0F;
//
//                //Move outside if to allow for more then just CLOTH
//                int j = itemarmor.getColor(itemstack);
//                if (j != -1)
//                {
//                    float f2 = (float)(j >> 16 & 255) / 255.0F;
//                    float f3 = (float)(j >> 8 & 255) / 255.0F;
//                    float f4 = (float)(j & 255) / 255.0F;
//                    GL11.glColor3f(f1 * f2, f1 * f3, f1 * f4);
//
//                    if (itemstack.isItemEnchanted())
//                    {
//                        return 31;
//                    }
//
//                    return 16;
//                }
//
//                GL11.glColor3f(f1, f1, f1);
//
//                if (itemstack.isItemEnchanted())
//                {
//                    return 15;
//                }
//
//                return 1;
//            }
//        }
//
//        return -1;
//    }
//
//    protected void func_130220_b(EntityClone par1EntityClone, int par2, float par3)
//    {
//        ItemStack itemstack = par1EntityClone.inventory.armorItemInSlot(3 - par2);
//
//        if (itemstack != null)
//        {
//            Item item = itemstack.getItem();
//
//            if (item instanceof ItemArmor)
//            {
//                this.bindTexture(RenderBiped.getArmorResource(par1EntityClone, itemstack, par2, "overlay"));
//                float f1 = 1.0F;
//                GL11.glColor3f(f1, f1, f1);
//            }
//        }
//    }
//
//    public void func_130009_a(EntityClone par1EntityClone, double par2, double par4, double par6, float par8, float par9)
//    {
//        float f2 = 1.0F;
//        GL11.glColor3f(f2, f2, f2);
//        ItemStack itemstack = par1EntityClone.getCurrentEquippedItem();
//        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = itemstack != null ? 1 : 0;
//
////        System.out.println(par1EntityClone.isUsingItem());
//        
//        if (itemstack != null && par1EntityClone.getItemInUseCount() > 0)
//        {
//            EnumAction enumaction = itemstack.getItemUseAction();
//
//            if (enumaction == EnumAction.block)
//            {
//                this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 3;
//            }
//            else if (enumaction == EnumAction.bow)
//            {
//                this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = true;
//            }
//        }
//        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = par1EntityClone.isSneaking();
//        double d3 = par4 - (double)par1EntityClone.yOffset;
//
//        if (par1EntityClone.isSneaking()/* && !(par1EntityClone instanceof EntityPlayerSP)*/)
//        {
//            d3 -= 0.125D;
//        }
//
//        
//        super.doRenderLiving(par1EntityClone, par2, d3, par6, par8, par9);
//        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
//        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
//        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
//    }
//
//    protected ResourceLocation getEntityTexture(EntityClone par1EntityClone)
//    {
//    	ResourceLocation loc = par1EntityClone.getSkinResource();
//    	if(loc == null){
//    		loc = field_110826_a;
//    	}
//    	return loc;
//    }
//
//    /**
//     * Method for adding special render rules
//     */
//    protected void renderSpecials(EntityClone par1EntityClone, float par2)
//    {
///*        RenderPlayerEvent.Specials.Pre event = new RenderPlayerEvent.Specials.Pre(par1EntityClone, this, par2);
//        if (MinecraftForge.EVENT_BUS.post(event))
//        {
//            return;
//        }
//*/
//        float f1 = 1.0F;
//        GL11.glColor3f(f1, f1, f1);
//        super.renderEquippedItems(par1EntityClone, par2);
//        super.renderArrowsStuckInEntity(par1EntityClone, par2);
//        ItemStack itemstack = par1EntityClone.inventory.armorItemInSlot(3);
//
//        if (itemstack != null/* && event.renderHelmet*/)
//        {
//            GL11.glPushMatrix();
//            this.modelBipedMain.bipedHead.postRender(0.0625F);
//            float f2;
//
//            if (itemstack != null && itemstack.getItem() instanceof ItemBlock)
//            {
//                IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack, EQUIPPED);
//                boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack, BLOCK_3D));
//
//                if (is3D || RenderBlocks.renderItemIn3d(Block.blocksList[itemstack.itemID].getRenderType()))
//                {
//                    f2 = 0.625F;
//                    GL11.glTranslatef(0.0F, -0.25F, 0.0F);
//                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
//                    GL11.glScalef(f2, -f2, -f2);
//                }
//
//                this.renderManager.itemRenderer.renderItem(par1EntityClone, itemstack, 0);
//            }
//            else if (itemstack.getItem().itemID == Item.skull.itemID)
//            {
//                f2 = 1.0625F;
//                GL11.glScalef(f2, -f2, -f2);
//                String s = "";
//
//                if (itemstack.hasTagCompound() && itemstack.getTagCompound().hasKey("SkullOwner"))
//                {
//                    s = itemstack.getTagCompound().getString("SkullOwner");
//                }
//
//                TileEntitySkullRenderer.skullRenderer.func_82393_a(-0.5F, 0.0F, -0.5F, 1, 180.0F, itemstack.getItemDamage(), s);
//            }
//
//            GL11.glPopMatrix();
//        }
//
///*        if (par1EntityClone.getCommandSenderName().equals("deadmau5") && par1EntityClone.func_110309_l().func_110557_a())
//        {
//            this.func_110776_a(par1EntityClone.func_110306_p());
//
//            for (int i = 0; i < 2; ++i)
//            {
//                float f3 = par1EntityClone.prevRotationYaw + (par1EntityClone.rotationYaw - par1EntityClone.prevRotationYaw) * par2 - (par1EntityClone.prevRenderYawOffset + (par1EntityClone.renderYawOffset - par1EntityClone.prevRenderYawOffset) * par2);
//                float f4 = par1EntityClone.prevRotationPitch + (par1EntityClone.rotationPitch - par1EntityClone.prevRotationPitch) * par2;
//                GL11.glPushMatrix();
//                GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
//                GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
//                GL11.glTranslatef(0.375F * (float)(i * 2 - 1), 0.0F, 0.0F);
//                GL11.glTranslatef(0.0F, -0.375F, 0.0F);
//                GL11.glRotatef(-f4, 1.0F, 0.0F, 0.0F);
//                GL11.glRotatef(-f3, 0.0F, 1.0F, 0.0F);
//                float f5 = 1.3333334F;
//                GL11.glScalef(f5, f5, f5);
//                this.modelBipedMain.renderEars(0.0625F);
//                GL11.glPopMatrix();
//            }
//        }
//
//        boolean flag = par1EntityClone.func_110310_o().func_110557_a();
//        boolean flag1 = !par1EntityClone.isInvisible();
//        boolean flag2 = !par1EntityClone.getHideCape();
//        flag = event.renderCape && flag;
//        float f6;
//
//        if (flag && flag1 && flag2)
//        {
//            this.func_110776_a(par1EntityClone.func_110303_q());
//            GL11.glPushMatrix();
//            GL11.glTranslatef(0.0F, 0.0F, 0.125F);
//            double d0 = par1EntityClone.field_71091_bM + (par1EntityClone.field_71094_bP - par1EntityClone.field_71091_bM) * (double)par2 - (par1EntityClone.prevPosX + (par1EntityClone.posX - par1EntityClone.prevPosX) * (double)par2);
//            double d1 = par1EntityClone.field_71096_bN + (par1EntityClone.field_71095_bQ - par1EntityClone.field_71096_bN) * (double)par2 - (par1EntityClone.prevPosY + (par1EntityClone.posY - par1EntityClone.prevPosY) * (double)par2);
//            double d2 = par1EntityClone.field_71097_bO + (par1EntityClone.field_71085_bR - par1EntityClone.field_71097_bO) * (double)par2 - (par1EntityClone.prevPosZ + (par1EntityClone.posZ - par1EntityClone.prevPosZ) * (double)par2);
//            f6 = par1EntityClone.prevRenderYawOffset + (par1EntityClone.renderYawOffset - par1EntityClone.prevRenderYawOffset) * par2;
//            double d3 = (double)MathHelper.sin(f6 * (float)Math.PI / 180.0F);
//            double d4 = (double)(-MathHelper.cos(f6 * (float)Math.PI / 180.0F));
//            float f7 = (float)d1 * 10.0F;
//
//            if (f7 < -6.0F)
//            {
//                f7 = -6.0F;
//            }
//
//            if (f7 > 32.0F)
//            {
//                f7 = 32.0F;
//            }
//
//            float f8 = (float)(d0 * d3 + d2 * d4) * 100.0F;
//            float f9 = (float)(d0 * d4 - d2 * d3) * 100.0F;
//
//            if (f8 < 0.0F)
//            {
//                f8 = 0.0F;
//            }
//
//            float f10 = par1EntityClone.prevCameraYaw + (par1EntityClone.cameraYaw - par1EntityClone.prevCameraYaw) * par2;
//            f7 += MathHelper.sin((par1EntityClone.prevDistanceWalkedModified + (par1EntityClone.distanceWalkedModified - par1EntityClone.prevDistanceWalkedModified) * par2) * 6.0F) * 32.0F * f10;
//
//            if (par1EntityClone.isSneaking())
//            {
//                f7 += 25.0F;
//            }
//
//            GL11.glRotatef(6.0F + f8 / 2.0F + f7, 1.0F, 0.0F, 0.0F);
//            GL11.glRotatef(f9 / 2.0F, 0.0F, 0.0F, 1.0F);
//            GL11.glRotatef(-f9 / 2.0F, 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
//            this.modelBipedMain.renderCloak(0.0625F);
//            GL11.glPopMatrix();
//        }*/
//        float f6;
//        ItemStack itemstack1 = par1EntityClone.inventory.getCurrentItem();
//
//        if (itemstack1 != null/* && event.renderItem*/)
//        {
//            GL11.glPushMatrix();
//            this.modelBipedMain.bipedRightArm.postRender(0.0625F);
//            GL11.glTranslatef(-0.0625F, 0.4375F, 0.0625F);
//
//            //Add if fishing is implemented
//            /*if (par1EntityClone.fishEntity != null)
//            {
//                itemstack1 = new ItemStack(Item.stick);
//            }*/
//
//            EnumAction enumaction = null;
//
//            if (par1EntityClone.getItemInUseCount() > 0)
//            {
//                enumaction = itemstack1.getItemUseAction();
//            }
//
//            float f11;
//
//            IItemRenderer customRenderer = MinecraftForgeClient.getItemRenderer(itemstack1, EQUIPPED);
//            boolean is3D = (customRenderer != null && customRenderer.shouldUseRenderHelper(EQUIPPED, itemstack1, BLOCK_3D));
//            boolean isBlock = itemstack1.itemID < Block.blocksList.length && itemstack1.getItemSpriteNumber() == 0;
//
//            if (is3D || (isBlock && RenderBlocks.renderItemIn3d(Block.blocksList[itemstack1.itemID].getRenderType())))
//            {
//                f11 = 0.5F;
//                GL11.glTranslatef(0.0F, 0.1875F, -0.3125F);
//                f11 *= 0.75F;
//                GL11.glRotatef(20.0F, 1.0F, 0.0F, 0.0F);
//                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//                GL11.glScalef(-f11, -f11, f11);
//            }
//            else if (itemstack1.itemID == Item.bow.itemID)
//            {
//                f11 = 0.625F;
//                GL11.glTranslatef(0.0F, 0.125F, 0.3125F);
//                GL11.glRotatef(-20.0F, 0.0F, 1.0F, 0.0F);
//                GL11.glScalef(f11, -f11, f11);
//                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
//                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//            }
//            else if (Item.itemsList[itemstack1.itemID].isFull3D())
//            {
//                f11 = 0.625F;
//
//                if (Item.itemsList[itemstack1.itemID].shouldRotateAroundWhenRendering())
//                {
//                    GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
//                    GL11.glTranslatef(0.0F, -0.125F, 0.0F);
//                }
//
//                if (par1EntityClone.getItemInUseCount() > 0 && enumaction == EnumAction.block)
//                {
//                    GL11.glTranslatef(0.05F, 0.0F, -0.1F);
//                    GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
//                    GL11.glRotatef(-10.0F, 1.0F, 0.0F, 0.0F);
//                    GL11.glRotatef(-60.0F, 0.0F, 0.0F, 1.0F);
//                }
//
//                GL11.glTranslatef(0.0F, 0.1875F, 0.0F);
//                GL11.glScalef(f11, -f11, f11);
//                GL11.glRotatef(-100.0F, 1.0F, 0.0F, 0.0F);
//                GL11.glRotatef(45.0F, 0.0F, 1.0F, 0.0F);
//            }
//            else
//            {
//                f11 = 0.375F;
//                GL11.glTranslatef(0.25F, 0.1875F, -0.1875F);
//                GL11.glScalef(f11, f11, f11);
//                GL11.glRotatef(60.0F, 0.0F, 0.0F, 1.0F);
//                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
//                GL11.glRotatef(20.0F, 0.0F, 0.0F, 1.0F);
//            }
//
//            float f12;
//            float f13;
//            int j;
//
//            if (itemstack1.getItem().requiresMultipleRenderPasses())
//            {
//                for (j = 0; j < itemstack1.getItem().getRenderPasses(itemstack1.getItemDamage()); ++j)
//                {
//                	
//                    int k = itemstack1.getItem().getColorFromItemStack(itemstack1, j);
//                    f13 = (float)(k >> 16 & 255) / 255.0F;
//                    f12 = (float)(k >> 8 & 255) / 255.0F;
//                    f6 = (float)(k & 255) / 255.0F;
//                    GL11.glColor4f(f13, f12, f6, 1.0F);
//                    this.renderManager.itemRenderer.renderItem(par1EntityClone, itemstack1, j);
//                }
//            }
//            else
//            {
//                j = itemstack1.getItem().getColorFromItemStack(itemstack1, 0);
//                float f14 = (float)(j >> 16 & 255) / 255.0F;
//                f13 = (float)(j >> 8 & 255) / 255.0F;
//                f12 = (float)(j & 255) / 255.0F;
//                GL11.glColor4f(f14, f13, f12, 1.0F);
//                this.renderManager.itemRenderer.renderItem(par1EntityClone, itemstack1, 0);
//            }
//
//            GL11.glPopMatrix();
//        }
////        MinecraftForge.EVENT_BUS.post(new RenderPlayerEvent.Specials.Post(par1EntityClone, this, par2));
//    }
//
////    protected void renderPlayerScale(EntityClone par1EntityClone, float par2)
////    {
////        float f1 = par1EntityClone.geneScaling*0.9375F;
////        GL11.glScalef(f1, f1, f1);
////    }
////
////    protected void func_96450_a(EntityClone par1EntityClone, double par2, double par4, double par6, String par8Str, float par9, double par10)
////    {
////        if (par10 < 100.0D)
////        {
////            Scoreboard scoreboard = par1EntityClone.getWorldScoreboard();
////            ScoreObjective scoreobjective = scoreboard.func_96539_a(2);
////
////            if (scoreobjective != null)
////            {
////                Score score = scoreboard.func_96529_a(par1EntityClone.getEntityName(), scoreobjective);
////
////                if (par1EntityClone.isPlayerSleeping())
////                {
////                    this.renderLivingLabel(par1EntityClone, score.getScorePoints() + " " + scoreobjective.getDisplayName(), par2, par4 - 1.5D, par6, 64);
////                }
////                else
////                {
////                    this.renderLivingLabel(par1EntityClone, score.getScorePoints() + " " + scoreobjective.getDisplayName(), par2, par4, par6, 64);
////                }
////
////                par4 += (double)((float)this.getFontRendererFromRenderManager().FONT_HEIGHT * 1.15F * par9);
////            }
////        }
////
////        super.func_96449_a(par1EntityClone, par2, par4, par6, par8Str, par9, par10);
////    }
//
//    public void renderFirstPersonArm(EntityPlayer par1EntityPlayer)
//    {
//        float f = 1.0F;
//        GL11.glColor3f(f, f, f);
//        this.modelBipedMain.onGround = 0.0F;
//        this.modelBipedMain.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, par1EntityPlayer);
//        this.modelBipedMain.bipedRightArm.render(0.0625F);
//    }
//
//    /**
//     * Renders player with sleeping offset if sleeping
//     */
//    protected void renderPlayerSleep(EntityClone par1EntityClone, double par2, double par4, double par6)
//    {
//    	super.renderLivingAt(par1EntityClone, par2, par4, par6);
///*        if (par1EntityClone.isEntityAlive() && par1EntityClone.isPlayerSleeping())
//        {
//            super.renderLivingAt(par1EntityClone, par2 + (double)par1EntityClone.field_71079_bU, par4 + (double)par1EntityClone.field_71082_cx, par6 + (double)par1EntityClone.field_71089_bV);
//        }
//        else
//        {
//        	super.renderLivingAt(par1EntityClone, par2, par4, par6);
//        }*/
//    }
//
//    /**
//     * Rotates the player if the player is sleeping. This method is called in rotateCorpse.
//     */
//    protected void rotatePlayer(EntityClone par1EntityClone, float par2, float par3, float par4)
//    {
//    	super.rotateCorpse(par1EntityClone, par2, par3, par4);
///*        if (par1EntityClone.isEntityAlive() && par1EntityClone.isPlayerSleeping())
//        {
//            GL11.glRotatef(par1EntityClone.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
//            GL11.glRotatef(this.getDeathMaxRotation(par1EntityClone), 0.0F, 0.0F, 1.0F);
//            GL11.glRotatef(270.0F, 0.0F, 1.0F, 0.0F);
//        }
//        else
//        {
//            super.rotateCorpse(par1EntityClone, par2, par3, par4);
//        }*/
//    }
//
//    protected void func_96449_a(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, String par8Str, float par9, double par10)
//    {
//        this.func_96450_a((EntityClone)par1EntityLivingBase, par2, par4, par6, par8Str, par9, par10);
//    }
//
//    /**
//     * Allows the render to do any OpenGL state modifications necessary before the model is rendered. Args:
//     * entityLiving, partialTickTime
//     */
//    protected void preRenderCallback(EntityLivingBase par1EntityLivingBase, float par2)
//    {
//        this.renderPlayerScale((EntityClone)par1EntityLivingBase, par2);
//    }
//
//    protected void func_82408_c(EntityLivingBase par1EntityLivingBase, int par2, float par3)
//    {
//        this.func_130220_b((EntityClone)par1EntityLivingBase, par2, par3);
//    }
//
//    /**
//     * Queries whether should render the specified pass or not.
//     */
//    protected int shouldRenderPass(EntityLivingBase par1EntityLivingBase, int par2, float par3)
//    {
//        return this.setArmorModel((EntityClone)par1EntityLivingBase, par2, par3);
//    }
//
//    protected void renderEquippedItems(EntityLivingBase par1EntityLivingBase, float par2)
//    {
//        this.renderSpecials((EntityClone)par1EntityLivingBase, par2);
//    }
//
//    protected void rotateCorpse(EntityLivingBase par1EntityLivingBase, float par2, float par3, float par4)
//    {
//        this.rotatePlayer((EntityClone)par1EntityLivingBase, par2, par3, par4);
//    }
//
//    /**
//     * Sets a simple glTranslate on a LivingEntity.
//     */
//    protected void renderLivingAt(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6)
//    {
//        this.renderPlayerSleep((EntityClone)par1EntityLivingBase, par2, par4, par6);
//    }
//
//    public void func_130000_a(EntityLivingBase par1EntityLivingBase, double par2, double par4, double par6, float par8, float par9)
//    {
//        this.func_130009_a((EntityClone)par1EntityLivingBase, par2, par4, par6, par8, par9);
//    }
//
//    protected ResourceLocation getEntityTexture(Entity par1Entity)
//    {
//        return this.getEntityTexture((EntityClone)par1Entity);
//    }
//
//    /**
//     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
//     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
//     * (Render<T extends Entity) and this method has signature public void doRender(T entity, double d, double d1,
//     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
//     */
//    public void doRender(Entity par1Entity, double par2, double par4, double par6, float par8, float par9)
//    {
//        this.func_130009_a((EntityClone)par1Entity, par2, par4, par6, par8, par9);
//        
//    }
}
