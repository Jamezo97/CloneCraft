package net.jamezo97.util;

public class SortedList<C extends Comparable> {

	private int additionalStepSize = 5;
	
	private int default_size = 5;
	
	Comparable[] data;
	
	int MAX_SIZE = 100000;
	
	int size = 0;
	
	boolean flipped = false;
	
	public SortedList(boolean order){
		data = new Comparable[default_size];
		if(!order){
			flipped = true;
		}
	}
	
	public SortedList(){
		this(SORT_ASCENDING);
	}
	
	public C add(C comparer){
		insertAtPosition(getPositionForValue(comparer), comparer);
		return comparer;
	}
	
	private int getPositionForValue(C comparer){
		if(this.size < 10){
			int a = 0;
			for(; a < size; a++){
				if(data[a].compareTo(comparer) > 0){
					break;
				}
			}
			return a;
		}else{
			if(data[0].compareTo(comparer) > 0){
				return 0;
			}else if(data[size-1].compareTo(comparer) < 0){
				return size;
			}
			int max = size;
			int min = 0;
			int pos;//
			
			int temp = size+2;
			while(temp-- > 0){
				
				if(max-min == 1){
					return max;
				}
				
				pos = (max-min)/2 + min;
				
				//If the value below is equal to or less than,   or value above is equal to or greater than, then this is a valid position
//				System.out.println(comparer + ": " + pos + "{ " + data[pos-1] + ": " + data[pos-1].compareTo(comparer) + ", " + data[pos] + ": " + data[pos].compareTo(comparer));
				if(data[pos-1].compareTo(comparer) <= 0 && data[pos].compareTo(comparer) >= 0){
					
					return pos;
				}else{
					//If the value above is less than me
					if(data[pos].compareTo(comparer) < 0){
						min = pos;
					}else if(data[pos-1].compareTo(comparer) < 0){
					//Otherwise if the value below me is less than me	
						min = pos-1;
					}
					
					//If the value below me, is greater than me
					if(data[pos-1].compareTo(comparer) > 0){
						max = pos-1;
					}else if(data[pos].compareTo(comparer) > 0){
					//Otherwise if the value above me is greater than me	
						max = pos;
					}
				}
//				System.out.println("[" + min + " | " + max + "]");
				if(temp == 0){
					System.out.println("OH CRAP");
				}
			}
		}
		
		return -1;
	}
	
	private void insertAtPosition(int position, C comparer){
		ensureCapacity(size+1);
		if(position == size){
			data[position] = comparer;
		}else{
			//				src, 	srcPos,		dest, offset,  length
			System.arraycopy(data, position, data, position+1, size-position);
			data[position] = comparer;
		}
		size++;
	}
	
	public C remove(int index){
		if(flipped){
			index = size-index-1;
		}
		C temp = (C)data[index];
		data[index] = null;
		if(index+1 < size){
			System.arraycopy(data, index+1, data, index, size-index-1);
		}
		size--;
		return temp;
	}
	
	public C remove(C object){
		int index = this.indexOf(object);
		if(index != -1){
			return remove(index);
		}
		return null;
	}
	
	
	private void ensureCapacity(int nSize){
		if(data.length < nSize && nSize < MAX_SIZE){
			Comparable[] newData = new Comparable[data.length+additionalStepSize];
			System.arraycopy(data, 0, newData, 0, data.length);
			data = newData;
		}else if(nSize > MAX_SIZE){
			throw new RuntimeException("Cannot exceed max array size of " + MAX_SIZE + " (otherwise performance is exponentially reduced)");
		}
	}
	
	
	
	public C get(int index){
		if(flipped){
			index = size-index-1;
		}
		return (C)data[index];
	}
	
	private int T_indexOf(C o){
		if(size < 10){
			for(int a = 0; a < size; a++){
				if(o == data[a]){
					return a;
				}
			}
		}else{
			int max = size;
			int min = 0;
			int pos;
			
			int temp = size+2;
			while(temp-- > 0){
				
				if(max-min == 1){
					return -1;
				}
				
				pos = (max-min)/2 + min;

				if(data[pos] == o){
					return pos;
				}else{
					if(data[pos].compareTo(o) < 0){
						min = pos;
					}else if(data[pos].compareTo(o) > 0){
						max = pos;
					}
				}
				if(temp == 0){
					System.out.println("OH CRAP");
				}
			}
		}
		
		return -1;
	}
	
	public int indexOf(C o){
		int index = T_indexOf(o);
		if(index != -1 && flipped){
			return size-index-1;
		}else if(index != -1){
			return index;
		}
		return -1;
	}
	
	public boolean exists(C value){
		return indexOf(value) != -1;
	}
	
	public int size(){
		return size;
	}
	
	public void flip(){
		flipped = !flipped;
	}
	
	/**
	 * 
	 * @return TRUE if ascending, FALSE if descending
	 */
	public boolean getOrder(){
		return !flipped;
	}
	
	
	public static final boolean SORT_ASCENDING = true;
	public static final boolean SORT_DESCENDING = false;
	
}
