package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Store;
import cn.itcast.erp.entity.Storedetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	private IStoredetailDao storedetailDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	@Override
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult, int maxResults) {
		List<Storedetail> storedetails = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long,String> goodsNameMap = new HashMap<>();
		Map<Long,String> storeNameMap = new HashMap<>();
		for (Storedetail storedetail : storedetails) {
			storedetail.setGoodsName(getGoodsName(storedetail.getGoodsuuid(),goodsNameMap));
			storedetail.setStoreName(getStoreName(storedetail.getStoreuuid(),storeNameMap));
		}
		return storedetails;
	}

	private String getGoodsName(Long uuid,Map<Long,String> goodsNameMap) {
		if (uuid == null) {
			return null;
		}
		String goodsName = goodsNameMap.get(uuid);
		if (null == goodsName) {
			Goods goods = goodsDao.get(uuid);
			goodsName = goods.getName();
			goodsNameMap.put(uuid,goodsName);
		}
		return goodsName;
	}

	private String getStoreName(Long uuid,Map<Long,String> storeNameMap) {
		if (uuid == null) {
			return null;
		}
		String storeName = storeNameMap.get(uuid);
		if (null == storeName) {
			Store store = storeDao.get(uuid);
			storeName = store.getName();
			storeNameMap.put(uuid,storeName);
		}
		return storeName;
	}
}
