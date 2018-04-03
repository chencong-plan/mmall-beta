package com.mmall.task;

import com.mmall.common.Const;
import com.mmall.common.RedisShardedPool;
import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

/**
 * 定时关单,定时任务的方法
 *
 * @author chencong
 * @date 2018/3/26 21:31
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @PreDestroy
    public void delLock(){
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

    }

    /**
     * 第一个版本，无分布式锁。<br>
     * 如果没有Tomcat集群，这一个方法即可<br>
     * cron 代表每1分钟 每个1分钟整数倍<br>
     * 每1分钟执行一遍，关闭两个小时之前的未支付的订单。
     */
//    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务开始");
        int hour = PropertiesUtil.getIntegerProperty("close.order.task.time.hour", 2);
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

//    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV2() {
        log.info("关闭订单定时任务开始");
        long lockTime = PropertiesUtil.getLongProperty("lock.timeout", 5000);
        /*毫秒数 = 当前毫秒数+lockTime*/
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()) + lockTime);
        if (setnxResult != null && setnxResult.intValue() == 1) {
            /*返回值是1，代表设置成功，获取锁*/
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("没有获取分布式锁，{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
        log.info("关闭订单定时任务结束");
    }


    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV3() {
        log.info("关闭订单定时任务开始");
        long lockTime = PropertiesUtil.getLongProperty("lock.timeout", 5000);
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()) + lockTime);
        if (setnxResult != null && setnxResult.intValue() == 1) {
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            /*未获取到分布式锁，判断时间戳，是否可以重置并获取到分布式锁*/
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            if (lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)) {
                /*有获取分布式锁的权利*/
                /*
                 * 如果是单个Tomcat节点时，此时getSetResult和lockValueStr已经是相同
                 * 但是由于是Tomcat集群，这个getSetResult有可能已经被其他的Tomcat修改过
                 * 但是当前最近最新的值却还是getSetResult
                 * */
                String getSetResult = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()) + lockTime);
                /*
                 * 再次用当前时间戳getset
                 * 返回给的key的旧值-> 旧值判断，是否可以获取锁
                 * 当key没有旧值时，即key不存在时，返回nil(null) -> 获取锁
                 * 这里我们set了一个新的值，获取旧值
                 * */
                if (getSetResult == null || StringUtils.equals(lockValueStr, getSetResult)) {
                    /*真正获取到锁*/
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                } else {
                    log.info("没有获取到分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
                /*在set之前，该锁已经消失*/
            } else {
                log.info("没有获取到分布式锁：{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }

        }
        log.info("关闭订单定时任务结束");


    }

    /**
     * 关闭订单，防止死锁
     *
     * @param lockName 锁名称
     */
    private void closeOrder(String lockName) {
        /*设置当前key有效期，防止死锁*/
        RedisShardedPoolUtil.expire(lockName, 5);
        log.info("获取{}，ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        int hour = PropertiesUtil.getIntegerProperty("close.order.task.time.hour", 2);
//        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        log.info("释放{}，ThreadName:{}", Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, Thread.currentThread().getName());
        log.info("==============释放完毕===================");
    }


}
