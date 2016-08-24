package net.jamezo97.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class WavefrontOBJ
{
	
	public WavefrontOBJ(InputStream input) throws IOException
	{
		this.loadFromStream(input);
	}
	
	public WavefrontOBJ(File file) throws IOException
	{
		this(new FileInputStream(file));
	}
	
	private void loadFromStream(InputStream stream) throws IOException
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		String line = reader.readLine();
		
		while(line != null)
		{
			line = line.trim();
			
			//If it's not a comment
			if(!line.startsWith("#"))
			{
				
			}
			
			
			
			line = reader.readLine();
		}
		
		reader.close();
	}

}
