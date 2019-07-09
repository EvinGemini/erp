package cn.itcast.erp.action;
import cn.itcast.erp.biz.IRoleBiz;
import cn.itcast.erp.entity.Role;
import cn.itcast.erp.entity.Tree;
import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.ir.debug.JSONWriter;

import java.util.List;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;
	private String checkedStr;

	public void setCheckedStr(String checkedStr) {
		this.checkedStr = checkedStr;
	}

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}

	public void readRoleMenus() {
		List<Tree> trees = roleBiz.readRoleMenus(getId());
		write(JSON.toJSONString(trees));
	}

	public void updateRoleMenus() {
		try {
			roleBiz.updateRoleMenus(getId(),checkedStr);
			ajaxReturn(true,"更新成功");
		} catch (Exception e) {
			ajaxReturn(false,"更新失败");
			e.printStackTrace();
		}
	}

}
