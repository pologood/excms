package cn.lonsun.content.ideacollect.internal.service;

import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.core.base.service.IBaseService;
import cn.lonsun.core.util.Pagination;

public interface ICollectIdeaService extends IBaseService<CollectIdeaEO>{

	void deleteByCollectInfoId(Long[] ids);

	Pagination getPage(IdeaQueryVO query);

	void save(CollectIdeaEO collectIdea);

	void delete(Long[] ids,Long collectInfoId);

}
