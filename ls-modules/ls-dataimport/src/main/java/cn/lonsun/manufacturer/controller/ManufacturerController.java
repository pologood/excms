package cn.lonsun.manufacturer.controller;

import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.manufacturer.internal.entity.ManufacturerEO;
import cn.lonsun.manufacturer.internal.service.IManufacturerService;
import cn.lonsun.manufacturer.internal.vo.ManufacturerQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author caohaitao
 * @Title: ManufacturerController
 * @Package cn.lonsun.controller.manufacturer
 * @Description: 数据导入模块网站厂商管理
 * @date 2018/2/1 16:00
 */
@Controller
@RequestMapping(value = "manufacturer",produces = { "application/json;charset=UTF-8" })
public class ManufacturerController extends BaseController {

    private static final String FILE_BASE = "/dataimport/manufacturer";

    @Autowired
    private IManufacturerService manufacturerService;

    @RequestMapping("list")
    public String list() {
        return FILE_BASE + "/manufacturer_list";
    }

    @RequestMapping("edit")
    public String edit(Long id, ModelMap modelMap) {
        if(null != id){
            modelMap.addAttribute("manufacturerId",id);
        }
        return FILE_BASE + "/manufacturer_edit";
    }

    /**
     * 根据Id获取厂商信息
     * @param id
     * @return
     */
    @RequestMapping("getManufacturer")
    @ResponseBody
    public Object getManufacturer(Long id){
        ManufacturerEO manufacturerEO = null;
        if(null == id){
            manufacturerEO = new ManufacturerEO();
        }else {
            manufacturerEO = manufacturerService.getEntity(ManufacturerEO.class, id);
        }
        return getObject(manufacturerEO);
    }

    /**
     * 新增与修改厂商
     * @param eo
     * @return
     */
    @RequestMapping("saveOrUpdate")
    @ResponseBody
    public Object saveOrUpdate(ManufacturerEO eo) {
        if (null == eo.getId()) {
            manufacturerService.saveEntity(eo);
        } else {
            manufacturerService.updateEntity(eo);
        }
        return getObject();
    }

    /**
     * 删除厂商
     * @param ids
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids") Long[] ids) {
        if (ids != null && ids.length > 0) {
            manufacturerService.delete(ManufacturerEO.class, ids);
        }
        return getObject();
    }

    /**
     * 获取分页
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ManufacturerQueryVO queryVO) {
        Pagination page = manufacturerService.getPage(queryVO);
        return getObject(page);
    }

}
