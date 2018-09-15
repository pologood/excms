package cn.lonsun.staticcenter.generate.tag.impl.onlinework;

import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.util.UrlUtil;
import cn.lonsun.supervise.errhref.internal.util.URLHelper;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gu.fei
 * @version 2015-12-14 13:56
 */
@Component
public class OnlineNavUrlBeanService extends AbstractBeanService {


    @Override
    public Object getObject(JSONObject paramObj) {

        String url = paramObj.getString("url");
        Integer pageSize = paramObj.getInteger("pageSize");

        String rowName = paramObj.getString("rowName");
        try {
            JSONObject obj = URLHelper.readJsonFromUrl(UrlUtil.addParam(url,"pageSize=" + pageSize));

            if(null != rowName && !StringUtils.isEmpty(rowName)) {//有jsonarray数组的名称
                JSONArray array = obj.getJSONArray(rowName);
                if(array.size()>0 && null != array) {
                    if(array.size() > pageSize) {
                        return array.subList(0,pageSize);
                    }
                    return array;
                }
            }else {
                return obj;
            }
            /*if(null != array) {
                return JSONArray.parseArray(array.toJSONString());
            }*/

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 预处理结果
     * @param resultObj
     * @param paramObj
     * @return
     */
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) {
        return new HashMap<String, Object>();
    }
}
