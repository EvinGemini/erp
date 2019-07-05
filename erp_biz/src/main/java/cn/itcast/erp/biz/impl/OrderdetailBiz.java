package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.dao.IOrderdetailDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.*;
import cn.itcast.erp.exception.ErpException;
import com.redsum.bos.ws.impl.IWaybillWs;

import java.util.Date;
import java.util.List;

/**
 * 订单明细业务逻辑类
 * @author Administrator
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	private IOrderdetailDao orderdetailDao;
	private IStoredetailDao storedetailDao;
	private IStoreoperDao storeoperDao;
	private ISupplierDao supplierDao;
	private IWaybillWs waybillWs;

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		super.setBaseDao(this.orderdetailDao);
	}

	@Override
	public void doInStore(Long uuid, Long storeUuidd, Long empUuid) {
	  //1.更新商品明细
		Orderdetail orderdetail = orderdetailDao.get(uuid);
		//检查商品是否已经入库
		if (!Orderdetail.STATE_NOT_IN.equals(orderdetail.getState())) {
			throw new ErpException("不能重复入库");
		}
		//更改状态
		orderdetail.setState(Orderdetail.STATE_IN);
		//设置仓库编号
		orderdetail.setStoreuuid(storeUuidd);
		//设置库管员
		orderdetail.setEnder(empUuid);
		//设置入库时间
		orderdetail.setEndtime(new Date());
		orderdetailDao.update(orderdetail);
      //2.修改库存表
		Storedetail storedetail = new Storedetail();
		storedetail.setStoreuuid(storeUuidd);
		storedetail.setGoodsuuid(orderdetail.getGoodsuuid());
		//获取指定库存
		List<Storedetail> storedetailList = storedetailDao.getList(storedetail, null, null);
		//判断库存中是否有该商品
		if (storedetailList != null && storedetailList.size() > 0) {
			//有该商品，修改商品数量
			storedetailList.get(0).setNum(storedetailList.get(0).getNum() + orderdetail.getNum());
		}else {
			//没有该商品则插入
			storedetail.setNum(orderdetail.getNum());
			storedetailDao.add(storedetail);
		}
	  //3.插入变更记录
		Storeoper storeoper = new Storeoper();
		storeoper.setType(Storeoper.TYPE_IN);
		storeoper.setStoreuuid(storeUuidd);
		storeoper.setEmpuuid(empUuid);
		storeoper.setGoodsuuid(orderdetail.getGoodsuuid());
		storeoper.setNum(orderdetail.getNum());
		storeoper.setOpertime(orderdetail.getEndtime());
		storeoperDao.add(storeoper);
	  //4.是否需要更新订单状态
		//获取订单
		Orders orders = orderdetail.getOrders();
		//设置参数查询订单下的所有订单明细
		Orderdetail countParam = new Orderdetail();
		countParam.setOrders(orders);
		countParam.setState(Orderdetail.STATE_NOT_IN);
		long count = orderdetailDao.getCount(countParam, null, null);
		if (count == 0) {
			//更新订单状态
			orders.setState(Orders.STATE_END);
			orders.setEnder(empUuid);
			orders.setEndtime(orderdetail.getEndtime());
		}
	}

	@Override
	public void doOutStore(Long uuid, Long storeUuidd, Long empUuid) {
		//1.更新商品明细
		Orderdetail orderdetail = orderdetailDao.get(uuid);
		//检查商品是否已经出库
		if (!Orderdetail.STATE_NOT_OUT.equals(orderdetail.getState())) {
			throw new ErpException("亲，该明细已经出库，不能重复出库");
		}
		//更改状态
		orderdetail.setState(Orderdetail.STATE_OUT);
		//设置仓库编号
		orderdetail.setStoreuuid(storeUuidd);
		//设置库管员
		orderdetail.setEnder(empUuid);
		//设置入库时间
		orderdetail.setEndtime(new Date());
		orderdetailDao.update(orderdetail);
		//2.修改库存表
		Storedetail storedetail = new Storedetail();
		storedetail.setStoreuuid(storeUuidd);
		storedetail.setGoodsuuid(orderdetail.getGoodsuuid());
		//获取指定库存
		List<Storedetail> storedetailList = storedetailDao.getList(storedetail, null, null);
		//判断库存中是否有该商品
		long num = -1;
		if (storedetailList != null && storedetailList.size() > 0) {
			//有该商品，修改商品数量
			storedetail = storedetailList.get(0);
			num = storedetail.getNum().longValue() - orderdetail.getNum().longValue();
		}
		if (num >= 0) {
			//库存充足
			storedetail.setNum(num);
		}else {
			//库存不足
			throw new ErpException("库存不足");
		}
		//3.插入变更记录
		Storeoper storeoper = new Storeoper();
		storeoper.setType(Storeoper.TYPE_OUT);
		storeoper.setStoreuuid(storeUuidd);
		storeoper.setEmpuuid(empUuid);
		storeoper.setGoodsuuid(orderdetail.getGoodsuuid());
		storeoper.setNum(orderdetail.getNum());
		storeoper.setOpertime(orderdetail.getEndtime());
		storeoperDao.add(storeoper);
		//4.是否需要更新订单状态
		//获取订单
		Orders orders = orderdetail.getOrders();
		//设置参数查询订单下的所有订单明细
		Orderdetail countParam = new Orderdetail();
		countParam.setOrders(orders);
		countParam.setState(Orderdetail.STATE_NOT_OUT);
		long count = orderdetailDao.getCount(countParam, null, null);
		if (count == 0) {
			//更新订单状态
			orders.setState(Orders.STATE_OUT);
			orders.setEnder(empUuid);
			orders.setEndtime(orderdetail.getEndtime());
			//自动下单
			//获取客户信息
			Supplier supplier = supplierDao.get(orders.getSupplieruuid());
			//调用ws
			Long waybillSn = waybillWs.addWaybill(1l, supplier.getAddress(), supplier.getContact(), supplier.getTele(), "--");
			//设置运单号
			orders.setWaybillsn(waybillSn);
		}
	}
}
