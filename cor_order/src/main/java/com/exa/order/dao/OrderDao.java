package com.exa.order.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.exa.order.pojo.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 * 数据访问接口
 * @author Administrator
 *
 */
public interface OrderDao extends JpaRepository<Order,String>,JpaSpecificationExecutor<Order>{

    /*
    * 更新支付金额(退款操作)
    * */
    @Modifying
    @Query(value = "UPDATE cor_order SET paymoney = paymoney + ? WHERE orderid = ?", nativeQuery = true)
    void updatePaymoney(Double paymoney, String id);

    /*
     * (支付成功操作)(允许退款)
     * */
    @Modifying
    @Query(value = "UPDATE cor_order SET paymoney = ? , status = ? ,paytime = ? WHERE orderid = ?", nativeQuery = true)
    void updateSuccess(Double paymoney, String status,String paytime,String id);

    /*
     * (支付成功操作)(不允许退款)
     * */
    @Modifying
    @Query(value = "UPDATE cor_order SET paymoney = ? , status = ? ,paytime = ? ,endtime = ? WHERE orderid = ?", nativeQuery = true)
    void updateFinished(Double paymoney, String status,String paytime,String endtime,String id);

	
}
