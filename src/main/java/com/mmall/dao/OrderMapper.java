package com.mmall.dao;

import com.mmall.pojo.Order;
import org.apache.ibatis.annotations.Param;
import org.aspectj.weaver.ast.Or;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo") Long orderNo);


    Order selectByOrderNo(Long orderNo);


    List<Order> selectByUserId(Integer userId);


    List<Order> selectAllOrder();

    /**
     * 二期新增定时关单
     *
     * @param status 状态
     * @param date   时间
     * @return 返回list集合
     */
    List<Order> selectOrderStatusByCreateTime(@Param("status") Integer status, @Param("date") String date);

    /**
     * 通过orderId关闭订单
     *
     * @param id id
     * @return 返回修改数量
     */
    int closeOrderByOrderId(Integer id);
}