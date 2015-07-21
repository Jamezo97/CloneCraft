package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.OutputStream;

public class ByteBufOutput extends OutputStream{


	ByteBuf buf;

	public ByteBufOutput(ByteBuf buf) 
	{
		this.buf = buf;
	}
	
	@Override
	public void write(int paramInt) throws IOException 
	{
		buf.writeByte(paramInt);
	}

	@Override
	public void write(byte[] paramArrayOfByte) throws IOException 
	{
		buf.writeBytes(paramArrayOfByte);
	}

	@Override
	public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2) throws IOException 
	{
		buf.writeBytes(paramArrayOfByte, paramInt1, paramInt2);
	}

	@Override
	public void flush() throws IOException {
		
	}

	@Override
	public void close() throws IOException {
		
	}
	

}
