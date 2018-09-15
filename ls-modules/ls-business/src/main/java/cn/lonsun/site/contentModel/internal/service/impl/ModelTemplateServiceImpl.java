package cn.lonsun.site.contentModel.internal.service.impl;

import cn.lonsun.common.util.AppUtil;
import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.core.util.Pagination;
import cn.lonsun.site.contentModel.internal.dao.IModelTemplateDao;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateService;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <br/>
 *
 * @author wangss <br/>
 * @version v1.0 <br/>
 * @date 2015-10-8<br/>
 */
@Service("modelTemplateService")
public class ModelTemplateServiceImpl extends MockService<ModelTemplateEO> implements IModelTemplateService {
    @Autowired
    private IModelTemplateDao modelTpl;

    @Override
    public Pagination getPage(Long pageIndex, Integer pageSize, Long modelId) {
        return modelTpl.getPage(pageIndex, pageSize, modelId);
    }

    @Override
    public void delEOs(String modelIds) {
        Long[] modelIdArr = AppUtil.getLongs(modelIds, ",");
        List<ModelTemplateEO> list = new ArrayList<ModelTemplateEO>();

        int length = modelIdArr.length;
        if (modelIdArr != null && length > 0) {
            for (int i = 0; i < length; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                List<ModelTemplateEO> list1 = new ArrayList<ModelTemplateEO>();
                map.put("modelId", modelIdArr[i]);
                list1 = modelTpl.getEntities(ModelTemplateEO.class, map);
                if (list1 != null && list1.size() > 0) {
                    list.add(list1.get(0));
                }
            }
        }
        modelTpl.delete(list);
    }

    @Override
    public void saveEO(ModelTemplateEO eo) {
        if (eo != null) {
            if (eo.getTplId() == null) {
                saveEntity(eo);
            } else {
                updateEntity(eo);
            }
        }
       /* if(list!=null&&list.size()>0){
            int size=list.size();
            for(int i=0;i<size;i++){
               if(list.get(i)!=null){
                    if(list.get(i).getTplId()==null){
                        saveEntity(list.get(i));
                    }else{
                        updateEntity(list.get(i));
                    }
               }
            }
        }*/
    }

    @Override
    public void delEO(Long tplId) {
        if (tplId != null) {
            ModelTemplateEO eo = getEntity(ModelTemplateEO.class, tplId);
            delete(eo);
        }
    }

    @Override
    public Boolean checkCode(Long modelId, String code, Long tplId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("modelId", modelId);
        map.put("modelTypeCode", code);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        List<ModelTemplateEO> list = getEntities(ModelTemplateEO.class, map);
        if (list == null || list.size() == 0) {
            return true;
        }
        ModelTemplateEO eo = list.get(0);
        if (eo.getTplId().equals(tplId)) {
            return true;
        }
        return false;
    }

    @Override
    public List<ModelTemplateEO> getFirstModelType(Long modelId) {
        return modelTpl.getFirstModelType(modelId);
    }

    @Override
    public void saveVO(ContentModelVO vo, Long id) {
        ModelTemplateEO tempEO = new ModelTemplateEO();
        if (vo.getTplId() == null) {
            tempEO.setArticalTempId(vo.getArticalTempId());
            tempEO.setColumnTempId(vo.getColumnTempId());
            tempEO.setWapArticalTempId(vo.getWapArticalTempId());
            tempEO.setWapColumnTempId(vo.getWapColumnTempId());
            tempEO.setModelId(id);
            tempEO.setProcessId(vo.getProcessId());
            tempEO.setProcessName(vo.getProcessName());
            tempEO.setModelTypeCode(vo.getModelTypeCode());
            tempEO.setType(1);
            saveEntity(tempEO);
        } else {
            tempEO = getEntity(ModelTemplateEO.class, vo.getTplId());
            tempEO.setArticalTempId(vo.getArticalTempId());
            tempEO.setColumnTempId(vo.getColumnTempId());
            tempEO.setWapArticalTempId(vo.getWapArticalTempId());
            tempEO.setWapColumnTempId(vo.getWapColumnTempId());
            tempEO.setModelTypeCode(vo.getModelTypeCode());
            tempEO.setProcessId(vo.getProcessId());
            tempEO.setProcessName(vo.getProcessName());
            tempEO.setModelId(id);
            tempEO.setType(1);
            updateEntity(tempEO);
        }
    }

    @Override
    public void delTpls(Long modelId) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<ModelTemplateEO> list = new ArrayList<ModelTemplateEO>();
        map.put("modelId", modelId);
        map.put("recordStatus", AMockEntity.RecordStatus.Normal.toString());
        list = modelTpl.getEntities(ModelTemplateEO.class, map);
        delete(list);
    }

    @Override
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode) {
        return modelTpl.getFirstModelByColumnCode(columnTypeCode);
    }

}
