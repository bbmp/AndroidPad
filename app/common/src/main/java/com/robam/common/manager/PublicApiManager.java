package com.robam.common.manager;

import java.util.HashMap;
import java.util.Map;

public class PublicApiManager {
    private static Map<Class<? extends IModuleApi>, IModuleApi> moduleApi = new HashMap<>();

    public static <T extends IModuleApi> T getModuleApi(Class<T> clazz) {
        if (null == clazz)
            return null;
        IModuleApi api = null;
        if (moduleApi.containsKey(clazz))
            api = moduleApi.get(clazz);

        return (T) api;
    }

    public static void register(Class<? extends IModuleApi> clazz, IModuleApi api) {
        if (moduleApi.containsKey(clazz))
            return;

        moduleApi.put(clazz, api);
    }
}
