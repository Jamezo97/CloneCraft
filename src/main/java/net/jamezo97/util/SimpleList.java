package net.jamezo97.util;

public class SimpleList<E> {
	
	Object[] data;
	
	int size = 0;
	
	public SimpleList(int defaultSize){
		data = new Object[defaultSize];
	}
	
	public SimpleList(){
		this(10);
	}
	
	public int size(){
		return size;
	}
	
	public void add(E value){
		ensureCapacity(size+1);
		data[size++] = value;
	}
	
	public E get(int index){
		if(index >= 0 && index < size){
			return (E)data[index];
		}else{
			throw new ArrayIndexOutOfBoundsException(index + " is not within 0 and " + size);
		}
	}
	
	private void ensureCapacity(int len){
		if(len > size){
			Object[] newData = new Object[len+10];
			System.arraycopy(data, 0, newData, 0, data.length);
			this.data = newData;
		}
	}
	
	

}
