package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;

public class ByteBufInput extends InputStream{
	
	ByteBuf buf;
	
	public ByteBufInput(ByteBuf buf)
	{
		this.buf = buf;
	}

	@Override
	public int read() throws IOException 
	{
		return buf.readByte() & 0xFF;
	}

	@Override
	public int read(byte[] paramArrayOfByte) throws IOException 
	{
		int read = paramArrayOfByte.length;
		
		if(read > buf.readableBytes())
		{
			read = buf.readableBytes();
		}
		
		buf.readBytes(paramArrayOfByte, 0, read);
		
		return read;
	}

	@Override
	public int read(byte[] paramArrayOfByte, int index, int length)
			throws IOException 
	{
		if(length > buf.readableBytes())
		{
			length = buf.readableBytes();
		}
		
		buf.readBytes(paramArrayOfByte, index, length);
		
		return length;
	}

	@Override
	public long skip(long paramLong) throws IOException 
	{
		long skip = paramLong;
		if(skip > buf.readableBytes())
		{
			skip = buf.readableBytes();
		}
		buf.readerIndex(buf.readerIndex() + (int)skip);
		
		return skip;
	}

	@Override
	public int available() throws IOException {
		return buf.readableBytes();
	}

	@Override
	public void close() throws IOException {
		
	}

	@Override
	public synchronized void mark(int paramInt) {

	}

	@Override
	public synchronized void reset() throws IOException {

	}

	@Override
	public boolean markSupported() {
		return false;
	}

	
	
}
