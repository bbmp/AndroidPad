package com.robam.common.module;

import com.robam.common.mqtt.IProtocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ModulePubliclHelper {
    //保存接口和实现对象
    private static Map<Class<? extends IProtocol>, IProtocol> modulePublic = new HashMap<>();

    public static <T extends IProtocol> T getModulePublic(Class<T> clazz, String loadClass) {
        if (null == clazz)
            return null;
        IProtocol iProtocol = null;
        if (modulePublic.containsKey(clazz))
            iProtocol = modulePublic.get(clazz);
        else {
            try {
                Class implClass = Class.forName(loadClass);
                Method method = implClass.getMethod("getPublicApi");
                iProtocol = (IProtocol) method.invoke(null);
                modulePublic.put(clazz, iProtocol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (T) iProtocol;
    }

    public static void register(Class<? extends IProtocol> clazz, IProtocol iProtocol) {
        if (modulePublic.containsKey(clazz))
            return;

        modulePublic.put(clazz, iProtocol);
    }
}
