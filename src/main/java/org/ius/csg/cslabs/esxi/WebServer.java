package org.ius.csg.cslabs.esxi;

import com.vmware.common.ssl.TrustAll;
import com.vmware.connection.BasicConnection;
import com.vmware.SimpleClient;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import static spark.Spark.*;

public class WebServer
{
  public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException
  {
      if(args.length < 3) {
          throw new IllegalArgumentException("Invalid argument length. arguments: <host> <username> <password> are required");
      }
      String host = args[0];
      String username = args[1];
      String password = args[2];
      System.out.println("Trusting all certificates."); // @todo be able to preconfigure ssl certs from the esxi host.
      TrustAll.trust();
      SimpleClient simpleClient = new SimpleClient();
      simpleClient.setHostConnection(true);
      BasicConnection basicConnection = new BasicConnection();
      basicConnection.setUrl(host);
      basicConnection.setUsername(username);
      basicConnection.setPassword(password);
      simpleClient.setConnection(basicConnection);


      System.out.println("Constructing wsdl, this may take a while..");
      simpleClient.connect();
      System.out.println("Webserver starting on http://localhost:4567");
      get("/acquireTicket", (req, res) -> {
          try {
              return simpleClient.main();
          } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
              runtimeFaultFaultMsg.printStackTrace();
          } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
              invalidPropertyFaultMsg.printStackTrace();
          }
          return "Printed Inventory";
      });

  }
}
