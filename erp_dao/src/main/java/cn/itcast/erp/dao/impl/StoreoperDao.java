package cn.itcast.erp.dao.impl;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import cn.itcast.erp.dao.IStoreoperDao;
import cn.itcast.erp.entity.Storeoper;

import java.util.Calendar;

/**
 * 仓库操作记录数据访问类
 * @author Administrator
 *
 */
public class StoreoperDao extends BaseDao<Storeoper> implements IStoreoperDao {

	/**
	 * 构建查询条件
	 * @param storeoper1
	 * @param storeoper2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Storeoper storeoper1,Storeoper storeoper2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Storeoper.class);
		if(storeoper1!=null){
			//类型
			if(null != storeoper1.getType() && storeoper1.getType().trim().length()>0){
				dc.add(Restrictions.eq("type", storeoper1.getType()));
			}
			//员工id
			if (null != storeoper1.getEmpuuid()) {
				dc.add(Restrictions.eq("empuuid",storeoper1.getEmpuuid()));
			}
			//商品id
			if (null != storeoper1.getGoodsuuid()) {
				dc.add(Restrictions.eq("goodsuuid",storeoper1.getGoodsuuid()));
			}
			//仓库id
			if (null != storeoper1.getStoreuuid()) {
				dc.add(Restrictions.eq("storeuuid",storeoper1.getStoreuuid()));
			}
			//操作时间
			if (null != storeoper1.getOpertime()) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(storeoper1.getOpertime());
				calendar.set(Calendar.HOUR,0);
				calendar.set(Calendar.MINUTE,0);
				calendar.set(Calendar.SECOND,0);
				calendar.set(Calendar.MILLISECOND,0);
				dc.add(Restrictions.ge("opertime",calendar.getTime()));
			}
		}
		if (storeoper2 != null) {
			if (storeoper2.getOpertime() != null) {
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(storeoper2.getOpertime());
				calendar.set(Calendar.HOUR,0);
				calendar.set(Calendar.MINUTE,0);
				calendar.set(Calendar.SECOND,0);
				calendar.set(Calendar.MILLISECOND,0);
				dc.add(Restrictions.le("opertime",calendar.getTime()));
			}
		}
		return dc;
	}

}
