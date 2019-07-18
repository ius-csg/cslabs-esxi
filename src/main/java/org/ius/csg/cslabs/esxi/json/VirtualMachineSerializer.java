package org.ius.csg.cslabs.esxi.json;
import java.lang.reflect.Type;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.vmware.conrete.VirtualMachine;

public  class VirtualMachineSerializer implements JsonSerializer
{
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context)
    {
        if(src instanceof VirtualMachine) {
            VirtualMachine vm = (VirtualMachine)src;
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("name", vm.name);
            jsonObject.addProperty("powerState", vm.powerState.value());
            return jsonObject;
        }
        return null;
    }
}
