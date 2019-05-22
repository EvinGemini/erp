package cn.itcast.erp.action;
import cn.itcast.erp.biz.IOrderdetailBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.exception.ErpException;

/**
 * 订单明细Action 
 * @author Administrator
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {
	private Long storeuuid;

	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	private IOrderdetailBiz orderdetailBiz;

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}

	public void doInStore() {
		//判断是否登陆
		Emp loginUser = getLoginUser();
		if (loginUser == null) {
			ajaxReturn(false,"请登陆后在操作");
			return;
		}
		try {
			orderdetailBiz.doInStore(getId(),storeuuid,loginUser.getUuid());
			ajaxReturn(true,"入库成功");
		} catch (ErpException e){
			ajaxReturn(false,e.getMessage());
		}catch (Exception e) {
			ajaxReturn(false,"入库失败");
			e.printStackTrace();
		}


	}

}
