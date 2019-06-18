package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	
	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	@Override
	public void add(Orders orders) {
		//设置订单状态,开始下单都是未审核
		orders.setState(Orders.STATE_CREATE);
		//设置下单时间
		orders.setCreatetime(new Date());
		//设置订单类型
//		orders.setType(Orders.TYPE_IN);
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

	@Override
	public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult, int maxResults) {
		List<Orders> listByPage = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long,String> empMap = new HashMap<>();
		Map<Long,String> supplierMap = new HashMap<>();
		for (Orders orders : listByPage) {
			//设置下单员姓名
			orders.setCreaterName(getEmpName(orders.getCreater(),empMap));
			//设置审核姓名
			orders.setCheckerName(getEmpName(orders.getChecker(),empMap));
			//设置采购员姓名
			orders.setStarterName(getEmpName(orders.getStarter(),empMap));
			//设置库管员姓名
			orders.setEnderName(getEmpName(orders.getEnder(),empMap));
			//设置供应商姓名
			orders.setSupplierName(getSupplierName(orders.getSupplieruuid(),supplierMap));
		}
		return listByPage;
	}

	/**
	 * 订单审核
	 * @param uuid
	 * @param empUuid
	 */
	public void doCheck(Long uuid, Long empUuid) {
		//获取订单
		Orders orders = ordersDao.get(uuid);
		//设置订单状态
		if (!Orders.STATE_CREATE.equals(orders.getState())) {
			throw new ErpException("对不起，您的订单已审核！");
		}
		orders.setState(Orders.STATE_CHECK);
		//设置审核时间
		orders.setChecktime(new Date());
		//设置审核人
		orders.setChecker(empUuid);
		ordersDao.update(orders);
	}

	/**
	 * 订单确认
	 * @param uuid
	 * @param empUuid
	 */
	public void doStart(Long uuid, Long empUuid) {
		//获取订单
		Orders orders = ordersDao.get(uuid);
		//设置订单状态
		if (!Orders.STATE_CHECK.equals(orders.getState())) {
			throw new ErpException("对不起，您的订单已经确认过了！");
		}
		orders.setState(Orders.STATE_START);
		//设置采购时间
		orders.setStarttime(new Date());
		//设置采购员
		orders.setStarter(empUuid);
		ordersDao.update(orders);
	}


	private String getEmpName(Long uuid,Map<Long,String> empMap) {
		//如果员工编号为null，则返回null
		if (null == uuid) {
			return null;
		}
		//通过员工编号从缓存中获取empName
		String empName = empMap.get(uuid);
		if (null == empName) {
			Emp emp = empDao.get(uuid);
			empMap.put(uuid,emp.getName());
			empName = emp.getName();
		}
		return empName;
	}

	private String getSupplierName(Long uuid,Map<Long,String> supplierMap) {
		//如果供应商id为null，则返回null
		if (null == uuid) {
			return null;
		}
		//通过供应商id从缓存中获取supplierName
		String supplierName = supplierMap.get(uuid);
		if (null == supplierName) {
			Supplier supplier = supplierDao.get(uuid);
			supplierMap.put(uuid,supplier.getName());
			supplierName = supplier.getName();
		}
		return supplierName;
	}
}
