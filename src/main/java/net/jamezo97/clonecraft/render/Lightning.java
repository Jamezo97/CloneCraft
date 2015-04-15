package net.jamezo97.clonecraft.render;

import java.util.ArrayList;
import java.util.Random;

import net.jamezo97.clonecraft.raytrace.Line;
import net.jamezo97.clonecraft.raytrace.Point;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Lightning {
	
	double x1, x2, y1, y2, z1, z2;
	Point[] points;
	boolean branch = false, powered = false;;
	
	private int index;
	
	ArrayList<Lightning> subBranches = new ArrayList<Lightning>();
	
	public Lightning(Line line, int points, boolean branch, boolean powered){
		this(line.x1, line.y1, line.z1, line.x2, line.y2, line.z2, points, branch, powered);
	}
	
	public Lightning(Point from, Point to, int points, boolean branch, boolean powered){
		this(from.x, from.y, from.z, to.x, to.y, to.z, points, branch, powered);
	}
	
	public Lightning(double x1, double y1, double z1, double x2, double y2, double z2, int points, boolean branch, boolean powered){
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.z1 = z1;
		this.z2 = z2;
		this.points = new Point[points+2];
		this.branch = branch;
		this.powered = powered;
		generate();
	}
	
	public Point getPointAt(float f){
		return new Point((x2-x1) * f + x1, (y2-y1) * f + y1, (z2-z1) * f + z1);
	}
	
	public Point getMidPoint(Point a, Point b){
		return new Point((a.x+b.x) / 2, (a.y+b.y) / 2, (a.z+b.z) / 2);
	}
	
	public double getDistanceBetween(Point a, Point b){
		return getDistanceBetween(a.x, a.y, a.z, b.x, b.y, b.z);
	}
	
	public double getDistanceBetween(double x1, double y1, double z1, double x2, double y2, double z2){
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)+(z1-z2)*(z1-z2));
	}
	
	public void generate(){
		Random rand = new Random();
		float chunkSize = 1.0f / ((float)points.length-1);
		double lineDistance = getDistanceBetween(x1, y1, z1, x2, y2, z2);
		double chunkDistance = lineDistance / ((double)points.length-1);
		Point[] basePoints = new Point[points.length];
		Point temp;
		for(int a = 0; a < points.length; a++){
			if(a==0||a==points.length-1){
				points[a] = getPointAt(chunkSize * a);
				basePoints[a] = points[a].clone();
			}else{
				points[a] = getPointAt(chunkSize * a + rand.nextFloat()*chunkSize*0.4f);
				basePoints[a] = points[a].clone();
				
				points[a].x += points[a-1].x - basePoints[a-1].x;
				points[a].y += points[a-1].y - basePoints[a-1].y;
				points[a].z += points[a-1].z - basePoints[a-1].z;
				
				temp = getMidPoint(basePoints[a], points[a]);
				
				points[a].x = temp.x + (rand.nextFloat()-0.5)*chunkDistance*1.4;
				points[a].y = temp.y + (rand.nextFloat()-0.5)*chunkDistance*1.4;
				points[a].z = temp.z + (rand.nextFloat()-0.5)*chunkDistance*1.4;
				if(branch && rand.nextInt(5) == 0){
					double distance = lineDistance * (rand.nextFloat()*0.3+0.2f);
					int noPoints = (int)Math.ceil(distance/chunkDistance);
					
					Lightning branch = new Lightning(points[a].x, points[a].y, points[a].z, points[a].x + (x2-x1)/lineDistance*(rand.nextFloat()*0.4f+0.6f)*distance, points[a].y + (y2-y1)/lineDistance*(rand.nextFloat()*0.4f+0.6f)*distance, points[a].z + (z2-z1)/lineDistance*(rand.nextFloat()*0.4f+0.6f)*distance, noPoints, rand.nextInt(50) < 20, powered);
					branch.setColourFrom(redF, greenF, blueF);
					branch.setColourTo(redT, greenT, blueT);
					subBranches.add(branch);
					branch.index = a;
				}
			}
		}
	}
	
	float redF = 0f, greenF = 0f, blueF = 0f;
	float redT = 0f, greenT = 0f, blueT = 0f;
	
	public Lightning setColourFrom(int colour){
		redF = ((float)((colour >> 16) & 0xff))/255.0f*0.9F;
		greenF = ((float)((colour >> 8) & 0xff))/255.0f*0.9F;
		blueF = ((float)((colour) & 0xff))/255.0f*0.9F;
		for(int a = 0; a < subBranches.size(); a++){
			subBranches.get(a).setColourFrom(redF, greenF, blueF);
		}
		return this;
	}
	
	public Lightning setColourFrom(float r, float g, float b){
		this.redF = r;
		this.greenF = g;
		this.blueF = b;
		return this;
	}
	
	public Lightning setColourTo(int colour){
		redT = ((float)((colour >> 16) & 0xff))/255.0f*0.9F;
		greenT = ((float)((colour >> 8) & 0xff))/255.0f*0.9F;
		blueT = ((float)((colour) & 0xff))/255.0f*0.9F;
		for(int a = 0; a < subBranches.size(); a++){
			subBranches.get(a).setColourFrom(redT, greenT, blueT);
		}
		return this;
	}
	
	public Lightning setColourTo(float r, float g, float b){
		this.redT = r;
		this.greenT = g;
		this.blueT = b;
		return this;
	}

	
	@SideOnly(value = Side.CLIENT)
	public void render(float partial){
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		render(partial, 1.0f);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}
	
	@SideOnly(value = Side.CLIENT)
	public void render(float partial, float beginPower){

		GL11.glPushMatrix();
		Entity renderView = Minecraft.getMinecraft().renderViewEntity;
		GL11.glTranslated(x1, y1, z1);

		Point entityViewPoint = new Point(x1, y1, z1);
		if(renderView != null){
			entityViewPoint = new Point(renderView.posX, renderView.posY, renderView.posZ);
		}
		GL11.glTranslated(-x1, -y1, -z1);
		GL11.glLineWidth(2f);
		
		Point from, to;
		float percent;
		double d;
		float power = 1.0f, powerSqr = 1.0f;
		for(int a = 0; a < points.length-1; a++){
			from = points[a];
			to = points[a+1];
			percent = ((float)a) / ((float)points.length-1);
//			percent = percent * percent;
			if(powered){
				power = (((float)points.length-1 - a)) / ((float)(points.length-1)) * beginPower;
				powerSqr = (float)Math.sqrt(Math.sqrt(power));
			}else{
				power = powerSqr = 1;
			}
			d = this.getDistanceBetween(getMidPoint(from,to), entityViewPoint)*100.0;
			GL11.glLineWidth((float)(1200.0f / d)*power);

			GL11.glColor4f(redF + percent*(redT-redF), greenF + percent*(greenT-greenF), blueF + percent*(blueT-blueF), 0.4f*powerSqr+0.6f);
//			GL11.glColor4f(0.1f*powerSqr+0.1f, 0.2f*powerSqr+0.8f, 0.2f*powerSqr+0.8f, 0.2f*powerSqr+0.8f);
			GL11.glBegin(GL11.GL_LINES);
			GL11.glVertex3d(from.x, from.y, from.z);
			GL11.glVertex3d(to.x, to.y, to.z);
			GL11.glEnd();
			
			
			for(int b = 0; b < subBranches.size(); b++){
				if(subBranches.get(b).index == a){
					subBranches.get(b).render(partial, power);
				}
			}
		}
		
		GL11.glPopMatrix();

	}

	public Lightning setColours(int from, int to) {
		this.setColourFrom(from);this.setColourTo(to);
		return this;
	}
	
	

}
