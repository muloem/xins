/*
 * $Id$
 */
package org.xins.tools.security;

import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Tool that fetches the SSL certificate of a remote host.
 *
 * @version $Revision$ $Date$
 * @author Ernst de Haan (<a href="mailto:znerd@FreeBSD.org">znerd@FreeBSD.org</a>)
 */
public final class FetchCertificate extends Object {

   //-------------------------------------------------------------------------
   // Class fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Class functions
   //-------------------------------------------------------------------------

   public static void main(String[] args) throws Throwable {

      // Check arguments
      if (args == null || args.length != 1) {
         System.err.println("ERROR: Exactly one argument expected with host name.");
         return;
      }

      // Disable SSL certificate validation
      disableCertificateValidation();

      // Create the client socket
      int port = 443;
      String hostname = args[0];
      SSLSocketFactory factory = HttpsURLConnection.getDefaultSSLSocketFactory();
      SSLSocket socket = (SSLSocket)factory.createSocket(hostname, port);
    
      // Connect to the server
      socket.startHandshake();
    
      // Retrieve the server's certificate chain
      Certificate[] serverCerts = socket.getSession().getPeerCertificates();
    
      // Close the socket
      socket.close();

      // Print all certificates
      int count = serverCerts == null ? 0 : serverCerts.length;

      if (count != 1) {
         System.err.println("ERROR: Expected 1 certificate, but received " + count + '.');
         return;
      }

      storeCertificate(args[0], serverCerts[0]);
   }

   private static void disableCertificateValidation()
   throws Throwable {

      // Create a trust manager that does not validate certificate chains
      TrustManager[] trustAllCerts = new TrustManager[] {
         new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
               return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
               // empty
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
               // empty
            }
         }
      };
    
      // Install the all-trusting trust manager
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
   }

   private static void storeCertificate(String alias, Certificate cert)
   throws Throwable {

      // Create an empty keystore object with no password
      KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
      keystore.load(null, null);
    
      // Add the certificate
      keystore.setCertificateEntry(alias, cert);
    
      // Save the keystore
      String file = alias + ".cert";
      FileOutputStream out = new FileOutputStream(file);
      keystore.store(out, new char[] {});
      out.close();

      System.out.println("Stored certificate in " + file);
   }



   //-------------------------------------------------------------------------
   // Constructors
   //-------------------------------------------------------------------------

   /**
    * Constructs a new <code>FetchCertificate</code>. This constructor
    * is private since this no instances of this class should be created.
    */
   private FetchCertificate() {
      // empty
   }


   //-------------------------------------------------------------------------
   // Fields
   //-------------------------------------------------------------------------

   //-------------------------------------------------------------------------
   // Methods
   //-------------------------------------------------------------------------
}
