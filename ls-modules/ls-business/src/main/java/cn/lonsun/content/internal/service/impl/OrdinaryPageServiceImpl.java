package cn.lonsun.content.internal.service.impl;

import cn.lonsun.util.ColumnUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IOrdinaryPageService;
import cn.lonsun.content.vo.OrdinaryPageVO;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.SysLog;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-11-18<br/>
 */
@Service("ordinaryPageService")
public class OrdinaryPageServiceImpl  extends MockService<BaseContentEO> implements IOrdinaryPageService {
    @Autowired
    private IBaseContentService baseService;
    @Autowired
    private ContentMongoServiceImpl contentMongoService;
    @Override
    public BaseContentEO saveEO(OrdinaryPageVO vo) {
        BaseContentEO eo=new BaseContentEO();
        if(vo.getId()==null){//新增
            eo.setTitle(vo.getTitle());
            eo.setIsPublish(vo.getIsPublish());
            eo.setTypeCode(BaseContentEO.TypeCode.ordinaryPage.toString());
            eo.setColumnId(vo.getColumnId());
            eo.setSiteId(vo.getSiteId());
            eo.setTitleColor(vo.getTitleColor());
            eo.setIsBold(vo.getIsBold());
            eo.setIsUnderline(vo.getIsUnderline());
            eo.setIsTilt(vo.getIsTilt());
            eo.setRemarks(vo.getRemarks());
            eo.setImageLink(vo.getImageLink());
            eo.setPublishDate(vo.getPublishDate());
            baseService.saveEntity(eo);
            CacheHandler.saveOrUpdate(BaseContentEO.class,eo);
            SysLog.log("新增普通页面：栏目（"+ ColumnUtil.getColumnName(eo.getColumnId(),eo.getSiteId())+"），标题（" + eo.getTitle()+"）",
                    "BaseContentEO", CmsLogEO.Operation.Add.toString());
        }else{//修改
            eo=baseService.getEntity(BaseContentEO.class,vo.getId());
            eo.setTitle(vo.getTitle());
            eo.setIsPublish(1);
            eo.setColumnId(vo.getColumnId());
            eo.setSiteId(vo.getSiteId());
            eo.setTitleColor(vo.getTitleColor());
            eo.setIsBold(vo.getIsBold());
            eo.setIsUnderline(vo.getIsUnderline());
            eo.setIsTilt(vo.getIsTilt());
            eo.setIsPublish(vo.getIsPublish());
            eo.setRemarks(vo.getRemarks());
            eo.setImageLink(vo.getImageLink());
            eo.setPublishDate(vo.getPublishDate());
            baseService.updateEntity(eo);
            CacheHandler.saveOrUpdate(BaseContentEO.class,eo);
            SysLog.log("修改普通页面：栏目（"+ ColumnUtil.getColumnName(eo.getColumnId(),eo.getSiteId())+"），标题（" + eo.getTitle()+"）",
                    "BaseContentEO", CmsLogEO.Operation.Update.toString());

        }
        if(BaseContentEO.TypeCode.ordinaryPage.toString().equals(vo.getTypeCode())){
            ContentMongoEO _eo=new ContentMongoEO();
            _eo.setId(eo.getId());
            _eo.setContent(vo.getContent());
            contentMongoService.save(_eo);
        }
        return eo;
    }
}
