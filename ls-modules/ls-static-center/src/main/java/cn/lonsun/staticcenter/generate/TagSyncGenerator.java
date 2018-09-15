package cn.lonsun.staticcenter.generate;

import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.staticcenter.generate.tag.BeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.RegexUtil;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 异步渲染标签
 * @author zhongjun
 */
public class TagSyncGenerator implements Callable<String>{

    private Logger log = LoggerFactory.getLogger(getClass());

    private Context context;

    private BeanService service;

    private String labelName;

    private String content;

    private JSONObject paramObj;

    public TagSyncGenerator(Context context, BeanService service, String labelName, String content, JSONObject paramObj) {
        this.context = context;
        this.service = service;
        this.labelName = labelName;
        this.content = content;
        this.paramObj = paramObj;
    }

    @Override
    public String call() throws Exception {
        try {
            ContextHolder.setContext(this.context);
            return HibernateSessionUtil.execute(new SessionUtil(service, labelName, content, paramObj));
        } catch (Exception e) {
            e.printStackTrace();
            log.error("======异步线程字标签渲染出现异常 : {}", e.getCause());
        }finally {
            //释放资源
            this.context = null;
            this.service = null;
            this.labelName = null;
            this.content = null;
            this.paramObj = null;
        }
        return "";
    }

    public class SessionUtil implements HibernateHandler<String> {

        private BeanService service;

        private String labelName;

        private String content;

        private JSONObject paramObj;

        public SessionUtil(BeanService service, String labelName, String content, JSONObject paramObj) {
            this.service = service;
            this.labelName = labelName;
            this.content = content;
            this.paramObj = paramObj;
        }

        @Override
        public String execute() throws Throwable {
            return RegexUtil.parseLabelHtml(this.service, this.labelName, this.content, this.paramObj);
        }

        @Override
        public String complete(String result, Throwable exception) {
            if (null != exception) {
                throw new BaseRunTimeException(TipsMode.Message.toString(), "异步渲染字标签hibernate失败！");
            }
            return result;
        }
    }
}
