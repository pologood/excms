package cn.lonsun.site.contentModel.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IContentModelSpecialService  extends IMockService<ContentModelEO> {

    //根据栏目CODE获取模型
    public List<ContentModelVO> getByCodes(Long siteId, String[] codes);

    public ContentModelEO getByCode(String code);

    public String delEO(Long modelId);

    public ContentModelEO saveEO(ContentModelVO vo);
}
