/*
 * RuntimeServices.java         2016年7月13日 <br/>
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

package cn.lonsun.velocity;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.util.RuntimeServicesAware;

/**
 * velocity运行时服务 <br/>
 *
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class RuntimeServices implements ReferenceInsertionEventHandler, RuntimeServicesAware {
    // velocity运行时服务
    private static org.apache.velocity.runtime.RuntimeServices runtimeServices = null;

    public static org.apache.velocity.runtime.RuntimeServices getRuntimeServices() {
        return runtimeServices;
    }

    @Override
    public void setRuntimeServices(org.apache.velocity.runtime.RuntimeServices rs) {
        runtimeServices = rs;
    }

    /**
     * 对模板解析值做特殊处理，后续可以针对html进行解码转码
     * 
     * @see org.apache.velocity.app.event.ReferenceInsertionEventHandler#referenceInsert(java.lang.String,
     *      java.lang.Object)
     */
    @Override
    public Object referenceInsert(String reference, Object value) {
        return value;
    }
}