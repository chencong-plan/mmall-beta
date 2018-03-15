package com.mmall.common;

import com.mmall.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Shared redis 分片连接池
 *
 * @author chencong
 * @date 2018/3/15 21:47
 */
public class RedisShardedPool {

    /**
     * Shared jedis 连接池
     */
    private static ShardedJedisPool pool;

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
     * 获取RedisIP 节点1
     */
    private static String redis_1_Ip = PropertiesUtil.getProperty("redis_1.ip");

    /**
     * 获取RedisPort 节点1
     */
    private static Integer redis_1_Port = PropertiesUtil.getIntegerProperty("redis_1.port");

    /**
     * 获取RedisIP 节点2
     */
    private static String redis_2_Ip = PropertiesUtil.getProperty("redis_2.ip");

    /**
     * 获取RedisPort 节点2
     */
    private static Integer redis_2_Port = PropertiesUtil.getIntegerProperty("redis_2.port");


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

        /*pool = new JedisPool(config, redisIp, redisPort, 1000 * 2);*/
        /*jedisShardInfo 节点*/
        JedisShardInfo info_1 = new JedisShardInfo(redis_1_Ip,redis_1_Port,1000*2);
        /*如果redis存在密码的话*/
        /*info_1.setPassword("")*/;
        JedisShardInfo info_2 = new JedisShardInfo(redis_2_Ip,redis_2_Port,1000*2);

        /*因为这里只做两个节点，因此初始化list结合大小为2，不指定也是可以的*/
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);

        jedisShardInfoList.add(info_1);
        jedisShardInfoList.add(info_2);

        /*初始化pool*/
        /*分片策略 ：默认Hashing.MURMUR_HASH 对应一直性算法*/
        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
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
    public static ShardedJedis getJedis() {
        return pool.getResource();
    }


    /**
     * 放回jedis
     *
     * @param jedis jedis
     */
    public static void returnBrokenResource(ShardedJedis jedis) {
        pool.returnBrokenResource(jedis);
    }

    /**
     * 放回jedis
     *
     * @param jedis jedis
     */
    public static void returnResource(ShardedJedis jedis) {
        pool.returnResource(jedis);
    }


    public static void main(String[] args) {
        ShardedJedis jedis = pool.getResource();
        for (int i = 0; i < 20; i++) {
            jedis.set("key"+i,"value"+i);
        }
        returnResource(jedis);

        System.out.println("program is end");
    }


}
