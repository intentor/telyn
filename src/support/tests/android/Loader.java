package org.escape2team.telyn;

import java.io.IOException;

import org.newdawn.slick.SlickActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

public class Loader extends SlickActivity {
	//http://code.google.com/p/android-labs/source/browse/trunk/NoiseAlert/src/com/google/android/noisealert/NoiseAlert.java
	public static Context ANDROID_CONTEXT;
	public static SoundMeter MIC;
	public static double AMP;
	private static final int POLL_INTERVAL = 300;
	
	private Handler mHandler = new Handler();
	
	private Runnable mSleepTask = new Runnable() {
        public void run() {
                start();
        }
	};
	
	private Runnable mPollTask = new Runnable() {
        public void run() {
        	AMP = MIC.getAmplitude();
            mHandler.postDelayed(mPollTask, POLL_INTERVAL);
        }
	};

	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ANDROID_CONTEXT = this;
        MIC = new SoundMeter();
        start(new GameScene(), 800, 480);
    }

    @Override
    public void onResume() {    	
        super.onResume();
        start();
    }

    @Override
    public void onStop() {
        super.onStop();
        stop();
    }
    
    //================
    
    private void start() {
        try {
			MIC.start();
	        mHandler.postDelayed(mPollTask, POLL_INTERVAL);
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void stop() {
        mHandler.removeCallbacks(mSleepTask);
        mHandler.removeCallbacks(mPollTask);
        MIC.stop();
	}
}