package cn.lonsun.staticcenter.generate.tag.impl.ordinarypage;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IOrdinaryPageService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.MongoUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 普通页面栏目页<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-3-3<br/>
 */
@Component
public class OrdinaryPageBeanService extends AbstractBeanService {

    @Resource
    private IOrdinaryPageService pageService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (columnId == null) {
            columnId = context.getColumnId();
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("columnId", columnId);
        map.put("siteId", context.getSiteId());
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return pageService.getEntity(BaseContentEO.class, map);
    }

    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        BaseContentEO eo = (BaseContentEO) resultObj;
        Boolean showContent = paramObj.getBoolean("showContent");
        System.out.println(showContent);
        if (null != showContent && !showContent) {//当为false时显示显示标题
            return super.objToStr(content, resultObj, paramObj);
        }
        return MongoUtil.queryById(eo.getId());
    }
}