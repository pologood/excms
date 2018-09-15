package cn.lonsun.content.controller;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.KnowledgeBaseEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IKnowledgeBaseService;
import cn.lonsun.content.messageBoard.controller.internal.entity.MessageBoardEO;
import cn.lonsun.content.messageBoard.service.IMessageBoardService;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.content.vo.KnowledgeBaseVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.job.timingjob.jobimpl.NewsIssueTaskImpl;
import cn.lonsun.job.timingjob.jobimpl.NewsTopTaskImpl;
import cn.lonsun.job.util.ScheduleJobUtil;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.system.datadictionary.internal.entity.DataDictItemEO;
import cn.lonsun.system.datadictionary.internal.service.IDataDictItemService;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.ColumnUtil;
import cn.lonsun.util.SysLog;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.lonsun.cache.client.CacheHandler.getEntity;

/**
 * 问答知识库 controller 入口
 * @author zhangmf
 * @version 2018-05-23 14:29
 */
@Controller
@RequestMapping(value = "knowledgeBase")
public class KnowledgeBaseController extends BaseController {
    @Autowired
    private IBaseContentService baseContentService;

    @Autowired
    private IKnowledgeBaseService knowledgeBaseService;

    @Autowired
    private IDataDictItemService dataDictItemService;

    @Autowired
    private IMessageBoardService messageBoardService;

    @RequestMapping("index")
    public String index(Long pageIndex,Long indicatorId, ModelMap map) {
        map.put("indicatorId", indicatorId);
        return "/content/knowledgebase/knowledge_list";
    }

    /**
     * 跳转编辑
     * @param knowledgeBaseId
     * @param m
     * @return
     */
    @RequestMapping("edit")
    public String edit(Long knowledgeBaseId,Long contentId,Model m) {
        m.addAttribute("knowledgeBaseId", knowledgeBaseId);
        m.addAttribute("contentId", contentId);
        return "/content/knowledgebase/knowledge_edit";
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("getPage")
    @ResponseBody
    public Object getPage(ContentPageVO query){
        if(query.getColumnId() == null || query.getSiteId() == null){
            throw new BaseRunTimeException(TipsMode.Message.toString(), "栏目或站点id不能为空");
        }
        if (StringUtils.isEmpty(query.getTypeCode())) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "typeCode不能为空");
        }
        // 页码与查询最多查询数据量纠正
        if (query.getPageIndex()==null||query.getPageIndex() < 0) {
            query.setPageIndex(0L);
        }
        Integer size = query.getPageSize();
        if (size==null||size <= 0 || size > Pagination.MAX_SIZE) {
            query.setPageSize(15);
        }

