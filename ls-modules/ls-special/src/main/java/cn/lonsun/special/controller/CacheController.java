package cn.lonsun.special.controller;

import cn.lonsun.core.base.controller.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Stack;

/**
 * Created by xiayc on 2017/3/15.
 */
@Controller
@RequestMapping(value = "/special/cache")
public class CacheController extends BaseController {
    private HttpSession session;
    @ModelAttribute
    private void before(HttpSession session) {
        this.session = session;
    }
    private final static String CACHE_KEY = "___form_cache";

    /**
     * 设置缓存
     * @param html
     * @return
     */
    @RequestMapping("setCache")
    @ResponseBody
    public Object setCache(String html,Long specialId) {
        if(null !=html && !"".equals(html)){
            getStack(specialId).push(html);
            return ajaxOk();
        }else{
            return ajaxErr("内容为空");
        }
    }

    /**
     * 读取缓存
     * @return
     */
    @RequestMapping("getCache")
    @ResponseBody
    public Object getCache(Long specialId) {
        if(getStack(specialId).empty()){
            return ajaxErr("");
        }else{
            return ajaxOk(getStack(specialId).pop());
        }
    }

    /**
     * 清除缓存
     * @return
     */
    @RequestMapping("clearCache")
    @ResponseBody
    public Object clearCache(Long specialId) {
        if(!getStack(specialId).empty()){
            getStack(specialId).clear();
        }
        return ajaxOk();
    }

    private Stack<String> getStack(Long specialId){
        Stack<String> stack;
        if(null !=session.getAttribute(specialId + "_" + CACHE_KEY)){
            stack = (Stack<String>)session.getAttribute(specialId + "_" + CACHE_KEY);
        }else{
            stack = new Stack<String>();
            session.setAttribute(specialId + "_" + CACHE_KEY,stack);
        }
        return stack;
    }
    /**
     * 读取缓存
     * @return
     */
    @RequestMapping("getPreViewCache")
    @ResponseBody
    public Object getPreViewCache(Long specialId) {
        if(getStack(specialId).empty()){
            return ajaxErr("");
        }else{
            return ajaxOk(getStack(specialId).peek());
        }
    }
}
