import java.util.Queue;

import org.lwjgl.opengl.GL11;

public class Seeker {
	Vector2D<Integer> position;
	int w = 2;
	int h = 2;
	
	int speed = 6;
	
	boolean requestedPath = false; //waiting for path to be received?
	Queue<Chunk> waypoints = null;
	Chunk currentChunk;
	Game g; //which game is this seeker attached to
	
	public Seeker(Game g, int x, int y) {
		this.g = g;
		position = new Vector2D(x, y);
		
		Vector2D<Integer> currentChunkGridCoords = Chunk.convertWorldToGridCoords(x, y, Game.gridSize);
		currentChunk = g.worldGrid[currentChunkGridCoords.x][currentChunkGridCoords.y];
		System.out.println("cC:" + currentChunk);
	}
	
	public void move(int x, int y) {
		position.x += x;
		position.y += y;
	}
	
	public void update() {
		if (waypoints != null) {
			if ( waypoints.size() != 0) {
				//navigate
				moveTowardsWaypoint();
				setCurrentChunk();
				
				if (currentChunk.equals(waypoints.peek())) {
					waypoints.remove();
				}
			}
		}
		
		//if stuck inside a blocked chunk, move.
		if (currentChunk.blocked) {
			Vector2D currentChunkCenterPos = currentChunk.getWorldCoordsCenter();
			position.y--;
			setCurrentChunk();
		}
	}
	
	public void moveTowardsWaypoint() {
		Vector2D<Integer> currentChunkCenterPos = waypoints.peek().getWorldCoordsCenter();

		Vector2D<Float> unitVector = Vector2D.getUnitVector(position, currentChunkCenterPos);
		
		position.x += (int) (unitVector.x * speed);
		position.y += (int) (unitVector.y * speed);
	}
	
	public void setCurrentChunk() {
		Vector2D<Integer> currentChunkGridCoords = Chunk.convertWorldToGridCoords(position.x, position.y, Game.gridSize);
		currentChunk = g.worldGrid[currentChunkGridCoords.x][currentChunkGridCoords.y];
	}
	
	public void setTarget(Vector2D target, PathfinderQueue queue) {
		if (!requestedPath) {
			requestedPath = true;
			queue.requestPath(this, target);
		}
		else {
			queue.removeRequest(this);
			queue.requestPath(this, target);
		}
	}
	
	public void draw() {
		GL11.glColor3f(1,0,1);
		
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(position.x, position.y);
        GL11.glVertex2f(position.x+w, position.y);
        GL11.glVertex2f(position.x+w, position.y+h);
        GL11.glVertex2f(position.x, position.y+h);
        GL11.glEnd();
	}

}
