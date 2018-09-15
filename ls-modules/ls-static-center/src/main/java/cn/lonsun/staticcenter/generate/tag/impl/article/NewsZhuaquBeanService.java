package cn.lonsun.staticcenter.generate.tag.impl.article;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.vo.PostItemVO;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.util.CollectUtil;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : liuk
 * @date : 2016/10/17
 */
@Component
public class NewsZhuaquBeanService extends AbstractBeanService {
    @Override
    public Object getObject(JSONObject paramObj) {
        String url = paramObj.getString("dataUrl");
        String website = paramObj.getString("website");
        Integer num = paramObj.getInteger("num");
        if (num == null) {
            String numStr = ContextHolder.getContext().getParamMap().get("num");
            num = AppUtil.getInteger(numStr);
        }
        List<PostItemVO> list = new ArrayList<PostItemVO>();
        System.out.println(url + "___" + website);
        if (!AppUtil.isEmpty(url)) {
            //ajax请求数据是会将URL中的&符号替换成&amp;，这里要替换回去
            url = url.replace("&amp;", "&");
            if (!AppUtil.isEmpty(website) && website.equals("shizfxx")) {//九江市政府信息
                list = CollectUtil.getInstance().getShizfxxData(url);
            } else if (!AppUtil.isEmpty(website) && website.equals("szfxx")) {//江西省政府信息
                list = CollectUtil.getInstance().getSzfxxData(url);
            } else if (!AppUtil.isEmpty(website) && website.equals("gwyxx")) {//国务院信息
                list = CollectUtil.getInstance().getGwyxxData(url);
            } else if (!AppUtil.isEmpty(website) && website.equals("gwyzcwjxx")) {//国务院政策文件信息
                list = CollectUtil.getInstance().getGwyxxData(url);
            } else if (!AppUtil.isEmpty(website) && website.equals("gwywj")) {//国务院文件信息
                list = CollectUtil.getInstance().getGwywjData(url);
            } else if (!AppUtil.isEmpty(website) && website.equals("ahszfwj")) {//安徽省政府文件
                list = CollectUtil.getInstance().getAHSZFWJData(url);
            }
        }
        if (null != num && list.size() > num) {
            return list.subList(0, num);
        }
        return list;
    }
}
