package cn.lonsun.special.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.special.internal.service.ISpecialThemeService;
import cn.lonsun.special.internal.vo.SpecialThemeQueryVO;
import cn.lonsun.special.internal.vo.SpecialTypeVO;
import cn.lonsun.webservice.special.ISpecialWebService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 专题云模板
 * @author zhongjun
 */
@Controller
@RequestMapping("special/cloud/theme")
public class SpecialCloudThemeController extends BaseController {

    @Autowired
    private ISpecialWebService specialWebService;

    @Autowired
    private ISpecialThemeService specialThemeService;

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mv){
        mv.setViewName("special/cloud/theme/index");
        return mv;
    }

    /**
     * 获取专题类型列表
     * @return
     */
    @RequestMapping("getTypeList")
    @ResponseBody
    public Object getSpecialType(HttpServletRequest request) {
        try {
            return getObject(specialWebService.getSpecialTypeList(SpecialTypeVO.class));
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("获取分类失败：" + e.getMessage());
        }
    }

    /**
     * 获取专题类型列表
     * @return
     */
    @ResponseBody
    @RequestMapping("getSpecialList")
    public Object getSpecialList(HttpServletRequest request) {
        try {
            return specialThemeService.getCloudList(AppUtil.parseRequestToMap(request));
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("获取分类失败：" + e.getMessage());
        }
    }

    /**
     * 获取专题类型列表
     * @return
     */
    @ResponseBody
    @RequestMapping("downloadTheme")
    public Object downloadTheme(Long id, Integer version) {
        try {
            specialThemeService.downloadSpecialTheme(id, version);
            return getObject("正在下载");
        } catch (BaseRunTimeException e) {
            return ajaxErr(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ajaxErr("下载开启失败");
        }
    }

}
