package net.jamezo97.clonecraft.clone;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import org.lwjgl.opengl.GL11;

public class CCWavefrontOBJ
{

	public CCWavefrontOBJ(String s) throws Exception
	{
		if (s.toLowerCase().endsWith("obj"))
		{
			InputStream input = null;
			if ((input = CCWavefrontOBJ.class.getResourceAsStream(s)) != null)
			{
				load(input);
			}
		}
		else
		{
			throw new Exception("Not valid WaveFront OBJ file");
		}
	}

	public CCWavefrontOBJ(File f) throws Exception
	{
		if (f.getName().toLowerCase().endsWith("obj"))
		{
			load(new FileInputStream(f));
		}
		else
		{
			throw new Exception("Not valid WaveFront OBJ file");
		}
	}

	public CCWavefrontOBJ(String[] lines) throws Exception
	{
		if (lines != null)
		{
			load(lines);
		}
		else
		{
			throw new Exception("The List Is Null");
		}
	}

	private void load(String[] lines)
	{
		for (int a = 0; a < lines.length; a++)
		{
			String s = lines[a];
			if (s.toLowerCase().startsWith("v "))
			{
				handleVertice(s.substring(2).trim());
			}
			else if (s.toLowerCase().startsWith("vt "))
			{
				handleUV(s.substring(3).trim());
			}
			else if (s.toLowerCase().startsWith("f "))
			{
				handleFace(s.substring(2).trim());
			}
		}
	}

	private void load(InputStream input)
	{
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(input));
			String s = reader.readLine();
			while (s != null)
			{
				if (s.toLowerCase().startsWith("v "))
				{
					handleVertice(s.substring(2).trim());
				}
				else if (s.toLowerCase().startsWith("vt "))
				{
					handleUV(s.substring(3).trim());
				}
				else if (s.toLowerCase().startsWith("f "))
				{
					handleFace(s.substring(2).trim());
				}
				s = reader.readLine();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	private void handleFace(String s)
	{
		String[] split = s.split(" ");
		Vertice[] vs = new Vertice[split.length];
		UV[] uvs = new UV[split.length];
		boolean useUV = true;
		for (int a = 0; a < split.length; a++)
		{
			if (!split[a].contains("/"))
			{
				useUV = false;
				break;
			}
		}
		if (useUV)
		{
			for (int a = 0; a < split.length; a++)
			{
				String[] split2 = split[a].split("/");
				if (split2.length >= 2)
				{
					vs[a] = vertices.get(Integer.parseInt(split2[0]) - 1);
					uvs[a] = UVs.get(Integer.parseInt(split2[1]) - 1);
				}
			}
		}
		else
		{
			for (int a = 0; a < split.length; a++)
			{
				vs[a] = vertices.get(Integer.parseInt(split[a]) - 1);
			}
		}
		faces.add(new Face(vs, uvs, useUV));
	}

	private void handleUV(String s)
	{
		String[] split = s.split(" ");
		if (split.length >= 2)
		{
			try
			{
				UVs.add(new UV(Float.parseFloat(split[0]), 1 - Float.parseFloat(split[1])));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	private void handleVertice(String s)
	{
		String[] split = s.split(" ");
		if (split.length >= 3)
		{
			try
			{
				vertices.add(new Vertice(Double.parseDouble(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2])));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}

	}

	public void render()
	{
		GL11.glPushMatrix();
		for (int a = 0; a < faces.size(); a++)
		{
			faces.get(a).render();
		}
		GL11.glPopMatrix();
	}

	ArrayList<Vertice> vertices = new ArrayList<Vertice>();
	ArrayList<UV> UVs = new ArrayList<UV>();
	ArrayList<Face> faces = new ArrayList<Face>();

	public class Vertice
	{
		double x, y, z;
		byte r, g, b;

		public Vertice(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			/*Random rand = new Random();
			r = (byte) (rand.nextInt(256));
			g = (byte) (rand.nextInt(256));
			b = (byte) (rand.nextInt(256));*/
		}

		public void setColour(byte r, byte g, byte b)
		{
			this.r = r;
			this.g = g;
			this.b = b;
		}
	}

	public class UV
	{
		float U, V;

		public UV(float U, float V)
		{
			this.U = U;
			this.V = V;
		}
	}

	public class Face
	{
		boolean useUV = false;
		Vertice[] vs;
		UV[] uvs;

		public Face(Vertice[] vs, UV[] uvs, boolean useUV)
		{
			this.vs = vs;
			this.uvs = uvs;
			this.useUV = useUV;

		}

		public void render()
		{
			if (useUV)
			{
//				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			// GL11.glColor3ub(r, g, b);
			// GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glBegin(GL11.GL_QUADS);
			for (int a = 0; a < vs.length; a++)
			{
				if (useUV)
				{
					GL11.glTexCoord2f(uvs[a].U, uvs[a].V);
				}
				else
				{
					// GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
				}
				// GL11.glColor3ub(r, g, (byte)Math.round(b-(vs[a].y*50)));

				GL11.glColor3ub(vs[a].r, vs[a].g, (byte) Math.round(vs[a].b - (vs[a].y * 50)));

				GL11.glVertex3d(vs[a].x, vs[a].y, vs[a].z);

			}
			GL11.glEnd();
			// GL11.glEnable(GL11.GL_LIGHTING);
			/*
			 * Tessellator t = Tessellator.instance; t.setBrightness(240);
			 * t.startDrawing(GL11.GL_POLYGON);
			 * 
			 * 
			 * if(useUV){ for(int a = 0; a < vs.length; a++){
			 * t.addVertexWithUV(vs[a].x, vs[a].y, vs[a].z, uvs[a].U, uvs[a].V);
			 * } }else{ for(int a = 0; a < vs.length; a++){ t.addVertex(vs[a].x,
			 * vs[a].y, vs[a].z); } } t.draw();
			 */
		}
	}
}
