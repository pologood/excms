package cn.lonsun.staticcenter.generate.tag.impl.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.vo.BaseContentVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;

import com.alibaba.fastjson.JSONObject;

/**
 * Created by houhd on 2016/8/29.
 */
@Component
public class SiteArticlesRankingBeanService extends AbstractBeanService {

    @Resource
    private ISiteChartMainService siteChartMainService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long siteId = ObjectUtils.defaultIfNull(paramObj.getLong("siteId"), context.getSiteId());
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        BaseContentVO baseContentVO = new BaseContentVO();
        baseContentVO.setSiteId(siteId);
        baseContentVO.setTypeCode(BaseContentEO.TypeCode.articleNews.toString());
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        return siteChartMainService.getPvList(baseContentVO, num);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        List<String> nameList = new ArrayList<String>();
        List<Long> numList = new ArrayList<Long>();
        List<BaseContentVO> list = (List<BaseContentVO>) resultObj;
        for (BaseContentVO bc : list) {
            nameList.add("\"" + bc.getOrganName() + "\"");
            numList.add(bc.getCounts());
        }
        map.put("nameList", nameList);
        map.put("numList", numList);
        return map;
    }
}