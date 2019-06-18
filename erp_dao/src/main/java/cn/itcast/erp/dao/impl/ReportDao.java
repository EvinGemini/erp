package cn.itcast.erp.dao.impl;

import cn.itcast.erp.dao.IReportDao;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportDao extends HibernateDaoSupport implements IReportDao {
    @Override
    public List orderReport(Date startDate, Date endDate) {
        String hql = "select new Map(gt.name as name,sum(ol.money) as y) from Goodstype gt,Goods gs,Orderdetail ol,Orders o " +
                "where gs.goodstype = gt and ol.orders = o and ol.goodsuuid = gs.uuid and o.type = '2'";
        List<Date> list = new ArrayList<>();
        if (startDate != null) {
            hql += " and o.createtime>=? ";
            list.add(startDate);
        }
        if (endDate != null) {
            hql += " and o.createtime<=? ";
            list.add(endDate);
        }
        hql += "  group by gt.name";
        Date[] date = new Date[0];
        Date[] dates = list.toArray(date);
        return getHibernateTemplate().find(hql,dates);
    }

    @Override
    public List<Map<String, Object>> getSumMoney(int year) {
        String hql = "select new Map(month(o.createtime) || '月' as name,sum(ol.money) as y) from " +
                "Orderdetail ol,Orders o " +
                "where ol.orders=o and o.type='2' and year(o.createtime)=? group by month(o.createtime)";
        List<Map<String, Object>> list = (List<Map<String, Object>>) getHibernateTemplate().find(hql, year);
        return list;
    }
}
