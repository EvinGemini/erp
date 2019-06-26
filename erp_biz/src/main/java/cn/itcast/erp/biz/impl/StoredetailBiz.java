package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IStoredetailBiz;
import cn.itcast.erp.dao.IGoodsDao;
import cn.itcast.erp.dao.IStoreDao;
import cn.itcast.erp.dao.IStorealertDao;
import cn.itcast.erp.dao.IStoredetailDao;
import cn.itcast.erp.entity.Goods;
import cn.itcast.erp.entity.Store;
import cn.itcast.erp.entity.Storealert;
import cn.itcast.erp.entity.Storedetail;
import cn.itcast.erp.exception.ErpException;
import cn.itcast.erp.utils.MailUtil;

import javax.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仓库库存业务逻辑类
 * @author Administrator
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	private IStoredetailDao storedetailDao;
	private IGoodsDao goodsDao;
	private IStoreDao storeDao;
	private IStorealertDao storealertDao;
	private MailUtil mailUtil;
	private String to;
	private String subject;
	private String text;
	
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		super.setBaseDao(this.storedetailDao);
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public void setStorealertDao(IStorealertDao storealertDao) {
		this.storealertDao = storealertDao;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult, int maxResults) {
		List<Storedetail> storedetails = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long,String> goodsNameMap = new HashMap<>();
		Map<Long,String> storeNameMap = new HashMap<>();
		for (Storedetail storedetail : storedetails) {
			storedetail.setGoodsName(getGoodsName(storedetail.getGoodsuuid(),goodsNameMap));
			storedetail.setStoreName(getStoreName(storedetail.getStoreuuid(),storeNameMap));
		}
		return storedetails;
	}

	private String getGoodsName(Long uuid,Map<Long,String> goodsNameMap) {
		if (uuid == null) {
			return null;
		}
		String goodsName = goodsNameMap.get(uuid);
		if (null == goodsName) {
			Goods goods = goodsDao.get(uuid);
			goodsName = goods.getName();
			goodsNameMap.put(uuid,goodsName);
		}
		return goodsName;
	}

	private String getStoreName(Long uuid,Map<Long,String> storeNameMap) {
		if (uuid == null) {
			return null;
		}
		String storeName = storeNameMap.get(uuid);
		if (null == storeName) {
			Store store = storeDao.get(uuid);
			storeName = store.getName();
			storeNameMap.put(uuid,storeName);
		}
		return storeName;
	}

	@Override
	public void sendStoreAlertMail() throws MessagingException {
		List<Storealert> storealertList = storealertDao.getStorealertList();
		int count = storealertList == null ? 0 : storealertList.size();
		if (count > 0) {
			//库存需要预警
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			mailUtil.sendMail(to,subject.replace("[time]",simpleDateFormat.format(new Date())),
					text.replace("[count]",String.valueOf(count)));
		}else {
			//库存不需要预警
			throw new ErpException("库存充足，不需要预警！");
		}


	}
}
