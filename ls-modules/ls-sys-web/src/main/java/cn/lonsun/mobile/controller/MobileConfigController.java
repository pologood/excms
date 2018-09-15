package cn.lonsun.mobile.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.mobile.internal.entity.MobileConfigEO;
import cn.lonsun.mobile.internal.service.IMobileConfigService;
import cn.lonsun.mobile.vo.MobileConfigVO;
import cn.lonsun.mobile.vo.MobileDataVO;
import cn.lonsun.util.LoginPersonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @author doocal
 * @ClassName: MobileColumnController
 * @Description: 手机配置控制层
 */
@Controller
@RequestMapping(value = "mobilecfg", produces = {"application/json;charset=UTF-8"})
public class MobileConfigController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IMobileConfigService mobileConfigService;

    /**
     * @param map
     * @return
     * @Description 首页
     * @author Doocal
     */
    @RequestMapping("index")
    public String mobileConfig(ModelMap map) {
        return "/system/mobilecfg/index";
    }

    /**
     * @param map
     * @return
     * @Description 手机端模拟配置首页
     * @author Doocal
     */
    @RequestMapping("deviceIndex")
    public String deviceIndex(ModelMap map) {
        return "/system/mobilecfg/device_Index";
    }

    /**
     * @param map
     * @return
     * @Description 手机端模拟配置项
     * @author Doocal
     */
    @RequestMapping("deviceCfg")
    public String deviceCfg(ModelMap map) {
        return "/system/mobilecfg/device_config";
    }

    /**
     * @param map
     * @return
     * @Description 修改页
     * @author Doocal
     */
    @RequestMapping("configEdit")
    public String securityIP_Edit(Integer type, ModelMap map) {
        return "/system/mobilecfg/config";
    }

    /**
     * @param map
     * @return
     * @Description 修改页
     * @author Doocal
     */
    @RequestMapping("publicConfigEdit")
    public String publicConfigEdit(Integer type, ModelMap map) {
        return "/system/mobilecfg/public_config";
    }

    /**
     * @param map
     * @return
     * @Description 修改页
     * @author Doocal
     */
    @RequestMapping("publicClassConfigEdit")
    public String publicClassConfigEdit(Integer type, ModelMap map) {
        return "/system/mobilecfg/public_class_config";
    }

    /**
     * 返回手机端配置和相关信息
     *
     * @return
     */
    @RequestMapping("getConfigList")
    @ResponseBody
    public Object mobileConfigList() {
        List<MobileConfigEO> list = mobileConfigService.getMobileConfigList(LoginPersonUtil.getSiteId());
        return getObject(list);
    }

    /**
     * @param map
     * @return
     * @Description 安全设置添加页
     * @author Doocal
     */
    @RequestMapping("getList")
    @ResponseBody
    public Object getPage(MobileDataVO vo, ModelMap map) {
        return null;
    }

    /**
     * @param vo
     * @return
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("saveMobileConfig")
    @ResponseBody
    public Object saveIp(MobileConfigVO vo) {
        return getObject(mobileConfigService.saveConfig(vo));
    }

    /**
     * @param vo
     * @return
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("saveMobilePublicConfig")
    @ResponseBody
    public Object saveMobilePublicConfig(MobileConfigVO vo) {
        return getObject(mobileConfigService.savePublicConfig(vo));
    }

    /**
     * @param eo
     * @return
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("editMobileColumn")
    @ResponseBody
    public Object editIP(MobileConfigEO eo) {
        return getObject(mobileConfigService.updateConfig(eo));
    }

    /**
     * @param id
     * @return
     * @Description 保存IP限制编辑
     * @author Doocal
     * @date 2015年8月31日 下午2:21:35
     */
    @RequestMapping("delMobileColumn")
    @ResponseBody
    public Object delIP(Long id) {

        if (AppUtil.isEmpty(id)) {
            return ajaxErr("参数错误！");
        }

        return getObject(mobileConfigService.deleteConfig(id));
    }

}
