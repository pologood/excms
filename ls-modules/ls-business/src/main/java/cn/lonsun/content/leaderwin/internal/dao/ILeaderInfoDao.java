package cn.lonsun.content.leaderwin.internal.dao;

import java.util.List;

import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface ILeaderInfoDao extends IBaseDao<LeaderInfoEO>{

	Pagination getPage(QueryVO query);

	List<LeaderInfoVO> getList(Long siteId,Long columnId);

	LeaderInfoEO getLeaderInfoByContentId(Long contentId);

	List<LeaderInfoVO> getLeaderInfoVOS(String code);

	List<LeaderInfoVO> getLeaderInfoVOSByTypeId(Long leaderTypeId);

	List<LeaderInfoVO> getLeaderInfo(Long siteId);

	//批量根据contentId物理删除
	void batchCompletelyDelete(Long[] contentIds);
}
