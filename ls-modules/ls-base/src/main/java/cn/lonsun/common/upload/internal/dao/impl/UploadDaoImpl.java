/*
 * UploadDaoImpl.java         2014年11月6日 <br/>
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

package cn.lonsun.common.upload.internal.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import cn.lonsun.common.upload.internal.dao.IUploadDao;
import cn.lonsun.common.upload.internal.entity.AttachmentEO;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.dao.impl.BaseDao;


/**
 * TODO				<br/>
 *	 
 * @date     2014年11月6日 	<br/>
 * @author 	 zhangx	<br/>
 * @version	 v1.0 		<br/>
 */
@Repository("uploadDao")
public class UploadDaoImpl extends BaseDao<AttachmentEO> implements IUploadDao {

    @Override
    public AttachmentEO getAttachmentEO(String uuid) {
        if(AppUtil.isEmpty(uuid)){
            return null;            
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("attachmentUuid", uuid);
        List<AttachmentEO> list = this.getEntities(AttachmentEO.class, map);
        if( null != list && list.size() > 0){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public List<AttachmentEO> getAttachs(String[] uuids) {
        
        if(uuids == null || uuids.length <= 0){
            return null;
        }
        List<AttachmentEO> list = new ArrayList<AttachmentEO>();
        Map<String, Object> map = new HashMap<String, Object>();
        for (String uuid : uuids) {
            if( !AppUtil.isEmpty(uuid) ){
                map.put("attachmentUuid", uuid);
                List<AttachmentEO> eoList = getEntities(AttachmentEO.class, map);
                list.addAll(eoList);
            }
        }
        return list;
    }
    
    
    

    
}

