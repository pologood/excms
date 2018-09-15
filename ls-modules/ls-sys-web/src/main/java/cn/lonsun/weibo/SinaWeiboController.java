package cn.lonsun.weibo;

import cn.lonsun.activemq.MessageSender;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.ConcurrentUtil;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.entity.SinaWeiboUserInfoEO;
import cn.lonsun.weibo.entity.WeiboConfEO;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;
import cn.lonsun.weibo.entity.vo.SinaWeiboContentVO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ISinaWeiboService;
import cn.lonsun.weibo.service.IWeiboConfService;
import cn.lonsun.weibo.service.IWeiboRadioContentService;
import cn.lonsun.weibo.util.SinaCredential;
import cn.lonsun.weibo.util.TokenGetUtil;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.model.Paging;
import weibo4j.model.WeiboException;

/**
 * @author gu.fei
 * @version 2015-12-18 9:08
 */
@Controller
@RequestMapping("/weibo/sina")
public class SinaWeiboController extends BaseController {

    private static final String FILE_BASE = "/weibo";

    @Autowired
    private ISinaWeiboService sinaWeiboService;

    @Autowired
    private IWeiboConfService weiboConfService;

    @Autowired
    private IWeiboRadioContentService weiboRadioContentService;

    @Autowired
    private TaskExecutor taskExecutor;

