package cn.itcast.erp.action;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;
import com.redsum.bos.ws.Waybilldetail;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
	private Long waybillSn;

	public void setWaybillSn(Long waybillSn) {
		this.waybillSn = waybillSn;
	}

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

	/**
	 * 我的订单
	 */
	public void myListByPage() {
		if (getT1() == null) {
			setT1(new Orders());
		}
		Emp loginUser = getLoginUser();
		if (loginUser == null) {
			ajaxReturn(false,"亲，请先登录！");
			return;
		}
		getT1().setCreater(loginUser.getUuid());
		super.listByPage();
	}

	/**
	 * 根据id导出订单详情
	 */
	public void exportById() {
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = getId() + "_.xls";
		try {
			response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(),"ISO-8859-1"));
			ordersBiz.exportById(response.getOutputStream(),getId());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void waybillDetailList() {
		List<Waybilldetail> waybilldetails = ordersBiz.waybillDetailList(waybillSn);
		write(JSON.toJSONString(waybilldetails));
	}

}
