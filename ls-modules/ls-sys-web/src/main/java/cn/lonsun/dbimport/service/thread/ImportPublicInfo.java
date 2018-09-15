package cn.lonsun.dbimport.service.thread;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.publicInfo.internal.entity.PublicClassEO;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ImportPublicInfo implements Callable<String>, Runnable {

    private IPublicContentService publicContentService;

    private IPublicClassService publicClassService;

    private Long siteId;

    private List<Map<String, Object>> data;

    private Map<String, Map<String, Object>> xxgkClassMap;

    private Map<String,List<Map<String,Object>>> xxgkClassRelMap;

    private String organId;

    private Long ex8organId;

    private Long ex8CatId;

    private String type;

    private Map<String, Object> sessionInfo;

    public ImportPublicInfo(IPublicContentService publicContentService, IPublicClassService publicClassService, Long siteId,
                            List<Map<String, Object>> data, Map<String, Map<String, Object>> xxgkClassMap,
                            Map<String,List<Map<String,Object>>> xxgkClassRelMap,
                            Long ex8organId, Long ex8CatId, String type, Map<String, Object> sessionInfo) {
        this.publicContentService = publicContentService;
        this.publicClassService = publicClassService;
        this.siteId = siteId;
        this.data = data;
        this.ex8organId = ex8organId;
        this.ex8CatId = ex8CatId;
        this.type = type;
        this.xxgkClassMap = xxgkClassMap;
        this.xxgkClassRelMap = xxgkClassRelMap;
        this.sessionInfo = sessionInfo;
    }

    @Override
    public String call() throws Exception {
        try {
            return excute();
        } catch (Exception e) {
            e.printStackTrace();
            return "导入" + type + "数据失败:" + e.getMessage();
        }
    }

    @Override
    public void run() {
        excute();
    }

    private String excute(){
        ThreadUtil.set(sessionInfo);
        return HibernateSessionUtil.execute(new HibernateHandler<String>() {
            @Override
            public String execute() throws Throwable {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //导入前删除所有数据
//                publicContentService.deleteByCatIdAndOrganId(ex8CatId, ex8organId);
                for (Map<String, Object> map : data) {
                    PublicContentVO publicContentVO = new PublicContentVO();
                    String docId = AppUtil.getValue(map.get("docId"));
                    if(null != xxgkClassRelMap){
                        List<Map<String,Object>> tempList = xxgkClassRelMap.get(docId);
                        Map<String, Object> param = new HashMap<String, Object>();
                        PublicClassEO publicClassEO = new PublicClassEO();
                        String classIds = "";
                        String classNames = "";
                        String parentClassIds = "";
                        if(null != tempList){
                            for(Map<String, Object> tempMap : tempList){
                                String classId1 = AppUtil.getValue(tempMap.get(docId));
                                Map<String,Object> temp = xxgkClassMap.get(classId1);
                                if(null != temp){
                                    System.out.println("分类名称=================" + temp.get("className").toString());
                                    if(!AppUtil.isEmpty(temp.get("className").toString())){
                                        param.put("name", temp.get("className").toString());
                                        param.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
                                        publicClassEO = publicClassService.getEntity(PublicClassEO.class,param);
                                        if(null != publicClassEO){
                                            classIds +=  publicClassEO.getId() +",";
                                            classNames += publicClassEO.getName() + ",";
                                            parentClassIds += publicClassEO.getParentId() +",";
                                        }
                                    }

                                }
                            }
                        }
                        if(!AppUtil.isEmpty(classIds)){
                            publicContentVO.setClassIds(classIds.substring(0,classIds.length()-1));
                        }
                        if(!AppUtil.isEmpty(parentClassIds)){
                            publicContentVO.setParentClassIds(parentClassIds.substring(0,parentClassIds.length()-1));
                        }
                        if(!AppUtil.isEmpty(classNames)){
                            publicContentVO.setClassNames(classNames.substring(0,classNames.length()-1));
                        }
                    }
                    //保存老数据库ID
                    publicContentVO.setOldSchemaId(AppUtil.getValue(map.get("docId")));
                    publicContentVO.setType(type);
                    publicContentVO.setSiteId(siteId);
                    publicContentVO.setOrganId(ex8organId);
                    if((Boolean)map.get("docCheckIn")){
                        publicContentVO.setIsPublish(1);
                    }else{
                        publicContentVO.setIsPublish(0);
                    }
                    publicContentVO.setCatId(ex8CatId);
                    publicContentVO.setAuthor("");
                    publicContentVO.setResources("舒城县人民政府");
                    publicContentVO.setTitle(null == map.get("docTitle") ? "空标题" : map.get("docTitle").toString());
                    publicContentVO.setFileNum(null == map.get("docNum") ? "" : map.get("docNum").toString());
                    publicContentVO.setKeyWords(null == map.get("docKeyWords") ? "" : map.get("docKeyWords").toString());
                    publicContentVO.setSummarize("");
                    publicContentVO.setIndexNum(null == map.get("docIndex") ? "" : map.get("docIndex").toString());
                    publicContentVO.setHit(Long.parseLong(map.get("docHits").toString()));
                    //处理内容
                    String content = AppUtil.getValue(map.get("docContent"));
                    content.replaceAll("/UploadFile/","/oldfiles/UploadFile/");
                    publicContentVO.setContent(content);
                    try {
                        publicContentVO.setCreateDate(map.get("docDateline") == null ? new Date() : sdf.parse(map.get("docDateline").toString()));
                    } catch (ParseException e) {
                        publicContentVO.setCreateDate(new Date());
                    }
                    publicContentVO.setSortDate(publicContentVO.getCreateDate());// 排序日期
                    publicContentVO.setPublishDate(publicContentVO.getCreateDate());
                    publicContentVO.setRepealDate(map.get("docDelDate") == null ? "" : map.get("docDelDate").toString());
                    publicContentService.saveEntity(publicContentVO);
                }
                return null;
            }

            @Override
            public String complete(String result, Throwable exception) {
                if(exception != null){
                    return exception.getMessage();
                }
                return result;
            }
        });
    }


    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getOrganId() {
        return organId;
    }

    public void setOrganId(String organId) {
        this.organId = organId;
    }

    public Long getEx8organId() {
        return ex8organId;
    }

    public void setEx8organId(Long ex8organId) {
        this.ex8organId = ex8organId;
    }

    public Long getEx8CatId() {
        return ex8CatId;
    }

    public void setEx8CatId(Long ex8CatId) {
        this.ex8CatId = ex8CatId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public IPublicContentService getPublicContentService() {
        return publicContentService;
    }

    public void setPublicContentService(IPublicContentService publicContentService) {
        this.publicContentService = publicContentService;
    }

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }
}
