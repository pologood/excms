package cn.lonsun.webservice.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.webservice.PublicClassService;
import cn.lonsun.webservice.to.WebServiceTO;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by fth on 2016/11/2.
 */
@Service("PublicClassServiceImpl_webservice")
public class PublicClassServiceImpl implements PublicClassService {

    @Override
    public WebServiceTO getPublicClassList() {
        WebServiceTO to = new WebServiceTO();
        List<PublicClassEO> publicClassList = CacheHandler.getList(PublicClassEO.class, CacheGroup.CMS_ALL, AMockEntity.RecordStatus.Normal.toString());
        return to.success(JSON.toJSONString(publicClassList), "操作成功！");
    }
}
