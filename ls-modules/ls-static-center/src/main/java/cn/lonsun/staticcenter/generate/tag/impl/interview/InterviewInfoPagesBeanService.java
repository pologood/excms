package cn.lonsun.staticcenter.generate.tag.impl.interview;

import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.interview.internal.dao.IInterviewInfoDao;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.staticcenter.generate.GenerateConstant;
import cn.lonsun.staticcenter.generate.GenerateException;
import cn.lonsun.staticcenter.generate.tag.impl.AbstractBeanService;
import cn.lonsun.staticcenter.generate.thread.Context;
import cn.lonsun.staticcenter.generate.thread.ContextHolder;
import cn.lonsun.staticcenter.generate.util.PathUtil;
import cn.lonsun.staticcenter.service.HtmlEnum;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhangchao on 2016/8/9.
 */
@Component
public class InterviewInfoPagesBeanService extends AbstractBeanService {
    private Boolean isParam = false;

    @Autowired
    private IInterviewInfoDao interviewInfoDao;

    @Override
    public Object getObject(JSONObject paramObj) throws GenerateException {
        Context context = ContextHolder.getContext();
        Long columnId = paramObj.getLong(GenerateConstant.ID);
        //查询某一年的数据
        String year = paramObj.getString("year");
        String yearParams = context.getParamMap().get("year");
        if (!StringUtils.isEmpty(yearParams)) {
            year = yearParams;
        }
        // 此写法是为了使得在页面这样调用也能解析
        if (null == columnId) {// 如果栏目id为空说明，栏目id是在页面传入的
            columnId = context.getColumnId();
        }
        Long siteId = paramObj.getLong("siteId");
        if (!(siteId != null && siteId != 0)) {
            siteId = context.getSiteId();
        }
        if (columnId == null) {
            throw new GenerateException("栏目ID不能为空！");
        }
        Integer itype = paramObj.getInteger(GenerateConstant.ITYPE);
        String itypeParams = context.getParamMap().get(GenerateConstant.ITYPE);
        if (!StringUtils.isEmpty(itypeParams)) {
            try {
                itype = Integer.parseInt(itypeParams);
            } catch (NumberFormatException e) {
            }
            isParam = true;
        }
        //处理分页去除最新展示的一条
        Boolean delFirst = false;
        try {
            delFirst = paramObj.getBoolean("delFirst");
        }catch (Exception e){ }
        Long firstId = null;
        if(delFirst){
            List<Object> values = new ArrayList<Object>();
            StringBuffer hql = getHql();
            values.add(AMockEntity.RecordStatus.Normal.toString());
            values.add(BaseContentEO.TypeCode.interviewInfo.toString());
            values.add(columnId);
            if(siteId != null){
                hql.append(" and b.siteId= ?");
                values.add(siteId);
            }
            if (itype != null && itype != 0) {
                hql.append(" and s.type = ?");
                values.add(itype);
            }
            if(!StringUtils.isEmpty(year)){
                hql.append(" and to_char(s.createDate,'yyyy') = ?");
                values.add(year);
            }
            hql.append(" order by b.num desc");
            List<InterviewInfoVO> list = (List<InterviewInfoVO>) interviewInfoDao.getBeansByHql(hql.toString(), values.toArray(), InterviewInfoVO.class, 1);
            if(list != null && list.size() > 0){
                firstId = list.get(0).getInterviewId();
            }
        }
        // 需要显示条数.
        Integer pageSize = paramObj.getInteger(GenerateConstant.PAGE_SIZE);
        Long pageIndex = context.getPageIndex() == null ? 0 : (context.getPageIndex() - 1);
        List<Object> values = new ArrayList<Object>();
        StringBuffer hql = getHql();
        values.add(AMockEntity.RecordStatus.Normal.toString());
        values.add(BaseContentEO.TypeCode.interviewInfo.toString());
        values.add(columnId);
        if(siteId != null){
            hql.append(" and b.siteId= ?");
            values.add(siteId);
        }
        if (itype != null && itype != 0) {
            hql.append(" and s.type = ?");
            values.add(itype);
        }
        if(firstId != null){
            hql.append(" and s.interviewId != ?");
            values.add(firstId);
        }
        if(!StringUtils.isEmpty(year)){
            hql.append(" and to_char(s.createDate,'yyyy') = ?");
            values.add(year);
        }
        hql.append(" order by b.num desc");
        Pagination pagination = interviewInfoDao.getPagination(pageIndex, pageSize, hql.toString(), values.toArray(), InterviewInfoVO.class);
        Map<String, Object> mapParams = new HashMap<String, Object>();
        if (null != pagination) {
            List<?> resultList = pagination.getData();
            // 处理查询结果
            if (null != resultList && !resultList.isEmpty()) {
                for (Object o : resultList) {
                    InterviewInfoVO vo = (InterviewInfoVO) o;
                    if(!StringUtils.isEmpty(vo.getOutLink())){
                        //转链
                        vo.setIsLink(1);
                        vo.setLinkUrl(vo.getOutLink());
                    }else{
                        vo.setLinkUrl(PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.ACRTILE.getValue(), vo.getColumnId(), vo.getContentId()));
                    }
                }
            }
            // 设置连接地址
            String path = PathUtil.getLinkPath(HtmlEnum.CONTENT.getValue(), HtmlEnum.COLUMN.getValue(), columnId, null);
            pagination.setLinkPrefix(path);
        }
        SimpleDateFormat parse = new SimpleDateFormat("yyyy");
        String date =parse.format(new Date());
        mapParams.put("pagination",pagination);
        mapParams.put("yearNow",year);
        mapParams.put("yeara",date);
        mapParams.put("yearb",(Long.parseLong(date)-1)+"");
        mapParams.put("yearc",(Long.parseLong(date)-2)+"");
        mapParams.put("yeard",(Long.parseLong(date)-3)+"");
        return mapParams;
    }

    private StringBuffer getHql() {
        return new StringBuffer("select b.id as contentId,b.title as title,b.columnId as columnId,b.siteId as siteId,"
                        + "b.num as sortNum,b.isPublish as issued,b.publishDate as issuedTime,b.imageLink as picUrl,"
                        + "s.interviewId as interviewId,s.presenter as presenter,s.userNames as userNames,s.time as time,s.liveLink as liveLink,"
                        + "s.outLink as outLink,s.handleOrgan as handleOrgan,s.content as content,s.desc as desc,s.isOpen as isOpen,s.openTime as openTime,s.startTime as startTime,s.endTime as endTime,s.address as address,s.organizer as organizer"
                        + " from BaseContentEO b,InterviewInfoEO s where b.id = s.contentId and b.recordStatus= ? and b.typeCode = ? and b.columnId = ? and b.isPublish = 1");
    }

    public Map<String, Object> doProcess(Object resultObj, JSONObject paramObj) throws GenerateException {
        Map<String, Object> map = super.doProcess(resultObj, paramObj);
        map.put("isParam", isParam);
        return map;
    }
}
