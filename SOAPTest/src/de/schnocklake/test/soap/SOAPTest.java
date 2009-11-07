package de.schnocklake.test.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Node;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import de.schnocklake.soap.SoapException;
import de.schnocklake.soap.SoapUtils;

public class SOAPTest extends Activity {
	/** Called when the activity is first created. */
	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		TextView QuoteNumberTextView = (TextView) this
				.findViewById(R.id.QuoteNumberTextView);
		Document doc = createDocument();
		try {

			HttpsURLConnection connection;
			// allowAllSSL();
//			connection = (HttpsURLConnection) new URL(
//			"https://crm.esworkplace.sap.com:443/sap/bc/srt/xip/sap/CRM_BTSQ_CUSTQTECRTRC?sap-client=800")
//			.openConnection();
			connection = (HttpsURLConnection) new URL(
			"https://crm.esworkplace.sap.com:443/sap/bc/srt/xip/sap/CRM_BTSQ_CUSTQTECRTCHKQR?sap-client=800")
			.openConnection();

			connection.setSSLSocketFactory(SoapUtils.getFakeSSLSocketFactory());
			connection.setHostnameVerifier(SoapUtils.getFakeHostnameVerifier());

			Document responseDoc = SoapUtils.request(doc, connection,
					"s0000", "password");

			// Document responseDoc = SoapUtils
			// .request(
			// doc,
			// "http://crm.esworkplace.sap.com:80/sap/bc/srt/xip/sap/CRM_BTSQ_CUSTQTECRTRC?sap-client=800",
			// "S000000", "password");

			String QuoteNumber = "";
			String BusinessDocumentProcessingResultCode = "";
			BusinessDocumentProcessingResultCode = responseDoc
					.selectSingleNode(
							"//Log/BusinessDocumentProcessingResultCode")
					.getText();
			if (BusinessDocumentProcessingResultCode.equalsIgnoreCase("3")) {
				QuoteNumber = responseDoc
						.selectSingleNode("//CustomerQuote/ID").getText();
				System.err.println(QuoteNumber);

				QuoteNumberTextView.setText(QuoteNumber);
			} else {
				QuoteNumberTextView
						.setText("BusinessDocumentProcessingResultCode = "
								+ BusinessDocumentProcessingResultCode);
			}
			
			List<Node> logItems = responseDoc.selectNodes("//Log/Item");
			System.err.println("Size" + logItems.size());
			
			
			
			for (Iterator<Node> logIterator = logItems.iterator(); logIterator.hasNext(); ) {
				Node logItem = logIterator.next();
				System.err.println(logItem.selectSingleNode("TypeID").getText());
				System.err.println(logItem.selectSingleNode("SeverityCode").getText());
				System.err.println(logItem.selectSingleNode("Note").getText());
//				System.err.println(logItem.selectSingleNode("WebURI").getText());
			}
			

		} catch (SoapException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unused")
	public Document createDocument() {
		DocumentFactory factory = DocumentFactory.getInstance();

		Document document = factory.createDocument();
		Element Envelope = document.addElement(factory.createQName("Envelope",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element header = Envelope.addElement(factory.createQName("Header",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
		Element Body = Envelope.addElement(factory.createQName("Body",
				"soapenv", "http://schemas.xmlsoap.org/soap/envelope/"));
//		Element CustomerQuoteCRMCreateRequest_sync_V1 = Body.addElement(factory
//				.createQName("CustomerQuoteCRMCreateRequest_sync_V1", "glob",
//						"http://sap.com/xi/CRM/Global2"));
		Element CustomerQuoteCRMCreateRequest_sync_V1 = Body.addElement(factory
				.createQName("CustomerQuoteCRMCreateCheckQuery_sync", "glob",
						"http://sap.com/xi/CRM/Global2"));
		Element CustomerQuote = CustomerQuoteCRMCreateRequest_sync_V1
				.addElement("CustomerQuote");
		Element ProcessingTypeCode = CustomerQuote.addElement(
				"ProcessingTypeCode").addText("AG");
		Element BuyerParty = CustomerQuote.addElement("BuyerParty");
		Element InternalID = BuyerParty.addElement("InternalID")
				.addText("3028");

		Element Item = CustomerQuote.addElement("Item");
		Element Product = Item.addElement("Product");
		Element EnteredProductInternalID = Product.addElement(
		"EnteredProductInternalID").addText("ISA-0003");

		Element ScheduleLine = Item.addElement("ScheduleLine");
		Element TypeCode = ScheduleLine.addElement("TypeCode").addText("CT");
		Element Quantity = ScheduleLine.addElement("Quantity").addAttribute(
				"unitCode", "CT").addText("10");
		Element DateTime = ScheduleLine.addElement("DateTime").addAttribute(
				"timeZoneCode", "CET").addAttribute(
				"daylightSavingTimeIndicator", "true").addText(
				"2009-10-18T16:32:33");

		return document;
	}

}