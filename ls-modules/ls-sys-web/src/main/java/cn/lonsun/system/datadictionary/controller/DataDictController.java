package cn.lonsun.system.datadictionary.controller;

import cn.lonsun.system.role.internal.cache.RightDictCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictService;
import cn.lonsun.system.datadictionary.vo.DataDictPageVO;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 
 * @ClassName: DataDictController
 * @Description: 数据字典控制器
 * @author Hewbing
 * @date 2015年8月25日 上午10:02:04
 *
 */
@Controller
@RequestMapping(value = "dataDict", produces = { "application/json;charset=UTF-8" })
public class DataDictController extends BaseController {

    @Autowired
    private IDataDictService dataDictService;

    private Logger logger = LoggerFactory.getLogger(DataDictController.class);

    /**
     * 
     * @Description 跳转数据字典分页列表
     * @return
     */
    @RequestMapping("datadict_list")
    public String dataDictList(ModelMap map) {
        map.put("isRoot", LoginPersonUtil.isRoot());
        return "/system/datadictionary/datadict_list";
    }

    /**
     * 
     * @Description 按条件获取数据字典分页
     * @param pageVO
     * @return
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(DataDictPageVO pageVO, String dataFlag) {
        return getObject(dataDictService.getPage(pageVO));
    }

    /**
     * 
     * @Description 跳转添加数据字典页
     * @return
     */
    @RequestMapping("addDict")
    public String addDict() {
        return "/system/datadictionary/addDict";
    }

    /**
     * 
     * @Description 保存字典
     * @param dataDictEO
     * @return
     */
    @RequestMapping("saveDict")
    @ResponseBody
    public Object saveDict(DataDictEO dataDictEO) {
        if (LoginPersonUtil.isRoot()) {
            dataDictEO.setSiteId(null);
        } else {
            return ajaxErr("您无此权限");
        }
        if (AppUtil.isEmpty(dataDictEO.getCode())) {
            return ajaxErr("唯一编码不能为空！");
        }
        DataDictEO dataDict = dataDictService.getDataDictByCode(dataDictEO.getCode());
        if (dataDict != null) {
            return ajaxErr("唯一编码已存在！");
        }
        // 更新数据字典
        CacheHandler.saveOrUpdate(DataDictEO.class, dataDictEO);
        return getObject(dataDictService.saveEntity(dataDictEO));
    }

    /**
     * 
     * @Description 跳转编辑页面
     * @param dictId
     * @param model
     * @return
     */
    @RequestMapping("editDict")
    public String editDict(Long dictId, ModelMap model) {
        model.put("DictEO", dataDictService.getEntity(DataDictEO.class, dictId));
        return "/system/datadictionary/editDict";
    }

    /**
     * 
     * @Description 保存修改
     * @param dataDictEO
     * @return
     */
    @RequestMapping("updateDict")
    @ResponseBody
    public Object updateDict(DataDictEO dataDictEO) {
        if (LoginPersonUtil.isRoot()) {
            dataDictEO.setSiteId(null);
        } else {
            return ajaxErr("您无此权限");
        }
        dataDictService.updateEntity(dataDictEO);
        // 更新数据字典
        CacheHandler.saveOrUpdate(DataDictEO.class, dataDictEO);
        return getObject();
    }

    /**
     * 
     * @Description 删除字典及字典项
     * @param id
     * @return
     */
    @RequestMapping("deleteDict")
    @ResponseBody
    public Object deleteDict(Long id) {
        if (LoginPersonUtil.isRoot()) {
            dataDictService.deleteDict(id);
            // 删除数据字典
            CacheHandler.delete(DataDictEO.class, CacheHandler.getEntity(DataDictEO.class, id));
            RightDictCache.refresh();
            return getObject();
        } else {
            return ajaxErr("您无此权限");
        }

    }

    /**
     * 
     * @Title: resetUse
     * @Description: 设置使用情况
     * @param id
     * @return Parameter
     * @return Object return type
     * @throws
     */
    @RequestMapping("resetUse")
    @ResponseBody
    public Object resetUse(Long id) {
        if (LoginPersonUtil.isRoot()) {
            dataDictService.changeUsed(id);
            return getObject();
        } else {
            return ajaxErr("您无此权限");
        }
    }
}
