package cn.lonsun.monitor.task.internal.service.impl;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.monitor.task.internal.dao.IMonitorHrefUseableDynamicDao;
import cn.lonsun.monitor.task.internal.entity.MonitorHrefUseableDynamicEO;
import cn.lonsun.monitor.task.internal.entity.vo.HrefUseableQueryVO;
import cn.lonsun.monitor.task.internal.service.IMonitorHrefUseableDynamicService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 任务指标管理服务类
 * @author gu.fei
 * @version 2017-09-28 9:24
 */
@Service
public class MonitorHrefUseableDynamicServiceImpl extends MockService<MonitorHrefUseableDynamicEO> implements IMonitorHrefUseableDynamicService {

    @Resource
    private IMonitorHrefUseableDynamicDao monitorHrefUseableDynamicDao;


    @Override
    public Pagination getPage(HrefUseableQueryVO vo) {
        return monitorHrefUseableDynamicDao.getPage(vo);
    }

    @Override
    public Long getHrefUseableDynamicCout(Long taskId, Date date) {
        return monitorHrefUseableDynamicDao.getHrefUseableDynamicCout(taskId,date);
    }
}
