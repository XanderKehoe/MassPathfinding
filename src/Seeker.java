import java.util.Queue;

import org.lwjgl.opengl.GL11;

public class Seeker {
	Vector2DInt position;
	int w = 2;
	int h = 2;
	
	int speed = 6;
	
	boolean requestedPath = false; //waiting for path to be received?
	Queue<Chunk> waypoints = null;
	Chunk currentChunk;
	Game g; //which game is this seeker attached to
	
	public Seeker(Game g, int x, int y) {
		this.g = g;
		position = new Vector2DInt(x, y);
		
		Vector2DInt currentChunkGridCoords = Chunk.convertWorldToGridCoords(x, y, Game.gridSize);
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
			position.y--;
			setCurrentChunk();
		}
	}
	
	public void moveTowardsWaypoint() {
		Vector2DInt currentChunkCenterPos = waypoints.peek().getWorldCoordsCenter();
		Vector2DFloat chunkPositionFloat = new Vector2DFloat(currentChunkCenterPos.x, currentChunkCenterPos.y);
		
		Vector2DFloat positionFloat = new Vector2DFloat(position.x, position.y);
		Vector2DFloat unitVector = Vector2DFloat.getUnitVector(positionFloat, chunkPositionFloat);
		
		position.x += unitVector.x * speed;
		position.y += unitVector.y * speed;
	}
	
	public void setCurrentChunk() {
		Vector2DInt currentChunkGridCoords = Chunk.convertWorldToGridCoords(position.x, position.y, Game.gridSize);
		currentChunk = g.worldGrid[currentChunkGridCoords.x][currentChunkGridCoords.y];
	}
	
	public void setTarget(Vector2DInt target, PathfinderQueue queue) {
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
