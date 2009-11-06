package de.schnocklake.demo.android.sapclient.data;

import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportBasicAuthSE;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;


public class WebServiceClient implements OnSharedPreferenceChangeListener {
	private String endpoint = "http://erp.esworkplace.sap.com/sap/bc/soap/rfc";
	private String username = "S00000000";
	private String password = "password";

	public WebServiceClient(SharedPreferences sharedPrefs) {
		setAll(
				sharedPrefs
						.getBoolean(
								"de.schnocklake.demo.android.sapclient.preference_key_demo_ws_settings",
								true),
				sharedPrefs
						.getString(
								"de.schnocklake.demo.android.sapclient.preference_key_webserviceendpoint",
								"http://ERP_HostName:ERP_HttpPort/sap/bc/soap/rfc"),
				sharedPrefs
						.getString(
								"de.schnocklake.demo.android.sapclient.preference_key_username",
								"username"),
				sharedPrefs
						.getString(
								"de.schnocklake.demo.android.sapclient.preference_key_password",
								"passwort")
				);
		sharedPrefs.registerOnSharedPreferenceChangeListener(this);
	}

	public WebServiceClient(String endpoint, String username, String password) {
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

	public WebServiceClient(boolean demoserver, String endpoint,
			String username, String password) {
		setAll(demoserver, endpoint, username, password);
	}

	private void setAll(boolean demoserver, String endpoint, String username,
			String password) {
		if (demoserver) {
			this.endpoint = "http://erp.esworkplace.sap.com/sap/bc/soap/rfc";
			this.username = "S0004428881";
			this.password = "Mantila1";
		} else {
			this.endpoint = endpoint;
			this.username = username;
			this.password = password;
		}
	}

	public ArrayList<Customer> searchCustomersTableADRC(String namePattern,
			int maxCount) {
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		ArrayList<Customer> names = new ArrayList<Customer>();

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			request.addProperty("ROWCOUNT", maxCount + "");
			request.addProperty("ROWSKIPS", "0");
			request.addProperty("DELIMITER", ";");
			request.addProperty("QUERY_TABLE", "ADRC");
			request.addProperty("DATA", null);

			SoapObject fields = new SoapObject(NAMESPACE, "FIELDS");
			request.addProperty("FIELDS", fields);

			SoapObject fieldsitem;
			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "NAME1");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "ADDRNUMBER");
			fields.addProperty("item", fieldsitem);

			SoapObject item = new SoapObject(NAMESPACE, "item");
			item.addProperty("TEXT", "NAME1 like '" + namePattern
					+ "%' OR NAME1 like '% " + namePattern + "%'");

			SoapObject SELOPT_TAB = new SoapObject(NAMESPACE, "OPTIONS");
			SELOPT_TAB.addProperty("item", item);
			request.addProperty("OPTIONS", SELOPT_TAB);

