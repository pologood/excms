package cn.lonsun.staticcenter.generate.tag.impl.common;

import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * @author Hewbing
 * @ClassName: CorrectionFormBeanService
 * @Description: 内容纠错表单
 * @date 2016年1月26日 下午2:36:05
 */
@Component
public class CorrectionFormBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        /*Long contentId = context.getContentId();
		BaseContentEO eo = CacheHandler.getEntity(BaseContentEO.class, contentId);

		if(null != eo){
			Integer isAllowComments = 0;
			if (eo.getIsAllowComments() == 1) {
				isAllowComments = 1;
			}
			paramObj.put("isAllowComments",isAllowComments);
		}*/

        return siteId;
    }
}
