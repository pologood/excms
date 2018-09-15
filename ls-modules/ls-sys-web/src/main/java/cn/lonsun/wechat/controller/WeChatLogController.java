package cn.lonsun.wechat.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.wechatmgr.internal.entity.UserGroupEO;
import cn.lonsun.wechatmgr.internal.entity.WeChatUserEO;
import cn.lonsun.wechatmgr.internal.service.IWeChatLogService;
import cn.lonsun.wechatmgr.vo.WeChatUserVO;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangchao on 2016/10/12.
 */

@Controller
@RequestMapping("/weChatLog")
public class WeChatLogController extends BaseController {

    @Resource
    private IWeChatLogService weChatLogService;


    @RequestMapping("list")
    public String list(){
        return "/wechat/wechatlog_list";
    }

    /**
     *
     * @Title: userPage
     * @Description: 关注用户
     * @param userVO
     * @return   Parameter
     * @return  Object   return type
     * @throws
     */

    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(WeChatUserVO userVO){
        Long siteId= LoginPersonUtil.getSiteId();
        if(null==siteId){
            return ajaxErr("权限不足");
        }
        userVO.setSiteId(siteId);
        Pagination page=weChatLogService.getPage(userVO);
        return getObject(page);
    }

}
