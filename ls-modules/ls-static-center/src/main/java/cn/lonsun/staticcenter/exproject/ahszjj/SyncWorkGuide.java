package cn.lonsun.staticcenter.exproject.ahszjj;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.content.internal.entity.BaseContentEO;
import cn.lonsun.content.internal.service.IBaseContentService;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.util.StringUtils;
import cn.lonsun.core.util.SpringContextHolder;
import cn.lonsun.net.service.entity.CmsGuideResRelatedEO;
import cn.lonsun.net.service.entity.CmsWorkGuideEO;
import cn.lonsun.net.service.service.IGuideResRelatedService;
import cn.lonsun.net.service.service.IWorkGuideService;
import cn.lonsun.solr.SolrFactory;
import cn.lonsun.solr.vo.SolrIndexVO;
import cn.lonsun.staticcenter.util.JdbcUtils;

import java.util.*;

/**
 * Created by 1960274114 on 2016-10-13.
 * 办事指南
 */
public class SyncWorkGuide extends AbSyncInfo{
    //办事指南栏目
    private final String followid = "76";
    private final String handleDate = "周一至周五 8：30--12：00 13：30--17：00";//办事时间
    private final String handleAddress = "安徽省合肥市马鞍山路509号";//办事地点
    private final String phone = "0551－62999702 62999709 62999707（代码）";//办事号码

    public SyncWorkGuide(JdbcUtils jdbcUtils, Long siteId, Long createUserId, Long curColumdId) {
        super(jdbcUtils, siteId, createUserId, curColumdId);
    }

    private List<Object> getList(String idstr){
        String orderby = " order by idate asc";
            String sql = "select title,content,idate,bszncontent1,bszncontent2,bszncontent3,bszncontent4,bszncontent5,bszncontent6,bszncontent7,bszncontent8,bszncontent9 from sys_news" +
                    " where flag=1 and followid=".concat(followid);
        if(!StringUtils.isEmpty(idstr)){
            sql = sql.concat(" and id in(").concat(idstr).concat(")");
        }
        sql = sql.concat(orderby);
        return jdbcUtils.excuteQuery(sql, null);
    }

    public void imp(String organgId,String organName){
        IBaseContentService baseContentService = SpringContextHolder.getBean(IBaseContentService.class);
        IGuideResRelatedService guideResRelatedService = SpringContextHolder.getBean(IGuideResRelatedService.class);
        IWorkGuideService workGuideService = SpringContextHolder.getBean(IWorkGuideService.class);
        List<Object> newsList = getList(null);

        for (int i = 0, l = newsList.size(); i < l; i++) {
            if (step > limitSize) {
                break;
            }
            step++;

            Map<String, Object> map = (HashMap<String, Object>) newsList.get(i);
            //开始保存
            CmsWorkGuideEO eo = new CmsWorkGuideEO();
            eo.setName((String)map.get("title"));
            eo.setContent((String)map.get("content"));
            eo.setJoinDate(formatDate(map.get("idate"),"yyyy-MM-dd HH:mm:ss"));
            //办理时间(老网站是固定的)
            eo.setHandleDate(handleDate);
            eo.setHandleAddress(handleAddress);
            eo.setPhone(phone);

            //设定依据
            eo.setSetAccord((String)map.get("bszncontent1"));
            //申请条件
            eo.setApplyCondition((String)map.get("bszncontent2"));
            //办理材料
            eo.setHandleData((String)map.get("bszncontent3"));
            //办理流程
            eo.setHandleProcess((String)map.get("bszncontent4"));

            //导入组织
            eo.setOrganId(organgId);
            eo.setOrganName(organName);
            //设置固定的
            eo.setSiteId(siteId);
            eo.setColumnId(curColumdId);

            String cIds = curColumdId.toString();
            BaseContentEO contentEO = new BaseContentEO();
            contentEO.setTitle(eo.getName());
            contentEO.setColumnId(eo.getColumnId());
            contentEO.setSiteId(siteId);
            contentEO.setColumnId(curColumdId);
            contentEO.setIsPublish(isPublish);

            contentEO.setIsPublish(Integer.parseInt(String.valueOf(eo.getPublish())));
            contentEO.setRecordStatus(AMockEntity.RecordStatus.Normal.toString());
            contentEO.setTypeCode(eo.getTypeCode());

            if(contentEO.getIsPublish() == 1) {
                contentEO.setPublishDate(new Date());
            }
            //导入标识
            contentEO.setEditor(imp_tag);
            //id为内容模型的ID
            Long id = baseContentService.saveEntity(contentEO);
            eo.setContentId(id);

            //gid(办事指南的ID)
            Long gid = workGuideService.saveEntity(eo);

            if(contentEO.getIsPublish() == 1) {

                SolrIndexVO vo = new SolrIndexVO();
                vo.setId(id + "");
                vo.setTitle(eo.getName());
                vo.setTypeCode(BaseContentEO.TypeCode.workGuide.toString());
                vo.setColumnId(eo.getColumnId());
                vo.setSiteId(siteId);
                vo.setContent(eo.getContent());
                vo.setSetAccord(eo.getSetAccord());
                vo.setApplyCondition(eo.getApplyCondition());
                vo.setHandleData(eo.getHandleData());
                vo.setHandleProcess(eo.getHandleProcess());
                vo.setCreateDate(contentEO.getPublishDate());
                try {
                    SolrFactory.createIndex(vo);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            //办事指南-关联资源表单
            List<CmsGuideResRelatedEO> list = new ArrayList<CmsGuideResRelatedEO>();
            if(!AppUtil.isEmpty(eo.getTableIds())) {
                Long[] ids = StringUtils.getArrayWithLong(eo.getTableIds(), ",");
                if(!AppUtil.isEmpty(ids)) {
                    for(Long rid : ids) {
                        CmsGuideResRelatedEO relatedEO = new CmsGuideResRelatedEO();
                        relatedEO.setGuideId(gid);
                        relatedEO.setResId(rid);
                        relatedEO.setType(CmsGuideResRelatedEO.TYPE.TABLE.toString());
                        list.add(relatedEO);
                    }
                }
            }

            if(!AppUtil.isEmpty(eo.getRuleIds())) {
                Long[] ids = StringUtils.getArrayWithLong(eo.getRuleIds(),",");
                if(!AppUtil.isEmpty(ids)) {
                    for (Long rid : ids) {
                        CmsGuideResRelatedEO relatedEO = new CmsGuideResRelatedEO();
                        relatedEO.setGuideId(gid);
                        relatedEO.setResId(rid);
                        relatedEO.setType(CmsGuideResRelatedEO.TYPE.RULE.toString());
                        list.add(relatedEO);
                    }
                }
            }
            guideResRelatedService.saveEntities(list);
            workGuideService.saveClassify(gid,cIds);
        }


    }
}
