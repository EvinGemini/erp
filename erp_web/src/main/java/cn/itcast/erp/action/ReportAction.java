package cn.itcast.erp.action;

import cn.itcast.erp.biz.IReportBiz;
import com.alibaba.fastjson.JSON;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportAction {
    private IReportBiz reportBiz;
    private Date startDate;
    private Date endDate;
    private Integer year;

    public void setReportBiz(IReportBiz reportBiz) {
        this.reportBiz = reportBiz;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void orderReport() {
        List list = reportBiz.orderReport(startDate,endDate);
        write(JSON.toJSONString(list));
    }

    public void trendReport() {
        List<Map<String, Object>> trendReport = reportBiz.trendReport(year);
        write(JSON.toJSONString(trendReport));
    }

    /**
     * 输出字符串到前端
     * @param jsonString
     */
    public void write(String jsonString){
        try {
            //响应对象
            HttpServletResponse response = ServletActionContext.getResponse();
            //设置编码
            response.setContentType("text/html;charset=utf-8");
            //输出给页面
            response.getWriter().write(jsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
