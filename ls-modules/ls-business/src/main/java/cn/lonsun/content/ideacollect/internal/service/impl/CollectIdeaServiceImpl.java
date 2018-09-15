package cn.lonsun.content.ideacollect.internal.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.content.ideacollect.internal.dao.ICollectIdeaDao;
import cn.lonsun.content.ideacollect.internal.entity.CollectIdeaEO;
import cn.lonsun.content.ideacollect.internal.entity.CollectInfoEO;
import cn.lonsun.content.ideacollect.internal.service.ICollectIdeaService;
import cn.lonsun.content.ideacollect.internal.service.ICollectInfoService;
import cn.lonsun.content.ideacollect.vo.IdeaQueryVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;

@Service
public class CollectIdeaServiceImpl extends BaseService<CollectIdeaEO> implements ICollectIdeaService{

	@Autowired
	private ICollectIdeaDao collectIdeaDao;
	
	@Autowired
	private ICollectInfoService collectInfoService;

	@Override
	public void deleteByCollectInfoId(Long[] ids) {
		collectIdeaDao.deleteByCollectInfoId(ids);
	}

	@Override
	public Pagination getPage(IdeaQueryVO query) {
		return collectIdeaDao.getPage(query);
	}

	@Override
	public void save(CollectIdeaEO collectIdea) {
		collectIdeaDao.save(collectIdea);
		if(collectIdea !=null && collectIdea.getCollectInfoId() != null){
			CollectInfoEO info = collectInfoService.getEntity(CollectInfoEO.class, collectIdea.getCollectInfoId());
			if(info !=null ){
			    Long count = info.getIdeaCount() +1;
				info.setIdeaCount(count);
				collectInfoService.updateEntity(info);
			}
		}
	}

	@Override
	public void delete(Long[] ids,Long collectInfoId) {
		int length =  ids.length;
		collectIdeaDao.delete(CollectIdeaEO.class,ids);
		CollectInfoEO info = collectInfoService.getEntity(CollectInfoEO.class, collectInfoId);
		if(info !=null ){
		    Long count = info.getIdeaCount() - length;
			info.setIdeaCount(count);
			collectInfoService.updateEntity(info);
		}
	}
	
}
