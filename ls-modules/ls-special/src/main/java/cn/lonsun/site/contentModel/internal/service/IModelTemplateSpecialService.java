package cn.lonsun.site.contentModel.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IModelTemplateSpecialService extends IMockService<ModelTemplateEO> {

    /**
     * 根据栏目类型获取第一个内容模型
     *
     * @param columnTypeCode
     * @return
     */
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode);

    public void delTpls(Long modelId);

    public void saveVO(ContentModelVO vo, Long id);
}
