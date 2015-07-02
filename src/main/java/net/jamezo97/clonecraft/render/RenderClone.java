package net.jamezo97.clonecraft.render;

import java.util.Random;
import java.util.UUID;

import net.jamezo97.clonecraft.clone.EntityClone;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.tileentity.TileEntitySkullRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
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
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

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
                ((ModelBipedCustom)modelbiped).modelBreastLeft.showModel = modelbiped.bipedBody.showModel;
                ((ModelBipedCustom)modelbiped).modelBreastRight.showModel = modelbiped.bipedBody.showModel;
                
                ((ModelBipedCustom)modelbiped).isChestPlate = modelbiped.bipedBody.showModel;
                
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
    private static final ResourceLocation RES_ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    /**
     * Actually renders the given argument. This is a synthetic bridge method, always casting down its argument and then
     * handing it off to a worker function which does the actual work. In all probabilty, the class Render is generic
     * (Render<T extends Entity) and this method has signature public void func_76986_a(T entity, double d, double d1,
     * double d2, float f, float f1). But JAD is pre 1.5 so doesn't do that.
     */
    public void doRender(EntityClone clone, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_)
    {
    	this.shadowSize = 0.5F * clone.getScale();
    	
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
        
        this.textureForPass = clone.getTexture();
        
        
        super.doRender((EntityLivingBase)clone, p_76986_2_, d3, p_76986_6_, p_76986_8_, p_76986_9_);

        
        
        
       
        //Render glint effect maybe.   From 'ItemRenderer'
        
        if(clone.getGrowthFactor() != 0){
        	this.textureForPass = RES_ITEM_GLINT;
        	
        	//Max shrinking factor:
        	//0.02005
        	//Max growing factor:
        	//0.03005
        	
        	float growFactor01 = Math.abs(clone.getGrowthFactor()) / 0.03f*20;
        	
        	float intensity = Math.min(0.1f + 0.9f * growFactor01, 0.9f);
        	
        	float f8 = (float)clone.ticksExisted + p_76986_9_;
            this.bindTexture(RES_ITEM_GLINT);
            GL11.glEnable(GL11.GL_BLEND);
            float f9 = 0.05F;
            GL11.glColor4f(f9, f9, f9, 1.0F);
            GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDepthMask(false);

            for (int k = 0; k < 3; ++k)
            {
                GL11.glDisable(GL11.GL_LIGHTING);
//                float f10 = 1.0F;
                GL11.glColor4f(0.5F * intensity, 0.25F * intensity, 0.8F * intensity, 1.0F);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                GL11.glMatrixMode(GL11.GL_TEXTURE);
                GL11.glLoadIdentity();
                float f11 = f8 * (0.001F + (float)k * 0.003F) * 20.0F;
                float f12 = 0.33333334F;
                GL11.glScalef(f12, f12, f12);
                GL11.glRotatef(30.0F - (float)k * 60.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(0.0F, f11, 0.0F);
                GL11.glMatrixMode(GL11.GL_MODELVIEW);

                {
                	GL11.glPushMatrix();
                	
                	float f2 = this.interpolateRotation(clone.prevRenderYawOffset, clone.renderYawOffset, p_76986_9_);
                    float f3 = this.interpolateRotation(clone.prevRotationYawHead, clone.rotationYawHead, p_76986_9_);
                    float f4;

                    float f13 = clone.prevRotationPitch + (clone.rotationPitch - clone.prevRotationPitch) * p_76986_9_;
                    super.renderLivingAt(clone, p_76986_2_, p_76986_4_, p_76986_6_);
                    f4 = this.handleRotationFloat(clone, p_76986_9_);
                    super.rotateCorpse(clone, f4, f2, p_76986_9_);
                    float f5 = 0.0625F;
                    GL11.glEnable(GL12.GL_RESCALE_NORMAL);
                    GL11.glScalef(-1.0F, -1.0F, 1.0F);
                    this.preRenderCallback(clone, p_76986_9_);
                    GL11.glTranslatef(0.0F, -24.0F * f5 - 0.0078125F, 0.0F);
                    float f6 = clone.prevLimbSwingAmount + (clone.limbSwingAmount - clone.prevLimbSwingAmount) * p_76986_9_;
                    float f7 = clone.limbSwing - clone.limbSwingAmount * (1.0F - p_76986_9_);

                    if (clone.isChild())
                    {
                        f7 *= 3.0F;
                    }

                    if (f6 > 1.0F)
                    {
                        f6 = 1.0F;
                    }

                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    this.mainModel.setLivingAnimations(clone, f7, f6, p_76986_9_);
                    this.renderModel(clone, f7, f6, f4, f3 - f2, f13, f5);
                    
                    GL11.glPopMatrix();
                }
                
                
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glDepthMask(true);
            GL11.glLoadIdentity();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        	
        	/*GL11.glDepthFunc(GL11.GL_EQUAL);
            GL11.glDisable(GL11.GL_LIGHTING);
            Minecraft.getMinecraft().renderEngine.bindTexture(RES_ITEM_GLINT);
            GL11.glEnable(GL11.GL_BLEND);
            OpenGlHelper.glBlendFunc(768, 1, 1, 0);
            float f7 = 0.76F;
            GL11.glColor4f(0.5F * f7, 0.25F * f7, 0.8F * f7, 1.0F);
            GL11.glMatrixMode(GL11.GL_TEXTURE);
            GL11.glPushMatrix();
            float f8 = 0.125F;
            GL11.glScalef(f8, f8, f8);
            float f9 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F * 8.0F;
            GL11.glTranslatef(f9, 0.0F, 0.0F);
            GL11.glRotatef(-50.0F, 0.0F, 0.0F, 1.0F);
            super.doRender((EntityLivingBase)clone, p_76986_2_, d3, p_76986_6_, p_76986_8_, p_76986_9_);
//            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glScalef(f8, f8, f8);
            f9 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F * 8.0F;
            GL11.glTranslatef(-f9, 0.0F, 0.0F);
            GL11.glRotatef(10.0F, 0.0F, 0.0F, 1.0F);
//            super.doRender((EntityLivingBase)clone, p_76986_2_, d3, p_76986_6_, p_76986_8_, p_76986_9_);
//            renderItemIn2D(tessellator, 0.0F, 0.0F, 1.0F, 1.0F, 256, 256, 0.0625F);
            GL11.glPopMatrix();
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glDepthFunc(GL11.GL_LEQUAL);*/
        }
        
        
        
        
        this.modelArmorChestplate.aimedBow = this.modelArmor.aimedBow = this.modelBipedMain.aimedBow = false;
        this.modelArmorChestplate.isSneak = this.modelArmor.isSneak = this.modelBipedMain.isSneak = false;
        this.modelArmorChestplate.isEating = this.modelBipedMain.isEating = false;
        this.modelArmorChestplate.heldItemRight = this.modelArmor.heldItemRight = this.modelBipedMain.heldItemRight = 0;
        
      
        if(clone.getOptions().stats.get() && clone.canUseThisEntity(Minecraft.getMinecraft().thePlayer)){
        	renderStatus(clone,Math.round(clone.getHealth()) + "/" + Math.round(clone.getMaxHealth()), clone.getHealth() / clone.getMaxHealth(), p_76986_2_, p_76986_4_, p_76986_6_, p_76986_9_, 0);
        	renderStatus(clone, clone.foodStats.getFoodLevel() + "/20", clone.foodStats.getFoodLevel()/20.0f, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_9_, 1);
        }
    }
    
    private float interpolateRotation(float p_77034_1_, float p_77034_2_, float p_77034_3_)
    {
        float f3;

        for (f3 = p_77034_2_ - p_77034_1_; f3 < -180.0F; f3 += 360.0F)
        {
            ;
        }

        while (f3 >= 180.0F)
        {
            f3 -= 360.0F;
        }

        return p_77034_1_ + p_77034_3_ * f3;
    }
    
    ResourceLocation textureForPass = null;
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityClone p_110775_1_)
    {
        return p_110775_1_.getTexture();
    }
    
    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(Entity p_110775_1_)
    {
        return textureForPass;//this.getEntityTexture((EntityClone)p_110775_1_);
    }
    
    public void renderStatus(EntityClone clone, String s, float done, double x, double y, double z, float partial, int type)
    {
    	float f = 1.0f/256.0f;
    	double d3 = clone.getDistanceSqToEntityIncEye(this.renderManager.livingPlayer) / clone.getScale();
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
            
            fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, -1);
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
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(true);
            GL11.glDisable(GL11.GL_BLEND);
            
            GL11.glEnable(GL11.GL_LIGHTING);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    protected void passSpecialRender(EntityLivingBase entity, double posX, double posY, double posZ)
    {
        float red = 0, green = 0, blue = 0;
        EntityClone clone = (EntityClone)entity;
        red = ((float)((clone.getCTeam().teamColour >> 16) & 0xff))/255.0f;
        green = ((float)((clone.getCTeam().teamColour >> 8) & 0xff))/255.0f;
        blue = ((float)((clone.getCTeam().teamColour) & 0xff))/255.0f;
    	
    	
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
                    GL11.glTranslatef((float)posX + 0.0F, (float)posY + entity.height + 0.5F, (float)posZ);
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
                    this.func_96449_a(entity, posX, posY, posZ, s, f1, d3);
                }
            }
        }
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

        float f4;

        ItemStack s1 = clone.getHeldItem();
        ItemStack s2 = clone.getOfferedItem();
        if (s1 != null && (s1 == null || s2 == null || s1.getItem() != s2.getItem() || s1.getItemDamage() != s2.getItemDamage() || s1.stackSize != s2.stackSize))
        {
            renderItemstackInHand(s1, 0, clone, partial);
        }
        if (s2 != null)
        {
            renderItemstackInHand(s2, 1, clone, partial);
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
    	
//    	System.out.println(p_77041_2_);
    	
        float f1 = 0.9375F * p_77041_1_.getInterpolatedScale(p_77041_2_);
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

    
    
    int temp = 0;

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
        	red = ((float)((clone.getCTeam().teamColour >> 16) & 0xff))/255.0f;
        	green = ((float)((clone.getCTeam().teamColour >> 8) & 0xff))/255.0f;
        	blue = ((float)((clone.getCTeam().teamColour) & 0xff))/255.0f;
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
}
