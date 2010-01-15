package de.schnocklake.demo.android.sapclient2.data;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

public class PreferenceHolder implements OnSharedPreferenceChangeListener {
	private String endpoint = "http://erp.esworkplace.sap.com/sap/bc/soap/rfc";
	private String username = "S00000000";
	private String password = "password";

	public PreferenceHolder(SharedPreferences sharedPrefs) {
		setAll(
				sharedPrefs
						.getBoolean(
								"de.schnocklake.demo.android.sapclient2.preference_key_demo_ws_settings",
								true),
				sharedPrefs
						.getString(
								"de.schnocklake.demo.android.sapclient2.preference_key_webserviceendpoint",
								"http://ERP_HostName:ERP_HttpPort/sap/bc/soap/rfc"),
				sharedPrefs
						.getString(
								"de.schnocklake.demo.android.sapclient2.preference_key_username",
								"username"),
				sharedPrefs
						.getString(
								"de.schnocklake.demo.android.sapclient2.preference_key_password",
								"passwort"));
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	}

	public PreferenceHolder(String endpoint, String username, String password) {
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public PreferenceHolder(boolean demoserver, String endpoint,
			String username, String password) {
		setAll(demoserver, endpoint, username, password);
	}

	private void setAll(boolean demoserver, String endpoint, String username,
			String password) {
		if (demoserver) {
			// this.endpoint = "http://erp.esworkplace.sap.com/sap/bc/soap/rfc";
			this.endpoint = "http://crm.esworkplace.sap.com/sap/bc/soap/rfc";
			this.username = "S0004428881";
			this.password = "Mantila1";
		} else {
			this.endpoint = endpoint;
			this.username = username;
			this.password = password;
		}
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
			String key) {
		if (key
				.equalsIgnoreCase("de.schnocklake.demo.android.sapclient2.preference_key_demo_ws_settings")
				|| key
						.equalsIgnoreCase("de.schnocklake.demo.android.sapclient2.preference_key_webserviceendpoint")
				|| key
						.equalsIgnoreCase("de.schnocklake.demo.android.sapclient2.preference_key_username")
				|| key
						.equalsIgnoreCase("de.schnocklake.demo.android.sapclient2.preference_key_password")) {

			setAll(
					sharedPrefs
							.getBoolean(
									"de.schnocklake.demo.android.sapclient2.preference_key_demo_ws_settings",
									true),
					sharedPrefs
							.getString(
									"de.schnocklake.demo.android.sapclient2.preference_key_webserviceendpoint",
									"http://ERP_HostName:ERP_HttpPort/sap/bc/soap/rfc"),
					sharedPrefs
							.getString(
									"de.schnocklake.demo.android.sapclient2.preference_key_username",
									"username"),
					sharedPrefs
							.getString(
									"de.schnocklake.demo.android.sapclient2.preference_key_password",
									"passwort"));
		}

	}

}
