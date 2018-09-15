package cn.lonsun.content.officePublicity.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsOfficePublicityEO;
import cn.lonsun.net.service.entity.vo.OfficePublicityQueryVO;

/**
 * Created by huangxx on 2017/2/24.
 */
public interface IOfficePublicityService extends IMockService<CmsOfficePublicityEO> {

    public Pagination getPage(OfficePublicityQueryVO queryVO);

    public void saveEO(CmsOfficePublicityEO eo);

    public void updateEO(CmsOfficePublicityEO eo);

    public void deleteEO(Long[] ids);
}
