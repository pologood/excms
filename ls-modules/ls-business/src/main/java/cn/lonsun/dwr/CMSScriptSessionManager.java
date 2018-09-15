/*
 * CMSScriptSessionManager.java         2015年11月28日 <br/>
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

package cn.lonsun.dwr;

import org.directwebremoting.impl.DefaultScriptSessionManager;

/**
 * dwr scriptSession管理 <br/>
 *
 * @date 2015年11月28日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class CMSScriptSessionManager extends DefaultScriptSessionManager {

    public CMSScriptSessionManager() {
        // 绑定一个ScriptSession增加销毁事件的监听器
        this.addScriptSessionListener(new CMSScriptSessionListener());
    }
}