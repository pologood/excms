package cn.lonsun.statictask.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statictask.internal.entity.TaskInfoEO;
import cn.lonsun.statictask.internal.vo.TaskInfoQueryVO;

/**
 * 生成静态任务失败信息Dao层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */

public interface ITaskInfoDao extends IMockDao<TaskInfoEO> {
    public void deleteByStatus(Long status);

    Pagination getPagination(TaskInfoQueryVO queryVO);
}
