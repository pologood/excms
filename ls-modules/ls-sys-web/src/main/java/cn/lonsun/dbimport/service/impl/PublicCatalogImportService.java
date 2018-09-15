package cn.lonsun.dbimport.service.impl;

import cn.lonsun.base.anno.DbInject;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.dbimport.internal.entity.DbImportBackupEO;
import cn.lonsun.dbimport.internal.entity.DbImportIdRelationEO;
import cn.lonsun.dbimport.internal.service.IDbImportBackupService;
import cn.lonsun.dbimport.internal.service.IDbImportIdRelationService;
import cn.lonsun.dbimport.service.IImportService;
import cn.lonsun.dbimport.service.base.BaseImportService;
import cn.lonsun.publicInfo.internal.dao.IPublicCatalogDao;
import cn.lonsun.publicInfo.internal.entity.OrganConfigEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogEO;
import cn.lonsun.publicInfo.internal.entity.PublicCatalogOrganRelEO;
import cn.lonsun.publicInfo.internal.service.IOrganConfigService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogOrganRelService;
import cn.lonsun.publicInfo.internal.service.IPublicCatalogService;
import cn.lonsun.rbac.internal.service.IOrganService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * ex7信息公开目录导入
 * @author zhongjun
 */
@Component("ex7publicCatalogImportService")
public class PublicCatalogImportService extends BaseImportService implements IImportService {

    @Autowired
    private IDbImportBackupService dbImportBackupService;

    @Autowired
    private IPublicCatalogService publicCatalogService;

    @DbInject("publicCatalog")
    private IPublicCatalogDao publicCatalogDao;

    @Autowired
    private IDbImportIdRelationService dbImportIdRelationService;

    @Autowired
    private IOrganConfigService organConfigService;

    @Autowired
    private IPublicCatalogOrganRelService publicCatalogOrganRelService;

    @Autowired
    private IOrganService organService;

    public PublicCatalogImportService() {
        super("ex7");
    }

    @Override
    public Object excuteImport(String taskId) {
        List<PublicCatalogEO> result = this.doBackUp();
        saveCatalog();
        return null;
    }

    public void saveCatalog(){
        List<PublicCatalogEO> publicList = queryListBySqlKey(group, "selectEx7PublicCatalog", PublicCatalogEO.class );
        log.info("查询到公共目录：{}条", publicList.size());
//        物理删除
        publicCatalogDao.deleteAll();
//        sql插入
        publicCatalogDao.insertBySql(publicList);

        Map<String, PublicCatalogEO> owner = new HashMap<String, PublicCatalogEO>();
        for(PublicCatalogEO eo : publicList){
            if(eo.getOrganId() != null && eo.getOrganId().longValue() != 0){
                owner.put(eo.getOrganId().longValue() + "_" + eo.getId().longValue(), eo);
            }
        }
        //私有目录
        saveCatalogOrganRel(owner);
        //单位目录配置
        saveCatalogConfig();
    }

