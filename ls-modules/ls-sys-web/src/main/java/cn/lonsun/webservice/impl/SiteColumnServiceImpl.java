package cn.lonsun.webservice.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.indicator.internal.service.IIndicatorService;
import cn.lonsun.webservice.SiteColumnService;
import cn.lonsun.webservice.to.WebServiceTO;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liuk on 2017年1月5日.
 */
@Service("SiteColumnServiceImpl_webservice")
public class SiteColumnServiceImpl implements SiteColumnService {

    @Autowired
    private IIndicatorService indicatorService;

    @Override
    public WebServiceTO getSiteColumnList(Long siteId) {
        WebServiceTO to = new WebServiceTO();
        if (null == siteId) {
            return to.error(WebServiceTO.ErrorCode.ArgumentsError, "站点id不能为空");
        }
        Map<String, Object> map2 = new HashMap<String, Object>();
        map2.put("siteId", siteId);
        map2.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        try {
            List<IndicatorEO> indicat = indicatorService.getEntities(IndicatorEO.class, map2);
            return to.success(JSON.toJSONString(indicat), "操作成功！");
        }catch (Exception e) {
            e.printStackTrace();
            return to.error(WebServiceTO.ErrorCode.SystemError, "系统异常，请稍后重试！");
        }
    }
}
