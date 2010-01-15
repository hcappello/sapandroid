package de.schnocklake.demo.android.sapclient2.data;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;
import de.schnocklake.soap.SoapException;
import de.schnocklake.soap.SoapUtils;

public class WebServiceClient {
	private String endpoint = "http://crm.esworkplace.sap.com/sap/bc/soap/rfc";
	private String username = "s0004428881";
	private String password = "Mantila1";

	public WebServiceClient() {		
	}

	public WebServiceClient(String endpoint, String username, String password) {
		this.endpoint = endpoint;
		this.username = username;
		this.password = password;
	}

	@SuppressWarnings("unchecked")
	public String[] getCustomersDetailCRM2(String customerNumber) {
		String customerAddress = "", customerGeneral = "", 
//		customerBank = "", 
		customerAddressGeoSearch = "";

		Document doc = createCustomerDetailCRMRequest(customerNumber);
//		String s = doc.asXML();
		try {
			HttpURLConnection connection;

			connection = (HttpURLConnection) new URL(
					"http://crm.esworkplace.sap.com:80/sap/bc/srt/xip/sap/CRM_BUPA_CUSTID_QR?sap-client=800")
					.openConnection();

			// connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			// connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());

			Document responseDoc = SoapUtils.request(doc, connection, username, password);
			connection.disconnect();

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

	public Document createRFCReadTableRequestRequest(String table, int maxCount, 
			String fields[], String options[]) {
		String METHOD_NAME = "RFC_READ_TABLE";
		String NAMESPACE = "urn:sap-com:document:sap:rfc:functions";

		DocumentFactory factory = DocumentFactory.getInstance();

		Document document = factory.createDocument();

		Element Envelope = document.addElement(factory.createQName("Envelope",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		//Element header = 
		Envelope.addElement(factory.createQName("Header",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element Body = Envelope.addElement(factory.createQName("Body",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element RFC_READ_TABLE = Body.addElement(factory.createQName(
				METHOD_NAME, "urn",
				NAMESPACE));

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

}
