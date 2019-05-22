package cn.itcast.erp.action;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;

import java.util.Date;
import java.util.List;

/**
 * 订单Action 
 * @author Administrator
 *
 */
public class OrdersAction extends BaseAction<Orders> {

	private IOrdersBiz ordersBiz;
	private String json;

	public void setJson(String json) {
		this.json = json;
	}

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}

	@Override
	public void add() {
		//判断是否登陆
		Emp loginUser = getLoginUser();
		if (null == loginUser) {
			ajaxReturn(false,"亲，请先登录！");
			return;
		}
		try {
			Orders orders = getT();
			//设置下单人
			orders.setCreater(loginUser.getUuid());
			//获取订单明细
			List<Orderdetail> orderdetails = JSON.parseArray(json, Orderdetail.class);
			//设置订单明细
			orders.setOrderdetails(orderdetails);
			//保存订单
			ordersBiz.add(orders);
			ajaxReturn(true,"添加订单成功！");
		} catch (Exception e) {
			ajaxReturn(false,"添加订单失败！");
			e.printStackTrace();
		}
	}

    /**
     * 订单审核
     */
	public void doCheck() {
		//判断是否登陆
		Emp loginUser = getLoginUser();
		if (null == loginUser) {
			ajaxReturn(false,"亲，请先登录！");
			return;
		}
		try {
			ordersBiz.doCheck(getId(),loginUser.getUuid());
			ajaxReturn(true,"审核成功");
		}catch (ErpException e) {
			ajaxReturn(true,e.getMessage());
		} catch (Exception e) {
			ajaxReturn(false,"审核失败");
			e.printStackTrace();
		}
	}

    /**
     * 订单确认
     */
    public void doStart() {
        //判断是否登陆
        Emp loginUser = getLoginUser();
        if (null == loginUser) {
            ajaxReturn(false,"亲，请先登录！");
            return;
        }
        try {
            ordersBiz.doStart(getId(),loginUser.getUuid());
            ajaxReturn(true,"确认成功");
        }catch (ErpException e) {
            ajaxReturn(true,e.getMessage());
        } catch (Exception e) {
            ajaxReturn(false,"确认失败");
            e.printStackTrace();
        }
    }

}
