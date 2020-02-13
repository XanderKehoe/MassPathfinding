import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;

import org.lwjgl.opengl.GL11;

public class CollisionChunk {
	private Vector2D<Integer> position;
	Vector2D<Integer> collisionGridPosition;
	Game g; //which game is this chunk attached to
	
	LinkedBlockingDeque<Seeker> seekersInChunk;
	
	boolean blocked = false;
	
	public CollisionChunk(Game g, int x, int y) {
		position = new Vector2D<Integer>(x, y);
		this.g = g;
		
		seekersInChunk = new LinkedBlockingDeque<Seeker>();
		
		collisionGridPosition = Chunk.convertWorldToGridCoords(x, y, Game.collisionGridSize);
	}
	
	public ArrayList<CollisionChunk> getNeighbours() {
		ArrayList<CollisionChunk> neighbours = new ArrayList<CollisionChunk>();
		
		//have to check these so corners aren't cut
		boolean top = false;
		boolean left = false;
		boolean bottom = false;
		boolean right = false;
		
		int checkX = this.collisionGridPosition.x;
		int checkY = this.collisionGridPosition.y;
		
		CollisionChunk[][] thisGrid = g.collisionGrid;
		
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
	
	public String toString() {
		return "X: "+position.x+" | Y: "+position.y;
	}
}
