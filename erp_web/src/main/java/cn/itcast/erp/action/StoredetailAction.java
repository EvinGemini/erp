package cn.itcast.erp.action;
import cn.itcast.erp.biz.IStorealertBiz;
import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;

import javax.mail.MessagingException;
import java.util.List;

/**
 * 仓库库存Action 
 * @author Administrator
 *
 */
public class StoredetailAction extends BaseAction<Storedetail> {

	private IStoredetailBiz storedetailBiz;
	private IStorealertBiz storealertBiz;

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
		super.setBaseBiz(this.storedetailBiz);
	}

	public void setStorealertBiz(IStorealertBiz storealertBiz) {
		this.storealertBiz = storealertBiz;
	}

	public void getStorealertList() {
		List<Storealert> storealertList = storealertBiz.getStorealertList();
		write(JSON.toJSONString(storealertList));
	}

	public void sendStorealertMail() {
		try {
			storedetailBiz.sendStoreAlertMail();
			ajaxReturn(true,"邮件发送成功");
		} catch (MessagingException e) {
			e.printStackTrace();
			ajaxReturn(false,"邮件发送失败");
		}catch (ErpException e) {
			e.printStackTrace();
			ajaxReturn(false,e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			ajaxReturn(false,"邮件发送失败~");
		}
	}

}
