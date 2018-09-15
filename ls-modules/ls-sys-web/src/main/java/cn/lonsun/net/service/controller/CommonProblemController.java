package cn.lonsun.net.service.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsCommonProblemEO;
import cn.lonsun.net.service.service.ICommonProblemService;
import cn.lonsun.site.template.internal.entity.ParamDto;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.role.internal.util.RoleAuthUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author gu.fei
 * @version 2016-08-01 8:52
 */
@Controller
@RequestMapping(value = "commonProblem")
public class CommonProblemController extends BaseController {

    private static final String FILE_BASE = "/net/service";

    @Autowired
    private ICommonProblemService commonProblemService;

    @RequestMapping("/index")
    public String index() {
        return FILE_BASE + "/problem/index";
    }

    @ResponseBody
    @RequestMapping("/getPageEntities")
    public Object getPageEntities(ParamDto dto) {

        if(!RoleAuthUtil.isCurUserColumnAdmin(dto.getClassifyId()) && !LoginPersonUtil.isRoot() && !LoginPersonUtil.isSiteAdmin() && !LoginPersonUtil.isSuperAdmin()) {
            dto.setByOrgan(true);
        }

        Pagination page = commonProblemService.getPageEntities(dto);
        return page;
    }

    @ResponseBody
    @RequestMapping("/saveEO")
    public Object saveEO(CmsCommonProblemEO eo) {
        eo.setSiteId(LoginPersonUtil.getSiteId());
        commonProblemService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }

    @ResponseBody
    @RequestMapping("/updateEO")
    public Object updateEO(CmsCommonProblemEO eo) {
        CmsCommonProblemEO oeo = commonProblemService.getEntity(CmsCommonProblemEO.class,eo.getId());
        oeo.setTitle(eo.getTitle());
        oeo.setContent(eo.getContent());
        oeo.setPublish(eo.getPublish());
        commonProblemService.updateEntity(oeo);
        return ResponseData.success("修改成功!");
    }

    @ResponseBody
    @RequestMapping("/delete")
    public Object deleteEO(@RequestParam(value="ids[]",required=false) Long[] ids) {
        commonProblemService.deleteEO(ids);
        return ResponseData.success("删除成功!");
    }

    @ResponseBody
    @RequestMapping("/publish")
    public Object publish(String ids,Long publish) {
        String msg = (null != publish && publish.intValue() == 1)?"发布":"取消发布";
        if(ids == null) {
            return ResponseData.success(msg + "失败!");
        }
        Long[] idsl = StringUtils.getArrayWithLong(ids, ",");

        if(idsl.length <= 0) {
            return ResponseData.success(msg + "失败!");
        }

        commonProblemService.publish(idsl, publish);
        return ResponseData.success(msg + "成功!");
    }
}
