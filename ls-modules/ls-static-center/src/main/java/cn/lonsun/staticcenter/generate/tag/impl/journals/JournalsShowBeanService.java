package cn.lonsun.staticcenter.generate.tag.impl.journals;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ContextHolderUtils;
import cn.lonsun.journals.dao.IJournalsDao;
import cn.lonsun.journals.entity.JournalsEO;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lonsun on 2017-1-6.
 */
@Component
public class JournalsShowBeanService extends AbstractBeanService {
    @Autowired
    private IJournalsDao journalsDao;
    @Value("${mangerUrl}")
    private String mangerUrl;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Long contentId  = ContextHolder.getContext().getContentId();
        Map<String,Object> map =new HashMap<String, Object>();
        map.put("baseContentId",contentId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        JournalsEO eo =  journalsDao.getEntity(JournalsEO.class, map);
        if(null!=eo){
            eo.setFilePath(mangerUrl + eo.getFilePath());

        }
        return eo;
    }
}
