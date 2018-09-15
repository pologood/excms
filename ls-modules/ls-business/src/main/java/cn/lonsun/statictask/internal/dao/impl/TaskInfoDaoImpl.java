package cn.lonsun.statictask.internal.dao.impl;

import org.springframework.stereotype.Repository;

import cn.lonsun.core.base.dao.impl.MockDao;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statictask.internal.dao.ITaskInfoDao;
import cn.lonsun.statictask.internal.entity.TaskInfoEO;
import cn.lonsun.statictask.internal.vo.TaskInfoQueryVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 生成静态任务失败信息Dao层实现类<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Repository("taskInfoDao")
public class TaskInfoDaoImpl extends MockDao<TaskInfoEO> implements ITaskInfoDao {
    @Override
    public void deleteByStatus(Long status) {
        String hql = "delete from TaskInfoEO t where t.taskId in (select id from StaticTaskEO where siteId = ? and status = ?)";
        executeUpdateByHql(hql, new Object[] { LoginPersonUtil.getSiteId(), status });
    }

    @Override
    public Pagination getPagination(TaskInfoQueryVO queryVO) {
        String hql = "from TaskInfoEO t where t.taskId = ? and t.recordStatus = ?";
        // 放入参数对象
        Object[] values = new Object[] { queryVO.getTaskId(), AMockEntity.RecordStatus.Normal.toString() };
        return getPagination(queryVO.getPageIndex(), queryVO.getPageSize(), hql.toString(), values);
    }
}