package cn.itcast.erp.biz.impl;

import java.util.List;
import java.util.Map;

import cn.itcast.erp.biz.IBaseBiz;
import cn.itcast.erp.dao.IBaseDao;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Store;

/**
 * 通用业务逻辑实现类
 * @author Administrator
 *
 * @param <T>
 */
public class BaseBiz<T> implements IBaseBiz<T> {

	/** 数据访问注入*/
	private IBaseDao<T> baseDao;

	public void setBaseDao(IBaseDao<T> baseDao) {
		this.baseDao = baseDao;
	}
	
	/**
	 * 条件查询
	 * @param t1
	 * @return
	 */
	public List<T> getList(T t1,T t2,Object param){
		return baseDao.getList(t1,t2,param);
	}
	
	/**
	 * 条件查询
	 * @param t1
	 * @return
	 */
	public List<T> getListByPage(T t1,T t2,Object param,int firstResult, int maxResults){
		return baseDao.getListByPage(t1,t2,param,firstResult, maxResults);
	}

	@Override
	public long getCount(T t1,T t2,Object param) {
		return baseDao.getCount(t1,t2,param);
	}

	@Override
	public void add(T t) {
		baseDao.add(t);
	}

	/**
	 * 删除
	 */
	public void delete(Long uuid){
		baseDao.delete(uuid);
	}
	
	/**
	 * 通过编号查询对象
	 * @param uuid
	 * @return
	 */
	public T get(Long uuid){
		return baseDao.get(uuid);
	}
	
	/**
	 * 通过字符串编号查询对象
	 * @param uuid
	 * @return
	 */
	public T get(String uuid){
		return baseDao.get(uuid);
	}
	
	/**
	 * 更新
	 */
	public void update(T t){
		baseDao.update(t);
	}

	public String getGoodsName(Long uuid, Map<Long,String> goodsNameMap, IGoodsDao goodsDao) {
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

	public String getStoreName(Long uuid, Map<Long,String> storeNameMap, IStoreDao storeDao) {
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

	public String getEmpName(Long uuid, Map<Long,String> empMap, IEmpDao empDao) {
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

}

