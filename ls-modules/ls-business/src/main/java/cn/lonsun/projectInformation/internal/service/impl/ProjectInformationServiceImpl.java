package cn.lonsun.projectInformation.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.projectInformation.internal.dao.IProjectInformationDao;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.internal.service.IProjectInformationService;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by huangxx on 2017/3/3.
 */
@Service
public class ProjectInformationServiceImpl extends MockService<ProjectInformationEO> implements IProjectInformationService {

    @Autowired
    private IProjectInformationDao projectInformationDao;

    @Autowired
    private IBaseContentService baseContentService;

    @Override
    public Pagination getPageEntities(ProjectInformationQueryVO queryVO) {
        return projectInformationDao.getPageEntities(queryVO);
    }

    @Override
    public List<ProjectInformationEO> getPageEntities(ProjectInformationQueryVO queryVO, Long[] ids) {
        return projectInformationDao.getPageEntities(queryVO,ids);
    }

    @Override
    public void saveEO(ProjectInformationEO eo) {
        if(null != eo){
            Long siteId = null != eo.getSiteId()?eo.getSiteId(): LoginPersonUtil.getSiteId();

            BaseContentEO contentEO = new BaseContentEO();
            contentEO.setTitle(eo.getProjectName());
            contentEO.setColumnId(eo.getColumnId());
            contentEO.setSiteId(eo.getSiteId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
            contentEO.setTypeCode(eo.getInformationType());

            //id为内容模型的ID
            Long id = baseContentService.saveEntity(contentEO);
            CacheHandler.saveOrUpdate(BaseContentEO.class,contentEO);

            eo.setContentId(id);
            eo.setSiteId(siteId);
            this.saveEntity(eo);
        }
    }

    @Override
    public void updateEO(ProjectInformationEO eo) {
        if(null != eo) {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);
            this.updateEntity(eo);

            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,eo.getContentId());
            if(null != contentEO) {
                contentEO.setTitle(eo.getProjectName());
                contentEO.setColumnId(eo.getColumnId());
                contentEO.setSiteId(siteId);
                contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
                contentEO.setTypeCode(eo.getInformationType());

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
            ProjectInformationEO projectInformationEO = this.getEntity(ProjectInformationEO.class,id);
            BaseContentEO contentEO = baseContentService.getEntity(BaseContentEO.class,projectInformationEO.getContentId());
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Removed.toString());
            baseContentService.updateEntity(contentEO);
            CacheHandler.delete(BaseContentEO.class, contentEO);
            this.delete(projectInformationEO);

        }
    }

}
