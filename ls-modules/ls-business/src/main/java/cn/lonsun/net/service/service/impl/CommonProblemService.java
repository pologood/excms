package cn.lonsun.net.service.service.impl;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.dao.ICommonProblemDao;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.net.service.service.ICommonProblemService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author gu.fei
 * @version 2015-11-18 13:46
 */
@Service
public class CommonProblemService extends MockService<CmsCommonProblemEO> implements ICommonProblemService {

    @Autowired
    private ICommonProblemDao commonProblemDao;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Pagination getPageEntities(ParamDto dto) {
        return commonProblemDao.getPageEntities(dto);
    }

    @Override
    public void saveEO(CmsCommonProblemEO eo) {
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);

        BaseContentEO contentEO = new BaseContentEO();
        contentEO.setTitle(eo.getTitle());
        contentEO.setColumnId(eo.getColumnId());
        contentEO.setSiteId(siteId);
        contentEO.setIsPublish(eo.getPublish());
        contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
        contentEO.setTypeCode(BaseContentEO.TypeCode.commonProblem.toString());
        if (contentEO.getIsPublish() == 1) {
            contentEO.setPublishDate(new Date());
        }

        Long contentId = baseContentService.saveEntity(contentEO);
        eo.setContentId(contentId);

        this.saveEntity(eo);
    }

    @Override
    public void updateEO(CmsCommonProblemEO eo) {
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        this.updateEntity(eo);

        BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
        contentEO.setTitle(eo.getTitle());
        contentEO.setIsPublish(eo.getPublish());

        if (contentEO.getPublishDate() == null && contentEO.getIsPublish() == 1) {
            contentEO.setPublishDate(new Date());
        }

        baseContentService.updateEntity(contentEO);

        CmsCommonProblemEO oeo = this.getEntity(CmsCommonProblemEO.class, eo.getId());
        oeo.setTitle(eo.getTitle());
        oeo.setContent(eo.getContent());
        oeo.setPublish(eo.getPublish());
        this.updateEntity(oeo);
    }

    @Override
    public Object publish(Long[] ids, Long publish) {
        Long[] contenIds = new Long[ids.length];
        int count = 0;
        for (Long id : ids) {
            CmsCommonProblemEO problemEO = this.getEntity(CmsCommonProblemEO.class, id);
            contenIds[count++] = problemEO.getContentId();
            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, problemEO.getContentId());
            if (publish != null && publish == 1) {
                if (contentEO.getPublishDate() == null) {
                    contentEO.setPublishDate(new Date());
                    baseContentService.updateEntity(contentEO);
                }
            }
            baseContentService.changePublish(new ContentPageVO(null, null, Integer.parseInt(String.valueOf(publish)), contenIds, null));
        }
        return commonProblemDao.publish(ids, publish);
    }

    @Override
    public void deleteEO(Long[] ids) {
        for (Long id : ids) {
            CmsCommonProblemEO eo = this.getEntity(CmsCommonProblemEO.class, id);
            eo.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            commonProblemDao.update(eo);

            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class, eo.getContentId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            baseContentService.updateEntity(contentEO);
        }
    }
}
