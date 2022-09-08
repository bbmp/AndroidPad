package com.robam.common.module;

import com.robam.common.mqtt.IProtocol;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ModuleProtocolHelper {
    private static Map<Class<? extends IProtocol>, IProtocol> moduleProtocol = new HashMap<>();

    public static <T extends IProtocol> T getModuleProtocol(Class<T> clazz, String loadClass) {
        if (null == clazz)
            return null;
        IProtocol iProtocol = null;
        if (moduleProtocol.containsKey(clazz))
            iProtocol = moduleProtocol.get(clazz);
        else {
            try {
                Class testClass = Class.forName(loadClass);
                Method saddMethod1 = testClass.getMethod("getPublicApi");
                iProtocol = (IProtocol) saddMethod1.invoke(null, null);
                moduleProtocol.put(clazz, iProtocol);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return (T) iProtocol;
    }

    public static void register(Class<? extends IProtocol> clazz, IProtocol iProtocol) {
        if (moduleProtocol.containsKey(clazz))
            return;

        moduleProtocol.put(clazz, iProtocol);
    }
}
