package net.jamezo97.framebuffer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class FrameBuffer {
	
	int renderWidth;
	int renderHeight;
	int texWidth;
	int texHeight;
	
	int GLFrameBufferId;
	int GLTexBufferId;
	int GLDepthBufferId;
	
	public FrameBuffer(int width, int height){
		this.renderWidth = width;
		this.renderHeight = height;
		
//		texWidth = width;//ceilToNearestPower(width);
//		texHeight = height;//ceilToNearestPower(height);
		
		texWidth = ceilToNearestPower(width);
		texHeight = ceilToNearestPower(height);
		
//		System.out.println(texWidth + ", " + texHeight);
		

		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GLTexBufferId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GLTexBufferId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texWidth, texHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer)null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		
		GLFrameBufferId = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFrameBufferId);
		
		
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, this.GLTexBufferId, 0);
		
		
		GLDepthBufferId = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, GLDepthBufferId);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, texWidth, texHeight);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, GLDepthBufferId);

		
		
		int status = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		try{
			switch(status) {
			    case GL30.GL_FRAMEBUFFER_COMPLETE:
//			    	System.out.println("AAALLLLLLLLLLGGGGGGGGOOOOOOOOOOODDDDDDDDDDDDD!!!");
			        break;
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
				    throw new Exception("An attachment could not be bound to frame buffer object!");
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
				    throw new Exception("Attachments are missing! At least one image (texture) must be bound to the frame buffer object!");
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER:
				    throw new Exception("A Draw buffer is incomplete or undefinied. All draw buffers must specify attachment points that have images attached.");
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER:
				    throw new Exception("A Read buffer is incomplete or undefinied. All read buffers must specify attachment points that have images attached.");
				case GL30.GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE:
				    throw new Exception("All images must have the same number of multisample samples.");
				case GL30.GL_FRAMEBUFFER_UNSUPPORTED:
				    throw new Exception("Attempt to use an unsupported format combinaton!");
				default:
				    throw new Exception("Unknown error while attempting to create frame buffer object!");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		setClearColour(0xff000000);
	}
	
	public void destroy(){
		GL11.glDeleteTextures(GLTexBufferId);
		GL30.glDeleteRenderbuffers(GLDepthBufferId);
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		GL30.glDeleteFramebuffers(GLFrameBufferId);
	}

	
	
	
	
	
	public static int ceilToNearestPower(int value){
		return (int)Math.pow(2, (Math.ceil(Math.log10(value) / Math.log10(2))));
	}

	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFrameBufferId);
	}
	
	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
	}
	
	public void bindTexture() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GLTexBufferId);
	}
	
	FloatBuffer projectionMatrix = BufferUtils.createFloatBuffer(16);
	
	FloatBuffer modelMatrix = BufferUtils.createFloatBuffer(16);

	public void setupRender() {
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projectionMatrix);
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelMatrix);
		
		
		GL11.glClearColor(clearR, clearG, clearB, clearA);
		GL11.glClearDepth(1.0f);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		   //-------------------------
		GL11.glViewport(0, 0, renderWidth, renderHeight);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, renderWidth, renderHeight, 0.0, -2000.0, 3000.0); 

//		GL11.glOrtho(0.0, renderWidth, 0.0, renderHeight, -2000.0, 3000.0); 
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glShadeModel( GL11.GL_SMOOTH );
		   //-------------------------
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glScalef(1, -1.0f, 1);
		GL11.glTranslatef(0, -this.renderHeight, 0);
//		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}
	
	public void revertRender() {
		GL11.glEnable(GL11.GL_BLEND);
//		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glViewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glLoadMatrix(projectionMatrix);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
        GL11.glLoadMatrix(modelMatrix);
        
        
        
//        ScaledResolution scaledresolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
//        GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
//        GL11.glMatrixMode(GL11.GL_PROJECTION);
//        GL11.glLoadIdentity();
//        GL11.glOrtho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
//        GL11.glMatrixMode(GL11.GL_MODELVIEW);
//        GL11.glLoadIdentity();
//        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
	}
	
	public void beginRender(){
		bindFrameBuffer();
		GL11.glPushMatrix();
		setupRender();
	}
	
	public void endRender(){
		revertRender();
		GL11.glPopMatrix();
		unbindFrameBuffer();
	}

	public float getUMax(){
		return ((float)this.renderWidth) / ((float)this.texWidth);
	}
	
	public float getVMax(){
		return ((float)this.renderHeight) / ((float)this.texHeight);
	}

	float clearR, clearG, clearB, clearA;
	
	public void setClearColour(int i) {
		clearA = (float)((i >> 24) & 0xff) / 255.0f;
		clearR = (float)((i >> 16) & 0xff) / 255.0f;
		clearG = (float)((i >> 8) & 0xff) / 255.0f;
		clearB = (float)(i & 0xff) / 255.0f;
	}
	
}
