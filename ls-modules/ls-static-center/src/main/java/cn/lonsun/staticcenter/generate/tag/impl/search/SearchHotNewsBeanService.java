package cn.lonsun.staticcenter.generate.tag.impl.search;


import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.heatAnalysis.service.IColumnNewsHeatService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 搜索界面的热点数据
 * @author zhongjun
 */
@Component
public class SearchHotNewsBeanService extends AbstractBeanService {

    @Autowired
    private IColumnNewsHeatService columnNewsHeatService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = paramObj.getLong("siteId");
        siteId = siteId != null ? siteId : Long.valueOf(context.getParamMap().get("siteId"));
        Integer num = paramObj.getInteger("num");
        num = num != null ? num : Integer.valueOf(context.getParamMap().get("num"));
        //热点新闻
        ContentPageVO contentPageVO = new ContentPageVO();
        contentPageVO.setSiteId(siteId);
        contentPageVO.setPageSize(num);
        contentPageVO.setTypeCodes(new String[]{
                BaseContentEO.TypeCode.articleNews.toString(),
                BaseContentEO.TypeCode.pictureNews.toString(),
                BaseContentEO.TypeCode.videoNews.toString()
        });
        List<BaseContentEO> contentEOs = columnNewsHeatService.getNewsHeatList(contentPageVO);
        for (BaseContentEO baseContentEO : contentEOs) {
            if (null != baseContentEO.getRedirectLink()) {
                baseContentEO.setLink(baseContentEO.getRedirectLink());
            } else {
                String link = PathUtil.getLinkPath(baseContentEO.getColumnId(), baseContentEO.getId());
                baseContentEO.setLink(link);
            }
        }
        return contentEOs;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        String target = paramObj.getString("target");
        target = target != null ? target : context.getParamMap().get("target");
        target = target != null ? target : "_blank";
        Integer length = paramObj.getInteger("length");
        length = length != null ? length : Integer.valueOf(context.getParamMap().get("length"));
        length = length != null ? length : 20;
        map.put("target", target);
        map.put("length", length);
        return map;
    }
}
