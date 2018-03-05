package com.mmall.common;

import redis.clients.jedis.JedisPool;

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
    private static Integer maxTotal = 0;

    /**
     * 在jedisPool中最大idle状态(空闲)
     */
    private static Integer maxIdle = 0;

    /**
     * 在jedisPool当中最小的idle状态(空闲)
     */
    private static Integer minIdle = 0;

    /**
     * 在Borrow一个jedis实例的时候是否进行验证操作。
     * 如果赋值为true，则拿到的jedis是可用的
     */
    private static Boolean testOnBorrow = true;


    /**
     * 在return一个jedis实例时候，是否要进行测试，
     * 如果赋值为true时，则放回的jedis实例为可用的
     */
    private static Boolean testOnReturn = true;


}
