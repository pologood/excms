package cn.lonsun.site.contentModel.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.site.contentModel.internal.entity.ContentModelEO;
import cn.lonsun.site.contentModel.vo.ContentModelVO;

import java.util.List;

/**
 * Created by Administrator on 2017/9/26.
 */
public interface IContentModelSpecialDao extends IMockDao<ContentModelEO> {
    public List<ContentModelVO> getByCodes(Long siteId, String[] codes);

}
