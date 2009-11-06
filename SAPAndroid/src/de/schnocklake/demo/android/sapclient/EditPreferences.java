package de.schnocklake.demo.android.sapclient;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;

public class EditPreferences extends PreferenceActivity implements OnPreferenceChangeListener {
	CheckBoxPreference demoPref = null; 
	EditTextPreference urlPref = null;
	EditTextPreference usernamePref = null;
	EditTextPreference passwordPref = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		demoPref = (CheckBoxPreference)this.findPreference("de.schnocklake.demo.android.sapclient.preference_key_demo_ws_settings");
		demoPref.setOnPreferenceChangeListener(this);
		
		urlPref = (EditTextPreference)this.findPreference("de.schnocklake.demo.android.sapclient.preference_key_webserviceendpoint");
		usernamePref = (EditTextPreference)this.findPreference("de.schnocklake.demo.android.sapclient.preference_key_username");
		passwordPref = (EditTextPreference)this.findPreference("de.schnocklake.demo.android.sapclient.preference_key_password");
		
		
		boolean checked = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("de.schnocklake.demo.android.sapclient.preference_key_demo_ws_settings", true);
		if (checked == false ) {
			urlPref.setEnabled(true);
			usernamePref.setEnabled(true);
			passwordPref.setEnabled(true);
		}
		else {
			urlPref.setEnabled(false);
			usernamePref.setEnabled(false);
			passwordPref.setEnabled(false);
		}

	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (newValue.equals(Boolean.FALSE)) {
			urlPref.setEnabled(true);
			usernamePref.setEnabled(true);
			passwordPref.setEnabled(true);
		}
		else {
			urlPref.setEnabled(false);
			usernamePref.setEnabled(false);
			passwordPref.setEnabled(false);
		}
		return true;
	}		
}
