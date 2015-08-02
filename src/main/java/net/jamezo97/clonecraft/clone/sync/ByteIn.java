package net.jamezo97.clonecraft.clone.sync;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteIn
{

	ByteArrayInputStream byteIn = null;
	DataInputStream in = null;

	boolean isOpen = false;

	public ByteIn(byte[] data)
	{
		try
		{
			byteIn = new ByteArrayInputStream(data);
			in = new DataInputStream(byteIn);
			isOpen = true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		if (in != null)
		{
			try
			{
				in.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		isOpen = false;
	}

	public DataInputStream getDataStream()
	{
		return in;
	}

	public boolean isOpen()
	{
		return isOpen;
	}

}
