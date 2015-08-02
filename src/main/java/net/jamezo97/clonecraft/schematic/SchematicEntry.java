package net.jamezo97.clonecraft.schematic;

import java.io.File;

public class SchematicEntry implements Comparable<SchematicEntry>
{

	public Schematic schem;

	public String theName;

	public File fileLoc;

	public long lastModified;

	public SchematicEntry(Schematic schem, File fileLoc, long lastModified)
	{
		this.schem = schem;
		this.fileLoc = fileLoc;
		this.lastModified = lastModified;
		this.theName = schem.name;
	}

	@Override
	public int compareTo(SchematicEntry arg0)
	{
		return this.theName.compareTo(arg0.theName);
	}

	@Override
	public String toString()
	{
		return "SchemEntry: {Schem: " + schem + ", lastModified: " + lastModified + ", fileName: " + fileLoc.getName() + "}";
	}

}
