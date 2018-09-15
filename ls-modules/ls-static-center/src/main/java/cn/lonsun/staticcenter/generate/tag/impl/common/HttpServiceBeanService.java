package cn.lonsun.staticcenter.generate.tag.impl.common;

import cn.lonsun.base.util.HttpClientUtils;
import cn.lonsun.cache.jedis.JedisCache;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

/**
 * http接口调用
 * Created by fth on 2017/2/14.
 */
@Service
public class HttpServiceBeanService extends AbstractBeanService {

    @Autowired
    private JedisCache jedisCache;

    private static final String cacheKey = "Tag_HttpServiceCache_";

    @Override
    public Object getObject(JSONObject paramObj) {
        String domain = paramObj.getString("domain");// 域名
        AssertUtil.isEmpty(domain, "http请求domain参数不能为空！");
        String params = paramObj.getString("params"); // 参数
        if (StringUtils.isNotEmpty(params)) {
            domain += "?" + params;// 请求的地址
        }
        domain = domain.replace("&amp;", "&");// 替换请求参数
        boolean refresh = paramObj.getBooleanValue("refresh");// 是否强制刷新
        domain = domain.contains("?")?domain + "&" : domain + "?";
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        domain += ("timesTamp=" + c.getTimeInMillis());
        String key = cacheKey + domain;
        if(jedisCache.exists(key) && !refresh){
            return JSON.parse(jedisCache.getValue(key));
        }
        String result = HttpClientUtils.get(domain, null);
        jedisCache.saveOrUpdate(key, result);
        jedisCache.expire(key, 3600);//保留1小时
        return JSON.parse(result); // 返回结果
    }
}
