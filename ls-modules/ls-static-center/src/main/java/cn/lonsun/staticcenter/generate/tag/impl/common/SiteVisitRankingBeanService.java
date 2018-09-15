package cn.lonsun.staticcenter.generate.tag.impl.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.system.sitechart.service.ISiteChartMainService;
import cn.lonsun.system.sitechart.vo.CountVisitVO;

import com.alibaba.fastjson.JSONObject;

/**
 * 获取网站访问排行 Created by houhd on 2016/8/26.
 */
@Component
public class SiteVisitRankingBeanService extends AbstractBeanService {
    @Resource
    private ISiteChartMainService siteChartMainService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Integer num = paramObj.getInteger(GenerateConstant.NUM);
        return siteChartMainService.getPvClick(num);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        List<CountVisitVO> visitList = (List<CountVisitVO>) resultObj;
        List<String> nameList = new ArrayList<String>();
        List<Long> numList = new ArrayList<Long>();
        for (CountVisitVO vl : visitList) {
            nameList.add("\"" + vl.getName() + "\"");
            numList.add(vl.getNum());
        }
        map.put("nameList", nameList);
        map.put("numList", numList);
        return map;
    }
}