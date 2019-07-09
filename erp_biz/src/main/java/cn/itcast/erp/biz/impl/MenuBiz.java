package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.entity.Menu;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单业务逻辑类
 * @author Administrator
 *
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	private IMenuDao menuDao;
	
	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		super.setBaseDao(this.menuDao);
	}

	@Override
	public List<Menu> getMenusByEmpuuid(Long id) {
		return menuDao.getMenusByEmpuuid(id);
	}

	@Override
	public Menu readMenusByEmpuuid(Long uuid) {
		//1.获取所有菜单
		Menu menu = menuDao.get("0");
		//新建root菜单
		Menu root = cloneMenu(menu);
		//2.获取用户所拥有的菜单
		List<Menu> empMenus = menuDao.getMenusByEmpuuid(uuid);
		Menu rootM1 = null;
		Menu rootM2 = null;
		for (Menu m1 : menu.getMenus()) {		//获取一级菜单
			//克隆一级菜单
			rootM1 = cloneMenu(m1);
			for (Menu m2 : m1.getMenus()) {	//获取二级菜单
				if (empMenus.contains(m2)) {
					//克隆二级菜单
					rootM2 = cloneMenu(m2);
					rootM1.getMenus().add(rootM2);
				}
			}
			if (rootM1.getMenus().size() > 0) {
				root.getMenus().add(rootM1);
			}
		}
		return root;
	}

	private Menu cloneMenu(Menu src) {
		Menu _new = new Menu();
		_new.setIcon(src.getIcon());
		_new.setMenuid(src.getMenuid());
		_new.setMenuname(src.getMenuname());
		_new.setUrl(src.getUrl());
		_new.setMenus(new ArrayList<Menu>());
		return _new;
	}
}
