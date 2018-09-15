package cn.lonsun.staticcenter.generate.tag.impl.common;

import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.CommentsVO;

import com.alibaba.fastjson.JSONObject;

@Component
public class CommentsFormBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        CommentsVO vo = new CommentsVO();
        Long contentId = context.getContentId();
        vo.setContentId(contentId);
        Long columnId = context.getColumnId();
        vo.setColumnId(columnId);
        Long siteId = context.getSiteId();
        BaseContentEO _eo = CacheHandler.getEntity(BaseContentEO.class, contentId);
        if(_eo!=null){
	        vo.setSiteId(siteId == null ? _eo.getSiteId() : siteId);
	        Integer isAllowComments = 0;
	        if (_eo.getIsAllowComments() == 1) {
	            isAllowComments = 1;
	        }
	        vo.setContentTitle(_eo.getTitle());
	        vo.setIsAllowComment(isAllowComments);
        }else{
        	vo=null;
        }
        return vo;
    }
}