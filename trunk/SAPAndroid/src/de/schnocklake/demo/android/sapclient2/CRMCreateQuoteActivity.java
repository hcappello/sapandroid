package de.schnocklake.demo.android.sapclient2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import de.schnocklake.demo.android.sapclient2.R;
import de.schnocklake.demo.android.sapclient2.data.WebServiceClient;

public class CRMCreateQuoteActivity extends Activity  {
	AutoCompleteTextView textView;

	private final static int ABOUT_ITEM_ID = 1;
	private final static int SETTINGS_ITEM_ID = 2;

	private WebServiceClient webServiceClient;

	public WebServiceClient getWebServiceClient() {
		return webServiceClient;
	}

	public void setWebServiceClient(WebServiceClient webServiceClient) {
		this.webServiceClient = webServiceClient;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.crm_create_quote);
		if (this.webServiceClient == null) {
			this.setWebServiceClient(new WebServiceClient(PreferenceManager.getDefaultSharedPreferences(this)));
		}	
		
//		this.webServiceClient.createCRMQuote();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu
				.add(0, ABOUT_ITEM_ID, 0,
						getString(R.string.about_menu_item_title));
		menu.add(0, SETTINGS_ITEM_ID, 0,
				getString(R.string.settings_menu_item_title));
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result = null;
		Builder builder;

		switch (id) {
		case ABOUT_ITEM_ID:
			builder = new AlertDialog.Builder(this);

			builder.setTitle(R.string.about_menu_item_title);
			builder.setMessage(R.string.dlg_about_msg);

			builder.setPositiveButton("Ok", null);

			result = builder.create();
			break;

		default:
			result = super.onCreateDialog(id);
			break;
		}

		return result;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case ABOUT_ITEM_ID:
			showDialog(ABOUT_ITEM_ID);
			return true;
		case SETTINGS_ITEM_ID:
			startActivity(new Intent(this, EditPreferences.class));
			return true;

		default:
			return false;
		}
	}

}
