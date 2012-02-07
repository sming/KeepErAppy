package org.psk.kea;

import org.psk.kea.R;

import android.app.ListActivity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 
 * @author Pete
 * POTENTIALLY TO BE MOTHBALLED/TRASHED
 */
public class BakedMessagesActivity extends ListActivity implements
		OnItemClickListener {

	public BakedMessagesActivity() {
		// TODO Auto-generated constructor stub
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String[] baked = getResources().getStringArray(R.array.baked_array);
		
		String[] pBaked = personalizeBaked(baked, "KEA_SIGN_IN", "Hi Luvva!");
		String[] ppBaked = personalizeBaked(pBaked, "KEA_SIGN_OFF", "Chicka XXX");

		setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, ppBaked));

		ListView lv = getListView();
		lv.setTextFilterEnabled(true);
		lv.setOnItemClickListener(this);
	}

	private String[] personalizeBaked(final String[] baked, final String reg, final String rep) {
		String[] res = new String[baked.length];
		
		for (int i = 0; i < baked.length; i++) {
			final String str = baked[i];
			res[i] = str.replaceAll(reg, rep);
		}
		return res;
	}

	@Override
	public void onItemClick(android.widget.AdapterView<?> parent, View view,
			int position, long id) {
		// When clicked, show a toast with the TextView text
		final CharSequence body = ((TextView) view).getText();
		Toast.makeText(getApplicationContext(), body, Toast.LENGTH_SHORT).show();

		Log.d("PSK", body.toString());

		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		final String eAddr = prefs.getString("etpHerEmail1", "");
		final String subject = prefs.getString("etpSubject1", "");
		
		HappyEmail hemail = new HappyEmail(eAddr, subject, body.toString());

		Sender sender = new Sender(this);
		sender.send(hemail);

		// add to recents
		Editor ed = prefs.edit();
		ed.putString("recents1", body.toString());
		ed.commit();
	}

}
