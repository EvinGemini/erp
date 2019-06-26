package cn.itcast.erp.dao;

import cn.itcast.erp.entity.Storealert;

import java.util.List;

public interface IStorealertDao extends IBaseDao<Storealert> {
    List<Storealert> getStorealertList();
}
