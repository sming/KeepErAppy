package org.psk.kea;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.widget.BaseAdapter;

/**
 * @author Pete
 * A multi-preference screen settings activity. For some of the sub-screens,
 * the contents of a preference are used as the title for the sub-screen. This
 * gives the user an indication of what the settings' current values are.
 */
public class SettingsActivity extends PreferenceActivity implements
		OnPreferenceChangeListener {
	
	private static final int NUM_SETTINGS = 5;
	private PreferenceScreen _prefScreen;
	private SharedPreferences _prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		if (android.os.Build.VERSION.SDK_INT >= 6) {
			// there's a display bug in 2.1, 2.2, 2.3 (unsure about 2.0)
			// which causes PreferenceScreens to have a black background.
			// http://code.google.com/p/android/issues/detail?id=4611
			setTheme(android.R.style.Theme_Black);
		}

		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.layout.settings);
		
		_prefs = PreferenceManager
				.getDefaultSharedPreferences(getBaseContext());
	}

	@Override
	protected void onResume() {
		super.onResume();
		populate();
	}

	@Override
	protected void onStart() {
		super.onStart();
		populate();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onWindowFocusChanged(boolean)
	 * if this screen has focus, it could be that a preference has been updated.
	 * Hence update the sub-screen summaries.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			populateScreenSummaries();
			BaseAdapter adapter = (BaseAdapter) _prefScreen.getRootAdapter();
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * grab existing preferences and pop (some of) them into the screen's summaries.
	 */
	private void populate() {
		_prefScreen = getPreferenceScreen();
		setAllETP_Titles();
		populateScreenSummaries();
	}

	/**
	 * Populate the EditTextPreference widgets with their current contents so the
	 * user can see the current value.
	 */
	private void setAllETP_Titles() {
		setETP_TitlesFromPrefs("etpSignIn");
		setETP_TitlesFromPrefs("etpSignOff");
		setETP_TitlesFromPrefs("etpSubject");
		setETP_TitleFromPrefN("etpHerEmail", 1);

		setETPTempTitleFromPref("Cold");
		setETPTempTitleFromPref("Hot");

		setETPCityTitleFromPref();
		/*setETP_Title("etpWeatherCold");
		setETP_Title("etpWeatherHot");
		setETP_Title("etpWeatherTextCold");
		setETP_Title("etpWeatherTextHot");
		setETP_Title("etpWeatherCity");*/
	}

	private void setETPCityTitleFromPref() {
		final String key = "etpWeatherCity";
		final String city = _prefs.getString(key, "City, Country");

		EditTextPreference etp = (EditTextPreference) _prefScreen
				.findPreference(key);

		etp.setTitle("Location "+ "\t[" + city + "]");

		// this only needs to be called once but it's OK to call it more than once
		// and requires less code.
		etp.setOnPreferenceChangeListener(this);		
	}

	/**
	 * @param threshold - "Hot" or "Cold" postfix. Corresponds to the two preference keys.
	 */
	private void setETPTempTitleFromPref(final String threshold) {
		final String key = "etpWeather" + threshold;
		final String val = _prefs.getString(key, "");

		EditTextPreference etp = (EditTextPreference) _prefScreen
				.findPreference(key);

		etp.setTitle(threshold + "\t[" + val + "F]");

		// this only needs to be called once but it's OK to call it more than once
		// and requires less code.
		etp.setOnPreferenceChangeListener(this);
	}

	/**
	 * For each screen, use the contents of a preference as its summary text. 
	 */
	private void populateScreenSummaries() {
		populateScreenSummary("etpHerEmail1", "EmailScreen");
		populateScreenSummary("etpWeatherCity", "WeatherScreen");
		populateScreenSummary("etpSignIn1", "SignInScreen");
		populateScreenSummary("etpSubject1", "SubjectScreen");
		populateScreenSummary("etpSignOff1", "SignOffScreen");
	}

	/**
	 * @param prefKey - e.g. key of preference etpHerEmail1 (e.g. abc@def.com)
	 * @param screenKey - key of the sub-screen
	 */
	private void populateScreenSummary(final String prefKey,
			final String screenKey) {
		final String prefVal = _prefs.getString(prefKey, "") + " ...";
		Preference pref = _prefScreen.findPreference(screenKey);
		pref.setSummary(prefVal);
	}

	/**
	 * @param prefKeyPrefix - e.g. etpSignOff
	 */
	private void setETP_TitlesFromPrefs(final String prefKeyPrefix) {
		for (int i = 1; i < NUM_SETTINGS; i++) {
			setETP_TitleFromPrefN(prefKeyPrefix, i);
		}
	}

	/**
	 * @param keyPrefix - preference key prefix
	 * @param i - i'th key
	 */
	private void setETP_TitleFromPrefN(final String keyPrefix, final int i) {
		final String keyN = keyPrefix + (new Integer(i)).toString();
		setETP_TitleFromPref(keyN);
	}

	/**
	 * @param key - name of shared preference and name of ETP widget. 
	 */
	private void setETP_TitleFromPref(final String key) {
		final String val = _prefs.getString(key, "");

		EditTextPreference etp = (EditTextPreference) _prefScreen
				.findPreference(key);
		if (etp == null)
			System.out.println(key);
		etp.setTitle(val);

		// this only needs to be called once but it's OK to call it more than once
		// and requires less code.
		etp.setOnPreferenceChangeListener(this);
	}

	/* (non-Javadoc)
	 * @see android.preference.Preference.OnPreferenceChangeListener#onPreferenceChange(android.preference.Preference, java.lang.Object)
	 * update the preference's title to reflect its updated contents.
	 */
	@Override
	public boolean onPreferenceChange(Preference pref, Object obj) {
		pref.setTitle(obj.toString());
		return true;
	}
}
