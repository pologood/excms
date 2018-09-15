package cn.lonsun.staticcenter.generate.tag.impl.onlinedeclaration;

import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.content.onlineDeclaration.internal.dao.IOnlineDeclarationDao;
import cn.lonsun.content.onlineDeclaration.vo.OnlineDeclarationVO;
import cn.lonsun.content.vo.GuestBookEditVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.rbac.internal.entity.OrganEO;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import cn.lonsun.system.datadictionary.vo.DataDictVO;
import cn.lonsun.util.DataDictionaryUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2016-7-1<br/>
 */
@Component
public class OnlineDeclarationListBeanService extends AbstractBeanService {

    @Autowired
    private IOnlineDeclarationDao declarationDao;

    @Override
    public Object getObject(JSONObject paramObj) {
        Context context = ContextHolder.getContext();
        // 此写法是为了使得在主页面这样调用也能解析
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在标签页面默认传入的
            columnId = context.getColumnId();
        }
        String docNum = context.getParamMap().get("docNum");
        String randomCode=context.getParamMap().get("randomCode");

        Integer isReply = paramObj.getInteger("isReply");

        Long siteId=context.getSiteId();

        StringBuffer hql = new StringBuffer("select c.title as title,c.publishDate as publishDate,c.createDate as createDate,c.isPublish as isPublish,c.columnId as columnId")
                .append(",c.id as baseContentId,g.id as id,g.replyContent as replyContent,g.factReason as factReason,g.dealStatus as dealStatus")
                .append(",g.personName as personName,g.replyDate as replyDate,g.attachId as attachId,g.attachName as attachName")
                .append(",g.recUnitId as recUnitId,g.docNum as docNum,g.randomCode as randomCode")
                .append(" from BaseContentEO c,OnlineDeclarationEO g where g.baseContentId=c.id and c.recordStatus=:recordStatus ")
                .append(" and c.isPublish=1");

        Map<String,Object> map=new HashMap<String, Object>();
        map.put("recordStatus",AMockEntity.RecordStatus.Normal.toString());

        if (siteId!=null) {
            hql.append(" and c.siteId=:siteId");
            map.put("siteId",siteId);
        }
        if (columnId!=null) {
            hql.append(" and c.columnId=:columnId");
            map.put("columnId",columnId);
        }
        if(isReply!=null){
            if(isReply==1){
                hql.append(" and g.dealStatus in('handled','replyed')");
            }
        }

        if(!StringUtils.isEmpty(docNum)){
            hql.append(" and g.docNum=:docNum");
            map.put("docNum",docNum);
        }
        if(!StringUtils.isEmpty(randomCode)){
            hql.append(" and g.randomCode=:randomCode");
            map.put("randomCode",randomCode);
        }

        Integer num=paramObj.getInteger(GenerateConstant.NUM);
        hql.append(" order by c.createDate desc");
        return declarationDao.getBeansByHql(hql.toString(), map, OnlineDeclarationVO.class,num);
    }

    /**
     * 预处理数据
     *
     * @throws GenerateException
     * @see cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService#doProcess(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        // 预处理数据，处理文章链接问题
        List<OnlineDeclarationVO> list = (List<OnlineDeclarationVO>) resultObj;
        if (null != list && !list.isEmpty()) {
            // 处理文章链接
            for (OnlineDeclarationVO vo : list) {
                String path = PathUtil.getLinkPath(vo.getColumnId(), vo.getBaseContentId());
                // String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getBaseContentId());
                vo.setLink(path);
                if(vo.getRecUnitId()!=null){
                    OrganEO organEO = CacheHandler.getEntity(OrganEO.class, vo.getRecUnitId());
                    if (organEO != null) {
                        vo.setRecUnitName(organEO.getName());
                    }
                }
                if(!StringUtils.isEmpty(vo.getDealStatus())){
                    DataDictVO dictVO = DataDictionaryUtil.getItem("deal_status", vo.getDealStatus());
                    if (dictVO != null) {
                        vo.setStatusName(dictVO.getKey());
                    }
                }

            }
        }
        return super.doProcess(resultObj, paramObj);
    }

}
