package cn.lonsun.staticcenter.generate.tag.impl.delegatemgr;

import cn.lonsun.delegatemgr.entity.AdviceEO;
import cn.lonsun.delegatemgr.entity.DelegateEO;
import cn.lonsun.delegatemgr.internal.service.IDelegateService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 代表管理详细信息<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-12<br/>
 */
@Component
public class DelegateDetailBeanService extends AbstractBeanService {
    @Resource
    private IDelegateService delegateService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Long id=paramObj.getLong("DId");
        DelegateEO delegateEO=delegateService.getEntity(DelegateEO.class,id);
        return delegateEO;
    }
}
