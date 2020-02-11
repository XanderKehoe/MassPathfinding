
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Stack;

public class PathfinderQueue {
	Queue<Request> queue = new ArrayDeque<Request>();
	
	int requestPerFrame; //how many request to process each time update is called
	
	Game g; //which game is this queue attached to
	
	private class Request{
		Seeker seeker;
		Vector2DInt target;
		
		public Request(Seeker s, Vector2DInt target) {
			seeker = s;
			this.target = target;
		}
	}
	
	public PathfinderQueue(int requestPerFrame, Game g) {
		this.requestPerFrame = requestPerFrame;
		this.g = g;
	}
	
	public void requestPath(Seeker s, Vector2DInt target) {
		queue.add(new Request(s, target));
	}
	
	public void removeRequest(Seeker s) {
		for (Request r : queue) {
			if (r.seeker.equals(s)) {
				queue.remove(r);
			}
		}
	}
	
	public void update() {
		for (int i = 0; i < requestPerFrame && !queue.isEmpty(); i++) {
			calculatePath(queue.remove());
		}
	}
	
	public void calculatePath(Request request) {
		Vector2DInt source = Chunk.convertWorldToGridCoords(request.seeker.position.x, request.seeker.position.y, Game.gridSize);
		Vector2DInt target = Chunk.convertWorldToGridCoords(request.target.x, request.target.y, Game.gridSize);
		
		if (!g.worldGrid[source.x][source.y].blocked && !g.worldGrid[target.x][target.y].blocked) {
			MinHeap<Chunk> openSet = new MinHeap<Chunk>();
			HashSet<Chunk> closedSet = new HashSet<Chunk>();
			openSet.add(g.worldGrid[source.x][source.y]); //insert source node to start off
			
			boolean pathFound = false;
			
			while (!openSet.isEmpty()) {
				Chunk current = openSet.remove();
				closedSet.add(current);
				
				if (current == null) {
					System.out.println("No Path Found");
					break;
				}
				
				Vector2DInt currentGridCoords = current.gridPosition;
				if (currentGridCoords.x == target.x && currentGridCoords.y == target.y) {
					RetracePathAndSet(request.seeker, g.worldGrid[source.x][source.y], g.worldGrid[target.x][target.y]);
					pathFound = true;
					break; //target has been found!
				}
				
				//for (Chunk n : current.getNeighbours()){
				//	System.out.println("\t"+n.toString());
				//}
				
				
				for (Chunk n : current.getNeighbours()){
					if (n.blocked || closedSet.contains(n)) {
						continue;
					}
					
					int newCostToNeighbour = current.gCost + Vector2DInt.distance(current.gridPosition, n.gridPosition);
					if (newCostToNeighbour < n.gCost || !openSet.contains(n)) { 
						n.gCost = newCostToNeighbour;
						n.hCost = Vector2DInt.distance(n.gridPosition, target);
						n.parent = current;
						
						if(!openSet.contains(n)) {
							openSet.add(n);
						}
					}
				}
			}
		}
	}
	
	public void RetracePathAndSet(Seeker s, Chunk start, Chunk end) {
		Stack<Chunk> tempPath = new Stack<Chunk>();
		Chunk current = end;
		
		while (current != start) {
			tempPath.push(current);
			current = current.parent;
		}
		
		//reverse path
		Queue<Chunk> path = new ArrayDeque<Chunk>();
		while (!tempPath.isEmpty()) {
			path.add(tempPath.pop());
		}
		
		s.waypoints = path;
		s.requestedPath = false;
	}
	
	
}
