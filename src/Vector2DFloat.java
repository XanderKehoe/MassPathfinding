public class Vector2DFloat {
	float x;
	float y;
	
	public Vector2DFloat(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	static public float distance(Vector2DFloat first, Vector2DFloat second) {
		//get euclidean distance from two point vectors
		return (float) Math.sqrt((Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2)));
	}
	
	static public Vector2DFloat getUnitVector(Vector2DFloat source, Vector2DFloat target) {
		Vector2DFloat thisVector = new Vector2DFloat(target.x - source.x, target.y - source.y);
		double magnitude = Math.sqrt(thisVector.x * thisVector.x + thisVector.y * thisVector.y);
		return new Vector2DFloat((float) (thisVector.x / magnitude), (float) (thisVector.y / magnitude));
	}
}