    private void saveCatalogOrganRel(Map<String, PublicCatalogEO> owner){
        //单位新老id对应关系
        List<DbImportIdRelationEO> organRel = dbImportIdRelationService.getByType("organ");

        SqlRowSet privateSet = queryResultSetBySqlKey(group, "selectEx7PrimaryCatalog");
        log.info("查询私有目录结束");

        //将list转成map,方便下面根据id获取
        Map<Long , PublicCatalogEO> catalogMap = new HashMap<Long, PublicCatalogEO>();
        List<PublicCatalogEO> publicList = publicCatalogService.getEntities(PublicCatalogEO.class, Collections.EMPTY_MAP);
        for(PublicCatalogEO catalog : publicList){
            catalogMap.put(catalog.getId(), catalog);
        }

        List<PublicCatalogOrganRelEO> catalogOrganRel = new ArrayList<PublicCatalogOrganRelEO>();
        long i = 0;
        List<Long> organRelids  = new ArrayList<Long>();
        while (privateSet.next()){
            PublicCatalogOrganRelEO eo = new PublicCatalogOrganRelEO();
            Long catId = privateSet.getLong("catId");
            int isShow = privateSet.getInt("isShow");
            Long parentId = privateSet.getLong("parentId");
            String name = privateSet.getString("name");
            Long organId = privateSet.getLong("organId");
            String link = privateSet.getString("link");
            PublicCatalogEO catalog = catalogMap.get(catId);
            if(parentId == null || catalog == null){
                log.info("新增私有目录：{}({})", name, catId);
                //如果parentId为空，则为新增的节点
                catalog = new PublicCatalogEO();
                catalog.setId(catId);
                catalog.setParentId(parentId);
//                catalog.setIsParent(Boolean.TRUE);
                catalog.setName(name);
                catalog.setLink(link);
                catalog.setSortNum(0);
                catalog.setType(2); //私有目录
                catalog.setCode(UUID.randomUUID().toString().replace("-", ""));
                catalog.setIsShow(isShow==1);
                catalog.setCreateDate(new Date());
                catalog.setRecordStatus(PublicCatalogEO.RecordStatus.Normal.toString());
                //保存目录
                List<PublicCatalogEO> addedCatalog = new ArrayList<PublicCatalogEO>();
                addedCatalog.add(catalog);
                publicCatalogDao.insertBySql(addedCatalog);
                catalogMap.put(catId, catalog);
            }else{
                //如果该目录已存在与公共目录中
                String key = organId.longValue() + "_" + catId.longValue();
                if(owner.containsKey(key)){ //删除已存在的配置
                    owner.remove(key);
                }
            }
            BeanUtils.copyProperties(catalog, eo);
            eo.setCatId(catalog.getId());
            eo.setName(name);
            eo.setOrganId(organId);
            eo.setLink(link);
            //ex7中 自定义的 ishide = 1 只能控制隐藏，不能设置显示
            eo.setIsShow(isShow==0?Boolean.FALSE : catalog.getIsShow());
            //根据id对应关系，获取新的OrganId
            for(DbImportIdRelationEO id : organRel){
                if(null != id.getOldId() && null != eo.getOrganId() ){
                    if(id.getOldId().longValue() == eo.getOrganId().longValue()){
                        eo.setOrganId(id.getNewId());
                        break;
                    }
                }
            }
            catalogOrganRel.add(eo);
            organRelids.add(eo.getCatId());
        }
        for(Map.Entry<String, PublicCatalogEO>  item : owner.entrySet()){
            PublicCatalogOrganRelEO eo = new PublicCatalogOrganRelEO();
            BeanUtils.copyProperties(item.getValue(), eo);
            eo.setCatId(eo.getId());
            //根据id对应关系，获取新的OrganId
            for(DbImportIdRelationEO id : organRel){
                if(null != id.getOldId() && null != eo.getOrganId() ){
                    if(id.getOldId().longValue() == eo.getOrganId().longValue()){
                        eo.setOrganId(id.getNewId());
                        break;
                    }
                }
            }
            catalogOrganRel.add(eo);
        }
        log.info("删除私有目录");
        publicCatalogOrganRelService.deleteAll();
        log.info("保存私有目录");
        publicCatalogOrganRelService.saveEntities(catalogOrganRel);
        log.info("私有目录保存完成");
    }

    private void saveWidthIdRelation(List<PublicCatalogEO> publicList) {
        log.info("保存公共目录...");
        List<DbImportIdRelationEO> relation = new ArrayList<DbImportIdRelationEO>();
        Map<Long, Long> idrelaction = new HashMap<Long, Long>();
        String type = PublicCatalogEO.class.getSimpleName();
        List<PublicCatalogEO> needUpdateParentId = new ArrayList<PublicCatalogEO>();
        for(PublicCatalogEO ca : publicList){
            long oldId = ca.getId();
            //更新parentid
            boolean hasdata = idrelaction.containsKey(ca.getParentId());
            if(hasdata){
                ca.setParentId(idrelaction.get(ca.getParentId()));
            }
            publicCatalogService.saveEntity(ca);
            DbImportIdRelationEO re = new DbImportIdRelationEO(ca.getId(), oldId, type);
            relation.add(re);
            idrelaction.put(oldId, ca.getId());
            if(!hasdata){
                needUpdateParentId.add(ca);
            }
        }
        log.info("更新父节点id:{}条", needUpdateParentId.size());
        for(PublicCatalogEO ca : needUpdateParentId){
            boolean hasdata = idrelaction.containsKey(ca.getParentId());
            if(hasdata){
                ca.setParentId(idrelaction.get(ca.getParentId()));
            }
            publicCatalogService.saveOrUpdateEntity(ca);
        }
        log.info("目录父节点更新完成！保存id对应关系");
        dbImportIdRelationService.deleteType(type);
        dbImportIdRelationService.saveEntities(relation);
    }

