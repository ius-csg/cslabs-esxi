package com.vmware.conrete;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;

import java.util.List;

public class SimplifiedManagedObject
{

    public SimplifiedManagedObject() {

    }

    public SimplifiedManagedObject(ObjectContent objectContent) {
        this.inflateFromReference(objectContent);
    }

    public String name = "";

    public String objectType = "";

    public String reference() {
        if(managedObjectReference == null)
            return "";
        return managedObjectReference.getValue();
    }
    public ManagedObjectReference managedObjectReference;

    public ObjectContent objectContent;

    public void inflateFromReference(ObjectContent objectContent)
    {
        this.objectContent = objectContent;
        managedObjectReference = objectContent.getObj();
        objectType = managedObjectReference.getType();

        List<DynamicProperty> listdp = objectContent.getPropSet();
        if (listdp != null) {
            if(listdp.size() == 1) {
                DynamicProperty dynamicProperty = listdp.get(0);
                if (dynamicProperty != null && dynamicProperty.getName().equals("name")) {
                    if (!dynamicProperty.getVal().getClass().isArray()) {
                        name = (String)dynamicProperty.getVal();
                    }
                }
            }

        }
    }


}
