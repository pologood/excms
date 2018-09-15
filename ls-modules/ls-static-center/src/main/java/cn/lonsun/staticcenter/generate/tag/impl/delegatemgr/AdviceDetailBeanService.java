package cn.lonsun.staticcenter.generate.tag.impl.delegatemgr;

import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 建议管理详细信息<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-12<br/>
 */
@Component
public class AdviceDetailBeanService extends AbstractBeanService {

    @Resource
    private IAdviceService adviceService;


    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Long id=paramObj.getLong("AId");
        AdviceEO adviceEO=adviceService.getEntity(AdviceEO.class,id);
        return adviceEO;
    }
}
