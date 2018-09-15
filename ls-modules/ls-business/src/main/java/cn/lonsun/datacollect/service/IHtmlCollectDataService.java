package cn.lonsun.datacollect.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.datacollect.entity.HtmlCollectDataEO;
import cn.lonsun.datacollect.vo.CollectPageVO;

import java.util.List;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2016-1-21 14:31
 */
public interface IHtmlCollectDataService extends IMockService<HtmlCollectDataEO> {

    /**
     * 分页查询采集数据
     * @param vo
     * @return
     */
    public Pagination getPageEOs(CollectPageVO vo);

    /**
     * 保存采集数据
     * @param map
     */
    public void saveData(Map<String,Object> map);

    /**
     * 根据名称查询
     * @param taskId
     * @param title
     * @return
     */
    public List<HtmlCollectDataEO> getEntityByName(Long taskId, String title);

    /**
     * @param taskId
     */
    public void deleteByTaskId(Long taskId);

    /**
     * 引用数据到栏目中
     * @param columnId
     * @param ids
     */
    public String quoteData(Long columnId,Long cSiteId,Long[] ids);
}
