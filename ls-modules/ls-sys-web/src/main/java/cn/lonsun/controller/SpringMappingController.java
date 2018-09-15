package cn.lonsun.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.CSVUtils;
import cn.lonsun.core.util.SpringContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * @auth zhongjun
 * @createDate 2018-08-06 10:31
 */
@Controller
@RequestMapping("springMapping")
public class SpringMappingController extends BaseController {

//    @ResponseBody
    @RequestMapping("getMapping")
    @RequiresPermissions("springMapping:getMapping")
    public void getMapping(HttpServletRequest request, HttpServletResponse response) throws Exception {
        RequestMappingHandlerMapping rmhp = SpringContextHolder.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> map = rmhp.getHandlerMethods();
        List<String[]> list = new ArrayList<String[]>();
        for(Map.Entry<RequestMappingInfo, HandlerMethod> info : map.entrySet()){
            Map<String, String> urlMap = getUrlMap(info);
            if(urlMap != null){
                list.add(new String[]{urlMap.get("url")+" = "+urlMap.get("baseCode")+":"+urlMap.get("permissionCode")});
            }
        }
        CSVUtils.download("1212312313.properties", new String[]{"url"}, list, response);
    }

    public Map<String, String> getUrlMap(Map.Entry<RequestMappingInfo, HandlerMethod> entity){
        Map<String, String> map = new HashMap<String, String>();
        RequestMapping classMapping = entity.getValue().getBeanType().getAnnotation(RequestMapping.class);
        RequestMapping methodMapping = entity.getValue().getMethodAnnotation(RequestMapping.class);
        if(methodMapping.value().length == 0){
            return null;
        }
        String url = getSetFirst(entity.getKey().getPatternsCondition().getPatterns());
        if(url.endsWith("/")){
            url = url.substring(0, url.length() - 1);
        }
        String baseUrl = replaceFirstAndEnd(classMapping.value()[0]);
        map.put("baseCode", formatUrl2Code(baseUrl,"APP_GLOBAL_"));
        map.put("bean", entity.getValue().getBeanType().getSimpleName());
        map.put("url", url);
        map.put("permissionCode", formatUrl2Code(methodMapping.value()[0]));
        return map;
    }

    public String getSetFirst(Set<String> set){
        for(String value : set){
            return value;
        }
        return "";
    }

    public String replaceFirstAndEnd(String url){
        if(url.startsWith("/")){
            url = url.substring(1, url.length());
        }
        if(url.endsWith("/")){
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public String formatUrl2Code(String url){
        return formatUrl2Code(url, "*");
    }

    public String formatUrl2Code(String url, String defaultCode){
        if(StringUtils.isEmpty(url)){
            return defaultCode;
        }
        url = replaceFirstAndEnd(url);
        url.replaceAll("//","/");
        return url.replaceAll("/","_");
    }

}
