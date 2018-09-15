package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.base.util.HttpClientUtils;
import cn.lonsun.shiro.map.TimeoutMap;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by fth on 2017/7/6.
 */
public class HeFeiNewsBeanService extends AbstractBeanService {

    private String pattern_content = "<div class=\"content clearfix\">(.*?)</ul>";
    private Pattern r_content = Pattern.compile(pattern_content, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private String pattern = "<a[^>]+href=\"(.+?)\"[^>]+title=\"(.+?)\">([^<]+)</a>([^<]+)</li>";
    private Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    // 缓存的map，键为请求的参数，值为查询到的结果，2个小时的缓存
    private Map<String, List<Map<String, String>>> cacheMap = new TimeoutMap<String, List<Map<String, String>>>(TimeUnit.MINUTES, 1 * 60);

    @Override
    public Object getObject(JSONObject paramObj) {
        String domain = paramObj.getString("domain");
        AssertUtil.isEmpty(domain, "http请求domain参数不能为空！");
        domain = domain.replace("&amp;", "&");//替换请求参数
        List<Map<String, String>> resultList = null;//返回的结果
        if (cacheMap.containsKey(domain)) {//结果
            resultList = cacheMap.get(domain);
        } else {
            String result = HttpClientUtils.get(domain, null);
            if (StringUtils.isNotEmpty(result)) {// 正则分析内容
                Matcher m = r_content.matcher(result);
                if (m.find()) {// 取第一个内容
                    String content = m.group(1);
                    if (StringUtils.isNotEmpty(content)) {
                        resultList = new ArrayList<Map<String, String>>();
                        Matcher matcher = r.matcher(content);
                        while (matcher.find()) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("href", matcher.group(1).trim());
                            map.put("title", matcher.group(2).trim());
                            map.put("shortTitle", matcher.group(3).trim());
                            map.put("date", matcher.group(4).trim());
                            resultList.add(map);
                        }
                        cacheMap.put(domain, resultList);
                    }
                }
            }
        }
        int num = paramObj.getIntValue("num"); //查询的条数
        int listSize = null == resultList ? 0 : resultList.size();
        return listSize > num ? resultList.subList(0, num) : resultList;
    }
}
