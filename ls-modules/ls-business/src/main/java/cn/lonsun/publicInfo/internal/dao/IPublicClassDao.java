package cn.lonsun.publicInfo.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.entity.PublicContentEO;
import cn.lonsun.publicInfo.vo.PublicClassMobileVO;
import cn.lonsun.publicInfo.vo.PublicContentRetrievalVO;

import java.util.List;

/**
 * Created by zx on 2016-6-27.
 */
public interface IPublicClassDao extends IMockDao<PublicContentEO> {

    public List<PublicClassMobileVO> getPublicClassify(PublicContentRetrievalVO vo);

    public List<PublicClassEO> getChildClass(Long pid,Long siteId);
}
