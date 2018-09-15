package cn.lonsun.projectInformation.internal.dao;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.projectInformation.internal.entity.ProjectInformationEO;
import cn.lonsun.projectInformation.vo.ProjectInformationQueryVO;

import java.util.List;

/**
 * Created by huangxx on 2017/3/3.
 */
public interface IProjectInformationDao extends IMockDao<ProjectInformationEO>{

    public Pagination getPageEntities(ProjectInformationQueryVO queryVO);

    public List<ProjectInformationEO> getPageEntities(ProjectInformationQueryVO queryVO, Long[] ids);

}
