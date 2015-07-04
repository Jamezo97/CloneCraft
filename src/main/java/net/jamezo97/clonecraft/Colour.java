package net.jamezo97.clonecraft;

public class Colour {
	
	private int colour;
	
	private int red, green, blue, alpha;
	
	private float redF, greenF, blueF, alphaF;
	
	public Colour(int colour)
	{
		setColour(colour);
	}
	
	public void setColour(int colour)
	{
		this.colour = colour;
		this.red = (this.colour >> 16) & 0xFF;
		this.green = (this.colour >> 8) & 0xFF;
		this.blue = (this.colour) & 0xFF;
		this.alpha = (this.colour >> 24) & 0xFF;

		this.redF = this.red / 255.0f;
		this.greenF = this.green / 255.0f;
		this.blueF = this.blue / 255.0f;
		this.alphaF = this.alpha / 255.0f;
	}
	
	public void setColourI(int red, int green, int blue, int alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		
		this.colour = (alpha << 24) | (red << 16) | (green << 8) | blue;

		this.redF = this.red / 255.0f;
		this.greenF = this.green / 255.0f;
		this.blueF = this.blue / 255.0f;
		this.alphaF = this.alpha / 255.0f;
	}
	
	public void setColourF(float red, float green, float blue, float alpha)
	{
		this.redF = red;
		this.greenF = green;
		this.blueF = blue;
		this.alphaF = alpha;
		
		this.red = Math.round(red * 255.0f);
		this.green = Math.round(green * 255.0f);
		this.blue = Math.round(blue * 255.0f);
		this.alpha = Math.round(alpha * 255.0f);
		
		this.colour = (this.alpha << 24) | (this.red << 16) | (this.green << 8) | this.blue;
		
	}
	
	public int getRed()
	{
		return red;
	}
	public int getGreen()
	{
		return green;
	}
	public int getBlue()
	{
		return blue;
	}
	public int getAlpha()
	{
		return alpha;
	}
	
	
	public float getRedF()
	{
		return redF;
	}
	public float getGreenF()
	{
		return greenF;
	}
	public float getBlueF()
	{
		return blueF;
	}
	public float getAlphaF()
	{
		return alphaF;
	}
	
	
	public int getColour()
	{
		return colour;
	}
}
