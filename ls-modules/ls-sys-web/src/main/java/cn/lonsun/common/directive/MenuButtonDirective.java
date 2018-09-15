/*
 * MenuButtonDirective.java         2016年4月8日 <br/>
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

package cn.lonsun.common.directive;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.Node;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.util.SessionUtil;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.rbac.login.InternalAccount;
import cn.lonsun.system.role.internal.service.IMenuRoleService;
import cn.lonsun.util.LoginPersonUtil;

/**
 * 菜单按钮权限 <br/>
 *
 * @date 2016年4月8日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class MenuButtonDirective extends ButtonDirective {

    private IMenuRoleService menuRoleService = SpringContextHolder.getBean(IMenuRoleService.class);

    @Override
    public String getName() {
        return "MenuButton";
    }

    @Override
    public List<String> getCodeList(InternalContextAdapter context, Node node) {
        // 按钮code列表
        List<String> codeList = new ArrayList<String>();
        Long menuId = null;
        // 如果一个参数都没写，那么采用当前点击的菜单id，并且是取当前菜单下的按钮code列表
        if (node.jjtGetNumChildren() == 0) {
            menuId = SessionUtil.getLongProperty(LoginPersonUtil.getSession(), InternalAccount.USER_MENUID);
        } else if (node.jjtGetNumChildren() == 1) {// 如果是一个参数
            menuId = NumberUtils.toLong(node.jjtGetChild(0).value(context).toString());
        } else if (node.jjtGetNumChildren() == 2) {// 如果是2个参数
            menuId = NumberUtils.toLong(node.jjtGetChild(0).value(context).toString());
            boolean self = BooleanUtils.toBoolean(node.jjtGetChild(1).value(context).toString());
            if (self) {// 如果是菜单自己
                IndicatorEO indicatorEO = CacheHandler.getEntity(IndicatorEO.class, menuId);
                if (null != indicatorEO) {
                    boolean menuShow = menuRoleService.isMenuShow(indicatorEO.getUri());
                    if (menuShow) {
                        codeList.add(String.valueOf(menuId));
                    }
                }
                return codeList;
            }
        }
        if (null != menuId) {
            List<IndicatorEO> buttonList = menuRoleService.getCurUserHasRights(menuId);
            if (null != buttonList && !buttonList.isEmpty()) {
                for (IndicatorEO eo : buttonList) {
                    codeList.add(eo.getCode());
                }
            }
        }
        return codeList;
    }
}