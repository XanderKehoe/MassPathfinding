import java.util.ArrayList;
import java.util.LinkedList;

import org.lwjgl.opengl.GL11;

public class Chunk implements Comparable<Chunk> {
	private Vector2D<Integer> position;
	Vector2D<Integer> worldGridPosition;
	Game g; //which game is this chunk attached to
	
	LinkedList<Seeker> seekersInChunk;
	
	boolean blocked = false;
	
	//variables for pathfinding
	Chunk parent = null;
	int gCost = Integer.MAX_VALUE;
	int hCost = Integer.MAX_VALUE;
	int fCost = Integer.MAX_VALUE;
	
	public Chunk(Game g, int x, int y) {
		position = new Vector2D(x, y);
		this.g = g;
		
		seekersInChunk = new LinkedList<Seeker>();
		
		worldGridPosition = Chunk.convertWorldToGridCoords(x, y, Game.worldGridSize);
	}
	
	public void draw() {
		if (blocked) {
			GL11.glColor3f(0,0,0);
			
	        GL11.glBegin(GL11.GL_QUADS);
	        GL11.glVertex2f(position.x, position.y);
	        GL11.glVertex2f(position.x+Game.worldGridSize, position.y);
	        GL11.glVertex2f(position.x+Game.worldGridSize, position.y+Game.worldGridSize);
	        GL11.glVertex2f(position.x, position.y+Game.worldGridSize);
	        GL11.glEnd();
		}
		 
	}
	
	public Chunk clone() {
		return new Chunk(this.g, position.x, position.y);
	}
	
	public void setCosts(Chunk source, Chunk target) {
		this.gCost = (int) calculateDistanceCost(source);
		this.hCost = (int) calculateDistanceCost(target);
		this.fCost = this.gCost + this.hCost;
	}
	
	public float calculateDistanceCost(Chunk source) {
		//cost is equal to the total distance from source node
		final int multiplier = 10;
		return Vector2D.distance(source.position, position) * multiplier;
	}
	
	public Vector2D<Float> getWorldCoordsCenter() {
			return new Vector2D<Float>((float) (position.x + (Game.worldGridSize / 2)), (float) (position.y + (Game.worldGridSize/2)));
	}
	
	public ArrayList<Chunk> getNeighbours(boolean travel) {
		ArrayList<Chunk> neighbours = new ArrayList<Chunk>();
		
		//have to check these so corners aren't cut
		boolean top = false;
		boolean left = false;
		boolean bottom = false;
		boolean right = false;
		
		int checkX = this.worldGridPosition.x;
		int checkY = this.worldGridPosition.y;
		
		Chunk[][] thisGrid = g.worldGrid;
		
		//top
		if(checkX >= 0 && checkX < thisGrid.length && checkY - 1 >= 0 && checkY - 1 < thisGrid[0].length
				&& !thisGrid[checkX][checkY - 1].blocked) {
			top = true;
			neighbours.add(thisGrid[checkX][checkY - 1]);
		}
		//bottom
		if(checkX >= 0 && checkX < thisGrid.length && checkY + 1 >= 0 && checkY + 1 < thisGrid[0].length
				&& !thisGrid[checkX][checkY + 1].blocked) {
			bottom = true;
			neighbours.add(thisGrid[checkX][checkY + 1]);
		}
		//left
		if(checkX - 1 >= 0 && checkX - 1 < thisGrid.length && checkY >= 0 && checkY < thisGrid[0].length
				&& !thisGrid[checkX - 1][checkY].blocked) {
			left = true;
			neighbours.add(thisGrid[checkX - 1][checkY]);
		}
		//right
		if(checkX + 1 >= 0 && checkX + 1 < thisGrid.length && checkY >= 0 && checkY < thisGrid[0].length
				&& !thisGrid[checkX + 1][checkY].blocked) {
			right = true;
			neighbours.add(thisGrid[checkX + 1][checkY]);
		}
		
		//top-left
		if(checkX - 1 >= 0 && checkX - 1 < thisGrid.length && checkY - 1 >= 0 && checkY - 1 < thisGrid[0].length
				&& top && left) {
			neighbours.add(thisGrid[checkX - 1][checkY - 1]);
		}
		//top-right
		if(checkX + 1 >= 0 && checkX + 1 < thisGrid.length && checkY - 1 >= 0 && checkY - 1 < thisGrid[0].length
				&& top && right) {
			neighbours.add(thisGrid[checkX + 1][checkY - 1]);
		}
		//bottom-left
		if(checkX - 1 >= 0 && checkX - 1 < thisGrid.length && checkY + 1 >= 0 && checkY + 1 < thisGrid[0].length
				&& bottom && left) {
			neighbours.add(thisGrid[checkX - 1][checkY + 1]);
		}
		//bottom-right
				if(checkX + 1 >= 0 && checkX + 1 < thisGrid.length && checkY + 1 >= 0 && checkY + 1 < thisGrid[0].length
						&& bottom && right) {
					neighbours.add(thisGrid[checkX + 1][checkY + 1]);
				}
		
		return neighbours;
	}
	
	
	public static Vector2D<Integer> convertWorldToGridCoords(int x, int y, int gridSize) {
		Vector2D<Integer> gridCoords = new Vector2D<Integer>(x / gridSize, y / gridSize);
		return gridCoords;	
	}
	
	/*public static Vector2DFloat convertWorldToGridCoords(int x, int y, int gridSize) {
		Vector2DFloat gridCoords = new Vector2DFloat(x / gridSize, y / gridSize);
		return gridCoords;	
	}*/
	
	public String toString() {
		return "X: "+position.x+" | Y: "+position.y;
	}
	
	
	@Override
	public int compareTo(Chunk c) {
		//optimize this by just subtracting fCost?
		if (this.fCost > c.fCost)
			return -1;
		else if (this.fCost < c.fCost)
			return 1;
		return 0;
	}
}
