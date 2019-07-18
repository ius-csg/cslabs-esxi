package com.vmware.conrete;

import com.vmware.AssignFunction;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ObjectContent;

import java.util.List;
import static com.vmware.PropertyUtility.*;

public class SimplifiedManagedObject
{

    public SimplifiedManagedObject()
    {

    }

    public SimplifiedManagedObject(ObjectContent objectContent)
    {
        this.inflateFromReference(objectContent);
    }

    public String name = "";

    public String objectType = "";

    public String reference()
    {
        if (managedObjectReference == null)
            return "";
        return managedObjectReference.getValue();
    }

    public ManagedObjectReference managedObjectReference;

    public ObjectContent objectContent;


    protected void inflateFromReference(ObjectContent objectContent, AssignFunction assignFunction) {
        this.objectContent = objectContent;
        managedObjectReference = objectContent.getObj();
        objectType = managedObjectReference.getType();

        assign((name, property) -> {
            if(name.equals("name"))
                this.name = (String)property.getVal();
            if(assignFunction != null)
                assignFunction.assign(name, property);
        }, objectContent);
    }

    public void inflateFromReference(ObjectContent objectContent)
    {
        inflateFromReference(objectContent, null);
    }


}
