package cn.lonsun.log.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.log.internal.entity.LogEO;
import cn.lonsun.log.internal.entity.LoginHistoryEO;
import cn.lonsun.log.internal.service.ILogService;
import cn.lonsun.log.internal.service.ILoginHistoryService;

/**
 * Created by yy on 2014/8/12.
 */
@Controller
@RequestMapping("log")
public class LogController extends BaseController {
    @Autowired
    private ILogService logService;
    @Autowired
    private ILoginHistoryService loginHistoryService;
    
    /**
     * 登录日志
     * @return
     */
    @RequestMapping("loginLogPage")
    public String loginLogPage(){
    	return "/app/mgr/log_login";
    }
    /**
     * 管理日志
     * @return
     */
    @RequestMapping("mgrLogPage")
    public String mgrLogPage(){
    	return "/app/mgr/log_mgr";
    }
    
    /**
     * 管理日志查询
     *
     * @author yy
     * @param pageIndex
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(Long pageIndex,Integer pageSize, String startDate, String endDate, String type, String key){
        if(pageIndex==null||pageIndex<0){
            pageIndex = 0L;
        }
        if(pageSize==null||pageSize<0){
            pageSize = 15;
        }
        Date start = null;
        Date end = null;
        if(null!=StringUtils.trimToNull(startDate)) {
            start = formatStringToTime(startDate, "yyyy-MM-dd HH:mm:ss");
        }
        if(null!=StringUtils.trimToNull(endDate)) {
        	endDate = endDate.replace("00:00:00", "23:59:59");
            end = formatStringToTime(endDate, "yyyy-MM-dd HH:mm:ss");
        }
        Pagination page = null;
        page = logService.getPage(pageIndex, pageSize, start, end, type, key);
        return getObject(page);
    }
    
    /**
     * 登录日志查询
     *
     * @author yy
     * @param pageIndex
     * @param pageSize
     * @param startDate
     * @param endDate
     * @param type
     * @param key
     * @return
     */
    @RequestMapping("getLoginPage")
    @ResponseBody
    public Object getLoginPage(Long pageIndex,Integer pageSize, String startDate, String endDate, String type, String key){
        if(pageIndex==null||pageIndex<0){
            pageIndex = 0L;
        }
        if(pageSize==null||pageSize<0){
            pageSize = 15;
        }
        Date start = null;
        Date end = null;
        if(null!=StringUtils.trimToNull(startDate)) {
            start = formatStringToTime(startDate, "yyyy-MM-dd HH:mm:ss");
        }
        if(null!=StringUtils.trimToNull(endDate)) {
        	endDate = endDate.replace("00:00:00", "23:59:59");
            end = formatStringToTime(endDate, "yyyy-MM-dd HH:mm:ss");
        }
        Pagination page = null;
        page = loginHistoryService.getPage(pageIndex, pageSize, start, end, type, key);
        return getObject(page);
    }
    
    /***
     * 导出管理日志
     * 
     * @param actionMapping
     * @param request
     * @param response
     */
    @RequestMapping("getLogExport")
    public void getLogExport(HttpServletResponse response, String startDate, String endDate, String type, String key){
        Date start = null;
        Date end = null;
        if(null!=StringUtils.trimToNull(startDate)) {
            start = formatStringToTime(startDate, "yyyy-MM-dd HH:mm:ss");
        }
        if(null!=StringUtils.trimToNull(endDate)) {
        	endDate = endDate.replace("00:00:00", "23:59:59");
            end = formatStringToTime(endDate, "yyyy-MM-dd HH:mm:ss");
        }
        List<LogEO> logs = logService.getAllLogs(start, end, type, key);
        //文件头
        String[] titles = new String[]{"用户名","姓名","单位名称","IP","操作时间","维护内容"};
        //内容
        List<String[]> datas = new ArrayList<String[]>();
        if(logs!=null&&logs.size()>0){
        	for (LogEO log : logs) {
                String[] row1 = new String[6];
                row1[0] = log.getUid();
                row1[1] = log.getCreateUser();
                row1[2] = log.getOrganName();
                row1[3] = log.getOperationIp();
                row1[4] = formatTimeToString(log.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
                row1[5] = log.getDescription();
                
                datas.add(row1);
            }
        }
        //导出
        String name = System.currentTimeMillis()+"";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /***
     * 导出登录日志
     * 
     * @param actionMapping
     * @param request
     * @param response
     */
    @RequestMapping("getLogHistoryExport")
    public void getLogHistoryExport(HttpServletResponse response, String startDate, String endDate, String type, String key){
        Date start = null;
        Date end = null;
        if(null!=StringUtils.trimToNull(startDate)) {
            start = formatStringToTime(startDate, "yyyy-MM-dd HH:mm:ss");
        }
        if(null!=StringUtils.trimToNull(endDate)) {
        	endDate = endDate.replace("00:00:00", "23:59:59");
            end = formatStringToTime(endDate, "yyyy-MM-dd HH:mm:ss");
        }
        List<LoginHistoryEO> logs = loginHistoryService.getAllLogs(start, end, type, key);
        //文件头
        String[] titles = new String[]{"用户名","姓名","单位名称","登录IP","登录时间","描述"};
        //内容
        List<String[]> datas = new ArrayList<String[]>();
        if(logs!=null&&logs.size()>0){
        	 for (LoginHistoryEO log : logs) {
                 String[] row1 = new String[6];
                 row1[0] = log.getUid();
                 row1[1] = log.getCreateUser();
                 row1[2] = log.getOrganName();
                 row1[3] = log.getLoginIp();
                 row1[4] = formatTimeToString(log.getCreateDate(), "yyyy-MM-dd HH:mm:ss");
                 row1[5] = log.getDescription();
                 datas.add(row1);
             }
        }
       
        //导出
        String name = System.currentTimeMillis()+"";
        try {
            CSVUtils.download(name, titles, datas, response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Date formatStringToTime(String str,String pattern){
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        Date result = null;
        try {
            result = format.parse(str);
        } catch (ParseException e) {
            return null;
        }
        return result;
    }
    
    public String formatTimeToString(Date date,String format){
        SimpleDateFormat f = new SimpleDateFormat(format);
        if(date!=null){
            return f.format(date);
        }
        return null;
    }
}
