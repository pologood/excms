package cn.lonsun.site.thirdLoginManage;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.site.thirdLoginManage.internal.entity.ThirdLoginMgrEO;
import cn.lonsun.site.thirdLoginManage.internal.service.IThirdLoginMgrService;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("thirdLoginManage")
@Controller
public class ThirdLoginManageController extends BaseController {

    @Autowired
    private IThirdLoginMgrService thirdLoginMgrService;

    private static final String FILE_BASE = "/site/thirdLoginManage/";

    @RequestMapping("index")
    public String index(){
        return FILE_BASE + "index";
    }


    /**
     * 获取当前站点下的第三方登录配置
     * @Author: liuk
     * @Date: 2018-4-23 17:34:214
     */
    @RequestMapping("getThirdLoginMgrInfo")
    @ResponseBody
    public Object getThirdLoginMgrInfo(){
        List<ThirdLoginMgrEO> resultList = new ArrayList<ThirdLoginMgrEO>();

        Long siteId = LoginPersonUtil.getSiteId();


        ThirdLoginMgrEO weiBo = thirdLoginMgrService.getMgrInfoByType(siteId, ThirdLoginMgrEO.Type.WeiBo.toString());
        if(weiBo==null){
            weiBo = new ThirdLoginMgrEO();
            weiBo.setType(ThirdLoginMgrEO.Type.WeiBo.toString());
        }
        resultList.add(weiBo);

        ThirdLoginMgrEO qq = thirdLoginMgrService.getMgrInfoByType(siteId, ThirdLoginMgrEO.Type.QQ.toString());
        if(qq==null){
            qq = new ThirdLoginMgrEO();
            qq.setType(ThirdLoginMgrEO.Type.QQ.toString());
        }
        resultList.add(qq);

        ThirdLoginMgrEO weChat = thirdLoginMgrService.getMgrInfoByType(siteId, ThirdLoginMgrEO.Type.WeChat.toString());
        if(weChat==null){
            weChat = new ThirdLoginMgrEO();
            weChat.setType(ThirdLoginMgrEO.Type.WeChat.toString());
        }
        resultList.add(weChat);

        return getObject(resultList);
    }

    /**
     * 保存第三方登录配置信息
     * @Author: liuk
     * @Date: 2018-4-23 17:38:19
     */
    @RequestMapping("saveThirdLoginMgr")
    @ResponseBody
    public Object saveThirdLoginMgr(String appId,String appSecret,String type){
        if(AppUtil.isEmpty(type)){
            return ajaxErr("分类不能为空");
        }
        thirdLoginMgrService.saveThirdLoginMgr(appId,appSecret,LoginPersonUtil.getSiteId(),type);
        return getObject();
    }


    /**
     * 变更启用状态
     * @Author: liuk
     * @Date: 2018-4-23 17:38:19
     */
    @RequestMapping("changeStatus")
    @ResponseBody
    public Object changeStatus(String type,Integer status){
        if(AppUtil.isEmpty(type)){
            return ajaxErr("分类不能为空");
        }
        thirdLoginMgrService.changeStatus(LoginPersonUtil.getSiteId(),type,status);
        return getObject();
    }

}
