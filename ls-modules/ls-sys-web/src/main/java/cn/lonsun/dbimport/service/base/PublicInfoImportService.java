package cn.lonsun.dbimport.service.base;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.dbimport.internal.entity.DbImportIdRelationEO;
import cn.lonsun.dbimport.internal.service.IDbImportIdRelationService;
import cn.lonsun.dbimport.service.IImportService;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.service.IPublicApplyService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.publicInfo.internal.service.IPublicClassService;
import cn.lonsun.publicInfo.internal.service.IPublicContentService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class PublicInfoImportService extends BaseImportService implements IImportService {

    protected Long guideId_ = 121699L;// 公开指南id

    protected Long institutionId_ = 121704L;// 公开制度id

    protected Long reportId_ = 121701L;// 公开年报

    protected Long catalog_ = 2657091L;//依申请公开目录

    public PublicInfoImportService(String dbGroup) {
        super(dbGroup);
    }

    @Autowired
    protected IPublicCatalogService publicCatalogService;

    @Autowired
    protected IDbImportIdRelationService dbImportIdRelationService;

    @Autowired
    protected IPublicContentService publicContentService;

    @Resource
    protected IPublicApplyService publicApplyService;

    @Resource
    protected IPublicClassService publicClassService;

    protected final Map<String, Map<String, Object>> xxgkClassMap = new HashMap<String, Map<String, Object>>();

    protected final Map<String,List<Map<String,Object>>> xxgkClassRelMap = new HashMap<String, List<Map<String,Object>>>();

    /**
     * key 是ex7系统中的id, value是ex8系统中的id
     */
    protected static final Map<Long, Long> organIdMap = new HashMap<Long, Long>();

    @Override
    public String doBefore() {
        String taskId = super.doBefore();
        final BaseImportService current = this;
        current.addProcess(taskId, ">开始加载准备数据");
        getXxgkClassList();
        getXxgkClassRelList();
        getOrganIdMap();
        current.addProcess(taskId, ">数据准备完成");
        return taskId;
    }

    @Override
    public Object excuteImport(String taskId) {
        this.addProcess(taskId, ">开始导入每个单位的数据");
        ExecutorService executor = Executors.newCachedThreadPool();
        for(Map.Entry<Long, Long> item: organIdMap.entrySet()){
            Long ex8OrganId = item.getValue();
            Long organId = item.getKey();
            String organInfo = importOrgan(taskId, executor, ex8OrganId, organId);
            //检查是否有停止命令，如果有则返回false,终止余下任务
            Command c = taskCommand.get(taskId);
            if(c == null){
                continue;
            }
            switch (taskCommand.get(taskId)){
                case stop:
                    this.addProcess(taskId, ">终止导入,当前单位" + organInfo);
                    return taskId;
                default: ;
            }
        }
        log.info("导入完成");
        return taskId;
    }

    private String importOrgan(String taskId, ExecutorService executor, Long ex8OrganId, Long organId) {
        String organInfo = "ex7OrganId:" + organId + "; ex8OrganId:" + ex8OrganId;
        List<Future> taskList = new ArrayList<Future>();
        //导入公开指南
        this.addProcess(taskId, ">开始导入公开指南：" + organInfo);
        excuteThread(taskList, executor, importPublicGuide(organId, ex8OrganId));
        //导入公开制度
        this.addProcess(taskId, ">开始导入公开制度：" + organInfo);
        excuteThread(taskList, executor, importPublicInstitution(organId, ex8OrganId));
        //导入公开年报
        this.addProcess(taskId, ">开始导入公开年报：" + organInfo);
        excuteThread(taskList, executor, importPublicAnnualReport(organId, ex8OrganId));
        //导入依申请公开目录
        this.addProcess(taskId, ">开始导入依申请公开目录：" + organInfo);
        excuteThread(taskList, executor, importPublicCatalog(organId, ex8OrganId));
        //导入依申请公开
        this.addProcess(taskId, ">开始导入依申请公开：" + organInfo);
        excuteThread(taskList, executor, importPublic(organId, ex8OrganId));
        //导入主动公开信息
        List<PublicCatalogEO> catlogList = getEx8PublicCatalog(ex8OrganId);

        this.addProcess(taskId, ">开始导入主动公开信息：" + organInfo);
        for(PublicCatalogEO ca : catlogList){
            this.addProcess(taskId, ">开始导入目录【" + ca.getName() + "(" + ca.getId() + ")】");
            excuteThread(taskList, executor, importDrivingPublic(ca.getId(), ca.getId(), organId, ex8OrganId));
            if(taskList.size() >= 10){
                try {
                    waitTaskFinish(taskList);
                } catch (BaseRunTimeException e) {
                    this.addProcess(taskId, ">目录导入异常：" + e.getMessage());
                    throw e;
                }
            }
        }
        try {
            waitTaskFinish(taskList);
        } catch (BaseRunTimeException e) {
            this.addProcess(taskId, ">目录导入异常：" + e.getMessage());
            throw e;
        }
        this.addProcess(taskId, ">单位数据导入完成：" + organInfo);
        return organInfo;
    }


    private void excuteThread(List<Future> taskList, ExecutorService executor, Callable callAble){
        if(callAble == null){
            return;
        }
//        Future f = executor.submit(callAble);
//        if(f != null){
//            try {
//                f.get();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//        }
        taskList.add(executor.submit(callAble));
    }

    private List<PublicCatalogEO> getEx8PublicCatalog(Long organId){
        List<PublicCatalogEO> treeList = new ArrayList<PublicCatalogEO>();
        OrganConfigEO organConfigEO = CacheHandler.getEntity(OrganConfigEO.class, CacheGroup.CMS_ORGAN_ID, organId);
        if (null == organConfigEO || null == organConfigEO.getCatId()) {
            return treeList;
        }
        // 查询出单位配置的私有目录和隐藏目录
        List<PublicCatalogEO> catalogList = publicCatalogService.getAllChildListByCatId(organConfigEO.getCatId());
//        PublicCatalogUtil.filterCatalogList(catalogList, organId, false);
//        PublicCatalogUtil.sortCatalog(catalogList);
        return catalogList;
    }

    private Map<Long, Long> getOrganIdMap(){
        if(!organIdMap.isEmpty()){
            return organIdMap;
        }
        List<DbImportIdRelationEO> list = dbImportIdRelationService.getByType("cht");
        // key 是ex7系统中的id, value是ex8系统中的id
        for(DbImportIdRelationEO item: list){
            organIdMap.put(item.getOldId(), item.getNewId());
        }
        return organIdMap;
    }

    private Map<String, Map<String, Object>> getXxgkClassList() {
        if(!xxgkClassMap.isEmpty()){
            return xxgkClassMap;
        }
        List<Map<String,Object>> xxgkClassList =  queryMapBySqlKey(group, "getXxgkClass");
        Map<String,Map<String,Object>> xxgkClassMap = this.xxgkClassMap;
        for(Object o : xxgkClassList){
            Map<String, Object> map = (Map<String, Object>) o;
            String classId =  AppUtil.getValue(map.get("classId"));
            String classPid =  AppUtil.getValue(map.get("classPid"));
            String className =  AppUtil.getValue(map.get("className"));
            if(!AppUtil.isEmpty(classId)){
                Map<String,Object> temp = new HashMap<String, Object>();
                temp.put("classId",classId);
                temp.put("classPid",classPid);
                temp.put("className",className);
                xxgkClassMap.put(classId,temp);
            }
        }
        return xxgkClassMap;
    }

    private Map<String,List<Map<String,Object>>> getXxgkClassRelList() {
        if(!xxgkClassRelMap.isEmpty()){
            return xxgkClassRelMap;
        }
        List<Map<String, Object>> xxgkClassRelList = queryMapBySqlKey(group, "getXxgkContentClassRe");
        for(Object o : xxgkClassRelList){
            Map<String, Object> map = (Map<String, Object>) o;
            String contentsId = AppUtil.getValue(map.get("contentsId"));
            String classId = AppUtil.getValue(map.get("classId"));
            if(!AppUtil.isEmpty(contentsId)){
                Map<String,Object> temp = new HashMap<String, Object>();
                temp.put(contentsId,classId);
                List<Map<String,Object>> list = null;
                if(xxgkClassRelMap.containsKey(contentsId)){
                    xxgkClassRelMap.get(contentsId).add(temp);
                }else{
                    list = new ArrayList<Map<String, Object>>();
                    list.add(temp);
                    xxgkClassRelMap.put(contentsId, list);
                }
            }
        }
        return xxgkClassRelMap;
    }

    /**
     * 导入已申请公开
     * @param organId
     * @param ex8organId
     * @return
     */
    public abstract Callable<String> importPublic(Long organId, Long ex8organId);
    /**
     * 导入公开指南
     * @return
     */
    public abstract Callable importPublicGuide(Long organId, Long ex8organId);

    /**
     * 导入公开制度
     * @return
     */
    public abstract Callable importPublicInstitution(Long organId, Long ex8organId);


    /**
     * 导入公开年报
     *
     * @return
     */
    public abstract Callable importPublicAnnualReport(Long organId, Long ex8organId);

    /**
     * 导入依申请公开目录
     *
     * @return
     */
    public abstract Callable importPublicCatalog(Long organId, Long ex8organId);

    /**
     * 导入主动公开信息
     * @return
     */
    public abstract Callable importDrivingPublic(Long catId, Long ex8CatId, Long organId, Long ex8OrganId);

    @Override
    public List doBackUp() {
        return null;
    }

    @Override
    public void revert(Long task_id) throws BaseRunTimeException {

    }
}
