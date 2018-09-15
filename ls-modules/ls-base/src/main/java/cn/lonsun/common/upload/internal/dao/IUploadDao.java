/*
 * IUploadDao.java         2014年11月6日 <br/>
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

package cn.lonsun.common.upload.internal.dao;

import java.util.List;

import cn.lonsun.common.upload.internal.entity.AttachmentEO;
import cn.lonsun.core.base.dao.IBaseDao;


/**
 * TODO				<br/>
 *	 
 * @date     2014年11月6日 	<br/>
 * @author 	 zhangx	<br/>
 * @version	 v1.0 		<br/>
 */
public interface IUploadDao extends IBaseDao<AttachmentEO> {

    /**
     * 根据uuid查询附件对象
     * 
     * @param uuid 
     * @return 
     */
    public AttachmentEO getAttachmentEO(String uuid);
    
    
    /**
     * 获取附件列表的方法
     * @Time 2014年9月26日 上午10:06:32
     * @param uuids 附件的ID数组
     * @return
     */
    public List<AttachmentEO> getAttachs(String[] uuids);
}