    /**
     * 保存信息公开目录单位配置
     * 公共目录
     */
    public void saveCatalogConfig(){
        List<OrganConfigEO> list =  organConfigService.getEntities(OrganConfigEO.class, Collections.EMPTY_MAP);
        organConfigService.delete(list);
        //查询老库数据
        List<Map<String, Object>> publicList = queryMapBySqlKey(group, "selectCatalogOrganConfig");
        list = new ArrayList<OrganConfigEO>();
//        //查询id对应关系
        List<DbImportIdRelationEO> catIdRel = dbImportIdRelationService.getByType("PublicCatalogEO");
        //单位新老id对应关系
        List<DbImportIdRelationEO> organRel = dbImportIdRelationService.getByType("cht");

        List<Long> organId = new ArrayList<Long>();

        for(Map<String, Object> item : publicList){
            OrganConfigEO eo = new OrganConfigEO();
            eo.setCatId(Long.valueOf(String.valueOf(item.get("catId"))));
            eo.setOrganId(Long.valueOf(String.valueOf(item.get("organId"))));
            eo.setSortNum(Long.valueOf(String.valueOf(item.get("sortNum"))));
            if(catIdRel != null && !catIdRel.isEmpty()){
                //根据id对应关系，获取新的catId
                for(DbImportIdRelationEO id : catIdRel){
                    if(id.getOldId() == eo.getCatId()){
                        eo.setCatId(id.getNewId());
                        break;
                    }
                }
            }
            //根据id对应关系，获取新的 OrganId
            for(DbImportIdRelationEO id : organRel){
                if(id.getOldId() == eo.getOrganId()){
                    eo.setOrganId(id.getNewId());
                    break;
                }
            }
            organId.add(eo.getOrganId());
            list.add(eo);
        }
        organConfigService.saveEntities(list);
        log.info("开始更新单位为已公开");
        organService.updateIsPublic(organId.toArray(new Long[]{}));
    }

    public List<PublicCatalogEO> doBackUp(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<PublicCatalogEO> list = publicCatalogService.getEntities(PublicCatalogEO.class, map);
        DbImportBackupEO backup = new DbImportBackupEO();
        backup.setData(JSONArray.toJSONString(list, SerializerFeature.WriteMapNullValue));
        backup.setType(PublicCatalogEO.class.getSimpleName());
        dbImportBackupService.saveEntity(backup);
        return list;
    }

    /**
     * 还原到上次备份
     * @throws BaseRunTimeException
     */
    public void revert(Long id) throws BaseRunTimeException{
        List<PublicCatalogEO> data = null;
        if(id != null){
            DbImportBackupEO eo = dbImportBackupService.getEntity(DbImportBackupEO.class, id);
            data = JSONArray.parseArray(eo.getData(), PublicCatalogEO.class);
        }else{
            List<DbImportBackupEO> list = dbImportBackupService.getListByDateSort("PublicCatalogEO");
            if(list == null || list.isEmpty()){
                throw new BaseRunTimeException("备份不存在");
            }
            data = JSONArray.parseArray(list.get(0).getData(), PublicCatalogEO.class);
        }
//        Map<String, Object> map = new HashMap<String, Object>();
//        map.put("recordStatus", PublicCatalogEO.RecordStatus.Normal.toString());
//        List<PublicCatalogEO> exitlist = publicCatalogService.getEntities(PublicCatalogEO.class, map);
        //删除老数据，保存备份数据
        publicCatalogDao.deleteAll();
        publicCatalogDao.insertBySql(data);
    }

}
