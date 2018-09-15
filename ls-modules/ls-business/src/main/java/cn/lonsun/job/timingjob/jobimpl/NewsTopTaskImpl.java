package cn.lonsun.job.timingjob.jobimpl;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.job.service.ISchedulerService;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;

public class NewsTopTaskImpl extends ISchedulerService{

	private static IBaseContentService baseContentService=SpringContextHolder.getBean("baseContentService");
	
	@Override
	public void execute(String json) {
		Long id=Long.parseLong(json);
		baseContentService.changeTopStatus(new Long[]{id}, 0);
		BaseContentEO _eo = baseContentService.getEntity(BaseContentEO.class, id);
		CacheHandler.saveOrUpdate(BaseContentEO.class, _eo);

		if(_eo.getIsPublish()==1){
			MessageSenderUtil.publishContent(
					new MessageStaticEO(_eo.getSiteId(), _eo.getColumnId(), new Long[]{id})
							.setType(MessageEnum.PUBLISH.value()).setUserId(_eo.getUpdateUserId()),1);
		}
	}

}
