package cn.lonsun.content.officePublicity.controller;

import cn.lonsun.content.officePublicity.internal.service.IOfficePublicityService;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsOfficePublicityEO;
import cn.lonsun.net.service.entity.vo.OfficePublicityQueryVO;
import cn.lonsun.site.template.util.ResponseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by huangxx on 2017/2/24.
 */
@Controller
@RequestMapping("officePublicity")
public class OfficePublicityController extends BaseController{

    @Autowired
    private IOfficePublicityService officePublicityService;


    /**
     * 列表页面
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "content/officePublicity/list";
    }

    /**
     * 获取分页
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(OfficePublicityQueryVO queryVO) {
        Pagination page = officePublicityService.getPage(queryVO);
        return page;
    }


    @RequestMapping("/saveEO")
    @ResponseBody
    public Object saveEO(CmsOfficePublicityEO eo) {
        officePublicityService.saveEO(eo);
        return ResponseData.success("保存成功!");
    }


    @RequestMapping("/updateEO")
    @ResponseBody
    public Object updateEO(CmsOfficePublicityEO eo) {
        officePublicityService.updateEO(eo);
        return ResponseData.success("更新成功!");
    }


    @RequestMapping("/delete")
    @ResponseBody
    public Object deleteEO(String ids) {
        Long[] idsn = StringUtils.getArrayWithLong(ids,",");
        officePublicityService.deleteEO(idsn);
        return ResponseData.success("删除成功!");
    }
}
