package cn.lonsun.weibo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.message.internal.entity.MessageSystemEO;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.entity.WeiboConfEO;
import cn.lonsun.weibo.entity.WeiboRadioContentEO;
import cn.lonsun.weibo.entity.vo.TencentWeiboContentVO;
import cn.lonsun.weibo.entity.vo.WeiboPageVO;
import cn.lonsun.weibo.service.ITencentWeiboService;
import cn.lonsun.weibo.service.IWeiboConfService;
import cn.lonsun.weibo.service.IWeiboRadioContentService;
import cn.lonsun.weibo.util.TencentCredential;
import cn.lonsun.weibo.util.TokenGetUtil;

/**
 * @author gu.fei
 * @version 2015-12-21 9:03
 */
@Controller
@RequestMapping("/weibo/tencent")
public class TencentWeiboController {

    private static final String FILE_BASE = "/weibo";

    @Autowired
    private ITencentWeiboService tencentWeiboService;

    @Autowired
    private IWeiboConfService weiboConfService;

    @Autowired
    private IWeiboRadioContentService weiboRadioContentService;

    @RequestMapping("/auth")
    public String auth(String code,String openId,ModelMap map) {
        Long userId = LoginPersonUtil.getUserId();
        Long siteId = LoginPersonUtil.getSiteId();
        String token = "";
        MessageSystemEO eo = new MessageSystemEO();
        eo.setTitle("腾讯微博验证API验证");
        eo.setLink("/weibo/content");
        eo.setModeCode("weibo");
        eo.setRecUserIds(userId + "");
        if(null == code) {
            eo.setContent("腾讯微博API验证失败!回调code为空!");
            map.put("result", "腾讯微博API验证失败!回调code为空!");
        } else {
            try {
                token = TokenGetUtil.getTencentAccessToken(code);
                eo.setContent("腾讯微博API验证成功!");
                map.put("result", "腾讯微博API验证成功!");
            } catch (Exception e) {
                eo.setContent("腾讯微博API验证失败:" + e.getMessage());
                map.put("result", "腾讯微博API验证失败:" + e.getMessage());
            }
        }

        WeiboConfEO ceo = weiboConfService.getByType(WeiboConfEO.Type.Sina.toString(),siteId);
        ceo.setToken(token);
        ceo.setOpenID(openId);
        weiboConfService.updateEntity(ceo);

        if(token != null) {
            try {
                TencentCredential.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return FILE_BASE + "/tencent_auth_success";
    }

    @ResponseBody
    @RequestMapping("/getToken")
    public Object getToken() {
        WeiboConfEO eo = weiboConfService.getByType(WeiboConfEO.Type.Tencent.toString(), LoginPersonUtil.getSiteId());
        try {
            TokenGetUtil.getAccessCode(eo);
        } catch (Exception e) {
            return ResponseData.fail("获取Token失败!");
        }

        return ResponseData.success("");
    }

    /**
     * 发布微博
     * @return
     */
    @ResponseBody
    @RequestMapping("/publishWeibo")
    public Object publishWeibo(TencentWeiboContentVO vo) {
        WeiboRadioContentEO eo = new WeiboRadioContentEO();
        eo.setContent(vo.getContent());
        eo.setType(WeiboRadioContentEO.Type.Tencent.toString());
        eo.setSiteId(LoginPersonUtil.getSiteId());
        weiboRadioContentService.saveEntity(eo);
        return ResponseData.success("微博发布成功!");
    }

    /**
     * 分页获取关注的人
     * @param vo
     * @return
     */
    @ResponseBody
    @RequestMapping("/getPageFollows")
    public Object getPageFollows(WeiboPageVO vo) {
        return tencentWeiboService.getPageFollows(vo);
    }

    /**
     * 取消关注
     * @param openIDs
     * @return
     */
    @ResponseBody
    @RequestMapping("/cancelIdol")
    public Object cancelIdol(String[] openIDs) {
        tencentWeiboService.cancelIdol(openIDs);
        return ResponseData.success("取消关注成功!");
    }

}
