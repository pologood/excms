package cn.lonsun.site.site.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IColumnConfigSpecialDao extends IMockDao<ColumnConfigEO> {

    /**
     * 获取序号
     * @param parentId
     * @return
     */
    public Integer getNewSortNum(Long parentId,boolean isCom) ;

    /**
     * 获取某个站点下内容模型code值为code的栏目
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

}
