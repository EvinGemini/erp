package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Storedetail;

import javax.mail.MessagingException;

/**
 * 仓库库存业务逻辑层接口
 * @author Administrator
 *
 */
public interface IStoredetailBiz extends IBaseBiz<Storedetail>{
    void sendStoreAlertMail() throws MessagingException;
}

