package de.schnocklake.demo.android.sapclient;


import android.app.ListActivity;
import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import de.schnocklake.demo.android.sapclient.data.SAPValueHelpSoapAdapter;

public class ValueHelpListViewActivity extends ListActivity {
	AutoCompleteTextView textView;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SAPValueHelpSoapAdapter adapter = new SAPValueHelpSoapAdapter(this);
        this.setListAdapter(adapter);
        getListView().setTextFilterEnabled(true);

/*        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mStrings));
        getListView().setTextFilterEnabled(true);
*/        
    }
   
    
}
