package cn.lonsun.staticcenter.generate.tag.impl.picnews;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentPicEO;
import cn.lonsun.content.internal.service.IContentPicService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;

import com.alibaba.fastjson.JSONObject;

@Component
public class PictureContentBeanService extends AbstractBeanService {

    @Autowired
    private IContentPicService contentPicService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long contentId = context.getContentId();
        return CacheHandler.getEntity(BaseContentEO.class, contentId);
    }

    @Override
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        BaseContentEO eo = (BaseContentEO) resultObj;
        eo.setArticle(MongoUtil.queryById(eo.getId()));
        List<ContentPicEO> picList = contentPicService.getPicsList(eo.getId());
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        map.put("PicList", picList);
        return map;
    }
}