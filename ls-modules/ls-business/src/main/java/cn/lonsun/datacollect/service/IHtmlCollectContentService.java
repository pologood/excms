package cn.lonsun.datacollect.service;

import java.util.List;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.HtmlCollectContentEO;
import cn.lonsun.datacollect.vo.CollectPageVO;
import cn.lonsun.datacollect.vo.ColumnVO;

/**
 * @author gu.fei
 * @version 2016-1-21 14:31
 */
public interface IHtmlCollectContentService extends IMockService<HtmlCollectContentEO> {

    /**
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * 根据任务ID获取配置的采集内容信息
     * @param taskId
     * @return
     */
    public List<HtmlCollectContentEO> getByTaskId(Long taskId);

    /**
     * 保存采集任务
     * @param eo
     */
    public void saveEO(HtmlCollectContentEO eo);

    /**
     * 更新采集任务
     * @param eo
     */
    public void updateEO(HtmlCollectContentEO eo);

    /**
     * 删除采集任务
     * @param ids
     */
    public void deleteEOs(Long[] ids);

    /**
     * 根据表名称获取字段
     * @param tableName
     * @return
     */
    public List<ColumnVO> getColumns(String tableName);

}
