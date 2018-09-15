package cn.lonsun.staticcenter.generate.tag.impl.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.lonsun.content.contentcorrection.internal.service.IContentCorrectionService;
import cn.lonsun.content.contentcorrection.vo.CorrectionPageVO;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;

import com.alibaba.fastjson.JSONObject;

/**
 * @author gu.fei
 * @version 2016-5-26 13:49
 */
@Component
public class CorrectionListBeanService extends AbstractBeanService {

    @Autowired
    private IContentCorrectionService contentCorrectionService;

    @Override
    public Object getObject(JSONObject paramObj) {
        //分页条数
        Integer pageSize = paramObj.getInteger("pageSize");

        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        //页码
        Long pageIndex = context.getPageIndex();
        CorrectionPageVO pageVO = new CorrectionPageVO();
        pageVO.setSiteId(siteId);
        pageVO.setPageSize(pageSize);
        pageVO.setPageIndex(pageIndex - 1);
        Pagination page = contentCorrectionService.getPage(pageVO);
        return page;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Map<String, Object> map = new HashMap<String, Object>();
        String linkPrefix = context.getPath();
        map.put("linkPrefix",linkPrefix);
        map.put("siteId",context.getSiteId());
        return map;
    }
}

