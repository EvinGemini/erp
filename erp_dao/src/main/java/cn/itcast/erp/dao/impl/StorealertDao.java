package cn.itcast.erp.dao.impl;

import cn.itcast.erp.dao.IStorealertDao;
import cn.itcast.erp.entity.Storealert;

import java.util.List;

public class StorealertDao extends BaseDao<Storealert> implements IStorealertDao {

    @Override
    public List<Storealert> getStorealertList() {
        return (List<Storealert>) getHibernateTemplate().find("from Storealert where storenum<outnum");
    }
}
