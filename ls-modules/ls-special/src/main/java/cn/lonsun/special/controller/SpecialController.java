package cn.lonsun.special.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.site.site.internal.entity.SiteConfigEO;
import cn.lonsun.site.site.internal.entity.SiteMgrEO;
import cn.lonsun.special.internal.entity.SpecialEO;
import cn.lonsun.special.internal.service.ISpecialService;
import cn.lonsun.special.internal.vo.SpecialQueryVO;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @author DooCal
 * @ClassName: SpecialController
 * @Description:
 * @date 2015/12/8 8:49
 */
@Controller
@RequestMapping(value = "/special")
public class SpecialController extends BaseController {

    @Autowired
    private ISpecialService specialService;

    @RequestMapping("specialList")
    public String specialList(ModelMap map) {
        return "/special/special/special_list";
    }

    @RequestMapping("specialEdit")
    public String specialEdit(Long id, ModelMap map) {
        map.put("id", id);
        return "/special/special/special_edit";
    }

    @RequestMapping("specialSelect")
    public String specialSelect(Long id, ModelMap map) {
        map.put("id", id);
        return "/special/special/special_select";
    }

    /**
     * 根据ID获取专题主题
     *
     * @param id
     * @return
     */
    @RequestMapping("getSpecial")
    @ResponseBody
    public Object getSpecial(Long id) {
        SpecialEO special = null;
        if (null == id) {
            special = new SpecialEO();
        } else {
            special = specialService.getEntity(SpecialEO.class, id);
        }
        return getObject(special);
    }

    /**
     * 保存专题主题
     *
     * @param special
     * @return
     */
    @RequestMapping("saveSpecial")
    @ResponseBody
    public Object saveSpecial(SpecialEO special) {
        if (AppUtil.isEmpty(special.getName())) {
            return ajaxErr("名称不能为空");
        }
        if (AppUtil.isEmpty(special.getThemeId())) {
            return ajaxErr("未选择对应主题模板");
        }
        special.setSiteId(LoginPersonUtil.getSiteId());
        specialService.saveSpecial(special);
        return getObject();
    }

    /**
     * 保存专题子站
     *
     * @param siteMgrEO
     * @return
     */
    @RequestMapping("saveSpecialSite")
    @ResponseBody
    public Object saveSpecialSite(SiteMgrEO siteMgrEO) {

        if (AppUtil.isEmpty(siteMgrEO.getThemeId())) {
            return ajaxErr("未选择对应主题模板");
        }

        if (AppUtil.isEmpty(siteMgrEO.getName())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "站点名称不能为空");
        }

        if (AppUtil.isEmpty(siteMgrEO.getSortNum())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "序号不能为空");
        }

        if (AppUtil.isEmpty(siteMgrEO.getUnitNames()) || AppUtil.isEmpty(siteMgrEO.getUnitIds())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位不能为空");
        }

        if (AppUtil.isEmpty(siteMgrEO.getUri())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "绑定域名不能为空");
        }

        if (AppUtil.isEmpty(siteMgrEO.getUnitIds())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "单位不能为空");
        }

        if (!AppUtil.isEmpty(siteMgrEO.getKeyWords()) && siteMgrEO.getKeyWords().length() > 300) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "关键词的长度应小于300");
        }

        SiteConfigEO eo = specialService.saveSiteSpecial(siteMgrEO);

        return getObject(eo);
    }

    /**
     * 获取分页数据
     *
     * @param queryVO
     * @return
     */
    @RequestMapping("getPagination")
    @ResponseBody
    public Object getPagination(SpecialQueryVO queryVO) {
        queryVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination pagination = specialService.getPagination(queryVO);
        return getObject(pagination);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @RequestMapping("deleteSpecial")
    @ResponseBody
    public Object deleteSpecial(@RequestParam(defaultValue = "0") Long id) {
        if (AppUtil.isEmpty(id)) {
            return ajaxErr("参数不能为空");
        }
        specialService.deleteSpecial(id);
        return getObject();
    }

    /**
     * 状态
     *
     * @param id
     * @return
     */
    @RequestMapping("changeSpecial")
    @ResponseBody
    public Object changeSpecial(@RequestParam(defaultValue = "0") Long id, Long specialStatus) {
        if (AppUtil.isEmpty(id)) {
            return ajaxErr("参数不能为空");
        }
        specialService.changeSpecial(id, specialStatus);
        return getObject();
    }

    @RequestMapping("specialSelectColumn")
    public String specialSelectColumn(Model model, HttpServletRequest request) {
        return "common/specialSelectColumn";
    }

}
