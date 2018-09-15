package cn.lonsun.base;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.enums.SystemCodes;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.webservice.indicator.IIndicatorService;
import cn.lonsun.webservice.vo.indicator.IndicatorVO;

/**
 * Created by zhusy on 2016-7-18.
 */
public class ProcessEngineConfig {

    private static String processEngineHost;

    public static String getProcessEngineHost(){

        if(AppUtil.isEmpty(processEngineHost)){
            IIndicatorService indirectIndicatorService = SpringContextHolder.getBean("indirectIndicatorService");
            IndicatorVO[] indicatorVOs = indirectIndicatorService.getAllApps();
            for (IndicatorVO indicatorVO : indicatorVOs) {
                if(SystemCodes.processEngine.toString().equals(indicatorVO.getSystemCode())){
                    processEngineHost = indicatorVO.getHost();
                }
            }
        }

        return processEngineHost;
    }


}
