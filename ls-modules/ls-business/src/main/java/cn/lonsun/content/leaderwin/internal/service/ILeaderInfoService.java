package cn.lonsun.content.leaderwin.internal.service;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.leaderwin.internal.entity.LeaderInfoEO;
import cn.lonsun.content.leaderwin.vo.LeaderInfoVO;
import cn.lonsun.content.vo.QueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.system.datadictionary.vo.DataDictVO;

import java.util.List;

public interface ILeaderInfoService extends IBaseService<LeaderInfoEO>{

	Pagination getPage(QueryVO query);

	void delete(Long[] ids,Long[] contentIds);

	LeaderInfoVO getLeaderInfoVO(Long infoId);
	
	/**
	 * 彻底删除接口
	 * @param contents
	 */
	void deleteByContentIds(BaseContentEO content);

	//批量根据contentId物理删除
	void batchCompletelyDelete(Long[] contentIds);

	List<LeaderInfoVO> getLeaderInfoVOS(String code);

	List<LeaderInfoVO> getLeaderInfoVOSByTypeId(Long leaderTypeId);

	BaseContentEO save(LeaderInfoVO leaderInfoVO);

	List<LeaderInfoVO> getLeaderInfo(Long siteId);

	List<LeaderInfoVO> getList(Long siteId, Long columnId);
}
