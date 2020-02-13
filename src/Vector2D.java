public class Vector2D<T> {
	
	public T x;
	public T y;
	
	public Vector2D(T x, T y)
	{
		this.x = x;
		this.y = y;
	}
	
	static public float distanceFloat(Vector2D<Float> first, Vector2D<Float> second) {
		//get euclidean distance from two point vectors
		return (float) Math.sqrt((Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2)));
	}
	
	static public float distanceInt(Vector2D<Integer> first, Vector2D<Integer> second) {
		//get euclidean distance from two point vectors
		return (float) Math.sqrt((Math.pow(first.x - second.x, 2) + Math.pow(first.y - second.y, 2)));
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
