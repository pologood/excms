package cn.lonsun.staticcenter.generate.tag.impl.publicInfo;

import cn.lonsun.base.util.HttpClientUtils;
import cn.lonsun.shiro.map.TimeoutMap;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.AssertUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 合肥政务公开查询标签，带缓存功能
 * Created by fth on 2017/7/4.
 */
@Service
public class HeFeiZwgkBeanService extends AbstractBeanService {
    // 按指定模式在字符串查找
    private String pattern = "<a[^>]+onmouseover[^>]+href=\"(.+?)\"[^>]+>([^<]+)</a>.*?align=\"center\"[^>]+>([^<]+?)</td>.*?<td colspan=\"3\" valign=\"top\">([^>]+)</td>";
    // 创建 Pattern 对象
    private Pattern r = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    // 缓存的map，键为请求的参数，值为查询到的结果，2个小时的缓存
    private Map<String, List<Map<String, String>>> cacheMap = new TimeoutMap<String, List<Map<String, String>>>(TimeUnit.MINUTES, 1 * 60);

    @Override
    public Object getObject(JSONObject paramObj) {
        String domain = paramObj.getString("domain");
        AssertUtil.isEmpty(domain, "http请求domain参数不能为空！");
        String params = paramObj.getString("params");
        AssertUtil.isEmpty(params, "http请求params参数不能为空！");
        String key = domain + "?" + params;// 请求的地址
        key = key.replace("&amp;", "&");//替换请求参数
        List<Map<String, String>> resultList = null;//返回的结果
        if (cacheMap.containsKey(key)) {//结果
            resultList = cacheMap.get(key);
        } else {
            String result = HttpClientUtils.get(key, null);
            if (StringUtils.isNotEmpty(result)) {// 正则分析内容
                Matcher m = r.matcher(result);
                resultList = new ArrayList<Map<String, String>>();
                while (m.find()) {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("href", m.group(1).trim());
                    map.put("shortTitle", m.group(2).trim());
                    map.put("date", m.group(3).trim());
                    map.put("title", m.group(4).trim());
                    resultList.add(map);
                }
                cacheMap.put(key, resultList);
            }
        }
        int num = paramObj.getIntValue("num"); //查询的条数
        int listSize = null == resultList ? 0 : resultList.size();
        return listSize > num ? resultList.subList(0, num) : resultList;
    }
}