package de.schnocklake.demo.android.sapclient.data;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportBasicAuthSE;

import de.schnocklake.soap.SoapException;
import de.schnocklake.soap.SoapUtils;

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
								"passwort"));
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

	public ArrayList<Customer> searchCustomersTableBUT000(String namePattern,
			int maxCount) {
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		ArrayList<Customer> names = new ArrayList<Customer>();

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			request.addProperty("ROWCOUNT", maxCount + "");
			request.addProperty("ROWSKIPS", "0");
			request.addProperty("DELIMITER", ";");
			request.addProperty("QUERY_TABLE", "BUT000");
			request.addProperty("DATA", null);

			SoapObject fields = new SoapObject(NAMESPACE, "FIELDS");
			request.addProperty("FIELDS", fields);

			SoapObject fieldsitem;
			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "NAME_FIRST");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "NAME_LAST");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "PARTNER");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "MC_NAME1");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "MC_NAME2");
			fields.addProperty("item", fieldsitem);

			SoapObject SELOPT_TAB = new SoapObject(NAMESPACE, "OPTIONS");
			SoapObject item;

			String[] token = namePattern.split("\\ ");
			String opt;

			for (int i = 0; i < token.length; i++) {

				opt = "MC_NAME1 like '" + token[i].toUpperCase()
						+ "%' OR MC_NAME1 like '% " + token[i].toUpperCase()
						+ "%'";
				if (i > 0) {
					opt = "AND ( " + opt;
				} else {
					opt = " ( " + opt;
				}
				item = new SoapObject(NAMESPACE, "item");
				item.addProperty("TEXT", opt);
				SELOPT_TAB.addProperty("item", item);

				opt = "OR MC_NAME2 like '" + token[i].toUpperCase()
						+ "%' OR MC_NAME2 like '% " + token[i].toUpperCase()
						+ "%'";
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
				customer = new Customer(token2[0].trim() + " "
						+ token2[1].trim(), token2[2].trim(), token2[2].trim(),
						token2[3].trim());
				// customer = new Customer(token2[0].trim(), token2[1].trim(),
				// token2[2].trim(), token2[3].trim());
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

	public String[] getCustomersDetailCRM(String customerNumber) {
		String METHOD_NAME = "CustomerCRMByIDQuery";
		String NAMESPACE = "http://sap.com/xi/CRM/Global2";

		try {
			Object o = null;

			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			// request.addProperty("CUSTOMERNO", customerNumber);

			SoapObject BusinessPartnerSelectionByBusinessPartner = new SoapObject(
					NAMESPACE, "BusinessPartnerSelectionByBusinessPartner");
			BusinessPartnerSelectionByBusinessPartner.addProperty("InternalID",
					customerNumber);
			request.addProperty("BusinessPartnerSelectionByBusinessPartner",
					BusinessPartnerSelectionByBusinessPartner);

			SoapObject MessageHeader = new SoapObject(NAMESPACE,
					"MessageHeader");
			request.addProperty("MessageHeader", MessageHeader);

			// String s = request.toString();

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
					"http://crm.esworkplace.sap.com:80/sap/bc/srt/xip/sap/CRM_BUPA_CUSTID_QR?sap-client=800",
					username, password);
			Log.i("SAP", "vor androidHttpTransport");
			androidHttpTransport.call(METHOD_NAME, envelope);

			Log.i("SAP", "von envelope.bodyIn");

			SoapObject so = (SoapObject) envelope.bodyIn;

			PropertyInfo propertyInfo = null;

			String customerAddress = "";
			String customerGeneral = "";
			String customerBank = "";
			String customerAddressGeoSearch = "";

			try {
				SoapObject BusinessPartner = ((SoapObject) so
						.getProperty("BusinessPartner"));
				String LifeCycleStatusName = ((SoapPrimitive) BusinessPartner
						.getProperty("LifeCycleStatusName")).toString();

				SoapObject Common = ((SoapObject) BusinessPartner
						.getProperty("Common"));
				SoapObject Person = ((SoapObject) Common.getProperty("Person"));
				SoapObject Name = ((SoapObject) Person.getProperty("Name"));

				SoapObject AddressInformation = ((SoapObject) BusinessPartner
						.getProperty("AddressInformation"));
				SoapObject Address = ((SoapObject) AddressInformation
						.getProperty("Address"));
				SoapObject PostalAddress = ((SoapObject) Address
						.getProperty("PostalAddress"));
				/*
				 * <CountryCode>US</CountryCode> <CountryName>United
				 * States</CountryName> <RegionCode
				 * listID="10660">CO</RegionCode>
				 * <RegionName>Colorado</RegionName> <CityName>Denver</CityName>
				 * <DistrictName>DENVER</DistrictName>
				 * <StreetPostalCode>80212</StreetPostalCode>
				 * <StreetPrefixName>Sea Drive</StreetPrefixName>
				 * <StreetName>Stuart St 0 - 9999</StreetName>
				 * <HouseID>4</HouseID> <POBoxIndicator>false</POBoxIndicator>
				 * <TaxJurisdictionCode
				 * listID="10378">0603101401</TaxJurisdictionCode>
				 * <TimeZoneCode>CST</TimeZoneCode> <TimeZoneName>Central Time
				 * (Dallas)</TimeZoneName>
				 * <RegionalStructureAddressConsistencyStatusCode
				 * >1</RegionalStructureAddressConsistencyStatusCode>
				 * <RegionalStructureAddressConsistencyStatusName>Not
				 * checked</RegionalStructureAddressConsistencyStatusName>
				 */

				// customerAddressGeoSearch =
				// ((SoapPrimitive)PostalAddress.getProperty("StreetPrefixName")).toString();
				customerAddressGeoSearch = customerAddressGeoSearch + " "
						+ getPropertyText(PostalAddress, "StreetName");
				customerAddressGeoSearch = customerAddressGeoSearch + " "
						+ getPropertyText(PostalAddress, "StreetPostalCode");
				customerAddressGeoSearch = customerAddressGeoSearch + " "
						+ getPropertyText(PostalAddress, "CityName");
				// customerAddressGeoSearch = customerAddressGeoSearch + " " +
				// getPropertyText(PostalAddress,"CountryName");
				customerAddress = customerAddressGeoSearch;
				customerGeneral = getPropertyText(Name, "DeviatingFullName")
						+ " " + customerAddressGeoSearch;
				customerBank = customerAddressGeoSearch;

			} catch (Exception ex) {
			}
			return new String[] { customerAddress, customerGeneral,
					customerBank, customerAddressGeoSearch };
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getPropertyText(SoapObject so, String property) {
		Object o = so.getProperty(property);
		if (o == null) {
			return "";
		} else {
			return ((SoapPrimitive) o).toString();
		}
	}

	public String[] getCustomersDetailCRM2(String customerNumber) {
		String customerAddress = "", customerGeneral = "", customerBank = "", customerAddressGeoSearch = "";

		Document doc = createDocument(customerNumber);
		String s = doc.asXML();
		try {
			HttpsURLConnection connection;

			connection = (HttpsURLConnection) new URL(
					"https://crm.esworkplace.sap.com:443/sap/bc/srt/xip/sap/CRM_BUPA_CUSTID_QR?sap-client=800")
					.openConnection();

			connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());

			Document responseDoc = SoapUtils.request(doc, connection,
					"s0004428881", "Mantila1");

			Node PostalAddress = responseDoc
					.selectSingleNode("//Address/PostalAddress");
			if (PostalAddress != null) {
				String CityName = PostalAddress.selectSingleNode("CityName")
						.getText();
				List<Node> postalItems = PostalAddress.selectNodes("*");
				for (Iterator<Node> postalIterator = postalItems.iterator(); postalIterator
						.hasNext();) {
					Node postalItem = postalIterator.next();
					Log.i("postalItem", postalItem.getName() + " "
							+ postalItem.getText());
					if ("CityName StreetPostalCode StreetName HouseID"
							.contains(postalItem.getName())) {
						customerAddressGeoSearch = customerAddressGeoSearch
								+ postalItem.getText() + " ";
					}
					Log.i("customerAddressGeoSearch", customerAddressGeoSearch);
				}
			}

			Node DeviatingFullName = responseDoc.selectSingleNode(
					"//Common/Person/Name/DeviatingFullName");

			if (DeviatingFullName != null) {
				customerGeneral = DeviatingFullName.getText();
			}
			// return new String[] { customerAddress, customerGeneral,
			// customerBank, customerAddressGeoSearch };
			return new String[] { customerGeneral, customerGeneral,
					customerGeneral, customerAddressGeoSearch };

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

	@SuppressWarnings("unused")
	public Document createDocument(String customerNumber) {
		DocumentFactory factory = DocumentFactory.getInstance();

		Document document = factory.createDocument();
		Element Envelope = document.addElement(factory.createQName("Envelope",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element header = Envelope.addElement(factory.createQName("Header",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element Body = Envelope.addElement(factory.createQName("Body",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element CustomerCRMByIDQuery = Body.addElement(factory
				.createQName("CustomerCRMByIDQuery", "glob",
						"http://sap.com/xi/CRM/Global2"));

		Element MessageHeader = CustomerCRMByIDQuery
				.addElement("MessageHeader");

		Element BusinessPartnerSelectionByBusinessPartner = CustomerCRMByIDQuery
				.addElement("BusinessPartnerSelectionByBusinessPartner");
		BusinessPartnerSelectionByBusinessPartner.addElement("InternalID")
				.addText(customerNumber);

		//		
		// Element ProcessingTypeCode = CustomerQuote.addElement(
		// "ProcessingTypeCode").addText("AG");
		// Element BuyerParty = CustomerQuote.addElement("BuyerParty");
		// Element InternalID = BuyerParty.addElement("InternalID")
		// .addText("3028");
		//
		// Element Item = CustomerQuote.addElement("Item");
		// Element Product = Item.addElement("Product");
		// Element EnteredProductInternalID = Product.addElement(
		// "EnteredProductInternalID").addText("ISA-0003");
		//
		// Element ScheduleLine = Item.addElement("ScheduleLine");
		// Element TypeCode = ScheduleLine.addElement("TypeCode").addText("CT");
		// Element Quantity = ScheduleLine.addElement("Quantity").addAttribute(
		// "unitCode", "CT").addText("10");
		// Element DateTime = ScheduleLine.addElement("DateTime").addAttribute(
		// "timeZoneCode", "CET").addAttribute(
		// "daylightSavingTimeIndicator", "true").addText(
		// "2009-10-18T16:32:33");

		return document;
	}

	public void createCRMQuote() {
		String METHOD_NAME = "CustomerQuoteCRMCreateRequest_sync_V1";
		String NAMESPACE = "http://sap.com/xi/CRM/Global2";

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
			request.addProperty("ProcessingTypeCode", "AG");

			SoapObject customerQuote = new SoapObject(NAMESPACE,
					"CustomerQuote");
			request.addProperty("CustomerQuote", customerQuote);

			SoapObject buyerParty = new SoapObject(NAMESPACE, "BuyerParty");
			buyerParty.addProperty("InternalID", "3028");
			customerQuote.addProperty("BuyerParty", buyerParty);

			SoapObject Item = new SoapObject(NAMESPACE, "Item");
			customerQuote.addProperty("Item", Item);

			SoapObject Product = new SoapObject(NAMESPACE, "Product");

			Item.addProperty("Product", Product);

			SoapObject ScheduleLine = new SoapObject(NAMESPACE, "ScheduleLine");
			SoapObject Quantity = new SoapObject(NAMESPACE, "Quantity");
			Quantity.addAttribute("unitCode", "CT");
			Quantity.addProperty("value", "10");
			ScheduleLine.addProperty("Quantity", Quantity);

			// ScheduleLine.addProperty("TypeCode", "CT");

			// PropertyInfo propertyInfo = new PropertyInfo();

			// SoapObject Quantity = new SoapObject(NAMESPACE, "Quantity");
			// Quantity.addProperty("unitCode", "CT");

			// ScheduleLine.addProperty("Quantity", Quantity);

			// ScheduleLine.addProperty("Quantity", "10");
			// ScheduleLine.addProperty("DateTime", "ISA-0003");
			Item.addProperty("ScheduleLine", ScheduleLine);

			// String s = request.toString();

			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER11);
			envelope.setOutputSoapObject(request);
			// HttpTransportBasicAuthSE androidHttpTransport = new
			// HttpTransportBasicAuthSE(
			// "http://crm.esworkplace.sap.com:80/sap/bc/srt/xip/sap/CRM_BTSQ_CUSTQTECRTRC?sap-client=800",
			// username, password);
			HttpTransportBasicAuthSE androidHttpTransport = new HttpTransportBasicAuthSE(
					"http://192.168.1.36:8080/", username, password);
			Log.i("SAP", "vor androidHttpTransport");
			androidHttpTransport.call(METHOD_NAME, envelope);

			Log.i("SAP", "von envelope.bodyIn");

			SoapObject so = (SoapObject) envelope.bodyIn;
			System.err.println(so);
			// SoapObject SALES_ORDERS = (SoapObject) so.getProperty("DATA");
			//
			// String resultLine = "";
			//
			// String spmon = "";
			// double UMNETWR = 0;
			//
			// double x = 0;
			//
			// for (int i = 0; i < SALES_ORDERS.getPropertyCount(); i++) {
			// SoapObject sales_order_so = (SoapObject) SALES_ORDERS
			// .getProperty(i);
			// resultLine = ((SoapPrimitive) sales_order_so.getProperty("WA"))
			// .toString();
			// String[] token = resultLine.split("\\;");
			//
			// if ((!spmon.equalsIgnoreCase(token[0].trim()) && !spmon
			// .equalsIgnoreCase(""))
			// || (i + 1 == SALES_ORDERS.getPropertyCount())) {
			// x = x + 1;
			//
			// ChartPoint point = new ChartPoint(x, UMNETWR
			// + java.lang.Double.parseDouble(token[1].trim()));
			// point.setLabel(spmon);
			// pointCollection.add(point);
			// UMNETWR = 0;
			// } else {
			// UMNETWR = UMNETWR
			// + java.lang.Double.parseDouble(token[1].trim());
			// }
			// spmon = token[0].trim();
			// }

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
