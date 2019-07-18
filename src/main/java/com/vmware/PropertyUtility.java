package com.vmware;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.ObjectContent;

import java.util.List;
import java.util.Optional;


public class PropertyUtility
{
    public static <T> T getProperty(Class<T> genericClass, ObjectContent objectContent, String name, T defaultValue) {
        List<DynamicProperty> propSet = objectContent.getPropSet();
        if (propSet == null)
            return defaultValue;

        Optional<DynamicProperty> dynamicProperty =  propSet.stream().filter(prop -> prop != null && prop.getName().equals(name)).findFirst();

        if(!dynamicProperty.isPresent())
            return defaultValue;

        Object value = dynamicProperty.get().getVal();
        if(genericClass.isInstance(value))
            return (T) value;

        return defaultValue;
    }

    public static void assign(AssignFunction assignFunction, ObjectContent objectContent) {
        List<DynamicProperty> listdp = objectContent.getPropSet();
        if (listdp == null)
            return;
        for(int i = 0; i < listdp.size(); i++) {
            DynamicProperty dynamicProperty = listdp.get(i);
            if (dynamicProperty == null)
                continue;
            assignFunction.assign(dynamicProperty.getName(), dynamicProperty);
        }

    }
}