			// String s = request.toString();

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
					endpoint, username, password);
			Log.i("SAP", "vor androidHttpTransport");
			androidHttpTransport.call(METHOD_NAME, envelope);

			Log.i("SAP", "von envelope.bodyIn");

			SoapObject so = (SoapObject) envelope.bodyIn;

			SoapObject SALES_ORDERS = (SoapObject) so.getProperty("DATA");

			String resultLine = "";
			Customer customer;

			for (int i = 0; i < SALES_ORDERS.getPropertyCount(); i++) {
				SoapObject sales_order_so = (SoapObject) SALES_ORDERS
						.getProperty(i);
				resultLine = ((SoapPrimitive) sales_order_so.getProperty("WA"))
						.toString();
				String[] token = resultLine.split("\\;");
				customer = new Customer(token[0].trim(), token[1].trim(), "",
						"");
				names.add(customer);
			}
			Log.i("SAP", "von return");
			return names;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ArrayList<Customer> searchCustomersTableKNA1(String namePattern,
			int maxCount) {
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		ArrayList<Customer> names = new ArrayList<Customer>();

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			request.addProperty("ROWCOUNT", maxCount + "");
			request.addProperty("ROWSKIPS", "0");
			request.addProperty("DELIMITER", ";");
			request.addProperty("QUERY_TABLE", "KNA1");
			request.addProperty("DATA", null);

			SoapObject fields = new SoapObject(NAMESPACE, "FIELDS");
			request.addProperty("FIELDS", fields);

			SoapObject fieldsitem;
			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "NAME1");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "KUNNR");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "ORT01");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "STRAS");
			fields.addProperty("item", fieldsitem);

			SoapObject SELOPT_TAB = new SoapObject(NAMESPACE, "OPTIONS");
			SoapObject item;

			String[] token = namePattern.split("\\ ");
			String opt;

			for (int i = 0; i < token.length; i++) {

				opt = "NAME1 like '" + token[i] + "%' OR NAME1 like '% "
						+ token[i] + "%'";
				if (i > 0) {
					opt = "AND ( " + opt;
				} else {
					opt = " ( " + opt;
				}
				item = new SoapObject(NAMESPACE, "item");
				item.addProperty("TEXT", opt);
				SELOPT_TAB.addProperty("item", item);

				opt = "OR NAME1 like '" + token[i].toLowerCase()
						+ "%' OR NAME1 like '% " + token[i].toLowerCase()
						+ "%'";
				item = new SoapObject(NAMESPACE, "item");
				item.addProperty("TEXT", opt);
				SELOPT_TAB.addProperty("item", item);

				opt = "OR NAME1 like '" + token[i].toUpperCase()
						+ "%' OR NAME1 like '% " + token[i].toUpperCase()
						+ "%'";
				item = new SoapObject(NAMESPACE, "item");
				item.addProperty("TEXT", opt);
				SELOPT_TAB.addProperty("item", item);

				token[i] = token[i].substring(0, 1).toUpperCase()
						+ token[i].substring(1, token[i].length())
								.toLowerCase();
				opt = "OR NAME1 like '" + token[i] + "%' OR NAME1 like '% "
						+ token[i] + "%'";
				item = new SoapObject(NAMESPACE, "item");
				if (i > 0) {
					opt = opt + " ) ";
				} else {
					opt = opt + " ) ";
				}
				item.addProperty("TEXT", opt);
				SELOPT_TAB.addProperty("item", item);
			}

			request.addProperty("OPTIONS", SELOPT_TAB);

			// String s = request.toString();

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
					endpoint, username, password);
			Log.i("SAP", "vor androidHttpTransport");
			androidHttpTransport.call(METHOD_NAME, envelope);

			Log.i("SAP", "von envelope.bodyIn");

			SoapObject so = (SoapObject) envelope.bodyIn;

			SoapObject SALES_ORDERS = (SoapObject) so.getProperty("DATA");

			String resultLine = "";
			Customer customer;

			for (int i = 0; i < SALES_ORDERS.getPropertyCount(); i++) {
				SoapObject sales_order_so = (SoapObject) SALES_ORDERS
						.getProperty(i);
				resultLine = ((SoapPrimitive) sales_order_so.getProperty("WA"))
						.toString();
				// String[] token2 = resultLine.split("\\;");
				String[] token2 = split(resultLine, ';');
				Log.i("resultLine", resultLine);
				customer = new Customer(token2[0].trim(), token2[1].trim(),
						token2[2].trim(), token2[3].trim());
				// customer = new Customer(token2[0].trim(), token2[0].trim(),
				// token2[0].trim(), token2[0].trim());
				names.add(customer);
			}
			Log.i("SAP", "von return");
			return names;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] getCustomersDetail2(String customerNumber) {
		String METHOD_NAME = "BAPI_CUSTOMER_GETDETAIL2";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		try {
			Object o = null;
			;
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			request.addProperty("CUSTOMERNO", customerNumber);

			// String s = request.toString();

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
					endpoint, username, password);
			Log.i("SAP", "vor androidHttpTransport");
			androidHttpTransport.call(METHOD_NAME, envelope);

			Log.i("SAP", "von envelope.bodyIn");

			SoapObject so = (SoapObject) envelope.bodyIn;

			PropertyInfo propertyInfo = null;

			String customerAddress = "";
			String customerAddressGeoSearch = "";
			try {
				o = null;
				o = so.getProperty("CUSTOMERADDRESS");
			} catch (Exception ex) {
			}
			if (o != null) {
				SoapObject CUSTOMERADDRESS = (SoapObject) o;
				for (int i = 0; i < CUSTOMERADDRESS.getPropertyCount(); i++) {
					propertyInfo = new PropertyInfo();
					CUSTOMERADDRESS.getPropertyInfo(i, null, propertyInfo);
					Object prop = CUSTOMERADDRESS.getProperty(i);
					if (prop instanceof SoapPrimitive) {
						SoapPrimitive prim = (SoapPrimitive) prop;
						if (prim.toString().length() > 0) {
							Log.i(propertyInfo.name, prim.toString());
							customerAddress = customerAddress + "<b>"
									+ propertyInfo.name + ":</b>   "
									+ prim.toString() + "<br>";
						}
					}
				}

				Object prop = CUSTOMERADDRESS.getProperty("STREET");
				if (prop instanceof SoapPrimitive) {
					customerAddressGeoSearch = customerAddressGeoSearch
							+ ((SoapPrimitive) prop).toString();
				}
				prop = CUSTOMERADDRESS.getProperty("CITY");
				if (prop instanceof SoapPrimitive) {
					customerAddressGeoSearch = customerAddressGeoSearch + "+"
							+ ((SoapPrimitive) prop).toString();
				}
				prop = CUSTOMERADDRESS.getProperty("COUNTRY");
				if (prop instanceof SoapPrimitive) {
					customerAddressGeoSearch = customerAddressGeoSearch + "+"
							+ ((SoapPrimitive) prop).toString();
				}

			}

			String customerGeneral = "";
			try {
				o = null;
				o = so.getProperty("CUSTOMERGENERALDETAIL");
			} catch (Exception ex) {
			}
			if (o != null) {
				SoapObject CUSTOMERGENERALDETAIL = (SoapObject) o;
				for (int i = 0; i < CUSTOMERGENERALDETAIL.getPropertyCount(); i++) {
					propertyInfo = new PropertyInfo();
					CUSTOMERGENERALDETAIL
							.getPropertyInfo(i, null, propertyInfo);
					Object prop = CUSTOMERGENERALDETAIL.getProperty(i);
					if (prop instanceof SoapPrimitive) {
						SoapPrimitive prim = (SoapPrimitive) prop;
						if (prim.toString().length() > 0) {
							Log.i(propertyInfo.name, prim.toString());
							customerGeneral = customerGeneral + "<b>"
									+ propertyInfo.name + ":</b>   "
									+ prim.toString() + "<br>";
						}
					}
				}
			}

			String customerBank = "";
			try {
				o = null;
				o = so.getProperty("CUSTOMERBANKDETAIL");
			} catch (Exception ex) {
			}
			if (o != null) {
				SoapObject CUSTOMERBANKDETAIL = (SoapObject) o;
				for (int i = 0; i < CUSTOMERBANKDETAIL.getPropertyCount(); i++) {
					propertyInfo = new PropertyInfo();
					CUSTOMERBANKDETAIL.getPropertyInfo(i, null, propertyInfo);
					Object prop = CUSTOMERBANKDETAIL.getProperty(i);
					if (prop instanceof SoapPrimitive) {
						SoapPrimitive prim = (SoapPrimitive) prop;
						if (prim.toString().length() > 0) {
							Log.i(propertyInfo.name, prim.toString());
							customerBank = customerBank + "<b>"
									+ propertyInfo.name + ":</b>   "
									+ prim.toString() + "<br>";
						}
					}
				}
			}

			// customerAddress = customerAddress + "</table>";
			return new String[] { customerAddress, customerGeneral,
					customerBank, customerAddressGeoSearch };
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public void createCRMQuote() {
		String METHOD_NAME = "CustomerQuoteCRMCreateRequest_sync_V1";
		String NAMESPACE = "http://sap.com/xi/CRM/Global2";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("ProcessingTypeCode", "AG");

			SoapObject customerQuote = new SoapObject(NAMESPACE, "CustomerQuote");
			request.addProperty("CustomerQuote", customerQuote);
			
			
			SoapObject buyerParty = new SoapObject(NAMESPACE, "BuyerParty");
			buyerParty.addProperty("InternalID", "3028");
			customerQuote.addProperty("BuyerParty", buyerParty);

			SoapObject Item = new SoapObject(NAMESPACE, "Item");
			customerQuote.addProperty("Item", Item);

			SoapObject Product = new SoapObject(NAMESPACE, "Product");
			
			
			Item.addProperty("Product", Product);
			
			
			
			SoapObject ScheduleLine = new SoapObject(NAMESPACE, "ScheduleLine");
			SoapObject Quantity =  new SoapObject(NAMESPACE, "Quantity");
			Quantity.addAttribute("unitCode", "CT");
			Quantity.addProperty("value", "10");
			ScheduleLine.addProperty("Quantity",Quantity);
			
//			ScheduleLine.addProperty("TypeCode", "CT");

//			PropertyInfo propertyInfo = new PropertyInfo();
			
			
			
			
//			SoapObject Quantity = new SoapObject(NAMESPACE, "Quantity");
//			Quantity.addProperty("unitCode", "CT");
			
			
			
//			ScheduleLine.addProperty("Quantity", Quantity);
			
//			ScheduleLine.addProperty("Quantity", "10");
//			ScheduleLine.addProperty("DateTime", "ISA-0003");
			Item.addProperty("ScheduleLine", ScheduleLine);
			
			
			
			
			// String s = request.toString();

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
//			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
//					"http://crm.esworkplace.sap.com:80/sap/bc/srt/xip/sap/CRM_BTSQ_CUSTQTECRTRC?sap-client=800", username, password);
			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
					"http://192.168.1.36:8080/", username, password);
			Log.i("SAP", "vor androidHttpTransport");
			androidHttpTransport.call(METHOD_NAME, envelope);

			Log.i("SAP", "von envelope.bodyIn");

			SoapObject so = (SoapObject) envelope.bodyIn;
System.err.println(so);
//			SoapObject SALES_ORDERS = (SoapObject) so.getProperty("DATA");
//
//			String resultLine = "";
//
//			String spmon = "";
//			double UMNETWR = 0;
//
//			double x = 0;
//
//			for (int i = 0; i < SALES_ORDERS.getPropertyCount(); i++) {
//				SoapObject sales_order_so = (SoapObject) SALES_ORDERS
//						.getProperty(i);
//				resultLine = ((SoapPrimitive) sales_order_so.getProperty("WA"))
//						.toString();
//				String[] token = resultLine.split("\\;");
//
//				if ((!spmon.equalsIgnoreCase(token[0].trim()) && !spmon
//						.equalsIgnoreCase(""))
//						|| (i + 1 == SALES_ORDERS.getPropertyCount())) {
//					x = x + 1;
//
//					ChartPoint point = new ChartPoint(x, UMNETWR
//							+ java.lang.Double.parseDouble(token[1].trim()));
//					point.setLabel(spmon);
//					pointCollection.add(point);
//					UMNETWR = 0;
//				} else {
//					UMNETWR = UMNETWR
//							+ java.lang.Double.parseDouble(token[1].trim());
//				}
//				spmon = token[0].trim();
//			}

			Log.i("SAP", "von return");
			return;

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
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

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPrefs,
			String key) {
		if (key
				.equalsIgnoreCase("de.schnocklake.demo.android.sapclient.preference_key_demo_ws_settings")
				|| key
						.equalsIgnoreCase("de.schnocklake.demo.android.sapclient.preference_key_webserviceendpoint")
				|| key
						.equalsIgnoreCase("de.schnocklake.demo.android.sapclient.preference_key_username")
				|| key
						.equalsIgnoreCase("de.schnocklake.demo.android.sapclient.preference_key_password")) {

			setAll(
					sharedPrefs
							.getBoolean(
									"de.schnocklake.demo.android.sapclient.preference_key_demo_ws_settings",
									true),
					sharedPrefs
							.getString(
									"de.schnocklake.demo.android.sapclient.preference_key_webserviceendpoint",
									"http://ERP_HostName:ERP_HttpPort/sap/bc/soap/rfc"),
					sharedPrefs
							.getString(
									"de.schnocklake.demo.android.sapclient.preference_key_username",
									"username"),
					sharedPrefs
							.getString(
									"de.schnocklake.demo.android.sapclient.preference_key_password",
									"passwort"));
		}

	}

}
