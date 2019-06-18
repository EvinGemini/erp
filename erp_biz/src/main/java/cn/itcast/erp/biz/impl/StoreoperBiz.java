package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IStoreoperBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Storeoper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库操作记录业务逻辑类
 * @author Administrator
 *
 */
public class StoreoperBiz extends BaseBiz<Storeoper> implements IStoreoperBiz {

	private IStoreoperDao storeoperDao;
	private IEmpDao empDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	
	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
		super.setBaseDao(this.storeoperDao);
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	@Override
	public List<Storeoper> getListByPage(Storeoper t1, Storeoper t2, Object param, int firstResult, int maxResults) {
		List<Storeoper> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long,String> empMap = new HashMap<>();
		Map<Long,String> goodsNameMap = new HashMap<>();
		Map<Long,String> storeNameMap = new HashMap<>();
		for (Storeoper storeoper : list) {
			storeoper.setEmpName(getEmpName(storeoper.getEmpuuid(),empMap,empDao));
			storeoper.setGoodsName(getGoodsName(storeoper.getGoodsuuid(),goodsNameMap,goodsDao));
			storeoper.setStoreName(getStoreName(storeoper.getStoreuuid(),storeNameMap,storeDao));
		}
		return list;
	}
}
