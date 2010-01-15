package de.schnocklake.android.sap.searchhelp;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import de.schnocklake.android.soap.SoapException;
import de.schnocklake.android.soap.SoapUtils;
import de.schnocklake.demo.android.sapclient2.StopWatch;

public abstract class SAPValueHelpSoapAdapter extends BaseAdapter implements
		Filterable {
	protected String endpoint = "http://crm.esworkplace.sap.com/sap/bc/soap/rfc";
	protected String username = "S0004428881";
	protected String password = "Mantila1";

	public SAPValueHelpSoapAdapter(Context context) {
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new SAPValueHelpSoapFilter();
		}
		return mFilter;
	}

	@Override
	public int getCount() {
		return mObjects.size();
	}

	@Override
	public Object getItem(int position) {
		return mObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return generateResultitemView(getItem(position), convertView, parent);
	}

	private class SAPValueHelpSoapFilter extends Filter {

		@SuppressWarnings("unchecked")
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			Log.i("search", "performFiltering");
			FilterResults results = new FilterResults();

			final ArrayList newValues = new ArrayList(100);
			if (prefix != null) {
				String s = prefix.toString();
				Log.i("search", s);

				ArrayList<Object> names = searchMatches(s);

				newValues.addAll((ArrayList<Object>) names);
			}
			results.values = newValues;
			results.count = newValues.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			// noinspection unchecked

			mObjects = (List<Object>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

		@Override
		public CharSequence convertResultToString(Object resultValue) {
			return SAPValueHelpSoapAdapter.this
					.convertResultToString(resultValue);
			// if (resultValue instanceof Customer) {
			// return ((Customer) resultValue).getName();
			// } else
			// {
			// return super.convertResultToString(resultValue);
			// }
		}
	}

	private SAPValueHelpSoapFilter mFilter;
	private List<Object> mObjects;
	protected LayoutInflater mInflater;

	@SuppressWarnings("unchecked")
	public String[][] callRFCReadTable(String table, int maxCount,
			String fields[], String options[]) {

		StopWatch.start();
		Document request = createRFCReadTableRequest(table, maxCount, fields,
				options);
		Log.w("time", "createRFCReadTableRequestRequest2 took "
				+ StopWatch.getTime());
		// String req = request.asXML();

		try {
			HttpURLConnection connection;

			connection = (HttpURLConnection) new URL(endpoint).openConnection();

			// connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			// connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());

			Document responseDoc = SoapUtils.request(request, connection,
					username, password);
			Log.w("time", "createRFCReadTableRequestRequest2 request took "
					+ StopWatch.getTime());
			connection.disconnect();

			// String x = responseDoc.asXML();

			List<Node> dataItems = responseDoc.selectNodes("//DATA/item");
			Log.w("time", "responseDoc.selectNodes(\"//DATA/item\"); took "
					+ StopWatch.getTime());

			String results[][] = new String[dataItems.size()][fields.length];

			int zeile = 0;
			for (Iterator<Node> dataIterator = dataItems.iterator(); dataIterator
					.hasNext();) {
				String resultLine = dataIterator.next().selectSingleNode("WA")
						.getText();

				Log.i("resultLine", resultLine);

				results[zeile] = split(resultLine, ';');

				zeile++;
			}
			Log.w("time", "alles took " + StopWatch.getTime());

			return results;

		} catch (SoapException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Document createRFCReadTableRequest(String table, int maxCount,
			String fields[], String options[]) {
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		DocumentFactory factory = DocumentFactory.getInstance();

		Document document = factory.createDocument();

		Element Envelope = document.addElement(factory.createQName("Envelope",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		// Element header =
		Envelope.addElement(factory.createQName("Header", "soapenv",
				"http://schemas.xmlsoap.org/soap/envelope/"));
		Element Body = Envelope.addElement(factory.createQName("Body",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element RFC_READ_TABLE = Body.addElement(factory.createQName(
				METHOD_NAME, "urn", NAMESPACE));

		RFC_READ_TABLE.addElement("ROWCOUNT").addText("" + maxCount);
		RFC_READ_TABLE.addElement("ROWSKIPS").addText("0");
		RFC_READ_TABLE.addElement("DELIMITER").addText(";");
		RFC_READ_TABLE.addElement("QUERY_TABLE").addText(table);
		RFC_READ_TABLE.addElement("ROWCOUNT").addText("30");
		RFC_READ_TABLE.addElement("DATA");

		Element FIELDS = RFC_READ_TABLE.addElement("FIELDS");
		for (String field : fields) {
			FIELDS.addElement("item").addElement("FIELDNAME").addText(field);
		}

		Element OPTIONS = RFC_READ_TABLE.addElement("OPTIONS");
		for (String option : options) {
			OPTIONS.addElement("item").addElement("TEXT").addText(option);
		}

		return document;
	}

	public String[] split(String source, char at) {
		Vector<String> v = new Vector<String>();
		int i = source.indexOf(at);
		while (i > -1) {
			v.add(source.substring(0, i));
			source = source.substring(i + 1);
			i = source.indexOf(at);
		}
		v.add(source);
		String retArray[] = new String[v.size()];
		v.copyInto(retArray);
		return retArray;
	}

	@SuppressWarnings("unchecked")
	protected abstract ArrayList searchMatches(String searchText);

	protected abstract View generateResultitemView(Object item,
			View convertView, ViewGroup parent);

	protected abstract CharSequence convertResultToString(Object resultValue);

}
