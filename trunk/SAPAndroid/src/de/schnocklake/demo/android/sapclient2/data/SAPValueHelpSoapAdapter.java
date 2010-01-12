package de.schnocklake.demo.android.sapclient2.data;

import java.util.ArrayList;
import java.util.List;

import android.R;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class SAPValueHelpSoapAdapter extends BaseAdapter implements Filterable {
	private WebServiceClient webServiceClient;	
	
	public WebServiceClient getWebServiceClient() {
		return webServiceClient;
	}

	public void setWebServiceClient(WebServiceClient webServiceClient) {
		this.webServiceClient = webServiceClient;
	}

	public SAPValueHelpSoapAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);		
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
		View view;
		if (convertView == null) {
			view = mInflater.inflate(de.schnocklake.demo.android.sapclient2.R.layout.simple_dropdown_item_2line, parent, false);
		} else {
			view = convertView;
		}
		
		Customer customer = (Customer)getItem(position); 
		
		((TextView) view.findViewById(R.id.text1)).setText(customer.getName());
		((TextView) view.findViewById(R.id.text2)).setText(customer.getCity() + ',' + customer.getStreet() );
		
		return view;
	}
	
	
	
	private class SAPValueHelpSoapFilter extends Filter {
		
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			Log.i("search","performFiltering");
			FilterResults results = new FilterResults();

			final ArrayList<Customer> newValues = new ArrayList<Customer>(100);

			if (prefix != null) {
				String s = prefix.toString();
				Log.i("search", s);

				ArrayList<Customer> names = webServiceClient.searchCustomersTableBUT000(s,
						30);
				newValues.addAll((ArrayList<Customer>) names);
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
			
			mObjects = (List<Customer>) results.values;
			if (results.count > 0) {
				notifyDataSetChanged();
			} else {
				notifyDataSetInvalidated();
			}
		}

		@Override
		public CharSequence convertResultToString(Object resultValue) {
			if (resultValue instanceof Customer) {
				return ((Customer) resultValue).getName();				
			} else
			{
				return super.convertResultToString(resultValue);
			}
		}
		
		
		
	}
	
	private SAPValueHelpSoapFilter mFilter;
	private List<Customer> mObjects;
	private LayoutInflater mInflater;
	
	
	
	
	

}