    @RequestMapping("/auth")
    public String auth(String code,ModelMap map) {
        Long userId = LoginPersonUtil.getUserId();
//        Long siteId = LoginPersonUtil.getSiteId();
        String token = "";
        MessageSystemEO eo = new MessageSystemEO();
        eo.setTitle("微博验证API验证");
        eo.setLink("/weibo/content");
        eo.setModeCode("weibo");
        eo.setRecUserIds(userId + "");
        if(null == code) {
            eo.setContent("微博API验证失败!");
            map.put("result", "新浪微博API验证失败!回调code为空!");
        } else {
            try {
                token = TokenGetUtil.getSinaAccessToken(code);
                eo.setContent("微博API验证成功!");
                map.put("result", "新浪微博API验证成功!");
            } catch (Exception e) {
                eo.setContent("微博API验证失败:" + e.getMessage());
            }
        }

//        WeiboConfEO ceo = weiboConfService.getByType(WeiboConfEO.Type.Sina.toString(),siteId);
//        ceo.setToken(token);
//        weiboConfService.updateEntity(ceo);
        if(token != null) {
            map.put("token",token);
            try {
                SinaCredential.refresh(LoginPersonUtil.getSiteId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return FILE_BASE + "/sina_auth_success";
    }

    @ResponseBody
    @RequestMapping("/getToken")
    public Object getToken(WeiboConfEO eo) {
        String url =  "";
        try {
            Long siteId = LoginPersonUtil.getSiteId();
            eo.setSiteId(siteId);
            weiboConfService.saveEO(eo,eo.getType());
//            TokenGetUtil.getAccessCode(eo);
            url = TokenGetUtil.getAccessUrl(eo);
        } catch (Exception e) {
            return ResponseData.fail("获取验证地址失败!");
        }

        return getObject(url);
    }

    /**
     *同步微博数据
     * @return
     */
    @ResponseBody
    @RequestMapping("/syncWeiboDataOnline")
    public Object syncWeiboDataOnline() {
        try {
            SinaCredential.refresh(LoginPersonUtil.getSiteId());
        } catch (Exception e) {
            return ResponseData.fail(e.getMessage());
        }
        Paging paging = new Paging();
        paging.setPage(1);
        paging.setCount(100); //默认同步100条最新微博数据
        asyncThread(paging);
        return ResponseData.success("数据同步中!");
    }

    /**
     * 获取自己的微博信息
     * @return
     */
    @ResponseBody
    @RequestMapping("/getSelfWeiboInfo")
    public Object getSelfWeiboInfo() {
        SinaWeiboUserInfoEO eo = sinaWeiboService.getSelfWeiboInfo();
        return null == eo?new SinaWeiboUserInfoEO():eo;
    }

    /**
     * 分页加载微博内容
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageCurWeibo")
    public Object getPageCurWeibo(WeiboPageVO vo) {
        Long siteId = LoginPersonUtil.getSiteId();
        vo.setSiteId(siteId);
        Pagination page = sinaWeiboService.getPageCurWeibo(vo);
        return null == page?new Pagination():page;
    }

    /**
     * 发布微博
     * @return
     */
    @ResponseBody
    @RequestMapping("/publishWeibo")
    public Object publishWeibo(SinaWeiboContentVO vo) {
        WeiboRadioContentEO eo = new WeiboRadioContentEO();
        eo.setContent(vo.getText());
        eo.setType(WeiboRadioContentEO.Type.Sina.toString());
        eo.setPicUrl(vo.getOriginalPic());
        eo.setSiteId(LoginPersonUtil.getSiteId());
        weiboRadioContentService.saveEntity(eo);
        return ResponseData.success("操作成功!");
    }

    /**
     * 移除微博
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/removeWeibo")
    public Object removeWeibo(String weiboId) {
        return sinaWeiboService.removeWeibo(weiboId);
    }

    /**
     * 转发一条微博
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/repostWeibo")
    public Object repostWeibo(String weiboId) {
        return sinaWeiboService.repostWeibo(weiboId);
    }

    /**
     * 收藏一条微博
     * @param tagId
     * @param weiboId
     * @return
     */
    @ResponseBody
    @RequestMapping("/favoriteWeibo")
    public Object favoriteWeibo(String tagId,String weiboId) {
        return sinaWeiboService.favoriteWeibo(tagId, weiboId);
    }

    /**
     * 取消收藏一条微博
     * @param weiboId
     * @return
     */
    @ResponseBody
    @RequestMapping("/cancelFavoriteWeibo")
    public Object cancelFavoriteWeibo(String weiboId) {
        return sinaWeiboService.cancelFavoriteWeibo(weiboId);
    }

    /**
     * 根据微博ID分页获取评论列表
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageComments")
    public Object getPageComments(WeiboPageVO vo) {
        Long siteId = LoginPersonUtil.getSiteId();
        vo.setSiteId(siteId);
        try {
            return sinaWeiboService.getPageComments(vo);
        } catch (WeiboException e) {
            return ResponseData.fail("获取评论失败!");
        }
    }

    /**
     * 评论一条微博
     * @param weiboId
     * @param comment
     * @return
     */
    @ResponseBody
    @RequestMapping("/commentWeibo")
    public Object commentWeibo(String weiboId,String comment) {
        return sinaWeiboService.commentWeibo(weiboId, comment);
    }

    /**
     * 回复评论
     * @param cid
     * @param weiboId
     * @param comment
     * @return
     */
    @ResponseBody
    @RequestMapping("/replyComment")
    public Object replyComment(String cid, String weiboId, String comment) {
        return sinaWeiboService.replyComment(cid, weiboId, comment);
    }

    /**
     * 删除评论
     * @param cid
     * @return
     */
    @ResponseBody
    @RequestMapping("/removeReplyComment")
    public Object removeReplyComment(String cid,String commentType) {
        return sinaWeiboService.removeReplyComment(cid,commentType);
    }

    /**
     * 分页查询关注用户
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageFollows")
    public Object getPageFollows(WeiboPageVO vo) {
        return sinaWeiboService.getPageFollows(vo);
    }

    /**
     * 取消关注
     * @param uid
     * @return
     */
    @ResponseBody
    @RequestMapping("/cancelFollow")
    public Object cancelFollow(String uid) {
        return sinaWeiboService.cancelFollow(uid);
    }

    private void asyncThread(final Paging paging) {
        final Long siteId = LoginPersonUtil.getSiteId();
        final Long userId = LoginPersonUtil.getUserId();
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                MessageSystemEO eo = new MessageSystemEO();
                eo.setTitle("微博数据同步");
                eo.setLink("/weibo/content");
                eo.setModeCode("weibo");
                eo.setRecUserIds(userId + "");
                SessionFactory sessionFactory = SpringContextHolder.getBean(SessionFactory.class);
                boolean participate = ConcurrentUtil.bindHibernateSessionToThread(sessionFactory);
                try {
                    sinaWeiboService.syncWeiboDataOnline(paging, siteId);
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.success.toString());
                    eo.setContent("微博数据同步成功!");
                } catch (Exception e) {
                    e.printStackTrace();
                    eo.setMessageStatus(MessageSystemEO.MessageStatus.error.toString());
                    eo.setContent("微博数据同步失败!");
                }
                // 关闭session
                MessageSender.sendMessage(eo);
                ConcurrentUtil.closeHibernateSessionFromThread(participate, sessionFactory);
            }
        });
    }
}