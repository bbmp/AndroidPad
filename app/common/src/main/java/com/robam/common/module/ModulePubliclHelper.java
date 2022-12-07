package com.robam.common.module;

import com.robam.common.mqtt.IProtocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ModulePubliclHelper {
    //保存接口和实现对象
    private static Map<Class<? extends IPublicApi>, IPublicApi> modulePublic = new HashMap<>();

    public static <T extends IPublicApi> T getModulePublic(Class<T> clazz, String loadClass) {
        if (null == clazz)
            return null;
        IPublicApi iPublicApi = null;
        if (modulePublic.containsKey(clazz))
            iPublicApi = modulePublic.get(clazz);
        else {
            try {
                Class implClass = Class.forName(loadClass);
                Method method = implClass.getMethod("getPublicApi");
                iPublicApi = (IPublicApi) method.invoke(null);
                modulePublic.put(clazz, iPublicApi);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (T) iPublicApi;
    }

    public static void register(Class<? extends IPublicApi> clazz, IPublicApi iPublicApi) {
        if (modulePublic.containsKey(clazz))
            return;

        modulePublic.put(clazz, iPublicApi);
    }
}
