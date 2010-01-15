package de.schnocklake.demo.android.sapclient2.data;

import java.util.ArrayList;
import java.util.Vector;

import de.schnocklake.android.sap.searchhelp.SAPValueHelpSoapAdapter;

import android.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CRMProductValueHelpAdapter extends SAPValueHelpSoapAdapter {

	public CRMProductValueHelpAdapter(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected ArrayList searchMatches(String searchText) {
		return searchProductTable(searchText, 30);
	}

	public ArrayList<Product> searchProductTable(String namePattern,
			int maxCount) {
		Vector<String> optionsVector = new Vector<String>();
		String[] token = namePattern.split("\\ ");
		String opt;

		for (int i = 0; i < token.length; i++) {
			opt = "SHTEXT_LARGE like '" + token[i].toUpperCase()
					+ "%' OR SHTEXT_LARGE like '% " + token[i].toUpperCase()
					+ "%'";
			if (i > 0) {
				opt = "AND ( " + opt;
			} else {
				opt = " ( " + opt;
			}

			optionsVector.add(opt);

			opt = "OR PRODUCT_ID like '" + token[i].toUpperCase()
					+ "%' OR PRODUCT_ID like '% " + token[i].toUpperCase()
					+ "%'";

			if (i > 0) {
				opt = opt + " ) ";
			} else {
				opt = opt + " ) ";
			}
			optionsVector.add(opt);
		}

		String options[] = new String[optionsVector.size()];
		optionsVector.copyInto(options);

		String[][] results = callRFCReadTable("BBPV_F4PR_SHTEXT", maxCount,
				new String[] { "PRODUCT_GUID", "SHTEXT_LARGE", "PRODUCT_ID",
						"LANGU" }, options);

		ArrayList<Product> products = new ArrayList<Product>();

		for (String[] resultLine : results) {
			Product product = new Product(resultLine[0].trim(), resultLine[1]
					.trim(), resultLine[2].trim(), resultLine[3].trim());
			products.add(product);
		}
		return products;
	}

	@Override
	protected View generateResultitemView(Object item, View convertView,
			ViewGroup parent) {
		Product product = (Product) item;
		View view;
		if (convertView == null) {
			view = mInflater
					.inflate(
							de.schnocklake.demo.android.sapclient2.R.layout.simple_dropdown_item_2line,
							parent, false);
		} else {
			view = convertView;
		}

		((TextView) view.findViewById(R.id.text1)).setText(product
				.getSHTEXT_LARGE());
		((TextView) view.findViewById(R.id.text2)).setText(product
				.getPRODUCT_ID()
				+ ',' + product.getLANGU() + ',' + product.getPRODUCT_GUID());

		return view;
	}

	@Override
	protected CharSequence convertResultToString(Object resultValue) {
		return ((Product) resultValue).getPRODUCT_ID();
	}

	public class Product {
		public Product(String pRODUCTGUID, String sHTEXTLARGE,
				String pRODUCTID, String lANGU) {
			super();
			PRODUCT_GUID = pRODUCTGUID;
			SHTEXT_LARGE = sHTEXTLARGE;
			PRODUCT_ID = pRODUCTID;
			LANGU = lANGU;
		}

		private String PRODUCT_GUID;
		private String SHTEXT_LARGE;
		private String PRODUCT_ID;
		private String LANGU;

		public String getPRODUCT_GUID() {
			return PRODUCT_GUID;
		}

		public String getSHTEXT_LARGE() {
			return SHTEXT_LARGE;
		}

		public String getPRODUCT_ID() {
			return PRODUCT_ID;
		}

		public String getLANGU() {
			return LANGU;
		}
	}
}
