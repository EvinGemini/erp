package cn.itcast.erp.action;
import cn.itcast.erp.biz.IStoreBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Store;

/**
 * 仓库Action 
 * @author Administrator
 *
 */
public class StoreAction extends BaseAction<Store> {

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}

	public void myList() {
		Store store = getT1();
		if (null == store) {
			setT1(new Store());
		}
		Emp loginUser = getLoginUser();
		getT1().setEmpuuid(loginUser.getUuid());
		super.list();
	}

}
