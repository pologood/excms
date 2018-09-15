package cn.lonsun.system.datadictionary.controller;

import cn.lonsun.system.role.internal.cache.RightDictCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.system.datadictionary.internal.entity.DataDictEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;
import cn.lonsun.system.datadictionary.internal.service.IDataDictService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * @author Hewbing
 * @ClassName: DataDictItemController
 * @Description: 数据字典项控制器
 * @date 2015年8月24日 上午10:01:07
 */
@Controller
@RequestMapping(value = "dictItem", produces = { "application/json;charset=UTF-8" })
public class DataDictItemController extends BaseController {

    private Logger logger = LoggerFactory.getLogger(DataDictItemController.class);

    @Autowired
    private IDataDictItemService dataDictItemService;

    @Autowired
    private IDataDictService dataDictService;

    @ModelAttribute
    public void putAttr(ModelMap map) {
        map.put("rightsCode", LoginPersonUtil.isRoot() ? "root" : LoginPersonUtil.isSuperAdmin() ? "superAdmin" : LoginPersonUtil.isSiteAdmin() ? "siteAdmin"
                : "normal");
    }

    /**
     * @return
     * @Description 跳转字典项列表
     */
    @RequestMapping("dictItemList")
    public String dictItemList(Long dictId, ModelMap map) {
        map.put("dictId", dictId);
        return "/system/datadictionary/dictItem_List";
    }

    /**
     * @param pageIndex
     * @param pageSize
     * @param dictId
     * @return
     * @Description 根据字典ID获取字典项分页
     */
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(Long pageIndex, Integer pageSize, Long dictId, String name) {
        if (pageIndex == null)
            pageIndex = 0L;
        if (pageSize == null)
            pageSize = 10;
        return getObject(dataDictItemService.getPageByDictId(pageIndex, pageSize, dictId, name));
    }

    /**
     * @param dictId
     * @return
     * @Description 根据字典ID获取字典项集合
     */
    @RequestMapping("getListByDictId")
    @ResponseBody
    public Object getListByDictId(Long dictId) {
        return getObject(dataDictItemService.getListByDictId(dictId));
    }

    /**
     * @param dictId
     * @param map
     * @return
     * @Description 新增页面
     */
    @RequestMapping("addItem")
    public String addItem(Long dictId, ModelMap map) {
        map.put("DataDict", dataDictService.getEntity(DataDictEO.class, dictId));
        map.put("maxSort", dataDictItemService.getMaxItem(dictId) + 2);
        return "/system/datadictionary/addItem";
    }

    /**
     * @param dictItem
     * @return
     * @Description 保存增加
     */
    @RequestMapping("saveItem")
    @ResponseBody
    public Object saveItem(DataDictItemEO dictItem) {
        DataDictItemEO item = dataDictItemService.getDataDictItemByCode(dictItem.getCode(), dictItem.getDataDicId());
        if (item != null) {
            return ajaxErr("唯一编码已存在！");
        }
        if (LoginPersonUtil.isRoot()) {
            dictItem.setSiteId(null);
        } else if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
            dictItem.setSiteId(LoginPersonUtil.getSiteId());
        } else {
            ajaxErr("您无此权限");
        }
        // 更新数据字典
        CacheHandler.saveOrUpdate(DataDictItemEO.class, dictItem);
        return getObject(dataDictItemService.saveItem(dictItem));
    }

    /**
     * @param ids
     * @return
     * @Description 删除字典项
     */
    @RequestMapping("deleteItem")
    @ResponseBody
    public Object deleteItem(@RequestParam("ids[]") Long[] ids) {
        dataDictItemService.delete(DataDictItemEO.class, ids);
        if (null != ids && ids.length > 0) {
            for (Long id : ids) {
                // 删除数据字典
                CacheHandler.delete(DataDictItemEO.class, CacheHandler.getEntity(DataDictItemEO.class, id));
            }
        }
        RightDictCache.refresh();
        return getObject();
    }

    /**
     * @param itemId
     * @param map
     * @return
     * @Description 编辑字典页
     */
    @RequestMapping("editItem")
    public String editItem(Long itemId, ModelMap map) {
        map.put("ItemEO", dataDictItemService.getEntity(DataDictItemEO.class, itemId));
        return "/system/datadictionary/editItem";
    }

    /**
     * 修改隐藏状态
     * @param itemId
     * @return
     */
    @RequestMapping("setHide")
    @ResponseBody
    public Object setHide(Long itemId,Integer flag){
        dataDictItemService.updateHide(itemId,flag);
        return getObject();
    }

    /**
     * @param itemEO
     * @return
     * @Description 保存修改
     */
    @RequestMapping("updateItem")
    @ResponseBody
    public Object updateItem(DataDictItemEO itemEO) {
        if (LoginPersonUtil.isRoot()) {
            itemEO.setSiteId(null);
        } else if (LoginPersonUtil.isSiteAdmin() || LoginPersonUtil.isSuperAdmin()) {
            itemEO.setSiteId(LoginPersonUtil.getSiteId());
        } else {
            ajaxErr("您无此权限");
        }
        // 更新数据字典
        CacheHandler.saveOrUpdate(DataDictItemEO.class, itemEO);
        dataDictItemService.updateItem(itemEO);
        return getObject();
    }

    /**
     * @param itemId
     * @param dictId
     * @param flag
     * @return Object return type
     * @throws
     * @Title: setDefault
     * @Description: 设置默认值
     */
    @RequestMapping("setDefault")
    @ResponseBody
    public Object setDefault(Long itemId, Long dictId, Integer flag) {
        dataDictItemService.setDefault(itemId, dictId, flag);
        return getObject();
    }

}
