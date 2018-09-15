package cn.lonsun.site.contentModel.internal.service.impl;

import cn.lonsun.core.base.entity.AMockEntity;
import cn.lonsun.core.base.service.impl.MockService;
import cn.lonsun.site.contentModel.internal.dao.IModelTemplateSpecialDao;
import cn.lonsun.site.contentModel.internal.entity.ModelTemplateEO;
import cn.lonsun.site.contentModel.internal.service.IModelTemplateSpecialService;
import cn.lonsun.site.contentModel.vo.ContentModelVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/26.
 */
@Service
public class ModelTemplateSpecialServiceImpl extends MockService<ModelTemplateEO> implements IModelTemplateSpecialService  {

    @Resource
    private IModelTemplateSpecialDao modelTplSpecialDao;

    @Override
    public ModelTemplateEO getFirstModelByColumnCode(String columnTypeCode) {
        return modelTplSpecialDao.getFirstModelByColumnCode(columnTypeCode);
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
        list = modelTplSpecialDao.getEntities(ModelTemplateEO.class, map);
        delete(list);

    }
}
