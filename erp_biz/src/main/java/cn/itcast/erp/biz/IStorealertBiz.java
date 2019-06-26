package cn.itcast.erp.biz;

import cn.itcast.erp.dao.IBaseDao;
import cn.itcast.erp.entity.Storealert;

import java.util.List;

public interface IStorealertBiz extends IBaseDao<Storealert> {
    List<Storealert> getStorealertList();
}
