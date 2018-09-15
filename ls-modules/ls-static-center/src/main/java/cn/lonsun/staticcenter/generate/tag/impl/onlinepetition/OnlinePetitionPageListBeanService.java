package cn.lonsun.staticcenter.generate.tag.impl.onlinepetition;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlinePetition.internal.dao.IOnlinePetitionDao;
import cn.lonsun.content.onlinePetition.internal.service.IOnlinePetitionService;
import cn.lonsun.content.onlinePetition.vo.OnlinePetitionVO;
import cn.lonsun.content.onlinePetition.vo.PetitionPageVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.indicator.internal.entity.IndicatorEO;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
import cn.lonsun.site.site.internal.cache.DictItemCache;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import cn.lonsun.util.ModelConfigUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-12-16<br/>
 */
@Component
public class OnlinePetitionPageListBeanService extends AbstractBeanService {

    @Resource
    private IOnlinePetitionDao petitionDao;

    @Autowired
    private IOnlinePetitionService petitionService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId||columnId==0L) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        if (null == columnId||columnId==0L) {
            return null;
        }
        String action=context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }
        if ("new".equals(action)) {
            return getNew(columnId);
        } else if ("detail".equals(action)) {
            return getDetail(paramObj);
        } else if("search".equals(action)){
            return getSearch();
        }else {
            return getPage(paramObj, columnId);
        }

    }

    private Object getSearch() {
        Context context = ContextHolder.getContext();
        String randomCode=context.getParamMap().get("randomCode");
        if(StringUtils.isEmpty(randomCode)){
            return "0";
        }
        Long siteId=context.getSiteId();
        if(siteId==null){
            String siteIdStr=context.getParamMap().get("siteId");
            if(!StringUtils.isEmpty(siteIdStr)){
                siteId=Long.parseLong(siteIdStr);
            }
        }
        if(siteId==null){
            return "2";
        }
        OnlinePetitionVO vo=petitionService.getByCheckCode(randomCode,siteId);
        if(vo==null||vo.getContentId()==null){
            return "1";
        }
        return vo;
    }

    private Object getNew(Long columnId) {
        OnlinePetitionVO vo = new OnlinePetitionVO();
        vo.setColumnId(columnId);
        IndicatorEO eo = CacheHandler.getEntity(IndicatorEO.class, columnId);
        if (eo != null) {
            vo.setSiteId(eo.getSiteId());
        }
        vo.setTypeCode(BaseContentEO.TypeCode.onlinePetition.toString());
        return vo;
    }

    private Object getDetail(JSONObject paramObj) {
        String contentIdStr = ContextHolder.getContext().getParamMap().get("id");
        Long contentId=null;
        if (StringUtils.isEmpty(contentIdStr)) {
            contentId = ContextHolder.getContext().getContentId();
        }else{
            contentId = Long.parseLong(contentIdStr);
        }
        if (contentId == null||contentId==0) {
            return null;
        }
        BaseContentEO contentEO = CacheHandler.getEntity(BaseContentEO.class, contentId);
        if (null == contentEO) {
            return null;
        }
        if(contentEO.getIsPublish()==null||contentEO.getIsPublish()==0){
            return "1";
        }
        OnlinePetitionVO vo = petitionService.getVO(contentId);
        return vo;
    }

    private Object getPage(JSONObject paramObj, Long columnId) {
        Context context = ContextHolder.getContext();
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);

        StringBuffer sql =
            new StringBuffer("select c.id as id, c.title as title,c.column_id as columnId,c.site_id as siteId ")
                .append(" ,c.publish_date as publishDate,c.is_publish as isPublish,c.author as author,o.is_public as isPublic")
                .append(" ,o.create_date as createDate,o.id as petitionId,o.deal_status as dealStatus,o.content as content,o.ip as ip,o.attach_id as attachId,o.attach_name as attachName ")
                .append(" ,r.create_date as replyDate,r.id as replyId,r.reply_content as replyContent,r.reply_ip as replyIp ,r.reply_user_name as replyUserName")
                .append("  from cms_base_content c inner join cms_online_petition o on c.id=o.content_id left join cms_petition_rec r on o.id=r.petition_id where 1=1")
                .append(" and c.record_status='" + AMockEntity.RecordStatus.Normal.toString() + "' and o.record_status='"
                    + AMockEntity.RecordStatus.Normal.toString() + "'")
                .append("  and c.column_id=" + columnId + " and c.site_id="+context.getSiteId()+" and c.is_publish=1 ");

        sql.append(" order by ").append(ModelConfigUtil.getOrderTypeValue(columnId, context.getSiteId()));
        List<String> fields = new ArrayList<String>();
        fields.add("id");
        fields.add("title");
        fields.add("columnId");
        fields.add("siteId");
        fields.add("publishDate");
        fields.add("isPublish");
        fields.add("author");
        fields.add("isPublic");
        fields.add("createDate");
        fields.add("petitionId");
        fields.add("dealStatus");
        fields.add("content");
        fields.add("ip");
        fields.add("attachId");
        fields.add("attachName");
        fields.add("replyDate");
        fields.add("replyId");
        fields.add("replyContent");
        fields.add("replyIp");
        fields.add("replyUserName");
        String[] str = new String[fields.size()];
        return petitionDao.getPaginationBySql(pageIndex, pageSize, sql.toString(), new Object[]{}, PetitionPageVO.class, fields.toArray(str));
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();// 上下文
        String action=context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        map.put("action",action);
        if ("new".equals(action) || "detail".equals(action)) {
            Long columnId = context.getColumnId();
            if (null == columnId) {// 如果栏目id为空,说明栏目id是在页面传入的
                columnId = paramObj.getLong(GenerateConstant.ID);
            }
            List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId,context.getSiteId(),null);
            if (list != null && list.size() > 0) {
                map.put("recType",list.get(0).getRecType());
                map.put("recList", list);
            }

            List<DataDictVO> plist =   DataDictionaryUtil.getItemList("petition_purpose", context.getSiteId());
            List<DataDictVO> clist =  DataDictionaryUtil.getItemList("petition_category", context.getSiteId());
            map.put("pList", plist);
            map.put("cList", clist);
            if("detail".equals(action)){
                if("1".equals(resultObj)){
                    map.put("message","<div>您查询的信息不存在，请核准后再试！</div>");
                }
            }
            return map;
        } else if ("list".equals(action)) {
            // 获取分页对象
            Pagination pagination = (Pagination) resultObj;
            if (null != pagination) {

                List<?> resultList = pagination.getData();
                // 处理查询结果
                if (null != resultList && !resultList.isEmpty()) {
                    for (Object o : resultList) {
                        PetitionPageVO vo = (PetitionPageVO) o;
                        // String path = PathUtil.getLinkPath(vo.getColumnId(),
                        // vo.getId());//拿到栏目页和文章页id
                        String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getId());//拿到栏目页和文章页id
                       // String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getId());
                        vo.setLink(path);
                    }
                }

                String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
                pagination.setLinkPrefix(path);
            }
        }else if("search".equals(action)){
            Long columnId = context.getColumnId();
            if (null == columnId) {// 如果栏目id为空,说明栏目id是在页面传入的
                columnId = paramObj.getLong(GenerateConstant.ID);
            }
            List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId,context.getSiteId(),null);
            if (list != null && list.size() > 0) {
                map.put("recList", list);
            }
            List<DataDictVO> plist = DictItemCache.get("petition_purpose");
            List<DataDictVO> clist = DictItemCache.get("petition_category");
            map.put("pList", plist);
            map.put("cList", clist);
            if("1".equals(resultObj)){
                map.put("message","<div>您查询的信息不存在，请核准后再试！</div>");
            }
            if("0".equals(resultObj)){
                map.put("message","<div>查询密码不能为空！</div>");
            }
            if("2".equals(resultObj)){
                map.put("message","<div>站点ID不能为空！</div>");
            }
        }

        return map;
    }
}
