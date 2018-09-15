package cn.lonsun.content.officePublicity.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.officePublicity.internal.dao.IOfficePublicityDao;
import cn.lonsun.content.officePublicity.internal.service.IOfficePublicityService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsOfficePublicityEO;
import cn.lonsun.net.service.entity.vo.OfficePublicityQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by huangxx on 2017/2/24.
 */
@Service("officePublicityService")
public class OfficePublicityServiceImpl extends MockService<CmsOfficePublicityEO> implements IOfficePublicityService {

    @Autowired
    private IOfficePublicityDao officePublicityDao;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Pagination getPage(OfficePublicityQueryVO queryVO) {
        return officePublicityDao.getPage(queryVO);
    }

    @Override
    public void saveEO(CmsOfficePublicityEO eo) {

        if(null != eo) {
            Long siteId = null != eo.getSiteId()?eo.getSiteId(): LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);

            BaseContentEO contentEO = new BaseContentEO();
            contentEO.setTitle(eo.getAcceptanceItem());
            contentEO.setColumnId(eo.getColumnId());
            contentEO.setSiteId(eo.getSiteId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
            contentEO.setTypeCode(eo.getTypeCode());

            //id为内容模型的ID
            Long id = baseContentService.saveEntity(contentEO);
            CacheHandler.saveOrUpdate(BaseContentEO.class,contentEO);

            eo.setContentId(id);
            eo.setCheckIn(1);
            eo.setInputDate(new Date());
            this.saveEntity(eo);
        }

    }

    @Override
    public void updateEO(CmsOfficePublicityEO eo) {
        if(null != eo) {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);

            eo.setInputDate(new Date());
            eo.setCheckIn(1);
            this.updateEntity(eo);

            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,eo.getContentId());
            if(null != contentEO) {
                contentEO.setTitle(eo.getAcceptanceItem());
                contentEO.setColumnId(eo.getColumnId());
                contentEO.setSiteId(siteId);
                contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
                contentEO.setTypeCode(eo.getTypeCode());

            baseContentService.updateEntity(contentEO);
            CacheHandler.saveOrUpdate(BaseContentEO.class, contentEO);
            } else {
                throw new BaseRunTimeException("数据错误！");
            }

        }
    }

    @Override
    public void deleteEO(Long[] ids) {
        for(Long id : ids) {
            CmsOfficePublicityEO officePublicityEO = this.getEntity(CmsOfficePublicityEO.class,id);
            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,officePublicityEO.getContentId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            baseContentService.updateEntity(contentEO);
            CacheHandler.delete(BaseContentEO.class, contentEO);

            this.delete(officePublicityEO);

        }
    }


}
