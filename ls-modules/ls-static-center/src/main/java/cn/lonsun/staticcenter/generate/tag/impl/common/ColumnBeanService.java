package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.generate.util.VelocityUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ColumnBeanService extends AbstractBeanService {

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        return paramObj;
    }

    /**
     * 生成页面默认逻辑
     *
     * @throws GenerateException
     * @see cn.lonsun.staticcenter.generate.tag.BeanService#objToStr(java.lang.String,
     *      java.lang.Object, com.alibaba.fastjson.JSONObject)
     */
    @Override
    public String objToStr(String content, Object resultObj, JSONObject paramObj) throws GenerateException {
        // 当前上下文
        Context context = ContextHolder.getContext();
        // 放入当前栏目信息，并且只有当生成栏目页时才放入
        Long columnId = paramObj.getLong("columnId");
        //如果为空，从context中获取参数
        if(columnId == null || columnId == 0){
            Map<String, String> paramMap = context.getParamMap();
            if(paramMap.containsKey("columnId")){
                columnId = Long.valueOf(paramMap.get("columnId"));
            }else{
                columnId = context.getColumnId();
            }
        }
        ColumnMgrEO column = CacheHandler.getEntity(ColumnMgrEO.class, columnId);
        if(column == null){
            return "<a>不存在的栏目</a>";
        }

        // 获取数据
        Map<String, Object> map = this.doProcess(resultObj, paramObj);
        map.put("column", column);
        map.put("resultObj", column);
        map.put("paramObj", paramObj);
        map.put("context", context);
        String file = paramObj.getString(GenerateConstant.FILE);
        //如果没有指定vm
        if(!StringUtils.isEmpty(file)){
            return VelocityUtil.mergeTemplate(file, map);// 判断如果自定义页面了，则使用自定义模板文件
        }
        String href = paramObj.getString("href");
        href = StringUtils.isEmpty(href)? column.getUrlPath()+"/index.html":href;
        String attributes = paramObj.getString("attributes");
        attributes = StringUtils.isEmpty(attributes)? "":attributes;
        StringBuilder dom = new StringBuilder();
        dom.append("<a href = '/").append(href)
                .append("' title ='").append(column.getName())
                .append("' ").append(attributes)
                .append(" >");
        if(StringUtils.isNotEmpty(StringUtils.trim(content))){
            if(content.contains("$name")){
                String text = content.replaceAll("\\$name", column.getName());
                dom.append(text);
            }
        }else{
            dom.append(column.getName());
        }
        dom.append("</a>");
        return dom.toString();
    }
}
