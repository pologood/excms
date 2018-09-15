package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.interview.internal.entity.InterviewInfoEO;
import cn.lonsun.content.interview.internal.service.IInterviewInfoService;
import cn.lonsun.content.interview.vo.InterviewInfoVO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.util.JdbcUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 1960274114 on 2016-10-12.
 * 在线访谈
 */
public class SyncInterviewInfo extends AbSyncInfo {
    //默认访谈类型
    private Integer type = InterviewInfoEO.Type.history.getType();

    public SyncInterviewInfo(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils, siteId, createUserId, curColumdId);
    }
    /**
     * 老网站在线防谈表
     * @param idstr
     * @return
     */
    private List<Object> getSys_zxftjsList(String idstr){
        String orderby = " order by idate asc";
        String sql = "select id,title,ftdate,people,info,pic from SYS_zxftjs";
        if(!StringUtils.isEmpty(idstr)){
            sql = sql.concat(" and id in(").concat(idstr).concat(")");
        }
        sql = sql.concat(orderby);
        return jdbcUtils.excuteQuery(sql, null);
    }

    /**
     * 老网站在线谈内容表
     * @param id
     * @return
     */
    private String getSys_zxftinfo(Long id){
        String sql = "select info from SYS_zxftinfo where classid=?";
        Object object = jdbcUtils.executeQuerySingle(sql,new Long[]{id});
        return null == object?null:object.toString();
    }

    public void imp(){
        List<Object> list = getSys_zxftjsList(null);
        IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);
        IInterviewInfoService interviewInfoService = SpringContextHolder.getBean(IInterviewInfoService.class);
        for (int i = 0, l = list.size(); i < l; i++) {
            if(step>limitSize){
                break;
            }
            step ++;

            Map<String, Object> map = (HashMap<String, Object>) list.get(i);
            InterviewInfoVO interviewInfoVO = new InterviewInfoVO();
            interviewInfoVO.setSiteId(siteId);
            interviewInfoVO.setColumnId(curColumdId);
            interviewInfoVO.setTime(formatDate(map.get("ftdate"), "yyyy年MM月dd日"));
            interviewInfoVO.setTitle((String)map.get("title"));
            interviewInfoVO.setUserNames((String)map.get("people"));
            interviewInfoVO.setContent((String)map.get("info"));
            interviewInfoVO.setPicUrl((String)map.get("pic"));
            //访谈详情
            Long id = AppUtil.getLong(map.get("id"));
            interviewInfoVO.setDesc(getSys_zxftinfo(id));


            BaseContentEO content = new BaseContentEO();
            content.setTitle(interviewInfoVO.getTitle());
            content.setColumnId(interviewInfoVO.getColumnId());
            content.setSiteId(interviewInfoVO.getSiteId());
            content.setTypeCode(BaseContentEO.TypeCode.interviewInfo.toString());
            content.setNum(interviewInfoVO.getSortNum());
            content.setIsPublish(interviewInfoVO.getIssued());
            content.setPublishDate(interviewInfoVO.getIssuedTime());
            content.setImageLink(interviewInfoVO.getPicUrl());
            content.setContentPath(interviewInfoVO.getContentPath());
            //导入标识
            content.setEditor(imp_tag);
            //默认状态设置
            content.setIsPublish(isPublish);

            //保存基本对象
            baseContentService.saveEntity(content);
            InterviewInfoEO interviewInfo = new InterviewInfoEO();
            BeanUtils.copyProperties(interviewInfoVO, interviewInfo);
            interviewInfo.setContentId(content.getId());
            //默认开启
            interviewInfo.setIsOpen(isOpen);
            //默认访谈类型
            interviewInfo.setType(type);
            //保存在线访谈
            interviewInfoService.saveEntity(interviewInfo);

        }
    }
}
