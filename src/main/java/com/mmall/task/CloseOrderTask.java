package com.mmall.task;

import com.mmall.service.IOrderService;
import com.mmall.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

    /**
     * 第一个版本，无分布式锁。<br>
     * 如果没有Tomcat集群，这一个方法即可<br>
     * cron 代表每1分钟 每个1分钟整数倍<br>
     * 每1分钟执行一遍，关闭两个小时之前的未支付的订单。
     */
    @Scheduled(cron = "0 */1 * * * ?")
    public void closeOrderTaskV1() {
        log.info("关闭订单定时任务开始");
        int hour = PropertiesUtil.getIntegerProperty("close.order.task.time.hour", 2);
        iOrderService.closeOrder(hour);
        log.info("关闭订单定时任务结束");
    }

}
