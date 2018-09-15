package cn.lonsun.weibo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author gu.fei
 * @version 2015-12-18 8:49
 */
@Controller
@RequestMapping("weibo")
public class WeiboController {

    private static final String FILE_BASE = "/weibo";

    /**
     * 官微内容
     * @return
     */
    @RequestMapping("/content")
    public String content() {
        return FILE_BASE + "/content";
    }

    /**
     * 关注的微博
     * @return
     */
    @RequestMapping("/follow")
    public String follow() {
        return FILE_BASE + "/follow";
    }

    /**
     * 评论
     * @return
     */
    @RequestMapping("/comment")
    public String comment() {
        return FILE_BASE + "/comment";
    }

    /**
     * 收藏的微博
     * @return
     */
    @RequestMapping("/favorite")
    public String favorite() {
        return FILE_BASE + "/favorite";
    }

    /**
     * @我的
     * @return
     */
    @RequestMapping("/mention")
    public String mention() {
        return FILE_BASE + "/mention";
    }

}
