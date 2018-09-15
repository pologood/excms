package cn.lonsun.site.contentModel.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IModelTemplateSpecialDao extends IMockDao<ModelTemplateEO> {

    /**
     * 根据栏目类型获取第一个内容模型
     *
     * @param columnTypeCode
     * @return
     */
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode);
}
