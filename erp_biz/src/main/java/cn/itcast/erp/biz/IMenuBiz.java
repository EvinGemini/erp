package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Menu;
import sun.rmi.runtime.Log;

import java.util.List;

/**
 * 菜单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IMenuBiz extends IBaseBiz<Menu>{
    List<Menu> getMenusByEmpuuid(Long id);

    /**
     * 根据员工编号获取动态菜单
     * @param uuid
     * @return
     */
    Menu readMenusByEmpuuid(Long uuid);
}

