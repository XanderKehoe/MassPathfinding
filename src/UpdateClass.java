import java.util.ArrayList;

public class UpdateClass implements Runnable {
	
	ArrayList<Seeker> seekerList;
	
	public UpdateClass(ArrayList<Seeker> seekerList) {
		this.seekerList = seekerList;
	}

	@Override
	public void run() {
		while (true) {
			try{
				Thread.sleep(1);
			}
			catch (Exception ignore) {
			}
				
			for (Seeker s : seekerList) {
				s.update();
			}
		}	
	}
}
