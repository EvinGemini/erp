package cn.itcast.erp.biz.impl;
import cn.itcast.erp.biz.IOrdersBiz;
import cn.itcast.erp.dao.IEmpDao;
import cn.itcast.erp.dao.IOrdersDao;
import cn.itcast.erp.dao.ISupplierDao;
import cn.itcast.erp.entity.Emp;
import cn.itcast.erp.entity.Orderdetail;
import cn.itcast.erp.entity.Orders;
import cn.itcast.erp.entity.Supplier;
import cn.itcast.erp.exception.ErpException;
import com.alibaba.fastjson.JSON;
import com.redsum.bos.ws.Waybilldetail;
import com.redsum.bos.ws.impl.IWaybillWs;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单业务逻辑类
 * @author Administrator
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	private IOrdersDao ordersDao;
	private IEmpDao empDao;
	private ISupplierDao supplierDao;
	private IWaybillWs waybillWs;

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		super.setBaseDao(this.ordersDao);
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	@Override
	public void add(Orders orders) {
		//设置订单状态,开始下单都是未审核
		orders.setState(Orders.STATE_CREATE);
		//设置下单时间
		orders.setCreatetime(new Date());
		//设置订单类型
//		orders.setType(Orders.TYPE_IN);
		double total = 0;
		//设置订单明细与订单的关联与合计金额
		for (Orderdetail orderdetail : orders.getOrderdetails()) {
			total += orderdetail.getMoney();
			//设置订单明细状态
			orderdetail.setState(Orderdetail.STATE_NOT_IN);
			//设置关联关系
			orderdetail.setOrders(orders);
		}
		//设置订单的合计金额
		orders.setTotalmoney(total);
		//保存订单
		ordersDao.add(orders);
	}

	@Override
	public List<Orders> getListByPage(Orders t1, Orders t2, Object param, int firstResult, int maxResults) {
		List<Orders> listByPage = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long,String> empMap = new HashMap<>();
		Map<Long,String> supplierMap = new HashMap<>();
		for (Orders orders : listByPage) {
			//设置下单员姓名
			orders.setCreaterName(getEmpName(orders.getCreater(),empMap));
			//设置审核姓名
			orders.setCheckerName(getEmpName(orders.getChecker(),empMap));
			//设置采购员姓名
			orders.setStarterName(getEmpName(orders.getStarter(),empMap));
			//设置库管员姓名
			orders.setEnderName(getEmpName(orders.getEnder(),empMap));
			//设置供应商姓名
			orders.setSupplierName(getSupplierName(orders.getSupplieruuid(),supplierMap));
		}
		return listByPage;
	}

	/**
	 * 订单审核
	 * @param uuid
	 * @param empUuid
	 */
	public void doCheck(Long uuid, Long empUuid) {
		//获取订单
		Orders orders = ordersDao.get(uuid);
		//设置订单状态
		if (!Orders.STATE_CREATE.equals(orders.getState())) {
			throw new ErpException("对不起，您的订单已审核！");
		}
		orders.setState(Orders.STATE_CHECK);
		//设置审核时间
		orders.setChecktime(new Date());
		//设置审核人
		orders.setChecker(empUuid);
		ordersDao.update(orders);
	}

	/**
	 * 订单确认
	 * @param uuid
	 * @param empUuid
	 */
	public void doStart(Long uuid, Long empUuid) {
		//获取订单
		Orders orders = ordersDao.get(uuid);
		//设置订单状态
		if (!Orders.STATE_CHECK.equals(orders.getState())) {
			throw new ErpException("对不起，您的订单已经确认过了！");
		}
		orders.setState(Orders.STATE_START);
		//设置采购时间
		orders.setStarttime(new Date());
		//设置采购员
		orders.setStarter(empUuid);
		ordersDao.update(orders);
	}

	@Override
	public void exportById(OutputStream outputStream, Long uuid) throws Exception {
		Orders orders = ordersDao.get(uuid);
		String sheetTitle = "";
		if (Orders.TYPE_IN.equals(orders.getType())) {
			sheetTitle = "采购";
		}else if (Orders.TYPE_OUT.equals(orders.getType())) {
			sheetTitle = "销售";
		}
		List<Orderdetail> orderdetails = orders.getOrderdetails();
		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet(sheetTitle);
		//创建单元格的样式
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderLeft(BorderStyle.THIN);  //左边框
		cellStyle.setBorderTop(BorderStyle.THIN);  //上边框
		cellStyle.setBorderRight(BorderStyle.THIN);  //右边框
		cellStyle.setBorderBottom(BorderStyle.THIN);  //下边框
		//设置对齐方式
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		//设置内容的字体
		HSSFFont content_font = workbook.createFont();
		content_font.setFontName("宋体");
		content_font.setFontHeightInPoints((short) 11);
		cellStyle.setFont(content_font);
		//商品详情数量
		int rowCount = orderdetails.size() + 9;
		//创建行
		for (int i = 2; i <= rowCount; i++) {
			HSSFRow row = sheet.createRow(i);
			row.setHeight((short) 500);
			for (int j = 0; j < 4; j++) {
				HSSFCell cell = row.createCell(j);
				cell.setCellStyle(cellStyle);
			}
		}
		//合并单元格
		sheet.addMergedRegion(new CellRangeAddress(0,0,0,3));
		sheet.addMergedRegion(new CellRangeAddress(2,2,1,3));
		sheet.addMergedRegion(new CellRangeAddress(7,7,0,3));
		//设置单元格值
		HSSFCell firstCell = sheet.createRow(0).createCell(0);
		if (Orders.TYPE_IN.equals(orders.getType())) {
			sheetTitle = "采购单";
		}else if (Orders.TYPE_OUT.equals(orders.getType())) {
			sheetTitle = "销售单";
		}
		firstCell.setCellValue(sheetTitle);
		if (Orders.TYPE_IN.equals(orders.getType())) {
			sheetTitle = "供应商";
		}else if (Orders.TYPE_OUT.equals(orders.getType())) {
			sheetTitle = "客户";
		}
		sheet.getRow(2).getCell(0).setCellValue(sheetTitle);
		sheet.getRow(3).getCell(0).setCellValue("下单日期");
		sheet.getRow(3).getCell(2).setCellValue("经办人");
		sheet.getRow(4).getCell(0).setCellValue("审核日期");
		sheet.getRow(4).getCell(2).setCellValue("经办人");
		sheet.getRow(5).getCell(0).setCellValue("采购日期");
		sheet.getRow(5).getCell(2).setCellValue("经办人");
		if (Orders.TYPE_IN.equals(orders.getType())) {
			sheetTitle = "入库日期";
		}else if (Orders.TYPE_OUT.equals(orders.getType())) {
			sheetTitle = "出库日期";
		}
		sheet.getRow(6).getCell(0).setCellValue(sheetTitle);
		sheet.getRow(6).getCell(2).setCellValue("经办人");
		sheet.getRow(7).getCell(0).setCellValue("订单明细");
		sheet.getRow(8).getCell(0).setCellValue("商品名称");
		sheet.getRow(8).getCell(1).setCellValue("数量");
		sheet.getRow(8).getCell(2).setCellValue("价格");
		sheet.getRow(8).getCell(3).setCellValue("金额");
		//设置行高和列宽
		sheet.getRow(0).setHeight((short) 1000);
		//设置列宽
		for (int i = 0; i < 4; i++) {
			sheet.setColumnWidth(i,5000);
		}
		//设置表头对齐方式
		HSSFCellStyle firstCellStyle = workbook.createCellStyle();
		firstCellStyle.setAlignment(HorizontalAlignment.CENTER);
		firstCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		HSSFFont firstFont = workbook.createFont();
		firstFont.setFontName("黑体");
		firstFont.setBold(true);
		firstFont.setFontHeightInPoints((short) 18);
		firstCellStyle.setFont(firstFont);
		firstCell.setCellStyle(firstCellStyle);
		//设置日期格式
		HSSFCellStyle date_style = workbook.createCellStyle();
		date_style.cloneStyleFrom(cellStyle);
		HSSFDataFormat dataFormat = workbook.createDataFormat();
		date_style.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm"));
		for (int i = 3; i < 7; i++) {
			sheet.getRow(i).getCell(1).setCellStyle(date_style);
		}


		Map<Long,String> empMap = new HashMap<>();
		Map<Long,String> supplierMap = new HashMap<>();
		//设置供应商名称
		sheet.getRow(2).getCell(1).setCellValue(getSupplierName(orders.getSupplieruuid(),supplierMap));
		//设置日期
		if (null != orders.getCreatetime()) {
			sheet.getRow(3).getCell(1).setCellValue(orders.getCreatetime());
		}
		if (null != orders.getChecktime()) {
			sheet.getRow(4).getCell(1).setCellValue(orders.getChecktime());
		}
		if (null != orders.getStarttime()) {
			sheet.getRow(5).getCell(1).setCellValue(orders.getStarttime());
		}
		if (null != orders.getEndtime()) {
			sheet.getRow(6).getCell(1).setCellValue(orders.getEndtime());
		}

		//设置经办人
		if (null != orders.getCreater()) {
			sheet.getRow(3).getCell(3).setCellValue(getEmpName(orders.getCreater(),empMap));
		}
		if (null != orders.getChecker()) {
			sheet.getRow(4).getCell(3).setCellValue(getEmpName(orders.getChecker(),empMap));
		}
		if (null != orders.getStarter()) {
			sheet.getRow(5).getCell(3).setCellValue(getEmpName(orders.getStarter(),empMap));
		}
		if (null != orders.getEnder()) {
			sheet.getRow(6).getCell(3).setCellValue(getEmpName(orders.getEnder(),empMap));
		}
		//设置订单明细
		int index = 0;
		for (int i = 9; i < rowCount; i++) {
			Orderdetail orderdetail = orderdetails.get(index);
			sheet.getRow(i).getCell(0).setCellValue(orderdetail.getGoodsname());
			sheet.getRow(i).getCell(1).setCellValue(orderdetail.getNum());
			sheet.getRow(i).getCell(2).setCellValue(orderdetail.getPrice());
			sheet.getRow(i).getCell(3).setCellValue(orderdetail.getMoney());
			index++;
		}
		sheet.getRow(rowCount).getCell(0).setCellValue("合计");
		sheet.getRow(rowCount).getCell(3).setCellValue(orders.getTotalmoney());
		workbook.write(outputStream);
		workbook.close();
	}

	@Override
	public List<Waybilldetail> waybillDetailList(Long sn) {
		return waybillWs.waybilldetailList(sn);
	}


	private String getEmpName(Long uuid,Map<Long,String> empMap) {
		//如果员工编号为null，则返回null
		if (null == uuid) {
			return null;
		}
		//通过员工编号从缓存中获取empName
		String empName = empMap.get(uuid);
		if (null == empName) {
			Emp emp = empDao.get(uuid);
			empMap.put(uuid,emp.getName());
			empName = emp.getName();
		}
		return empName;
	}

	private String getSupplierName(Long uuid,Map<Long,String> supplierMap) {
		//如果供应商id为null，则返回null
		if (null == uuid) {
			return null;
		}
		//通过供应商id从缓存中获取supplierName
		String supplierName = supplierMap.get(uuid);
		if (null == supplierName) {
			Supplier supplier = supplierDao.get(uuid);
			supplierMap.put(uuid,supplier.getName());
			supplierName = supplier.getName();
		}
		return supplierName;
	}
}
