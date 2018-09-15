package cn.lonsun.content.internal.service.impl;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.dao.IKnowledgeBaseDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.service.impl.BaseService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.site.internal.entity.ColumnMgrEO;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ArrayFormat;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.SysLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author zhangmf
 * @version 2018-05-23 17:27
 */
@Service
public class KnowledgeBaseServiceImpl extends BaseService<KnowledgeBaseEO> implements IKnowledgeBaseService {

    @Autowired
    IKnowledgeBaseDao knowledgeBaseDao;

    @Autowired
    IBaseContentService baseContentService;

    @Override
    public Pagination getPage(ContentPageVO query) {
        return knowledgeBaseDao.getPage(query);
    }

    @Override
    public KnowledgeBaseEO saveOrupdate(KnowledgeBaseVO knowledgeBaseVO) {
        BaseContentEO content = null;
        KnowledgeBaseEO kbEO = null;
        if(knowledgeBaseVO.getIsPublish() == 1 && knowledgeBaseVO.getPublishDate() == null){
            knowledgeBaseVO.setPublishDate(new Date());
        }
        if(knowledgeBaseVO.getContentId() != null){
            //更新
            content = baseContentService.getEntity(BaseContentEO.class, knowledgeBaseVO.getContentId());
            content.setTitle(knowledgeBaseVO.getTitle());
//            content.setSubTitle();
//            content.setTitleColor();
//            content.setIsBold();
//            content.setIsUnderline();
//            content.setIsTilt();
//            content.setResources();
//            content.setIsNew();
//            content.setIsHot();
//            content.setIsTop();
//            content.setTopValidDate();
//            content.setIsTitle();
//            content.setAuthor();
//            content.setRedirectLink();
//            content.setIsAllowComments();
//            content.setIsJob();
            AppUtil.copyProperties(content,knowledgeBaseVO);
            if (AppUtil.isEmpty(knowledgeBaseVO.getResources())) {
                ColumnMgrEO columnMgr = CacheHandler.getEntity(ColumnMgrEO.class, knowledgeBaseVO.getColumnId());
                content.setResources(columnMgr.getName());
            }

            //content.setNum(knowledgeBaseVO.getSortNum());
            //如果是已发布状态,保存之后,状态设置为正在发布,即需要重新发布
            if(knowledgeBaseVO.getIsPublish()!=null&&knowledgeBaseVO.getIsPublish()==1){
                content.setIsPublish(2);
            }else{
                content.setIsPublish(knowledgeBaseVO.getIsPublish());
            }
            content.setPublishDate(knowledgeBaseVO.getPublishDate());
            baseContentService.updateEntity(content);

            kbEO = getEntity(KnowledgeBaseEO.class, knowledgeBaseVO.getKnowledgeBaseId());
            AppUtil.copyProperties(kbEO,knowledgeBaseVO);
            updateEntity(kbEO);

            SysLog.log("修改问答知识库 ：栏目（" + ColumnUtil.getColumnName(content.getColumnId(), content.getSiteId()) + "），标题（" + content.getTitle()+"）", "KnowledgeBaseEO",
                    CmsLogEO.Operation.Update.toString());
        }else{
            //新增
            content = new BaseContentEO();
//            content.setTitle(knowledgeBaseVO.getTitle());
//            content.setColumnId(knowledgeBaseVO.getColumnId());
//            content.setSiteId(knowledgeBaseVO.getSiteId());
            AppUtil.copyProperties(content,knowledgeBaseVO);
            if (AppUtil.isEmpty(knowledgeBaseVO.getResources())) {
                ColumnMgrEO columnMgr = CacheHandler.getEntity(ColumnMgrEO.class, knowledgeBaseVO.getColumnId());
                content.setResources(columnMgr.getName());
            }

            content.setTypeCode(BaseContentEO.TypeCode.knowledgeBase.toString());
//            content.setNum(knowledgeBaseVO.getSortNum());
            if(knowledgeBaseVO.getIsPublish()!=null&&knowledgeBaseVO.getIsPublish()==1){
                content.setIsPublish(2);
            }else{
                content.setIsPublish(knowledgeBaseVO.getIsPublish());
            }
            content.setPublishDate(knowledgeBaseVO.getPublishDate());
            baseContentService.saveEntity(content);
            kbEO = new KnowledgeBaseEO();
            AppUtil.copyProperties(kbEO,knowledgeBaseVO);
            kbEO.setContentId(content.getId());
            saveEntity(kbEO);
            SysLog.log("新增问答知识库 ：栏目（" + ColumnUtil.getColumnName(content.getColumnId(), content.getSiteId()) + "），标题（" + content.getTitle()+"）", "KnowledgeBaseEO",
                    CmsLogEO.Operation.Add.toString());
        }
        CacheHandler.saveOrUpdate(BaseContentEO.class,content);
        return kbEO;
    }

    @Override
    public void delete(Long[] ids, Long[] contentIds) {
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
        if (list != null && list.size() > 0) {
            for (BaseContentEO content : list) {
                CacheHandler.delete(BaseContentEO.class, content);
            }
        }
        baseContentService.delete(BaseContentEO.class, contentIds);
    }

    @Override
    public void batchCompletelyDelete(Long[] contentIds) {
        knowledgeBaseDao.batchCompletelyDelete(contentIds);
        SysLog.log("彻底删除问答知识库>> ID：" + ArrayFormat.ArrayToString(contentIds), "KnowledgeBaseEO", CmsLogEO.Operation.Update.toString());
    }
}
