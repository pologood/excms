/*
 * TaskInfoQueryVO.java         2016年5月17日 <br/>
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

package cn.lonsun.statictask.internal.vo;

import cn.lonsun.common.vo.PageQueryVO;

/**
 * 错误日志查询 <br/>
 *
 * @date 2016年5月17日 <br/>
 * @author fangtinghua <br/>
 * @version v1.0 <br/>
 */
public class TaskInfoQueryVO extends PageQueryVO {

    private Long taskId;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
}