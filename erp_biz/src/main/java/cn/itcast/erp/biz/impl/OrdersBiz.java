package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;

import java.util.Date;

/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	
	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}

	@Override
	public void add(Orders orders) {
		//设置订单状态,开始下单都是未审核
		orders.setState(Orders.STATE_CREATE);
		//设置下单时间
		orders.setCreatetime(new Date());
		//设置订单类型
		orders.setType(Orders.TYPE_IN);
		double total = 0;
		//设置订单明细与订单的关联与合计金额
		for (Orderdetail orderdetail : orders.getOrderdetails()) {
			total += orderdetail.getMoney();
			//设置订单明细状态
			orderdetail.setState(Orderdetail.STATE_NOT_IN);
			//设置关联关系
			orderdetail.setOrders(orders);
		}
		//设置订单的合计金额
		orders.setTotalmoney(total);
		//保存订单
		ordersDao.add(orders);
	}
}
