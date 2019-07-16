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

package com.vmware;

import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.conrete.DataCenter;
import com.vmware.conrete.Folder;
import com.vmware.conrete.RootFolder;
import com.vmware.conrete.VirtualMachine;
import com.vmware.vim25.*;
import org.ius.csg.cslabs.esxi.VmNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


/**
 * <pre>
 * SimpleClient
 *
 * This sample lists the inventory contents (managed entities)
 *
 * <b>Parameters:</b>
 * url          [required] : url of the web service
 * username     [required] : username for the authentication
 * password     [required] : password for the authentication
 *
 * <b>Command Line:</b>
 * run.bat com.vmware.SimpleClient
 * --url [webserviceurl] --username [username] --password [password]
 * </pre>
 */


public class SimpleClient extends ConnectedVimServiceBase
{

    private ManagedObjectReference propCollectorRef;

    /**
     * Uses the new RetrievePropertiesEx method to emulate the now deprecated
     * RetrieveProperties method
     *
     * @param listpfs
     * @return list of object content
     * @throws Exception
     */
    List<ObjectContent> retrievePropertiesAllObjects(List<PropertyFilterSpec> listpfs) throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg
    {

        RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

        List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();

        RetrieveResult rslts = vimPort.retrievePropertiesEx(propCollectorRef, listpfs, propObjectRetrieveOpts);
        if (rslts != null && rslts.getObjects() != null
                && !rslts.getObjects().isEmpty()) {
            listobjcontent.addAll(rslts.getObjects());
        }
        String token = null;
        if (rslts != null && rslts.getToken() != null) {
            token = rslts.getToken();
        }
        while (token != null && !token.isEmpty()) {
            rslts =
                    vimPort.continueRetrievePropertiesEx(propCollectorRef, token);
            token = null;
            if (rslts != null) {
                token = rslts.getToken();
                if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
                    listobjcontent.addAll(rslts.getObjects());
                }
            }
        }

        return listobjcontent;
    }

    TraversalSpec makeTraverselSpec(String name, String type, String path) {
        TraversalSpec traversalSpec = new TraversalSpec();
        traversalSpec.setName(name);
        traversalSpec.setType(type);
        traversalSpec.setPath(path);
        traversalSpec.setSkip(Boolean.FALSE);
        return traversalSpec;
    }

    private TraversalSpec makeTraverselSpec(String name, String type, String path, String selectionName) {
        TraversalSpec traversalSpec = makeTraverselSpec(name, type, path);
        SelectionSpec selectionSpec = new SelectionSpec();
        selectionSpec.setName(selectionName);
        traversalSpec.getSelectSet().add(selectionSpec);
        return traversalSpec;
    }

    private SelectionSpec makeSelectionSpec(String name) {
        SelectionSpec selectionSpec = new SelectionSpec();
        selectionSpec.setName(name);
        return selectionSpec;
    }

    private List<SelectionSpec> makeSelectionSpecs() {
        List<SelectionSpec> selectionSpecs = new ArrayList<SelectionSpec>();
        selectionSpecs.add(makeSelectionSpec("folderTraversalSpec"));
//        selectionSpecs.add(makeTraverselSpec("datacenterHostTraversalSpec", "Datacenter", "hostFolder", "folderTraversalSpec"));
        selectionSpecs.add(makeTraverselSpec("datacenterVmTraversalSpec", "Datacenter", "vmFolder", "folderTraversalSpec"));
//        selectionSpecs.add(makeTraverselSpec("computeResourceRpTraversalSpec", "ComputeResource", "resourcePool", "resourcePoolTraversalSpec"));
//        selectionSpecs.add(makeTraverselSpec("computeResourceHostTraversalSpec", "ComputeResource", "host"));
//        selectionSpecs.add(makeTraverselSpec("resourcePoolTraversalSpec", "ResourcePool", "resourcePool", "resourcePoolTraversalSpec"));
        return selectionSpecs;
    }

    RootFolder downloadObjects() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg
    {
        List<SelectionSpec> selectionSpecs = makeSelectionSpecs();
        TraversalSpec folderTraversalSpec = makeTraverselSpec("folderTraversalSpec", "Folder", "childEntity");
        folderTraversalSpec.getSelectSet().addAll(selectionSpecs);
        PropertySpec props = new PropertySpec();
        props.setAll(Boolean.FALSE);
        props.getPathSet().add("name");
        props.setType("ManagedEntity");
        List<PropertySpec> propspecary = new ArrayList<>();
        propspecary.add(props);

        PropertyFilterSpec propertyFilterSpec = new PropertyFilterSpec();
        propertyFilterSpec.getPropSet().addAll(propspecary);

        propertyFilterSpec.getObjectSet().add(new ObjectSpec());
        propertyFilterSpec.getObjectSet().get(0).setObj(rootRef);
        propertyFilterSpec.getObjectSet().get(0).setSkip(Boolean.FALSE);
        propertyFilterSpec.getObjectSet().get(0).getSelectSet().add(folderTraversalSpec);

        List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
        listpfs.add(propertyFilterSpec);
        List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);
        return buildObjectTree(listobjcont);
    }

    public ArrayList<VirtualMachine> getVms() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg, InvalidStateFaultMsg
    {
        refreshSession();
        RootFolder rootFolder = downloadObjects();
        return rootFolder.getDataCenter().VMs.children;
    }

    public String acquireTicket(VirtualMachine vm) throws RuntimeFaultFaultMsg, InvalidStateFaultMsg
    {
        refreshSession();
        return vm.acquireTicket(vimPort);
    }



    public RootFolder buildObjectTree(List<ObjectContent> listobjcont) {
        if (listobjcont == null) {
           return null;
        }

        RootFolder root = new RootFolder();

        for (int oci = 0; oci < listobjcont.size(); oci++) {
            ObjectContent objectContent = listobjcont.get(oci);
            ManagedObjectReference managedObjectReference = objectContent.getObj();
            switch(managedObjectReference.getValue()) {
                case "ha-folder-root":
                    root.inflateFromReference(objectContent);
                    break;
                case "ha-datacenter":
                    root.children.add(new DataCenter(objectContent));
                    break;
                case "ha-folder-vm":
                    root.getDataCenter().VMs = new Folder<>(objectContent);
                    break;
                default: {
                    if(managedObjectReference.getType().equals("VirtualMachine")) {
                        root.getDataCenter().VMs.children.add(new VirtualMachine(objectContent));
                    }
                }
            }
        }

        return root;
    }




    public void refreshSession() {
        connection.connect();
        propCollectorRef = serviceContent.getPropertyCollector();
    }

}
