package cn.lonsun.staticcenter.controller;

import cn.lonsun.base.util.HttpClientUtils;
import cn.lonsun.core.base.controller.BaseController;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by fth on 2017/2/23.
 */
@Controller
@RequestMapping(value = "/httpService")
public class HttpServiceController extends BaseController {

    /**
     * 获取外平台http接口数据
     *
     * @param paramsMap
     * @return
     */
    @ResponseBody
    @RequestMapping("get")
    public Object getExternalPlatformResult(@RequestParam Map<String, String> paramsMap) {
        //去除ajax请求所带参数
        paramsMap.remove("IsAjax");
        paramsMap.remove("dataType");
        long now = System.currentTimeMillis();
        String url = paramsMap.remove("url");
        String result = HttpClientUtils.get(url, paramsMap);
        return getObject(JSON.parseObject(result));
    }
}
