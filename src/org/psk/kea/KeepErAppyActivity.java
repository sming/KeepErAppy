package org.psk.kea;

import org.psk.kea.weather.WeatherFacade;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

/**
 * @author Pete
 * The main activity of the application. Acts as controller. 
 */
public class KeepErAppyActivity extends Activity implements OnClickListener {

	private SharedPreferences _prefs;
	private Resources _res;
	private WeatherFacade _weather;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// short kiss-kiss sfx
		Music.play(this, R.raw.kisses);

		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());

		_res = getResources();

		hookAsListener();

		performFirstTimeConfig();

		enableRecentsButton();

		initializeWeather();

	}

	/**
	 * runs in a new thread so that UI does not lock-up should the weather
	 * take some time to retrieve. 
	 */
	private void initializeWeather() {
		_weather = new WeatherFacade(this);
		
		Runnable run = new Runnable() {
			public void run() {
				_weather.fetchCurrent();		
				_weather.prompt();
			}
		};

		Thread weatherThread = new Thread(run, "WeatherThread");
		weatherThread.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		Music.stop(this);
		_weather.pause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		_weather.resume();
		// no music to resume - it's just a short SFX
	}

	/**
	 * enable the Recents button only if there have been previous emails composed.
	 */
	private void enableRecentsButton() {
		final String s = new String(_prefs.getString(
				RecentsUtil.SUBJECT_RECENT_PREF + 1, ""));
		findViewById(R.id.recents_button).setEnabled(
				!(s.equals("") || s.equals(" ...")));
	}

	/**
	 * if this is the first time the app has run on this device since
	 * installation, prompt the user to enter some settings.
	 */
	private void performFirstTimeConfig() {
		if (ResourceUtil.isFirstRun(this)) {
			final String help = "Welcome to Keep Er Appy. "
					+ "Take a moment to fill in a few bits of "
					+ "information about yourself.";
			Toast.makeText(getApplicationContext(), help, Toast.LENGTH_LONG)
					.show();

			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 * When we return to the screen, the Recents button may need to be 
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			enableRecentsButton();
		}
	}

	/**
	 * this activity wants to listen to all its own UI widgets.
	 */
	private void hookAsListener() {
		findViewById(R.id.baked_messages_button).setOnClickListener(this);
		findViewById(R.id.compose_button).setOnClickListener(this);
		findViewById(R.id.about_button).setOnClickListener(this);
		findViewById(R.id.settings_button).setOnClickListener(this);
		findViewById(R.id.recents_button).setOnClickListener(this);
		findViewById(R.id.help_button).setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * determine what was clicked and fire off the appropriate action.
	 */
	@Override
	public void onClick(View v) {
		Intent intent = null;

		switch (v.getId()) {
		case R.id.compose_button: {
			intent = new Intent(this, ComposeActivity.class);
		}
			break;
		case R.id.baked_messages_button: {
			intent = new Intent(this, BakedMessagesActivity.class);
		}
			break;
		case R.id.about_button: {
			intent = new Intent(this, AboutActivity.class);
		}
			break;
		case R.id.settings_button: {
			intent = new Intent(this, SettingsActivity.class);
		}
			break;
		case R.id.recents_button: {
			intent = new Intent(this, RecentsActivity.class);
		}
			break;
		case R.id.help_button: {
			// showDialog(/* id */1);
			intent = new Intent(this, HelpActivity.class);
		}
			break;
		default:
			Log.d("PSK", "Unexpected view was clicked");

		};

		if (intent != null)
			startActivity(intent);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateDialog(int)
	 * The Help dialog is the only dialog that could have been created.
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(_res.getString(R.string.help_text))
				.setCancelable(false).setPositiveButton("OK", null);
		AlertDialog alert = builder.create();

		return alert;
	}
}