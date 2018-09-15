package cn.lonsun.dbimport.service.thread;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.util.ThreadUtil;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import cn.lonsun.publicInfo.vo.PublicContentVO;
import cn.lonsun.util.HibernateHandler;
import cn.lonsun.util.HibernateSessionUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class ImportPublicInfoOther implements Callable<String>, Runnable {

    private IPublicContentService publicContentService;

    private Long siteId;

    private List<Map<String, Object>> data;

    private String organId;

    private Long ex8organId;

    private Long ex8CatId;

    private String type;

    private Map<String, Object> sessionInfo;

    public ImportPublicInfoOther(IPublicContentService publicContentService, Long siteId, List<Map<String, Object>> data ,
                                 Long ex8organId, Long ex8CatId, String type, Map<String, Object> sessionInfo) {
        this.publicContentService = publicContentService;
        this.siteId = siteId;
        this.data = data;
        this.ex8organId = ex8organId;
        this.ex8CatId = ex8CatId;
        this.type = type;
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
                for( Map<String, Object> map :data) {
                    PublicContentVO publicContentVO = new PublicContentVO();

                    publicContentVO.setContent(null == map.get("docContent") ? "" : map.get("docContent").toString());
                    //String attach = this.doAttach(attachCollection, d.get("_id").toString(), publicContentVO);
                    try {
                        publicContentVO.setCreateDate(map.get("docDateline") == null ? new Date() : sdf.parse(map.get("docDateline").toString()));
                    } catch (ParseException e) {
                        publicContentVO.setCreateDate(new Date());
                    }
                    publicContentVO.setOldSchemaId(AppUtil.getValue(map.get("docId")));
                    publicContentVO.setAuthor("");
                    publicContentVO.setSortDate(publicContentVO.getCreateDate());// 排序日期
                    publicContentVO.setPublishDate(publicContentVO.getCreateDate());
                    publicContentVO.setIsPublish(1);
                    publicContentVO.setSiteId(siteId);
                    publicContentVO.setCatId(ex8CatId);
                    publicContentVO.setTitle(null == map.get("docTitle") ? "空标题" : map.get("docTitle").toString());
                    publicContentVO.setOrganId(ex8organId);
                    publicContentVO.setType(type);
                    publicContentVO.setFileNum(null == map.get("docNum") ? "" : map.get("docNum").toString());
                    publicContentVO.setKeyWords(null == map.get("docKeyWords") ? "" : map.get("docKeyWords").toString());
                    publicContentVO.setSummarize("");
                    publicContentVO.setHit(Long.parseLong(map.get("docHits").toString()));
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
