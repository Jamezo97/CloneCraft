package net.jamezo97.clonecraft.clone.sync;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

public class ByteOut {
	
	ByteArrayOutputStream byteOut = null;
	DataOutputStream out = null;
	
	private boolean isOpen = false;
	
	public ByteOut(){
		try{
			byteOut = new ByteArrayOutputStream();
			out = new DataOutputStream(byteOut);
			isOpen = true;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public byte[] close(){
		if(out != null){
			try{
				isOpen = false;
				out.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		if(byteOut != null){
			return byteOut.toByteArray();
		}
		return null;
	}

	public boolean isOpen(){
		return isOpen;
	}

	public DataOutputStream getDataStream() {
		return out;
	}
	
}
