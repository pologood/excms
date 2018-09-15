package cn.lonsun.content.leaderwin.internal.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.leaderwin.internal.dao.ILeaderTypeDao;
import cn.lonsun.content.leaderwin.internal.entity.LeaderTypeEO;
import cn.lonsun.content.leaderwin.internal.service.ILeaderTypeService;
import cn.lonsun.content.leaderwin.vo.LeaderQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class LeaderTypeServiceImpl extends BaseService<LeaderTypeEO> implements ILeaderTypeService{

	@Autowired
	private ILeaderTypeDao leaderTypeDao;

	@Override
	public Pagination getPage(LeaderQueryVO query) {
		return leaderTypeDao.getPage(query);
	}

	@Override
	public Map<Long, String> getMap(Long siteId,Long columnId) {
		List<LeaderTypeEO> list= this.getList(siteId,columnId);
		Map<Long,String> map = null;
		if(list !=null && list.size()>0){
			map = new HashMap<Long,String>();
			for(LeaderTypeEO lt:list){
				map.put(lt.getLeaderTypeId(), lt.getTitle());
			}
		}
		return map;
	}

	@Override
	public List<LeaderTypeEO> getList(Long siteId,Long columnId) {
		return leaderTypeDao.getList(siteId,columnId);
	}

	@Override
	public Long getMaxSortNum(Long siteId, Long columnId) {
		return leaderTypeDao.getMaxSortNum(siteId,columnId);
	}
}
