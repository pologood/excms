package cn.lonsun.statictask.internal.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.statictask.internal.entity.StaticTaskEO;

/**
 * 生成静态任务业务层<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */

public interface IStaticTaskService extends IMockService<StaticTaskEO> {
    public Pagination getPage(Long pageIndex, Integer pageSize, Long userId);

    public List<StaticTaskEO> clearTask();

    /**
     * 删除初始化和正在进行的任务
     *
     * @author fangtinghua
     */
    public void deleteInitDoingTask();

    /**
     * 更新所有初始化、进行中的任务为终止
     *
     * @author fangtinghua
     */
    public void updateInitDoingToOver();

    /**
     * 检测是否有重复的任务
     */
    public List<StaticTaskEO> checkTask(Long siteId, Long columnID, Long scope, Long source);
}