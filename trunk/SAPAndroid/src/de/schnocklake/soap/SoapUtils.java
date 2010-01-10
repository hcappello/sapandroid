package de.schnocklake.soap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import android.util.Log;

public class SoapUtils {

	public static Document request(Document document, String endPoint,
			String username, String password) throws SoapException {
		HttpURLConnection connection;
		try {
			connection = (HttpURLConnection) new URL(endPoint).openConnection();
			return SoapUtils.request(document, connection, username, password);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	public void postData() {
		
		
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.yoursite.com/script.php");

		((DefaultHttpClient)httpclient).getCredentialsProvider().setCredentials(
	        new AuthScope("crm.esworkplace.sap.com", 80),
	        new UsernamePasswordCredentials("s0004428881", "Mantila1")); 

		
		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("id", "12345"));
			nameValuePairs.add(new BasicNameValuePair("stringdata",
					"AndDev is Cool!"));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}

	public static Document request(Document document,
			HttpURLConnection connection, String username, String password)
			throws SoapException {
		String request = document.asXML();

		byte[] requestData = request.getBytes();
		try {
			connection.setUseCaches(false);
			connection.setDoOutput(true);
			connection.setDoInput(true);

			if (username != null && password != null) {
				StringBuffer buf = new StringBuffer(username);
				buf.append(':').append(password);
				byte[] raw = buf.toString().getBytes();
				buf.setLength(0);
				buf.append("Basic ");
				org.kobjects.base64.Base64.encode(raw, 0, raw.length, buf);
				connection.setRequestProperty("Authorization", buf.toString());
			}

			connection.setRequestProperty("User-Agent",
					"Jakarta Commons-HttpClient/3.1");
			connection.setRequestProperty("SOAPAction", "xyungeloesst");
			connection.setRequestProperty("Content-Type", "text/xml");
			connection.setRequestProperty("Connection", "close");
			connection.setRequestProperty("Content-Length", ""
					+ requestData.length);
			connection.setRequestMethod("POST");
			connection.connect();
			OutputStream os = connection.getOutputStream();
			String requestDump = new String(requestData);
			Log.i("request", requestDump);
			os.write(requestData, 0, requestData.length);
			os.flush();
			os.close();

			InputStream is;
			// connection.connect();
			// is = connection.getInputStream();

			try {
				connection.connect();
				is = connection.getInputStream();
			} catch (IOException e) {
				Log.i("SAP", "IOException");

				is = connection.getErrorStream();
				if (is == null) {
					connection.disconnect();
					throw (e);
				}
			}

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] buf = new byte[256];
			while (true) {
				int rd = is.read(buf, 0, 256);
				if (rd == -1)
					break;
				bos.write(buf, 0, rd);
			}
			bos.flush();
			buf = bos.toByteArray();
			String responseDump = new String(buf);
			Log.i("response", responseDump);
			is.close();
			is = new ByteArrayInputStream(buf);

			SAXReader reader = new SAXReader(); // dom4j SAXReader

			Document responseDocument;

			Log.i("vor parse", "vor parse");
			System.err.println("!!!!!responseDump");
			System.err.println("!!!!!responseDump");
			System.err.println("!!!!!responseDump");
			System.err.println(responseDump);
			responseDocument = reader.read(is);
			Log.i("nach parse", "nach parse");
			connection.disconnect();
			return responseDocument;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		} // dom4j Document
	}

	private static TrustManager[] trustManagers;

	public static class _FakeX509TrustManager implements
			javax.net.ssl.X509TrustManager {
		private static final X509Certificate[] _AcceptedIssuers = new X509Certificate[] {};

		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
		}

		public boolean isClientTrusted(X509Certificate[] chain) {
			return (true);
		}

		public boolean isServerTrusted(X509Certificate[] chain) {
			return (true);
		}

		public X509Certificate[] getAcceptedIssuers() {
			return (_AcceptedIssuers);
		}
	}

	public static SSLSocketFactory getFakeSSLSocketFactory() {
		javax.net.ssl.SSLContext context = null;
		if (trustManagers == null) {
			trustManagers = new javax.net.ssl.TrustManager[] { new _FakeX509TrustManager() };
		}
		try {
			context = javax.net.ssl.SSLContext.getInstance("TLS");
			context.init(null, trustManagers, new SecureRandom());
		} catch (NoSuchAlgorithmException e) {
			Log.e("allowAllSSL", e.toString());
		} catch (KeyManagementException e) {
			Log.e("allowAllSSL", e.toString());
		}
		// javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(context
		// .getSocketFactory());
		return context.getSocketFactory();

	}

	public static HostnameVerifier getFakeHostnameVerifier() {
		return new HostnameVerifier() {
			public boolean verify(String hostname, SSLSession session) {
				return true;
			}
		};
	}

	public static void allowAllSSL() {
		javax.net.ssl.HttpsURLConnection
				.setDefaultHostnameVerifier(getFakeHostnameVerifier());
	}

}
