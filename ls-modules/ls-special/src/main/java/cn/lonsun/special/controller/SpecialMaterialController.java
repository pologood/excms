package cn.lonsun.special.controller;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.special.internal.entity.SpecialMaterialEO;
import cn.lonsun.special.internal.service.ISpecialMaterialService;
import cn.lonsun.special.internal.vo.SpecialMaterialQueryVO;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.LoginPersonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by doocal on 2016-10-15.
 */
@Controller
@RequestMapping(value = "/specialMaterial")
public class SpecialMaterialController extends BaseController {

    @Autowired
    private ISpecialMaterialService specialMaterialService;


    @RequestMapping("specialMaterialList")
    public String specialMaterialList(ModelMap map) {
        return "/special/specialmaterial/special_material_list";
    }

    @RequestMapping("specialMaterialEdit")
    public String specialMaterialEdit(Long id, ModelMap map) {
        List<DataDictVO> materialCategory = DataDictionaryUtil.getDDList("materialCategory");
        List<DataDictVO> materialType = DataDictionaryUtil.getDDList("materialType");
        map.put("id", id);
        map.put("materialType", materialType);
        map.put("materialCategory", materialCategory);
        return "/special/specialmaterial/special_material_edit";
    }

    /**
     * 根据ID获取专题主题
     *
     * @param id
     * @return
     */
    @RequestMapping("getSpecialMaterial")
    @ResponseBody
    public Object getSpecialMaterial(Long id) {
        SpecialMaterialEO specialMaterial = null;
        if (null == id) {
            specialMaterial = new SpecialMaterialEO();
        } else {
            specialMaterial = specialMaterialService.getEntity(SpecialMaterialEO.class, id);
        }
        return getObject(specialMaterial);
    }

    /**
     * 保存专题主题
     *
     * @param specialMaterial
     * @return
     */
    @RequestMapping("saveSpecialMaterial")
    @ResponseBody
    public Object saveSpecialMaterial(SpecialMaterialEO specialMaterial) {
        if (AppUtil.isEmpty(specialMaterial.getName())) {
            return ajaxErr("名称不能为空");
        }
        specialMaterial.setSiteId(LoginPersonUtil.getSiteId());
        specialMaterialService.saveSpecialMaterial(specialMaterial);
        return getObject();
    }

    /**
     * 获取分页数据
     *
     * @param queryVO
     * @return
     */
    @RequestMapping("getPagination")
    @ResponseBody
    public Object getPagination(SpecialMaterialQueryVO queryVO) {
        queryVO.setSiteId(LoginPersonUtil.getSiteId());
        Pagination pagination = specialMaterialService.getPagination(queryVO);
        return getObject(pagination);
    }

    /**
     * 删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("deleteSpecialMaterial")
    @ResponseBody
    public Object deleteSpecialMaterial(Long[] ids) {
        if (null == ids || ids.length == 0) {
            return ajaxErr("参数ids不能为空");
        }
        specialMaterialService.delete(SpecialMaterialEO.class, ids);
        return getObject();
    }


    /**
     * @return
     */
    @RequestMapping("getSpecialMaterialList")
    @ResponseBody
    public Object getSpecialMaterialList() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("siteId", LoginPersonUtil.getSiteId());
        params.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        return getObject(specialMaterialService.getEntities(SpecialMaterialEO.class, params));
    }
}
