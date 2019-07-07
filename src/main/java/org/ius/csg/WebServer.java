package org.ius.csg;

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
      TrustAll.trust();
      SimpleClient simpleClient = new SimpleClient();
      simpleClient.setHostConnection(true);
      BasicConnection basicConnection = new BasicConnection();
      basicConnection.setUrl("https://esxi.home.local/sdk");
      basicConnection.setUsername("root");
      basicConnection.setPassword("");
      simpleClient.setConnection(basicConnection);
      System.out.println("Constructing wsdl, this may take a while..");
      simpleClient.connect();
      System.out.println("Webserver starting.");
      get("/acquireToken", (req, res) -> {
          try {
              return simpleClient.main();
          } catch (RuntimeFaultFaultMsg runtimeFaultFaultMsg) {
              runtimeFaultFaultMsg.printStackTrace();
          } catch (InvalidPropertyFaultMsg invalidPropertyFaultMsg) {
              invalidPropertyFaultMsg.printStackTrace();
          }
          return "Printed Inventory";
      });

      simpleClient.disconnect();
  }
}
