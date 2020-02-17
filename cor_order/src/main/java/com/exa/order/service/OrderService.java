package com.exa.order.service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.exa.order.entity.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import util.IdWorker;

import com.exa.order.dao.OrderDao;
import com.exa.order.pojo.Order;

/**
 * 服务层
 * 
 * @author Administrator
 *
 */
@Service
public class OrderService {

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private IdWorker idWorker;

    @Autowired
    private RedisTemplate redisTemplate;
	/**
	 * 查询全部列表
	 * @return
	 */
	public List<Order> findAll() {
		return orderDao.findAll();
	}

	
	/**
	 * 条件查询+分页
	 * @param whereMap
	 * @param page
	 * @param size
	 * @return
	 */
	public Page<Order> findSearch(Map whereMap, int page, int size) {
		Specification<Order> specification = createSpecification(whereMap);
		PageRequest pageRequest =  PageRequest.of(page-1, size);
		return orderDao.findAll(specification, pageRequest);
	}

	
	/**
	 * 条件查询
	 * @param whereMap
	 * @return
	 */
	public List<Order> findSearch(Map whereMap) {
		Specification<Order> specification = createSpecification(whereMap);
		return orderDao.findAll(specification);
	}

	/**
	 * 根据ID查询实体
	 * @param id
	 * @return
	 */
	public Order findById(String id) {
		//先从缓存中查询当前对象
		Order order= (Order) redisTemplate.opsForValue().get("order_"+id);
		//如果缓存没用
		if(order==null){
			//从数据库查询
			order=orderDao.findById(id).get();
			/**
			 * 第三和第四个参数时过期时间数和过期时间设置
			 */
			redisTemplate.opsForValue().set("order"+id,order,1, TimeUnit.DAYS);
		}
		return order;
	}

	/**
	 * 增加
	 * @param order
	 */
	public void add(Order order) {
		order.setId( idWorker.nextId()+"" );
		order.setCreatetime(new Date());
		order.setPaymoney(0.0);
		order.setStatus(OrderStatus.UNPAID.getCode());
		orderDao.save(order);
	}

	/**
	 * 修改
	 * @param order
	 */
	public void update(Order order) {
		order.setUpdatetime(new Date());
		redisTemplate.delete("order_"+order.getId());
		orderDao.save(order);
	}

	/**
	 * 删除
	 * @param id
	 */
	public void deleteById(String id) {
		redisTemplate.delete("order_"+id);
		orderDao.deleteById(id);
	}

	/**
	 * 动态条件构建
	 * @param searchMap
	 * @return
	 */
	private Specification<Order> createSpecification(Map searchMap) {

		return new Specification<Order>() {

			@Override
			public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> predicateList = new ArrayList<Predicate>();
                // ID
                if (searchMap.get("id")!=null && !"".equals(searchMap.get("id"))) {
                	predicateList.add(cb.like(root.get("id").as(String.class), "%"+(String)searchMap.get("id")+"%"));
                }
                // 订单号ID
                if (searchMap.get("orderid")!=null && !"".equals(searchMap.get("orderid"))) {
                	predicateList.add(cb.like(root.get("orderid").as(String.class), "%"+(String)searchMap.get("orderid")+"%"));
                }
                // 用户ID
                if (searchMap.get("userid")!=null && !"".equals(searchMap.get("userid"))) {
                	predicateList.add(cb.like(root.get("userid").as(String.class), "%"+(String)searchMap.get("userid")+"%"));
                }
                // 支付类型
                if (searchMap.get("paytype")!=null && !"".equals(searchMap.get("paytype"))) {
                	predicateList.add(cb.like(root.get("paytype").as(String.class), "%"+(String)searchMap.get("paytype")+"%"));
                }
                // 支付平台
                if (searchMap.get("payplatform")!=null && !"".equals(searchMap.get("payplatform"))) {
                	predicateList.add(cb.like(root.get("payplatform").as(String.class), "%"+(String)searchMap.get("payplatform")+"%"));
                }
                // 订单状态
                if (searchMap.get("status")!=null && !"".equals(searchMap.get("status"))) {
                	predicateList.add(cb.like(root.get("status").as(String.class), "%"+(String)searchMap.get("status")+"%"));
                }
				
				return cb.and( predicateList.toArray(new Predicate[predicateList.size()]));

			}
		};

	}

}
