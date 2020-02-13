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

//TODO
	//get rid of waypoint removal when entering node it aint supposed to (makes navigating skinny paths disgusting)
		//just replace with manual check every time before you move?
	//try BST instead of concurrent linked list thing


public class Game {
	public static int width=1900;
	public static int height=1000;
	private static String title="Game";
	
	static int worldGridSize = 50;              
	public Chunk[][] worldGrid = new Chunk[width/worldGridSize][height/worldGridSize];
	
	static int collisionGridSize = 10;
	public CollisionChunk[][] collisionGrid = new CollisionChunk[width/collisionGridSize][height/collisionGridSize];
	
	public float collisionMovementMultiplier = 2;
	
	long window;
	
	ArrayList<Seeker> seekerList = new ArrayList<Seeker>();
	int n = 1000;
	
	PathfinderQueue pfqueue = new PathfinderQueue(100, this);
	Thread pathfindingThread = new Thread(pfqueue);
	
	int updateThreadCount = 20;
	Thread[] updateThreads = new Thread[updateThreadCount];
	
	                
	Chunk previousChunk = null; //keep track of last placed to avoid buggy behavior
	
	
	public Game() throws Exception
	{
		if (width % worldGridSize != 0 || height % worldGridSize != 0) {
			throw new Exception("GRID SIZE DIVISION ERROR");
		}
		
		Random rand = new Random();

		//initialize worldGrid
		for (int i = 0; i < worldGrid.length; i++) {
			for (int j = 0; j < worldGrid[i].length; j++) {
				worldGrid[i][j] = new Chunk(this, i * worldGridSize, j * worldGridSize);
			}
		}
		
		//initialize collisionGrid
				for (int i = 0; i < collisionGrid.length; i++) {
					for (int j = 0; j < collisionGrid[i].length; j++) {
						collisionGrid[i][j] = new CollisionChunk(this, i * collisionGridSize, j * collisionGridSize);
					}
				}
		
		placeBorderWalls();
		
		for (int i = 0; i < n; i++) {
			seekerList.add(new Seeker(this, worldGridSize + rand.nextInt(width - worldGridSize * 2), worldGridSize + rand.nextInt(height - worldGridSize * 2)));
		}
		
		pathfindingThread.start();
		
		for (int i = 0; i < updateThreadCount; i++) {
			ArrayList<Seeker> subList = new ArrayList<Seeker>();
			for (int j = i * (seekerList.size() / updateThreadCount); j < (seekerList.size() / updateThreadCount) + i * (seekerList.size() / updateThreadCount) ; j++) {
				subList.add(seekerList.get(j));
			}
			
			UpdateClass updateClass = new UpdateClass(subList);
			Thread updateThread = new Thread(updateClass);
			updateThreads[i] = updateThread;
		}
		
		for (int i = 0; i < updateThreadCount; i++)
			updateThreads[i].start();
		
		
	}
	
	public boolean keyPressed(int x){
		return glfwGetKey(window, x) == GLFW_PRESS;
	}
	
	public Vector2D<Float> getMouseLocation()
	{
		double[] x = new double[1];
		double[] y = new double[1];
		GLFW.glfwGetCursorPos(window,  x,  y);
		return new Vector2D<Float>((float) x[0],(float) y[0]);
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
			Vector2D<Float> mouseCoords = getMouseLocation();
			if (mouseCoords.x > 0 && mouseCoords.x < width && mouseCoords.y > 0 && mouseCoords.y < height) {
				Chunk selected = worldGrid[Math.round(mouseCoords.x) / worldGridSize][Math.round(mouseCoords.y) / worldGridSize];
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
			Vector2D<Float> mouseCoords = getMouseLocation();
			for (Seeker s : seekerList) {
				if (!s.requestedPath)
					s.setTarget(mouseCoords, pfqueue);
			}
		}
		
		
		for (int i = 0; i < worldGrid.length; i++) {
			for (int j = 0; j < worldGrid[i].length; j++) {
				worldGrid[i][j].draw();
			}
		}
		
		for (Seeker s : seekerList) {
			s.draw();
		}
	}
	
	public void placeBorderWalls() {
		for (int i = 0; i < worldGrid.length; i++) {
			worldGrid[i][0].blocked = true;
			worldGrid[i][worldGrid[0].length - 1].blocked = true;
		}
		
		for (int i = 0; i < worldGrid[0].length; i++) {
			worldGrid[0][i].blocked = true;
			worldGrid[worldGrid.length - 1][i].blocked = true;
		}
		
	}


}
