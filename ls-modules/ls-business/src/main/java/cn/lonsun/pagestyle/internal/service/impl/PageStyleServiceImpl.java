package cn.lonsun.pagestyle.internal.service.impl;

import cn.lonsun.cache.client.CacheGroup;
import cn.lonsun.cache.client.CacheHandler;
import cn.lonsun.common.vo.PageQueryVO;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.exception.BaseRunTimeException;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.pagestyle.internal.dao.IPageStyleDao;
import cn.lonsun.pagestyle.internal.dao.IPageStyleModelDao;
import cn.lonsun.pagestyle.internal.entity.PageStyleEO;
import cn.lonsun.pagestyle.internal.entity.PageStyleModelEO;
import cn.lonsun.pagestyle.internal.service.IPageStyleService;
import cn.lonsun.pagestyle.internal.vo.PageStyleVO;
import cn.lonsun.site.contentModel.internal.service.IContentModelService;
import cn.lonsun.site.site.internal.entity.ColumnConfigEO;
import cn.lonsun.site.site.internal.entity.ColumnConfigRelEO;
import cn.lonsun.site.site.internal.service.IColumnConfigRelService;
import cn.lonsun.site.site.internal.service.IColumnConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("pageStyleService")
public class PageStyleServiceImpl extends MockService<PageStyleEO> implements IPageStyleService {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private IPageStyleDao pageStyleDao;

    @Autowired
    private IPageStyleModelDao pageStyleModelDao;

    @Autowired
    private IColumnConfigService columnConfigService;

    @Override
    public Pagination getPage(PageQueryVO vo, Map<String, Object> param) {
        StringBuilder sb = new StringBuilder();
        List value = new ArrayList();
        sb.append("from PageStyleEO t where t.recordStatus = ? ");
        value.add(PageStyleEO.RecordStatus.Normal.toString());
        if(param.containsKey("searchKey") && param.get("searchKey") != null){
            sb.append(" and t.name like ?");
            value.add("%" + param.get("searchKey").toString() + "%");
        }
        return pageStyleDao.getPagination(vo.getPageIndex(), vo.getPageSize(), sb.toString(), value.toArray());
    }

    @Override
    public Map<Long, Object> getAllWithColumn() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", PageStyleEO.RecordStatus.Normal.toString());
        map.put("isBase", 0);
        List<PageStyleEO> list = pageStyleDao.getEntities(PageStyleEO.class, map);
        Map<Long, Object> result = new HashMap<Long, Object>();
        Map<String, Object> columnConfigParam = new HashMap<String, Object>();
        columnConfigParam.put("recordStatus", ColumnConfigRelEO.RecordStatus.Normal.toString());
        for(PageStyleEO eo : list){
            PageStyleVO newEo = new PageStyleVO();
            BeanUtils.copyProperties(eo, newEo);
            //清空样式
            newEo.setStyle("");
            //根据模型编码获取对应的栏目
            columnConfigParam.put("contentModelCode", eo.getModelCode());
            List<ColumnConfigEO> columnConfigList = columnConfigService.getEntities(ColumnConfigEO.class, columnConfigParam);
            if(columnConfigList == null  || columnConfigList.isEmpty()){
                continue;
            }
            for(ColumnConfigEO config: columnConfigList){
                result.put(config.getIndicatorId(), newEo);
            }
        }
        return result;
    }

    @Transactional
    @Override
    public void save(PageStyleEO eo){
        //如果是基础样式， 则判断是否已存在
        if(eo.getIsBase() == 1){
            PageStyleEO base = getBaseStyle();
            if(base != null){
                //新增操作
                if(eo.getId() == null){
                    throw new BaseRunTimeException("基础样式已存在，请不要重复添加！");
                }else if(base.getId().longValue() != eo.getId().longValue()){ //如果数据库中存在的不是当前保存的，则
                    throw new BaseRunTimeException("基础样式已存在，请不要重复添加！");
                }else{
                    BeanUtils.copyProperties(eo, base);
                    eo = base;
                }
            }
            //基础配置不绑定内容模型
            eo.setStyleModelConfig(null);
        }
        if(eo.getId() == null || eo.getId() == 0){
            pageStyleDao.save(eo);
        }else{
            pageStyleDao.update(eo);
        }
        //下面的方法不能保存插入日期和修改日期插入人等数据
//        super.saveOrUpdateEntity(eo);
        saveModelConfig(eo.getId(), eo.getStyleModelConfig() == null?null:eo.getStyleModelConfig().toArray(new String[]{}));
    }

    /**
     * 删除样式，同步删除样式模型的关联
     * @param id
     */
    @Transactional
    @Override
    public void delete(Long id) {
        PageStyleEO eo = pageStyleDao.getEntity(PageStyleEO.class, id);
        if(eo == null){
            throw new BaseRunTimeException("样式不存在！");
        }
        if(eo.getIsBase().longValue() == 1){
            throw new BaseRunTimeException("基础样式不允许删除！");
        }
        pageStyleDao.delete(eo);
        pageStyleModelDao.deleteByStyle(id);
    }

    /**
     * 保存模型关联配置
     * @param styleId
     * @param modelCodes 如果该字段为null 则删除style的所有模型配置数据
     */
    @Transactional
    @Override
    public void saveModelConfig(Long styleId, String[] modelCodes) {
        if(styleId == null){
            throw new BaseRunTimeException("样式id不能为空");
        }
        //删除以前的配置
        pageStyleModelDao.deleteByStyle(styleId);
        if(modelCodes == null){
            return;
        }
        for(String model : modelCodes){
            pageStyleModelDao.save(new PageStyleModelEO(styleId, model));
        }
    }

    /**
     * 根据模型id获取所有样式
     * @param modelCode
     * @return
     */
    @Override
    public PageStyleEO getStyleByModel(String modelCode) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("modelCode", modelCode);
        List<PageStyleModelEO> list = pageStyleModelDao.getEntities(PageStyleModelEO.class, map);
        if(list == null || list.isEmpty()){
            return getBaseStyle();
        }
        PageStyleEO result = pageStyleDao.getEntity(PageStyleEO.class, list.get(0).getStyleId());
        if(result == null){
            return getBaseStyle();
        }
        return result;
    }

    /**
     * 获取已配置的模型
     * @param styleId
     * @return
     */
    @Override
    public List<PageStyleModelEO> getEfficientModel(Long styleId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("styleId", styleId);
        return  pageStyleModelDao.getEntities(PageStyleModelEO.class, map);
    }

    /**
     * 根据栏目id获取所有样式
     * @param columnId
     * @return
     */
    @Override
    public PageStyleEO getStyleByColumn(Long columnId) {
        ColumnConfigEO columnConfigEO = CacheHandler.getEntity(ColumnConfigEO.class, CacheGroup.CMS_INDICATORID, columnId);
        if(columnConfigEO == null){
            return getBaseStyle();
        }
        return getStyleByModel(columnConfigEO.getContentModelCode());
    }

    /**
     * 获取所有样式
     * @return
     */
    @Override
    public List<PageStyleEO> getAll() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", PageStyleEO.RecordStatus.Normal.toString());
        return pageStyleDao.getEntities(PageStyleEO.class, map);
    }

    /**
     * 获取基础样式
     * @return
     */
    @Override
    public PageStyleEO getBaseStyle() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("recordStatus", PageStyleEO.RecordStatus.Normal.toString());
        map.put("isBase", 1);
        List<PageStyleEO> list = pageStyleDao.getEntities(PageStyleEO.class, map);
        if(list == null || list.isEmpty()){
            return null;
        }
        return list.get(0);
    }
}
