package pl.janpogocki.hitchhikingcalculator.javas;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Jan on 12.04.2017.
 * Opening websites, sending, receiving cookies, sending POST messages - mini web browser
 * Receiving bitmaps - images
 */

public class FetchWebsite {
    private String URL = "";
    private String locationHTTP;
    private Integer responseCode;

    private class TrivialTrustManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {}

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    private class TrivialHostVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String host, SSLSession session) {
            if (host.equalsIgnoreCase("www.e-petrol.pl"))
                return true;
            else
                return false;
        }
    }

    public FetchWebsite(String _url){
        URL = _url;
    }

    public String getWebsite(){
        String ret = "";

        // Send data
        try
        {
            BufferedReader reader;

            // Defined URL  where to send data
            java.net.URL url = new URL(URL);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{ new TrivialTrustManager() }, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new TrivialHostVerifier());
            conn.setSSLSocketFactory(sc.getSocketFactory());

            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0");

            conn.connect();

            // Getting Location if redirect
            locationHTTP = conn.getHeaderField("Location");
            responseCode = conn.getResponseCode();

            // Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            while((line = reader.readLine()) != null)
            {
                // Append server response in string
                sb.append(line + "\n");
            }
            ret = sb.toString();

            reader.close();
            conn.disconnect();

        } catch(Exception ex){
            ex.printStackTrace();
        }

        return ret;
    }

    public String getLocationHTTP(){
        if (locationHTTP == null)
            return "";
        else
            return locationHTTP;
    }

    public Integer getResponseCode(){
        return responseCode;
    }
}