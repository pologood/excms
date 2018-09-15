package cn.lonsun.monitor.internal.service.impl;

import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.monitor.internal.dao.IMonitorCodeRegisterDao;
import cn.lonsun.monitor.internal.entity.MonitorCodeRegisterEO;
import cn.lonsun.monitor.internal.service.IMonitorCodeRegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 日常监测站群注册service实现<br/>
 *
 */
@Service("monitorCodeRegisterService")
public class MonitorCodeRegisterServiceImpl extends BaseService<MonitorCodeRegisterEO> implements IMonitorCodeRegisterService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IMonitorCodeRegisterDao monitorCodeRegisterDao;





}

