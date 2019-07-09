package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.dao.IMenuDao;
import cn.itcast.erp.dao.IRoleDao;
import cn.itcast.erp.entity.Menu;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色业务逻辑类
 * @author Administrator
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {

	private IRoleDao roleDao;
	private IMenuDao menuDao;
	
	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		super.setBaseDao(this.roleDao);
	}

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}

	@Override
	public List<Tree> readRoleMenus(Long uuid) {
		List<Tree> trees = new ArrayList<>();
		Menu menu = menuDao.get("0");
		//获取当前角色
		List<Menu> roleMenus = roleDao.get(uuid).getMenus();
		Tree t1 = null;
		Tree t2 = null;
		for (Menu m : menu.getMenus()) {	//得到一级菜单
			t1 = new Tree();
			t1.setId(m.getMenuid());
			t1.setText(m.getMenuname());
			for (Menu m2 : m.getMenus()) {
				t2 = new Tree();
				t2.setId(m2.getMenuid());
				t2.setText(m2.getMenuname());
				if (roleMenus.contains(m2)) {
					t2.setChecked(true);
				}
				t1.getChildren().add(t2);
			}
			trees.add(t1);
		}
		return trees;
	}

	@Override
	public void updateRoleMenus(Long uuid, String checkedStr) {
		Role role = roleDao.get(uuid);
		role.setMenus(new ArrayList<Menu>());
		Menu menu = null;
		for (String id : checkedStr.split(",")) {
			menu = menuDao.get(id);
			role.getMenus().add(menu);
		}
	}
}
