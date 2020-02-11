import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

public class Chunk implements Comparable<Chunk> {
	private Vector2DInt position;
	Vector2DInt gridPosition;
	int size;
	Game g; //which game is this chunk attached to
	
	boolean blocked = false;
	
	//variables for pathfinding
	Chunk parent = null;
	int gCost = Integer.MAX_VALUE;
	int hCost = Integer.MAX_VALUE;
	int fCost = Integer.MAX_VALUE;
	
	public Chunk(Game g, int x, int y, int size) {
		position = new Vector2DInt(x, y);
		this.size = size;
		this.g = g;
		
		gridPosition = Chunk.convertWorldToGridCoords(x, y, size);
	}
	
	public void draw() {
		if (blocked) {
			GL11.glColor3f(0,0,0);
			
	        GL11.glBegin(GL11.GL_QUADS);
	        GL11.glVertex2f(position.x, position.y);
	        GL11.glVertex2f(position.x+size, position.y);
	        GL11.glVertex2f(position.x+size, position.y+size);
	        GL11.glVertex2f(position.x, position.y+size);
	        GL11.glEnd();
		}
		 
	}
	
	public Chunk clone() {
		return new Chunk(this.g, position.x, position.y, this.size);
	}
	
	public void setCosts(Chunk source, Chunk target) {
		this.gCost = (int) calculateDistanceCost(source);
		this.hCost = (int) calculateDistanceCost(target);
		this.fCost = this.gCost + this.hCost;
	}
	
	public float calculateDistanceCost(Chunk source) {
		//cost is equal to the total distance from source node
		final int multiplier = 10;
		return Vector2DInt.distance(source.position, position) * multiplier;
	}
	
	public Vector2DInt getWorldCoordsCenter() {
			return new Vector2DInt(position.x + (size / 2), position.y + (size/2));
	}
	
	public ArrayList<Chunk> getNeighbours() {
		ArrayList<Chunk> neighbours = new ArrayList<Chunk>();
		
		//have to check these so corners aren't cut
		boolean top = false;
		boolean left = false;
		boolean bottom = false;
		boolean right = false;
		
		int checkX = this.gridPosition.x;
		int checkY = this.gridPosition.y;
		
		//top
		if(checkX >= 0 && checkX < g.worldGrid.length && checkY - 1 >= 0 && checkY - 1 < g.worldGrid[0].length
				&& !g.worldGrid[checkX][checkY - 1].blocked) {
			top = true;
			neighbours.add(g.worldGrid[checkX][checkY - 1]);
		}
		//bottom
		if(checkX >= 0 && checkX < g.worldGrid.length && checkY + 1 >= 0 && checkY + 1 < g.worldGrid[0].length
				&& !g.worldGrid[checkX][checkY + 1].blocked) {
			bottom = true;
			neighbours.add(g.worldGrid[checkX][checkY + 1]);
		}
		//left
		if(checkX - 1 >= 0 && checkX - 1 < g.worldGrid.length && checkY >= 0 && checkY < g.worldGrid[0].length
				&& !g.worldGrid[checkX - 1][checkY].blocked) {
			left = true;
			neighbours.add(g.worldGrid[checkX - 1][checkY]);
		}
		//right
		if(checkX + 1 >= 0 && checkX + 1 < g.worldGrid.length && checkY >= 0 && checkY < g.worldGrid[0].length
				&& !g.worldGrid[checkX + 1][checkY].blocked) {
			right = true;
			neighbours.add(g.worldGrid[checkX + 1][checkY]);
		}
		
		//top-left
		if(checkX - 1 >= 0 && checkX - 1 < g.worldGrid.length && checkY - 1 >= 0 && checkY - 1 < g.worldGrid[0].length
				&& top && left) {
			neighbours.add(g.worldGrid[checkX - 1][checkY - 1]);
		}
		//top-right
		if(checkX + 1 >= 0 && checkX + 1 < g.worldGrid.length && checkY - 1 >= 0 && checkY - 1 < g.worldGrid[0].length
				&& top && right) {
			neighbours.add(g.worldGrid[checkX + 1][checkY - 1]);
		}
		//bottom-left
		if(checkX - 1 >= 0 && checkX - 1 < g.worldGrid.length && checkY + 1 >= 0 && checkY + 1 < g.worldGrid[0].length
				&& bottom && left) {
			neighbours.add(g.worldGrid[checkX - 1][checkY + 1]);
		}
		//bottom-right
				if(checkX + 1 >= 0 && checkX + 1 < g.worldGrid.length && checkY + 1 >= 0 && checkY + 1 < g.worldGrid[0].length
						&& bottom && right) {
					neighbours.add(g.worldGrid[checkX + 1][checkY + 1]);
				}
		
		return neighbours;
	}
	
	
	public static Vector2DInt convertWorldToGridCoords(int x, int y, int gridSize) {
		Vector2DInt gridCoords = new Vector2DInt(x / gridSize, y / gridSize);
		return gridCoords;	
	}
	
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
