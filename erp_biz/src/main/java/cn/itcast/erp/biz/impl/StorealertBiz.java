package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IStorealertBiz;
import cn.itcast.erp.dao.IStorealertDao;
import cn.itcast.erp.entity.Storealert;

import java.util.List;

public class StorealertBiz extends BaseBiz<Storealert> implements IStorealertBiz {
    private IStorealertDao storealertDao;

    public void setStorealertDao(IStorealertDao storealertDao) {
        this.storealertDao = storealertDao;
    }

    @Override
    public List<Storealert> getStorealertList() {
        return storealertDao.getStorealertList();
    }
}
