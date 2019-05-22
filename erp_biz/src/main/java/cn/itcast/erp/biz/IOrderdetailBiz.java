package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Orderdetail;
/**
 * 订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrderdetailBiz extends IBaseBiz<Orderdetail>{
    /**
     * 商品入库
     * @param uuid 订单明细id
     * @param storeUuidd 仓库id
     * @param empUuid 库管员id
     */
    void doInStore(Long uuid,Long storeUuidd,Long empUuid);
}

