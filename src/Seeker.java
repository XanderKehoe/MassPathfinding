import java.util.ArrayList;
import java.util.Queue;

import org.lwjgl.opengl.GL11;

public class Seeker {
	Vector2D<Float> position;
	int size = 4;
	
	float speed = 1f;
	
	RGB color;
	
	boolean requestedPath = false; //waiting for path to be received?
	Queue<Chunk> waypoints = null;
	Chunk currentWorldChunk;
	CollisionChunk currentCollisionChunk;
	Game g; //which game is this seeker attached to
	
	boolean moved = false; //used to prevent double moving from collision
	
	public Seeker(Game g, float x, float y) {
		this.g = g;
		position = new Vector2D<Float>(x, y);
		
		Vector2D<Integer> currentWorldChunkGridCoords = Chunk.convertWorldToGridCoords(Math.round(x), Math.round(y), Game.worldGridSize);
		currentWorldChunk = g.worldGrid[currentWorldChunkGridCoords.x][currentWorldChunkGridCoords.y];
		
		Vector2D<Integer> currentCollisionChunkGridCoords = Chunk.convertWorldToGridCoords(Math.round(x), Math.round(y), Game.collisionGridSize);
		currentCollisionChunk = g.collisionGrid[currentCollisionChunkGridCoords.x][currentCollisionChunkGridCoords.y];
		
		color = new RGB();
	}
	
	public void move(float x, float y) {
		position.x += x;
		position.y += y;
	}
	
	public void update() {
		moved = false;
		if (waypoints != null) {
			if ( waypoints.size() != 0) {
				//navigate
				moveTowardsWaypoint();
				setCurrentChunk();
				
				if (currentWorldChunk.equals(waypoints.peek())) {
					waypoints.remove();
				}
			}
		}
		
		//if stuck inside a blocked chunk, move to nearby free chunk.
		if (currentWorldChunk.blocked) {
			ArrayList<Chunk> neighbours = currentWorldChunk.getNeighbours();
			float closestDistance = Float.MAX_VALUE;
			Chunk closestChunk = null;
			for (Chunk n : neighbours) {
				if (!n.blocked && Vector2D.distanceFloat(this.position, n.getWorldCoordsCenter()) < closestDistance) {
					closestChunk = n;
					closestDistance = Vector2D.distanceFloat(this.position, n.getWorldCoordsCenter());
				}
			}
			
			if (closestChunk != null) {
				position = closestChunk.getWorldCoordsCenter();
			}
			setCurrentChunk();
		}
		
		doCollisionMovement();
	}
	
	public void moveTowardsWaypoint() {
		Vector2D<Float> currentChunkCenterPos = waypoints.peek().getWorldCoordsCenter();
		Vector2D<Float> unitVector = Vector2D.getUnitVectorFloat(position, currentChunkCenterPos);
		
		position.x += (float) (unitVector.x * speed);
		position.y += (float) (unitVector.y * speed);
	}
	
	public void setCurrentChunk() {
		Vector2D<Integer> currentWorldChunkGridCoords = Chunk.convertWorldToGridCoords(Math.round(position.x), Math.round(position.y), Game.worldGridSize);
		Chunk thisWorldChunk = g.worldGrid[currentWorldChunkGridCoords.x][currentWorldChunkGridCoords.y];
		if (!thisWorldChunk.equals(currentWorldChunk)) {
			//switch which chunk this seeker is in.
			currentWorldChunk = thisWorldChunk;
		}
		
		Vector2D<Integer> currentCollisionChunkGridCoords = Chunk.convertWorldToGridCoords(Math.round(position.x), Math.round(position.y), Game.collisionGridSize);
		CollisionChunk thisCollisionChunk = g.collisionGrid[currentCollisionChunkGridCoords.x][currentCollisionChunkGridCoords.y];
		if (!thisCollisionChunk.equals(currentCollisionChunk)) {
			//switch which chunk this seeker is in.
			currentCollisionChunk.seekersInChunk.remove(this);
			thisCollisionChunk.seekersInChunk.add(this);
			
			currentCollisionChunk = thisCollisionChunk;
		}
		
		
	}
	
	public void setTarget(Vector2D<Float> target, PathfinderQueue queue) {
		if (!requestedPath) {
			requestedPath = true;
			queue.requestPath(this, target);
		}
		else {
			queue.removeRequest(this);
			queue.requestPath(this, target);
		}
	}
	
	public void doCollisionMovement() {
		ArrayList<CollisionChunk> neighbouringChunks = currentCollisionChunk.getNeighbours();
		neighbouringChunks.add(currentCollisionChunk); //check current chunk as well
		
		ArrayList<Seeker> chunkUpdateNeededList = new ArrayList<Seeker>();
		
		for (CollisionChunk c : neighbouringChunks) {
			for (Seeker s : c.seekersInChunk) {
				if (this.intersects(s) && s != this && !s.moved) {
					float multiplier = g.collisionMovementMultiplier;
					
					Vector2D<Float> unitVector = Vector2D.getUnitVectorFloat(this.position, s.position);
					this.position.x -= (float) (unitVector.x * multiplier);
					this.position.y -= (float) (unitVector.y * multiplier);
					s.position.x += (float) (unitVector.x * multiplier);
					s.position.y += (float) (unitVector.y * multiplier);
					
					if (!chunkUpdateNeededList.contains(this))
							chunkUpdateNeededList.add(this);
					if (!chunkUpdateNeededList.contains(s))
						chunkUpdateNeededList.add(s);
					
					s.moved = true;
				}
			}
		}
		
		for (Seeker s : chunkUpdateNeededList) {
			s.setCurrentChunk();
		}
		
	}
	
	public boolean intersects(Seeker otherSeeker) {
		if (otherSeeker.position.x < this.position.x + this.size && otherSeeker.position.x + otherSeeker.size > this.position.x &&
				otherSeeker.position.y + otherSeeker.size > this.position.y && otherSeeker.position.y < this.position.y + this.size)
			return true;
		return false;
	}
	
	public void draw() {
		GL11.glColor3f(color.r, color.g, color.b);
		
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glVertex2f(position.x, position.y);
        GL11.glVertex2f(position.x+size, position.y);
        GL11.glVertex2f(position.x+size, position.y+size);
        GL11.glVertex2f(position.x, position.y+size);
        GL11.glEnd();
	}

}
