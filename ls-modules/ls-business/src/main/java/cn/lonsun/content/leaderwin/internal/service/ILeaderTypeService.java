package cn.lonsun.content.leaderwin.internal.service;

import java.util.List;
import java.util.Map;

import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.vo.LeaderQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface ILeaderTypeService extends IBaseService<LeaderTypeEO>{

	Pagination getPage(LeaderQueryVO query);

	Map<Long, String> getMap(Long siteId,Long columnId);
	
	List<LeaderTypeEO> getList(Long siteId,Long columnId);

	Long getMaxSortNum(Long siteId, Long columnId);
}
