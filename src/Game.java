import  org.lwjgl.opengl.GL11;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.*;


public class Game {
	public static int width=1900;
	public static int height=1000;
	private static String title="Game";
	
	static int gridSize = 50;              
	public Chunk[][] worldGrid = new Chunk[width/gridSize + 1][height/gridSize + 1];
	
	long window;
	
	ArrayList<Seeker> seekerList = new ArrayList<Seeker>();
	int n = 10000;
	
	PathfinderQueue pfqueue = new PathfinderQueue(100, this);
	                
	Chunk previousChunk = null; //keep track of last placed to avoid buggy behavior
	
	
	public Game() throws Exception
	{
		if (width % gridSize != 0 || height % gridSize != 0) {
			throw new Exception("GRID SIZE DIVISION ERROR");
		}
		
		Random rand = new Random();
		
		System.out.println("worldGrid Length: "+worldGrid[1].length);
		//initialize worldGrid
		for (int i = 0; i < worldGrid.length; i++) {
			for (int j = 0; j < worldGrid[i].length; j++) {
				worldGrid[i][j] = new Chunk(this, i * gridSize, j * gridSize, gridSize);
			}
		}
		
		for (int i = 0; i < n; i++) {
			seekerList.add(new Seeker(this, rand.nextInt(width), rand.nextInt(height)));
		}
		
		
	}
	
	public boolean keyPressed(int x){
		return glfwGetKey(window, x) == GLFW_PRESS;
	}
	
	public Vector2D getMouseLocation()
	{
		double[] x = new double[1];
		double[] y = new double[1];
		GLFW.glfwGetCursorPos(window,  x,  y);
		return new Vector2D((int) x[0],(int) y[0]);
	}
	
	// returns window id
	public long init()
	{
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");
		
		window = glfwCreateWindow(width, height, title, NULL, NULL);
		

		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		//set up OpenGL
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		glfwSwapInterval(1);
		
		// screen clear is white (this could go in drawFrame if you wanted it to change)
		glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		
		// set projection to dimensions of window
        // set viewport to entire window
        GL11.glViewport(0,0,width,height);
         
        // set up orthographic projection to map world pixels to screen
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, height, 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);

		return window;
	}
	
	public void drawFrame(float delta)
	{
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		//placement of blocked chunks
		if (keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE)){
			Vector2D mouseCoords = getMouseLocation();
			if ((int) mouseCoords.x > 0 && (int) mouseCoords.x < width && (int) mouseCoords.y > 0 && (int) mouseCoords.y < height) {
				Chunk selected = worldGrid[(int) mouseCoords.x / gridSize][(int) mouseCoords.y / gridSize];
				if (selected != previousChunk && selected != null) {
					selected.blocked = !selected.blocked;
					previousChunk = selected;
				}
			}                    
		}
		else {
			//if spacebar is removed, allow changing of previousChunk
			previousChunk = null;
		}
		
		if (keyPressed(org.lwjgl.glfw.GLFW.GLFW_KEY_B)){
			Vector2D mouseCoords = getMouseLocation();
			for (Seeker s : seekerList) {
				if (!s.requestedPath)
					s.setTarget(mouseCoords, pfqueue);
			}
		}
		
		
		for (int i = 0; i < worldGrid.length - 1; i++) {
			for (int j = 0; j < worldGrid[i].length - 1; j++) {
				worldGrid[i][j].draw();
			}
		}
		
		for (Seeker s : seekerList) {
			s.draw();
			s.update();
		}
		pfqueue.update();
	}


}
