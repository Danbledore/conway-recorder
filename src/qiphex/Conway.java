package qiphex;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class Conway {
	
	static final int w=240;
	static final int h=144;
	
	int frame=0;
	
	static final int size = w*h;
	long seed;
	
	static byte[] grid = new byte[w*h];
	
	static boolean running = false;
	static boolean complete = false;
	
	public File folder;
	
	/**
	 * Basic Java/LWJGL port of Conway's Game of Life with a built-in screen recorder method.
	 * 
	 * 
	 * 
	 * @author Original concept by John Horton Conway, project written by The Sound Interface Dude
	 */
	public Conway() {
		folder = new File(w+"x"+h);
		folder.mkdir();
		Random r = new Random(1);
		r.setSeed(seed);
		try {
			Display.setDisplayMode(new DisplayMode(w,h));
			Display.setTitle("Conway Game of Life Recorder");
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
		}
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0,w,h,0,1,-1);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		for(int i=0;i<grid.length;i++) {
			grid[i] = (byte)(r.nextBoolean() ? 1:0);
		}
		
		while(!Display.isCloseRequested()&&!complete) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT|GL11.GL_DEPTH_BUFFER_BIT);
			render();
			
			save();
			iterate();
			frame++;
			
			Display.update();
			Display.sync(60);
		}
		
		Display.destroy();
	}
	
	/**
	 * Saves the current screen as a .png file with the current frame. IE <i>0.png, 1.png, etc...</i>
	 * 
	 */
	private void save() {
		BufferedImage canvas=new BufferedImage(w,h,BufferedImage.TYPE_INT_RGB);
		Graphics2D artist = canvas.createGraphics();
		artist.setColor(Color.black);
		artist.drawRect(0, 0, w, h);
		for(int s=0;s<size;s++) {
			if(grid[s]!=0) {
				int x=s%w;
				int y=s/w;
				canvas.setRGB(x, y, Color.WHITE.getRGB());
			}
		}
		
		File f = new File(folder, frame+".png");
		try {
			ImageIO.write(canvas, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
		artist.dispose();
	}
	
	private void render() {
		
		GL11.glBegin(GL11.GL_POINTS); //I feel disgusting for using this, but I'm not exactly focusing on the renderer here.
		GL11.glColor3f(1, 1, 1);
		
		for(int i=0;i<size;i++) {
			if(grid[i]!=0) {
				int x=i%w;
				int y=i/w;
				GL11.glVertex2f(x, y);
			}
		}
		GL11.glEnd();
	}
	
	/**
	 * Main Game loop for Conway's Game of Life.
	 */
	private void iterate() {
		byte[] prev = new byte[size];
		System.arraycopy(grid, 0, prev, 0, size);
		
		byte[] next = new byte[size];
		for(int i=0;i<w;i++) {
			for(int j=0;j<h;j++) {
				byte type=gB(i,j);
				int q = j*w+i;
				if(type>0) {
					next[q]=1;
				} else {
					next[q]=0;
				}
			}
		}
		
		System.arraycopy(next, 0, grid, 0, size);
	}
	
	
	/**
	 * Checks the states of cell[x,y]'s neighbors.
	 * @param x is the X coordinate of the cell being tested.
	 * @param y is the Y coordinate of the cell being tested.
	 * @param prev is the grid being checked.
	 * @return 1 if the cell has 2-3 neighbors, 0 if not.
	 */
	byte gB(int x, int y) {
		byte b=0;
		
		int pos1 = y*w+x;
		
		for(int i=x-1;i<=x+1;i++) {
			for(int j=y-1;j<=y+1;j++) {
				int pos = j*w+i;
				if(pos>=0&&pos<size-1&&pos!=pos1) {
					if(grid[pos]==1) {
						b++;
					}
				}
			}
		}
		
		
		if(grid[pos1]==0) {
			if(b==3) {
				return 1;
			}
			return 0;
		} else {
			if(b<2||b>3) {
				return 0;
			}
			return 1;
		}
	}
	
	
	
	public static void main(String[] args) {
		new Conway();
	}
}
