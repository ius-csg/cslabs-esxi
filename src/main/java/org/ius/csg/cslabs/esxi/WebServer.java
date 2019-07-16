package org.ius.csg.cslabs.esxi;

import com.vmware.common.ssl.TrustAll;
import com.vmware.connection.BasicConnection;
import com.vmware.SimpleClient;
import com.vmware.conrete.VirtualMachine;
import com.vmware.vim25.InvalidPropertyFaultMsg;
import com.vmware.vim25.RuntimeFaultFaultMsg;
import org.ius.csg.cslabs.esxi.responses.ErrorResponse;

import java.io.PrintStream;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;

import static spark.Spark.*;
import static org.ius.csg.cslabs.esxi.JsonUtil.*;

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

      options("/*",
              (request, response) -> {

                  String accessControlRequestHeaders = request
                          .headers("Access-Control-Request-Headers");
                  if (accessControlRequestHeaders != null) {
                      response.header("Access-Control-Allow-Headers",
                              accessControlRequestHeaders);
                  }

                  String accessControlRequestMethod = request
                          .headers("Access-Control-Request-Method");
                  if (accessControlRequestMethod != null) {
                      response.header("Access-Control-Allow-Methods",
                              accessControlRequestMethod);
                  }

                  return "OK";
              });

      before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));

      get("/acquireTicket", (req, res) -> {
          res.type("application/json");
          if(!req.queryParams().contains("name")) {
              res.status(400);
              return new ErrorResponse("The name parameter is required");
          }
          String name = req.queryParams("name");
          Optional<VirtualMachine> targetVirtualMachine = simpleClient.getVms().stream().filter(vm -> vm.name.trim().equals(name.trim())).findFirst();
          if(!targetVirtualMachine.isPresent()) {
              res.status(400);
              return new ErrorResponse("VM " + name + " not found");
          }


          return simpleClient.acquireTicket(targetVirtualMachine.get());
      }, json());


      get("/vms", (req, res) -> {
          res.type("application/json");
          ArrayList<VirtualMachine> vms =  simpleClient.getVms();
          return vms.stream().map(vm -> vm.name).toArray();
      }, json());

      after((req, res) -> {
          res.type("application/json");
      });

  }
}
