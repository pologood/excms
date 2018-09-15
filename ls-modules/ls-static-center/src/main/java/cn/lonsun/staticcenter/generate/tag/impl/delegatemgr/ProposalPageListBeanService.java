package cn.lonsun.staticcenter.generate.tag.impl.delegatemgr;

import cn.lonsun.core.util.Pagination;
import cn.lonsun.delegatemgr.internal.service.IAdviceService;
import cn.lonsun.delegatemgr.internal.service.IProposalService;
import cn.lonsun.delegatemgr.vo.DelegateQueryVO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 建议议案分页标签<br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2017-6-28<br/>
 */
@Component
public class ProposalPageListBeanService extends AbstractBeanService {

    @Resource
    private IProposalService proposalService;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        String session=paramObj.getString("session_");
        DelegateQueryVO queryVO=new DelegateQueryVO();
        queryVO.setSession(session);
        queryVO.setSiteId(context.getSiteId());
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        queryVO.setPageSize(pageSize);
        queryVO.setPageIndex(pageIndex);
        Pagination page=proposalService.getPage(queryVO);
        return page;
    }

}
