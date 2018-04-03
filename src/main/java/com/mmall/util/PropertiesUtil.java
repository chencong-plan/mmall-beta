package com.mmall.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

/**
 * 读取properties配置文件的工具类<br>
 * 2018年3月10日21:44:34 新增获取Boolean int类型的方法及其重载方法
 *
 * @author chencong
 */
public class PropertiesUtil {

    /**
     * 读取日志
     */
    private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

    /**
     * properties属性
     */
    private static Properties props;

    /*
     * 静态代码块读取制定配置文件
     */
    static {
        /*
         * 应为使用了maven环境隔离，编译之后都存在于src下面，因此这里使用指明读取的是哪一个配置文件就行了<br>
         * 切记不要使用这种：src/resources.dev/mmall.properties 会出现异常
         */
        String fileName = "mmall.properties";
        props = new Properties();
        try {
            props.load(
                    new InputStreamReader(
                            PropertiesUtil.class
                                    .getClassLoader().
                                    getResourceAsStream(fileName), "UTF-8"));
        } catch (IOException e) {
            logger.error("配置文件读取异常", e);
        }
    }

    /**
     * 获取配置文件当中key所对应值
     *
     * @param key key
     * @return 返回key所对应的值，存在则返回，不存在则返回null
     */
    public static String getProperty(String key) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return value.trim();
    }

    /**
     * 获取配置文件当中key所对应的值，同时给予默认值。<br>
     * 当key所对应的值不存在时，返回参数当中给予的默认值
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 返回key对应值，不存在或者为null时返回默认值defaultValue
     */
    public static String getProperty(String key, String defaultValue) {
        String value = props.getProperty(key.trim());
        if (StringUtils.isBlank(value)) {
            value = defaultValue;
        }
        return value.trim();
    }

    /**
     * 获取配置文件当中key所对应值，存在则进行数字转换。将其返回
     *
     * @param key key
     * @return 返回key对应的值，不存在或发生NumberFormatException则返回null
     */
    public static Integer getIntegerProperty(String key) {
        String value = props.getProperty(key.trim());
        Integer result;
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info("参数转换异常：" + e.getMessage());
            return null;
        }
        return result;
    }

    /**
     * 获取配置文件当中key所对应值，存在则进行数字转换。将其返回
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 返回key对应的值，不存在时或发生NumberFormatException则返回defaultValue
     */
    public static Integer getIntegerProperty(String key, Integer defaultValue) {
        String value = props.getProperty(key);
        Integer result;
        if (StringUtils.isBlank(value)) {
            result = defaultValue;
            return result;
        }
        try {
            result = Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.info("参数转换异常：" + e.getMessage());
            result = defaultValue;
        }
        return result;
    }

    /**
     * 获取配置文件当中key所对应值<br>
     * 如果其值为true则返回true，false或其他值则返回false,为空时返回null
     *
     * @param key key
     * @return key对应的值，值为true时返回true，false或其他值则返回false
     */
    public static Boolean getBooleanProperty(String key) {
        String value = props.getProperty(key.trim()).trim();
        Boolean result;
        if (StringUtils.isBlank(value)) {
            return null;
        }
        result = "true".equals(value);
        return result;
    }

    /**
     * 获取配置文件当中key所对应值<br>
     * 如果其值为true则返回true，false或其他值则返回false，为null时返回defaultValue
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return key对应的值，值为true时返回true，false或其他值则返回false
     */
    public static Boolean getBooleanProperty(String key, Boolean defaultValue) {
        String value = props.getProperty(key.trim()).trim();
        Boolean result;
        if (StringUtils.isBlank(value)) {
            result = defaultValue;
            return result;
        }
        result = "true".equals(value);
        return result;
    }


    /**
     * 获取配置文件当中key所对应的值<br>
     * 如果其值存在且key不null则返回，值不存在返回defaultValue
     *
     * @param key          key
     * @param defaultValue 默认值
     * @return 返回key所对应的value或者defaultValue
     */
    public static Long getLongProperty(String key, long defaultValue) {
        String value = props.getProperty(key.trim()).trim();
        Long result = null;
        if (StringUtils.isBlank(value)) {
            result = defaultValue;
            return result;
        }
        try {
            result = Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.info("key:{} value:{}异常，{}", key, value, e);
        }
        return result;
    }
}
