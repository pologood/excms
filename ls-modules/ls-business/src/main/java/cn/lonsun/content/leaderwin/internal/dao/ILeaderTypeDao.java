package cn.lonsun.content.leaderwin.internal.dao;

import java.util.List;

import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.vo.LeaderQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface ILeaderTypeDao extends IBaseDao<LeaderTypeEO>{

	Pagination getPage(LeaderQueryVO query);

	List<LeaderTypeEO> getList(Long siteId,Long columnId);

	Long getMaxSortNum(Long siteId, Long columnId);
	
}
