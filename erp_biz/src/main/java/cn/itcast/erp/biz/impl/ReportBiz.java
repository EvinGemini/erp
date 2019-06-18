package cn.itcast.erp.biz.impl;

import cn.itcast.erp.biz.IReportBiz;
import cn.itcast.erp.dao.IReportDao;

import java.util.*;

public class ReportBiz implements IReportBiz {
    private IReportDao reportDao;

    public void setReportDao(IReportDao reportDao) {
        this.reportDao = reportDao;
    }

    @Override
    public List orderReport(Date startDate, Date endDate) {
        return reportDao.orderReport(startDate,endDate);
    }

    @Override
    public List<Map<String, Object>> trendReport(int year) {
        //{"month":"6月","y":2168}
        List<Map<String, Object>> yearDate = reportDao.getSumMoney(year);
        List<Map<String, Object>> resultYearDate = new ArrayList<>();
        Map<String, Object> param = null;
        Map<String,Map<String, Object>> map = new HashMap<>();
        for (Map<String, Object> data : yearDate) {
            map.put((String) data.get("name"),data);
        }
        for (int i = 1; i <= 12; i++) {
            param = map.get(i + "月");
            if (param == null) {
                param = new HashMap<>();
                param.put("name",i + "月");
                param.put("y",0);
            }
            resultYearDate.add(param);
        }
        return resultYearDate;
    }
}
