package cn.lonsun.weibo;

import cn.lonsun.weibo.util.SinaCredential;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.weibo.entity.WeiboConfEO;
import cn.lonsun.weibo.service.IWeiboConfService;

/**
 * @author gu.fei
 * @version 2015-12-10 17:26
 */
@Controller
@RequestMapping("/weibo/conf")
public class WeiboConfController {

    private static final String FILE_BASE = "/weibo";

    @Autowired
    private IWeiboConfService weiboConfService;

    @RequestMapping("/index")
    public ModelAndView conf() {
        Long siteId = LoginPersonUtil.getSiteId();
        ModelAndView model = new ModelAndView(FILE_BASE + "/connect_conf");
        WeiboConfEO eo = weiboConfService.getByType(WeiboConfEO.Type.Sina.toString(),siteId);
        if(null != eo) {
            model.addObject("flag",true);
            model.addObject("eo",eo);
        } else {
            model.addObject("flag",false);
        }
        return model;
    }

    @ResponseBody
    @RequestMapping("/getConfByType")
    public Object getConfByType(String type) {
        Long siteId = LoginPersonUtil.getSiteId();
        return ResponseData.success(weiboConfService.getByType(type,siteId), "");
    }

    @ResponseBody
    @RequestMapping("/saveEO")
    public Object saveEO(WeiboConfEO eo,String type) {
        Long siteId = LoginPersonUtil.getSiteId();
        eo.setSiteId(siteId);
        try {
            weiboConfService.saveEO(eo,type);
            SinaCredential.refresh(LoginPersonUtil.getSiteId());
        } catch (Exception e) {
            return ResponseData.success(e.getMessage());
        }
        return ResponseData.success("保存成功!");
    }
}
