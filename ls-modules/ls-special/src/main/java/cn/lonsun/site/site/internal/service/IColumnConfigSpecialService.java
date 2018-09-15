package cn.lonsun.site.site.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IColumnConfigSpecialService extends IMockService<ColumnConfigEO> {
    /**
     * 保存
     * @param columnVO
     */
    public Long saveEO(ColumnMgrEO columnVO);

    /**
     * 获取排序号
     * @param parentId
     * @return
     */
    public Integer getNewSortNum(Long parentId,boolean isCom);

    /**
     * 查询某个站点下内容模型code值为code的所有栏目
     * @param siteId
     * @param code
     * @return
     */
    public List<ColumnMgrEO> getColumnByContentModelCode(Long siteId, String code);

    /**
     * 获取某个栏目下所有的栏目
     * @param columnId
     * @return
     */
    public List<ColumnMgrEO> getAllColumnBySite(Long columnId);

    /**
     * 删除公共栏目
     * @param indicatorId
     */
    public void deleteComEO(Long indicatorId) ;
}
