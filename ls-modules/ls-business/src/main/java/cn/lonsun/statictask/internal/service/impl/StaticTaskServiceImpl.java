package cn.lonsun.statictask.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.statictask.internal.dao.IStaticTaskDao;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;
import cn.lonsun.statictask.internal.service.IStaticTaskService;
import cn.lonsun.statictask.internal.service.ITaskInfoService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 生成静态任务业务层实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Service("staticTaskService")
public class StaticTaskServiceImpl extends MockService<StaticTaskEO> implements IStaticTaskService {
    @Autowired
    private IStaticTaskDao taskDao;
    @Autowired
    private ITaskInfoService infoService;

    @Override
    public Pagination getPage(Long pageIndex, Integer pageSize, Long userId) {
        if (userId == null) {
            return null;
        }
        return taskDao.getPage(pageIndex, pageSize, userId);
    }

    @Override
    public List<StaticTaskEO> clearTask() {
        Map<String, Object> map = new HashMap<String, Object>();
        Long siteId = LoginPersonUtil.getSiteId();
        if (siteId == null) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点ID为空");
        }
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        map.put("siteId", siteId);
        map.put("status", StaticTaskEO.COMPLETE);
        List<StaticTaskEO> list = getEntities(StaticTaskEO.class, map);
        taskDao.deleteByStatus(StaticTaskEO.COMPLETE);
        infoService.deleteByStatus(StaticTaskEO.COMPLETE);
        return list;
    }

    @Override
    public void deleteInitDoingTask() {
        taskDao.deleteInitDoingTask();
    }

    @Override
    public void updateInitDoingToOver() {
        taskDao.updateInitDoingToOver();
    }

    @Override
    public List<StaticTaskEO> checkTask(Long siteId, Long columnID, Long scope, Long source) {
        return taskDao.checkTask(siteId, columnID, scope, source);
    }
}