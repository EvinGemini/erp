package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Orders;
import com.redsum.bos.ws.Waybilldetail;

import java.io.OutputStream;
import java.util.List;

/**
 * 订单业务逻辑层接口
 * @author Administrator
 *
 */
public interface IOrdersBiz extends IBaseBiz<Orders>{
    void doCheck(Long uuid, Long empUuid);
    /**
     * 订单确认
     * @param uuid
     * @param empUuid
     */
    void doStart(Long uuid, Long empUuid);

    //导出订单
    void exportById(OutputStream outputStream,Long uuid) throws Exception;

    List<Waybilldetail> waybillDetailList(Long sn);
}

