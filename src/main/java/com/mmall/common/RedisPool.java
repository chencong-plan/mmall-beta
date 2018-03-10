package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Jedis连接Redis的连接池
 *
 * @author : ChenCong
 * @date : Created in 17:22 2018/3/2
 */
public class RedisPool {

    /**
     * jedis 连接池
     */
    private static JedisPool pool;

    /**
     * 最大连接数
     */
    private static Integer maxTotal = PropertiesUtil.getIntegerProperty("redis.max.total", 20);

    /**
     * 在jedisPool中最大idle状态(空闲)
     */
    private static Integer maxIdle = PropertiesUtil.getIntegerProperty("redis.max.idle", 10);

    /**
     * 在jedisPool当中最小的idle状态(空闲)
     */
    private static Integer minIdle = PropertiesUtil.getIntegerProperty("redis.min.idle", 2);

    /**
     * 在Borrow一个jedis实例的时候是否进行验证操作。
     * 如果赋值为true，则拿到的jedis是可用的
     */
    private static Boolean testOnBorrow = PropertiesUtil.getBooleanProperty("redis.test.borrow", true);


    /**
     * 在return一个jedis实例时候，是否要进行测试，
     * 如果赋值为true时，则放回的jedis实例为可用的
     */
    private static Boolean testOnReturn = PropertiesUtil.getBooleanProperty("redis.test.return", true);
    /**
     * 获取RedisIP
     */
    private static String redisIp = PropertiesUtil.getProperty("redis.ip");

    /**
     * 获取RedisPort
     */
    private static Integer redisPort = PropertiesUtil.getIntegerProperty("redis.port");

    /**
     * 初始化JedisPoolConfig连接池
     */
    private static void initPool() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        /*
         * 连接耗尽时是否阻塞，false则会抛出异常，true阻塞直到超时，默认为true
         */
        config.setBlockWhenExhausted(true);

        pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);
    }

    /*
     * 初始化Jedis连接池
     */
    static {
        initPool();
    }

    /**
     * 获取jedis连接
     *
     * @return jedis
     */
    public static Jedis getJedis() {
        return pool.getResource();
    }


    /**
     * 放回jedis
     *
     * @param jedis jedis
     */
    public static void returnBrokenResource(Jedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    /**
     * 放回jedis
     *
     * @param jedis jedis
     */
    public static void returnResource(Jedis jedis) {
        pool.returnResource(jedis);
    }

    public static void main(String[] args) {
        Jedis jedis = pool.getResource();
        jedis.set("chencongKey","chencongValue");
        returnResource(jedis);

        /*获取值*/
        System.out.println(jedis.get("chencongKey"));

        /*
         * 临时调用，销毁连接池中所有连接
         */
        pool.destroy();

        System.out.println("program is end");
    }


}
