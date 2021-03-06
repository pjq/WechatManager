package me.pjq.httpclient;

import me.pjq.util.Log;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

/*****************************************
 * The {@link HttpClient} helper,it support trust the self-signed
 * {@link KeyStore} You can use {@link #getInstance()} to get it.And use the
 * static function {@link #createNewDefaultHttpClient()} to create the special
 * http/https client.<br>
 * Example
 * 
 * <pre>
 * HttpClient httpClient = null;
 * HttpClientHelper helper = null;
 * 
 * // Two ways to create the HttpClient.
 * if (true) {
 * 	helper = HttpClientHelper.getInstance();
 * 	httpClient = helper.createNewDefaultHttpClient();
 * } else {
 * 	helper = HttpClientHelper.getInstance();
 * 	httpClient = new DefaultHttpClient();
 * 	helper.registerHttpsScheme(httpClient);
 * }
 * </pre>
 * 
 *****************************************/
public class HttpClientHelper {
	private static HttpClientHelper mInstance;
	private MySSLSocketFactory mMySSLSocketFactory;

	public HttpClientHelper() {
		// TODO Auto-generated constructor stub
		KeyStore truststore;
		try {
			truststore = KeyStore.getInstance(KeyStore.getDefaultType());
			mMySSLSocketFactory = new MySSLSocketFactory(truststore);
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Get the singleton instance.
	 * 
	 * @return
	 */
	public static HttpClientHelper getInstance() {
		if (null == mInstance) {
			mInstance = new HttpClientHelper();
		}

		return mInstance;
	}

	/**
	 * Create the new {@link DefaultHttpClient} with the special registed
	 * http/https schemes,and it support trust the self-signed KeyStore.
	 * 
	 * @return {@link DefaultHttpClient} or null if something error occurs.
	 */
	public HttpClient createNewDefaultHttpClient() {
		if (null != mMySSLSocketFactory) {
			return mMySSLSocketFactory.createNewDefaultHttpClient();
		} else {
			return null;
		}
	}

	/**
	 * Set Proxy here,including http proxy and https proxy
	 */
	protected static void setProxy() {
		// Set Http Proxy
		System.getProperties().setProperty("http.proxyHost", "10.85.40.153");
		System.getProperties().setProperty("http.proxyPort", "8000");

		// Set Https Proxy
		// FIXME Should import the certificates to avoid the trust problem.
		// To avoid the certification problems,just trust all the certs.
		// SSLUtilities.trustAllHttpsCertificates();
		System.getProperties().setProperty("https.proxyHost", "10.85.40.153");
		System.getProperties().setProperty("https.proxyPort", "8000");
	}

	/**
	 * Register the https scheme to the httpClient
	 * 
	 * @param httpClient
	 *            the HttpClient need to support the self-signed KeyStore.
	 */
	public void registerHttpsScheme(HttpClient httpClient) {
		SSLSocketFactory factory;
		try {
			// factory = new SSLSocketFactory(null);
			factory = SSLSocketFactory.getSocketFactory();
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);
			factory = new MySSLSocketFactory(trustStore);
			factory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			Scheme https = new Scheme("https", factory, 443);
			httpClient.getConnectionManager().getSchemeRegistry()
					.register(https);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Read data from InputStreamReader
	 * 
	 * @param isr
	 *            InputStreamReader
	 * @return The Data read from The InputStreamReader
	 */
	public static String readFromInputStream(InputStreamReader isr) {
		String result = "";

		BufferedReader rd = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			isr.close();
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Read data from InputStreamReader
	 * 
	 * @param isr
	 *            InputStreamReader
	 * @return The Data read from The InputStreamReader
	 */
	public static String readFromInputStream2(InputStreamReader isr) {
		String result = "";

		BufferedReader rd = new BufferedReader(isr);
		StringBuilder sb = new StringBuilder();
		String line = null;

		try {
			while ((line = rd.readLine()) != null) {
				sb.append(line + '\n');
			}

			isr.close();
			rd.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public static byte[] download(URL url) throws IOException {
		URLConnection uc = url.openConnection();
		int len = uc.getContentLength();
		InputStream is = new BufferedInputStream(uc.getInputStream());
		try {
			byte[] data = new byte[len];
			int offset = 0;
			while (offset < len) {
				int read = is.read(data, offset, data.length - offset);
				if (read < 0) {
					break;
				}
				offset += read;
			}
			if (offset < len) {
				throw new IOException(String.format(
						"Read %d bytes; expected %d", offset, len));
			}
			return data;
		} finally {
			is.close();
		}
	}

	public static void downloadFile(String file, String url) {
		Log.i("", "downloadFile url = " + url + " , file = " + file);
		URL website;
		try {
			website = new URL(url);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(file);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
			fos.close();
			fos = null;
			Log.i("", "downloadFile Success url = " + url + " , file = " + file);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
