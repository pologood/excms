package cn.lonsun.monitor.config.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.monitor.config.internal.entity.MonitoredAlarmConfigEO;
import cn.lonsun.monitor.config.internal.service.IMonitoredAlarmConfigService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by lonsun on 2017-10-24.
 */
@Controller
@RequestMapping("/monitor/alarm")
public class MonitoredAlarmConfigController extends BaseController {
    @Resource
    private IMonitoredAlarmConfigService monitoredAlarmConfigService;


    @RequestMapping("alarmIndex")
    private String alarmIndex(){

        return "/monitor/config/alarm_index";
    }


    @RequestMapping("getAlarmBySite")
    @ResponseBody
    private Object getAlarmBySite(){
        MonitoredAlarmConfigEO monitoredAlarmConfigEO =new MonitoredAlarmConfigEO();
        monitoredAlarmConfigEO =monitoredAlarmConfigService.getConfigBySiteId(LoginPersonUtil.getSiteId());
        return getObject(monitoredAlarmConfigEO);
    }

    @RequestMapping("saveAlarmBySite")
    @ResponseBody
    private Object saveAlarmBySite(MonitoredAlarmConfigEO monitoredAlarmConfigEO){
        if(AppUtil.isEmpty(monitoredAlarmConfigEO.getAlarmId())){
            monitoredAlarmConfigEO.setSiteId(LoginPersonUtil.getSiteId());
           monitoredAlarmConfigService.saveEntity(monitoredAlarmConfigEO);
        }else {

            monitoredAlarmConfigService.updateEntity(monitoredAlarmConfigEO);
        }
        return getObject(monitoredAlarmConfigEO);
    }


}