        Pagination page = knowledgeBaseService.getPage(query);
        return getObject(page);
    }

    /**
     * 获取分类
     * @param map
     * @return
     */
    @RequestMapping("getClass")
    @ResponseBody
    public Object getClass(ModelMap map) {
        List<DataDictItemEO> eos = dataDictItemService.getListByDictId(6331440l);
        //用publicClassEO接收对象
        List<PublicClassEO> classes = new ArrayList<PublicClassEO>();
        PublicClassEO tempEO;
        for (DataDictItemEO eo : eos) {
            Long id = eo.getItemId();
            tempEO = new PublicClassEO();
            tempEO.setId(id);
            tempEO.setParentId(0l);
            tempEO.setName(eo.getName());
            tempEO.setCode(eo.getCode());
            tempEO.setIsSelect(false);

            classes.add(tempEO);

            JSONObject obj = new JSONObject(eo.getValue());
            Iterator<String> iterator =obj.keys();
            //遍历json对象,拿出字节点
            while(iterator.hasNext()){
                String key = iterator.next();
                String value = obj.getString(key);
                //匹配数字
                String regEx="[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(key);
                //设置子节点的id
                Long tempId = id*10 + Long.parseLong(m.replaceAll("").trim());

                tempEO = new PublicClassEO();
                tempEO.setId(tempId);
                tempEO.setParentId(id);
                tempEO.setName(value);
                tempEO.setCode(key);
                tempEO.setIsSelect(false);

                classes.add(tempEO);
            }
        }
        return classes;
    }

    /**
     *
     *
     * @author fangtinghua
     * @param map
     * @return
     */
    @RequestMapping("getKnowLedgeBaseById")
    @ResponseBody
    public Object getKnowLedgeBaseById(Long knowledgeBaseId,Long siteId,Long columnId,String typeCode, ModelMap map) {
        KnowledgeBaseVO knowledgeBaseVO = null;
        if (!AppUtil.isEmpty(knowledgeBaseId)) {
            ContentPageVO query = new ContentPageVO();
            query.setIdArray(new Long[]{knowledgeBaseId});
            query.setSiteId(siteId);
            query.setColumnId(columnId);
            query.setTypeCode(typeCode);
            Pagination page = knowledgeBaseService.getPage(query);
            List<KnowledgeBaseVO> list = (List<KnowledgeBaseVO>) page.getData();
            if (list.size() > 0) {
                knowledgeBaseVO = list.get(0);
            }
        }else {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "id不能为空");
        }
        return getObject(knowledgeBaseVO);
    }

    @RequestMapping("save")
    @ResponseBody
    public Object save(KnowledgeBaseVO knowledgeBaseVO){
        KnowledgeBaseEO knowledgeBaseEO = knowledgeBaseService.saveOrupdate(knowledgeBaseVO);
        Long id = knowledgeBaseEO.getContentId();
        BaseContentEO contentEO = getEntity(BaseContentEO.class,id);
        //发布
        if (contentEO.getIsPublish()!=null&&contentEO.getIsPublish()==2) {
            MessageSenderUtil
                    .publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);

            SysLog.log("发布内容"+" ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "KnowledgeBaseEO",
                    CmsLogEO.Operation.Update.toString());
        } else {
            MessageSenderUtil.publishContent(new MessageStaticEO(contentEO.getSiteId(), contentEO.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()),
                    2);
            SysLog.log("取消发布内容"+" ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "KnowledgeBaseEO",
                    CmsLogEO.Operation.Update.toString());
        }

        if (contentEO.getIsTop() == 1 && contentEO.getTopValidDate() != null) {
            ScheduleJobUtil.addScheduleJob("新闻置顶有效期", NewsTopTaskImpl.class.getName(), ScheduleJobUtil.dateToCronExpression(contentEO.getTopValidDate()),
                    String.valueOf(id));
        }

        //设置定时发布
        Integer isJob = 0;
        if (contentEO.getIsJob() == 1 && contentEO.getJobIssueDate() != null) {
            isJob = 1;
            ScheduleJobUtil.addOrDelScheduleJob("新闻定时发布日期",
                    NewsIssueTaskImpl.class.getName(), isJob == 0 ? null : ScheduleJobUtil.dateToCronExpression(contentEO.getJobIssueDate()), String.valueOf(id), isJob);
        }
        return getObject();
    }

    @RequestMapping("delete")
    @ResponseBody
    public Object delete(@RequestParam("ids") Long[] ids, @RequestParam("contentIds") Long[] contentIds) {
        if (ids == null || ids.length < 1) {
            throw new BaseRunTimeException(TipsMode.Message.toString(), "请选择要删除的项！");
        }
        List<BaseContentEO> list = baseContentService.getEntities(BaseContentEO.class, contentIds);
        if (list != null && list.size() > 0) {
            Integer isPublish = 0;
            for (BaseContentEO contentEO : list) {
                if (contentEO != null && contentEO.getIsPublish() != null && contentEO.getIsPublish().intValue() == 1) {
                    isPublish = 1;
                    break;
                }
                String newsType = "";
                if(BaseContentEO.TypeCode.knowledgeBase.toString().equals(contentEO.getTypeCode())){
                    newsType = "问答知识库";
                }


                //添加操作日志
                if(list.size()>1){
                    SysLog.log("批量删除"+newsType+" ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "KnowledgeBaseEO",
                            CmsLogEO.Operation.Update.toString());
                }else{
                    SysLog.log("删除"+newsType+" ：栏目（" + ColumnUtil.getColumnName(contentEO.getColumnId(), contentEO.getSiteId()) + "），标题（" + contentEO.getTitle()+"）", "KnowledgeBaseEO",
                            CmsLogEO.Operation.Update.toString());
                }
            }

            //清空 留言 的问题knowledgeId字段
            for (Long id : ids) {
                //通过knowledgeId查找
                //更新messageBoard对象的 knowledgeBaseId
                MessageBoardEO eo = messageBoardService.getMessageBoardByKnowledgeBaseId(id);
                if (eo != null) {
                    eo.setKnowledgeBaseId(null);
                    messageBoardService.updateEntity(eo);
                }
            }

            BaseContentEO baseContentEO = getEntity(BaseContentEO.class, contentIds[0]);
            knowledgeBaseService.delete(ids,contentIds);
            if(isPublish!=null&&isPublish.intValue()==1){
                MessageSenderUtil.publishContent(
                        new MessageStaticEO(baseContentEO.getSiteId(), baseContentEO.getColumnId(),ids).setType(MessageEnum.UNPUBLISH.value()), 2);
            }
        }
        return getObject();
    }
}
