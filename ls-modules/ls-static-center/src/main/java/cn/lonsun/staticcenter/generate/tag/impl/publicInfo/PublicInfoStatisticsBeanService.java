package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.publicInfo.internal.service.IPublicInfoStatisticsService;
import cn.lonsun.content.publicInfo.vo.PublicApplyStatisticsVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lonsun on 2016-9-23.
 * 信息公开统计标签处理
 */
@Component
public class PublicInfoStatisticsBeanService extends AbstractBeanService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private IPublicInfoStatisticsService publicInfoStatisticsService;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Map<String,Object> result = new HashMap<String, Object>();

        Context context = ContextHolder.getContext();
        Long siteId = context.getSiteId();
        if(AppUtil.isEmpty(siteId)){
            siteId = Long.parseLong(context.getParamMap().get("siteId"));
        }
        Long organId = ObjectUtils.defaultIfNull(paramObj.getLong(GenerateConstant.ORGAN_ID), context.getColumnId());
        AssertUtil.isEmpty(organId, "单位id不能为空！");
        AssertUtil.isEmpty(siteId, "站点id不能为空！");
        logger.info("siteId=" + siteId);

        PublicApplyStatisticsVO vo = publicInfoStatisticsService.getStatistics(organId,siteId);
        result.put("data",vo);
        return result;
    }
}
