package de.schnocklake.demo.android.sapclient2.data;

import java.util.ArrayList;
import java.util.Vector;

import de.schnocklake.android.sap.searchhelp.SAPValueHelpSoapAdapter;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CRMCustomerValueHelpAdapter extends SAPValueHelpSoapAdapter {

	public CRMCustomerValueHelpAdapter(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ArrayList searchMatches(String searchText) {
		return searchCustomersTableBUT000(searchText, 30);
	}

	public ArrayList<Customer> searchCustomersTableBUT000(String namePattern,
			int maxCount) {
		Vector<String> optionsVector = new Vector<String>();
		String[] token = namePattern.split("\\ ");
		String opt;

		for (int i = 0; i < token.length; i++) {
			opt = "MC_NAME1 like '" + token[i].toUpperCase()
					+ "%' OR MC_NAME1 like '% " + token[i].toUpperCase() + "%'";
			if (i > 0) {
				opt = "AND ( " + opt;
			} else {
				opt = " ( " + opt;
			}

			optionsVector.add(opt);

			opt = "OR MC_NAME2 like '" + token[i].toUpperCase()
					+ "%' OR MC_NAME2 like '% " + token[i].toUpperCase() + "%'";

			if (i > 0) {
				opt = opt + " ) ";
			} else {
				opt = opt + " ) ";
			}
			optionsVector.add(opt);
		}

		String options[] = new String[optionsVector.size()];
		optionsVector.copyInto(options);

		String[][] results = callRFCReadTable("BBPV_BUPA_ADDR", maxCount,
				new String[] { "PARTNER", "MC_NAME1", "MC_NAME2", "NAME_ORG",
						"MC_CITY", "MC_STREET", "POST_CODE" }, options);

		ArrayList<Customer> names = new ArrayList<Customer>();

		for (String[] resultLine : results) {
			Customer customer = new Customer(resultLine[1].trim() + " "
					+ resultLine[2].trim(), resultLine[0].trim(), resultLine[4]
					.trim(), resultLine[5].trim());
			names.add(customer);
		}
		return names;
	}

	@Override
	protected View generateResultitemView(Object item, View convertView,
			ViewGroup parent) {
		Customer customer = (Customer) item;
		View view;
		if (convertView == null) {
			view = mInflater
					.inflate(
							de.schnocklake.demo.android.sapclient2.R.layout.simple_dropdown_item_2line,
							parent, false);
		} else {
			view = convertView;
		}

		((TextView) view.findViewById(R.id.text1)).setText(customer.getName());
		((TextView) view.findViewById(R.id.text2)).setText(customer.getCity()
				+ ',' + customer.getStreet());

		return view;
	}

	@Override
	protected CharSequence convertResultToString(Object resultValue) {
		return ((Customer) resultValue).getName();
	}

	public class Customer {
		private String name;
		private String number;
		private String city;
		private String street;

		public Customer(String name, String number, String city, String street) {
			super();
			this.name = name;
			this.number = number;
			this.city = city;
			this.street = street;
		}

		public String getName() {
			return name;
		}

		public String getNumber() {
			return number;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return this.getName();
		}

		public String getCity() {
			return city;
		}

		public String getStreet() {
			return street;
		}

	}
}
