package net.jamezo97.framebuffer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import net.minecraft.client.Minecraft;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.EXTFramebufferObject;
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

		checkStatus();
		
		if(this.FBO_TYPE == -1)
		{
			//Frame buffers aren't supported.. Maybe.. Throw an error?
			//Maybe change this to a texture that just tells you it's invalid?
//			throw new FBException("Frame Buffer's Aren't Supported!");
		}
		
		this.renderWidth = width;
		this.renderHeight = height;
		
//		texWidth = width;//ceilToNearestPower(width);
//		texHeight = height;//ceilToNearestPower(height);
		
		texWidth = ceilToNearestPower(width);
		texHeight = ceilToNearestPower(height);
		
//		System.out.println(texWidth + ", " + texHeight);
		
		ByteBuffer beginImage = null;
		
		if(this.FBO_TYPE == -1)
		{
			BufferedImage image = this.getMissingImage(texWidth, texHeight, width, height);
			beginImage = convertedImageToByteBuffer(image);
		}
		

		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GLTexBufferId = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, GLTexBufferId);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, texWidth, texHeight, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, beginImage);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		
		if(this.FBO_TYPE == 0)
		{
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
//				    	System.out.println("AAALLLLLLLLLLGGGGGGGGOOOOOOOOOOODDDDDDDDDDDDD!!!");
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
			
		}
		else if(this.FBO_TYPE == 1)
		{
			GLFrameBufferId = EXTFramebufferObject.glGenFramebuffersEXT();
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, GLFrameBufferId);
			
			
			EXTFramebufferObject.glFramebufferTexture2DEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT, GL11.GL_TEXTURE_2D, this.GLTexBufferId, 0);
			
			
			GLDepthBufferId = EXTFramebufferObject.glGenRenderbuffersEXT();
			EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GLDepthBufferId);
			EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, GL11.GL_DEPTH_COMPONENT, texWidth, texHeight);
			EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, GLDepthBufferId);

			
			
			int status = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
			try{
				switch(status) {
				    case EXTFramebufferObject.GL_FRAMEBUFFER_COMPLETE_EXT:
//				    	System.out.println("AAALLLLLLLLLLGGGGGGGGOOOOOOOOOOODDDDDDDDDDDDD!!!");
				        break;
					case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_EXT:
					    throw new Exception("An attachment could not be bound to frame buffer object!");
					case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_EXT:
					    throw new Exception("Attachments are missing! At least one image (texture) must be bound to the frame buffer object!");
					case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_EXT:
					    throw new Exception("A Draw buffer is incomplete or undefinied. All draw buffers must specify attachment points that have images attached.");
					case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_EXT:
					    throw new Exception("A Read buffer is incomplete or undefinied. All read buffers must specify attachment points that have images attached.");
					case EXTFramebufferObject.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_EXT:
					    throw new Exception("All images must have the same number of multisample samples.");
					case EXTFramebufferObject.GL_FRAMEBUFFER_UNSUPPORTED_EXT:
					    throw new Exception("Attempt to use an unsupported format combinaton!");
					default:
					    throw new Exception("Unknown error while attempting to create frame buffer object!");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 1);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		setClearColour(0xff000000);
	}
	
	public void destroy(){
		GL11.glDeleteTextures(GLTexBufferId);
		
		if(this.FBO_TYPE == 0)
		{
			GL30.glDeleteRenderbuffers(GLDepthBufferId);
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
			GL30.glDeleteFramebuffers(GLFrameBufferId);
		}
		else if(this.FBO_TYPE == 1)
		{
			EXTFramebufferObject.glDeleteRenderbuffersEXT(GLDepthBufferId);
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 1);
			EXTFramebufferObject.glDeleteFramebuffersEXT(GLFrameBufferId);
		}
		
		
	}

	
	
	
	
	
	public static int ceilToNearestPower(int value){
		return (int)Math.pow(2, (Math.ceil(Math.log10(value) / Math.log10(2))));
	}

	public void bindFrameBuffer() {
		if(this.FBO_TYPE == 0)
		{
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, GLFrameBufferId);
		}
		else if(this.FBO_TYPE == 1)
		{
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, GLFrameBufferId);
		}
	}
	
	public void unbindFrameBuffer() {
		if(this.FBO_TYPE == 0)
		{
			GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		}
		else if(this.FBO_TYPE == 1)
		{
			EXTFramebufferObject.glBindFramebufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, 1);
		}
		
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
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glShadeModel( GL11.GL_SMOOTH );
		   //-------------------------
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glScalef(1, -1.0f, 1);
		GL11.glTranslatef(0, -this.renderHeight, 0);
	}
	
	public void revertRender() {
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glViewport(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glLoadMatrix(projectionMatrix);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
        GL11.glLoadMatrix(modelMatrix);

	}
	
	public void beginRender(){
		if(FBO_TYPE != -1)
		{
			bindFrameBuffer();
			GL11.glPushMatrix();
			setupRender();
		}
		else
		{
			GL11.glPushMatrix();
			GL11.glTranslated(0, 0, -10000);
		}
		
	}
	
	public void endRender(){
		if(FBO_TYPE != -1)
		{
			revertRender();
			GL11.glPopMatrix();
			unbindFrameBuffer();
		}
		else
		{
			GL11.glPopMatrix();
		}
		
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
	
	
	public static ByteBuffer convertedImageToByteBuffer(BufferedImage image){
		int[] pixels = image.getRGB(0, 0, image.getWidth(), image.getHeight(), new int[image.getWidth()*image.getHeight()], 0, image.getWidth());
		
		ByteBuffer bb = BufferUtils.createByteBuffer(image.getWidth()*image.getHeight()*4);
		
		for(int i = 0; i < pixels.length; i++){
			byte a = (byte)((pixels[i] >> 24) & 0xff);
			byte r = (byte)((pixels[i] >> 16) & 0xff);
			byte g = (byte)((pixels[i] >> 8) & 0xff);
			byte b = (byte)(pixels[i] & 0xff);
			bb.put(r);
			bb.put(g);
			bb.put(b);
			bb.put(a);
		}
		
		bb.flip();
		
		return bb;
	}
	
	public static BufferedImage getMissingImage(int texWidth, int texHeight, int width, int height)
	{
		String[] messages = new String[]{"FRAME BUFFERS", "ARE NOT SUPPORTED", "",
				"Who ever programmed this", "should have accounted for this.", ""};
		BufferedImage theImage = new BufferedImage(texWidth, texHeight, BufferedImage.TYPE_INT_ARGB);
		
		Graphics g = theImage.getGraphics();
		
		g.setColor(Color.MAGENTA);
		g.fillRect(0, 0, texWidth, texHeight);
		
		g.setFont(new Font("Arial", Font.BOLD, 16));
		
		g.setColor(Color.BLACK);
		
		FontMetrics fm = g.getFontMetrics();
		
		int fontHeight = fm.getHeight();
		
		int beginY = (height - (messages.length * fontHeight))/2;
		
		
		
		for(int a = 0; a < messages.length; a++)
		{
			String message = messages[a];
			int beginX = (width-fm.stringWidth(message))/2;

			g.drawString(message, beginX, beginY + a*fontHeight);
			
		}
		
		g.dispose();
		
		return theImage;
	}
	
	//No FrameBuffer type is supported.
	static int FBO_TYPE = -1;
	
	private static boolean checkedStatus = false;
	
	public static void checkStatus(){
		if(checkedStatus)
		{
			return;
		}
		checkedStatus = true;
		
		int code = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
		if(code != GL30.GL_FRAMEBUFFER_UNSUPPORTED){
			//Frame Buffers should be supported?
			FBO_TYPE = 0;
			return;
		}
		code = EXTFramebufferObject.glCheckFramebufferStatusEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT);
		if(code != EXTFramebufferObject.GL_FRAMEBUFFER_UNSUPPORTED_EXT){
			//Frame Buffer Objects should be supported?
			FBO_TYPE = 1;
			return;
		}
		
		//They're not supported at all!
		FBO_TYPE = -1;
	}
}
