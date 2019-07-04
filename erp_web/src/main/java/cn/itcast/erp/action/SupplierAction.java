package cn.itcast.erp.action;
import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * 供应商Action 
 * @author Administrator
 *
 */
public class SupplierAction extends BaseAction<Supplier> {

	private ISupplierBiz supplierBiz;
	private File file;
	private String fileFileName;
	private String fileContentType;

	public void setFile(File file) {
		this.file = file;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}

	public void setSupplierBiz(ISupplierBiz supplierBiz) {
		this.supplierBiz = supplierBiz;
		super.setBaseBiz(this.supplierBiz);
	}

	public void export() {
		HttpServletResponse response = ServletActionContext.getResponse();
		String fileName = "";
		if (Supplier.TYPE_SUPPLIER.equals(getT1().getType())) {
			fileName = "供应商.xls";
		}else if (Supplier.TYPE_CUSTOMER.equals(getT1().getType())) {
			fileName = "客户.xls";
		}
		try {
			response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(),"ISO-8859-1"));
			supplierBiz.export(response.getOutputStream(),getT1());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void doImport() {
		if (!"application/vnd.ms-excel".equals(fileContentType)) {
			ajaxReturn(false,"上传的文件必须是Excel格式的");
			return;
		}
		try {
			supplierBiz.doImport(new FileInputStream(file));
			ajaxReturn(true,"上传的文件成功");
		} catch (ErpException e) {
			ajaxReturn(false,e.getMessage());
		} catch (IOException e) {
			ajaxReturn(false,"上传的文件失败");
			e.printStackTrace();
		}
	}
}
