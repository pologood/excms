package cn.lonsun.publicInfo.internal.dao;

import java.util.List;

import cn.lonsun.core.base.dao.IMockDao;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.publicInfo.internal.entity.PublicLeadersEO;
import cn.lonsun.publicInfo.vo.PublicLeadersQueryVO;

/**
 * TODO <br/>
 * 
 * @date 2016年9月19日 <br/>
 * @author liukun <br/>
 * @version v1.0 <br/>
 */
public interface IPublicLeadersDao extends IMockDao<PublicLeadersEO> {

    Pagination getPagination(PublicLeadersQueryVO queryVO);

    List<PublicLeadersEO> getPublicLeadersList(PublicLeadersQueryVO queryVO);
}