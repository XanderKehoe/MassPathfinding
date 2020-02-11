public class Vector2DInt {
	int x;
	int y;
	
	public Vector2DInt(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	static public int distance(Vector2DInt first, Vector2DInt second) {
		//get euclidean distance from two point vectors
		return (int) Math.sqrt((Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2)));
	}
	
}
