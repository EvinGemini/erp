package cn.itcast.erp.action;
import cn.itcast.erp.entity.Emp;
import com.alibaba.fastjson.JSON;

import cn.itcast.erp.biz.IMenuBiz;
import cn.itcast.erp.entity.Menu;

import java.util.List;

/**
 * 菜单Action 
 * @author Administrator
 *
 */
public class MenuAction extends BaseAction<Menu> {

	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
		super.setBaseBiz(this.menuBiz);
	}
	
	/**
	 * 获取菜单数据
	 */
	public void getMenuTree(){
		//通过获取主菜单，自关联就会带其下所有的菜单
		Menu menu = menuBiz.get("0");
		write(JSON.toJSONString(menu));
	}

	public void getMenusByEmpuuid() {
		Emp loginUser = getLoginUser();
		List<Menu> menus = menuBiz.getMenusByEmpuuid(loginUser.getUuid());
		write(JSON.toJSONString(menus));
	}

	public void readMenusByEmpuuid() {
		Emp loginUser = getLoginUser();
		Menu menu = menuBiz.readMenusByEmpuuid(loginUser.getUuid());
		write(JSON.toJSONString(menu));
	}

}
