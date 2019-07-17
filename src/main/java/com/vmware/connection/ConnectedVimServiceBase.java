/*
 * ******************************************************
 * Copyright VMware, Inc. 2010-2012.  All Rights Reserved.
 * ******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.vmware.connection;

import com.vmware.connection.helpers.GetMOREF;
import com.vmware.connection.helpers.WaitForValues;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.VimPortType;

import java.util.Map;

/**
 * An abstract base class, extend this class if your common needs to
 * open a connection to the virtual center server before it can do anything useful.
 * <p>
 * Example: The simplest possible extension class merely forms a connection and
 * specifies it's own common action.
 * <p>
 * <pre>
 *     &#064;Sample(name = "connect")
 *     public class Connect extends ConnectedVimServiceBase {
 *          &#064;Action
 *          public void action() {
 *               System.out.println("currently connected: " + this.isConnected());
 *          }
 *     }
 * </pre>
 * <p>
 * This is provided as an alternative to extending the Connection class directly.
 * <p>
 * For a very simple connected sample:
 *
 */
public abstract class ConnectedVimServiceBase {
    public static final String PROP_ME_NAME = "name";
    public static final String SVC_INST_NAME = "ServiceInstance";
    protected Connection connection;
    protected VimPortType vimPort;
    protected ServiceContent serviceContent;
    protected ManagedObjectReference rootRef;
    @SuppressWarnings("rawtypes")
    protected Map headers;
    protected WaitForValues waitForValues;
    protected GetMOREF getMOREFs;

    /**
     * Use this method to get a reference to the service instance itself.
     * <p>
     *
     * @return a managed object reference referring to the service instance itself.
     */
    public ManagedObjectReference getServiceInstanceReference() {
        return connection.getServiceInstanceReference();
    }

    /**
     * A method for dependency injection of the connection object.
     * <p>
     *
     * @param connect the connection object to use for this POJO
     * @see com.vmware.connection.Connection
     */
    public void setConnection(Connection connect) {
        this.connection = connect;
    }

    /**
     * connects this object, returns itself to allow for method chaining
     *
     * @return a connected reference to itself.
     */
    public Connection connect() {

        try {
            connection.connect();
            this.waitForValues = new WaitForValues(connection);
            this.getMOREFs = new GetMOREF(connection);
            this.headers = connection.getHeaders();
            this.vimPort = connection.getVimPort();
            this.serviceContent = connection.getServiceContent();
            this.rootRef = serviceContent.getRootFolder();
        }
        catch (ConnectionException e) {
            // SSO or Basic connection exception has occurred
            e.printStackTrace();
            // not the best form, but without a connection these samples are pointless.
            System.err.println("No valid connection available. Exiting now.");
            System.exit(0);
        }
        return connection;
    }

    /**
     * disconnects this object and returns a reference to itself for method chaining
     *
     * @return a disconnected reference to itself
     */
    public Connection disconnect() {
        this.waitForValues = null;
        try {
            connection.disconnect();
        } catch (Throwable t) {
            throw new ConnectionException(t);
        }
        return connection;
    }

    public class ConnectionException extends RuntimeException {
        public ConnectionException(Throwable cause) {
            super(cause);
        }

        public ConnectionException(String message) {
            super(message);
        }
    }
}
