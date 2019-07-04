package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.ISupplierBiz;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * 供应商业务逻辑类
 * @author Administrator
 *
 */
public class SupplierBiz extends BaseBiz<Supplier> implements ISupplierBiz {

	private ISupplierDao supplierDao;
	
	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
		super.setBaseDao(this.supplierDao);
	}

	@Override
	public void export(OutputStream outputStream, Supplier t1) {
		List<Supplier> supplierList = supplierDao.getList(t1, null, null);
		HSSFWorkbook workbook = new HSSFWorkbook();
		//创建表格
		String workTitle = "";
		if (Supplier.TYPE_SUPPLIER.equals(t1.getType())) {
			workTitle = "供应商";
		}else if (Supplier.TYPE_CUSTOMER.equals(t1.getType())) {
			workTitle = "客户";
		}
		HSSFSheet sheet = workbook.createSheet(workTitle);
		//创建表头
		HSSFRow headRow = sheet.createRow(0);
		String[] titles = {"名称","地址","联系人","电话","Email"};
		HSSFCell cell = null;
		//创建表标题
		for (int i = 0; i < titles.length; i++) {
			cell = headRow.createCell(i);
			cell.setCellValue(titles[i]);
		}
		HSSFRow row = null;
		for (int i = 0; i <= supplierList.size() - 1; i++) {
			row = sheet.createRow(i+1);
			row.createCell(0).setCellValue(supplierList.get(i).getName());
			row.createCell(1).setCellValue(supplierList.get(i).getAddress());
			row.createCell(2).setCellValue(supplierList.get(i).getContact());
			row.createCell(3).setCellValue(supplierList.get(i).getTele());
			row.createCell(4).setCellValue(supplierList.get(i).getEmail());
		}
		try {
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doImport(InputStream in) throws IOException {
		HSSFWorkbook workbook = new HSSFWorkbook(in);
		HSSFSheet sheet = workbook.getSheetAt(0);
		String type = "";
		if ("供应商".equals(sheet.getSheetName())) {
			type = Supplier.TYPE_SUPPLIER;
		}else if ("客户".equals(sheet.getSheetName())) {
			type = Supplier.TYPE_CUSTOMER;
		}else {
			throw new ErpException("工作表名称不正确");
		}
		int lastNum = sheet.getLastRowNum();
		Supplier supplier = null;
		for (int i = 1; i <= lastNum; i++) {
			supplier = new Supplier();
			supplier.setName(sheet.getRow(i).getCell(0).getStringCellValue());
			List<Supplier> suppliers = supplierDao.getList(null, supplier, null);
			if (suppliers.size() > 0) {
				supplier = suppliers.get(0);
			}
			supplier.setAddress(sheet.getRow(i).getCell(1).getStringCellValue());
			supplier.setContact(sheet.getRow(i).getCell(2).getStringCellValue());
			supplier.setTele(sheet.getRow(i).getCell(3).getStringCellValue());
			supplier.setEmail(sheet.getRow(i).getCell(4).getStringCellValue());
			if (suppliers.size() == 0) {
				supplier.setType(type);
				supplierDao.add(supplier);
			}
		}
		workbook.close();
	}
}
