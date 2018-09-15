package cn.lonsun.staticcenter.generate.tag.impl.delegatemgr;

import cn.lonsun.delegatemgr.entity.ProposalEO;
import cn.lonsun.delegatemgr.internal.service.IProposalService;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 *建议议案详情标签 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-28<br/>
 */
@Component
public class ProposalDetailBeanService  extends AbstractBeanService {
    @Resource
    private IProposalService proposalService;
    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Long id=paramObj.getLong("AId");
        ProposalEO proposalEO=proposalService.getEntity(ProposalEO.class,id);
        return proposalEO;
    }
}
