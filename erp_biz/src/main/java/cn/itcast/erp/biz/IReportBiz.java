package cn.itcast.erp.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface IReportBiz {
    List orderReport(Date startDate, Date endDate);
    List<Map<String,Object>> trendReport(int year);
}
