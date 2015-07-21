package net.jamezo97.csv;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;


public class CSVTools {
	
	public static boolean writeTableToFile(Table table, File file){
		boolean success = false;
		DataOutputStream writer = null;
		try{
			writer = new DataOutputStream(new FileOutputStream(file));
			Object o;
			String s;
			for(int a = 0; a < table.getHeight(); a++){
				Object[] data = table.getRow(a);
				for(int b = 0; b < table.getWidth(); b++){
					if(b < data.length){
						o = data[b];
					}else{
						o = null;
					}
					if(o != null)
					{
						String s2;
						
						s2 = o.toString();
						
						
						if(o instanceof String)
						{
							s = (s2).replace("\"", "\"\"");
							
							if(s.contains("\"") || s.contains(",") || s.contains("\n") || s.contains("\r"))
							{
								s = "\"" + s + "\"";
							}
							writer.writeBytes(s);
							
						}
						else
						{
							writer.writeBytes(s2);
						}
						
					}
					
					if(b+1 < table.getWidth())
					{
						writer.writeBytes(",");
					}
				}
				
				if(a+1 < table.getHeight())
				{
					writer.writeBytes("\r");
				}
			}
			
			writer.flush();
			success = true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			success = false;
		}
		finally
		{
			if(writer != null){
				try{
					writer.close();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
		return success;
	}
	
	public static Table getCSV(File file)
	{
		return getCSV(file, false);
	}
	
	public static Table getCSV(File file, boolean cast)
	{
		DataInputStream reader = null;
		Table table = new Table();
		char c;
		char lastC = 0;
		String s = "";
		boolean lastCharWasQuote = false;//Only true when enclosed is true.
		boolean enclosed = false;
		
		try
		{
			reader = new DataInputStream(new FileInputStream(file));
			
			ArrayList<String> items = new ArrayList<String>();
			
			while(reader.available() > 0)
			{
				c = (char)reader.readByte();

				
				//If the current character is a quote
				if(c == '\"')
				{
					//If we're not inside a quotation, we are now
					if(!enclosed)
					{
						enclosed = true;
					}
					else
					{
						//If the last character wasn't a quote, and I'm a quote, and we're enclosed
						//Then it might be a double quote. Let's add the quote.
						//And check it later. If the next char isn't a quote, then it's not longer enclosed
						if(!lastCharWasQuote)
						{
							//Leap of faith. Next char might not be a quote. If it's not, then gotta substring it out of there
							//and disable enclosed
							s += c;
							lastCharWasQuote = true;
						}else{
							//It was a double quote. Our faith was good.
							lastCharWasQuote = false;
						}
					}
				}
				else
				{
					if(lastCharWasQuote)
					{	
						//Oh bugger, it wasn't a double quote.
						lastCharWasQuote = false;
						
						if(s.length() > 0)
						{
							//Remove the accidental double quote.
							s = s.substring(0, s.length()-1);
						}
						//And because it was a single quote, and we must be enclosed right now
						//Thus the quote has ended and we are no longer enclosed.
						enclosed = false;
					}
					//lastCharWasQuote is always false by here.
					if(enclosed)
					{
						s += c;
					}
					else
					{
						if(c == ',')
						{
							items.add(s);
							s = "";
						}
						else if(c == '\n' || c == '\r')
						//It's a new line, and we aren't enclosed. Thus it's a new row of data
						{
//							System.out.println("New line: " + items.size());
							//Unless if it was written with a CRLF at the end of each line.
							//In which case, ignore the empty line.
							//So, if there's nothing on this line, and the last character wasn't a carriage return (\r)
							//and the current character isn't a new line feed (\n). Then we can save this row.
							if(items.size() != 0 || s.length() != 0 && lastC != '\r' && c != '\n'){
//								System.out.println("Add");
								items.add(s);
								
								s = "";
								
								Object[] data = items.toArray();
								
								items.clear();
								
								if(cast)
								{
									table.addRowCheckCast(data);
								}
								else
								{
									table.addRow(data);
								}
							}
							
						}
						else
						//Otherwise it's perfectly good safe data. Append it.
						{
							s += c;
						}
					}
				}
				lastC = c;
			}
			//Reached the end of file. Add any remaining data to the table.
			if(items.size() > 0)
			{
				items.add(s);
				Object[] data = items.toArray();
				
				items.clear();
				
				if(cast)
				{
					table.addRowCheckCast(data);
				}
				else
				{
					table.addRow(data);
				}
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return table;
	}
	
	public static Table getCSVOld(File file, boolean cast)
	{
		BufferedReader reader = null;
		Table table = null;
		char[] chars;
		char c;
		
		try
		{
			reader = new BufferedReader(new FileReader(file));
			String s = reader.readLine();
			
			
			ArrayList<String> values = new ArrayList<String>();
			String current;
			boolean inString;
			
			while(s != null)
			{
				chars = s.toCharArray();
				values.clear();
				current = "";
				inString = false;
				
				for(int a = 0; a < chars.length; a++)
				{
					c = chars[a];
					
					if(c == ',' && !inString)
					{
						if(current.length() == 0)
						{
							current = null;
						}
						
						values.add(current);
						current = "";
					}
					else if(c == '"' && !inString)
					{
						inString = true;
						
					}
					else if(a+1<chars.length && c == '"' && chars[a+1] == '"')
					{
						current += "\"";
						a++;
					}
					else if(c == '"')
					{
						inString = false;
					}
					else
					{
						current += c;
					}
				}
				
				values.add(current);
				
				for(int a = 0; a < values.size(); a++){
					String d = values.get(a);
					if(d != null){
						d = d.replaceAll("&#14;", "\r");
						d = d.replaceAll("&#11;", "\n");

						d = d.replaceAll("&&14;", "&#14;");
						d = d.replaceAll("&&11;", "&#11;");
						
						values.set(a, d);
					}
					
				}
				
				if(table == null)
				{
					table = new Table();
				}
				
				if(cast)
				{
					table.addRowCheckCast(values.toArray(new Object[values.size()]));
				}
				else
				{
					table.addRow(values.toArray(new Object[values.size()]));
				}
				
				
				
				s = reader.readLine();
			}
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(reader != null)
			{
				try
				{
					reader.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return table;
	}
	
	
	
	
/*	
	
	
	public static void main(String[] args){
		Table table = new Table();
		
		
		
		table.setRow(4, "Hello", 12, 15, 15.0f, 1.5d);

		table.setRow(8, "Hello", 12, 15, 15.0f, 1.5d);
		

		table.setRow(8, (Object[])null);
		
		table.setValue("Hello", 12, 6);
		table.setValue(null, 12, 6);
		
		table.setValue("Hello", 17, 9);
		
		table.clean();
		
		CSVTools.writeTableToFile(table, new File("Output.csv"));
		
		table.PRINT();
	}*/
	

}
