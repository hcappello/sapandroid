package de.schnocklake.demo.android.sapclient;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import de.schnocklake.demo.android.sapclient.data.WebServiceClient;

public class DetailTabActivity extends TabActivity implements Runnable, OnClickListener {
	TextView textView1;
	TextView textView2;
	TextView textView3;
	Dialog dialog;
	String kunnr;
	String name1;
	private String customerDetail[];
	private ProgressDialog pd;
	Button geoButton;
//	Button salesChartButton;
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
		setContentView(R.layout.tabdetail);

		TabHost mTabHost = getTabHost();

		mTabHost.addTab(mTabHost.newTabSpec("tab_test1")
				.setIndicator("Address").setContent(R.id.tab1));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test2")
				.setIndicator("General").setContent(R.id.textview2));
		mTabHost.addTab(mTabHost.newTabSpec("tab_test3").setIndicator("Bank")
				.setContent(R.id.tab3));

		mTabHost.setCurrentTab(0);

		kunnr = this.getIntent().getExtras().get("KUNNR").toString();
		name1 = this.getIntent().getExtras().get("NAME1").toString();

		geoButton = (Button) findViewById(R.id.GeoButton);
		geoButton.setEnabled(false);
		geoButton.setOnClickListener(this);

//		salesChartButton = (Button) findViewById(R.id.SalesChart);
//		salesChartButton.setOnClickListener(this);

		textView1 = (TextView) findViewById(R.id.textview1);
		textView2 = (TextView) findViewById(R.id.textview2);
		textView3 = (TextView) findViewById(R.id.textview3);

		textView1.setText(Html.fromHtml("<b>" + kunnr + " " + name1
				+ "</b><br> looking up detail"));
		textView2.setText(Html.fromHtml("<b>" + kunnr + " " + name1
				+ "</b><br> looking up detail"));
		textView3.setText(Html.fromHtml("<b>" + kunnr + " " + name1
				+ "</b><br> looking up detail"));

		pd = ProgressDialog.show(this, "Loading Customer '" + name1 + "'",
				"Please wait...", true, false);

		this.setWebServiceClient(new WebServiceClient(PreferenceManager.getDefaultSharedPreferences(this)));
		
		Thread thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
//		customerDetail = webServiceClient.getCustomersDetail2(kunnr);
		customerDetail = webServiceClient.getCustomersDetailCRM2(kunnr);
		handler.sendEmptyMessage(0);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			textView1.setText(Html.fromHtml(customerDetail[0]));
			textView2.setText(Html.fromHtml(customerDetail[1]));
			textView3.setText(Html.fromHtml(customerDetail[2]));
			
			if (!customerDetail[3].equalsIgnoreCase("")) {
				geoButton.setEnabled(true);				
			}
			else {
				geoButton.setEnabled(false);
				geoButton.setVisibility(android.view.View.INVISIBLE);
			}
			pd.dismiss();
		}
	};

	@Override
	public void onClick(View v) {
		if (v == geoButton) {
			String geoString = "geo:0,0?q=" + customerDetail[3];
			geoString = geoString.replaceAll("\\s", "+");
			geoString = geoString.replaceAll("-", "");
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(geoString));
			this.startActivity(i);
			
		} 
//		else if (v == salesChartButton) {
//			Intent i = new Intent(
//					this,
//					de.schnocklake.demo.android.sapclient.chart.SplineActivity.class);
//			this.startActivity(i);
//		}

	}
}
