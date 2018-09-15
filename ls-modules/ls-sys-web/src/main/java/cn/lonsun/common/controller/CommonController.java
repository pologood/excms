package cn.lonsun.common.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.rbac.utils.TimeCutUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author DooCal
 * @ClassName: Common
 * @Description:
 * @date 2016/7/6 18:31
 */
@Controller
@RequestMapping("/common")
public class CommonController extends BaseController {

    @Autowired
    private IOrganService organService;

    @RequestMapping("selectUnitUser")
    public String selectUnitUser(Model model, @RequestParam(value = "scope", required = false, defaultValue = "0") Integer scope
        , String organIds) {
        String dns = null;
        // 读取时间截
        String key = scope.intValue() == 0 ? "PersonUpdateDate" : "OrganUpdateDate";
        String timecut = null;
        try {
            //获取dns
            if (!StringUtils.isEmpty(organIds)) {
                Long[] ids = cn.lonsun.core.base.util.StringUtils.getArrayWithLong(organIds, ",");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("organId", Long.valueOf(organIds));
                List<OrganEO> organs = organService.getEntities(OrganEO.class, params);
                if (organs != null && organs.size() > 0) {
                    for (OrganEO organ : organs) {
                        String dn = organ.getDn().replace(",", "-");
                        if (StringUtils.isEmpty(dns)) {
                            dns = dn;
                        } else {
                            dns = dns + "," + dn;
                        }
                    }
                }
            }
            //获取时间戳
            timecut = TimeCutUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("timecut", timecut);
        model.addAttribute("dns", dns);
        return "/common/selectUnitUser";

    }

    @RequestMapping("selectUnitUser2")
    public String selectUnitUser2(Model model, @RequestParam(value = "scope", required = false, defaultValue = "0") Integer scope
            , String organIds) {
        String dns = null;
        // 读取时间截
        String key = scope.intValue() == 0 ? "PersonUpdateDate" : "OrganUpdateDate";
        String timecut = null;
        try {
            //获取dns
            if (!StringUtils.isEmpty(organIds)) {
                Long[] ids = cn.lonsun.core.base.util.StringUtils.getArrayWithLong(organIds, ",");
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("organId", Long.valueOf(organIds));
                List<OrganEO> organs = organService.getEntities(OrganEO.class, params);
                if (organs != null && organs.size() > 0) {
                    for (OrganEO organ : organs) {
                        String dn = organ.getDn().replace(",", "-");
                        if (StringUtils.isEmpty(dns)) {
                            dns = dn;
                        } else {
                            dns = dns + "," + dn;
                        }
                    }
                }
            }
            //获取时间戳
            timecut = TimeCutUtil.get(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        model.addAttribute("timecut", timecut);
        model.addAttribute("dns", dns);
        return "/common/selectUnitUser2";

    }

    @RequestMapping("selectColumn")
    public String selectColumn(Model model, HttpServletRequest request) {
        return "common/selectColumn";
    }

    @RequestMapping("selectColumn2")
    public String selectColumn2(Model model, HttpServletRequest request) {
        return "common/selectColumn2";
    }

    @RequestMapping("specialSelectColumn")
    public String specialSelectColumn(Model model, HttpServletRequest request) {
        return "common/specialSelectColumn";
    }

    @RequestMapping("calendar")
    public String calendar(Model model, HttpServletRequest request) {
        return "calendar/index";
    }

    @RequestMapping("calendar1")
    public String calendar1(Model model, HttpServletRequest request) {
        return "calendar/index";
    }

    @ResponseBody
    @RequestMapping("externalPage")
    public String externalPage(String address){
        if(StringUtils.isEmpty(address)){
            return "地址为空";
        }
        return "<iframe id='externalPageFrame' style='border:none;width:100%;height:100%;' src='" + URLDecoder.decode(address) + "'></iframe>";
    }
}
