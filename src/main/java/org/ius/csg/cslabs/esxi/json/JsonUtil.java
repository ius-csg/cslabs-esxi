package org.ius.csg.cslabs.esxi.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vmware.conrete.VirtualMachine;
import spark.ResponseTransformer;

public class JsonUtil {

    private static Gson gson;

    public static String toJson(Object object) {
        return getGson().toJson(object);
    }

    public static Gson getGson() {
        if(gson != null)
            return gson;
        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(VirtualMachine.class, new VirtualMachineSerializer());
        gson = gsonBuilder.create();
        return gson;
    }

    public static ResponseTransformer json() {
        return JsonUtil::toJson;
    }
}