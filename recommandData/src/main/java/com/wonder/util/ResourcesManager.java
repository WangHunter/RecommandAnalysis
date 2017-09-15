package com.wonder.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


public class ResourcesManager {

    private final static Properties PROP;
    private final static Map<String, String> RESOURCES_MAP = new HashMap<String, String>();

    static {
        PROP = new Properties();
        try {
            PROP.load(ResourcesManager.class.getResourceAsStream("/client.properties"));
            for (Map.Entry entry : PROP.entrySet()) {
                RESOURCES_MAP.put(entry.getKey().toString(), entry.getValue().toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置属性
     *
     * @param key
     * @param value
     */
    public static void setProp(String key, String value) {
        RESOURCES_MAP.put(key, value);
    }

    /**
     * 根据key值获取相应的value,可以自己设置默认值
     * @param key
     * @param defaultValue
     * @return
     */
    public static String getProp(String key, String defaultValue) {
        if (!RESOURCES_MAP.containsKey(key)) {
            if (null != defaultValue) {
                return defaultValue;
            } else {
                return "";
            }
        } else {
            return RESOURCES_MAP.get(key);
        }
    }

    /**
     * 根据key值获取相应的value，默认为空串
     * @param key
     * @return
     */
    public static String getProp(String key) {
        return getProp(key,"");
    }

}
