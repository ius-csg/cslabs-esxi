package org.ius.csg.cslabs.esxi;

import com.vmware.EsxiDataSource;
import com.vmware.common.ssl.TrustAll;
import com.vmware.connection.BasicConnection;
import com.vmware.SimpleClient;
import com.vmware.conrete.VirtualMachine;
import com.vmware.vim25.VirtualMachinePowerState;
import org.ius.csg.cslabs.esxi.responses.ErrorResponse;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Optional;

import static spark.Spark.*;
import static org.ius.csg.cslabs.esxi.json.JsonUtil.*;

public class WebServer
{
    private static SimpleClient simpleClient;
    private static EsxiDataSource esxiDataSource;

    private static void refreshSession()
    {
        if (simpleClient != null) {
            if (simpleClient.isSessionExpired()) {
                System.out.println("Dumping Session due to it being 30 minutes since last activity.");
                simpleClient.disconnect();
                simpleClient = makeSimpleClient();
            }
        } else {
            simpleClient = makeSimpleClient();
        }
    }

    public static SimpleClient makeSimpleClient()
    {
        SimpleClient simpleClient = new SimpleClient();
        BasicConnection basicConnection = new BasicConnection();
        basicConnection.setUrl(esxiDataSource.host);
        basicConnection.setUsername(esxiDataSource.username);
        basicConnection.setPassword(esxiDataSource.password);
        simpleClient.setConnection(basicConnection);
        simpleClient.connect();
        return simpleClient;
    }

    private static void parseArgs(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Invalid argument length. arguments: <host> <username> <password> are required");
        }
        esxiDataSource = new EsxiDataSource(args[0], args[1], args[2]);
    }

    public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException
    {
        parseArgs(args);
        System.out.println("Trusting all certificates."); // @todo be able to preconfigure ssl certs from the esxi host.
        TrustAll.trust();
        System.out.println("Constructing wsdl, this may take a while..");
        refreshSession();
        System.out.println("Webserver starting on http://localhost:4567");
        Middleware.register();
        registerRoutes();
    }

    public static void registerRoutes()
    {
        get("/acquireTicket", (req, res) -> {
            res.type("application/json");
            if (!req.queryParams().contains("name")) {
                res.status(400);
                return new ErrorResponse("The name parameter is required");
            }
            String name = req.queryParams("name");
            refreshSession();
            Optional<VirtualMachine> targetVirtualMachine = simpleClient.getVms().stream().filter(vm -> vm.name.trim().equals(name.trim())).findFirst();
            if (!targetVirtualMachine.isPresent()) {
                res.status(400);
                return new ErrorResponse("VM " + name + " not found");
            }
            if(targetVirtualMachine.get().powerState != VirtualMachinePowerState.POWERED_ON) {
                res.status(400);
                return new ErrorResponse("VM must be turned on!");
            }
            return simpleClient.acquireTicket(targetVirtualMachine.get());
        }, json());


        get("/vms", (req, res) -> {
            res.type("application/json");
            refreshSession();
            ArrayList<VirtualMachine> vms = simpleClient.getVms();
            return vms;
        }, json());
    }
}
