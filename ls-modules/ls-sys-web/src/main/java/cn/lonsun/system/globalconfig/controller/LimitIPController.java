package cn.lonsun.system.globalconfig.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.system.globalconfig.internal.entity.LimitIPEO;
import cn.lonsun.system.globalconfig.internal.service.ILimitIPService;
import cn.lonsun.system.globalconfig.vo.LimitIPPageVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Hewbing
 * @ClassName: GlobConfigController
 * @Description: 全局配置控制层
 * @date 2015年8月25日 上午10:25:34
 */
@Controller
@RequestMapping(value = "limitIP", produces = {"application/json;charset=UTF-8"})
public class LimitIPController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(LimitIPController.class);

    @Autowired
    private ILimitIPService limitIPService;

    /**
     * @param map
     * @return
     * @Description 安全设置页
     * @author Hewbing
     * @date 2015年8月28日 下午2:20:09
     */
    @RequestMapping("index")
    public String sysSafeSet(ModelMap map) {
        return "/system/gloconfig/limitIP";
    }

    /**
     * @param map
     * @return
     * @Description 安全设置添加页
     * @author Doocal
     */
    @RequestMapping("limitIPAdd")
    public String securityIP_Add(ModelMap map) {
        return "/system/gloconfig/limitIP_add";
    }

    /**
     * @param map
     * @return
     * @Description 安全设置添加页
     * @author Doocal
     */
    @RequestMapping("limitIPEdit")
    public String securityIP_Edit(Long id, ModelMap map) {
        if (AppUtil.isEmpty(id)) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "ID不能为空");
        }
        LimitIPEO eo = limitIPService.getOneIP(id);
        map.put("id", id);
        map.put("ip", eo.getIp());
        map.put("rules", eo.getRules());
        map.put("description", eo.getDescription());
        return "/system/gloconfig/limitIP_add";
    }

    /**
     * @param map
     * @return
     * @Description 安全设置添加页
     * @author Doocal
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(LimitIPPageVO vo, ModelMap map) {
        return getObject(limitIPService.getPage(vo));
    }

    /**
     * @param eo
     * @return
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("saveIP")
    @ResponseBody
    public Object saveIp(LimitIPEO eo) {

        if (!validateIp(eo.getIp())) {
            return ajaxErr("IP格式不正确");
        }

        List<LimitIPEO> list = limitIPService.checkIP(eo);
        if (list.size() > 0) {
            return ajaxErr("该IP已添加！");
        }

        return getObject(limitIPService.saveLimitIP(eo));
    }

    /**
     * @param eo
     * @return
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("editIP")
    @ResponseBody
    public Object editIP(LimitIPEO eo) {

        if (!validateIp(eo.getIp())) {
            return ajaxErr("IP格式不正确");
        }

        List<LimitIPEO> list = limitIPService.checkIP(eo);
        if (list.size() > 0) {
            return ajaxErr("该IP已添加！");
        }

        return getObject(limitIPService.updateLimitIP(eo));
    }

    /**
     * @param id
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("delIP")
    @ResponseBody
    public Object delIP(Long id) {

        if (AppUtil.isEmpty(id)) {
            return ajaxErr("参数错误！");
        }

        return getObject(limitIPService.deleteLimitIP(id));
    }

    public boolean validateIp(String ip) {
        String ver = "^((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)|(\\*))))$";
        Pattern p = Pattern.compile(ver);
        Matcher m = p.matcher(ip);
        boolean result = m.matches();
        return result;
    }

}
