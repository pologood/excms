package cn.lonsun.content.onlineDeclaration.internal.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.onlineDeclaration.internal.dao.IDeclarationRecordDao;
import cn.lonsun.content.onlineDeclaration.internal.entity.DeclarationRecordEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IDeclarationRecordService;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.rbac.internal.entity.OrganEO;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-13<br/>
 */
@Service("declarationRecordService")
public class DeclarationRecordServiceImpl extends MockService<DeclarationRecordEO> implements IDeclarationRecordService{

    @Autowired
    private IDeclarationRecordDao recordDao;

    @Override
    public Pagination getPage(PageQueryVO pageVO, Long declarationId) {
        Pagination page= recordDao.getPage(pageVO,declarationId);
        if(page!=null&&page.getData()!=null&&page.getData().size()>0){
            List<DeclarationRecordEO> list=( List<DeclarationRecordEO> )page.getData();
            for(DeclarationRecordEO eo:list){
                OrganEO organEO= CacheHandler.getEntity(OrganEO.class,eo.getTransToId());
                if(organEO!=null){
                    eo.setTransToName(organEO.getName());
                }
                OrganEO organEO1= CacheHandler.getEntity(OrganEO.class,eo.getCreateOrganId());
                if(organEO!=null){
                    eo.setTransUserName(organEO1.getName());
                }
            }
        }
        return page;
    }
}
