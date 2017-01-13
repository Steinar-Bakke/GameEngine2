package Audio;

import java.io.IOException;

import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

public class Test {

	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO Auto-generated method stub
		
		AudioMaster.init();
		AudioMaster.setListenerData(0,0,0);
		AL10.alDistanceModel(AL10.AL_INVERSE_DISTANCE_CLAMPED);
		
		int buffer = AudioMaster.loadSound("audio/animals.wav");
		Source source = new Source();
		source.setLooping(true);
		source.play(buffer);
		
		float xPos = 0;
		source.setPosition(xPos,  0, 0);
		
		char c = ' ';
		while(c != 'q'){
			
			xPos -= 0.03f;
			source.setPosition(xPos,  0, 2);
			System.out.println(xPos);
			Thread.sleep(10);

		}

		source.delete();
		AudioMaster.cleanUp();

	}

}
