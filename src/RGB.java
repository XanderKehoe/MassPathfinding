import java.util.Random;

public class RGB {
	float r;
	float g;
	float b;
	
	public RGB() {
		Random rand = new Random();
		r = rand.nextFloat();
		g = rand.nextFloat();
		b = rand.nextFloat();
	}
	
	public RGB(float r, float g, float b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
}
