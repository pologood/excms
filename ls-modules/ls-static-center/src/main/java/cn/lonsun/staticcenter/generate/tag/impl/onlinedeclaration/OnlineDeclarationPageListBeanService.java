package cn.lonsun.staticcenter.generate.tag.impl.onlinedeclaration;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.onlineDeclaration.internal.service.IOnlineDeclarationService;
import cn.lonsun.content.onlineDeclaration.vo.DeclaQueryVO;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.net.service.entity.CmsTableResourcesEO;
import cn.lonsun.net.service.service.ITableResourcesService;
import cn.lonsun.site.contentModel.vo.ContentModelParaVO;
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

import java.util.List;
import java.util.Map;

/**
 * 在线申报 <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-6-14<br/>
 */
@Component
public class OnlineDeclarationPageListBeanService extends AbstractBeanService {

    @Autowired
    private IOnlineDeclarationService declarationService;

    @Autowired
    private ITableResourcesService resourcesService;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId || columnId == 0) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        if (null == columnId) {
            return null;
        }
        String action = context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }
        if ("new".equals(action)) {
            return getNew(columnId);
        } else if ("detail".equals(action)) {
            return getDetail(paramObj);
        } else if ("search".equals(action)) {
            return getSearch();
        } else {
            return getPage(paramObj, columnId);
        }
    }

    private Object getNew(Long columnId) {
        Context context = ContextHolder.getContext();
        OnlineDeclarationVO vo = new OnlineDeclarationVO();
        vo.setColumnId(columnId);
        vo.setSiteId(context.getSiteId());
        String organIdStr = context.getParamMap().get("organId");
        Long organId = null;
        if (!StringUtils.isEmpty(organIdStr)) {
            organId = Long.parseLong(organIdStr);
        }
        vo.setRecUnitId(organId);
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
        if(contentEO.getIsPublish()!=null&&contentEO.getIsPublish()==1){
            OnlineDeclarationVO vo = declarationService.getVO(contentId);
            return vo;
        }else{
            return "1";
        }

    }

    private Object getSearch() {
        Context context = ContextHolder.getContext();
        String randomCode = context.getParamMap().get("randomCode");
        String docNum = context.getParamMap().get("docNum");
        if (StringUtils.isEmpty(randomCode) || StringUtils.isEmpty(docNum)) {
            return "0";
        }
        randomCode=randomCode.trim();
        docNum=docNum.trim();
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
        OnlineDeclarationVO vo = declarationService.searchByCode(randomCode, docNum,siteId);
        if (vo == null || vo.getBaseContentId() == null) {
            return "1";
        }
        return vo;
    }


    private Object getPage(JSONObject paramObj, Long columnId) {
        Context context = ContextHolder.getContext();
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        DeclaQueryVO queryVO = new DeclaQueryVO();
        queryVO.setPageIndex(pageIndex);
        queryVO.setPageSize(pageSize);
        queryVO.setColumnId(columnId);
        queryVO.setSiteId(context.getSiteId());
        return declarationService.getFrontPage(queryVO);
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();// 上下文
        String action = context.getParamMap().get("action");
        if (AppUtil.isEmpty(action)) {
            action = paramObj.getString("action");
        }
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        map.put("action", action);
        if ("new".equals(action) ) {
            Long columnId = context.getColumnId();
            if (null == columnId) {// 如果栏目id为空,说明栏目id是在页面传入的
                columnId = paramObj.getLong(GenerateConstant.ID);
            }
            List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, ContextHolder.getContext().getSiteId(),null);
            if (list != null && list.size() > 0) {
                map.put("recList", list);
                map.put("recType", 0);
            } else {
                map.put("recType", null);
            }
            String ids=context.getParamMap().get("tableIds");
            if(!StringUtils.isEmpty(ids)){
                Long[] idArr=AppUtil.getLongs(ids,",");
                List<CmsTableResourcesEO> tableList=resourcesService.getEntities(CmsTableResourcesEO.class,idArr);
                map.put("tableList",tableList);
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
                        OnlineDeclarationVO vo = (OnlineDeclarationVO) o;
                        // String path = PathUtil.getLinkPath(vo.getColumnId(),
                        // vo.getId());//拿到栏目页和文章页id
                        String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getBaseContentId());//拿到栏目页和文章页id
                        //String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getBaseContentId());
                        vo.setLink(path);
                        if(!StringUtils.isEmpty(vo.getDealStatus())){
                            DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", vo.getDealStatus());
                            if (dictVO != null) {
                                vo.setStatusName(dictVO.getKey());
                            }
                        }
                    }
                }

                String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), context.getColumnId(), null);
                pagination.setLinkPrefix(path);
            }
        } else if ("search".equals(action)|| "detail".equals(action)) {
            Long columnId = context.getColumnId();
            if (null == columnId) {// 如果栏目id为空,说明栏目id是在页面传入的
                columnId = paramObj.getLong(GenerateConstant.ID);
            }
            List<ContentModelParaVO> list = ModelConfigUtil.getParam(columnId, ContextHolder.getContext().getSiteId(),null);
            if (list != null && list.size() > 0) {
                map.put("recList", list);
            }
            if ("1".equals(resultObj)) {
                map.put("message", "<div class=\"decl-msg-info\">您查询的信息不存在，请核准后再试！</div>");
            }
            if ("0".equals(resultObj)) {
                map.put("message", "<div class=\"decl-msg-info\">查询密码不能为空！</div>");
            }
            if ("2".equals(resultObj)) {
                map.put("message", "<div class=\"decl-msg-info\">站点ID不能为空！</div>");
            }
        }

        return map;
    }
}
