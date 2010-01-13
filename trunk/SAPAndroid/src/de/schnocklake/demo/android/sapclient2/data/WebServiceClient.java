package de.schnocklake.demo.android.sapclient2.data;

import java.io.IOException;
import java.net.HttpURLConnection;
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

import de.schnocklake.demo.android.sapclient2.StopWatch;
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

	public ArrayList<Customer> searchCustomersTableBUT000(String namePattern,
			int maxCount) {
		StopWatch.start();
		
		
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		ArrayList<Customer> names = new ArrayList<Customer>();

		try {
			SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

			request.addProperty("ROWCOUNT", maxCount + "");
			request.addProperty("ROWSKIPS", "0");
			request.addProperty("DELIMITER", ";");
			request.addProperty("QUERY_TABLE", "BBPV_BUPA_ADDR");
			request.addProperty("DATA", null);

			SoapObject fields = new SoapObject(NAMESPACE, "FIELDS");
			request.addProperty("FIELDS", fields);

			SoapObject fieldsitem;
			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "PARTNER");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "MC_NAME1");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "MC_NAME2");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "NAME_ORG");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "MC_CITY");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "MC_STREET");
			fields.addProperty("item", fieldsitem);

			fieldsitem = new SoapObject(NAMESPACE, "item");
			fieldsitem.addProperty("FIELDNAME", "POST_CODE");
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
			
			Log.w("time", "bis vor request took " + StopWatch.getTime());
			androidHttpTransport.call(METHOD_NAME, envelope);
			Log.w("time", "nach request took " + StopWatch.getTime());

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
				// Customer(String name, String number, String city, String
				// street) {
				customer = new Customer(token2[1].trim() + " "
						+ token2[2].trim(), token2[0].trim(), token2[4].trim(),
						token2[5].trim());
				// customer = new Customer(token2[0].trim(), token2[1].trim(),
				// token2[2].trim(), token2[3].trim());
				// customer = new Customer(token2[0].trim(), token2[0].trim(),
				// token2[0].trim(), token2[0].trim());
				names.add(customer);
			}
			Log.i("SAP", "von return");
			Log.w("time", "allet took " + StopWatch.getTime());

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

		Document doc = createCustomerDetailCRMRequest(customerNumber);
		String s = doc.asXML();
		try {
			HttpURLConnection connection;

			connection = (HttpURLConnection) new URL(
					"http://crm.esworkplace.sap.com:80/sap/bc/srt/xip/sap/CRM_BUPA_CUSTID_QR?sap-client=800")
					.openConnection();

			// connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			// connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());

			Document responseDoc = SoapUtils.request(doc, connection,
					"s0004428881", "Mantila1");
			connection.disconnect();
			//			
			//
			// //HttpsURLConnection connection;
			//
			// // connection = (HttpsURLConnection) new URL(
			// //
			// "https://crm.esworkplace.sap.com:443/sap/bc/srt/xip/sap/CRM_BUPA_CUSTID_QR?sap-client=800")
			// // .openConnection();
			// connection = (HttpsURLConnection) new URL(
			// "https://crm.esworkplace.sap.com:443/sap/bc/srt/xip/sap/CRM_BUPA_CUSTID_QR?sap-client=800")
			// .openConnection();
			//
			// connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			// connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());
			//
			// Document responseDoc2 = SoapUtils.request(doc, connection,
			// "s0004428881", "Mantila1");
			// Sch connection.disconnect();

			Node PostalAddress = responseDoc
					.selectSingleNode("//Address/PostalAddress");
			if (PostalAddress != null) {
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
						customerAddress = customerAddress
								+ postalItem.getText() + "<br>";
					}
					Log.i("customerAddressGeoSearch", customerAddressGeoSearch);
				}
			}

			Node InternalID = responseDoc
					.selectSingleNode("//BusinessPartner/InternalID");
			if (InternalID != null) {
				customerGeneral = InternalID.getText() + "<br>";
			}

			Node FirstLineName = responseDoc
					.selectSingleNode("//Common/Organisation/Name/FirstLineName");
			if (FirstLineName != null) {
				customerGeneral = customerGeneral + " "
						+ FirstLineName.getText() + "<br>";
			}
			Node DeviatingFullName = responseDoc
					.selectSingleNode("//Common/Person/Name/DeviatingFullName");
			if (DeviatingFullName != null) {
				customerGeneral = customerGeneral + " "
						+ DeviatingFullName.getText() + "<br>";
			}

			// return new String[] { customerAddress, customerGeneral,
			// customerBank, customerAddressGeoSearch };
			return new String[] { customerAddress, customerGeneral, "",
					customerAddressGeoSearch };

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

	@SuppressWarnings("unchecked")
	public ArrayList<Customer> searchCustomersTableBUT000_2(String namePattern,
			int maxCount) {
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		ArrayList<Customer> names = new ArrayList<Customer>();

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
		
//		String options[] = (String[]) optionsVector.toArray();
		StopWatch.start();
		Document request = createRFCReadTableRequestRequest2("BBPV_BUPA_ADDR", maxCount, 
				new String[] { "PARTNER", "MC_NAME1", "MC_NAME2", "NAME_ORG",
						"MC_CITY", "MC_STREET", "POST_CODE" },
				options);
		Log.w("time", "createRFCReadTableRequestRequest2 took " + StopWatch.getTime());
		String req = request.asXML();
		
		try {
			HttpURLConnection connection;

			connection = (HttpURLConnection) new URL(endpoint).openConnection();

			// connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			// connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());

			Document responseDoc = SoapUtils.request(request, connection,
					username, password);
			Log.w("time", "createRFCReadTableRequestRequest2 request took " + StopWatch.getTime());
			connection.disconnect();

			String x = responseDoc.asXML();
			
			List<Node> dataItems = responseDoc.selectNodes("//DATA/item");
			Log.w("time", "responseDoc.selectNodes(\"//DATA/item\"); took " + StopWatch.getTime());
			
			for (Iterator<Node> dataIterator = dataItems.iterator(); dataIterator.hasNext();) {
				String resultLine = dataIterator.next().selectSingleNode("WA").getText();

				String[] token2 = split(resultLine, ';');
				Log.i("resultLine", resultLine);
				Customer customer = new Customer(token2[1].trim() + " "
						+ token2[2].trim(), token2[0].trim(), token2[4].trim(),
						token2[5].trim());
				names.add(customer);
			}
			Log.w("time", "alles took " + StopWatch.getTime());
			
			return names;

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

	public Document createRFCReadTableRequestRequest2(String table, int maxCount, 
			String fields[], String options[]) {
		DocumentFactory factory = DocumentFactory.getInstance();

		Document document = factory.createDocument();

		Element Envelope = document.addElement(factory.createQName("Envelope",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element header = Envelope.addElement(factory.createQName("Header",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element Body = Envelope.addElement(factory.createQName("Body",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element RFC_READ_TABLE = Body.addElement(factory.createQName(
				"RFC_READ_TABLE", "urn",
				"urn:sap-com:document:sap:rfc:functions"));

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

	@SuppressWarnings("unused")
	public Document createCustomerDetailCRMRequest(String customerNumber) {
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
