package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.content.survey.internal.entity.SurveyThemeEO;
import cn.lonsun.content.survey.internal.service.ISurveyThemeService;
import cn.lonsun.content.survey.vo.SurveyThemeVO;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.staticcenter.util.JdbcUtils;
import org.springframework.beans.BeanUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 1960274114 on 2016-10-12.
 * 网上调查
 */
public class SynSurvey extends AbSyncInfo{
    public SynSurvey(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils, siteId, createUserId, curColumdId);
    }

    /**
     * 老网站的
     * @param idstr
     * @return
     */
    private List<Object> getSYS_zxdclist(String idstr){
        String orderby = " order by idate asc";
        String sql = "select id,title,content,flag,outlink,outurl,idate,isshow from sys_zxdclist";
        if(!StringUtils.isEmpty(idstr)){
            sql = sql.concat(" and id in(").concat(idstr).concat(")");
        }
        sql = sql.concat(orderby);
        return jdbcUtils.excuteQuery(sql, null);
    }

    /**
     * 获取内容
     * @param id
     * @return
     */
    private String getContent(Long id){
        String sql = "select content from SYS_News where id=?";
        Object object = jdbcUtils.executeQuerySingle(sql,new Long[]{id});
        return null == object?null:object.toString();
    }

    public void imp(){
        List<Object> list = getSYS_zxdclist(null);
        IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);
        ISurveyThemeService surveyThemeService = SpringContextHolder.getBean(ISurveyThemeService.class);
        for (int i = 0, l = list.size(); i < l; i++) {
            if (step > limitSize) {
                break;
            }
            step++;

            Map<String, Object> map = (HashMap<String, Object>) list.get(i);

            SurveyThemeVO surveyThemeVO = new SurveyThemeVO();
            surveyThemeVO.setSiteId(siteId);
            surveyThemeVO.setColumnId(curColumdId);
            surveyThemeVO.setTitle((String)map.get("title"));

            surveyThemeVO.setIsPublish(isPublish);
            surveyThemeVO.setOptions(SurveyThemeVO.Option.IS_TEXT.getOption());
            surveyThemeVO.setIssuedTime(getDateValue(map.get("idate")));
            surveyThemeVO.setStartTime(surveyThemeVO.getIssuedTime());
            surveyThemeVO.setEndTime(surveyThemeVO.getIssuedTime());
            int outlink = AppUtil.getint(map.get("outlink"));
            String outurl = AppUtil.getValue(map.get("outurl"));
            String con = (String)map.get("content");
            String id =AppUtil.getValue(map.get("id"));
            surveyThemeVO.setIsVisible(AppUtil.getInteger(map.get("isshow")));
            if(outlink==1){
                if(outurl.indexOf("ahqi.gov.cn") !=-1 && outurl.indexOf("tid=") !=-1){//本网站来年链接引用
                    String[] tidarg = outurl.split("tid=");
                    id = tidarg[1];
                }else{
                    surveyThemeVO.setIsLink(SurveyThemeVO.Status.Yes.getStatus());
                    surveyThemeVO.setLinkUrl(outurl);
                }
            }
            if(null !=surveyThemeVO.getIsLink() && surveyThemeVO.getIsLink() == SurveyThemeVO.Status.No.getStatus()){
                Long _id = AppUtil.getLong(id);
                if(null !=_id)
                    con = getContent(_id);
            }
            surveyThemeVO.setContent(con);
            BaseContentEO content = new BaseContentEO();
            content.setTitle(surveyThemeVO.getTitle());
            content.setColumnId(surveyThemeVO.getColumnId());
            content.setSiteId(surveyThemeVO.getSiteId());
            content.setTypeCode(BaseContentEO.TypeCode.survey.toString());
            content.setNum(surveyThemeVO.getSortNum());
            content.setIsPublish(surveyThemeVO.getIsPublish());
            content.setPublishDate(surveyThemeVO.getIssuedTime());
            //保存标识
            content.setEditor(imp_tag);

            //保存基类
            baseContentService.saveEntity(content);
            SurveyThemeEO st = new SurveyThemeEO();
            BeanUtils.copyProperties(surveyThemeVO, st);
            st.setContentId(content.getId());
            //保存持久化
            surveyThemeService.saveEntity(st);
        }
    }
}
