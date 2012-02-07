package org.psk.kea;

import org.psk.kea.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 
 * @author Pete
 * Entry point to KEA app. Shows the beer mug splash screen and forks new thread
 * to display the main screen. New thread waits 1 second before showing main screen.
 * It does this in the UI thread, as is required.
 */
public class SplashActivity extends Activity {
	
	private static SplashActivity tmpThis = null;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		tmpThis = this;
		
		Runnable showMain = new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(tmpThis, KeepErAppyActivity.class);
				startActivity(intent);
			}
		};

		View vw = findViewById(R.id.big_beer);
		vw.postDelayed(showMain, 1000);
	}
}