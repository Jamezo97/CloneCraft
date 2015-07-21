package net.jamezo97.util;

import java.util.Arrays;


//C - Comparer
//V - Value
public class SortedList<C extends Comparable<C>> {

	private int additionalStepSize = 5;
	
	private int default_size = 5;
	
	Comparable<C>[] data;
	
	int MAX_SIZE = 100000;
	
	int size = 0;
	
	boolean flipped = false;
	
	public SortedList(boolean order)
	{
		data = new Comparable[default_size];
		
		if(!order)
		{
			flipped = true;
		}
	}
	
	public SortedList()
	{
		this(SORT_ASCENDING);
	}
	
	public void clear()
	{
		this.size = 0;
	}
	
	public C add(C comparer)
	{
		insertAtPosition(getPositionForValue(comparer), comparer);
		return comparer;
	}
	
	private int getPositionForValue(C comparer)
	{
		return Arrays.binarySearch(data, 0, size, comparer);
	}
	
	private void insertAtPosition(int position, C comparer)
	{
		if(position < 0)
		{
			position = (-position)-1;
		}
		
		ensureCapacity(size+1);
		
		if(position == size)
		{
			data[position] = comparer;
		}
		else
		{
			//				src, 	srcPos,		dest, offset,  length
			System.arraycopy(data, position, data, position+1, size-position);
			data[position] = comparer;
		}
		size++;
	}
	
	public C remove(int index)
	{
		if(flipped)
		{
			index = size-index-1;
		}
		
		C temp = (C)data[index];
		data[index] = null;
		
		if(index+1 < size)
		{
			System.arraycopy(data, index+1, data, index, size-index-1);
		}
		
		size--;
		return temp;
	}
	
	public C remove(C object)
	{
		int index = this.indexOf(object);
		
		if(index != -1)
		{
			return remove(index);
		}
		return null;
	}
	
	
	private void ensureCapacity(int nSize)
	{
		if(data.length < nSize && nSize < MAX_SIZE)
		{
			Comparable<C>[] newData = new Comparable[data.length+additionalStepSize];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}
		else if(nSize > MAX_SIZE)
		{
			throw new RuntimeException("Cannot exceed max array size of " + MAX_SIZE + " (otherwise performance is exponentially reduced)");
		}
	}
	
	
	
	public C get(int index)
	{
		if(index < 0 || index >= size())
		{
			return null;
		}
		
		if(flipped)
		{
			index = size-index-1;
		}
		return (C)data[index];
	}
	
	private int T_indexOf(C o)
	{
		int index = getPositionForValue(o);
		
		if(index >= 0)
		{
			return index;
		}
		return -1;
	}
	
	public boolean equalCheckObj(Object o1, Object o2)
	{
		if(o1 == null && o2 == null)
		{
			return true;
		}
		else if(o1 != null && o2 != null)
		{
			return o1.equals(o2);
		}
		return false;
	}
	
	public int indexOf(C o)
	{
		int index = T_indexOf(o);
		
		if(index != -1 && flipped)
		{
			return size-index-1;
		}
		else if(index != -1)
		{
			return index;
		}
		return -1;
	}
	
	public boolean contains(C value)
	{
		return indexOf(value) != -1;
	}
	
	public int size()
	{
		return size;
	}
	
	public void flip()
	{
		flipped = !flipped;
	}
	
	/**
	 * 
	 * @return TRUE if ascending, FALSE if descending
	 */
	public boolean getOrder()
	{
		return !flipped;
	}
	
	public C[] toArray(C[] array) 
	{
		for(int a = 0; a < size; a++)
		{
			array[a] = this.get(a);
		}
		return array;
	}
	
	public String toString()
	{
		String ret = "[";
		
		for(int a = 0; a < this.size(); a++)
		{
			if(a != 0)
			{
				ret += ", ";
			}
			ret += this.get(a);
		}
		return ret + "]";
	}
	
	
	public static final boolean SORT_ASCENDING = true;
	public static final boolean SORT_DESCENDING = false;


	

	
	
	
}
