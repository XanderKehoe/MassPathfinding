
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Queue;
import java.util.Stack;

public class PathfinderQueue implements Runnable {
	Queue<Request> queue = new ArrayDeque<Request>();
	
	int requestPerFrame; //how many request to process each time update is called
	
	Game g; //which game is this queue attached to
	
	boolean alive = true;
	
	private class Request{
		Seeker seeker;
		Vector2D<Float> target;
		
		public Request(Seeker s, Vector2D<Float> target) {
			seeker = s;
			this.target = target;
		}
	}
	
	public PathfinderQueue(int requestPerFrame, Game g) {
		this.requestPerFrame = requestPerFrame;
		this.g = g;
	}
	
	public void requestPath(Seeker s, Vector2D<Float> target) {
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
		Vector2D<Integer> source = Chunk.convertWorldToGridCoords(Math.round(request.seeker.position.x), Math.round(request.seeker.position.y), Game.worldGridSize);
		Vector2D<Integer> target = Chunk.convertWorldToGridCoords(Math.round(request.target.x), Math.round(request.target.y), Game.worldGridSize);
		
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
				
				Vector2D<Integer> currentGridCoords = current.worldGridPosition;
				if (currentGridCoords.x == target.x && currentGridCoords.y == target.y) {
					RetracePathAndSet(request.seeker, g.worldGrid[source.x][source.y], g.worldGrid[target.x][target.y]);
					pathFound = true;
					break; //target has been found!
				}
				
				
				for (Chunk n : current.getNeighbours()){
					if (n.blocked || closedSet.contains(n)) {
						continue;
					}
					
					int newCostToNeighbour = (int) (current.gCost + Vector2D.distanceInt(current.worldGridPosition, n.worldGridPosition));
					if (newCostToNeighbour < n.gCost || !openSet.contains(n)) { 
						n.gCost = newCostToNeighbour;
						n.hCost = (int) Vector2D.distanceInt(n.worldGridPosition, target);
						n.parent = current;
						
						if(!openSet.contains(n)) {
							openSet.add(n);
						}
					}
				}
			}
			
			if (!pathFound) {
				System.out.println("no path found");
				request.seeker.requestedPath = false;
			}
		}
		else {
			request.seeker.requestedPath = false;
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

	@Override
	public void run() {
		while (alive) {
			try{
				Thread.sleep(1);
			}
			catch (Exception ignore) {
				//do nothing
			}
			update();
		}
	}
	
	
}
