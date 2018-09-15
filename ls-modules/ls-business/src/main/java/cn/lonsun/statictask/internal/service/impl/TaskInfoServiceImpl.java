package cn.lonsun.statictask.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statictask.internal.dao.ITaskInfoDao;
import cn.lonsun.statictask.internal.entity.TaskInfoEO;
import cn.lonsun.statictask.internal.service.ITaskInfoService;
import cn.lonsun.statictask.internal.vo.TaskInfoQueryVO;

/**
 * 生成静态任务失败信息业务层实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Service("taskInfoService")
public class TaskInfoServiceImpl extends MockService<TaskInfoEO> implements ITaskInfoService {

    @Autowired
    private ITaskInfoDao infoDao;

    @Override
    public void deleteByStatus(Long status) {
        infoDao.deleteByStatus(status);
    }

    @Override
    public Pagination getPagination(TaskInfoQueryVO queryVO) {
        return infoDao.getPagination(queryVO);
    }
}