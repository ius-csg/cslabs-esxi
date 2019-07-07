package com.vmware.common.ssl;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;


public final class TrustAllTrustManager
  implements TrustManager, X509TrustManager
{
  public void register() throws KeyManagementException, NoSuchAlgorithmException
  { register(this); }


  public static void register(TrustManager tm) throws NoSuchAlgorithmException, KeyManagementException {
    TrustManager[] trustAllCerts = new TrustManager[1];

    trustAllCerts[0] = tm;
    SSLContext sc = SSLContext.getInstance("SSL");
    SSLSessionContext sslsc = sc.getServerSessionContext();
    sslsc.setSessionTimeout(0);
    sc.init(null, trustAllCerts, null);
    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
  }




  public X509Certificate[] getAcceptedIssuers() { return null; }

  public void checkServerTrusted(X509Certificate[] certs, String authType) throws CertificateException {}

  public void checkClientTrusted(X509Certificate[] certs, String authType) throws CertificateException {}
}


/* Location:              C:\projects\cslabs-webapp\vsphere\vsphere-ws\java\JAXWS\lib\samples-core-1.0.0.jar!\com\vmware\common\ssl\TrustAllTrustManager.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.0.6
 */
