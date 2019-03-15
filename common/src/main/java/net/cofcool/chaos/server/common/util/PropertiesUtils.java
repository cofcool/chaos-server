package net.cofcool.chaos.server.common.util;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import org.springframework.core.env.PropertyResolver;

/**
 * {@link Properties} 工具类
 *
 * @author CofCool
 */
public class PropertiesUtils {

    private static final Map<String, Properties> PROPERTIES_MAP = new HashMap<>(4);

    private static Properties load(String fileName) {
        try {
            Properties properties = new Properties();
            properties.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName));

            return properties;
        } catch (IOException ignore) {
            throw new NullPointerException();
        }
    }

    /**
     * 读取配置文件的内容
     * @return Properties
     */
    public static Properties getConfigProperties(String fileName) {
        return PROPERTIES_MAP.computeIfAbsent(fileName, PropertiesUtils::load);
    }

    /**
     * 从配置文件读取对应的值, 不存在时返回"",
     * 调用本方法前需调用 {@link #getConfigProperties(String)} 或 {@link #getConfigProperty(String, String)}
     */
    public static String getConfigProperty(String key) {
        for (Entry<String, Properties> e : PROPERTIES_MAP.entrySet()) {
            String v = e.getValue().getProperty(key);
            if (!StringUtils.isNullOrEmpty(v)) {
                return v;
            }
        }

        return StringUtils.EMPTY_STRING;
    }

    /**
     * 从properties文件读取对应的值
     *
     * @throws NullPointerException 如果文件不存在
     */
    public static String getConfigProperty(String fileName, String key) {
        return getConfigProperties(fileName).getProperty(key);
    }

    /**
     *
     * @param properties properties
     * @param rootKey key前缀
     * @param clazz T的class实例
     * @param <T> 对象类型, 属性需为"String"类型
     *
     * @return T
     *
     * @throws IllegalArgumentException 如果解析失败
     */
    public static <T> T parseProperties(PropertyResolver properties, String rootKey, Class<T> clazz) {
        try {
            T instance = clazz.getConstructor().newInstance();
            parseProperties(instance, new SResolver(properties), rootKey);

            return instance;
        } catch (Exception ignore) {
            throw new IllegalArgumentException();
        }
    }

    /**
     *
     * @param properties properties
     * @param rootKey key前缀
     * @param clazz T的class实例
     * @param <T> 对象类型, 属性需为"String"类型
     *
     * @return T
     *
     * @throws IllegalArgumentException 如果解析失败
     */
    public static <T> T parseProperties(Properties properties, String rootKey, Class<T> clazz) {
        try {
            T instance = clazz.getConstructor().newInstance();
            parseProperties(instance, new PResolver(properties), rootKey);

            return instance;
        } catch (Exception ignore) {
            throw new IllegalArgumentException();
        }
    }

    private static void parseProperties(Object instance, Resolver properties, String rootKey)
        throws IllegalAccessException, InvocationTargetException {
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(instance.getClass());
        for (PropertyDescriptor pd : pds) {
            if (pd.getWriteMethod() == null || pd.getWriteMethod() == null) {
                continue;
            }

            String fieldKey = rootKey + "." + BeanUtils.convertWordsWithDashToCamelCase(pd.getName());
            if (pd.getPropertyType().equals(String.class)) {
                pd.getWriteMethod().invoke(instance, properties.getProperty(fieldKey, ""));
            } else {
                parseProperties(pd.getReadMethod().invoke(instance), properties, fieldKey);
            }
        }
    }

    interface Resolver {

        String getProperty(String key);

        String getProperty(String key, String defaultValue);

    }

    static class PResolver implements Resolver {

        private Properties properties;

        public PResolver(Properties properties) {
            this.properties = properties;
        }

        @Override
        public String getProperty(String key) {
            return this.properties.getProperty(key);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return this.properties.getProperty(key, defaultValue);
        }
    }

    static class SResolver implements Resolver {

        private PropertyResolver properties;

        public SResolver(PropertyResolver properties) {
            this.properties = properties;
        }

        @Override
        public String getProperty(String key) {
            return this.properties.getProperty(key);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return this.properties.getProperty(key, defaultValue);
        }
    }

}
