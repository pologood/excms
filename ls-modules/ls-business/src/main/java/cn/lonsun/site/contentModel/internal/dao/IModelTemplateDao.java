package cn.lonsun.site.contentModel.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */

public interface IModelTemplateDao extends IMockDao<ModelTemplateEO> {
    public Pagination getPage(Long pageIndex, Integer pageSize, Long modelId);

    public List<ModelTemplateEO> getFirstModelType(Long modelId);

    /**
     * 根据栏目类型获取第一个内容模型
     *
     * @param columnTypeCode
     * @return
     */
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode);
}
