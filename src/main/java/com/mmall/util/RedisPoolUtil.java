package com.mmall.util;

import com.mmall.common.RedisPool;
import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.Jedis;

/**
 * redisPoolUtil工具类
 *
 * @author chencong
 */
@Slf4j
public class RedisPoolUtil {

    /**
     * jedis set方法，通识设置值过期时间exTime,单位:秒<br>
     * 为后期session服务器共享，Redis存储用户session所准备
     *
     * @param key    key
     * @param value  value
     * @param exTime 过期时间,单位:秒
     * @return 执行成功则返回result 否则返回null
     */
    public static String setEx(String key, String value, int exTime) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.setex(key, exTime, value);
        } catch (Exception e) {
            log.error("set key:{} value{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * 对key所对应的值进行重置过期时间expire
     *
     * @param key    key
     * @param exTime 过期时间 单位:秒
     * @return 返回重置结果, 1:时间已经被重置，0:时间未被重置
     */
    public static Long expire(String key, int exTime) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.expire(key, exTime);
        } catch (Exception e) {
            log.error("expire key:{} error ", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;

    }

    /**
     * jedis set方法
     *
     * @param key   key
     * @param value value
     * @return 执行成功则返回result，否则返回null
     */
    public static String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{} value{} error", key, value, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * jedis get方法
     *
     * @param key key
     * @return 返回key对应的value 异常则返回null
     */
    public static String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("set key:{}error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        RedisPool.returnResource(jedis);
        return result;
    }

    /**
     * jedis 删除方法
     *
     * @param key key
     * @return 返回结果，异常返回null
     */
    public static Long del(String key) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = RedisPool.getJedis();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            RedisPool.returnBrokenResource(jedis);
            return result;
        }
        return result;
    }

    public static void main(String[] args) {
        Jedis jedis = RedisPool.getJedis();
        RedisPoolUtil.set("keyTest","keyValue");

        String  value = RedisPoolUtil.get("keyTest");

        RedisPoolUtil.setEx("keyEx","valueEx",60*10);

        RedisPoolUtil.expire("keyTest",60*20);

        RedisPoolUtil.del("keyTest");
    }
}
