/*
 * KeyWordsHeatController.java         2016年4月6日 <br/>
 *
 * Copyright (c) 1994-1999 AnHui LonSun, Inc. <br/>
 * All rights reserved.	<br/>
 *
 * This software is the confidential and proprietary information of AnHui	<br/>
 * LonSun, Inc. ("Confidential Information").  You shall not	<br/>
 * disclose such Confidential Information and shall use it only in	<br/>
 * accordance with the terms of the license agreement you entered into	<br/>
 * with Sun. <br/>
 */

package cn.lonsun.heatAnalysis;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.heatAnalysis.entity.KeyWordsHeatEO;
import cn.lonsun.heatAnalysis.service.IKeyWordsHeatService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 关键词热度分析 <br/>
 *
 * @date 2016年4月6日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
@Controller
@RequestMapping("/heatAnalysis/keyWords")
public class KeyWordsHeatController extends BaseController {

    @Resource
    private IKeyWordsHeatService keyWordsHeatService;

    /**
     * 获取关键词热度列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    @ResponseBody
    @RequestMapping("getKeyWordsHeatPage")
    public Object getKeyWordsHeatPage(ContentPageVO contentPageVO) {
        contentPageVO.setSiteId(LoginPersonUtil.getSiteId());
        return keyWordsHeatService.getKeyWordsHeatPage(contentPageVO);
    }

    /**
     * 获取关键词排序列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    @ResponseBody
    @RequestMapping("getKeyWordsSortPage")
    public Object getKeyWordsSortPage(ContentPageVO contentPageVO) {
        contentPageVO.setSiteId(LoginPersonUtil.getSiteId());
        return keyWordsHeatService.getKeyWordsSortPage(contentPageVO);
    }

    /**
     * 添加关键词排序列表
     *
     * @author fangtinghua
     * @param contentPageVO
     * @return
     */
    @ResponseBody
    @RequestMapping("saveToSort")
    public Object saveToSort(KeyWordsHeatEO keyWordsHeatEO) {
        keyWordsHeatService.saveToSort(keyWordsHeatEO);
        return getObject();
    }

    /**
     * 转向编辑页面
     *
     * @author fangtinghua
     * @return
     */
    @RequestMapping("edit")
    public String edit() {
        return "/heatAnalysis/keyWords/edit";
    }

    /**
     * 新增编辑
     *
     * @author fangtinghua
     * @param keyWordsHeatEO
     * @return
     */
    @ResponseBody
    @RequestMapping("saveOrUpdate")
    public Object saveOrUpdate(KeyWordsHeatEO keyWordsHeatEO) {
        if (StringUtils.isEmpty(keyWordsHeatEO.getKeyWords())) {
            return ajaxErr("关键词不能为空！");
        }
        keyWordsHeatService.saveEntity(keyWordsHeatEO);
        return getObject(keyWordsHeatEO);
    }

    /**
     * 删除
     *
     * @author fangtinghua
     * @param ids
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public Object delete(@RequestParam("ids[]") Long[] ids) {
        if (null == ids || ids.length == 0) {
            return ajaxErr("Id不能为空！");
        }
        keyWordsHeatService.delete(KeyWordsHeatEO.class, ids);
        return getObject();
    }

    /**
     * 获取对象
     *
     * @author fangtinghua
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("getKeyWordsHeat")
    public Object getKeyWordsHeat(Long id) {
        KeyWordsHeatEO keyWordsHeatEO = null;
        if (null != id) {
            keyWordsHeatEO = keyWordsHeatService.getEntity(KeyWordsHeatEO.class, id);
        } else {
            keyWordsHeatEO = new KeyWordsHeatEO();
        }
        return keyWordsHeatEO;
    }
}