/*
 * Powered By zhongjun
 * createtime 2017-11-16 17:55:00
 */

package cn.lonsun.search;

import cn.lonsun.activemq.MessageSenderUtil;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.content.internal.dao.IContentReferRelationDao;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.entity.ContentReferRelationEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.internal.service.IContentReferRelationService;
import cn.lonsun.content.internal.service.IVideoNewsService;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.optrecord.entity.ContentOptRecordEO;
import cn.lonsun.content.optrecord.util.ContentOptRecordUtil;
import cn.lonsun.content.vo.ContentPageVO;
import cn.lonsun.core.base.controller.BaseController;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.TipsMode;
import cn.lonsun.message.internal.entity.MessageEnum;
import cn.lonsun.message.internal.entity.MessageStaticEO;
import cn.lonsun.mongodb.entity.ContentMongoEO;
import cn.lonsun.mongodb.service.ContentMongoServiceImpl;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.publicInfo.internal.entity.PublicApplyEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.rbac.internal.service.IOrganService;
import cn.lonsun.search.internal.entity.SearchTaskEO;
import cn.lonsun.search.internal.service.ISearchTaskService;
import cn.lonsun.site.template.util.ResponseData;
import cn.lonsun.system.systemlog.internal.entity.CmsLogEO;
import cn.lonsun.util.LoginPersonUtil;
import cn.lonsun.util.SysLog;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author zhongjun
 * @createtime 2017-11-16 17:55:00
 */
@Controller
@RequestMapping("/globalSearch")
public class GlobalSearchController extends BaseController {

    @Autowired
    private ISearchTaskService searchTaskService;

    @Resource
    private IBaseContentService baseContentService;

    @Autowired
    private ContentMongoServiceImpl contentMongoService;

    @Autowired
    private IContentReferRelationDao contentReferRelationDao;

    @Autowired
    private IVideoNewsService videoService;

    @Autowired
    private IWorkGuideService workGuideService;

    @Autowired
    private IInterviewInfoService interviewInfoService;

    @Resource
    private IContentReferRelationService contentReferRelationService;

    @Autowired
    private IPublicCatalogService publicCatalogService;

    @Autowired
    private IPublicContentService publicContentService;

    @Autowired
    private IOrganService organService;
    /**主界面
	 * @author zhongjun 
	 * @createtime 2017-11-16 17:55:00
	 */
	@RequestMapping(method= RequestMethod.GET)
    public String index(){
		return "globalSearch/task/index";
    }

    /**主界面
     * @author zhongjun
     * @createtime 2017-11-16 17:55:00
     */
    @RequestMapping(value = "columnSelect")
    public String columnSelect(){
        return "globalSearch/column_select";
    }

    /**主界面
	 * @author zhongjun
	 * @createtime 2017-11-16 17:55:00
	 */
	@RequestMapping(value ="edit", method= RequestMethod.GET)
    public ModelAndView resultEditor(ModelAndView mv, Long id, String typeCode){
	    mv.addObject("id", id);
	    mv.addObject("typeCode", typeCode);
        BaseContentEO content = baseContentService.getEntity(BaseContentEO.class, id);
        Criteria criteria = Criteria.where("_id").is(content.getId());
        Query query = new Query(criteria);
        ContentMongoEO _eo = contentMongoService.queryOne(query);
        if (_eo != null){
            content.setArticle(_eo.getContent());
        }
        mv.addObject("data", content);
        mv.addObject("datajson", JSONObject.toJSONString(content, SerializerFeature.WriteMapNullValue));
        mv.setViewName("globalSearch/edit");
		return mv;
    }

    /**主界面
	 * @author zhongjun
	 * @createtime 2017-11-16 17:55:00
	 */
	@RequestMapping(value="result", method= RequestMethod.GET)
    public ModelAndView result(ModelAndView mv, Long taskId){
	    mv.addObject("taskId", taskId);
        mv.setViewName( "globalSearch/result");
		return mv;
    }
	/**分页查询
	 * @author zhongjun 
	 * @createtime 2017-11-16 17:55:00
	 */
    @ResponseBody
    @RequestMapping("task/getPage")
    public Object getPage(PageQueryVO pageinfo){

        return getObject(searchTaskService.getPage(pageinfo));
    }
	
	/** 分页查看搜索结果
	 * @author zhongjun
	 * @createtime 2017-11-16 17:55:00
	 */
    @ResponseBody
    @RequestMapping("getResultPage")
    public Object getResultPage(HttpServletRequest request, PageQueryVO pageinfo, Long taskId, String searchKey, String timeType, String startTime, String endTime){
        Map<String, Object> param = AppUtil.parseRequestToMap(request);
        if(StringUtils.isEmpty(searchKey) && taskId != null){
            SearchTaskEO entity = searchTaskService.getByID(taskId);
            searchKey = entity.getSearchKey();
        }
        if(StringUtils.isEmpty(searchKey)){
            return ajaxErr("搜索内容不能为空");
        }

        return getObject(searchTaskService.queryFromSolr(searchKey, param,  pageinfo.getPageIndex(),pageinfo.getPageSize()));
    }

