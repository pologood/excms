package cn.lonsun.projectInformation.internal.service;

import cn.lonsun.core.base.service.IMockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;

import java.util.List;

/**
 * Created by huangxx on 2017/3/3.
 */
public interface IProjectInformationService extends IMockService<ProjectInformationEO>{

    public Pagination getPageEntities(ProjectInformationQueryVO queryVO);

    public List<ProjectInformationEO> getPageEntities(ProjectInformationQueryVO queryVO, Long[] ids);

    public void saveEO(ProjectInformationEO eo);

    public void updateEO(ProjectInformationEO eo);

    public void deleteEO(Long[] ids);

}
