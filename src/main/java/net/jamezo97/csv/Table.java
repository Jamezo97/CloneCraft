package net.jamezo97.csv;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;



public class Table  {
	
//0 1 2 3 4 5 6 7 8 9   --> X
//1
//2
//3
//4
//5
//6
//7
//8
//9
//  Y
//  |
//  Y
	
	private int maxWidth = 0;
	
	ArrayList<Object[]> data = new ArrayList<Object[]>();
	
	final String validNumber = "01234567890.E-";
	
	boolean isDirty = false;
	
	/**
	 * Create a new table instance with the designated width
	 * @param size
	 */
	public Table(){
	}
	
	
	public void checkWidth(){
		if(isDirty){
			isDirty = false;
			maxWidth = 0;
			for(int a = 0; a < data.size(); a++){
				Object[] objData = data.get(a);
				int b = objData.length-1;
				for(; b >= 0; b--){
					if(objData[b] != null && objData[b].toString().length() != 0){
						b++;
						break;
					}
				}
				if(b > maxWidth){
					maxWidth = b;
				}
			}
		}
	}
	
	public void addRowV(Object... data){
		addRow(data);
	}
	
	public void addRow(Object[] tdata){
		if(tdata == null){
			tdata = new Object[0];
		}
		data.add(tdata);
		isDirty = true;
	}
	
	public void addRowCheckCast(Object[] tdata){
		addRow(parseData(tdata));
	}
	
	public Object[] removeRow(int index){
		return data.remove(index);
	}
	
	private Object[] parseData(Object[] tdata){
		for(int a = 0; a < tdata.length; a++){
			if(tdata[a] instanceof String && ((String)tdata[a]).length() > 0){
				String s = (String)tdata[a];
				
				
				//0 = Integer, 1 = Double, 2 = Float
				int type = -1;
				boolean decimal = false;
				boolean E = false;
				int validChars = 0;
				for(int b = 0; b < s.length(); b++){
					if(!validNumber.contains(s.substring(b, b+1))){
						s = null;
						break;
					}else if(s.charAt(b) == '.'){
						if(decimal){
							s = null;
							break;
						}else{
							decimal = true;
						}
					}else if(s.charAt(b) == 'E'){
						if(E){
							s = null;
							break;
						}else{
							E = true;
						}
					}else{
						validChars++;
					}
				}
				if(s == null || validChars == 0){
					continue;
				}else{
					if(type == -1){
						if(s.contains("E")){
							String[] split = s.split("E");
							if(split.length == 2){
								int value = Integer.parseInt(split[1]);
								if(value > 38 || value < -38){
									type = 1;
								}else{
									type = 2;
								}
							}
						}else if(decimal){
							type = 2;
						}else{
							type = 0;
						}
					}
					if(type != -1){
						if(type == 0){
							tdata[a] = Integer.parseInt(s);
						}else if(type == 1){
							tdata[a] = Double.parseDouble(s);
						}else if(type == 2){
							tdata[a] = Float.parseFloat(s);
						}
					}
				}
			}
		}
		return tdata;
	}
	
	public Object[] getRow(int index){
		if(index < data.size()){
			return data.get(index);
		}
		return new Object[0];
	}
	