	/** 保存，若id为空，则新增
	 * @author zhongjun 
	 * @createtime 2017-11-16 17:55:00
	 */
    @ResponseBody
    @RequestMapping("task/save")
    public Object save(String searchKey, PageQueryVO pageinfo){
        if(StringUtils.isEmpty(searchKey)){
            return ajaxErr("搜索内容不能为空！");
        }
        searchTaskService.save(searchKey);
        return getObject();
    }

	/**根据id获取对象
     * @param id
	 * @author zhongjun
	 * @createtime 2017-11-16 17:55:00
	 */
    @ResponseBody
    @RequestMapping("viewDetail")
    public Object getById(Long id){
        return getObject(searchTaskService.getByID(id));
    }

	/**
     * 删除
     * @param id
     * @author zhongjun
	 * @createtime 2017-11-16 17:55:00
     */
    @ResponseBody
    @RequestMapping("task/remove")
    public Object delete(String id) {
        if(AppUtil.isEmpty(id)) {
            return ajaxErr("id不能为空！");
        }
        searchTaskService.delete(id.split(","));
        return getObject();
    }

    //批量发布
    @ResponseBody
    @RequestMapping("batchPublish")
    public Object batchPublish(@RequestParam(value = "ids[]", required = false) Long[] ids,Integer status) {
        if(null != ids && ids.length > 0) {
            for(Long id : ids) {
                BaseContentEO content = CacheHandler.getEntity(BaseContentEO.class,id);
                String typeCode = content.getTypeCode();
                content.setIsPublish(2);//改为中间状态“发布中”
                if (content.getPublishDate() == null) {
                    content.setPublishDate(new Date());
                }
                if (Integer.valueOf(0).equals(content.getVideoStatus())) {
                    return ajaxErr("文章内容中视频未转换完成！");
                }
                if (status != null && status == 1) {
                    SysLog.log("发布内容 >> ID：" + content.getId() + ",标题：" + content.getTitle(),
                            "BaseContentEO", CmsLogEO.Operation.Update.toString());
                } else if (status != null && status == 0) {
                    SysLog.log("取消发布内容 >> ID：" + content.getId() + ",标题：" + content.getTitle(),
                            "BaseContentEO", CmsLogEO.Operation.Update.toString());
                }
                if(typeCode.equals(BaseContentEO.TypeCode.articleNews.toString()) ||
                        typeCode.equals(BaseContentEO.TypeCode.pictureNews.toString()) ||
                            typeCode.equals(BaseContentEO.TypeCode.videoNews.toString()) ||
                                typeCode.equals(BaseContentEO.TypeCode.guestBook.toString()) ||
                                    typeCode.equals(BaseContentEO.TypeCode.messageBoard.toString()) ||
                                        typeCode.equals(BaseContentEO.TypeCode.interviewInfo.toString())) {
                    baseContentService.updateEntity(content);
                    if (status == 1) {
                        MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
                    } else {
                        MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
                    }
                    //增加记录
                    ContentOptRecordUtil.saveOptRecord(ids,status, ContentOptRecordEO.Type.publish);
                } else if(typeCode.equals(BaseContentEO.TypeCode.workGuide.toString())) {
                    //删除办事
                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("contentId",id);
                    CmsWorkGuideEO guide = workGuideService.getEntity(CmsWorkGuideEO.class,params);
                    String returnStr = workGuideService.publish(new Long[]{guide.getId()}, Long.valueOf(status));
                    if(!StringUtils.isEmpty(returnStr)) {
                        if(status == 1) {
                            MessageSenderUtil.publishCopyNews(returnStr);
                        }
                        else {
                            MessageSenderUtil.unPublishCopyNews(returnStr);
                        }
                    }
                }else if(typeCode.equals(BaseContentEO.TypeCode.public_content.toString())) {
                    PublicContentVO vo = publicContentService.getPublicContentByBaseContentId(id);
                    if(AppUtil.isEmpty(vo)) {
                        return ajaxErr("信息公开内容错误！");
                    }
                    if (!PublicApplyEO.PUBLIC_APPLY.toString().equals(vo.getType())) {
                        String publisType = "";
                        publicContentService.updatePublicStatus(ids, vo.getOrganId(), vo.getCatId(), 2);// 不管发布还是取消发布，状态都改为2中间状态
                        if (status == 1) {
                            MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()), 1);
                        } else {
                            publisType = "取消";
                            MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
                        }
                        PublicCatalogEO catalogEO = publicCatalogService.getEntity(PublicCatalogEO.class,vo.getCatId());

                        Long organId = content.getColumnId();//主动公开的columnId存的是单位id
                        OrganEO organEO = organService.getEntity(OrganEO.class,organId);
                        SysLog.log("主动公开目录：" + publisType +"发布内容（"+content.getTitle()+"），目录（"+catalogEO==null?"":catalogEO.getName()
                                +"），公开单位（"+organEO==null?"":organEO.getName()+"）","PublicContentEO", CmsLogEO.Operation.Update.toString());
                        //同步发布或取消发布引用新闻
                        changeReferNewsPublish(content.getId(),status);

                    }else {
                        baseContentService.changePublish(new ContentPageVO(LoginPersonUtil.getSiteId(), content.getColumnId(), status, ids, null));// 依申请公开生成首页
                        MessageSenderUtil.publishContent(new MessageStaticEO(LoginPersonUtil.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.PUBLISH.value()).setScope(MessageEnum.INDEX.value()), 3);// 不用删除索引
                    }
                }
            }
        }
        return ResponseData.success("发布成功!");
    }
    /**
     * 同步发布或取消发布引用新闻
     * @param causeById
     * @param isPublish
     */
    private void changeReferNewsPublish(Long causeById,Integer isPublish){
        if(AppUtil.isEmpty(causeById)||AppUtil.isEmpty(isPublish)){
            return;
        }
        List<ContentReferRelationEO> relationEOS = contentReferRelationService.getByCauseId(causeById,null,ContentReferRelationEO.TYPE.REFER.toString());
        if(relationEOS!=null&&relationEOS.size()>0){
            List<Long> referIds = new ArrayList<Long>();
            for(ContentReferRelationEO relationEO:relationEOS){
                referIds.add(relationEO.getReferId());
            }
            if(referIds.size()>0){
                String msg = baseContentService.publishs(referIds.toArray(new Long[]{}),isPublish);
                if(isPublish.intValue()==0){//取消发布
                    MessageSenderUtil.unPublishCopyNews(msg);
                }else{
                    MessageSenderUtil.publishCopyNews(msg);
                }
            }
        }
    }

    //删除
    @ResponseBody
    @RequestMapping("batchDelete")
    public Object batchDelete(@RequestParam("ids[]") Long[] ids) {
        if(null != ids && ids.length > 0) {
            for(Long id : ids) {
                BaseContentEO content = CacheHandler.getEntity(BaseContentEO.class,id);
                String typeCode = content.getTypeCode();
                if(typeCode.equals(BaseContentEO.TypeCode.articleNews.toString()) ||
                        typeCode.equals(BaseContentEO.TypeCode.pictureNews.toString()) ) {
                    //删除文字，图片新闻
                    List<ContentReferRelationEO> list = contentReferRelationDao.getByCauseId(id, null, ContentReferRelationEO.TYPE.REFER.toString());
                    if (list != null && list.size() > 0) {
                        throw new BaseRunTimeException(TipsMode.Message.toString(), "【" + content.getTitle() + "】新闻已被引用，不可删除！");
                    }
                    baseContentService.delContent(new Long[]{id});
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(LoginPersonUtil.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
                } else if(typeCode.equals(BaseContentEO.TypeCode.videoNews.toString())) {
                    //删除视频新闻
                    String message = videoService.delVideoEO(id);
                    MessageSenderUtil.unPublishCopyNews(message);
                } else if(typeCode.equals(BaseContentEO.TypeCode.guestBook.toString()) ||
                        typeCode.equals(BaseContentEO.TypeCode.messageBoard.toString())) {
                    //删除留言
                    CacheHandler.delete(BaseContentEO.class, content);
                    // 只删除主表（假删）
                    baseContentService.delete(BaseContentEO.class, id);
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(content.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
                } else if(typeCode.equals(BaseContentEO.TypeCode.workGuide.toString())) {
                    //删除办事
                    Map<String,Object> params = new HashMap<String, Object>();
                    params.put("contentId",id);
                    CmsWorkGuideEO guide = workGuideService.getEntity(CmsWorkGuideEO.class,params);
                    String returnStr = workGuideService.deleteEO(new Long[]{guide.getId()});
                    if(!StringUtils.isEmpty(returnStr)) {
                        MessageSenderUtil.unPublishCopyNews(returnStr);
                    }
                } else if(typeCode.equals(BaseContentEO.TypeCode.interviewInfo.toString())) {
                    //删除在线访谈
                    InterviewInfoEO interviewInfo = interviewInfoService.getInterviewInfoByContentId(id);
                    interviewInfoService.delete(new Long[]{interviewInfo.getContentId()}, new Long[]{id});
                    MessageSenderUtil.publishContent(
                            new MessageStaticEO(content.getSiteId(), content.getColumnId(), new Long[]{id}).setType(MessageEnum.UNPUBLISH.value()), 2);
                }
            }
        }
        return ResponseData.success("删除成功!");
    }
}
