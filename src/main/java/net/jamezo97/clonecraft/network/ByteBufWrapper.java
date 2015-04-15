package net.jamezo97.clonecraft.network;

import io.netty.buffer.ByteBuf;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ByteBufWrapper implements DataOutput, DataInput{

	ByteBuf b;
	
	public ByteBufWrapper(ByteBuf buf){
		setByteBuffer(buf);
	}
	
	public void setByteBuffer(ByteBuf buf){
		this.b = buf;
	}
	
	@Override
	public void readFully(byte[] paramArrayOfByte) throws IOException {
		b.readBytes(paramArrayOfByte);
	}

	@Override
	public void readFully(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
			throws IOException {
		b.readBytes(paramArrayOfByte, paramInt1, paramInt2);
		
	}

	@Override
	public int skipBytes(int paramInt) throws IOException {
		b.skipBytes(paramInt);
		return paramInt;
	}

	@Override
	public boolean readBoolean() throws IOException {
		return b.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return b.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return b.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		return b.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return b.readUnsignedShort();
	}

	@Override
	public char readChar() throws IOException {
		return b.readChar();
	}

	@Override
	public int readInt() throws IOException {
		return b.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return b.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		return b.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return b.readDouble();
	}

	@Override
	public String readLine() throws IOException {
		int len = b.readInt();
		String s = "";
		for(int a = 0; a < len; a++){
			s += b.readChar();
		}
		return s;
	}

	@Override
	public String readUTF() throws IOException {
		int len = b.readInt();
		String s = "";
		for(int a = 0; a < len; a++){
			s += b.readChar();
		}
		return s;
	}


	@Override
	public void write(int paramInt) throws IOException {
		b.writeByte(paramInt);
	}

	@Override
	public void write(byte[] paramArrayOfByte) throws IOException {
		b.writeBytes(paramArrayOfByte);
	}

	@Override
	public void write(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
			throws IOException {
		b.writeBytes(paramArrayOfByte, paramInt1, paramInt2);
	}

	@Override
	public void writeBoolean(boolean paramBoolean) throws IOException {
		b.writeBoolean(paramBoolean);
	}

	@Override
	public void writeByte(int paramInt) throws IOException {
		b.writeByte(paramInt);
	}

	@Override
	public void writeShort(int paramInt) throws IOException {
		b.writeShort(paramInt);
	}

	@Override
	public void writeChar(int paramInt) throws IOException {
		b.writeChar(paramInt);
	}

	@Override
	public void writeInt(int paramInt) throws IOException {
		b.writeInt(paramInt);
	}

	@Override
	public void writeLong(long paramLong) throws IOException {
		b.writeLong(paramLong);
	}

	@Override
	public void writeFloat(float paramFloat) throws IOException {
		b.writeFloat(paramFloat);
	}

	@Override
	public void writeDouble(double paramDouble) throws IOException {
		b.writeDouble(paramDouble);
	}

	@Override
	public void writeBytes(String paramString) throws IOException {
		int i = paramString.length();
	    for (int j = 0; j < i; j++) {
	      this.b.writeByte((byte)paramString.charAt(j));
	    }
	}

	@Override
	public void writeChars(String paramString) throws IOException {
		int i = paramString.length();
	    for (int j = 0; j < i; j++)
	    {
	      int k = paramString.charAt(j);
	      this.b.writeByte(k >>> 8 & 0xFF);
	      this.b.writeByte(k >>> 0 & 0xFF);
	    }
	}

	@Override
	public void writeUTF(String paramString) throws IOException {
		b.writeInt(paramString.length());
		for(int a = 0; a < paramString.length(); a++){
			b.writeChar(paramString.charAt(a));
		}
	}

}
