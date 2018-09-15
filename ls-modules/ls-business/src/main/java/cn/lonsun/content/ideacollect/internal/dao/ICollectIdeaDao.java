package cn.lonsun.content.ideacollect.internal.dao;

import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.core.base.dao.IBaseDao;
import cn.lonsun.core.util.Pagination;

public interface ICollectIdeaDao extends IBaseDao<CollectIdeaEO>{

	void deleteByCollectInfoId(Long[] ids);

	Pagination getPage(IdeaQueryVO query);

}