	public Object getValue(int x, int y){
		if(y < data.size()){
			if(x < data.get(y).length){
				return data.get(y)[x];
			}
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T[] getRow(int index, Class<? extends T> type){
		Object[] dataFrom = this.getRow(index);
		
		T[] data = (T[])Array.newInstance(type, dataFrom.length);
		for(int a = 0; a < dataFrom.length; a++){
			data[a] = type.cast(dataFrom[a]);
		}
		return (T[])data;
	}
	
	public <T> T getValue(int x, int y, Class<? extends T> type){
		Object val = getValue(x, y);
		return type.cast(val);
	}
	
	public int getInt(int x, int y){
		Object val = getValue(x, y);
		if(val instanceof Integer){
			return (Integer)val;
		}else{
			return Integer.parseInt(val.toString());
		}
	}
	
	public float getFloat(int x, int y){
		Object val = getValue(x, y);
		if(val instanceof Float){
			return (Float)val;
		}else{
			return Float.parseFloat(val.toString());
		}
	}
	
	public double getDouble(int x, int y){
		Object val = getValue(x, y);
		if(val instanceof Double){
			return (Double)val;
		}else{
			return Double.parseDouble(val.toString());
		}
	}
	
	public String getString(int x, int y){
		Object val = getValue(x, y);
		if(val instanceof String){
			return (String)val;
		}else if(val != null){
			return val.toString();
		}
		return null;
	}
	
	public void setValue(Object value, int x, int y){
		//Ensure the table has the row capacity
		checkRowCapacity(y);
		//Ensure the column capacity
		checkColumnCapacity(x, y);

		data.get(y)[x] = value;
	}
	
	public void setRow(int y, Object... data){
		checkRowCapacity(y);
		if(data == null){
			data = new Object[0];
		}
		this.data.set(y, data);
	}
	
	private void checkRowCapacity(int y){
		if(y >= this.data.size()){
			int toAdd = 1 + y-this.data.size();
			for(int a = 0; a < toAdd; a++){
				this.addRow(null);
			}
		}
	}
	
	private void checkColumnCapacity(int x, int y){
		if(x >= this.data.get(y).length){
			Object[] newData = new Object[x+1];
			System.arraycopy(this.data.get(y), 0, newData, 0, this.data.get(y).length);
			this.data.set(y, newData);
		}
	}
	
/*	public BTable subTable(int x, int y, int width, int height){
		if(x >= 0 && y >= 0 && x+width <= getWidth() && y+height <= getHeight() && width > 0 && height > 0){
			BTable tableOut = new BTable(width);
			for(int a = 0; a < height; a++){
				Object[] data = new Object[width];
				for(int b = 0; b < width; b++){
					data[b] = this.getValue(x+b, y+a);
				}
				tableOut.addRow(data);
			}
			return tableOut;
		}
		return null;
	}
	
	public BTable subTableMinMax(int minX, int minY, int maxX, int maxY){
		return subTable(minX, minY, maxX - minX, maxY - minY);
	}*/
	
	/**
	 * Cleans up the table, removing empty rows at the bottom, and empty values at the end of each row
	 */
	public void clean(){
		for(int a = getHeight()-1; a >= 0; a--){
			Object[] rowData = this.data.get(a);
			if(rowData == null || rowData.length == 0){
				this.data.remove(a);
				continue;
			}
			//Check if the row data is empty
			int b = 0;
			for(; b < rowData.length; b++){
				if(rowData[b] != null && rowData[b].toString().length() > 0){
					b = -1;
					break;
				}
			}
			if(b != -1){
				this.data.remove(a);
				continue;
			}
			//To ensure the table's integrity, we only remove empty rows at the bottom of the table.
			//Thus if we reach the end here, we haven't remove a row from the table, thus it was full, we must stop from removing any other rows
			break;
		}

		//Now it's time to remove appending empty columns from the data
		for(int a = 0; a < this.data.size(); a++){
			Object[] objData = this.data.get(a);
			if(objData != null){
				int width = objData.length;
				for(int b = objData.length-1; b >= 0; b--){
					if(objData[b] == null || objData[b].toString().length() == 0){
						width--;
					}else{
						break;
					}
				}
				if(width != objData.length){
					if(width == 0){
						data.set(a, new Object[0]);
					}else{
						Object[] newData = new Object[width];
						System.arraycopy(objData, 0, newData, 0, width);
						data.set(a, newData);
					}
				}
			}else{
				data.set(a, new Object[0]);
			}
		}
		
		//We've done a lot of changes to the table. It's now 'dirty'
		this.isDirty = true;
	}
	
	public int getHeight(){
		return data.size();
	}

	public int getWidth() {
		checkWidth();
		return maxWidth;
	}
	
	public void PRINT(){
		for(int a = 0; a < data.size(); a++){
			Object[] data2 = data.get(a);
			String toPrint = "";
			for(int b = 0; b < data2.length; b++){
				if(b != 0){
					toPrint += ", ";
				}
				if(data2[b] != null){
					toPrint += data2[b];
				}
			}
			if(toPrint.length() == 0){
				toPrint = "-";
			}
			System.out.println(toPrint);
		}
	}
	
	static Method cloneMethod;

	static{
		try {
			
			cloneMethod = Object.class.getDeclaredMethod("clone");
			if(cloneMethod != null){
				System.out.println("Reflectively got 'clone' method from Object class");
				cloneMethod.setAccessible(true);
			}
			System.out.println(cloneMethod);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static Object cloneObj(Object toClone){
		if(toClone == null){
			return toClone;
		}
		if(toClone instanceof Cloneable){
			try {
				Object o = cloneMethod.invoke(toClone);
				if(o != null){
					return o;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return toClone;
	}

	public Table clone() {
		Table t = new Table();
		for(int a = 0; a < this.getHeight(); a++){
			Object[] row = this.getRow(a);
			if(row == null){
				t.addRow(null);
				continue;
			}else{
				Object[] newRow = new Object[row.length];
				for(int b = 0; b < row.length; b++){
					newRow[b] = cloneObj(row[b]);
				}
				t.addRow(newRow);
			}
		}
		return t;
	}

}
