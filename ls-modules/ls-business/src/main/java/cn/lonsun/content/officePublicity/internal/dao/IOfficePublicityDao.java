package cn.lonsun.content.officePublicity.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsOfficePublicityEO;
import cn.lonsun.net.service.entity.vo.OfficePublicityQueryVO;

/**
 * Created by huangxx on 2017/2/24.
 */

public interface IOfficePublicityDao extends IMockDao<CmsOfficePublicityEO>{

    public Pagination getPage(OfficePublicityQueryVO queryVO);
}
