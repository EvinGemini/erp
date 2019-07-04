package cn.itcast.erp.biz;
import cn.itcast.erp.entity.Supplier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 供应商业务逻辑层接口
 * @author Administrator
 *
 */
public interface ISupplierBiz extends IBaseBiz<Supplier>{
    //导出excel
    void export(OutputStream outputStream,Supplier t1);
    //导入excel
    void doImport(InputStream in) throws IOException;
}

