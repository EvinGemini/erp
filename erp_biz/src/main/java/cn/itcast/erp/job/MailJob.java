package cn.itcast.erp.job;

import cn.itcast.erp.biz.IStoredetailBiz;

import javax.mail.MessagingException;
import java.util.Date;

public class MailJob {
    private IStoredetailBiz storedetailBiz;

    public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
        this.storedetailBiz = storedetailBiz;
    }

    public void sendStoreAlertMail(){
        try {
            storedetailBiz.sendStoreAlertMail();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        System.out.println("任务调度：" + new Date());
    }
}
