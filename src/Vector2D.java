public class Vector2D<T> {
	
	public T x;
	public T y;
	
	public Vector2D(T x, T y)
	{
		this.x = x;
		this.y = y;
	}
	
	static public int distance(Vector2D first, Vector2D second) {
		//get euclidean distance from two point vectors
		return (int) Math.sqrt((Math.pow((int) first.x - (int) second.x, 2) + Math.pow((int) first.y - (int) second.y, 2)));
	}
	
	//Use integers as inputs, still returns float
	static public Vector2D<Float> getUnitVectorInt(Vector2D<Integer> source, Vector2D<Integer> target) {
		Vector2D<Integer> thisVector = new Vector2D<Integer>((int) target.x - (int) source.x, (int) target.y - (int) source.y);
		double magnitude = Math.sqrt((int) thisVector.x * (int) thisVector.x + (int) thisVector.y * (int) thisVector.y);
		return new Vector2D<Float>((float) ((int) thisVector.x / magnitude), (float) ((int) thisVector.y / magnitude));
	}
	
	
	//Use floats as inputs
	static public Vector2D<Float> getUnitVectorFloat(Vector2D<Float> source, Vector2D<Float> target) {
		Vector2D<Float> thisVector = new Vector2D<Float>(target.x - source.x, target.y - source.y);
		double magnitude = Math.sqrt(thisVector.x * thisVector.x + thisVector.y * thisVector.y);
		return new Vector2D<Float>((float) (thisVector.x / magnitude), (float) (thisVector.y / magnitude));
	}

	
}
