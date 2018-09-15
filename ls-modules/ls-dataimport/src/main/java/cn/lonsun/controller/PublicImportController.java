package cn.lonsun.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.internal.metadata.ImportType;
import cn.lonsun.internal.service.IPublicImportService;
import org.directwebremoting.annotations.Auth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author zhongjun
 */
@Controller
@RequestMapping("di/publicinfo")
public class PublicImportController extends BaseController {

    @Autowired
    private IPublicImportService publicImportService;

    /**
     * 导入
     * @param mv
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView index(ModelAndView mv){


        mv.setViewName("dataimport/publicinfo/import");
        return mv;
    }

    /**
     * 单位关系
     * @param mv
     * @return
     */
    @RequestMapping(value="organrelation", method = RequestMethod.GET)
    public ModelAndView organRelation(ModelAndView mv){


        mv.setViewName("dataimport/publicinfo/organrelation");
        return mv;
    }

    /**
     * 目录关系
     * @param mv
     * @return
     */
    @RequestMapping(value="catrelation", method = RequestMethod.GET)
    public ModelAndView catRelation(ModelAndView mv){


        mv.setViewName("dataimport/publicinfo/catrelation");
        return mv;
    }

    /**
     * 导入信息公开内容
     * @param importType
     * @param siteId
     * @param organId
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "startimport")
    public Object startImport(String importType, Long siteId, String organId, @RequestParam(value="ids[]", required=false) String[] ids){
        ImportType type = ImportType.valueOf(importType);
        try {
            publicImportService.importSite(type, siteId, organId, ids);
        } catch (BaseRunTimeException e) {
            return ajaxErr(e.getMessage());
        }
        return getObject();
    }

    /**
     * 导入信息公开内容
     * @param importType
     * @param siteId
     * @param organId
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "importOrgan")
    public Object importOrgan(String importType, Long siteId, String organId, @RequestParam(value="ids[]", required=false) String[] ids){
        ImportType type = ImportType.valueOf(importType);
        try {
            publicImportService.importSite(type, siteId, organId, ids);
        } catch (BaseRunTimeException e) {
            return ajaxErr(e.getMessage());
        }
        return getObject();
    }


}
