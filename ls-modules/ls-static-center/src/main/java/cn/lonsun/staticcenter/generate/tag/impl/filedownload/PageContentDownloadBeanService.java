package cn.lonsun.staticcenter.generate.tag.impl.filedownload;

import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 1960274114 on 2016-9-23.
 */
@Component
public class PageContentDownloadBeanService   extends AbstractBeanService {
    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        String pageDownImagePath = paramObj.getString("pageDownImagePath");
        String pageDownContent_tag_id = paramObj.getString("pageDownContent_tag_id");
        String pageDownTitle_tag_id = paramObj.getString("pageDownTitle_tag_id");
        String action = "/pageDom/downWord";

        Map<String,String> data = new HashMap<String, String>();
        data.put("action",action);
        data.put("pageDownTitle_tag_id",pageDownTitle_tag_id);
        data.put("pageDownContent_tag_id",pageDownContent_tag_id);
        data.put("pageDownImagePath",pageDownImagePath);

        Map<String,Object> ret = new HashMap<String, Object>();
        ret.put("data",data);
        return  ret;
    }
}
